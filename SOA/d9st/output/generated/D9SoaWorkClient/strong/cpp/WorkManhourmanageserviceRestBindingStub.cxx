/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2015
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

   Auto-generated source from service interface.
                 DO NOT EDIT

 ==================================================
*/


#include <d9/services/work/ManhourmanageserviceRestBindingStub.hxx>


#include <teamcenter/soa/common/xml/SaxToNodeParser.hxx>
#include <teamcenter/soa/common/xml/XmlUtils.hxx>
#include <teamcenter/soa/client/internal/SessionManager.hxx>
#include <teamcenter/soa/client/internal/ModelManagerImpl.hxx>
#include <teamcenter/soa/client/internal/CdmParser.hxx>
#include <teamcenter/soa/client/internal/CdmStream.hxx>
#include <teamcenter/soa/client/RuntimeException.hxx>
#include <teamcenter/soa/internal/common/Monitor.hxx>
#include <teamcenter/soa/internal/common/PolicyMarshaller.hxx>

D9::Services::Work::ManhourmanageserviceRestBindingStub::ManhourmanageserviceRestBindingStub( Teamcenter::Soa::Client::Sender* restSender, Teamcenter::Soa::Client::ModelManagerImpl* modelManager )
    : m_restSender( restSender ), m_modelManager( modelManager )
{
}



const std::string D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE ="http://d9.com/Schemas/Work/2018-06/ManHourManageService";

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourBillType::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "myRefBR", myRefBR );
    _cdmParser->parseStructMember( "billTypeInternal", billTypeInternal );
    _cdmParser->parseStructMember( "billRateName", billRateName );
}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry::serialize( Teamcenter::Soa::Client::CdmStream* _cdmStream, const std::string& elementName ) const
{


    _cdmStream->writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, elementName );
    _cdmStream->writeStructMember( "myUserName", myUserName );
    _cdmStream->writeStructMember( "myYear", myYear );
    _cdmStream->writeStructMember( "myMonth", myMonth );
    _cdmStream->writeStructMember( "myDay", myDay );
    _cdmStream->writeStructMember( "myDayOfWeek", myDayOfWeek );
    _cdmStream->writeStructMember( "myHoliday", myHoliday );
    _cdmStream->writeStructMember( "workRequired", workRequired );
    _cdmStream->writeStructMember( "myPrjName", myPrjName );
    _cdmStream->writeStructMember( "myPrjId", myPrjId );
    _cdmStream->writeStructMember( "myTaskType", myTaskType );
    _cdmStream->writeStructMember( "myTaskTypeDval", myTaskTypeDval );
    _cdmStream->writeStructMember( "billType", billType );
    _cdmStream->writeStructMember( "myRefBR", myRefBR );
    _cdmStream->writeStructMember( "workingHours", workingHours );
    _cdmStream->writeStructMember( "myRefTE", myRefTE );
    _cdmStream->writeStructMember( "tseStatus", tseStatus );
    _cdmStream->writeStructMember( "myRefMHE", myRefMHE );
    _cdmStream->writeStructMember( "row", (int)row );
    _cdmStream->writeStructMember( "col", (int)col );
    _cdmStream->writeStructMember( "billTypeInternal", billTypeInternal );
    _cdmStream->writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, true );

}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "myUserName", myUserName );
    _cdmParser->parseStructMember( "myYear", myYear );
    _cdmParser->parseStructMember( "myMonth", myMonth );
    _cdmParser->parseStructMember( "myDay", myDay );
    _cdmParser->parseStructMember( "myDayOfWeek", myDayOfWeek );
    _cdmParser->parseStructMember( "myHoliday", myHoliday );
    _cdmParser->parseStructMember( "workRequired", workRequired );
    _cdmParser->parseStructMember( "myPrjName", myPrjName );
    _cdmParser->parseStructMember( "myPrjId", myPrjId );
    _cdmParser->parseStructMember( "myTaskType", myTaskType );
    _cdmParser->parseStructMember( "myTaskTypeDval", myTaskTypeDval );
    _cdmParser->parseStructMember( "billType", billType );
    _cdmParser->parseStructMember( "myRefBR", myRefBR );
    _cdmParser->parseStructMember( "workingHours", workingHours );
    _cdmParser->parseStructMember( "myRefTE", myRefTE );
    _cdmParser->parseStructMember( "tseStatus", tseStatus );
    _cdmParser->parseStructMember( "myRefMHE", myRefMHE );
    _cdmParser->parseStructMember( "row", row );
    _cdmParser->parseStructMember( "col", col );
    _cdmParser->parseStructMember( "billTypeInternal", billTypeInternal );
}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "mheSet", mheSet );
}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourInfo::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "myUserName", myUserName );
    _cdmParser->parseStructMember( "myPosition", myPosition );
    _cdmParser->parseStructMember( "myYear", myYear );
    _cdmParser->parseStructMember( "myMonth", myMonth );
    _cdmParser->parseStructMember( "totalDays", totalDays );
    _cdmParser->parseStructMember( "theMonthTmp", theMonthTmp );
    _cdmParser->parseStructMember( "theProgramSet", theProgramSet );
    _cdmParser->parseStructMember( "isHourlyBasedUser", isHourlyBasedUser );
    _cdmParser->parseStructMember( "isManHourEditable", isManHourEditable );
    _cdmParser->parseStructMember( "theBillTypeSet", theBillTypeSet );
}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourMonthTmp::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "dayOfMonth", dayOfMonth );
    _cdmParser->parseStructMember( "dayOfWeek", dayOfWeek );
    _cdmParser->parseStructMember( "isWeekend", isWeekend );
    _cdmParser->parseStructMember( "isHoliday", isHoliday );
    _cdmParser->parseStructMember( "holidayName", holidayName );
    _cdmParser->parseStructMember( "isWorkRequired", isWorkRequired );
}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram::serialize( Teamcenter::Soa::Client::CdmStream* _cdmStream, const std::string& elementName ) const
{


    _cdmStream->writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, elementName );
    _cdmStream->writeStructMember( "prjId", prjId );
    _cdmStream->writeStructMember( "prjName", prjName );
    _cdmStream->writeStructMember( "tskType", tskType );
    _cdmStream->writeStructMember( "tskTypeDval", tskTypeDval );
    _cdmStream->writeStructMember( "stkTag", stkTag );
    _cdmStream->writeStructMember( "startDay", (int)startDay );
    _cdmStream->writeStructMember( "endDay", (int)endDay );
    _cdmStream->writeStructMember( "prjStartYear", (int)prjStartYear );
    _cdmStream->writeStructMember( "prjStartMonth", (int)prjStartMonth );
    _cdmStream->writeStructMember( "prjStartDay", (int)prjStartDay );
    _cdmStream->writeStructMember( "prjEndYear", (int)prjEndYear );
    _cdmStream->writeStructMember( "prjEndMonth", (int)prjEndMonth );
    _cdmStream->writeStructMember( "prjEndDay", (int)prjEndDay );
    _cdmStream->writeStructMember( "schTag", schTag );
    _cdmStream->writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, true );

}

