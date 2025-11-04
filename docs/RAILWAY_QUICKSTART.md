# ⚡ Railway Deploy - Quick Start

Guia rápido para deploy no Railway (5 minutos)

---

## 🚀 Passos Rápidos

### 1. Criar Projeto
1. https://railway.app → Login GitHub
2. **New Project** → **Deploy from GitHub repo**
3. Selecione `kidscoin-api`

### 2. Adicionar Banco
1. **+ New** → **Database** → **PostgreSQL**
2. ✅ Railway cria automaticamente

### 3. Configurar Variáveis
No serviço backend → **Variables** → Adicionar:

```bash
# JWT Secret (GERAR NOVA!)
JWT_SECRET=sua-chave-super-secreta-64-caracteres-minimo

# Profile
SPRING_PROFILES_ACTIVE=prod
```

**Gerar JWT Secret:**
```bash
# Linux/Mac
openssl rand -base64 64

# Windows PowerShell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

### 4. Deploy Automático
✅ Railway detecta Maven e faz deploy sozinho!

Aguarde: `Started KidsCoinsApiApplication` nos logs

### 5. Gerar URL
1. **Settings** → **Networking**
2. **Generate Domain**
3. Copie: `https://sua-url.railway.app`

---

## ✅ Testar

```bash
curl https://sua-url.railway.app/api/auth/login
```

Se retornar 400 ou JSON → ✅ **Funcionando!**

---

## 📱 Conectar Mobile

```typescript
// .env.production
API_URL=https://sua-url.railway.app/api
```

---

## 🐛 Problemas?

1. **Não inicia?** → Verifique `JWT_SECRET` nas variáveis
2. **Erro de DB?** → PostgreSQL foi criado?
3. **Build falha?** → Veja logs em **Deployments**

**Guia completo:** [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md)

---

**Tempo estimado:** 5-10 minutos
**Custo:** $5 grátis/mês (suficiente para TCC)
