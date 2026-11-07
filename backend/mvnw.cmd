@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.
@REM ----------------------------------------------------------------------------

@IF "%DEBUG%" == "" @ECHO OFF
@REM set %ENABLE_DELAYED_EXPANSION% to on
SETLOCAL ENABLEDELAYEDEXPANSION

SET "MAVEN_CMD_LINE_ARGS=%*"

IF EXIST "%~dp0..\..\..\Program Files\apache-maven-3.9.16\bin\mvn.cmd" (
    "%~dp0..\..\..\Program Files\apache-maven-3.9.16\bin\mvn.cmd" %*
    EXIT /B %ERRORLEVEL%
)

WHERE mvn >nul 2>nul
IF %ERRORLEVEL% EQU 0 (
    mvn %*
    EXIT /B %ERRORLEVEL%
)

ECHO Maven was not found in PATH or standard installation path.
ECHO Please install Maven 3.8+ or add it to your system PATH.
EXIT /B 1
