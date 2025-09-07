# ADR 0004: Containerização e Orquestração

- **Data:** 2025-09-07  
- **Autores:** Marcos Santos  
- **Status:** Aceito  

---

## Contexto
Precisamos de um ambiente padronizado para desenvolvimento local e testes, que permita subir o backend, banco e autenticação rapidamente.  
Requisitos principais:  
- Subir múltiplos serviços em conjunto.  
- Evitar necessidade de instalação manual.  
- Ser simples de usar por qualquer membro do time.  

---

## Alternativas Consideradas
1. **Docker Compose**  
   - ✅ Simples, rápido para ambientes locais.  
   - ✅ Bom para desenvolvimento inicial.  
   - ⚠️ Menos adequado para produção escalável.  

2. **Kubernetes (Minikube/K3s)**  
   - ✅ Alta escalabilidade, produção-ready.  
   - ⚠️ Complexidade desnecessária no estágio inicial do projeto.  

3. **Sem containerização (instalação manual)**  
   - ✅ Simplicidade inicial para um único dev.  
   - ⚠️ Difícil de reproduzir em diferentes máquinas.  
   - ⚠️ Pouca padronização.  

---

## Decisão
Adotar **Docker Compose** para subir o ambiente local, incluindo:  
- Backend (Spring Boot).  
- Banco de dados MySQL.  
- Keycloak.  

O frontend (Angular) rodará separado via `ng serve`, com proxy apontando para o backend.  

---

## Consequências
- ✅ Fácil de rodar em qualquer máquina com Docker instalado.  
- ✅ Time pode iniciar rapidamente.  
- ⚠️ Não é ideal para ambientes produtivos de alta escala (migrar futuramente para Kubernetes).  

---

## Referências
- [Docker Compose Docs](https://docs.docker.com/compose/)  
- [Keycloak Docker Setup](https://www.keycloak.org/getting-started/getting-started-docker)  
