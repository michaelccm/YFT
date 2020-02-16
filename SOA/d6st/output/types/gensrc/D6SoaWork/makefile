# nmake makefile



PLATFORMNAME=lnx64

TC_ROOT=/siemens/afuz1/teamcenter9
TC_DATA=/siemens/afuz1/teamcenter9

SRC=/siemens/afuz1/dev/workspace/d6st/src/server
GENSRC=/siemens/afuz1/dev/workspace/d6st/output/types
OBJ=/siemens/afuz1/dev/workspace/d6st/output/types/obj
LIB=/siemens/afuz1/dev/workspace/d6st/output/types/lib
SOA_CLIENT_KIT=/siemens/afuz1/dev/soa_client
MSVCLIBPATH=/usr/bin/lib
CXXFLAGS=-c -fPIC -m64 -DSTEP -DAP203 -DAP214 -D_XOPEN_SOURCE=600 -DPOSIX  -fmessage-length=0 -Wformat=2 -Wmissing-format-attribute -DD6SoaWorkTYPES_EXPORTS
LINKFLAGS=-shared
ADD_INCLUDE= 
COMPILECOMMOND=/usr/bin/g++
LINKCOMMOND=/usr/bin/g++
MODELTEMPLATENAME=d6st

SOA_KIT_LIBPATH=$(SOA_CLIENT_KIT)/cpp/libs/$(PLATFORMNAME)
XERCESPATH=$(SOA_CLIENT_KIT)/toolbox/xml4c

# MAKEFILEDIR=$(GENSRC)/services/D6SoaWork
MAKEFILEDIR=$(GENSRC)

# @set TC_ROOT=$(TC_ROOT)
# @$(TC_DATA)/tc_profilevars




SOAL1=-L/siemens/afuz1/teamcenter9/lib
SOAL2=-L/siemens/afuz1/dev/soa_client/cpp/libs/lnx64
SOAL3=-L/siemens/afuz1/dev/soa_client/toolbox/xml4c/3.2.0.0/lib
SOAL4=-L/siemens/afuz1/dev/workspace/d6st/output/types/lib
SOAL5=-L/siemens/afuz1/dev/workspace/d6st/output/server/lib
SOAL6=-L/siemens/afuz1/dev/workspace/d6st/output/client/lib


I1=-I$(TC_ROOT)/include
I2=-I$(TC_ROOT)/include_cpp
libd6soaworktypesI3=-I$(SOA_CLIENT_KIT)/cpp/includes/cpp/include
libd6soaworktypesI4=-I/siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/cpp
I5=-I$(XERCESPATH)/include
libd6soaworktypesI6= 

libd6soaworktypesINCLUDES = $(libd6soaworktypesI6) $(libd6soaworktypesI4) $(I1) $(I2) $(I5) $(libd6soaworktypesI3)

# Compile
# Set all compile arguments.

CXX_FLAGS= $(CXXFLAGS)

# Link
# Set all link arguments.

LD_FLAGS = $(LINKFLAGS)  $(SOAL1) $(SOAL2) $(SOAL3) $(SOAL4) $(SOAL5) $(SOAL6)
SYSLIBS =  -ldl -lpthread -lm -lc

LINKCMDlibd6soaworktypes = $(LD_FLAGS) $(TYPESOBJ)/libd6soaworktypes/*.o $(SYSLIBS)  -ltcsoacommon \
	 

all : soaALL

soaALL : libd6soaworktypesALL

libd6soaworktypesALL : libd6soaworktypesCLEAN libd6soaworktypesAll_message libd6soaworktypes.so

libd6soaworktypesAll_message :
	@echo _______________________________________________________________
	@echo BUILDING C++ libd6soaworktypes Types Library
	@echo _______________________________________________________________

libd6soaworktypes.so : libd6soaworktypescreateDir libd6soaworktypescxxObj libd6soaworktypesmoveObj
	$(LINKCOMMOND) $(LINKCMDlibd6soaworktypes) -o /siemens/afuz1/dev/workspace/d6st/output/types/lib/libd6soaworktypes.so

libd6soaworktypescxxObj :
	$(COMPILECOMMOND) -DIPLIB=libd6soaworktypes $(CXX_FLAGS) $(libd6soaworktypesINCLUDES) /siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/cpp/*.cxx

libd6soaworktypescreateDir :
	-@mkdir -p  "/siemens/afuz1/dev/workspace/d6st/output/types/obj/libd6soaworktypes"
	-@mkdir -p  "/siemens/afuz1/dev/workspace/d6st/output/types/lib"
    
libd6soaworktypesmoveObj :
	-@mv *.o "/siemens/afuz1/dev/workspace/d6st/output/types/obj/libd6soaworktypes/."
    
libd6soaworktypesCLEAN : libd6soaworktypesClean_message libd6soaworktypesClean_obj libd6soaworktypesClean_lib

libd6soaworktypesClean_message :
	@echo CLEANING Build Project directory /siemens/afuz1/dev/workspace/d6st/output/types/obj/libd6soaworktypes /siemens/afuz1/dev/workspace/d6st/output/types/lib

libd6soaworktypesClean_obj :
	-@rm -f  "/siemens/afuz1/dev/workspace/d6st/output/types/obj/libd6soaworktypes"/*.o

libd6soaworktypesClean_lib :
	-@rm -f  "/siemens/afuz1/dev/workspace/d6st/output/types/lib"/libd6soaworktypes.*
