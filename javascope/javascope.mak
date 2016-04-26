include Makefile.common

CLASSPATH = -classpath .;MindTerm.jar;w7x\swingx.jar;w7x\w7xDataProvider.jar
JAVAC = "$(JDK_DIR)\bin\javac.exe"
JAR = "$(JDK_DIR)\bin\jar.exe"
JARDIR = ..\java\classes

SOURCES = $(JSCOPE_SRC) $(WAVEDISPLAY_SRC) $(COMMON_SRC) $(JET_SRC) $(LOCAL_SRC) $(MDS_SRC) $(MISC_SRC) $(TWU_SRC) $(W7X_SRC) jScope/DEBUG.java
WAVECLASSES = $(COMMON_SRC:.java=*.class)
CLASSES = $(WAVECLASSES) $(JSCOPE_SRC:.java=*.class)
COMMON_CLASS = $(COMMON_SRC:.java=*.class)
JSCOPE_CLASS = $(COMMON_CLASS) $(JSCOPE_SRC:.java=*.class) $(EXTRA_CLASS)
JET_CLASS = $(JET_SRC:.java=*.class)
LOCAL_CLASS = $(LOCAL_SRC:.java=*.class)
MDS_CLASS = $(MDS_SRC:.java=*.class)
MISC_CLASS = $(MISC_SRC:.java=*.class)
TWU_CLASS = $(TWU_SRC:.java=*.class)
W7X_CLASS = $(W7X_SRC:.java=*.class)
WAVEDISPLAY_CLASS = $(COMMON_CLASS) $(WAVEDISPLAY_SRC:.java=*.class)

all: $(JARDIR) $(JARDIR)\jScope.properties $(JARDIR)\MindTerm.jar $(JARDIR)\w7x\w7xDataProvider.jar $(JARDIR)\jScope.jar $(JARDIR)\WaveDisplay.jar
	rem done

$(JARDIR):
	- mkdir ..\java
	- mkdir $@

$(JARDIR)\jScope.properties: jScope.properties
	copy $** $@

$(JARDIR)\MindTerm.jar: MindTerm.jar
	copy $** $@

$(JARDIR)\w7xDataProvider.jar: w7x\w7xDataProvider.jar
	copy $** $@

class.stamp: $(SOURCES)
	$(JAVAC) $(CLASSPATH) $(SOURCES)
	echo x > class.stamp

$(JARDIR)\jScope.jar: class.stamp
	- del/q/f/s docs
	- mkdir docs
	for %I in ($(DOCS)) do copy %I docs
	$(JAR) -cmf jScope\MANIFEST.mf $@ $(CLASSES) docs
	- del/q/f/s docs
	- rmdir docs

$(JARDIR)\jetDataProvider.jar: class.stamp
	$(JAR) c0mf jet\MANIFEST.mf $@ $(JET_CLASS) $<

$(JARDIR)\localDataProvider.jar: class.stamp
	$(JAR) c0mf local\MANIFEST.mf $@ $(LOCAL_CLASS) $<

$(JARDIR)\mdsDataProvider.jar: class.stamp
	$(JAR) c0mf mds\MANIFEST.mf $@ $(MDS_CLASS) $<

$(JARDIR)\miscDataProvider.jar: class.stamp
	$(JAR) c0mf misc\MANIFEST.mf $@ $(MISC_CLASS) $<

$(JARDIR)\twuDataProvider.jar: class.stamp
	$(JAR) c0mf twu\MANIFEST.mf $@ $(TWU_CLASS) $<

$(JARDIR)\w7xDataProvider.jar: class.stamp
	- copy $(W7XDATAPROVIDER) @builddir@
	$(JAR) u0mf w7x\MANIFEST.mf $@ $(W7X_CLASS) $<

$(JARDIR)\WaveDisplay.jar: class.stamp
	$(JAR) -cf $@ $(WAVECLASSES)

