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
SET TOPDOCS=^
  doc\jScope.html ^
  doc\ReadMe.html

SET SUBDOCS=doc\img\jScope.jpg ^
  doc\img\popup.jpg ^
  doc\data_popup.jpg ^
  doc\data_setup.jpg ^
  doc\frame_popup.jpg ^
  doc\image_setup.jpg

SET SUBDOCS=%TOPDOCS% %SUBDOCS%

SET JET_SRC=^
  jet\ji\JiDataSource.java ^
  jet\ji\JiDim.java ^
  jet\ji\JiNcSource.java ^
  jet\ji\JiNcVarByte.java ^
  jet\ji\JiNcVarChar.java ^
  jet\ji\JiNcVarDouble.java ^
  jet\ji\JiNcVarFloat.java ^
  jet\ji\JiNcVarImp.java ^
  jet\ji\JiNcVarInt.java ^
  jet\ji\JiNcVar.java ^
  jet\ji\JiNcVarShort.java ^
  jet\ji\JiSlabIterator.java ^
  jet\ji\JiSlab.java ^
  jet\ji\JiVarImpl.java ^
  jet\ji\JiVar.java ^
  jet\JetDataProvider.java ^
  jet\JetMdsDataProvider.java

SET LOCAL_SRC=^
  local\LocalDataProvider.java ^
  local\LocalDataProviderInfo.java

SET MDS_SRC=^
  mds\Descriptor.java ^
  mds\MdsAccess.java ^
  mds\MdsBrowseSignals.java ^
  mds\MdsConnection.java ^
  mds\MdsDataClient.java ^
  mds\MdsDataProvider.java ^
  mds\MdsIOException.java ^
  mds\MdsMessage.java ^
  mds\MdsParser.java ^
  mds\SshTunneling.java

SET MDSUDT_SRC=^
  mds\udt\MdsConnectionUdt.java ^
  mds\udt\MdsDataProviderUdt.java ^
  mds\udt\MdsIpProtocolWrapper.java

SET MISC_SRC=^
  misc\AsciiDataProvider.java ^
  misc\AsdexDataProvider.java ^
  misc\FtuDataProvider.java ^
  misc\TsDataProvider.java ^
  misc\UniversalDataProvider.java

SET TWU_SRC=^
  twu\TextorBrowseSignals.java ^
  twu\FakeTwuProperties.java ^
  twu\TwuFetchOptions.java ^
  twu\TwuProperties.java ^
  twu\TwuSignal.java ^
  twu\TwuAccess.java ^
  twu\TwuDataProvider.java ^
  twu\TwuNameServices.java ^
  twu\TwuSimpleFrameData.java ^
  twu\TwuSingleSignal.java ^
  twu\TwuWaveData.java

SET W7X_SRC=^
  w7x\W7XDataProvider.java ^
  w7x\W7XBrowseSignals.java ^
  w7x\Signalaccess.java


SET COMMON_SRC=^
  jscope\AboutWindow.java ^
  jscope\Base64.java ^
  jscope\ColorDialog.java ^
  jscope\ColorMapDialog.java ^
  jscope\ColorMap.java ^
  jscope\ConnectionEvent.java ^
  jscope\ConnectionListener.java ^
  jscope\ContourSignal.java ^
  jscope\DataAccess.java ^
  jscope\DataAccessURL.java ^
  jscope\DataCached.java ^
  jscope\DataCacheObject.java ^
  jscope\DataProvider.java ^
  jscope\DataServerItem.java ^
  jscope\FontSelection.java ^
  jscope\FrameData.java ^
  jscope\Frames.java ^
  jscope\Grid.java ^
  jscope\ImageTransferable.java ^
  jscope\jScopeBrowseSignals.java ^
  jscope\jScopeBrowseUrl.java ^
  jscope\jScopeDefaultValues.java ^
  jscope\jScopeFacade.java ^
  jscope\jScopeMultiWave.java ^
  jscope\jScopeProperties.java ^
  jscope\jScopeWaveContainer.java ^
  jscope\jScopeWaveInterface.java ^
  jscope\jScopeWavePopup.java ^
  jscope\MultiWaveform.java ^
  jscope\MultiWavePopup.java ^
  jscope\NotConnectedDataProvider.java ^
  jscope\ProfileDialog.java ^
  jscope\PropertiesEditor.java ^
  jscope\RandomAccessData.java ^
  jscope\RowColumnContainer.java ^
  jscope\SignalListener.java ^
  jscope\RowColumnLayout.java ^
  jscope\SetupDataDialog.java ^
  jscope\SetupDefaults.java ^
  jscope\SetupWaveformParams.java ^
  jscope\SignalBox.java ^
  jscope\AsynchDataSource.java ^
  jscope\Signal.java ^
  jscope\SignalsBoxDialog.java ^
  jscope\UpdateEvent.java ^
  jscope\UpdateEventListener.java ^
  jscope\WaveContainerEvent.java ^
  jscope\WaveContainerListener.java ^
  jscope\WaveData.java ^
  jscope\WaveformContainer.java ^
  jscope\WaveformEditor.java ^
  jscope\WaveformEditorListener.java ^
  jscope\WaveformEvent.java ^
  jscope\Waveform.java ^
  jscope\WaveformListener.java ^
  jscope\WaveformManager.java ^
  jscope\WaveformMetrics.java ^
  jscope\WaveInterface.java ^
  jscope\WavePopup.java ^
  jscope\XYData.java ^
  jscope\XYWaveData.java ^
  jscope\WaveDataListener.java

