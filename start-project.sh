#!/usr/bin/env bash
set -euo pipefail

echo "🚀 Iniciando Count Word (compose + keycloak + backend + minio + kafka + frontend)..."

# ===============================
# 0) Variáveis
# ===============================
KEYCLOAK_CONTAINER="${KEYCLOAK_CONTAINER:-$(docker ps --filter "name=english-keycloak" --format "{{.Names}}" | head -n1)}"
KC_USER="${KC_USER:-admin}"
KC_PASS="${KC_PASS:-admin}"
KC_URL_IN_CONTAINER="http://localhost:8080"

FRONT_DIR="${FRONT_DIR:-frontend}"
FRONT_PORT="${FRONT_PORT:-4200}"
FRONT_PROXY="${FRONT_PROXY:-proxy.conf.json}"
COMPOSE_DIR="${COMPOSE_DIR:-infrastructure/docker}"
DELAY_BETWEEN_1_AND_2="${DELAY_BETWEEN_1_AND_2:-2}"

# MinIO
MINIO_BUCKET="${MINIO_BUCKET:-countword}"
MINIO_HOSTNAME_IN_NET="${MINIO_HOSTNAME_IN_NET:-minio}"
MINIO_PORT="${MINIO_PORT:-9000}"
MINIO_USER="${MINIO_USER:-admin}"
MINIO_PASS="${MINIO_PASS:-admin123}"

# Kafka (bitnami/kafka no compose usa container_name: kafka)
KAFKA_BOOTSTRAP="${KAFKA_BOOTSTRAP:-kafka:9092}"
KAFKA_TOPIC="${KAFKA_TOPIC:-minio-events}"   # usado p/ MinIO -> Kafka
KAFKA_RETENTION_MS="${KAFKA_RETENTION_MS:-43200000}"  # 12h por padrão p/ tópicos do domínio
CW_TOPICS=("cw.file.stored" "cw.word.detected")

# Imagens utilitárias
CURL_IMAGE="curlimages/curl:8.10.1"
MC_IMAGE="minio/mc:latest"

# Helper env para o mc (dispensa 'mc alias set')
export MC_ALIAS_ENV="MC_HOST_local=http://${MINIO_USER}:${MINIO_PASS}@${MINIO_HOSTNAME_IN_NET}:${MINIO_PORT}"

# ======================================
# 1) Subir todos os serviços
# ======================================
echo "📦 Subindo serviços com Docker Compose (dir: ${COMPOSE_DIR})..."
(
  cd "${COMPOSE_DIR}" || { echo "❌ Pasta '${COMPOSE_DIR}' não encontrada."; exit 1; }
  docker compose up -d --build
)

if [[ "${DELAY_BETWEEN_1_AND_2}" =~ ^[0-9]+$ ]] && (( DELAY_BETWEEN_1_AND_2 > 0 )); then
  echo "⏲️ Aguardando ${DELAY_BETWEEN_1_AND_2}s antes de configurar o Keycloak..."
  sleep "${DELAY_BETWEEN_1_AND_2}"
fi

# ======================================
# 2) Configuração Keycloak
# ======================================
KEYCLOAK_CONTAINER="$(docker ps --filter "name=english-keycloak" --format "{{.Names}}" | head -n1 || true)"
if [[ -z "${KEYCLOAK_CONTAINER}" ]]; then
  echo "❌ Keycloak não encontrado."
  exit 1
fi
echo "🔎 Usando container do Keycloak: ${KEYCLOAK_CONTAINER}"

exec_in() {
  docker exec -i "${KEYCLOAK_CONTAINER}" bash -lc "$*"
}

echo "⏳ Aguardando Keycloak na porta 8080..."
exec_in 'until (exec 3<>/dev/tcp/127.0.0.1/8080); do sleep 1; done'
echo "✅ Keycloak UP."

KCADM="/opt/keycloak/bin/kcadm.sh"
exec_in "${KCADM} config credentials --server ${KC_URL_IN_CONTAINER} --realm master --user ${KC_USER} --password ${KC_PASS}"
exec_in "${KCADM} update realms/master -s sslRequired=NONE"
exec_in "${KCADM} update realms/english-realm -s sslRequired=NONE || true"
exec_in "${KCADM} update realms/master -s 'attributes.\"frontendUrl\"=\"http://localhost:8081\"'"

