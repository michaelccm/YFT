MSBUILDPATH=%WinDir%/Microsoft.NET/Framework/v2.0.50727
ANTPATH=/siemens/afuz1/dev/soa_client/toolbox/Ant/bin
MODELTEMPLATENAME=d6st

SERVERGENSRC=/siemens/afuz1/dev/workspace/d6st/output/server/gensrc
CLIENTGENSRC=/siemens/afuz1/dev/workspace/d6st/output/client/gensrc
TYPESGENSRC=/siemens/afuz1/dev/workspace/d6st/output/types/gensrc
SERVERLIB=/siemens/afuz1/dev/workspace/d6st/output/server/lib
CLIENTLIB=/siemens/afuz1/dev/workspace/d6st/output/client/lib
TYPESLIB=/siemens/afuz1/dev/workspace/d6st/output/types/lib
SERVEROBJ=/siemens/afuz1/dev/workspace/d6st/output/server/obj
CLIENTOBJ=/siemens/afuz1/dev/workspace/d6st/output/client/obj
TYPESOBJ=/siemens/afuz1/dev/workspace/d6st/output/types/obj


ALL : SoaJ2EEAll


SoaJ2EEAll: SoaJ2EEAllMsg
	$(ANTPATH)/ant  -file $(CLIENTGENSRC)/buildj2ee.xml

SoaJ2EEAllMsg:
	@echo ___________________________________________
	@echo BUILDING AXIS Artifacts for J2EE web tier
	@echo ___________________________________________