void D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram::parse( Teamcenter::Soa::Client::CdmParser* _cdmParser )
{

    _cdmParser->parseStructMember( "prjId", prjId );
    _cdmParser->parseStructMember( "prjName", prjName );
    _cdmParser->parseStructMember( "tskType", tskType );
    _cdmParser->parseStructMember( "tskTypeDval", tskTypeDval );
    _cdmParser->parseStructMember( "stkTag", stkTag );
    _cdmParser->parseStructMember( "startDay", startDay );
    _cdmParser->parseStructMember( "endDay", endDay );
    _cdmParser->parseStructMember( "prjStartYear", prjStartYear );
    _cdmParser->parseStructMember( "prjStartMonth", prjStartMonth );
    _cdmParser->parseStructMember( "prjStartDay", prjStartDay );
    _cdmParser->parseStructMember( "prjEndYear", prjEndYear );
    _cdmParser->parseStructMember( "prjEndMonth", prjEndMonth );
    _cdmParser->parseStructMember( "prjEndDay", prjEndDay );
    _cdmParser->parseStructMember( "schTag", schTag );
}



std::string D9::Services::Work::ManhourmanageserviceRestBindingStub::clearManHoursOP( const std::string&  theUser,
        const std::string&  theAction,
        const std::string&  thePara )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::clearManHoursOP" );
    
    std::string _requestXml;
    std::string _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::clearManHoursOP.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "ClearManHoursOPInput" );
        _cdmStream.writeStructMember( "theUser", theUser );
        _cdmStream.writeStructMember( "theAction", theAction );
        _cdmStream.writeStructMember( "thePara", thePara );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, true );


        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "clearManHoursOP", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::clearManHoursOP.parseXML" );                   
        _cdmParser.parseStructMember( "out", _out );

    }
    
    return _out;
}


D9::Services::Work::_2018_06::Manhourmanageservice::ManHourInfo D9::Services::Work::ManhourmanageserviceRestBindingStub::getManHourInfoOP( const std::string&  theUserName,
        const std::string&  theYear,
        const std::string&  theMonth )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::getManHourInfoOP" );
    
    std::string _requestXml;
    D9::Services::Work::_2018_06::Manhourmanageservice::ManHourInfo _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::getManHourInfoOP.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "GetManHourInfoOPInput" );
        _cdmStream.writeStructMember( "theUserName", theUserName );
        _cdmStream.writeStructMember( "theYear", theYear );
        _cdmStream.writeStructMember( "theMonth", theMonth );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, true );


        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "getManHourInfoOP", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::getManHourInfoOP.parseXML" );                   
        _out.parse( &_cdmParser );

    }
    
    return _out;
}