SET WAVEDISPLAY_SRC=^
  jscope\tools\WaveDisplay.java ^
  jscope\tools\CompositeWaveDisplay.java

SET JSCOPE_RES=^
  jscope\AboutWindow.jpg ^
  jscope\colors.tbl

SET JSCOPE_SRC=^
  jScope.java ^
  %JET_SRC% ^
  %LOCAL_SRC% ^
  %MDS_SRC% ^
  %MDSUDT_SRC% ^
  %MISC_SRC% ^
  %TWU_SRC% ^
  %W7X_SRC%

SET EXTRA_CLASS=^
jscope\FakeTwuProperties.class ^
jscope\FontPanel.class ^
jscope\ServerDialog*.class ^
jscope\WindowDialog.class

SET CLASSPATH=-classpath ".;mds\MindTerm.jar;w7x\swingx.jar;w7x\W7XDataProvider.jar"
SET JAVAC="%JDK_HOME%\bin\javac.exe"
SET JCFLAGS=-O -source 1.6 -target 1.6 -g:none||rem-Xlint -deprecation
SET SRCDIR=%CD%
SET JAR="%JDK_HOME%\bin\jar.exe"
SET JSMANIFEST=jscope\MANIFEST.mf
SET JARDIR=..\java\classes

ECHO compiling *.java to *.class . . .
%JAVAC% %JCFLAGS% -d %JARDIR% %CLASSPATH% %COMMON_SRC% %JSCOPE_SRC% %WAVEDISPLAY_SRC%
SET /A ERROR=%ERRORLEVEL%
IF %ERROR% NEQ 0 GOTO:cleanup

:gather
ECHO gathering data
COPY /Y jScope.properties %JARDIR%\>NUL
MKDIR  %JARDIR%\docs 2>NUL
MKDIR  %JARDIR%\jars 2>NUL
FOR %%F IN (%DOCS%) DO COPY /Y %%F /D %JARDIR%\docs>NUL
FOR %%F IN (%JSCOPE_RES%) DO COPY /Y %%F /D %JARDIR%\jscope>NUL
COPY %CD%\Mds\MindTerm.jar %JARDIR%\MdsDataProvider.jar>NUL
rem COPY %CD%\w7x\swingx.jar %JARDIR%>NUL || rem included in W7XDataProvider
COPY %CD%\w7x\W7XDataProvider.jar %JARDIR%>NUL
COPY /Y %JSMANIFEST% %JARDIR%\%JSMANIFEST% >NUL
COPY /Y %JSMANIFEST% %JARDIR%\%JSMANIFEST% >NUL
ECHO Built-Date: %DATE:~10,4%-%DATE:~4,2%-%DATE:~7,2% %TIME:~0,8%>>%JARDIR%\%JSMANIFEST%

:packjar
ECHO creating jar packages
PUSHD %JARDIR%
%JAR% -cmf %JSMANIFEST% "jScope.jar" jScope.class jScope.properties jscope docs
%JAR% -cmf %SRCDIR%\jet\MANIFEST.mf "JetDataProvider.jar" jet
%JAR% -cmf %SRCDIR%\local\MANIFEST.mf "LocalDataProvider.jar" local
%JAR% -umf %SRCDIR%\mds\MANIFEST.mf "MdsDataProvider.jar" mds
%JAR% -cmf %SRCDIR%\misc\MANIFEST.mf "MiscDataProvider.jar" misc
%JAR% -cmf %SRCDIR%\twu\MANIFEST.mf "TwuDataProvider.jar" twu
%JAR% -umf %SRCDIR%\w7x\MANIFEST.mf "W7XDataProvider.jar" w7x
%JAR% -cf "WaveDisplay.jar" %COMMON_SRC:.java=.class% %WAVEDISPLAY_SRC:.java=.class%
POPD

:cleanup
ECHO cleaning up
PUSHD %JARDIR%
RMDIR /S /Q docs 2>NUL
DEL colors.tbl jScope.properties jScope.class 2>NUL
RMDIR /S /Q jars 2>nul
RMDIR /S /Q jet 2>nul
RMDIR /S /Q jscope 2>nul
RMDIR /S /Q local 2>nul
RMDIR /S /Q mds 2>nul
RMDIR /S /Q misc 2>nul
RMDIR /S /Q twu 2>nul
RMDIR /S /Q w7x 2>nul

POPD

:jscope
IF %ERROR% NEQ 0 GOTO:end
ECHO start jScope?
PAUSE
CLS
java -jar -Xmx1G "%JARDIR%\jScope.jar"

:end
PAUSE
EXIT /B ERROR