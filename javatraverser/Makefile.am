EXTRA_DIST = servlet-2_3-fcs-classfiles.zip
CLEANFILES = $(java_DATA)

JAVAROOT = $(builddir)
javadir = $(exec_prefix)/java/classes
docsdir = $(exec_prefix)/java/classes/docs

# Create the shell script that runs the program
if MINGW
bin_SCRIPTS = traverser.bat
else
bin_SCRIPTS = jTraverser
CLEANFILES += $(bin_SCRIPTS)
EXTRA_DIST += jTraverser.template
$(bin_SCRIPTS): $(srcdir)/jTraverser.template
	cp $< $@
endif

#$(INSTALL) -D $< $@
# Build the class files
CLASSPATH_ENV = CLASSPATH=$(top_builddir)/javascope/mdsDataProvider.jar
DATA_CLS=$(DATA_SRC:.java=*.class)
DEVICE_CLS=$(DEVICE_SRC:.java=*.class)
TRAV_CLS=$(TRAV_SRC:.java=*.class)
TRAV_GIFS = $(addprefix ${srcdir}/,$(_TRAV_GIFS))
DEVICE_GIFS = $(addprefix ${srcdir}/,$(_DEVICE_GIFS))

# Build the final jar
java_DATA = jTraverser.jar DeviceBeans.jar mdsData.jar

jTraverser.jar: $(TRAV_CLS) $(TRAV_GIFS) classjava.stamp
	$(JAR) c0f $@ $(java_JAVA:.java=*.class) $< $(GIFS)

DeviceBeans.jar: classjava.stamp $(DEVICE_CLS) $(DEVICE_GIFS) DeviceBeans/MANIFEST.mf
	$(JAR) c0fm $@ ${srcdir}/DeviceBeans/MANIFEST.mf $(DEVICE_CLS) $(DEVICE_GIFS)

mdsData.jar: classjava.stamp $(DATA_CLS)
	$(JAR) c0f $@ $(DATA_CLS)

# Giant list of source files follow

DEVICE_SRC= \
  DeviceBeans/DeviceApply.java \
  DeviceBeans/DeviceApplyBeanInfo.java \
  DeviceBeans/DeviceButtons.java \
  DeviceBeans/DeviceButtonsBeanInfo.java \
  DeviceBeans/DeviceButtonsCustomizer.java \
  DeviceBeans/DeviceCancel.java \
  DeviceBeans/DeviceCancelBeanInfo.java \
  DeviceBeans/DeviceChannel.java \
  DeviceBeans/DeviceChannelBeanInfo.java \
  DeviceBeans/DeviceChannelCustomizer.java \
  DeviceBeans/DeviceChoice.java \
  DeviceBeans/DeviceChoiceBeanInfo.java \
  DeviceBeans/DeviceChoiceCustomizer.java \
  DeviceBeans/DeviceCloseListener.java \
  DeviceBeans/DeviceComponent.java \
  DeviceBeans/DeviceControl.java \
  DeviceBeans/DeviceCustomizer.java \
  DeviceBeans/DeviceDispatch.java \
  DeviceBeans/DeviceDispatchBeanInfo.java \
  DeviceBeans/DeviceDispatchField.java \
  DeviceBeans/DeviceField.java \
  DeviceBeans/DeviceFieldBeanInfo.java \
  DeviceBeans/DeviceFieldCustomizer.java \
  DeviceBeans/DeviceLabel.java \
  DeviceBeans/DeviceLabelBeanInfo.java \
  DeviceBeans/DeviceLabelCustomizer.java \
  DeviceBeans/DeviceMultiComponent.java \
  DeviceBeans/DeviceOk.java \
  DeviceBeans/DeviceOkBeanInfo.java \
  DeviceBeans/DeviceParameters.java \
  DeviceBeans/DeviceParametersBeanInfo.java \
  DeviceBeans/DeviceReset.java \
  DeviceBeans/DeviceResetBeanInfo.java \
  DeviceBeans/DeviceSetup.java \
  DeviceBeans/DeviceSetupBeanInfo.java \
  DeviceBeans/DeviceTable.java \
  DeviceBeans/DeviceTableBeanInfo.java \
  DeviceBeans/DeviceTableCustomizer.java \
  DeviceBeans/DeviceUpdateListener.java \
  DeviceBeans/DeviceWave.java \
  DeviceBeans/DeviceWaveBeanInfo.java \
  DeviceBeans/DeviceWaveCustomizer.java \
  DeviceBeans/DeviceWaveDisplay.java \
  DeviceBeans/DeviceWaveDisplayBeanInfo.java \
  DeviceBeans/DeviceWaveDisplayCustomizer.java \
  DeviceBeans/DeviceWaveParameters.java \
  DeviceBeans/DeviceWaveParametersBeanInfo.java

TRAV_SRC= \
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
  jTraverser/Editor/ActionEditor.java \
  jTraverser/Editor/ArgEditor.java \
  jTraverser/Editor/AxisEditor.java \
  jTraverser/Editor/DataEditor.java \
  jTraverser/Editor/DispatchEditor.java \
  jTraverser/Editor/Editor.java \
  jTraverser/Editor/ExprEditor.java \
  jTraverser/Editor/FloatArrayEditor.java \
  jTraverser/Editor/IntArrayEditor.java \
  jTraverser/Editor/LabeledExprEditor.java \
  jTraverser/Editor/MethodEditor.java \
  jTraverser/Editor/NodeDataPropertyEditor.java \
  jTraverser/Editor/NodeEditor.java \
  jTraverser/Editor/NodeInfoPropertyEditor.java \
  jTraverser/Editor/ParameterEditor.java \
  jTraverser/Editor/ProcedureEditor.java \
  jTraverser/Editor/ProgramEditor.java \
  jTraverser/Editor/PythonEditor.java \
  jTraverser/Editor/RangeEditor.java \
  jTraverser/Editor/RoutineEditor.java \
  jTraverser/Editor/TaskEditor.java \
  jTraverser/Editor/WindowEditor.java

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

_DEVICE_GIFS= \
  DeviceBeans/DeviceApply.gif \
  DeviceBeans/DeviceButtons.gif \
  DeviceBeans/DeviceCancel.gif \
  DeviceBeans/DeviceChannel.gif \
  DeviceBeans/DeviceChoice.gif \
  DeviceBeans/DeviceDispatch.gif \
  DeviceBeans/DeviceField.gif \
  DeviceBeans/DeviceOk.gif \
  DeviceBeans/DeviceReset.gif \
  DeviceBeans/DeviceSetup.gif

_TRAV_GIFS= \
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
