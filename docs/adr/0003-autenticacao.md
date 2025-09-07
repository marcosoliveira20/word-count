# ADR 0003: Autenticação e Autorização

- **Data:** 2025-09-07  
- **Autores:** Marcos Santos  
- **Status:** Aceito  

---

## Contexto
Precisamos de autenticação e autorização centralizada para permitir o registro de uso por usuário e proteger os endpoints de criação/uso de palavras.  
Requisitos principais:  
- Suporte a OAuth2 e OpenID Connect.  
- Integração com Spring Security.  
- Gestão de usuários e papéis (roles).  

---

## Alternativas Consideradas
1. **Keycloak**  
   - ✅ Open Source, robusto, suporte a OAuth2/OIDC.  
   - ✅ Painel de administração para gestão de usuários.  
   - ✅ Fácil integração com Spring Boot.  
   - ⚠️ Requer manutenção de container dedicado.  

2. **Microsoft Entra**  
   - ✅ SaaS com gestão simplificada.  
   - ⚠️ Licenciamento pago após limites.  
   - ⚠️ Vendor lock-in.  

---

## Decisão
Adotar **Keycloak** como provedor de identidade e autorização, integrado ao backend com validação de JWT.  

---

## Consequências
- ✅ Gestão centralizada de usuários e roles.  
- ✅ Alinhado com boas práticas de segurança.  
- ⚠️ Curva de aprendizado inicial para configurar realms e clients.  

---

## Referências
- [Keycloak Docs](https://www.keycloak.org/documentation)  
- [Spring Boot + Keycloak Integration](https://www.baeldung.com/spring-boot-keycloak)  
