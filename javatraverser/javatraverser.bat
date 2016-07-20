@ECHO OFF
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
  devicebeans\FloatArrayEditor.java ^
  devicebeans\IntArrayEditor.java ^
  devicebeans\NodeDataPropertyEditor.java ^
  devicebeans\NodeInfoPropertyEditor.java ^
  devicebeans\tools\LoadFile.java ^
  devicebeans\tools\LoadPulse.java ^
  devicebeans\tools\StoreFile.java

SET DEVWAVE_SCR=^
  devicebeans\devicewave\DeviceWave.java ^
  devicebeans\devicewave\DeviceWaveBeanInfo.java ^
  devicebeans\devicewave\DeviceWaveCustomizer.java ^
  devicebeans\devicewave\DeviceWaveDisplay.java ^
  devicebeans\devicewave\DeviceWaveDisplayBeanInfo.java ^
  devicebeans\devicewave\DeviceWaveDisplayCustomizer.java ^
  devicebeans\devicewave\DeviceWaveParameters.java ^
  devicebeans\devicewave\DeviceWaveParametersBeanInfo.java

SET TRAV_SRC=^
  jtraverser\DataChangeEvent.java ^
  jtraverser\DataChangeListener.java ^
  jtraverser\Node.java ^
  jtraverser\Tree.java ^
  jtraverser\TreeManager.java ^
  jtraverser\TreeNodeLabel.java ^
  jtraverser\dialogs\AddNode.java ^
  jtraverser\dialogs\DialogSet.java ^
  jtraverser\dialogs\Dialogs.java ^
  jtraverser\dialogs\DisplayData.java ^
  jtraverser\dialogs\DisplayNci.java ^
  jtraverser\dialogs\DisplayTags.java ^
  jtraverser\dialogs\Flags.java ^
  jtraverser\dialogs\GraphPanel.java ^
  jtraverser\dialogs\ModifyData.java ^
  jtraverser\dialogs\ModifyTags.java ^
  jtraverser\dialogs\Rename.java ^
  jtraverser\dialogs\SubTrees.java ^
  jtraverser\dialogs\TreeDialog.java ^
  jtraverser\dialogs\TreeOpenDialog.java ^
  jtraverser\editor\ActionEditor.java ^
  jtraverser\editor\ArgEditor.java ^
  jtraverser\editor\ArrayEditor.java ^
  jtraverser\editor\AxisEditor.java ^
  jtraverser\editor\DataEditor.java ^
  jtraverser\editor\DispatchEditor.java ^
  jtraverser\editor\Editor.java ^
  jtraverser\editor\ExprEditor.java ^
  jtraverser\editor\LabeledExprEditor.java ^
  jtraverser\editor\MethodEditor.java ^
  jtraverser\editor\NodeEditor.java ^
  jtraverser\editor\ParameterEditor.java ^
  jtraverser\editor\ProcedureEditor.java ^
  jtraverser\editor\ProgramEditor.java ^
  jtraverser\editor\PythonEditor.java ^
  jtraverser\editor\RangeEditor.java ^
  jtraverser\editor\RoutineEditor.java ^
  jtraverser\editor\SignalEditor.java ^
  jtraverser\editor\TaskEditor.java ^
  jtraverser\editor\WindowEditor.java ^
  jtraverser\jTraverserFacade.java ^
  jTraverser.java

SET TOOLS_SRC=^
  jtraverser\tools\CompileTree.java ^
  jtraverser\tools\DecompileTree.java

SET LOCAL_SRC=^
  local\localDatabase.java

SET MDSIP_SRC=^
  mds\Database.java ^
  mds\MdsException.java ^
  mds\MdsShr.java ^
  mds\TdiShr.java ^
  mds\TreeShr.java ^
  mds\data\descriptor\ARRAY.java ^
  mds\data\descriptor\DTYPE.java ^
  mds\data\descriptor\OPC.java ^
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
  mds\data\descriptor_r\BUILD.java ^
  mds\data\descriptor_r\Action.java ^
  mds\data\descriptor_r\Call.java ^
  mds\data\descriptor_r\Condition.java ^
  mds\data\descriptor_r\Conglom.java ^
  mds\data\descriptor_r\Dependency.java ^
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
  mds\data\descriptor_s\Pointer.java ^
  mds\data\descriptor_s\TREENODE.java ^
  mds\data\descriptor_s\Uint128.java ^
  mds\data\descriptor_s\Uint16.java ^
  mds\data\descriptor_s\Uint32.java ^
  mds\data\descriptor_s\Uint64.java ^
  mds\data\descriptor_s\Uint8.java ^
  mds\mdsip\Connection.java ^
  mds\mdsip\ConnectionEvent.java ^
  mds\mdsip\ConnectionListener.java ^
  mds\mdsip\Message.java ^
  mds\mdsip\UpdateEvent.java ^
  mds\mdsip\UpdateEventListener.java

