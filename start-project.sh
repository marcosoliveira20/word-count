#!/usr/bin/env bash
set -euo pipefail

echo "🚀 Iniciando Count Word (compose + keycloak + backend + frontend)..."

# ===============================
# 0) Variáveis (ajuste se quiser)
# =================``==============
# Nome do container do Keycloak (tenta auto-descobrir se vazio)
KEYCLOAK_CONTAINER="${KEYCLOAK_CONTAINER:-$(docker ps --filter "name=keycloak" --format "{{.Names}}" | head -n1)}"
# Credenciais do admin (devem bater com seu docker-compose)
KC_USER="${KC_USER:-admin}"
KC_PASS="${KC_PASS:-admin}"
KC_URL_IN_CONTAINER="http://localhost:8080"
# Caminho do frontend Angular relativo à RAIZ do projeto
FRONT_DIR="${FRONT_DIR:-frontend}"
FRONT_PORT="${FRONT_PORT:-4200}"
FRONT_PROXY="${FRONT_PROXY:-proxy.conf.json}"
# Caminho do diretório onde está o docker-compose
COMPOSE_DIR="${COMPOSE_DIR:-backend}"



# ======================================
# 1) Subir backend + db + keycloak
# ======================================
echo "📦 Subindo serviços com Docker Compose (dir: ${COMPOSE_DIR})..."
(
  cd "${COMPOSE_DIR}" || { echo "❌ Pasta '${COMPOSE_DIR}' não encontrada a partir da raiz."; exit 1; }
  docker compose up -d --build
)

# ======================================
# 2) Configurar Keycloak no container
# ======================================
if [[ -z "${KEYCLOAK_CONTAINER}" ]]; then
  echo "❌ Não encontrei um container do Keycloak em execução (filtro: name=keycloak)."
  echo "   Dica: export KEYCLOAK_CONTAINER=<nome-do-container> e rode de novo."
  exit 1
fi
echo "🔎 Usando container do Keycloak: ${KEYCLOAK_CONTAINER}"

exec_in() {
  docker exec -i "${KEYCLOAK_CONTAINER}" bash -lc "$*"
}

echo "⏳ Aguardando Keycloak responder na porta interna 8080..."
exec_in 'until (exec 3<>/dev/tcp/127.0.0.1/8080); do sleep 1; done'
echo "✅ Keycloak UP (porta interna 8080)."

KCADM="/opt/keycloak/bin/kcadm.sh"

echo "🔐 Autenticando kcadm..."
exec_in "${KCADM} config credentials --server ${KC_URL_IN_CONTAINER} --realm master --user ${KC_USER} --password ${KC_PASS}"

echo "🔧 sslRequired=NONE em 'master'..."
exec_in "${KCADM} update realms/master -s sslRequired=NONE"

echo "🔧 sslRequired=NONE em 'english-realm' (ignora se não existir)..."
exec_in "${KCADM} update realms/english-realm -s sslRequired=NONE || true"

echo "🌐 Definindo frontendUrl do realm master para http://localhost:8081 ..."
exec_in "${KCADM} update realms/master -s 'attributes.\"frontendUrl\"=\"http://localhost:8081\"'"

# ======================================
# 3) Aguardar backend subir (porta 8080)
# ======================================
echo "⏳ Aguardando backend (porta 8080)..."
# tenta /actuator/health; se falhar, cai para teste de socket
if ! curl -sf --max-time 2 http://localhost:8080/actuator/health >/dev/null 2>&1; then
  until (exec 3<>/dev/tcp/127.0.0.1/8080); do
    sleep 3
    echo "   Backend ainda iniciando..."
  done
fi
echo "✅ Backend disponível em http://localhost:8080"

# ======================================
# 4) Iniciar frontend Angular com proxy
# ======================================
echo "🌐 Iniciando frontend Angular em ${FRONT_DIR} (porta ${FRONT_PORT}) com proxy ${FRONT_PROXY}..."
cd "${FRONT_DIR}" || { echo "❌ Pasta '${FRONT_DIR}' não encontrada a partir da raiz."; exit 1; }

if [ ! -d "node_modules" ]; then
  echo "📥 Instalando dependências..."
  npm install
fi

# Start em background para liberar o terminal
npx ng serve --port "${FRONT_PORT}" --proxy-config "${FRONT_PROXY}" --open &
FRONT_PID=$!

echo "✅ Frontend rodando: http://localhost:${FRONT_PORT}"
echo "ℹ️  Para encerrar:"
echo "    1) Parar Angular: kill ${FRONT_PID}"
echo "    2) Parar serviços: (cd ${COMPOSE_DIR} && docker compose down)"

# Mantém o script “vivo” enquanto o Angular estiver rodando
wait ${FRONT_PID}
