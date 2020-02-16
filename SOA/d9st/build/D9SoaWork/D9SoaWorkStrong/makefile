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
# Define macros for Strong Client Binding libraries
# ==================================================
D9SOAWORKSTRONG_CLS      = $(PROJECT_CLS)$(PS)D9SoaWorkStrong
D9SOAWORKSTRONG_ANTARGS  = -Dsoa.client.home=$(SOA_CLIENT_KIT) -Dtoolbox.home=$(TOOLBOX_HOME) \
                                -Dgen.src.home=$(PROJECT_DRV)       -Dclasses.home=$(D9SOAWORKSTRONG_CLS) \
                                -Djars.home=$(PROJECT_JAR)          -Djavac.debug=$(JAVAC_DEBUG)
D9SOAWORKSTRONG_NETARGS  = /property:Configuration=$(NET_CONFIG),CHSARP_HOME=$(CHSARP_HOME),PROJECT_DRV=$(PROJECT_DRV),PROJECT_NET=$(PROJECT_NET),PROJECT_IOD=$(PROJECT_IOD),SOA_CLIENT_KIT=$(SOA_CLIENT_KIT)
D9SOAWORKSTRONG_JAVA_SRC = $(PROJECT_DRV)$(PS)D9SoaWork$(PS)D9SoaWorkMap.properties
D9SOAWORKSTRONG_JAVA_TARG= $(PROJECT_JAR)$(PS)D9SoaWorkStrong_11000.2.0.jar
D9SOAWORKSTRONG_NET_SRC  = $(PROJECT_DRV)$(PS)D9SoaWorkClient$(PS)strong$(PS)net$(PS)*.cs
D9SOAWORKSTRONG_NET_TARG = $(PROJECT_NET)$(PS)D9SoaWorkStrong.dll

# ==================================================
# Build targets for C++ Strong Client Bindings
# ==================================================
include build/D9SoaWork/D9SoaWorkStrong/libd9soaworkstrong.makeInc
include build/D9SoaWork/D9SoaWorkStrong/libd9soaworkstrongmngd.makeInc



D9SoaWorkStrong_all:    D9SoaWorkStrong-build_all$(JAVA_STRONG_ENABLED)   libd9soaworkstrong_all$(CPP_STRONG_ENABLED)   libd9soaworkstrong_all$(CPP_STRONG_ENABLED)   libd9soaworkstrongmngd_all$(CPP_STRONG_ENABLED)   libd9soaworkstrongmngd_all$(CPP_STRONG_ENABLED)  
D9SoaWorkStrong_clean:  D9SoaWorkStrong-build_clean$(JAVA_STRONG_ENABLED) libd9soaworkstrong_clean$(CPP_STRONG_ENABLED) libd9soaworkstrong_clean$(CPP_STRONG_ENABLED) libd9soaworkstrongmngd_clean$(CPP_STRONG_ENABLED) libd9soaworkstrongmngd_clean$(CPP_STRONG_ENABLED)


# ==================================================
# Build targets for Java Strong Client Bindings
# ==================================================
D9SoaWorkStrong-build_allNoOp:
D9SoaWorkStrong-build_all:  $(D9SOAWORKSTRONG_JAVA_TARG)

$(D9SOAWORKSTRONG_JAVA_SRC) :

$(D9SOAWORKSTRONG_JAVA_TARG) :  $(D9SOAWORKSTRONG_JAVA_SRC)  
	@echo ________________________________________________________________________________
	@echo Building Java Client D9SoaWorkStrong
	@$(ANT) -file build/D9SoaWork/D9SoaWorkStrong/D9SoaWorkStrong-build.xml $(D9SOAWORKSTRONG_ANTARGS) all
	$(COPY_FILE) $(D9SOAWORKSTRONG_JAVA_TARG) $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs
	@echo ________________________________________________________________________________

	
D9SoaWorkStrong-build_cleanNoOp:
D9SoaWorkStrong-build_clean:
	@echo ________________________________________________________________________________
	@echo Cleaning Java Client D9SoaWorkStrong
	@$(ANT) -file  build/D9SoaWork/D9SoaWorkStrong/D9SoaWorkStrong-build.xml $(D9SOAWORKSTRONG_ANTARGS) clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)java$(PS)libs$(PS)D9SoaWorkStrong_11000.2.0.jar
	@echo ________________________________________________________________________________





# ==================================================
# Build targets for .NET Strong Client Bindings
# ==================================================
D9SoaWorkStrong-cs_allNoOp:
D9SoaWorkStrong-cs_all: $(D9SOAWORKSTRONG_NET_TARG)

$(D9SOAWORKSTRONG_NET_SRC) : 

$(D9SOAWORKSTRONG_NET_TARG) : $(D9SOAWORKSTRONG_NET_SRC)   
	@echo ________________________________________________________________________________
	@echo Building .NET Client D9SoaWorkStrong
	@$(CSHARP_COMPILER) build/D9SoaWork/D9SoaWorkStrong/D9SoaWorkStrong.csproj $(D9SOAWORKSTRONG_NETARGS) /target:Build
	$(COPY_FILE)  $(D9SOAWORKSTRONG_NET_TARG) $(PORJECT_SOA_CLIENT)$(PS)net$(PS)libs
	@echo ________________________________________________________________________________

	
D9SoaWorkStrong-cs_cleanNoOp: 
D9SoaWorkStrong-cs_clean: 
	@echo ________________________________________________________________________________
	@echo Cleaning .NET Client D9SoaWorkStrong
	@$(CSHARP_COMPILER) build/D9SoaWork/D9SoaWorkStrong/D9SoaWorkStrong.csproj $(D9SOAWORKSTRONG_NETARGS) /target:Clean
	-$(DELETE_FILE)  $(PORJECT_SOA_CLIENT)$(PS)net$(PS)libs$(PS)D9SoaWorkStrong.dll
	@echo ________________________________________________________________________________


