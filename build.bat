@echo off
REM Script de compilación para Arca Digital
REM Compila todos los archivos Java y genera la carpeta out/

echo ======================================
echo Compilando Arca Digital...
echo ======================================

REM Crear carpeta out si no existe
if not exist out mkdir out

REM Compilar todos los archivos Java
javac -d out -cp "lib\mariadb-java-client-3.5.7.jar;src" ^
    src\com\arcadigital\model\Animal.java ^
    src\com\arcadigital\database\ConexionDB.java ^
    src\com\arcadigital\database\AnimalDAO.java ^
    src\com\arcadigital\api\ServidorAPI.java ^
    src\com\arcadigital\Main.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ======================================
    echo Compilación exitosa!
    echo ======================================
    echo.
    echo Para ejecutar el servidor, usa:
    echo   java -cp "out;lib\mariadb-java-client-3.5.7.jar" com.arcadigital.api.ServidorAPI
    echo.
) else (
    echo.
    echo ======================================
    echo Error en la compilación!
    echo ======================================
)

pause
