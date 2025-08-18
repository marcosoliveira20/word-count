# Count Word

Aplicação web para cadastro e contagem de palavras em inglês, permitindo registrar usos, acompanhar métricas por usuário e visualizar estatísticas por nível (A1–C2).

O projeto já conta com cerca de **5 mil palavras do dicionário de Oxford**, que representam as palavras mais usadas na língua inglesa.

## 🚀 Stack utilizada
- **Frontend:** Angular 17+ (standalone components, proxy para backend)
- **Backend:** Java + Spring Boot
- **Auth:** Keycloak (OpenID Connect / JWT)
- **Banco:** MySQL (com migrações Flyway)
- **Containerização:** Docker Compose para backend, banco e Keycloak

## ▶️ Como iniciar o projeto
Na raiz do repositório:
```bash
chmod +x start-project.sh
./start-project.sh
```
Isso irá:
1. Subir os containers de **backend + banco + keycloak**.
2. Configurar o Keycloak automaticamente (realm, sslRequired, frontendUrl).
3. Esperar o backend iniciar na porta `8080`.
4. Iniciar o **frontend Angular** em `http://localhost:4200` com proxy (`proxy.conf.json`).

## 📂 Estrutura de diretórios
- `english/` → contém `docker-compose.yml` (backend + db + keycloak)
- `count-word/` → código Angular (frontend)
- `start-project.sh` → script de inicialização

## 🔮 Próximos passos
- Integrar **Kong** como API Gateway.
- Implementar upload de arquivos.
- Suportar digitação e análise de textos maiores.

---
👤 Projeto desenvolvido para estudo de arquitetura e prática com Keycloak, Angular e Spring Boot.