# ======================================
# 2.b) Garantir existência de tópicos no Kafka
# ======================================
echo "🧩 Garantindo existência do tópico Kafka '${KAFKA_TOPIC:-minio-events}'..."
docker exec kafka bash -lc "/opt/bitnami/kafka/bin/kafka-topics.sh \
  --bootstrap-server localhost:9092 \
  --create --if-not-exists \
  --topic ${KAFKA_TOPIC:-minio-events} \
  --partitions 1 --replication-factor 1" || true

echo "🧩 Garantindo tópicos de domínio (retenção ${KAFKA_RETENTION_MS} ms ~ 12h)..."
for t in \"${CW_TOPICS[@]}\"; do
  # criar se não existir
  docker exec kafka bash -lc "/opt/bitnami/kafka/bin/kafka-topics.sh \
    --bootstrap-server localhost:9092 \
    --create --if-not-exists \
    --topic ${t} \
    --partitions 1 --replication-factor 1" || true

  # aplicar retenção de 12h e sem limite por tamanho (cleanup delete)
  docker exec kafka bash -lc "/opt/bitnami/kafka/bin/kafka-configs.sh \
    --bootstrap-server localhost:9092 \
    --entity-type topics --entity-name ${t} --alter \
    --add-config retention.ms=${KAFKA_RETENTION_MS},retention.bytes=-1,cleanup.policy=delete" || true
done

# ======================================
# 3) Configuração MinIO
# ======================================
echo "🔎 Descobrindo rede do container 'minio'..."
MINIO_NET="$(docker inspect -f '{{range $k,$v := .NetworkSettings.Networks}}{{printf "%s" $k}}{{end}}' minio 2>/dev/null || true)"
if [[ -z "${MINIO_NET}" ]]; then
  echo "❌ Não consegui detectar a rede do container 'minio'."
  docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Networks}}'
  exit 1
fi
echo "🌐 Rede do MinIO: ${MINIO_NET}"

MINIO_ENDPOINT="http://${MINIO_HOSTNAME_IN_NET}:${MINIO_PORT}"
echo "⏳ Aguardando MinIO ficar pronto em ${MINIO_ENDPOINT} ..."
until docker run --rm --network "${MINIO_NET}" "${CURL_IMAGE}" -sSf "${MINIO_ENDPOINT}/minio/health/ready" >/dev/null; do
  echo "   MinIO ainda não está pronto... tentando novamente em 2s"
  sleep 2
done
echo "✅ MinIO pronto."

echo "🪣 Criando bucket '${MINIO_BUCKET}' (idempotente)..."
docker run --rm --network "${MINIO_NET}" -e "${MC_ALIAS_ENV}" \
  "${MC_IMAGE}" mb -p "local/${MINIO_BUCKET}" || true

docker run --rm --network "${MINIO_NET}" -e "${MC_ALIAS_ENV}" \
  "${MC_IMAGE}" ls local || true
echo "✅ Bucket '${MINIO_BUCKET}' OK."

# ======================================
# 4) Aguardar backend
# ======================================
echo "⏳ Aguardando backend (porta 8080)..."
until (exec 3<>/dev/tcp/127.0.0.1/8080); do
  sleep 3
  echo "   Backend ainda iniciando..."
done
echo "✅ Backend disponível."

# ======================================
# 5) Iniciar frontend Angular
# ======================================
echo "🌐 Iniciando frontend Angular em ${FRONT_DIR} (porta ${FRONT_PORT})..."
cd "${FRONT_DIR}" || { echo "❌ Pasta '${FRONT_DIR}' não encontrada."; exit 1; }

if [ ! -d "node_modules" ]; then
  echo "📥 Instalando dependências..."
  npm install
fi

npx ng serve --port "${FRONT_PORT}" --proxy-config "${FRONT_PROXY}" --open &
FRONT_PID=$!

echo "✅ Frontend rodando: http://localhost:${FRONT_PORT}"
echo "ℹ️ Para encerrar: kill ${FRONT_PID} && (cd ${COMPOSE_DIR} && docker compose down)"

wait ${FRONT_PID}
