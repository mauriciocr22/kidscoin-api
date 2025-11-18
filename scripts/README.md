# 🛠️ Scripts de Teste - KidsCoins API

Scripts utilitários para facilitar testes e demonstrações.

---

## 🎯 unlock-all-badges - Desbloquear Todas as Badges

Desbloqueia as **8 badges** de uma vez para uma criança. Ideal para **demonstração na banca do TCC**.

### Windows (PowerShell)

```powershell
cd scripts
.\unlock-all-badges.ps1 -Username "joaozinho" -ApiUrl "https://sua-api.up.railway.app"
```

**Exemplo real:**
```powershell
.\unlock-all-badges.ps1 -Username "maria" -ApiUrl "https://kidscoin-api-production.up.railway.app"
```

### Linux/Mac (Bash)

```bash
cd scripts
chmod +x unlock-all-badges.sh
./unlock-all-badges.sh joaozinho https://sua-api.up.railway.app
```

**Exemplo real:**
```bash
./unlock-all-badges.sh maria https://kidscoin-api-production.up.railway.app
```

---

## 📊 O que o script faz?

1. ✅ Desbloqueia **todas as 8 badges** automaticamente
2. ✅ Adiciona **XP bônus** de cada badge (+725 XP total)
3. ✅ Cria **8 notificações** no app
4. ✅ Pode causar **level up** (dependendo do XP atual)
5. ✅ Mostra resumo ao final

### Resultado esperado:

```
🎯 Desbloqueando todas as badges para: joaozinho
🌐 API: https://kidscoin-api.up.railway.app

📛 Desbloqueando: Primeira Tarefa... ✅
📛 Desbloqueando: Poupador Iniciante... ✅
📛 Desbloqueando: Trabalhador Dedicado... ✅
📛 Desbloqueando: Dia Produtivo... ✅
📛 Desbloqueando: Consistente... ✅
📛 Desbloqueando: Planejador... ✅
📛 Desbloqueando: Comprador Consciente... ✅
📛 Desbloqueando: Milionário... ✅

==================================================
📊 RESUMO
==================================================
✅ Badges desbloqueadas: 8
❌ Falhas: 0
✨ XP Total Ganho: +725 XP
==================================================

🎉 Pronto! Verifique o app mobile para ver as badges.
```

---

## 🏆 Badges que serão desbloqueadas

| # | Badge | XP Bônus |
|---|-------|----------|
| 1 | Primeira Tarefa | +25 XP |
| 2 | Poupador Iniciante | +50 XP |
| 3 | Trabalhador Dedicado | +75 XP |
| 4 | Dia Produtivo | +100 XP |
| 5 | Consistente | +150 XP |
| 6 | Planejador | +100 XP |
| 7 | Comprador Consciente | +50 XP |
| 8 | Milionário | +200 XP |
| **TOTAL** | | **+725 XP** |

---

## 📱 Verificar no Mobile

Após executar o script:

1. Abra o app mobile
2. Faça login com a criança (ex: `joaozinho`)
3. Vá para a tela de **Badges/Conquistas**
4. Todas as 8 badges devem estar **desbloqueadas** ✅
5. Verifique também a aba **Notificações** (8 novas)

---

## ⚠️ Notas Importantes

- ✅ **Idempotente**: Se rodar novamente, não duplica badges
- ✅ **Seguro**: Não remove dados existentes
- ✅ **Rápido**: Leva ~3 segundos para desbloquear tudo
- ⚠️ **Temporário**: Lembre-se de remover o endpoint `/debug/unlock` antes da entrega final

---

## 🔧 Troubleshooting

**Erro de conexão:**
```
❌ Erro: Unable to connect to remote server
```
→ Verifique se a URL da API está correta e acessível

**Criança não encontrada:**
```
❌ Criança não encontrada com username: joaozinho
```
→ Verifique se a criança foi cadastrada no sistema

**Script não executa (PowerShell):**
```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

**Script não executa (Bash):**
```bash
chmod +x unlock-all-badges.sh
```

---

**Criado em:** 18/11/2025
**Para:** Demonstração TCC - KidsCoins
