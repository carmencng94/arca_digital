#!/usr/bin/env pwsh
# Script de compilación para Arca Digital (PowerShell)

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Compilando Arca Digital..." -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# Crear carpeta out si no existe
if (-not (Test-Path -Path "out")) {
    New-Item -ItemType Directory -Path "out" | Out-Null
    Write-Host "Carpeta 'out' creada." -ForegroundColor Green
}

# Compilar todos los archivos Java
$files = @(
    "src\com\arcadigital\model\Animal.java",
    "src\com\arcadigital\database\ConexionDB.java",
    "src\com\arcadigital\database\AnimalDAO.java",
    "src\com\arcadigital\api\ServidorAPI.java",
    "src\com\arcadigital\Main.java"
)

Write-Host "Compilando archivos..." -ForegroundColor Yellow

$compileCmd = @"
javac -d out -cp "lib\mariadb-java-client-3.5.7.jar;src" $($files -join ' ')
"@

Invoke-Expression $compileCmd

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Green
    Write-Host "Compilación exitosa!" -ForegroundColor Green
    Write-Host "======================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Para ejecutar el servidor, usa:" -ForegroundColor Cyan
    Write-Host '  java -cp "out;lib\mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI' -ForegroundColor White
    Write-Host ""
    Write-Host "O abre http://localhost:8080 en tu navegador" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "======================================" -ForegroundColor Red
    Write-Host "Error en la compilación!" -ForegroundColor Red
    Write-Host "======================================" -ForegroundColor Red
}
