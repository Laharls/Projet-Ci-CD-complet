@ECHO OFF
SETLOCAL ENABLEEXTENSIONS

REM --- Dossiers wrapper ---
set "BASE_DIR=%~dp0"
set "WRAPPER_DIR=.mvn\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
set "PROPERTIES_FILE=%WRAPPER_DIR%\maven-wrapper.properties"

IF NOT EXIST "%BASE_DIR%%PROPERTIES_FILE%" (
  ECHO Error: %PROPERTIES_FILE% not found.
  EXIT /B 1
)

REM --- Lire wrapperUrl depuis maven-wrapper.properties ---
FOR /F "usebackq tokens=1,* delims==" %%A IN ("%BASE_DIR%%PROPERTIES_FILE%") DO (
  IF /I "%%~A"=="wrapperUrl" SET "WRAPPER_URL=%%~B"
)
IF NOT DEFINED WRAPPER_URL (
  ECHO Error: wrapperUrl not found in %PROPERTIES_FILE%
  EXIT /B 1
)

REM --- Télécharger le JAR du wrapper si manquant ---
IF NOT EXIST "%BASE_DIR%%WRAPPER_JAR%" (
  IF NOT EXIST "%BASE_DIR%%WRAPPER_DIR%" MKDIR "%BASE_DIR%%WRAPPER_DIR%"
  WHERE curl >NUL 2>NUL
  IF %ERRORLEVEL%==0 (
    curl -fSL "%WRAPPER_URL%" -o "%BASE_DIR%%WRAPPER_JAR%"
  ) ELSE (
    WHERE wget >NUL 2>NUL
    IF %ERRORLEVEL%==0 (
      wget -q "%WRAPPER_URL%" -O "%BASE_DIR%%WRAPPER_JAR%"
    ) ELSE (
      powershell -NoLogo -NoProfile -ExecutionPolicy Bypass ^
        -Command "Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%BASE_DIR%%WRAPPER_JAR%'"
    )
  )
  IF NOT EXIST "%BASE_DIR%%WRAPPER_JAR%" (
    ECHO Error: failed to download %WRAPPER_URL%
    EXIT /B 1
  )
)

REM --- Détection Java ---
set "JAVA_EXE="
IF DEFINED JAVA_HOME IF EXIST "%JAVA_HOME%\bin\java.exe" SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
IF NOT DEFINED JAVA_EXE SET "JAVA_EXE=java"

REM Test Java (sans WHERE pour éviter les guillemets/espaces)
set "JAVA_DETECTED="
FOR /F "delims=" %%V IN ('"%JAVA_EXE%" -version 2^>^&1') DO ( set "JAVA_DETECTED=1" & goto :after_java_check )
:after_java_check
IF NOT DEFINED JAVA_DETECTED (
  ECHO Error: Java not found. Install JDK 17+ or set JAVA_HOME.
  EXIT /B 1
)

REM --- Lancer Maven Wrapper (IMPORTANT: terminer le projet par \. pour ne pas casser la guillemet) ---
"%JAVA_EXE%" "-Dmaven.multiModuleProjectDirectory=%BASE_DIR%." -classpath "%BASE_DIR%%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*

ENDLOCAL
