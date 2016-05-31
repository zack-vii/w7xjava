CLASSPATH = -classpath .;..\java\classes\mdsDataProvider.jar
JAVAC = "$(JDK_DIR)\bin\javac.exe"
JAR = "$(JDK_DIR)\bin\jar.exe"
JARDIR = ..\java\classes

.SUFFIXES: .class .java
.java.class:
	$(JAVAC) $*.java

DEVICE_SRC= \
  device/DeviceApply.java \
  device/DeviceApplyBeanInfo.java \
  device/DeviceButtons.java \
  device/DeviceButtonsBeanInfo.java \
  device/DeviceButtonsCustomizer.java \
  device/DeviceCancel.java \
  device/DeviceCancelBeanInfo.java \
  device/DeviceChannel.java \
  device/DeviceChannelBeanInfo.java \
  device/DeviceChannelCustomizer.java \
  device/DeviceChoice.java \
  device/DeviceChoiceBeanInfo.java \
  device/DeviceChoiceCustomizer.java \
  device/DeviceCloseListener.java \
  device/DeviceComponent.java \
  device/DeviceControl.java \
  device/DeviceCustomizer.java \
  device/DeviceDispatch.java \
  device/DeviceDispatchBeanInfo.java \
  device/DeviceDispatchField.java \
  device/DeviceField.java \
  device/DeviceFieldBeanInfo.java \
  device/DeviceFieldCustomizer.java \
  device/DeviceLabel.java \
  device/DeviceLabelBeanInfo.java \
  device/DeviceLabelCustomizer.java \
  device/DeviceMultiComponent.java \
  device/DeviceOk.java \
  device/DeviceOkBeanInfo.java \
  device/DeviceParameters.java \
  device/DeviceParametersBeanInfo.java \
  device/DeviceReset.java \
  device/DeviceResetBeanInfo.java \
  device/DeviceSetup.java \
  device/DeviceSetupBeanInfo.java \
  device/DeviceTable.java \
  device/DeviceTableBeanInfo.java \
  device/DeviceTableCustomizer.java \
  device/DeviceUpdateListener.java \
  device/DeviceWave.java \
  device/DeviceWaveBeanInfo.java \
  device/DeviceWaveCustomizer.java \
  device/DeviceWaveDisplay.java \
  device/DeviceWaveDisplayBeanInfo.java \
  device/DeviceWaveDisplayCustomizer.java \
  device/DeviceWaveParameters.java \
  device/DeviceWaveParametersBeanInfo.java

JTRAVERSER_SRC= \
  jTraverser/DataChangeEvent.java \
  jTraverser/DataChangeListener.java \
  jTraverser/Node.java \
  jTraverser/NodeInfo.java \
  jTraverser/Tree.java \
  jTraverser/TreeManager.java \
  jTraverser/TreeNode.java \
  jTraverser/dialogs/AddNode.java \
  jTraverser/dialogs/DialogSet.java \
  jTraverser/dialogs/Dialogs.java \
  jTraverser/dialogs/DisplayData.java \
  jTraverser/dialogs/DisplayNci.java \
  jTraverser/dialogs/DisplayTags.java \
  jTraverser/dialogs/Flags.java \
  jTraverser/dialogs/GraphPanel.java \
  jTraverser/dialogs/ModifyData.java \
  jTraverser/dialogs/ModifyTags.java \
  jTraverser/dialogs/Rename.java \
  jTraverser/dialogs/TreeDialog.java \
  jTraverser/dialogs/TreeOpenDialog.java \
  jTraverser/editor/ActionEditor.java \
  jTraverser/editor/ArgEditor.java \
  jTraverser/editor/AxisEditor.java \
  jTraverser/editor/DataEditor.java \
  jTraverser/editor/DispatchEditor.java \
  jTraverser/editor/Editor.java \
  jTraverser/editor/ExprEditor.java \
  jTraverser/editor/LabeledExprEditor.java \
  jTraverser/editor/MethodEditor.java \
  jTraverser/editor/NodeEditor.java \
  jTraverser/editor/ParameterEditor.java \
  jTraverser/editor/ProcedureEditor.java \
  jTraverser/editor/ProgramEditor.java \
  jTraverser/editor/PythonEditor.java \
  jTraverser/editor/RangeEditor.java \
  jTraverser/editor/RoutineEditor.java \
  jTraverser/editor/TaskEditor.java \
  jTraverser/editor/WindowEditor.java \
  jTraverser/jTraverserFacade.java \
  jTraverser.java

TOOLS_SRC= \
  jTraverser/tools/CompileTree.java \
  jTraverser/tools/DecompileTree.java

