@ECHO OFF
PUSHD %~dp0|rem temporally creates a network drive if executed from a network path
<!-- : --- get date Script ---------------------------
FOR /F "delims=" %%x IN ('cscript //nologo "%~f0?.wsf"') DO %%x
GOTO:rest
-->
<job id="Elevate"><script language="VBScript">
Wscript.Echo("set Year=" & DatePart("yyyy", Date))
Wscript.Echo("set Month=0" & DatePart("m", Date))
Wscript.Echo("set Day=0" & DatePart("d", Date))
</script></job>
:rest
ECHO preparing
if defined JDK_HOME GOTO:start
rem This script located the current version of
rem "Java Development Kit" and sets the
rem %JDK_HOME% environment variable
setlocal ENABLEEXTENSIONS
set KEY=HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Development Kit
FOR /F "usebackq tokens=2,* skip=2" %%L IN (`reg query "%KEY%" /v CurrentVersion`) DO SET JDKVER=%%M
FOR /F "usebackq tokens=2,* skip=2" %%L IN (`reg query "%KEY%\%JDKVER%" /v JavaHome`) DO SET JDK_HOME="%%M"
SET JDK_HOME=%JDK_HOME:"=%
IF EXIST "%JDK_HOME%" GOTO:start
ECHO JDK not found. Please set %%JDK_HOME%% to the root path of your jdk.
SET /A ERROR=1
GOTO:end

:start
SET SRCDIR=%CD%\java
SET JARDIR=%CD%\..\java\classes
SET JAR="%JDK_HOME%\bin\jar.exe"
SET JSFMANIFEST=%JARDIR%\JSFMANIFEST.MF

echo merge to jScopeFull.jar
COPY /Y %CD%\JSFMANIFEST.MF %JSFMANIFEST% >NUL
PUSHD %JARDIR%
RMDIR /S /Q full 2>nul
MKDIR full 2>NUL
PUSHD full
ECHO unpacking jScope.jar
%JAR% -xf ..\jScope.jar
ECHO unpacking MdsDataProvider.jar
%JAR% -xf ..\MdsDataProvider.jar
ECHO unpacking W7XDataProvider.jar
%JAR% -xf ..\W7XDataProvider.jar
DEL META-INF\MANIFEST.MF
ECHO Built-Date: %Year%-%Month:~-2%-%Day:~-2% %TIME:~0,8%>>%JSFMANIFEST%
POPD

ECHO packing jScopeFull.jar
%JAR% -cmf %JSFMANIFEST% jScopeFull.jar -C full .

ECHO cleaning up
RMDIR /S /Q full 2>nul
DEL %JSFMANIFEST%
POPD
:jscope
ECHO start jScopeFull?
PAUSE
CLS
java -jar -Xmx1G "%JARDIR%\jScopeFull.jar"
:end
PAUSE
EXIT /B ERROR