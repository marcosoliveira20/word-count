# ADR 0001: Stack de Backend

- **Data:** 2025-09-07  
- **Autores:** Marcos Santos  
- **Status:** Aceito  

---

## Contexto
Precisamos definir a stack de backend para expor APIs REST, lidar com autenticação, integrar com banco de dados e possibilitar evolução futura do projeto.  
Requisitos principais:  
- Suporte a APIs REST de forma simples.  
- Integração com Keycloak (OpenID Connect / JWT).  
- Comunidade ativa e robusta.  
- Maturidade e estabilidade no mercado.  

---

## Alternativas Consideradas
1. **Spring Boot (Java 21)**  
   - ✅ Integração madura com Keycloak, JPA e bibliotecas de mercado.  
   - ✅ Comunidade ampla, documentação extensa.  
   - ✅ Facilidade de testes e deploy em containers.  
   - ⚠️ Sobrecarga de configuração inicial comparado a frameworks mais leves.  

2. **Micronaut**  
   - ✅ Framework leve, inicialização rápida.  
   - ⚠️ Comunidade menor, menos exemplos.  

3. **Node.js (NestJS)**  
   - ✅ Leve e produtivo, bom para APIs REST.  
   - ⚠️ Menor integração nativa com Keycloak em comparação com o ecossistema Java.  
   - ⚠️ Curva de aprendizado extra para o time que já domina Java.  

---

## Decisão
Adotar **Spring Boot 3.5 (Java 21)** como framework de backend.  

---

## Consequências
- ✅ Fácil integração com Keycloak, JPA e Flyway.  
- ✅ Ecosistema sólido e consolidado no mercado.  
- ⚠️ Tempo de build e inicialização maior que alternativas como Micronaut.  

---

## Referências
- [Spring Boot Docs](https://spring.io/projects/spring-boot)  
- [Micronaut Docs](https://micronaut.io/)  
- [NestJS Docs](https://nestjs.com/)  
