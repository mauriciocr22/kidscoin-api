# 🔧 DEBUG: Desbloquear Badges Manualmente

> ⚠️ **ATENÇÃO:** Este é um endpoint temporário para TESTES. Remover antes da apresentação final!

## 🎯 Como Usar

### 1️⃣ **Via Postman/Insomnia** (Recomendado)

**Endpoint:**
```
POST https://sua-url-railway.up.railway.app/api/gamification/debug/unlock
```

**Headers:**
```
Content-Type: application/json
```

**Body (JSON):**
```json
{
  "username": "joaozinho",
  "badgeName": "Primeira Tarefa"
}
```

**Resposta de Sucesso:**
```
✅ Badge 'Primeira Tarefa' desbloqueada com sucesso para João Silva (+25 XP)!
```

### 2️⃣ **Via cURL** (Terminal/CMD)

```bash
curl -X POST https://sua-url-railway.up.railway.app/api/gamification/debug/unlock \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"joaozinho\",\"badgeName\":\"Primeira Tarefa\"}"
```

### 3️⃣ **Via PowerShell** (Windows)

```powershell
Invoke-RestMethod -Uri "https://sua-url-railway.up.railway.app/api/gamification/debug/unlock" `
  -Method POST `
  -ContentType "application/json" `
  -Body '{"username":"joaozinho","badgeName":"Primeira Tarefa"}'
```

---

## 🏆 Badges Disponíveis

| Nome da Badge | Descrição | XP Bônus |
|---------------|-----------|----------|
| **Primeira Tarefa** | Complete sua primeira tarefa | +25 XP |
| **Poupador Iniciante** | Acumule 100 moedas na carteira | +50 XP |
| **Trabalhador Dedicado** | Complete 10 tarefas | +75 XP |
| **Dia Produtivo** | Complete 5 tarefas em um dia | +100 XP |
| **Consistente** | Complete tarefas por 7 dias seguidos | +150 XP |
| **Planejador** | Guarde 200 moedas na poupança | +100 XP |
| **Comprador Consciente** | Resgate sua primeira recompensa | +50 XP |
| **Milionário** | Ganhe 1000 moedas no total | +200 XP |

---

## 📱 Como Verificar no Mobile

1. Faça a requisição POST acima
2. Abra o app mobile
3. Faça login com a criança (username que você usou)
4. Vá para a tela de **Badges/Conquistas**
5. A badge deve aparecer como **desbloqueada** ✅

**Bônus:** A criança também receberá:
- ✨ **XP Bônus** (varia por badge)
- 🔔 **Notificação** no app
- 📈 **Possível Level Up** (se o XP foi suficiente)

---

## ⚠️ Mensagens de Erro

**Criança não encontrada:**
```
❌ Criança não encontrada com username: joaozinho
```
→ Verifique se o username está correto

**Badge não encontrada:**
```
❌ Badge não encontrada: Primeira. Badges disponíveis: Primeira Tarefa, Poupador Iniciante, ...
```
→ Use o nome exato da badge (case-insensitive)

**Badge já desbloqueada:**
```
⚠️ Criança já possui a badge: Primeira Tarefa
```
→ Tente outra badge que ainda não foi desbloqueada

---

## 🧪 Exemplo Completo de Teste

### Cenário: Desbloquear 3 badges para "joaozinho"

**1. Primeira Tarefa:**
```json
POST /api/gamification/debug/unlock
{
  "username": "joaozinho",
  "badgeName": "Primeira Tarefa"
}
```

**2. Poupador Iniciante:**
```json
POST /api/gamification/debug/unlock
{
  "username": "joaozinho",
  "badgeName": "Poupador Iniciante"
}
```

**3. Milionário:**
```json
POST /api/gamification/debug/unlock
{
  "username": "joaozinho",
  "badgeName": "Milionário"
}
```

**Resultado:**
- 3 badges desbloqueadas
- +275 XP total (25 + 50 + 200)
- 3 notificações no app
- Possível level up (se tinha XP próximo)

---

## 🗑️ Lembrete

**Antes da apresentação final do TCC:**

1. Remover endpoint:
   - Deletar `UnlockBadgeDebugRequest.java`
   - Remover método `unlockBadgeDebug()` do `GamificationController`
   - Remover método `unlockBadgeForTest()` do `GamificationService`

2. Ou comentar o endpoint com:
   ```java
   // @PostMapping("/debug/unlock")
   ```

---

## 📝 Notas

- ✅ Não requer autenticação JWT (para facilitar testes)
- ✅ Funciona com qualquer criança cadastrada
- ✅ Adiciona XP bônus automaticamente
- ✅ Cria notificação automaticamente
- ✅ Verifica se já possui a badge (evita duplicatas)
- ✅ Case-insensitive (aceita "primeira tarefa" ou "PRIMEIRA TAREFA")

---

**Última atualização:** 18/11/2025
