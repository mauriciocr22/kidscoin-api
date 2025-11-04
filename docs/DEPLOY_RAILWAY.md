# 🚀 Deploy Backend no Railway

Guia completo para hospedar a KidsCoins API no Railway.app

---

## 📋 Pré-requisitos

- ✅ Conta no [Railway.app](https://railway.app)
- ✅ Código no GitHub (repositório público ou privado)
- ✅ Java 17 configurado no projeto (✓ já está)
- ✅ Maven configurado (✓ já está)

---

## 🎯 Passo a Passo

### 1️⃣ Criar Projeto no Railway

1. Acesse [railway.app](https://railway.app)
2. Faça login com GitHub
3. Clique em **"New Project"**
4. Selecione **"Deploy from GitHub repo"**
5. Escolha o repositório `kidscoin-api`

### 2️⃣ Adicionar PostgreSQL

1. No dashboard do projeto, clique em **"+ New"**
2. Selecione **"Database"** → **"PostgreSQL"**
3. Railway criará automaticamente o banco de dados

✅ **Importante:** O Railway gera automaticamente a variável `DATABASE_URL`

### 3️⃣ Configurar Variáveis de Ambiente

No painel do serviço backend, vá em **"Variables"** e adicione:

#### Obrigatórias:

```bash
# Database (Railway preenche automaticamente)
DATABASE_URL=postgresql://user:password@host:port/database
DATABASE_USERNAME=user
DATABASE_PASSWORD=password

# JWT Secret (GERAR NOVA CHAVE FORTE!)
JWT_SECRET=sua-chave-super-secreta-minimo-256-bits-para-producao-trocar-esta-aqui

# Profile
SPRING_PROFILES_ACTIVE=prod
```

#### Opcionais (já tem valores padrão):

```bash
# Hibernate
HIBERNATE_DDL_AUTO=update

# Logging
LOG_LEVEL=INFO

# Porta (Railway define automaticamente)
PORT=8080
```

---

## 🔑 Gerar JWT Secret Seguro

Use um dos métodos abaixo para gerar uma chave forte:

### Opção 1: Online (mais fácil)
1. Acesse: https://www.browserling.com/tools/random-string
2. Comprimento: 64 caracteres
3. Inclua: letras, números e símbolos
4. Copie o resultado

### Opção 2: Terminal Linux/Mac
```bash
openssl rand -base64 64
```

### Opção 3: Terminal Windows (PowerShell)
```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

⚠️ **NUNCA use a chave de desenvolvimento em produção!**

---

## 📦 Deploy Automático

Após configurar as variáveis, o Railway automaticamente:

1. ✅ Detecta que é projeto Maven (Java)
2. ✅ Executa `mvn clean package -DskipTests`
3. ✅ Inicia aplicação com `java -jar target/kidscoin-api-1.0.0.jar`
4. ✅ Conecta ao PostgreSQL automaticamente

### Acompanhar Deploy

1. Vá na aba **"Deployments"**
2. Clique no deploy ativo para ver logs em tempo real
3. Aguarde mensagem: `Started KidsCoinsApiApplication`

---

## 🌐 Obter URL da API

1. No dashboard do serviço, vá em **"Settings"**
2. Seção **"Networking"**
3. Clique em **"Generate Domain"**
4. Railway criará URL tipo: `https://kidscoin-api-production.up.railway.app`

✅ **Copie esta URL** - você vai usar no mobile!

---

## 🧪 Testar API Online

### Teste 1: Health Check
```bash
curl https://sua-url.railway.app/api/auth/login
```

Deve retornar 400 (esperado, pois não enviou dados) ou resposta JSON.

### Teste 2: Registro de Pai
```bash
curl -X POST https://sua-url.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@kidscoin.com",
    "password": "senha123",
    "fullName": "Pai Teste"
  }'
```

### Teste 3: Login
```bash
curl -X POST https://sua-url.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "teste@kidscoin.com",
    "password": "senha123"
  }'
```

Deve retornar JSON com `accessToken`, `refreshToken` e dados do usuário.

---

## 🔧 Configurar CORS para o Mobile

O CORS já está configurado para aceitar todas origens em desenvolvimento. Para produção, você pode restringir:

1. Abra `SecurityConfig.java`
2. Localize `corsConfigurationSource()`
3. Atualize `allowedOrigins`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "exp://192.168.*.*:8081",  // Expo Go local
    "https://*.expo.dev",       // Expo Go online
    "https://sua-app.com"       // App publicado
));
```

⚠️ **Atenção:** Para desenvolvimento, deixe `*` (todos).

---

## 📊 Monitoramento

### Ver Logs em Tempo Real
1. Dashboard do Railway → Aba **"Deployments"**
2. Clique no deploy ativo
3. Logs aparecem automaticamente

### Métricas
- Aba **"Metrics"** → CPU, Memória, Network

### Banco de Dados
1. Clique no serviço PostgreSQL
2. Aba **"Data"** → Query Editor
3. Execute queries SQL:
```sql
SELECT COUNT(*) FROM users;
SELECT * FROM tasks LIMIT 10;
```

---

## 🐛 Troubleshooting

### Erro: "Failed to build"
**Causa:** Maven não conseguiu compilar

**Solução:**
1. Verifique se `pom.xml` está correto
2. Teste local: `mvn clean package`
3. Veja logs completos no Railway

### Erro: "Application failed to start"
**Causa:** Banco não conectado ou variável faltando

**Solução:**
1. Verifique variáveis: `DATABASE_URL`, `JWT_SECRET`
2. Veja logs: procure por "Exception" ou "Error"
3. Teste conexão do banco no painel PostgreSQL

### Erro: "Port already in use"
**Causa:** Railway usa porta dinâmica

**Solução:**
- Variável `PORT` já está configurada (linha 43 do application.yml)
- Railway injeta automaticamente

### Erro: "JWT Secret too short"
**Causa:** `JWT_SECRET` menor que 256 bits

**Solução:**
- Gere nova chave com **no mínimo 64 caracteres**
- Atualize variável no Railway

### Logs mostram LazyInitializationException
**Causa:** Já corrigido! Mas caso apareça em novo endpoint:

**Solução:**
- Adicione `@Transactional(readOnly = true)` no método do Service
- Use JOIN FETCH nas queries do Repository

---

## 🔄 Deploy Contínuo (CD)

Railway faz deploy automático a cada push no GitHub:

1. Faça mudanças no código
2. Commit e push para `master`:
```bash
git add .
git commit -m "feat: nova funcionalidade"
git push origin master
```
3. Railway detecta push e faz deploy automaticamente
4. Acompanhe na aba "Deployments"

### Rollback
Se algo der errado:
1. Aba "Deployments"
2. Encontre deploy anterior estável
3. Clique nos 3 pontos → **"Rollback to this version"**

---

## 💰 Custos Railway

### Plano Gratuito (Trial)
- 💵 **$5 de crédito grátis/mês**
- ✅ Suficiente para projetos pequenos/TCC
- ⏱️ Sleep após 30min de inatividade (acordada ao receber request)

### Plano Hobby ($5/mês)
- 💵 **$5 fixos/mês**
- ✅ Sem sleep mode
- ✅ Métricas avançadas

Para TCC: **Plano gratuito é suficiente!**

---

## 📱 Conectar Mobile ao Backend

No seu projeto Expo/React Native, atualize a URL base da API:

```typescript
// src/services/api.ts
const API_URL = __DEV__
  ? 'http://localhost:8080/api'  // Desenvolvimento
  : 'https://sua-url.railway.app/api';  // Produção

