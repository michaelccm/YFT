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
# Define macros for RAC Client Binding libraries
# ==================================================
D9SOAWORKRAC_CLS      = $(PROJECT_CLS)$(PS)D9SoaWorkRac
D9SOAWORKRAC_ANTARGS  = -Dsoa.client.home=$(SOA_CLIENT_KIT) -Dtoolbox.home=$(TOOLBOX_HOME) \
                                -Dgen.src.home=$(PROJECT_DRV)       -Dclasses.home=$(D9SOAWORKRAC_CLS) \
                                -Djars.home=$(PROJECT_JAR)          -Djavac.debug=$(JAVAC_DEBUG) \
                                -Drac.home=$(RAC_HOME) 
D9SOAWORKRAC_JAVA_SRC = $(PROJECT_DRV)$(PS)D9SoaWork$(PS)D9SoaWorkMap.properties
D9SOAWORKRAC_JAVA_TARG= $(PROJECT_JAR)$(PS)D9SoaWorkRac_11000.2.0.jar



D9SoaWorkRac_all:   D9SoaWorkRac-build_all$(JAVA_RAC_ENABLED)
D9SoaWorkRac_clean: D9SoaWorkRac-build_clean$(JAVA_RAC_ENABLED)


# ==================================================
# Build targets for Java RAC Client Bindings
# ==================================================
D9SoaWorkRac-build_allNoOp:
D9SoaWorkRac-build_all:  $(D9SOAWORKRAC_JAVA_TARG)

$(D9SOAWORKRAC_JAVA_SRC) :

$(D9SOAWORKRAC_JAVA_TARG) :  $(D9SOAWORKRAC_JAVA_SRC)  
	@echo ________________________________________________________________________________
	@echo Building Java Client D9SoaWorkRac
	@$(ANT) -file build/D9SoaWork/D9SoaWorkRac/D9SoaWorkRac-build.xml $(D9SOAWORKRAC_ANTARGS) all
	$(COPY_FILE) $(D9SOAWORKRAC_JAVA_TARG) $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs
	@echo ________________________________________________________________________________



D9SoaWorkRac-build_cleanNoOp:	
D9SoaWorkRac-build_clean:
	@echo ________________________________________________________________________________
	@echo Cleaning Java Client D9SoaWorkRac
	@$(ANT) -file  build/D9SoaWork/D9SoaWorkRac/D9SoaWorkRac-build.xml $(D9SOAWORKRAC_ANTARGS) clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs$(PS)D9SoaWorkRac_11000.2.0.jar
	@echo ________________________________________________________________________________




