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
SET DEVICE_SRC=^
  devicebeans\DeviceApply.java ^
  devicebeans\DeviceApplyBeanInfo.java ^
  devicebeans\DeviceButtons.java ^
  devicebeans\DeviceButtonsBeanInfo.java ^
  devicebeans\DeviceButtonsCustomizer.java ^
  devicebeans\DeviceCancel.java ^
  devicebeans\DeviceCancelBeanInfo.java ^
  devicebeans\DeviceChannel.java ^
  devicebeans\DeviceChannelBeanInfo.java ^
  devicebeans\DeviceChannelCustomizer.java ^
  devicebeans\DeviceChoice.java ^
  devicebeans\DeviceChoiceBeanInfo.java ^
  devicebeans\DeviceChoiceCustomizer.java ^
  devicebeans\DeviceCloseListener.java ^
  devicebeans\DeviceComponent.java ^
  devicebeans\DeviceControl.java ^
  devicebeans\DeviceCustomizer.java ^
  devicebeans\DeviceDispatch.java ^
  devicebeans\DeviceDispatchBeanInfo.java ^
  devicebeans\DeviceDispatchField.java ^
  devicebeans\DeviceField.java ^
  devicebeans\DeviceFieldBeanInfo.java ^
  devicebeans\DeviceFieldCustomizer.java ^
  devicebeans\DeviceLabel.java ^
  devicebeans\DeviceLabelBeanInfo.java ^
  devicebeans\DeviceLabelCustomizer.java ^
  devicebeans\DeviceMultiComponent.java ^
  devicebeans\DeviceOk.java ^
  devicebeans\DeviceOkBeanInfo.java ^
  devicebeans\DeviceParameters.java ^
  devicebeans\DeviceParametersBeanInfo.java ^
  devicebeans\DeviceReset.java ^
  devicebeans\DeviceResetBeanInfo.java ^
  devicebeans\DeviceSetup.java ^
  devicebeans\DeviceSetupBeanInfo.java ^
  devicebeans\DeviceTable.java ^
  devicebeans\DeviceTableBeanInfo.java ^
  devicebeans\DeviceTableCustomizer.java ^
  devicebeans\DeviceUpdateListener.java ^
  devicebeans\DeviceWave.java ^
  devicebeans\DeviceWaveBeanInfo.java ^
  devicebeans\DeviceWaveCustomizer.java ^
  devicebeans\DeviceWaveDisplay.java ^
  devicebeans\DeviceWaveDisplayBeanInfo.java ^
  devicebeans\DeviceWaveDisplayCustomizer.java ^
  devicebeans\DeviceWaveParameters.java ^
  devicebeans\DeviceWaveParametersBeanInfo.java ^
  devicebeans\FloatArrayEditor.java ^
  devicebeans\IntArrayEditor.java ^
  devicebeans\NodeDataPropertyEditor.java ^
  devicebeans\NodeInfoPropertyEditor.java ^
  devicebeans\tools\LoadFile.java ^
  devicebeans\tools\LoadPulse.java ^
  devicebeans\tools\StoreFile.java

SET TRAV_SRC=^
  jTraverser\DataChangeEvent.java ^
  jTraverser\DataChangeListener.java ^
  jTraverser\Database.java ^
  jTraverser\Node.java ^
  jTraverser\NodeInfo.java ^
  jTraverser\Tree.java ^
  jTraverser\TreeManager.java ^
  jTraverser\TreeNode.java ^
  jTraverser\dialogs\AddNode.java ^
  jTraverser\dialogs\DialogSet.java ^
  jTraverser\dialogs\Dialogs.java ^
  jTraverser\dialogs\DisplayData.java ^
  jTraverser\dialogs\DisplayNci.java ^
  jTraverser\dialogs\DisplayTags.java ^
  jTraverser\dialogs\Flags.java ^
  jTraverser\dialogs\ModifyData.java ^
  jTraverser\dialogs\ModifyTags.java ^
  jTraverser\dialogs\Rename.java ^
  jTraverser\dialogs\TreeDialog.java ^
  jTraverser\dialogs\TreeOpenDialog.java ^
  jTraverser\editor\ActionEditor.java ^
  jTraverser\editor\ArgEditor.java ^
  jTraverser\editor\AxisEditor.java ^
  jTraverser\editor\DataEditor.java ^
  jTraverser\editor\DispatchEditor.java ^
  jTraverser\editor\Editor.java ^
  jTraverser\editor\ExprEditor.java ^
  jTraverser\editor\LabeledExprEditor.java ^
  jTraverser\editor\MethodEditor.java ^
  jTraverser\editor\NodeEditor.java ^
  jTraverser\editor\ParameterEditor.java ^
  jTraverser\editor\ProcedureEditor.java ^
  jTraverser\editor\ProgramEditor.java ^
  jTraverser\editor\PythonEditor.java ^
  jTraverser\editor\RangeEditor.java ^
  jTraverser\editor\RoutineEditor.java ^
  jTraverser\editor\TaskEditor.java ^
  jTraverser\editor\WindowEditor.java ^
  jTraverser\jTraverserFacade.java ^
  jTraverser.java