export const api = axios.create({
  baseURL: API_URL,
});
```

Ou use variáveis de ambiente (.env):
```bash
# .env.production
API_URL=https://kidscoin-api-production.up.railway.app/api
```

---

## ✅ Checklist Final

Antes de apresentar o TCC:

- [ ] Backend deployado no Railway
- [ ] PostgreSQL criado e conectado
- [ ] Todas variáveis de ambiente configuradas
- [ ] JWT_SECRET forte e seguro
- [ ] URL da API gerada
- [ ] Testes de registro e login funcionando
- [ ] Mobile conectado à API de produção
- [ ] Logs sem erros críticos
- [ ] Banco de dados com dados de teste (família, tarefas)

---

## 📞 Suporte

### Railway
- Docs: https://docs.railway.app
- Discord: https://discord.gg/railway

### Erros Comuns
- Veja seção **Troubleshooting** acima
- Verifique logs no Railway dashboard
- Teste endpoints com Postman/curl

---

## 🎓 Dica para Apresentação

Durante a defesa do TCC:

1. **Mostre o Dashboard Railway** - prova que está hospedado
2. **Demonstre Logs** - mostre requests chegando em tempo real
3. **Métricas** - mostre gráficos de uso
4. **Rollback** - explique que dá pra voltar versões
5. **Escalabilidade** - mencione que Railway escala automaticamente

---

**Última atualização:** 04/11/2025
**Status:** ✅ Pronto para deploy
**Suporte:** Java 17 | Spring Boot 3.2.5 | PostgreSQL 15
