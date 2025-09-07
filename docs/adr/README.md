# 📘 Arquitetura – Decisões Registradas (ADRs)

Este diretório contém os **Architecture Decision Records (ADRs)** do projeto **Count Word**.  
Os ADRs documentam as principais decisões arquiteturais tomadas ao longo do desenvolvimento, servindo como um histórico vivo da evolução do sistema.

---

## 📌 O que é um ADR?
Um **ADR (Architecture Decision Record)** é um registro curto e objetivo que explica:
- O **contexto** da decisão (problema ou necessidade).  
- A **decisão tomada** (qual tecnologia/abordagem foi escolhida).  
- As **consequências** (impactos positivos e negativos).  

Esse formato ajuda a equipe a entender **por que** determinadas escolhas foram feitas e quais alternativas foram consideradas.

---

## 🗂 Estrutura dos arquivos
Cada ADR é um arquivo Markdown, numerado sequencialmente:

docs/adr/
├── 0001-stack-backend.md
├── 0002-banco-dados.md
├── 0003-autenticacao.md
└── 0004-containerizacao.md



O nome do arquivo segue o padrão:


---

## ✅ Status das decisões
Cada ADR possui um status:
- **Proposto** → ainda em discussão.  
- **Aceito** → decisão validada e em uso.  
- **Rejeitado** → discutido, mas não adotado.  
- **Substituído** → superado por outra decisão posterior.  

---

## 📖 ADRs Iniciais do Projeto
- **ADR 001 – Stack de Backend**: Spring Boot 3.5 (Java 21).  
- **ADR 002 – Banco de Dados Relacional**: MySQL 8 + Flyway.  
- **ADR 003 – Autenticação**: Keycloak (OpenID Connect / JWT).  
- **ADR 004 – Containerização**: Docker Compose para backend, banco e Keycloak.  

---

## 🔄 Como contribuir
1. Crie um novo arquivo em `docs/adr/` com o próximo número sequencial.  
2. Use o [template.md](./template.md) como base.  
3. Abra PR para revisão da equipe.  

---



## 📚 Referências
- [Documenting Architecture Decisions – Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)  
- [adr.github.io](https://adr.github.io/)  


