@ECHO OFF
FOR /f %%i IN ('CD') DO SET root=%%i
PUSHD %TMP%
IF NOT EXIST %TMP%\password.bat GOTO:nopw
CALL %TMP%\password.bat
GOTO:pw
:nopw
SET /P pw=password? 
ECHO SET pw=%pw%>%TMP%\password.bat
:pw
SET JARFILEMASK=signalaccessFull*.jar
SET jhdir=E:\OneDrive\MDSplus\java\java_helper
SET saFull=%jhdir%\signalaccessFull.jar
wget -q --no-check-certificate --user=cloud --password=%pw% "https://nexus.ipp-hgw.mpg.de/service/local/artifact/maven/redirect?r=releases&g=de.mpg.ipp.codac&a=signalaccessFull&v=LATEST&e=jar&c=ueberjar"
FOR /F %%F IN ('DIR /ON /B %TMP%\%JARFILEMASK%') DO SET JARFILE=%TMP%\%%F
for %%a in (%JARFILE%) do (
ECHO %%~nxa
ECHO %%~nxa>%root%/signalaccessFull.txt
)
7z d %JARFILE% jars deps>NUL
COPY /Y %JARFILE% %root%\w7x\W7XDataProvider.jar>NUL
IF NOT EXIST %TMP%\swingx (
7z x %root%\w7x\swingx.jar -o%TMP%\swingx org>NUL
)
7z a %root%/w7x/W7XDataProvider.jar -r %TMP%\swingx\org>NUL
7z a %JARFILE% %jhdir%\*.class>NUL
MOVE /Y %JARFILE% %saFull%>NUL
POPD
