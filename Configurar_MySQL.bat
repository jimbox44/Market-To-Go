@echo off
setlocal

set MYSQL_BIN=C:\Program Files\MySQL\MySQL Server 8.4\bin
set MYSQL_EXE=%MYSQL_BIN%\mysql.exe
set MYSQLD_EXE=%MYSQL_BIN%\mysqld.exe
set DATA_DIR=C:\ProgramData\MySQL\MySQL Server 8.4\Data
set DB_SCRIPT=%~dp0database\markettogo.sql

echo ========================================
echo Configurando MySQL para MarketToGo
echo ========================================
echo.

if not exist "%MYSQL_EXE%" (
    echo ERROR: No se encontro mysql.exe en:
    echo %MYSQL_EXE%
    pause
    exit /b 1
)

netstat -ano | findstr ":3306" >nul 2>&1
if errorlevel 1 (
    echo MySQL no esta corriendo. Intentando iniciar mysqld...
    if not exist "%MYSQLD_EXE%" (
        echo ERROR: No se encontro mysqld.exe en:
        echo %MYSQLD_EXE%
        pause
        exit /b 1
    )
    start /B "" "%MYSQLD_EXE%" --datadir="%DATA_DIR%" --port=3306
    timeout /t 5 /nobreak >nul
) else (
    echo MySQL ya esta corriendo en puerto 3306.
)

netstat -ano | findstr ":3306" >nul 2>&1
if errorlevel 1 (
    echo ERROR: MySQL no pudo iniciar en puerto 3306.
    pause
    exit /b 1
)

if not exist "%DB_SCRIPT%" (
    echo ERROR: No se encontro el script SQL:
    echo %DB_SCRIPT%
    pause
    exit /b 1
)

echo Importando base de datos markettogo...
"%MYSQL_EXE%" -uroot -padmin123 < "%DB_SCRIPT%"
if errorlevel 1 (
    echo.
    echo ERROR: No se pudo importar la base con root/admin123.
    echo Revisa la contrasena de root o cambiala en Conexion.java.
    pause
    exit /b 1
)

echo.
echo Verificando usuario admin...
"%MYSQL_EXE%" -uroot -padmin123 markettogo -e "SELECT id,email,rol,activo FROM usuarios WHERE email='admin@markettogo.com';"

echo.
echo Listo. Credenciales de prueba:
echo Email: admin@markettogo.com
echo Password: admin123
echo.
pause
