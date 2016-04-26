JAVAROOT = $(builddir)
EXTRA_DIST = "$(MINDTERM) $(W7XDATAPROVIDER)"
CLEANFILES = $(java_DATA)

MINDTERM = $(srcdir)/MindTerm.jar
W7XDATAPROVIDER = $(srcdir)/w7x/w7xDataProvider.jar
CLASSPATH_ENV = CLASSPATH="$(MINDTERM):$(W7XDATAPROVIDER)"

# Create the shell script that runs the program
if MINGW
bin_SCRIPTS = jScope.bat
else
bin_SCRIPTS = scripts/jScope
EXTRA_DIST += jScope.template
CLEANFILES += $(bin_SCRIPTS)

$(bin_SCRIPTS): jScope.template
	$(INSTALL) -d scripts
	$(INSTALL) $< $@
endif


# Documentation
dist_docs_DATA = $(DOCS)
docsdir = $(exec_prefix)/java/classes/jdocs

dist_java_DATA = jScope.properties MindTerm.jar w7x/w7xDataProvider.jar

java_DATA = jScope.jar WaveDisplay.jar jetDataProvider.jar localDataProvider.jar mdsDataProvider.jar miscDataProvider.jar twuDataProvider.jar w7xDataProvider.jar
javadir = $(exec_prefix)/java/classes
$(java_DATA): classjava.stamp

EXTRA_DIST += colors1.tbl
jScope.jar: colors1.tbl
	$(MKDIR_P) @builddir@/jdocs
	cp $(addprefix @srcdir@/,$(SUBDOCS)) @builddir@/jdocs
	$(JAR) c0mf $@ $(addprefix @srcdir@/,jScopeManifest.mf) $(addprefix @srcdir@/,$(TOPDOCS)) $(JSCOPE_CLASS) @builddir@/jdocs $<
	rm -Rf @builddir@/jdocs

WaveDisplay.jar:
	$(JAR) c0f $@ $(WAVEDISPLAY_CLASS)

dist_java_JAVA = $(JSCOPE_SRC) $(WAVEDISPLAY_SRC) $(COMMON_SRC) $(JET_SRC) $(LOCAL_SRC) $(MDS_SRC) $(MISC_SRC) $(TWU_SRC) $(W7X_SRC) jScope/DEBUG.java
COMMON_CLASS = $(COMMON_SRC:.java=*.class)
JSCOPE_CLASS = $(COMMON_CLASS) $(JSCOPE_SRC:.java=*.class) $(EXTRA_CLASS)
JET_CLASS = $(JET_SRC:.java=*.class)
LOCAL_CLASS = $(LOCAL_SRC:.java=*.class)
MDS_CLASS = $(MDS_SRC:.java=*.class)
MISC_CLASS = $(MISC_SRC:.java=*.class)
TWU_CLASS = $(TWU_SRC:.java=*.class)
W7X_CLASS = $(W7X_SRC:.java=*.class)
WAVEDISPLAY_CLASS = $(COMMON_CLASS) $(WAVEDISPLAY_SRC:.java=*.class)
CLEANFILES += $(COMMON_CLASS) $(JSCOPE_CLASS) $(WAVEDISPLAY_CLASS) $(EXTRA_CLASS) $(JET_CLASS) $(LOCAL_CLASS) $(MDS_CLASS) $(MISC_CLASS) $(TWU_CLASS) $(W7X_CLASS) jScope/DEBUG.class

include Makefile.common
