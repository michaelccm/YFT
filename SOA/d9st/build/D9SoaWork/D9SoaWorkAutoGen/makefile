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
# Define macros for the service library Auto-Generation
# ==================================================   
D9SOAWORKAUTOGEN_ANTARGS   = -Dtoolbox.home=$(TOOLBOX_HOME)  \
                                 -Dtemplate.home=$(TEMPLATE_HOME)           -Dgen.src.home=$(PROJECT_DRV) \
                                 -Dproject.soa.client=$(PORJECT_SOA_CLIENT) -Dproject.tc=$(PROJECT_TC) \
                                 -Dplatform=$(PLAT) -Dteamcenter.home=$(TC_ROOT) \
                                 -Dgenerated.stubs="$(GENERATED_STUBS)"

D9SOAWORKAUTOGEN_AUTO_SRC  = $(PROJECT_HOME)$(PS)extensions$(PS)*.xml
D9SOAWORKAUTOGEN_AUTO_TARG = $(PROJECT_DRV)$(PS)D9SoaWork$(PS)D9SoaWorkMap.properties


# ==================================================
# Build targets for Auto-Generation of Service library
# ==================================================
D9SoaWorkAutoGen_all:    $(D9SOAWORKAUTOGEN_AUTO_TARG) 

$(D9SOAWORKAUTOGEN_AUTO_TARG) : $(D9SOAWORKAUTOGEN_AUTO_SRC)
	@echo ________________________________________________________________________________
	@echo Auto Generation of Source for Service Library D9SoaWork
	@$(ANT) -file build/D9SoaWork/D9SoaWorkAutoGen/D9SoaWorkAutoGen-build.xml $(D9SOAWORKAUTOGEN_ANTARGS) all
	@echo ________________________________________________________________________________


	
D9SoaWorkAutoGen_clean: 
	@echo ________________________________________________________________________________
	@echo Cleaning Auto Generated Source of Service Library D9SoaWork
	@$(ANT) -file build/D9SoaWork/D9SoaWorkAutoGen/D9SoaWorkAutoGen-build.xml $(D9SOAWORKAUTOGEN_ANTARGS) clean  
	@echo ________________________________________________________________________________
