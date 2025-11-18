# Script PowerShell: Desbloquear TODAS as badges para uma crianca
# Util para demonstracao na banca do TCC

param(
    [Parameter(Mandatory=$true)]
    [string]$Username,

    [Parameter(Mandatory=$true)]
    [string]$ApiUrl
)

Write-Host "Desbloqueando todas as badges para: $Username" -ForegroundColor Cyan
Write-Host "API: $ApiUrl" -ForegroundColor Gray
Write-Host ""

$badges = @(
    "Primeira Tarefa",
    "Poupador Iniciante",
    "Trabalhador Dedicado",
    "Dia Produtivo",
    "Consistente",
    "Planejador",
    "Comprador Consciente",
    "Milionario"
)

$totalXP = 0
$unlocked = 0
$failed = 0

foreach ($badgeName in $badges) {
    Write-Host "Desbloqueando: $badgeName..." -NoNewline

    $body = @{
        username = $Username
        badgeName = $badgeName
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "$ApiUrl/gamification/debug/unlock" -Method POST -ContentType "application/json" -Body $body

        if ($response -like "*sucesso*") {
            Write-Host " OK" -ForegroundColor Green
            $unlocked++

            # Extrair XP da resposta
            if ($response -match '\+(\d+) XP') {
                $xp = [int]$matches[1]
                $totalXP += $xp
            }
        } elseif ($response -like "*ja possui*") {
            Write-Host " JA DESBLOQUEADA" -ForegroundColor Yellow
        } else {
            Write-Host " ERRO: $response" -ForegroundColor Red
            $failed++
        }
    } catch {
        Write-Host " ERRO: $($_.Exception.Message)" -ForegroundColor Red
        $failed++
    }

    Start-Sleep -Milliseconds 300
}

Write-Host ""
Write-Host "=================================================="
Write-Host "RESUMO"
Write-Host "=================================================="
Write-Host "Badges desbloqueadas: $unlocked" -ForegroundColor Green
Write-Host "Ja possuia: $($badges.Count - $unlocked - $failed)" -ForegroundColor Yellow
Write-Host "Falhas: $failed" -ForegroundColor Red
Write-Host "XP Total Ganho: +$totalXP XP" -ForegroundColor Magenta
Write-Host "=================================================="
Write-Host ""
Write-Host "Pronto! Verifique o app mobile para ver as badges." -ForegroundColor Green
Write-Host ""
