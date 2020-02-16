/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2014
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

   Auto-generated source from service interface.
                 DO NOT EDIT

 ==================================================
*/


#include <unidefs.h>
#if defined(SUN)
#    include <unistd.h>
#endif

#include<ManHourManageService1806IiopSkeleton.hxx>


#include <teamcenter/soa/internal/common/PolicyMarshaller.hxx>
#include <teamcenter/soa/internal/server/PolicyManager.hxx>
#include <teamcenter/soa/internal/server/PerServicePropertyPolicy.hxx>
#include <teamcenter/soa/internal/server/SdmParser.hxx>
#include <teamcenter/soa/internal/server/SdmStream.hxx>

using namespace std;
using namespace Teamcenter::Soa::Server;
using namespace Teamcenter::Soa::Internal::Server;
using namespace Teamcenter::Soa::Internal::Common;
using namespace Teamcenter::Soa::Common::Exceptions;


namespace D9
{
    namespace Services
    {
    
        namespace Work
        {
             namespace _2018_06
             {




    ManHourManageServiceIiopSkeleton::ManHourManageServiceIiopSkeleton():
        IiopSkeleton()
    {
        m_serviceName = "ManHourManageService";
    
       _svcMap[ "clearManHoursOP" ]   = clearManHoursOP;
       _svcMap[ "getManHourInfoOP" ]   = getManHourInfoOP;
       _svcMap[ "loadOP" ]   = loadOP;
       _svcMap[ "mheTest" ]   = mheTest;
       _svcMap[ "reviseOP" ]   = reviseOP;
       _svcMap[ "saveOP" ]   = saveOP;

    }
    
    ManHourManageServiceIiopSkeleton::~ManHourManageServiceIiopSkeleton()
    {
    	// If the implementing class did not implement the ServicePolicy
    	// then delete it, since it was allocated here in the skeleton
	 	Teamcenter::Soa::Server::ServicePolicy* sp = dynamic_cast<Teamcenter::Soa::Server::ServicePolicy *>(_service);
    	if(sp == NULL)
    	{
    		delete _servicePolicy;
    	}
        delete _service;
    }



     void ManHourManageServiceIiopSkeleton::initialize()
    {
   	// If the impl class has not implemented the ServicePolicy interface
    	// Create an instance of the default ServicePolicy
	 	_servicePolicy = dynamic_cast<Teamcenter::Soa::Server::ServicePolicy *>(_service);
    	if(_servicePolicy == NULL)
    	{
    		_servicePolicy = new Teamcenter::Soa::Server::ServicePolicy;
    	}
    }




    static D9::Soa::Work::_2018_06::ManHourManageServiceImpl* _service;
	static Teamcenter::Soa::Server::ServicePolicy*  	 _servicePolicy;


    void ManHourManageServiceIiopSkeleton::clearManHoursOP( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::clearManHoursOP" );

        std::string _contentType;
        std::string  theUser;
        std::string  theAction;
        std::string  thePara;
        std::string _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::clearManHoursOP - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "theUser", theUser );
            _sdmParser.parseStructMember( "theAction", theAction );
            _sdmParser.parseStructMember( "thePara", thePara );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::clearManHoursOP" );
            _out = _service->clearManHoursOP( theUser, theAction, thePara );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::clearManHoursOP - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _sdmStream.writeBasicOut(  "ClearManHoursOPOutput", D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, _out );
          
        }
    }

