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
# Define macros for XSD Binding libraries
# ==================================================
D9SOAWORKTYPES_CLS      = $(PROJECT_CLS)$(PS)D9SoaWorkTypes
D9SOAWORKTYPES_IOD      = $(PROJECT_IOD)$(PS)d9soaworktypes
D9SOAWORKTYPES_ANTARGS  = -Dsoa.client.home=$(SOA_CLIENT_KIT) -Dtoolbox.home=$(TOOLBOX_HOME) \
                                -Dgen.src.home=$(PROJECT_DRV)       -Dclasses.home=$(D9SOAWORKTYPES_CLS) \
                                -Djars.home=$(PROJECT_JAR)          -Djavac.debug=$(JAVAC_DEBUG)
D9SOAWORKTYPES_NETARGS  = /property:Configuration=$(NET_CONFIG),CHSARP_HOME=$(CHSARP_HOME),PROJECT_DRV=$(PROJECT_DRV),PROJECT_NET=$(PROJECT_NET),PROJECT_IOD=$(PROJECT_IOD),SOA_CLIENT_KIT=$(SOA_CLIENT_KIT)
D9SOAWORKTYPES_JAVA_SRC = $(PROJECT_DRV)$(PS)D9SoaWork$(PS)D9SoaWorkMap.properties
D9SOAWORKTYPES_JAVA_TARG= $(PROJECT_JAR)$(PS)D9SoaWorkTypes_11000.2.0.jar
D9SOAWORKTYPES_NET_SRC  = $(PROJECT_DRV)$(PS)D9SoaWorkTypes$(PS)net$(PS)*.cs
D9SOAWORKTYPES_NET_TARG = $(PROJECT_NET)$(PS)D9SoaWorkTypes.dll

# ==================================================
# Build targets for C++ XSD Bindings
# ==================================================


# ==================================================
# All build targets for the XSD Binding libraries
# ==================================================
D9SoaWorkTypes_all:    D9SoaWorkTypes-build_all  
D9SoaWorkTypes_clean:  D9SoaWorkTypes-build_clean



# ==================================================
# Build targets for Java XSD Bindings
# ==================================================
D9SoaWorkTypes-build_all: $(D9SOAWORKTYPES_JAVA_TARG)

$(D9SOAWORKTYPES_JAVA_SRC) :

$(D9SOAWORKTYPES_JAVA_TARG) :  $(D9SOAWORKTYPES_JAVA_SRC)  
	@echo ________________________________________________________________________________
	@echo Building Java XSD Binding D9SoaWorkTypes
	@$(ANT) -file build/D9SoaWork/D9SoaWorkTypes/D9SoaWorkTypes-build.xml $(D9SOAWORKTYPES_ANTARGS) all
	$(COPY_FILE) $(D9SOAWORKTYPES_JAVA_TARG) $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs
	@echo ________________________________________________________________________________

	
D9SoaWorkTypes-build_clean:
	@echo ________________________________________________________________________________
	@echo Cleaning Java XSD Binding D9SoaWorkTypes
	@$(ANT) -file  build/D9SoaWork/D9SoaWorkTypes/D9SoaWorkTypes-build.xml $(D9SOAWORKTYPES_ANTARGS) clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs$(PS)D9SoaWorkTypes_11000.2.0.jar
	@echo ________________________________________________________________________________



# ==================================================
# Build targets for .NET XSD Bindings
# ==================================================
D9SoaWorkTypes-cs_allNoOp:
D9SoaWorkTypes-cs_all:   $(D9SOAWORKTYPES_NET_TARG)


$(D9SOAWORKTYPES_NET_SRC) : 

$(D9SOAWORKTYPES_NET_TARG) : $(D9SOAWORKTYPES_NET_SRC)   
	@echo ________________________________________________________________________________
	@echo Building .NET XSD Binding D9SoaWorkTypes
	@$(CSHARP_COMPILER) build/D9SoaWork/D9SoaWorkTypes/D9SoaWorkTypes.csproj $(D9SOAWORKTYPES_NETARGS) /target:Build
	$(COPY_FILE)  $(D9SOAWORKTYPES_NET_TARG) $(PORJECT_SOA_CLIENT)$(PS)net$(PS)libs
	@echo ________________________________________________________________________________

D9SoaWorkTypes-cs_cleanNoOp: 	
D9SoaWorkTypes-cs_clean: 
	@echo ________________________________________________________________________________
	@echo Cleaning .NET XSD Binding D9SoaWorkTypes
	@$(CSHARP_COMPILER) build/D9SoaWork/D9SoaWorkTypes/D9SoaWorkTypes.csproj $(D9SOAWORKTYPES_NETARGS) /target:Clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)net$(PS)libs$(PS)D9SoaWorkTypes.dll
	@echo ________________________________________________________________________________