SET DEVICE_GIF=^
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

SET DEVWAVE_GIF=^
  devicebeans\devicewave\DeviceWave.gif
  

SET TRAV_GIF=^
  jtraverser\action.gif ^
  jtraverser\any.gif ^
  jtraverser\axis.gif ^
  jtraverser\compound.gif ^
  jtraverser\device.gif ^
  jtraverser\dispatch.gif ^
  jtraverser\numeric.gif ^
  jtraverser\signal.gif ^
  jtraverser\structure.gif ^
  jtraverser\subtree.gif ^
  jtraverser\task.gif ^
  jtraverser\text.gif ^
  jtraverser\window.gif

SET JARDIR=%CD%\..\java\classes
SET SRCDIR=%CD%\java
SET CLASSPATH=-classpath "." ||rem ;%JARDIR%\jScope.jar"
SET JAVAC="%JDK_HOME%\bin\javac.exe" ||rem -Xlint -deprecation
SET JCFLAGS= -O -source 1.6 -target 1.6 -g:none||rem -Xlint -deprecation
SET JAR="%JDK_HOME%\bin\jar.exe"
SET DBMANIFEST=%CD%\DBMANIFEST.mf
SET JTMANIFEST=%JARDIR%\JTMANIFEST.mf
MKDIR %JARDIR% 2>NUL
SET DEVICE_CLS=%DEVICE_SRC:.java=*.class%
SET TRAV_CLS=%TRAV_SRC:.java=*.class%
SET TOOLS_CLS=%TOOLS_SRC:.java=*.class%
SET MDSIP_CLS=%MDSIP_SRC:.java=*.class%
SET LOCAL_CLS=%LOCAL_SRC:.java=*.class%
SET DEVWAV_CLS=%DEVWAV_SRC:.java=*.class%

ECHO compiling *.java to *.class . . .
PUSHD %SRCDIR%
%JAVAC% %JCFLAGS% -d %JARDIR% %CLASSPATH% %MDSIP_SRC% %TRAV_SRC% %TOOLS_SRC% %DEVICE_SRC% debug\DEBUG.java ||rem %LOCAL_SRC% %DEVWAV_SRC%
SET /A ERROR=%ERRORLEVEL%
POPD
IF %ERROR% NEQ 0 GOTO:cleanup

ECHO gathering data
MKDIR %JARDIR%\devicebeans 2>NUL
MKDIR %JARDIR%\jTraverser 2>NUL
COPY /Y %SRCDIR%\devicebeans\*.gif %JARDIR%\devicebeans>NUL
rem COPY /Y %SRCDIR%\devicebeans\devicewave\*.gif %JARDIR%\devicebeans\devicewave>NUL
COPY /Y %SRCDIR%\jtraverser\*.gif  %JARDIR%\jtraverser>NUL
COPY /Y %CD%\JTMANIFEST.mf %JTMANIFEST% >NUL
ECHO Built-Date: %Year%-%Month:~-2%-%Day:~-2% %TIME:~0,8%>>%JTMANIFEST%

ECHO creating jar packages
PUSHD %JARDIR%
%JAR% -cmf %DBMANIFEST% devicebeans.jar %DEVICE_CLS% %DEVICE_GIF%
%JAR% -cmf %JTMANIFEST% jTraverser.jar %TRAV_CLS% %TRAV_GIF% %MDSIP_CLS% %TOOLS_CLS% %DEVICE_CLS% %DEVICE_GIF% ||rem %DEVWAV_CLS% %DEVWAV_GIF%
rem %JAR% -cf localDatabase.jar %LOCAL_CLS%
%JAR% -cf MDSIP.jar %MDSIP_CLS%
rem %JAR% -cf jTraverserTools.jar %TOOLS_CLS%
POPD

:cleanup
ECHO cleaning up
PUSHD %JARDIR%
rem DEL /Q local\* 2>NUL
DEL /Q mds\data\array\* 2>NUL
DEL /Q mds\data\* 2>NUL
DEL /Q mds\* 2>NUL
DEL /Q devicebeans\* 2>NUL
DEL /Q jtraverser\Dialogs\* 2>NUL
DEL /Q jtraverser\Editor* 2>NUL
DEL /Q jtraverser\Tools* 2>NUL
DEL /Q jtraverser\* 2>NUL
DEL /Q jTraverser.class 2>NUL
DEL /Q debug\* 2>NUL
rem RMDIR /Q/S local
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