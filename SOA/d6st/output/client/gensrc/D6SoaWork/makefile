MSBUILDPATH=%WinDir%/Microsoft.NET/Framework/v2.0.50727
ANTPATH=/siemens/afuz1/dev/soa_client/toolbox/Ant/bin
MODELTEMPLATENAME=d6st
SERVICELIBNAME=D6SoaWork
GENSRC=/siemens/afuz1/dev/workspace/d6st/output/client

SERVERGENSRC=/siemens/afuz1/dev/workspace/d6st/output/server/gensrc
CLIENTGENSRC=/siemens/afuz1/dev/workspace/d6st/output/client/gensrc
TYPESGENSRC=/siemens/afuz1/dev/workspace/d6st/output/types/gensrc
SERVERLIB=/siemens/afuz1/dev/workspace/d6st/output/server/lib
CLIENTLIB=/siemens/afuz1/dev/workspace/d6st/output/client/lib
TYPESLIB=/siemens/afuz1/dev/workspace/d6st/output/types/lib
SERVEROBJ=/siemens/afuz1/dev/workspace/d6st/output/server/obj
CLIENTOBJ=/siemens/afuz1/dev/workspace/d6st/output/client/obj
TYPESOBJ=/siemens/afuz1/dev/workspace/d6st/output/types/obj


all : SoaTaskD6SoaWork

SoaTaskD6SoaWork : JavaClientD6SoaWork 

JavaClientD6SoaWork: JavaClientD6SoaWorkTypes JavaClientD6SoaWorkStrong JavaClientD6SoaWorkRac
JavaClientD6SoaWorkTypes: JavaClientD6SoaWorkTypesMsg
	$(ANTPATH)/ant  -file /siemens/afuz1/dev/workspace/d6st/output/client/gensrc/D6SoaWork/javaclientbuild.xml BuildTypes
	if test -s "/siemens/afuz1/dev/workspace/d6st/output/types/jars/D6SoaWorkTypes.jar"; then echo "Jar file Build successful"; else echo "Jar file did not get created"; exit 1; fi


JavaClientD6SoaWorkTypesMsg:
	@echo _________________________________________
	@echo BUILDING SOA Java Client Types bindings
	@echo _________________________________________

JavaClientD6SoaWorkStrong: JavaClientD6SoaWorkStrongMsg
	$(ANTPATH)/ant  -file /siemens/afuz1/dev/workspace/d6st/output/client/gensrc/D6SoaWork/javaclientbuild.xml BuildStrong
	if test -s "/siemens/afuz1/dev/workspace/d6st/output/client/jars/D6SoaWorkStrong.jar"; then echo "Jar file Build successful"; else echo "Jar file did not get created"; exit 1; fi


JavaClientD6SoaWorkStrongMsg:
	@echo _________________________________________
	@echo BUILDING SOA Java Client Strong bindings
	@echo _________________________________________

JavaClientD6SoaWorkRac: JavaClientD6SoaWorkRacMsg
	$(ANTPATH)/ant  -file /siemens/afuz1/dev/workspace/d6st/output/client/gensrc/D6SoaWork/javaclientbuild.xml BuildRac
	if test -s "/siemens/afuz1/dev/workspace/d6st/output/client/jars/D6SoaWorkRac.jar"; then echo "Jar file Build successful"; else echo "Jar file did not get created"; exit 1; fi


JavaClientD6SoaWorkRacMsg:
	@echo _________________________________________
	@echo BUILDING SOA Java Client RAC bindings
	@echo _________________________________________




include /siemens/afuz1/dev/workspace/d6st/output/types/gensrc/D6SoaWork/makefile