D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet D9::Services::Work::ManhourmanageserviceRestBindingStub::loadOP( const D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry&  manHourFilter )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::loadOP" );
    
    std::string _requestXml;
    D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::loadOP.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "LoadOPInput" );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, false );
    std::string _prefix =      _cdmStream.getNamespacePrefix( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE );

        _cdmStream.writeStructMember( _prefix+"manHourFilter", manHourFilter );
        _cdmStream.writeCloseElement(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "LoadOPInput"  );

        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "loadOP", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::loadOP.parseXML" );                   
        _out.parse( &_cdmParser );

    }
    
    return _out;
}


std::string D9::Services::Work::ManhourmanageserviceRestBindingStub::mheTest( const std::string&  theUser )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::mheTest" );
    
    std::string _requestXml;
    std::string _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::mheTest.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "MheTestInput" );
        _cdmStream.writeStructMember( "theUser", theUser );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, true );


        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "mheTest", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::mheTest.parseXML" );                   
        _cdmParser.parseStructMember( "out", _out );

    }
    
    return _out;
}


D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet D9::Services::Work::ManhourmanageserviceRestBindingStub::reviseOP( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::reviseOP" );
    
    std::string _requestXml;
    D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::reviseOP.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "ReviseOPInput" );
        _cdmStream.writeStructMember( "username", username );
        _cdmStream.writeStructMember( "year", year );
        _cdmStream.writeStructMember( "month", month );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, false );
    std::string _prefix =      _cdmStream.getNamespacePrefix( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE );

        _cdmStream.writeStructMember( _prefix+"manHours", manHours );
        _cdmStream.writeCloseElement(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "ReviseOPInput"  );

        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "reviseOP", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::reviseOP.parseXML" );                   
        _out.parse( &_cdmParser );

    }
    
    return _out;
}


D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet D9::Services::Work::ManhourmanageserviceRestBindingStub::saveOP( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram >& programs )
{
    Teamcenter::Soa::Client::RequestId _scopedRequestId( m_restSender );
    Teamcenter::Soa::Internal::Common::ScopedMonitor _requestMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::saveOP" );
    
    std::string _requestXml;
    D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet _out;

    Teamcenter::Soa::Client::CdmParser _cdmParser( m_modelManager ); 


    {
        Teamcenter::Soa::Internal::Common::ScopedMonitor _serializeMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::saveOP.serializeXML" );

        Teamcenter::Soa::Client::CdmStream _cdmStream;


        _cdmStream.writeOpenElement2( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "SaveOPInput" );
        _cdmStream.writeStructMember( "username", username );
        _cdmStream.writeStructMember( "year", year );
        _cdmStream.writeStructMember( "month", month );
        _cdmStream.writeOpenElementClose(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, false );
    std::string _prefix =      _cdmStream.getNamespacePrefix( D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE );

        _cdmStream.writeStructMember( _prefix+"manHours", manHours );
        _cdmStream.writeStructMember( _prefix+"programs", programs );
        _cdmStream.writeCloseElement(  D9::Services::Work::_2018_06::Manhourmanageservice::XSD_NAMESPACE, "SaveOPInput"  );

        _cdmStream.toString( _requestXml );
    }
    
    m_restSender->invoke( "Work-2018-06-ManHourManageService", "saveOP", _requestXml, _cdmParser );
    
    { 
        Teamcenter::Soa::Internal::Common::ScopedMonitor _parseMonitor( "D9::Services::Work::ManhourmanageserviceRestBindingStub::saveOP.parseXML" );                   
        _out.parse( &_cdmParser );

    }
    
    return _out;
}



using Teamcenter::Soa::Client::SessionManager;
using Teamcenter::Soa::Client::ServiceStub;
using Teamcenter::Soa::Client::ModelManagerImpl;

D9::Services::Work::ManhourmanageserviceService* D9::Services::Work::ManhourmanageserviceService::getService( Teamcenter::Soa::Client::Connection* conn )
{
    SessionManager* smanager = SessionManager::getSessionManager( conn );

    ServiceStub* stub = smanager->getService( "D9::Services::Work::ManhourmanageserviceService" );

    if( stub == 0 )
    {
        stub = (ServiceStub*)new D9::Services::Work::ManhourmanageserviceRestBindingStub( smanager->getSender(), (ModelManagerImpl*)conn->getModelManager() );
        smanager->registerService( "D9::Services::Work::ManhourmanageserviceService", stub );
    }

    D9::Services::Work::ManhourmanageserviceService* ret = dynamic_cast< D9::Services::Work::ManhourmanageserviceService* >( stub );

    if( ret == 0 )
    {
        throw Teamcenter::Soa::Client::RuntimeException(  Teamcenter::Soa::Client::RuntimeException::InternalError, "Could not cast service to correct type" );
    }

    return ret;        
}

