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
  devicebeans/Database.java \
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
  devicebeans/DeviceUpdateListener.java
  
DEVWAV_SRC= \
  devicebeans/devicewave/DeviceWave.java \
  devicebeans/devicewave/DeviceWaveBeanInfo.java \
  devicebeans/devicewave/DeviceWaveCustomizer.java \
  devicebeans/devicewave/DeviceWaveDisplay.java \
  devicebeans/devicewave/DeviceWaveDisplayBeanInfo.java \
  devicebeans/devicewave/DeviceWaveDisplayCustomizer.java \
  devicebeans/devicewave/DeviceWaveParameters.java \
  devicebeans/devicewave/DeviceWaveParametersBeanInfo.java

JTRAVERSER_SRC= \
  jtraverser/DataChangeEvent.java \
  jtraverser/DataChangeListener.java \
  jtraverser/Node.java \
  jtraverser/TreeView.java \
  jtraverser/TreeManager.java \
  jtraverser/dialogs/AddNode.java \
  jtraverser/dialogs/DialogSet.java \
  jtraverser/dialogs/Dialogs.java \
  jtraverser/dialogs/DisplayData.java \
  jtraverser/dialogs/DisplayNci.java \
  jtraverser/dialogs/DisplayTags.java \
  jtraverser/dialogs/ModifyFlags.java \
  jtraverser/dialogs/GraphPanel.java \
  jtraverser/dialogs/ModifyData.java \
  jtraverser/dialogs/ModifyTags.java \
  jtraverser/dialogs/Rename.java \
  jtraverser/dialogs/SubTrees.java \
  jtraverser/dialogs/TreeDialog.java \
  jtraverser/dialogs/TreeOpenDialog.java \
  jtraverser/editor/ActionEditor.java \
  jtraverser/editor/ArgEditor.java \
  jtraverser/editor/AxisEditor.java \
  jtraverser/editor/ArrayEditor.java \
  jtraverser/editor/DataEditor.java \
  jtraverser/editor/DispatchEditor.java \
  jtraverser/editor/Editor.java \
  jtraverser/editor/ExprEditor.java \
  jtraverser/editor/LabeledExprEditor.java \
  jtraverser/editor/MethodEditor.java \
  jtraverser/editor/NodeEditor.java \
  jtraverser/editor/ParameterEditor.java \
  jtraverser/editor/ProcedureEditor.java \
  jtraverser/editor/ProgramEditor.java \
  jtraverser/editor/PythonEditor.java \
  jtraverser/editor/RangeEditor.java \
  jtraverser/editor/RoutineEditor.java \
  jtraverser/editor/SignalEditor.java \
  jtraverser/editor/TaskEditor.java \
  jtraverser/editor/WindowEditor.java \
  jtraverser/jTraverserFacade.java \
  jTraverser.java

TOOLS_SRC= \
  jtraverser/tools/CompileTree.java \
  jtraverser/tools/DecompileTree.java

MDSIP_SRC= \
  mds/ITreeShr.java \
  mds/MdsException.java \
  mds/MdsShr.java \
  mds/TdiShr.java \
  mds/TreeShr.java \
  mds/TREE.java \
  mds/data/descriptor/ARRAY.java \
  mds/data/descriptor/DTYPE.java \
  mds/data/descriptor/OPC.java \
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
  mds/data/descriptor_r/BUILD.java \
  mds/data/descriptor_r/Action.java \
  mds/data/descriptor_r/Call.java \
  mds/data/descriptor_r/Condition.java \
  mds/data/descriptor_r/Conglom.java \
  mds/data/descriptor_r/Dependency.java \
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
  mds/data/descriptor_s/Pointer.java \
  mds/data/descriptor_s/TREENODE.java \
  mds/data/descriptor_s/Uint128.java \
  mds/data/descriptor_s/Uint16.java \
  mds/data/descriptor_s/Uint32.java \
  mds/data/descriptor_s/Uint64.java \
  mds/data/descriptor_s/Uint8.java \
  mds/mdsip/Connection.java \
  mds/mdsip/ConnectionEvent.java \
  mds/mdsip/ConnectionListener.java \
  mds/mdsip/Message.java \
  mds/mdsip/UpdateEvent.java \
  mds/mdsip/UpdateEventListener.java

DEVICE_GIF= \
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

DEVICE_GIF= \
  devicebeans/devicewave/DeviceWave.gif

TRAV_GIF= \
  jtraverser/action.gif \
  jtraverser/any.gif \
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