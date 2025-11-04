# 🚀 Guia de Deploy Railway - KidsCoins API

> **Status:** ✅ 100% pronto para deploy em produção

---

## 📚 Documentação Disponível

### 🎯 Por Onde Começar?

| Documento | Tempo | Quando Usar |
|-----------|-------|-------------|
| [RAILWAY_QUICKSTART.md](./RAILWAY_QUICKSTART.md) | 5 min | Quando quer fazer deploy **RÁPIDO** |
| [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md) | 20 min | Quando quer entender **TUDO** |
| [RAILWAY_FILES_SUMMARY.md](./RAILWAY_FILES_SUMMARY.md) | 10 min | Quando quer ver **O QUE FOI FEITO** |
| [COMMANDS.md](./COMMANDS.md) | Referência | Quando precisa de **COMANDOS ÚTEIS** |

---

## 🏃 Deploy em 3 Passos

### 1️⃣ Preparação (5 minutos)

```bash
# Compilar e testar
mvn clean package -DskipTests

# Ver arquivos criados
ls -la railway.json .env.example
ls -la src/main/resources/application-prod.yml
```

✅ **Tudo OK?** Prossiga!

---

### 2️⃣ Deploy no Railway (5 minutos)

1. **Acesse:** https://railway.app → Login GitHub
2. **New Project** → Deploy from GitHub repo
3. **Selecione:** `kidscoin-api`
4. **Adicione:** + New → Database → PostgreSQL
5. **Configure variáveis:**
   - Vá em Variables
   - Adicione: `JWT_SECRET` (gere com comando abaixo)
   - Adicione: `SPRING_PROFILES_ACTIVE=prod`

**Gerar JWT Secret:**
```bash
# Linux/Mac
openssl rand -base64 64

# Windows PowerShell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

---

### 3️⃣ Obter URL e Testar (2 minutos)

1. **Settings** → **Networking** → **Generate Domain**
2. Copie a URL (ex: `https://kidscoin-api-production.up.railway.app`)
3. Teste:

```bash
curl https://sua-url.railway.app/api/auth/login
```

Se retornar 400 ou JSON → ✅ **Funcionando!**

---

## 📁 Estrutura de Arquivos

```
kidscoin-api/
│
├── 🚀 RAILWAY - Arquivos de Deploy
│   ├── railway.json                    # Config Railway
│   └── .env.example                    # Template de variáveis
│
├── ⚙️ CONFIG - Profiles Spring
│   └── src/main/resources/
│       ├── application.yml             # Config com env vars
│       └── application-prod.yml        # Profile produção
│
└── 📚 DOCS - Documentação
    ├── DEPLOY_RAILWAY.md               # Guia completo (300+ linhas)
    ├── RAILWAY_QUICKSTART.md           # Quick start (5 min)
    ├── RAILWAY_FILES_SUMMARY.md        # Resumo arquivos
    ├── COMMANDS.md                     # Comandos úteis
    ├── README_DEPLOY.md                # Este arquivo
    └── PROGRESS.md                     # Histórico completo
```

---

## 🔑 Variáveis de Ambiente

### ✅ Railway gera automaticamente:
- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `PORT`

### ⚠️ Você precisa adicionar:
- `JWT_SECRET` (64+ caracteres) **OBRIGATÓRIO**
- `SPRING_PROFILES_ACTIVE=prod` **OBRIGATÓRIO**

### 🆗 Opcionais (já têm defaults):
- `LOG_LEVEL=INFO`
- `HIBERNATE_DDL_AUTO=update`

---

## 🧪 Testar Localmente Antes

```bash
# 1. Compilar
mvn clean package -DskipTests

# 2. Executar com profile prod
java -Dspring.profiles.active=prod -jar target/kidscoin-api-1.0.0.jar

# 3. Testar (nova aba terminal)
curl http://localhost:8080/api/auth/login
```

---

## 📱 Conectar Mobile

Após deploy, atualize a URL no mobile:

```typescript
// .env.production
API_URL=https://kidscoin-api-production.up.railway.app/api
```

Ou no código:

```typescript
const API_URL = __DEV__
  ? 'http://localhost:8080/api'
  : 'https://sua-url.railway.app/api';
```

---

## 🎓 Para Apresentação TCC

### Mostre Durante a Defesa:

1. ✅ **Dashboard Railway** - Prova hospedagem na nuvem
2. ✅ **Logs em tempo real** - Requests chegando
3. ✅ **Métricas** - Gráficos CPU/Memória
4. ✅ **PostgreSQL** - Dados persistidos
5. ✅ **URL pública** - API acessível de qualquer lugar

### Mencione:

- ✅ Deploy automático (CD) a cada push
- ✅ Rollback disponível
- ✅ Escalabilidade automática
- ✅ Monitoramento em tempo real
- ✅ Variáveis de ambiente seguras

---

## 🐛 Problemas Comuns

| Problema | Solução Rápida |
|----------|----------------|
| Build falha | Veja logs: Deployments → Click no deploy |
| App não inicia | Verifique `JWT_SECRET` nas variáveis |
| Erro 502 | Aguarde 1-2 min (Railway iniciando) |
| DB não conecta | PostgreSQL foi adicionado? |

**Troubleshooting completo:** [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md#-troubleshooting)

---

## 💰 Custos

| Plano | Preço | Adequado para |
|-------|-------|---------------|
| **Trial** | $5 grátis/mês | ✅ **TCC/Demo** (Recomendado!) |
| Hobby | $5/mês | Produção pequena |

**Trial é suficiente para apresentar o TCC!**

---

## ✅ Checklist Final

Antes de apresentar:

- [ ] Backend deployado no Railway
- [ ] PostgreSQL criado e conectado
- [ ] Variáveis configuradas (`JWT_SECRET`, `SPRING_PROFILES_ACTIVE`)
- [ ] URL pública gerada
- [ ] Testes de registro/login funcionando
- [ ] Mobile conectado à API de produção
- [ ] Logs sem erros críticos
- [ ] Dados de teste criados (família, tarefas, criança)

---

## 🚦 Status dos Arquivos

| Arquivo | Status | Descrição |
|---------|--------|-----------|
| `railway.json` | ✅ Criado | Config deploy Railway |
| `application-prod.yml` | ✅ Criado | Profile produção |
| `application.yml` | ✅ Modificado | Suporte env vars |
| `.env.example` | ✅ Criado | Template variáveis |
| `DEPLOY_RAILWAY.md` | ✅ Criado | Guia completo |
| `RAILWAY_QUICKSTART.md` | ✅ Criado | Quick start |
| `COMMANDS.md` | ✅ Criado | Comandos úteis |
| `PROGRESS.md` | ✅ Atualizado | Documentação sessão |

**Compilação:** ✅ BUILD SUCCESS (95 arquivos)

---

## 📞 Suporte & Links

- **Railway Docs:** https://docs.railway.app
- **Railway Dashboard:** https://railway.app/dashboard
- **Railway Discord:** https://discord.gg/railway

---

## 🎯 Resumo Visual

```
┌─────────────────────────────────────────┐
│  1️⃣ Código no GitHub                     │
│      ↓                                   │
│  2️⃣ Railway detecta push                 │
│      ↓                                   │
│  3️⃣ mvn clean package                    │
│      ↓                                   │
│  4️⃣ java -jar app.jar (profile prod)     │
│      ↓                                   │
│  5️⃣ Conecta PostgreSQL automaticamente   │
│      ↓                                   │
│  6️⃣ App online! 🎉                        │
│      https://sua-url.railway.app         │
└─────────────────────────────────────────┘
```

---

**Criado em:** 04/11/2025
**Última atualização:** 04/11/2025
**Versão:** 1.0.0
**Status:** ✅ Pronto para produção

---

## 🎬 Próximos Passos

1. **Leia:** [RAILWAY_QUICKSTART.md](./RAILWAY_QUICKSTART.md) (5 min)
2. **Execute:** Deploy no Railway (5 min)
3. **Teste:** API online (2 min)
4. **Conecte:** Mobile ao backend (5 min)
5. **Apresente:** TCC com sucesso! 🎓🚀

**Boa sorte na apresentação!** 🍀
