# 🔧 Script PowerShell: Desbloquear TODAS as badges para uma criança
# Útil para demonstração na banca do TCC

param(
    [Parameter(Mandatory=$true)]
    [string]$Username,

    [Parameter(Mandatory=$true)]
    [string]$ApiUrl
)

Write-Host "🎯 Desbloqueando todas as badges para: $Username" -ForegroundColor Cyan
Write-Host "🌐 API: $ApiUrl`n" -ForegroundColor Gray

$badges = @(
    "Primeira Tarefa",
    "Poupador Iniciante",
    "Trabalhador Dedicado",
    "Dia Produtivo",
    "Consistente",
    "Planejador",
    "Comprador Consciente",
    "Milionário"
)

$totalXP = 0
$unlocked = 0
$failed = 0

foreach ($badgeName in $badges) {
    Write-Host "📛 Desbloqueando: $badgeName..." -NoNewline

    $body = @{
        username = $Username
        badgeName = $badgeName
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod `
            -Uri "$ApiUrl/api/gamification/debug/unlock" `
            -Method POST `
            -ContentType "application/json" `
            -Body $body

        if ($response -like "*sucesso*") {
            Write-Host " ✅" -ForegroundColor Green
            $unlocked++

            # Extrair XP da resposta (regex simples)
            if ($response -match '\+(\d+) XP') {
                $xp = [int]$matches[1]
                $totalXP += $xp
            }
        } elseif ($response -like "*já possui*") {
            Write-Host " ⚠️  Já desbloqueada" -ForegroundColor Yellow
        } else {
            Write-Host " ❌ $response" -ForegroundColor Red
            $failed++
        }
    } catch {
        Write-Host " ❌ Erro: $($_.Exception.Message)" -ForegroundColor Red
        $failed++
    }

    Start-Sleep -Milliseconds 300
}

Write-Host "`n" + "="*50 -ForegroundColor Cyan
Write-Host "📊 RESUMO" -ForegroundColor Cyan
Write-Host "="*50 -ForegroundColor Cyan
Write-Host "✅ Badges desbloqueadas: $unlocked" -ForegroundColor Green
Write-Host "⚠️  Já possuía: $($badges.Count - $unlocked - $failed)" -ForegroundColor Yellow
Write-Host "❌ Falhas: $failed" -ForegroundColor Red
Write-Host "✨ XP Total Ganho: +$totalXP XP" -ForegroundColor Magenta
Write-Host "="*50 -ForegroundColor Cyan

Write-Host "`n🎉 Pronto! Verifique o app mobile para ver as badges.`n" -ForegroundColor Green
