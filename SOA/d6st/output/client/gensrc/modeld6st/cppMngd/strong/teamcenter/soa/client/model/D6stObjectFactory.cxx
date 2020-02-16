/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

  Auto-generated source from Teamcenter Data Model.
                 DO NOT EDIT

 ==================================================
*/



#include <teamcenter/soa/client/model/D6stObjectFactory.hxx>
#include <teamcenter/soa/client/model/StrongObjectFactory.hxx>




using namespace std;
using namespace Teamcenter::Soa::Client;
using namespace Teamcenter::Soa::Client::Model;



map< std::string, ModelObjectFactory* >* D6stObjectFactory::init( )
{
	map< string, ModelObjectFactory* >* factoryMap;
    factoryMap = new map< string, ModelObjectFactory* >;
	
    D6stObjectFactory::init0( *factoryMap );

	
	return factoryMap;
}

