# nmake makefile



PLATFORMNAME=lnx64

TC_ROOT=/siemens/afuz1/teamcenter9
TC_DATA=/siemens/afuz1/teamcenter9

SRC=/siemens/afuz1/dev/workspace/d6st/src/server
GENSRC=/siemens/afuz1/dev/workspace/d6st/output/server
OBJ=/siemens/afuz1/dev/workspace/d6st/output/server/obj
LIB=/siemens/afuz1/dev/workspace/d6st/output/server/lib
SOA_CLIENT_KIT=/siemens/afuz1/dev/soa_client
MSVCLIBPATH=/usr/bin/lib
CXXFLAGS=-c -fPIC -m64 -DSTEP -DAP203 -DAP214 -D_XOPEN_SOURCE=600 -DPOSIX  -fmessage-length=0 -Wformat=2 -Wmissing-format-attribute -DD6SoaWork_EXPORTS
LINKFLAGS=-shared
ADD_LIB1= 
#ADD_INCLUDE=-I/siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/cpp
SERVERLIBS=/siemens/afuz1/dev/workspace/d6st/output/server/lib
COMPILECOMMOND=/usr/bin/g++
LINKCOMMOND=/usr/bin/g++
MODELTEMPLATENAME=d6st

SOA_KIT_LIBPATH=$(SOA_CLIENT_KIT)/cpp/libs/$(PLATFORMNAME)
XERCESPATH=$(SOA_CLIENT_KIT)/toolbox/xml4c

# MAKEFILEDIR=$(GENSRC)/services/D6SoaWork
MAKEFILEDIR=/siemens/afuz1/dev/workspace/d6st/output/server/gensrc{FILE_SEP}D6SoaWork

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
libd6soaworkI3=-I$(SOA_CLIENT_KIT)/cpp/includes/cpp/include
I4=-I$(SOA_CLIENT_KIT)/toolbox/TCGATEWAYSRC
libd6soaworkI5=-I/siemens/afuz1/dev/workspace/d6st/output/server/gensrc/D6SoaWork
I6=-I$(XERCESPATH)/include
libd6soaworkI7=-I-I/siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/cpp
libd6soaworkI8=-I$(SRC)/D6SoaWork
libd6soaworkI9=-I/siemens/afuz1/dev/workspace/d6st/output/server/gensrc/D6SoaWork
libd6soaworkI10=-I$/siemens/afuz1/dev/workspace/d6st/output/server/gensrc
libd6soaworkI11=-I/siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/cpp

libd6soaworkINCLUDES = $(libd6soaworkI11) $(libd6soaworkI8) $(libd6soaworkI9) $(libd6soaworkI10) $(libd6soaworkI7) $(libd6soaworkI5) $(I4) $(I6) $(I1) $(I2) $(libd6soaworkI3)

# Compile
# Set all compile arguments.

CXX_FLAGS= $(CXXFLAGS)

# Link
# Set all link arguments.

LD_FLAGS = $(LINKFLAGS)  $(SOAL1) $(SOAL2) $(SOAL3) $(SOAL4) $(SOAL5) $(SOAL6)
SYSLIBS =  -ldl -lpthread -lm -lc

LINKCMDlibd6soawork = $(LD_FLAGS) $(SERVEROBJ)/libd6soawork/*.o $(SYSLIBS)  -ltcsoacommon  -ltcsoaserver -ltcgateway -ltcserverutil -lpom -litk -lfclasses -lmetaframework -ltccore -ltc -lcxpom -lbase_utils -lmld -ltc -ltccore -ltccoreext -lsa -lproperty -lqry -lepm -lgrm -lbom -llov -lschmgt  -ld6soaworktypes \
	 

all : soaALL

soaALL : libd6soaworkALL libd6soaworkCOPYTYPELIB

libd6soaworkALL : libd6soaworkCLEAN libd6soaworkAll_message libd6soawork.so

libd6soaworkAll_message :
	@echo _______________________________________________________________
	@echo BUILDING C++ libd6soawork Server Library
	@echo _______________________________________________________________

libd6soawork.so : libd6soaworkcreateDir libd6soaworkcxxObj libd6soaworkmoveObj
	$(LINKCOMMOND) $(LINKCMDlibd6soawork) -o $(LIB)/libd6soawork.so

libd6soaworkcxxObj :
	$(COMPILECOMMOND) -DIPLIB=libd6soawork $(CXX_FLAGS) $(libd6soaworkINCLUDES) /siemens/afuz1/dev/workspace/d6st/output/server/gensrc/D6SoaWork/*.cxx $(SRC)/D6SoaWork/*.cxx

libd6soaworkcreateDir :
	-@mkdir -p  "/siemens/afuz1/dev/workspace/d6st/output/server/obj/libd6soawork"
	-@mkdir -p  "/siemens/afuz1/dev/workspace/d6st/output/server/lib"
    
libd6soaworkmoveObj :
	-@mv *.o "/siemens/afuz1/dev/workspace/d6st/output/server/obj/libd6soawork/."
    
libd6soaworkCLEAN : libd6soaworkClean_message libd6soaworkClean_obj libd6soaworkClean_lib

libd6soaworkClean_message :
	@echo CLEANING Build Project directory /siemens/afuz1/dev/workspace/d6st/output/server/obj/libd6soawork /siemens/afuz1/dev/workspace/d6st/output/server/lib

libd6soaworkClean_obj :
	-@rm -f  "/siemens/afuz1/dev/workspace/d6st/output/server/obj/libd6soawork"/*.o

libd6soaworkClean_lib :
	-@rm -f  "/siemens/afuz1/dev/workspace/d6st/output/server/lib"/libd6soawork.*


libd6soaworkCOPYTYPELIB : libd6soaworkCOPYTYPELIBMSG
	-@cp "/siemens/afuz1/dev/workspace/d6st/output/types/lib/libd6soaworktypes.so" "/siemens/afuz1/dev/workspace/d6st/output/server/lib/libd6soaworktypes.so"

libd6soaworkCOPYTYPELIBMSG :
	@echo Copy libd6soaworktypes.so from /siemens/afuz1/dev/workspace/d6st/output/types/lib to /siemens/afuz1/dev/workspace/d6st/output/server/lib/libd6soaworktypes.so