SET TOOLS_SRC=^
  jTraverser\tools\CompileTree.java ^
  jTraverser\tools\DecompileTree.java

SET LOCAL_SRC=^
  local\localDatabase.java

SET MDSIP_SRC=^
  mds\data\descriptor\DTYPE.java ^
  mds\data\descriptor\Descriptor.java ^
  mds\data\descriptor\Descriptor_A.java ^
  mds\data\descriptor\Descriptor_APD.java ^
  mds\data\descriptor\Descriptor_CA.java ^
  mds\data\descriptor\Descriptor_D.java ^
  mds\data\descriptor\Descriptor_R.java ^
  mds\data\descriptor\Descriptor_S.java ^
  mds\data\descriptor\Descriptor_XD.java ^
  mds\data\descriptor\Descriptor_XS.java ^
  mds\data\descriptor_a\COMPLEXArray.java ^
  mds\data\descriptor_a\CStringArray.java ^
  mds\data\descriptor_a\Complex32Array.java ^
  mds\data\descriptor_a\Complex64Array.java ^
  mds\data\descriptor_a\EmptyArray.java ^
  mds\data\descriptor_a\FLOATArray.java ^
  mds\data\descriptor_a\Float32Array.java ^
  mds\data\descriptor_a\Float64Array.java ^
  mds\data\descriptor_a\Int128Array.java ^
  mds\data\descriptor_a\Int16Array.java ^
  mds\data\descriptor_a\Int32Array.java ^
  mds\data\descriptor_a\Int64Array.java ^
  mds\data\descriptor_a\Int8Array.java ^
  mds\data\descriptor_a\NUMBERArray.java ^
  mds\data\descriptor_a\NidArray.java ^
  mds\data\descriptor_a\Uint128Array.java ^
  mds\data\descriptor_a\Uint16Array.java ^
  mds\data\descriptor_a\Uint32Array.java ^
  mds\data\descriptor_a\Uint64Array.java ^
  mds\data\descriptor_a\Uint8Array.java ^
  mds\data\descriptor_r\Action.java ^
  mds\data\descriptor_r\Call.java ^
  mds\data\descriptor_r\Condition.java ^
  mds\data\descriptor_r\Conglom.java ^
  mds\data\descriptor_r\Dependenc.java ^
  mds\data\descriptor_r\Dim.java ^
  mds\data\descriptor_r\Dispatch.java ^
  mds\data\descriptor_r\Function.java ^
  mds\data\descriptor_r\Method.java ^
  mds\data\descriptor_r\Opaque.java ^
  mds\data\descriptor_r\Param.java ^
  mds\data\descriptor_r\Procedure.java ^
  mds\data\descriptor_r\Program.java ^
  mds\data\descriptor_r\Range.java ^
  mds\data\descriptor_r\Routine.java ^
  mds\data\descriptor_r\Signal.java ^
  mds\data\descriptor_r\Slope.java ^
  mds\data\descriptor_r\Window.java ^
  mds\data\descriptor_r\With_Error.java ^
  mds\data\descriptor_r\With_Units.java ^
  mds\data\descriptor_s\COMPLEX.java ^
  mds\data\descriptor_s\CString.java ^
  mds\data\descriptor_s\Complex32.java ^
  mds\data\descriptor_s\Complex64.java ^
  mds\data\descriptor_s\Event.java ^
  mds\data\descriptor_s\FLOAT.java ^
  mds\data\descriptor_s\Float32.java ^
  mds\data\descriptor_s\Float64.java ^
  mds\data\descriptor_s\Ident.java ^
  mds\data\descriptor_s\Int128.java ^
  mds\data\descriptor_s\Int16.java ^
  mds\data\descriptor_s\Int32.java ^
  mds\data\descriptor_s\Int64.java ^
  mds\data\descriptor_s\Int8.java ^
  mds\data\descriptor_s\Missing.java ^
  mds\data\descriptor_s\NUMBER.java ^
  mds\data\descriptor_s\Nid.java ^
  mds\data\descriptor_s\Path.java ^
  mds\data\descriptor_s\Uint128.java ^
  mds\data\descriptor_s\Uint16.java ^
  mds\data\descriptor_s\Uint32.java ^
  mds\data\descriptor_s\Uint64.java ^
  mds\data\descriptor_s\Uint8.java ^
  mds\MdsException.java ^
  mds\mdsip\Connection.java ^
  mds\mdsip\ConnectionEvent.java ^
  mds\mdsip\ConnectionListener.java ^
  mds\mdsip\Message.java ^
  mds\mdsip\SshTunneling.java ^
  mds\mdsip\UpdateEvent.java ^
  mds\mdsip\UpdateEventListener.java

