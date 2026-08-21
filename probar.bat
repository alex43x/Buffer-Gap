@echo off
setlocal

set "BUILD_DIR=build"

if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
del /q "*.class" 2>nul
mkdir "%BUILD_DIR%"
if errorlevel 1 goto error

echo Compilando en %BUILD_DIR%...
javac -d "%BUILD_DIR%" *.java
if errorlevel 1 goto error

echo.
echo Ejecutando TestBufferGap...
java -cp "%BUILD_DIR%" TestBufferGap
if errorlevel 1 goto error

echo.
echo Ejecutando TestHistorial...
java -cp "%BUILD_DIR%" TestHistorial
if errorlevel 1 goto error

set "RESULTADO=0"
echo.
echo Todas las pruebas finalizaron correctamente.
goto limpiar

:error
set "RESULTADO=%ERRORLEVEL%"
if "%RESULTADO%"=="0" set "RESULTADO=1"
echo.
echo Las pruebas finalizaron con errores.

:limpiar
if exist "%BUILD_DIR%" rmdir /s /q "%BUILD_DIR%"
del /q "*.class" 2>nul
echo Archivos .class eliminados.
exit /b %RESULTADO%
