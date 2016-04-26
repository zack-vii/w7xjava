@ECHO OFF
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
SET SRCDIR=%CD%
SET JAR="%JDK_HOME%\bin\jar.exe"
SET JARDIR=..\java\classes

echo merge to jScopeFull.jar
PUSHD %JARDIR%
RMDIR /S /Q full 2>nul
MKDIR full 2>NUL
PUSHD full
ECHO unpacking jScope.jar
%JAR% -xf ..\jScope.jar
ECHO unpacking mdsDataProvider.jar
%JAR% -xf ..\mdsDataProvider.jar
ECHO unpacking w7xDataProvider.jar
%JAR% -xf ..\w7xDataProvider.jar
DEL META-INF\MANIFEST.MF
POPD
ECHO packing jScopeFull.jar
%JAR% -cmf %SRCDIR%\MANIFEST.mf jScopeFull.jar -C full .
ECHO cleaning up
RMDIR /S /Q full 2>nul
POPD
:jscope
ECHO start jScopeFull?
PAUSE
CLS
java -jar -Xmx1G "%JARDIR%\jScopeFull.jar"
:end
PAUSE
EXIT /B ERROR