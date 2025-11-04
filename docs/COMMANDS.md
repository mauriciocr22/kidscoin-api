# 🛠️ Comandos Úteis - KidsCoins API

Comandos frequentes para desenvolvimento e deploy

---

## 🏗️ Build & Compilação

### Compilar
```bash
mvn clean compile
```

### Compilar sem testes
```bash
mvn clean compile -DskipTests
```

### Gerar JAR
```bash
mvn clean package -DskipTests
```

### Executar testes
```bash
mvn test
```

---

## 🚀 Executar Aplicação

### Modo desenvolvimento
```bash
mvn spring-boot:run
```

### Profile produção (local)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Executar JAR gerado
```bash
java -jar target/kidscoin-api-1.0.0.jar
```

### Executar JAR com profile prod
```bash
java -Dspring.profiles.active=prod -jar target/kidscoin-api-1.0.0.jar
```

### Com variáveis de ambiente
```bash
# Windows PowerShell
$env:JWT_SECRET="sua-chave-secreta"
mvn spring-boot:run

# Linux/Mac
JWT_SECRET=sua-chave-secreta mvn spring-boot:run
```

---

## 🗄️ PostgreSQL Local

### Iniciar serviço
```bash
# Windows
net start postgresql-x64-15

# Linux
sudo systemctl start postgresql

# Mac
brew services start postgresql
```

### Conectar ao banco
```bash
psql -U postgres -d educacao_financeira
```

### Criar banco (primeira vez)
```bash
psql -U postgres -c "CREATE DATABASE educacao_financeira;"
```

### Resetar banco (CUIDADO!)
```sql
DROP DATABASE educacao_financeira;
CREATE DATABASE educacao_financeira;
```

### Ver tabelas
```sql
\dt
```

### Contar usuários
```sql
SELECT COUNT(*) FROM users;
SELECT role, COUNT(*) FROM users GROUP BY role;
```

### Ver tarefas
```sql
SELECT * FROM tasks LIMIT 10;
```

---

## 🔐 Gerar JWT Secret

### Linux/Mac
```bash
openssl rand -base64 64
```

### Windows PowerShell
```powershell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(64))
```

### Node.js (qualquer OS)
```bash
node -e "console.log(require('crypto').randomBytes(64).toString('base64'))"
```

---

## 🧪 Testar API (curl)

### Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "pai@teste.com",
    "password": "senha123",
    "fullName": "Pai Teste"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrUsername": "pai@teste.com",
    "password": "senha123"
  }'
```

### Criar criança (com token)
```bash
curl -X POST http://localhost:8080/api/users/children \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "fullName": "João Silva",
    "username": "joaozinho",
    "age": 8,
    "pin": "1234"
  }'
```

### Criar tarefa
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "title": "Arrumar quarto",
    "description": "Organizar brinquedos",
    "coinValue": 50,
    "xpValue": 100,
    "category": "LIMPEZA",
    "childrenIds": ["uuid-da-crianca"]
  }'
```

---

## 🐳 Docker (Opcional)

### Criar imagem
```bash
docker build -t kidscoin-api .
```

### Executar container
```bash
docker run -p 8080:8080 \
  -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/educacao_financeira \
  -e DATABASE_USERNAME=postgres \
  -e DATABASE_PASSWORD=postgres \
  -e JWT_SECRET=sua-chave-aqui \
  kidscoin-api
```

---

## 🔍 Git & GitHub

### Status
```bash
git status
```

### Add & Commit
```bash
git add .
git commit -m "feat: adiciona configuração Railway"
```

### Push
```bash
git push origin master
```

### Ver histórico
```bash
git log --oneline -10
```

### Ver último commit
```bash
git log -1
```

---

## 🚂 Railway CLI (Opcional)

### Instalar
```bash
npm install -g @railway/cli
```

### Login
```bash
railway login
```

### Iniciar projeto
```bash
railway init
```

### Link projeto existente
```bash
railway link
```

### Ver logs
```bash
railway logs
```

### Abrir no navegador
```bash
railway open
```

### Deploy manual
```bash
railway up
```

---

## 📊 Logs & Debug

### Ver logs em tempo real
```bash
mvn spring-boot:run | grep -i error
```

### Logs do Railway (web)
1. Dashboard → Deployments
2. Click no deploy ativo
3. Logs aparecem automaticamente

### Verificar porta em uso
```bash
# Windows
netstat -ano | findstr :8080

# Linux/Mac
lsof -i :8080
```

### Matar processo na porta
```bash
# Windows (rodar como Admin)
netstat -ano | findstr :8080
taskkill /PID [numero_do_pid] /F

# Linux/Mac
kill -9 $(lsof -t -i:8080)
```

---

## 🧹 Limpeza

### Limpar target Maven
```bash
mvn clean
```

### Remover dependências baixadas (resetar)
```bash
rm -rf ~/.m2/repository
mvn clean install
```

### Limpar cache IntelliJ
```bash
# File → Invalidate Caches → Invalidate and Restart
```

---

## 📱 Testar com Mobile

### Descobrir IP local
```bash
# Windows
ipconfig

# Linux/Mac
ifconfig
```

### Atualizar API_URL no mobile
```typescript
// Para testar local
const API_URL = 'http://192.168.1.100:8080/api';

// Para testar Railway
const API_URL = 'https://sua-url.railway.app/api';
```

---

## 🎯 Checklist Rápido

### Antes de commitar
```bash
mvn clean compile -DskipTests  # Compilar
git status                      # Ver mudanças
git add .                       # Add arquivos
git commit -m "mensagem"        # Commit
```

### Antes de deploy Railway
```bash
mvn clean package -DskipTests  # Gerar JAR
java -jar target/*.jar          # Testar local
git push origin master          # Enviar para GitHub
# Railway faz deploy automático!
```

### Testar após deploy
```bash
curl https://sua-url.railway.app/api/auth/login
```

---

## 📞 Ajuda Rápida

| Comando | O que faz |
|---------|-----------|
| `mvn clean compile` | Compila código |
| `mvn spring-boot:run` | Executa aplicação |
| `mvn clean package` | Gera JAR |
| `psql -U postgres` | Abre PostgreSQL |
| `git status` | Ver mudanças |
| `git push` | Enviar para GitHub |
| `curl POST /api/auth/login` | Testar login |

---

**Última atualização:** 04/11/2025
**Mais informações:**
- [DEPLOY_RAILWAY.md](./DEPLOY_RAILWAY.md)
- [RAILWAY_QUICKSTART.md](./RAILWAY_QUICKSTART.md)
- [PROGRESS.md](./PROGRESS.md)