SET DEVICE_GIFS=^
  devicebeans\DeviceApply.gif ^
  devicebeans\DeviceButtons.gif ^
  devicebeans\DeviceCancel.gif ^
  devicebeans\DeviceChannel.gif ^
  devicebeans\DeviceChoice.gif ^
  devicebeans\DeviceDispatch.gif ^
  devicebeans\DeviceField.gif ^
  devicebeans\DeviceOk.gif ^
  devicebeans\DeviceReset.gif ^
  devicebeans\DeviceSetup.gif

SET TRAV_GIFS=^
  jTraverser\action.gif ^
  jTraverser\any.gif ^
  jTraverser\axis.gif ^
  jTraverser\compound.gif ^
  jTraverser\device.gif ^
  jTraverser\dispatch.gif ^
  jTraverser\numeric.gif ^
  jTraverser\signal.gif ^
  jTraverser\structure.gif ^
  jTraverser\subtree.gif ^
  jTraverser\task.gif ^
  jTraverser\text.gif ^
  jTraverser\window.gif

SET CLASSPATH=-classpath ".;.\mds\mdsip\MindTerm.jar;..\java\classes\jScope.jar"
SET JAVAC="%JDK_HOME%\bin\javac.exe" ||rem -Xlint -deprecation
SET JCFLAGS= -O -source 1.6 -target 1.6 -g:none||rem -Xlint -deprecation
SET JAR="%JDK_HOME%\bin\jar.exe"
SET DBMANIFEST=%CD%\devicebeans\MANIFEST.mf
SET JTMANIFEST=%CD%\jTraverser\MANIFEST.mf 
SET JARDIR=..\java\classes
MKDIR %JARDIR% 2>NUL
SET DEVICE_CLS=%DEVICE_SRC:.java=*.class%
SET TRAV_CLS=%TRAV_SRC:.java=*.class%
SET TOOLS_CLS=%TOOLS_SRC:.java=*.class%
SET MDSIP_CLS=%MDSIP_SRC:.java=*.class%
SET LOCAL_CLS=%LOCAL_SRC:.java=*.class%

ECHO compiling *.java to *.class . . .
%JAVAC% %JCFLAGS% -d %JARDIR% %CLASSPATH% %MDSIP_SRC% %TRAV_SRC% %TOOLS_SRC% %DEVICE_SRC% debug\DEBUG.java ||rem %LOCAL_SRC%
SET /A ERROR=%ERRORLEVEL%
IF %ERROR% NEQ 0 GOTO:cleanup

ECHO gathering data
MKDIR %JARDIR%\devicebeans 2>NUL
MKDIR %JARDIR%\jTraverser 2>NUL
COPY /Y devicebeans\*.gif %JARDIR%\devicebeans >NUL
COPY /Y jTraverser\*.gif %JARDIR%\jTraverser >NUL

ECHO creating jar packages
PUSHD %JARDIR%
%JAR% -cmf %DBMANIFEST% devicebeans.jar %DEVICE_CLS% %DEVICE_GIFS%
%JAR% -cmf %JTMANIFEST% jTraverser.jar %TRAV_CLS% %TRAV_GIFS% %MDSIP_CLS% %TOOLS_SRC% %DEVICE_CLS% %DEVICE_GIFS%
rem %JAR% -cf localDatabase.jar %LOCAL_CLS%
%JAR% -cf MDSIP.jar %MDSIP_CLS%
rem %JAR% -cf jTraverserTools.jar %TOOLS_CLS%
POPD

:cleanup
ECHO cleaning up
PUSHD %JARDIR%
DEL /Q local\* 2>NUL
DEL /Q mds\data\array\* 2>NUL
DEL /Q mds\data\* 2>NUL
DEL /Q mds\* 2>NUL
DEL /Q devicebeans\* 2>NUL
DEL /Q jTraverser\Dialogs\* 2>NUL
DEL /Q jTraverser\Editor* 2>NUL
DEL /Q jTraverser\Tools* 2>NUL
DEL /Q jTraverser\* 2>NUL
DEL /Q jTraverser.class 2>NUL
DEL /Q debug\* 2>NUL
RMDIR /Q/S local
RMDIR /Q/S mds 
RMDIR /Q/S jTraverser
RMDIR /Q/S devicebeans
RMDIR /Q/S debug
POPD

:jtraveser
IF %ERROR% NEQ 0 GOTO:end
ECHO start jTraverser?
PAUSE
CLS
java -jar "%JARDIR%\jTraverser.jar"

:end
PAUSE
EXIT /B ERROR