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

DeviceBeans.jar: classjava.stamp $(DEVICE_CLS) $(DEVICE_GIFS) devicebeans/MANIFEST.mf
	$(JAR) c0fm $@ ${srcdir}/devicebeans/MANIFEST.mf $(DEVICE_CLS) $(DEVICE_GIFS)

mdsData.jar: classjava.stamp $(DATA_CLS)
	$(JAR) c0f $@ $(DATA_CLS)

# Giant list of source files follow

DEVICE_SRC= \
  devicebeans/DeviceApply.java \
  devicebeans/DeviceApplyBeanInfo.java \
  devicebeans/DeviceButtons.java \
  devicebeans/DeviceButtonsBeanInfo.java \
  devicebeans/DeviceButtonsCustomizer.java \
  devicebeans/DeviceCancel.java \
  devicebeans/DeviceCancelBeanInfo.java \
  devicebeans/DeviceChannel.java \
  devicebeans/DeviceChannelBeanInfo.java \
  devicebeans/DeviceChannelCustomizer.java \
  devicebeans/DeviceChoice.java \
  devicebeans/DeviceChoiceBeanInfo.java \
  devicebeans/DeviceChoiceCustomizer.java \
  devicebeans/DeviceCloseListener.java \
  devicebeans/DeviceComponent.java \
  devicebeans/DeviceControl.java \
  devicebeans/DeviceCustomizer.java \
  devicebeans/DeviceDispatch.java \
  devicebeans/DeviceDispatchBeanInfo.java \
  devicebeans/DeviceDispatchField.java \
  devicebeans/DeviceField.java \
  devicebeans/DeviceFieldBeanInfo.java \
  devicebeans/DeviceFieldCustomizer.java \
  devicebeans/DeviceLabel.java \
  devicebeans/DeviceLabelBeanInfo.java \
  devicebeans/DeviceLabelCustomizer.java \
  devicebeans/DeviceMultiComponent.java \
  devicebeans/DeviceOk.java \
  devicebeans/DeviceOkBeanInfo.java \
  devicebeans/DeviceParameters.java \
  devicebeans/DeviceParametersBeanInfo.java \
  devicebeans/DeviceReset.java \
  devicebeans/DeviceResetBeanInfo.java \
  devicebeans/DeviceSetup.java \
  devicebeans/DeviceSetupBeanInfo.java \
  devicebeans/DeviceTable.java \
  devicebeans/DeviceTableBeanInfo.java \
  devicebeans/DeviceTableCustomizer.java \
  devicebeans/DeviceUpdateListener.java \
  devicebeans/DeviceWave.java \
  devicebeans/DeviceWaveBeanInfo.java \
  devicebeans/DeviceWaveCustomizer.java \
  devicebeans/DeviceWaveDisplay.java \
  devicebeans/DeviceWaveDisplayBeanInfo.java \
  devicebeans/DeviceWaveDisplayCustomizer.java \
  devicebeans/DeviceWaveParameters.java \
  devicebeans/DeviceWaveParametersBeanInfo.java

TRAV_SRC= \
  jTraverser.java \
  jtraverser/CompileTree.java \
  jtraverser/Convert.java \
  jtraverser/DataChangeEvent.java \
  jtraverser/DataChangeListener.java \
  jtraverser/DecompileTree.java \
  jtraverser/DisplayData.java \
  jtraverser/DisplayNci.java \
  jtraverser/DisplayTags.java \
  jtraverser/FrameRepository.java \
  jtraverser/jTraverserFacade.java \
  jtraverser/LoadFile.java \
  jtraverser/LoadPulse.java \
  jtraverser/ModifyData.java \
  jtraverser/Node.java \
  jtraverser/NodeBeanInfo.java \
  jtraverser/NodeDisplayData.java \
  jtraverser/NodeDisplayNci.java \
  jtraverser/NodeDisplayTags.java \
  jtraverser/NodeInfo.java \
  jtraverser/NodeModifyData.java \
  jtraverser/RemoteTree.java \
  jtraverser/StoreFile.java \
  jtraverser/SyntaxException.java \
  jtraverser/Tree.java \
  jtraverser/TreeDialog.java \
  jtraverser/TreeNode.java \
  jtraverser/TreeServer.java \
  jtraverser/editor/ActionEditor.java \
  jtraverser/editor/ArgEditor.java \
  jtraverser/editor/AxisEditor.java \
  jtraverser/editor/DataEditor.java \
  jtraverser/editor/DispatchEditor.java \
  jtraverser/editor/Editor.java \
  jtraverser/editor/ExprEditor.java \
  jtraverser/editor/FloatArrayEditor.java \
  jtraverser/editor/IntArrayEditor.java \
  jtraverser/editor/LabeledExprEditor.java \
  jtraverser/editor/MethodEditor.java \
  jtraverser/editor/NodeDataPropertyEditor.java \
  jtraverser/editor/NodeEditor.java \
  jtraverser/editor/NodeInfoPropertyEditor.java \
  jtraverser/editor/ParameterEditor.java \
  jtraverser/editor/ProcedureEditor.java \
  jtraverser/editor/ProgramEditor.java \
  jtraverser/editor/PythonEditor.java \
  jtraverser/editor/RangeEditor.java \
  jtraverser/editor/RoutineEditor.java \
  jtraverser/editor/TaskEditor.java \
  jtraverser/editor/WindowEditor.java

