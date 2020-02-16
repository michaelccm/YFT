# ==================================================
# Copyright 2013.
# Siemens Product Lifecycle Management Software Inc.
# All Rights Reserved.
# ==================================================
# ==================================================
#             Auto-generated source
#                 DO NOT EDIT
# ==================================================



# ==================================================
# Define macros for Loose Client Binding libraries
# ==================================================
D9SOAWORKLOOSE_CLS       = $(PROJECT_CLS)$(PS)D9SoaWorkLoose
D9SOAWORKLOOSE_ANTARGS   = -Dsoa.client.home=$(SOA_CLIENT_KIT) -Dtoolbox.home=$(TOOLBOX_HOME) \
                                 -Dgen.src.home=$(PROJECT_DRV)       -Dclasses.home=$(D9SOAWORKLOOSE_CLS) \
                                 -Djars.home=$(PROJECT_JAR)          -Djavac.debug=$(JAVAC_DEBUG)
D9SOAWORKLOOSE_JAVA_SRC  = $(PROJECT_DRV)$(PS)D9SoaWork$(PS)D9SoaWorkMap.properties
D9SOAWORKLOOSE_JAVA_TARG = $(PROJECT_JAR)$(PS)D9SoaWorkLoose_11000.2.0.jar


D9SoaWorkLoose_all:   D9SoaWorkLoose-build_all$(JAVA_LOOSE_ENABLED)
D9SoaWorkLoose_clean: D9SoaWorkLoose-build_clean$(JAVA_LOOSE_ENABLED)

# ==================================================
# Build targets for Java Loose Client Bindings
# ==================================================
D9SoaWorkLoose-build_allNoOp:
D9SoaWorkLoose-build_all: $(D9SOAWORKLOOSE_JAVA_TARG)


$(D9SOAWORKLOOSE_JAVA_SRC) :

$(D9SOAWORKLOOSE_JAVA_TARG) :  $(D9SOAWORKLOOSE_JAVA_SRC)  
	@echo ________________________________________________________________________________
	@echo Building Java Client D9SoaWorkLoose
	@$(ANT) -file build/D9SoaWork/D9SoaWorkLoose/D9SoaWorkLoose-build.xml $(D9SOAWORKLOOSE_ANTARGS) all
	$(COPY_FILE) $(D9SOAWORKLOOSE_JAVA_TARG) $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs
	@echo ________________________________________________________________________________


D9SoaWorkLoose-build_cleanNoOp :	
D9SoaWorkLoose-build_clean:
	@echo ________________________________________________________________________________
	@echo Cleaning Java Client D9SoaWorkLoose
	@$(ANT) -file  build/D9SoaWork/D9SoaWorkLoose/D9SoaWorkLoose-build.xml $(D9SOAWORKLOOSE_ANTARGS) clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs$(PS)D9SoaWorkLoose_11000.2.0.jar
	@echo ________________________________________________________________________________