MDSIP_SRC= \
  mds/data/descriptor/ARRAY.java \
  mds/data/descriptor/DTYPE.java \
  mds/data/descriptor/Descriptor.java \
  mds/data/descriptor/Descriptor_A.java \
  mds/data/descriptor/Descriptor_APD.java \
  mds/data/descriptor/Descriptor_CA.java \
  mds/data/descriptor/Descriptor_D.java \
  mds/data/descriptor/Descriptor_R.java \
  mds/data/descriptor/Descriptor_S.java \
  mds/data/descriptor/Descriptor_XD.java \
  mds/data/descriptor/Descriptor_XS.java \
  mds/data/descriptor_a/COMPLEXArray.java \
  mds/data/descriptor_a/CStringArray.java \
  mds/data/descriptor_a/Complex32Array.java \
  mds/data/descriptor_a/Complex64Array.java \
  mds/data/descriptor_a/EmptyArray.java \
  mds/data/descriptor_a/FLOATArray.java \
  mds/data/descriptor_a/Float32Array.java \
  mds/data/descriptor_a/Float64Array.java \
  mds/data/descriptor_a/Int128Array.java \
  mds/data/descriptor_a/Int16Array.java \
  mds/data/descriptor_a/Int32Array.java \
  mds/data/descriptor_a/Int64Array.java \
  mds/data/descriptor_a/Int8Array.java \
  mds/data/descriptor_a/NUMBERArray.java \
  mds/data/descriptor_a/NidArray.java \
  mds/data/descriptor_a/Uint128Array.java \
  mds/data/descriptor_a/Uint16Array.java \
  mds/data/descriptor_a/Uint32Array.java \
  mds/data/descriptor_a/Uint64Array.java \
  mds/data/descriptor_a/Uint8Array.java \
  mds/data/descriptor_r/Action.java \
  mds/data/descriptor_r/Call.java \
  mds/data/descriptor_r/Condition.java \
  mds/data/descriptor_r/Conglom.java \
  mds/data/descriptor_r/Dependenc.java \
  mds/data/descriptor_r/Dim.java \
  mds/data/descriptor_r/Dispatch.java \
  mds/data/descriptor_r/Function.java \
  mds/data/descriptor_r/Method.java \
  mds/data/descriptor_r/Opaque.java \
  mds/data/descriptor_r/Param.java \
  mds/data/descriptor_r/Procedure.java \
  mds/data/descriptor_r/Program.java \
  mds/data/descriptor_r/Range.java \
  mds/data/descriptor_r/Routine.java \
  mds/data/descriptor_r/Signal.java \
  mds/data/descriptor_r/Slope.java \
  mds/data/descriptor_r/Window.java \
  mds/data/descriptor_r/With_Error.java \
  mds/data/descriptor_r/With_Units.java \
  mds/data/descriptor_s/COMPLEX.java \
  mds/data/descriptor_s/CString.java \
  mds/data/descriptor_s/Complex32.java \
  mds/data/descriptor_s/Complex64.java \
  mds/data/descriptor_s/Event.java \
  mds/data/descriptor_s/FLOAT.java \
  mds/data/descriptor_s/Float32.java \
  mds/data/descriptor_s/Float64.java \
  mds/data/descriptor_s/Ident.java \
  mds/data/descriptor_s/Int128.java \
  mds/data/descriptor_s/Int16.java \
  mds/data/descriptor_s/Int32.java \
  mds/data/descriptor_s/Int64.java \
  mds/data/descriptor_s/Int8.java \
  mds/data/descriptor_s/Missing.java \
  mds/data/descriptor_s/NUMBER.java \
  mds/data/descriptor_s/Nid.java \
  mds/data/descriptor_s/Path.java \
  mds/data/descriptor_s/Uint128.java \
  mds/data/descriptor_s/Uint16.java \
  mds/data/descriptor_s/Uint32.java \
  mds/data/descriptor_s/Uint64.java \
  mds/data/descriptor_s/Uint8.java \
  mds/Database.java \
  mds/MdsException.java \
  mds/mdsip/Connection.java \
  mds/mdsip/ConnectionEvent.java \
  mds/mdsip/ConnectionListener.java \
  mds/mdsip/Message.java \
  mds/mdsip/SshTunneling.java \
  mds/mdsip/UpdateEvent.java \
  mds/mdsip/UpdateEventListener.java

DEVICE_GIFS= \
  device/DeviceApply.gif \
  device/DeviceButtons.gif \
  device/DeviceCancel.gif \
  device/DeviceChannel.gif \
  device/DeviceChoice.gif \
  device/DeviceDispatch.gif \
  device/DeviceField.gif \
  device/DeviceOk.gif \
  device/DeviceReset.gif \
  device/DeviceSetup.gif

TRAV_GIFS= \
  jTraverser/action.gif \
  jTraverser/axis.gif \
  jTraverser/compound.gif \
  jTraverser/device.gif \
  jTraverser/dispatch.gif \
  jTraverser/numeric.gif \
  jTraverser/signal.gif \
  jTraverser/structure.gif \
  jTraverser/subtree.gif \
  jTraverser/task.gif \
  jTraverser/text.gif \
  jTraverser/window.gif

MDSIP_CLS=$(MDSIP_SRC:.java=*.class)
DEVICE_CLS=$(DEVICE_SRC:.java=*.class)
TRAV_CLS=$(TRAV_SRC:.java=*.class)

all : $(MDSIP_SRC) $(DEVICE_SRC) $(TRAV_SRC)
	$(JAVAC) $(CLASSPATH) $(MDSIP_SRC) $(DEVICE_SRC) $(TRAV_SRC)
	$(JAR) -c0f ..\java\classes\MDSIP.jar $(MDSIP_SRC)
	$(JAR) -c0mf DeviceBeans/MANIFEST.mf ..\java\classes\DeviceBeans.jar $(DEVICE_SRC) $(DEVICE_GIFS)
	$(JAR) -c0mf jTraverser/MANIFEST.mf ..\java\classes\jTraverser.jar $(TRAV_SRC) $(TRAV_GIFS)