DATA_SRC= \
  mds/Database.java \
  mds/DatabaseException.java \
  mds/data/ActionData.java \
  mds/data/ApdData.java \
  mds/data/AtomicData.java \
  mds/data/ByteData.java \
  mds/data/CallData.java \
  mds/data/ComplexData.java \
  mds/data/CompoundData.java \
  mds/data/ConditionData.java \
  mds/data/ConglomData.java \
  mds/data/Data.java \
  mds/data/DataId.java \
  mds/data/DataListener.java \
  mds/data/DependencyData.java \
  mds/data/DimensionData.java \
  mds/data/DispatchData.java \
  mds/data/DoubleData.java \
  mds/data/EventData.java \
  mds/data/FloatData.java \
  mds/data/FunctionData.java \
  mds/data/IdentData.java \
  mds/data/IllegalDataException.java \
  mds/data/IntData.java \
  mds/data/MethodData.java \
  mds/data/NidData.java \
  mds/data/NodeId.java \
  mds/data/OctaData.java \
  mds/data/ParameterData.java \
  mds/data/PathData.java \
  mds/data/ProcedureData.java \
  mds/data/ProgramData.java \
  mds/data/QuadData.java \
  mds/data/RangeData.java \
  mds/data/RoutineData.java \
  mds/data/ShortData.java \
  mds/data/SignalData.java \
  mds/data/SlopeData.java \
  mds/data/StringData.java \
  mds/data/UnsupportedDataException.java \
  mds/data/WindowData.java \
  mds/data/WithErrorData.java \
  mds/data/WithUnitsData.java \
  mds/data/arraydata/ArrayData.java \
  mds/data/arraydata/ByteArray.java \
  mds/data/arraydata/DoubleArray.java \
  mds/data/arraydata/FloatArray.java \
  mds/data/arraydata/IntArray.java \
  mds/data/arraydata/OctaArray.java \
  mds/data/arraydata/QuadArray.java \
  mds/data/arraydata/ShortArray.java \
  mds/data/arraydata/StringArray.java \
  mds/Database.java \
  mds/MdsException.java \
  mds/mdsip/Connection.java \
  mds/mdsip/ConnectionEvent.java \
  mds/mdsip/ConnectionListener.java \
  mds/mdsip/Message.java \
  mds/mdsip/UpdateEvent.java \
  mds/mdsip/UpdateEventListener.java

_DEVICE_GIFS= \
  devicebeans/DeviceApply.gif \
  devicebeans/DeviceButtons.gif \
  devicebeans/DeviceCancel.gif \
  devicebeans/DeviceChannel.gif \
  devicebeans/DeviceChoice.gif \
  devicebeans/DeviceDispatch.gif \
  devicebeans/DeviceField.gif \
  devicebeans/DeviceOk.gif \
  devicebeans/DeviceReset.gif \
  devicebeans/DeviceSetup.gif

_TRAV_GIFS= \
  jtraverser/action.gif \
  jtraverser/axis.gif \
  jtraverser/compound.gif \
  jtraverser/device.gif \
  jtraverser/dispatch.gif \
  jtraverser/numeric.gif \
  jtraverser/signal.gif \
  jtraverser/structure.gif \
  jtraverser/subtree.gif \
  jtraverser/task.gif \
  jtraverser/text.gif \
  jtraverser/window.gif
