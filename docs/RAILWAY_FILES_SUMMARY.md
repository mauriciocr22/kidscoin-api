# 📦 Arquivos de Deploy Railway - Resumo

## 🎯 Arquivos Criados/Modificados

### ✅ Configuração Principal

```
kidscoin-api/
├── railway.json                             [NOVO] ⚙️ Config Railway
├── .env.example                             [NOVO] 📝 Template variáveis
│
├── src/main/resources/
│   ├── application.yml                      [MODIFICADO] ✏️ Variáveis ambiente
│   └── application-prod.yml                 [NOVO] 🚀 Profile produção
│
└── docs/
    ├── DEPLOY_RAILWAY.md                    [NOVO] 📚 Guia completo (300+ linhas)
    ├── RAILWAY_QUICKSTART.md                [NOVO] ⚡ Quick start (5 min)
    ├── RAILWAY_FILES_SUMMARY.md             [NOVO] 📋 Este arquivo
    └── PROGRESS.md                           [MODIFICADO] 📊 Documentação sessão
```

---

## 📝 Detalhes dos Arquivos

### 1. `railway.json`
**Propósito:** Configuração do build e deploy no Railway

**Conteúdo:**
```json
{
  "build": {
    "buildCommand": "mvn clean package -DskipTests"
  },
  "deploy": {
    "startCommand": "java -Dspring.profiles.active=prod -jar target/kidscoin-api-1.0.0.jar"
  }
}
```

**O que faz:**
- Define comando de build Maven
- Pula testes para build mais rápido
- Ativa profile `prod` automaticamente

---

### 2. `.env.example`
**Propósito:** Template de variáveis de ambiente

**Uso:**
1. Copie para `.env` localmente (já no .gitignore)
2. Referência para configurar Railway

**Variáveis principais:**
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET` ⚠️ **CRÍTICO**
- `SPRING_PROFILES_ACTIVE`

---

### 3. `application.yml` (Modificado)
**Mudanças:**
```yaml
# ANTES
url: jdbc:postgresql://localhost:5432/educacao_financeira
username: postgres

# DEPOIS
url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/educacao_financeira}
username: ${DATABASE_USERNAME:postgres}
port: ${PORT:8080}
```

**Benefício:**
- ✅ Funciona localmente (valores padrão após `:`)
- ✅ Funciona no Railway (lê variáveis de ambiente)
- ✅ Sem precisar alterar código

---

### 4. `application-prod.yml` (Novo)
**Propósito:** Configurações específicas de produção

**Diferenças do dev:**
- `show-sql: false` - Sem poluir logs
- `LOG_LEVEL: INFO` - Menos verboso
- `include-stacktrace: never` - Segurança

---

### 5. `DEPLOY_RAILWAY.md`
**Propósito:** Guia completo de deploy

**Seções:**
1. Pré-requisitos
2. Passo a passo (5 etapas)
3. Configuração de variáveis
4. Gerar JWT Secret (3 métodos)
5. Obter URL da API
6. Testar endpoints (curl)
7. Configurar CORS
8. Monitoramento e logs
9. Troubleshooting (6 problemas comuns)
10. Deploy contínuo (CD)
11. Rollback
12. Custos
13. Conectar mobile
14. Checklist TCC

**Linhas:** ~300

---

### 6. `RAILWAY_QUICKSTART.md`
**Propósito:** Deploy rápido em 5 minutos

**Para quem tem pressa:**
1. Criar projeto (1 min)
2. Adicionar banco (30s)
3. Configurar variáveis (1 min)
4. Deploy automático (2 min)
5. Gerar URL (30s)

**Total:** ~5 minutos

---

## 🔑 Variáveis de Ambiente Railway

### Obrigatórias

| Variável | Exemplo | Onde Obter |
|----------|---------|------------|
| `DATABASE_URL` | `postgresql://user:pass@host:port/db` | ✅ Railway gera automaticamente |
| `DATABASE_USERNAME` | `postgres` | ✅ Railway gera automaticamente |
| `DATABASE_PASSWORD` | `******` | ✅ Railway gera automaticamente |
| `JWT_SECRET` | `64+ caracteres aleatórios` | ⚠️ **VOCÊ DEVE GERAR** |
| `SPRING_PROFILES_ACTIVE` | `prod` | Definir manualmente |

