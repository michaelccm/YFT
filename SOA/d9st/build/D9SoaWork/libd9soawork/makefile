# ==================================================
# Copyright 2015
# Siemens Product Lifecycle Management Software Inc.
# All Rights Reserved.
# ==================================================
# ==================================================
#             Auto-generated source
#                 DO NOT EDIT
# ==================================================



# ==================================================
# Define macros for C++ Service implementation
# ==================================================
LIBD9SOAWORK_SRC        = $(PROJECT_HOME)$(PS)src$(PS)server$(PS)D9SoaWork
LIBD9SOAWORK_DRV        = $(PROJECT_DRV)$(PS)D9SoaWork
LIBD9SOAWORK_OBJ        = $(PROJECT_OBJ)$(PS)libd9soawork
LIBD9SOAWORKTYPES_DRV   = $(PROJECT_DRV)$(PS)D9SoaWorkTypes$(PS)cpp
LIBD9SOAWORK_CXXS       = $(LIBD9SOAWORK_SRC)$(PS)*.cxx $(LIBD9SOAWORK_DRV)$(PS)*.cxx
LIBD9SOAWORK_CXX_FLAGS  = $(CXX_FLAGS) -DIPLIB=libd9soawork
LIBD9SOAWORK_LINK_FLAGS = $(LINK_FLAGS)
LIBD9SOAWORK_INCLUDES   = $(CPP_INCLUDES) -I$(LIBD9SOAWORK_SRC) -I$(LIBD9SOAWORK_DRV) -I$(PROJECT_DRV) \
                                  -I$(LIBD9SOAWORKTYPES_DRV) -I$(TC_ROOT)$(PS)include -I$(TC_ROOT)$(PS)include_cpp \
                                  -I$(TOOLBOX_HOME)$(PS)TCGATEWAYSRC \
                                  
LIBD9SOAWORK_LIB_PATH   = $(LP)$(PROJECT_BIN)  $(LIB_PATH)
LIBD9SOAWORK_LIBS       = $(LIB_PREFIX)cxpom$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)itk$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)sa$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)tccore$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)tc$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)tccoreext$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)metaframework$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)bom$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)epm$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)grm$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)property$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)qry$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)lov$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)schmgt$(LIB_SUFFIX) \
                                  $(SYSLIBS)  \
                                  $(LIB_PREFIX)tcsoacommon$(LIB_SUFFIX)  $(LIB_PREFIX)tcsoaserver$(LIB_SUFFIX)   $(LIB_PREFIX)pom$(LIB_SUFFIX)  \
                                  $(LIB_PREFIX)itk$(LIB_SUFFIX)          $(LIB_PREFIX)fclasses$(LIB_SUFFIX)      $(LIB_PREFIX)metaframework$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)tccore$(LIB_SUFFIX)       $(LIB_PREFIX)tc$(LIB_SUFFIX)            $(LIB_PREFIX)cxpom$(LIB_SUFFIX) \
                                  $(LIB_PREFIX)base_utils$(LIB_SUFFIX)   $(LIB_PREFIX)mld$(LIB_SUFFIX)
LIBD9SOAWORK_LD_FLAGS   = $(LIBD9SOAWORK_LINK_FLAGS) $(LIBD9SOAWORK_LIB_PATH)
LIBD9SOAWORK_TARGET     = $(PROJECT_BIN)$(PS)libd9soawork



libd9soawork_all:   $(LIBD9SOAWORK_OBJ)  $(LIBD9SOAWORK_TARGET).$(LIB_EXT)


$(LIBD9SOAWORK_OBJ) :
	@echo Creating: $(LIBD9SOAWORK_OBJ)
	-@$(MAKE_DIR) $(LIBD9SOAWORK_OBJ)

$(LIBD9SOAWORK_CXXS) :


# ==================================================
# Windows Build Target
# ==================================================
$(LIBD9SOAWORK_TARGET).dll : $(LIBD9SOAWORK_CXXS)
	@echo ________________________________________________________________________________
	@echo Building C++ Server libd9soawork
	@echo Compiling Source...
	@$(COMPILE)  $(LIBD9SOAWORK_CXX_FLAGS) $(LIBD9SOAWORK_INCLUDES) -c $(**)   $(CARG_OUT)$(LIBD9SOAWORK_OBJ)$(PS)
	@echo.
	@$(LINK) $(LIBD9SOAWORK_LD_FLAGS) $(LIBD9SOAWORK_OBJ)$(PS)*.$(OBJ_EXT)  $(LIBD9SOAWORK_LIBS)  $(LARG_OUT)$(LIBD9SOAWORK_TARGET).dll
	$(COPY_FILE)  $(PROJECT_BIN)$(PS)libd9soawork.dll $(PROJECT_TC)$(PS)bin
	$(COPY_FILE)  $(PROJECT_BIN)$(PS)libd9soawork.lib $(PROJECT_TC)$(PS)lib
	@echo ________________________________________________________________________________


	
# ==================================================
# UNIX Build Target
# ==================================================
$(LIBD9SOAWORK_TARGET).so : $(LIBD9SOAWORK_CXXS)
	@echo ________________________________________________________________________________
	@echo Building C++ Server libd9soawork
	@# The compile output goes to the current directory (project root), so remove any .o files
	@# that may have been left here from previous failed builds. Then after the compile move all .o
	@# files to the the desired obj folder.
	@-rm *.$(OBJ_EXT)
	@echo Compiling $(LIBD9SOAWORK_CXXS)
	@$(COMPILE)  $(LIBD9SOAWORK_CXX_FLAGS) $(LIBD9SOAWORK_INCLUDES) -c $(LIBD9SOAWORK_CXXS) 
	@mv *.$(OBJ_EXT) $(LIBD9SOAWORK_OBJ)
	@echo Linking $(LIBD9SOAWORK_TARGET).so
	@$(LINK) $(LIBD9SOAWORK_LD_FLAGS) $(LIBD9SOAWORK_OBJ)$(PS)*.$(OBJ_EXT)  $(LIBD9SOAWORK_LIBS)  $(LARG_OUT) $(LIBD9SOAWORK_TARGET).so
	$(COPY_FILE)  $(PROJECT_BIN)$(PS)libd9soawork.so $(PROJECT_TC)$(PS)lib
	@echo ________________________________________________________________________________


libd9soawork_clean:
	@echo ________________________________________________________________________________
	@echo Cleaning C++ Server libd9soawork
	-$(DELETE_DIR)  $(LIBD9SOAWORK_OBJ)
	-$(DELETE_FILE) $(PROJECT_BIN)$(PS)libd9soawork.*	
	-$(DELETE_FILE) $(PROJECT_TC)$(PS)bin$(PS)libd9soawork.dll
	-$(DELETE_FILE) $(PROJECT_TC)$(PS)lib$(PS)libd9soawork.lib	
	-$(DELETE_FILE) $(PROJECT_TC)$(PS)lib$(PS)libd9soawork.so	
	@echo ________________________________________________________________________________