    void ManHourManageServiceIiopSkeleton::getManHourInfoOP( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::getManHourInfoOP" );

        std::string _contentType;
        std::string  theUserName;
        std::string  theYear;
        std::string  theMonth;
        D9::Soa::Work::_2018_06::ManHourManageService::ManHourInfo _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::getManHourInfoOP - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "theUserName", theUserName );
            _sdmParser.parseStructMember( "theYear", theYear );
            _sdmParser.parseStructMember( "theMonth", theMonth );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::getManHourInfoOP" );
            _out = _service->getManHourInfoOP( theUserName, theYear, theMonth );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::getManHourInfoOP - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _out.serialize( &_sdmStream );          
        }
    }

    void ManHourManageServiceIiopSkeleton::loadOP( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::loadOP" );

        std::string _contentType;
        D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntry  manHourFilter;
        D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntrySet _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::loadOP - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "manHourFilter", manHourFilter );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::loadOP" );
            _out = _service->loadOP( manHourFilter );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::loadOP - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _out.serialize( &_sdmStream );          
        }
    }

    void ManHourManageServiceIiopSkeleton::mheTest( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::mheTest" );

        std::string _contentType;
        std::string  theUser;
        std::string _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::mheTest - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "theUser", theUser );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::mheTest" );
            _out = _service->mheTest( theUser );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::mheTest - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _sdmStream.writeBasicOut(  "MheTestOutput", D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, _out );
          
        }
    }

    void ManHourManageServiceIiopSkeleton::reviseOP( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::reviseOP" );

        std::string _contentType;
        std::string  username;
        std::string  year;
        std::string  month;
        std::vector< D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntry >  manHours;
        D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntrySet _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::reviseOP - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "username", username );
            _sdmParser.parseStructMember( "year", year );
            _sdmParser.parseStructMember( "month", month );
            _sdmParser.parseStructMember( "manHours", manHours );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::reviseOP" );
            _out = _service->reviseOP( username, year, month, manHours );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::reviseOP - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _out.serialize( &_sdmStream );          
        }
    }

    void ManHourManageServiceIiopSkeleton::saveOP( const std::string& xmlOrJsonDoc, Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse )
    {
        ScopedJournal _journalSkeleton( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::saveOP" );

        std::string _contentType;
        std::string  username;
        std::string  year;
        std::string  month;
        std::vector< D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntry >  manHours;
        std::vector< D9::Soa::Work::_2018_06::ManHourManageService::ManHourProgram >  programs;
        D9::Soa::Work::_2018_06::ManHourManageService::ManHourEntrySet _out;

        //  ========== Parse the input XML or JSON document to the local input arguments ==========
        {
            ScopedJournal _journalParse( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::saveOP - Parse input document" );
            Teamcenter::Soa::Internal::Server::SdmParser _sdmParser( xmlOrJsonDoc );
            _contentType = _sdmParser.getDocumentType();
            _sdmParser.parseStructMember( "username", username );
            _sdmParser.parseStructMember( "year", year );
            _sdmParser.parseStructMember( "month", month );
            _sdmParser.parseStructMember( "manHours", manHours );
            _sdmParser.parseStructMember( "programs", programs );
        }        


        //  ===================== Call the service operation implementation  ======================
        {
            ScopedJournal journalExecute( "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceImpl::saveOP" );
            _out = _service->saveOP( username, year, month, manHours, programs );
        }

        //  ================== Serialize the response to a XML or JSON document  ==================
        {
            ScopedJournal _journalSerialize(  "D9::Soa::Work::_2018_06::ManHourManageService::ManHourManageServiceIiopSkeleton::saveOP - Serialize output document" );
            Teamcenter::Soa::Internal::Server::SdmStream _sdmStream(  _contentType, gatewayResponse.getBodyOutputStream() );
            _out.serialize( &_sdmStream );          
        }
    }




D9::Soa::Work::_2018_06::ManHourManageServiceImpl* ManHourManageServiceIiopSkeleton::_service = new D9::Soa::Work::_2018_06::ManHourManageServiceImpl;
Teamcenter::Soa::Server::ServicePolicy*  	  ManHourManageServiceIiopSkeleton::_servicePolicy = NULL;
static Teamcenter::Soa::Internal::Gateway::T2LService* me_ManHourManageService = Teamcenter::Soa::Internal::Gateway::TcServiceManager::instance().registerService( "Work-2018-06-ManHourManageService", new ManHourManageServiceIiopSkeleton );

 //register the service for getServiceList()
 static std::string registeredServiceOnStartup_ManHourManageService = Teamcenter::Soa::Internal::Server::ServiceManager::instance().registerService("Work-2018-06-ManHourManageService");

}}}}    // End Namespace