### Opcionais (têm defaults)

| Variável | Default | Descrição |
|----------|---------|-----------|
| `PORT` | `8080` | Railway injeta automaticamente |
| `LOG_LEVEL` | `INFO` | Nível de logging |
| `HIBERNATE_DDL_AUTO` | `update` | Estratégia DDL |

---

## 🔐 Gerar JWT Secret

### Método 1: Linux/Mac
```bash
openssl rand -base64 64
```

### Método 2: Windows PowerShell
```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

### Método 3: Online
https://www.browserling.com/tools/random-string (64 caracteres)

⚠️ **NUNCA** use a chave de desenvolvimento em produção!

---

## 📋 Checklist de Deploy

### Antes de Fazer Deploy
- [x] Código compilando (`mvn clean compile`)
- [x] Variáveis de ambiente configuradas
- [x] Profile prod criado
- [x] railway.json configurado
- [ ] Código commitado no GitHub

### Durante o Deploy
- [ ] Criar projeto Railway
- [ ] Conectar repositório GitHub
- [ ] Adicionar PostgreSQL
- [ ] Configurar variáveis (JWT_SECRET!)
- [ ] Aguardar build (3-5 min)
- [ ] Gerar domínio público

### Depois do Deploy
- [ ] Testar registro: `POST /api/auth/register`
- [ ] Testar login: `POST /api/auth/login`
- [ ] Copiar URL da API
- [ ] Atualizar mobile com URL de produção
- [ ] Criar dados de teste (família, tarefas)

---

## 🧪 Testar Localmente

### 1. Compilar
```bash
mvn clean package -DskipTests
```

### 2. Executar com Profile Prod
```bash
java -Dspring.profiles.active=prod -jar target/kidscoin-api-1.0.0.jar
```

### 3. Testar Endpoint
```bash
curl http://localhost:8080/api/auth/login
```

---

## 📱 Conectar Mobile ao Backend

### Opção 1: Variável de ambiente (.env)
```bash
# .env.production
API_URL=https://sua-url.railway.app/api
```

### Opção 2: Código TypeScript
```typescript
const API_URL = __DEV__
  ? 'http://localhost:8080/api'
  : 'https://kidscoin-api-production.up.railway.app/api';
```

---

## 💰 Custos Railway

| Plano | Preço | Crédito | Sleep Mode | Adequado para |
|-------|-------|---------|------------|---------------|
| Trial | Grátis | $5/mês | ✅ Sim (30min) | ✅ TCC/Demo |
| Hobby | $5/mês | Ilimitado | ❌ Não | Produção |

**Recomendação TCC:** Trial é suficiente!

---

## 🎓 Dicas para Apresentação

1. **Mostre o Dashboard:** Prove que está na nuvem
2. **Logs em tempo real:** Demonstre requests chegando
3. **Métricas:** Gráficos de CPU/Memória
4. **Rollback:** Explique recuperação de erros
5. **Escalabilidade:** Mencione que Railway escala automaticamente

---

## 🐛 Problemas Comuns

| Erro | Causa | Solução |
|------|-------|---------|
| Build falha | Maven não compilou | Veja logs completos |
| App não inicia | Variável faltando | Verifique JWT_SECRET |
| Erro de DB | PostgreSQL não criado | Adicione Database |
| 502 Bad Gateway | App não iniciou | Veja logs de startup |

**Guia completo:** [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md)

---

## 📞 Recursos

- **Railway Docs:** https://docs.railway.app
- **Railway Discord:** https://discord.gg/railway
- **Guia Completo:** [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md)
- **Quick Start:** [RAILWAY_QUICKSTART.md](./RAILWAY_QUICKSTART.md)
- **Progress:** [PROGRESS.md](./PROGRESS.md)

---

**Última atualização:** 04/11/2025
**Status:** ✅ 100% pronto para deploy
**Build:** SUCCESS (95 arquivos compilados)
