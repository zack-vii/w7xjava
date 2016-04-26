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
  jTraverser.java \
  jTraverser/CompileTree.java \
  jTraverser/Convert.java \
  jTraverser/DataChangeEvent.java \
  jTraverser/DataChangeListener.java \
  jTraverser/DecompileTree.java \
  jTraverser/DisplayData.java \
  jTraverser/DisplayNci.java \
  jTraverser/DisplayTags.java \
  jTraverser/FrameRepository.java \
  jTraverser/jTraverserFacade.java \
  jTraverser/LoadFile.java \
  jTraverser/LoadPulse.java \
  jTraverser/ModifyData.java \
  jTraverser/Node.java \
  jTraverser/NodeBeanInfo.java \
  jTraverser/NodeDisplayData.java \
  jTraverser/NodeDisplayNci.java \
  jTraverser/NodeDisplayTags.java \
  jTraverser/NodeInfo.java \
  jTraverser/NodeModifyData.java \
  jTraverser/RemoteTree.java \
  jTraverser/StoreFile.java \
  jTraverser/SyntaxException.java \
  jTraverser/Tree.java \
  jTraverser/TreeDialog.java \
  jTraverser/TreeNode.java \
  jTraverser/TreeServer.java \
  jTraverser/editor/ActionEditor.java \
  jTraverser/editor/ArgEditor.java \
  jTraverser/editor/AxisEditor.java \
  jTraverser/editor/DataEditor.java \
  jTraverser/editor/DispatchEditor.java \
  jTraverser/editor/Editor.java \
  jTraverser/editor/ExprEditor.java \
  jTraverser/editor/FloatArrayEditor.java \
  jTraverser/editor/IntArrayEditor.java \
  jTraverser/editor/LabeledExprEditor.java \
  jTraverser/editor/MethodEditor.java \
  jTraverser/editor/NodeDataPropertyEditor.java \
  jTraverser/editor/NodeEditor.java \
  jTraverser/editor/NodeInfoPropertyEditor.java \
  jTraverser/editor/ParameterEditor.java \
  jTraverser/editor/ProcedureEditor.java \
  jTraverser/editor/ProgramEditor.java \
  jTraverser/editor/PythonEditor.java \
  jTraverser/editor/RangeEditor.java \
  jTraverser/editor/RoutineEditor.java \
  jTraverser/editor/TaskEditor.java \
  jTraverser/editor/WindowEditor.java

DATA_SRC= \
  mds/Database.java \
  mds/DatabaseException.java \
  mds/Data/ActionData.java \
  mds/Data/ApdData.java \
  mds/Data/AtomicData.java \
  mds/Data/ByteData.java \
  mds/Data/CallData.java \
  mds/Data/ComplexData.java \
  mds/Data/CompoundData.java \
  mds/Data/ConditionData.java \
  mds/Data/ConglomData.java \
  mds/Data/Data.java \
  mds/Data/DataId.java \
  mds/Data/DataListener.java \
  mds/Data/DependencyData.java \
  mds/Data/DimensionData.java \
  mds/Data/DispatchData.java \
  mds/Data/DoubleData.java \
  mds/Data/EventData.java \
  mds/Data/FloatData.java \
  mds/Data/FunctionData.java \
  mds/Data/IdentData.java \
  mds/Data/IllegalDataException.java \
  mds/Data/IntData.java \
  mds/Data/MethodData.java \
  mds/Data/NidData.java \
  mds/Data/NodeId.java \
  mds/Data/OctaData.java \
  mds/Data/ParameterData.java \
  mds/Data/PathData.java \
  mds/Data/ProcedureData.java \
  mds/Data/ProgramData.java \
  mds/Data/QuadData.java \
  mds/Data/RangeData.java \
  mds/Data/RoutineData.java \
  mds/Data/ShortData.java \
  mds/Data/SignalData.java \
  mds/Data/SlopeData.java \
  mds/Data/StringData.java \
  mds/Data/UnsupportedDataException.java \
  mds/Data/WindowData.java \
  mds/Data/WithErrorData.java \
  mds/Data/WithUnitsData.java \
  mds/Data/ArrayData/ArrayData.java \
  mds/Data/ArrayData/ByteArray.java \
  mds/Data/ArrayData/DoubleArray.java \
  mds/Data/ArrayData/FloatArray.java \
  mds/Data/ArrayData/IntArray.java \
  mds/Data/ArrayData/OctaArray.java \
  mds/Data/ArrayData/QuadArray.java \
  mds/Data/ArrayData/ShortArray.java \
  mds/Data/ArrayData/StringArray.java

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

DATA_CLS=$(DATA_SRC:.java=*.class)
DEVICE_CLS=$(DEVICE_SRC:.java=*.class)
TRAV_CLS=$(TRAV_SRC:.java=*.class)

all : $(DATA_SRC) $(DEVICE_SRC) $(TRAV_SRC)
	$(JAVAC) $(CLASSPATH) $(DATA_SRC) $(DEVICE_SRC) $(TRAV_SRC)
	$(JAR) -c0f ..\java\classes\mdsData.jar $(DATA_SRC)
	$(JAR) -c0mf DeviceBeans/MANIFEST.mf ..\java\classes\DeviceBeans.jar $(DEVICE_SRC) $(DEVICE_GIFS)
	$(JAR) -c0mf jTraverser/MANIFEST.mf ..\java\classes\jTraverser.jar $(TRAV_SRC) $(TRAV_GIFS)
