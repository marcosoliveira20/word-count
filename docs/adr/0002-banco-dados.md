# ADR 0002: Banco de Dados Relacional

- **Data:** 2025-09-07  
- **Autores:** Marcos Santos  
- **Status:** Aceito  

---

## Contexto
O projeto precisa armazenar palavras, níveis, usuários e usos, com forte necessidade de relacionamentos entre tabelas e consultas para relatórios e estatísticas.  
Requisitos principais:  
- Modelo relacional.  
- Integração com Spring Boot e JPA.  
- Suporte a migrações versionadas.  

---

## Alternativas Consideradas
1. **MySQL 8**  
   - ✅ Amplo suporte no ecossistema Spring.  
   - ✅ Documentação extensa e ferramentas maduras (ex.: Flyway).  
   - ⚠️ Escalabilidade horizontal limitada sem soluções adicionais.  

2. **PostgreSQL**  
   - ✅ Mais recursos avançados (tipos, índices, JSONB).  
   - ⚠️ Complexidade desnecessária para os requisitos atuais.  

3. **MongoDB**  
   - ✅ Flexível em documentos.  
   - ⚠️ Não se encaixa bem no modelo relacional de palavras/usuários.  
   - ⚠️ Consultas para estatísticas seriam mais complexas.  

---

## Decisão
Adotar **MySQL 8** como banco de dados relacional, utilizando **Flyway** para migrações.  

---

## Consequências
- ✅ Integração direta com Spring Data JPA.  
- ✅ Suporte amplo da comunidade.  
- ⚠️ Caso o volume de dados cresça muito, será necessário planejar particionamento ou considerar PostgreSQL.  

---

## Referências
- [MySQL Docs](https://dev.mysql.com/doc/)  
- [Flyway Docs](https://documentation.red-gate.com/flyway)  
