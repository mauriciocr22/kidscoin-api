#!/bin/bash

# 🔧 Script Bash: Desbloquear TODAS as badges para uma criança
# Útil para demonstração na banca do TCC

# Verificar argumentos
if [ $# -ne 2 ]; then
    echo "❌ Uso: ./unlock-all-badges.sh <username> <api-url>"
    echo "   Exemplo: ./unlock-all-badges.sh joaozinho https://kidscoin-api.up.railway.app"
    exit 1
fi

USERNAME=$1
API_URL=$2

echo "🎯 Desbloqueando todas as badges para: $USERNAME"
echo "🌐 API: $API_URL"
echo ""

badges=(
    "Primeira Tarefa"
    "Poupador Iniciante"
    "Trabalhador Dedicado"
    "Dia Produtivo"
    "Consistente"
    "Planejador"
    "Comprador Consciente"
    "Milionário"
)

total_xp=0
unlocked=0
failed=0

for badge in "${badges[@]}"; do
    echo -n "📛 Desbloqueando: $badge..."

    response=$(curl -s -X POST "$API_URL/api/gamification/debug/unlock" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$USERNAME\",\"badgeName\":\"$badge\"}")

    if [[ $response == *"sucesso"* ]]; then
        echo " ✅"
        ((unlocked++))

        # Extrair XP (regex básico)
        if [[ $response =~ \+([0-9]+)\ XP ]]; then
            xp="${BASH_REMATCH[1]}"
            ((total_xp += xp))
        fi
    elif [[ $response == *"já possui"* ]]; then
        echo " ⚠️  Já desbloqueada"
    else
        echo " ❌ $response"
        ((failed++))
    fi

    sleep 0.3
done

echo ""
echo "=================================================="
echo "📊 RESUMO"
echo "=================================================="
echo "✅ Badges desbloqueadas: $unlocked"
echo "❌ Falhas: $failed"
echo "✨ XP Total Ganho: +$total_xp XP"
echo "=================================================="
echo ""
echo "🎉 Pronto! Verifique o app mobile para ver as badges."
echo ""
