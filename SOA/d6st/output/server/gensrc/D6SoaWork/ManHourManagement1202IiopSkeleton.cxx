/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
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

#include<ManHourManagement1202IiopSkeleton.hxx>


#include <mld/journal/journal.h>
#include <teamcenter/soa/internal/common/Monitor.hxx>
#include <teamcenter/soa/internal/common/PolicyMarshaller.hxx>
#include <teamcenter/soa/internal/server/PolicyManager.hxx>
#include <teamcenter/soa/internal/server/PerServicePropertyPolicy.hxx>


using namespace std;
using namespace Teamcenter::Soa::Server;
using namespace Teamcenter::Soa::Internal::Server;
using namespace Teamcenter::Soa::Internal::Common;
using namespace Teamcenter::Soa::Common::Xml;
using namespace Teamcenter::Soa::Common::Exceptions;


namespace Teamcenter
{
    namespace Services
    {
    
        namespace Work
        {
             namespace _2012_02
             {




    ManHourManagementIiopSkeleton::ManHourManagementIiopSkeleton()
    {
        m_serviceName = "ManHourManagement";
    
       _svcMap[ "getManHourInfo" ]   = getManHourInfo;
       _svcMap[ "clearManHours" ]   = clearManHours;
       _svcMap[ "load" ]   = load;
       _svcMap[ "save" ]   = save;
       _svcMap[ "revise" ]   = revise;
       _svcMap[ "mheTest" ]   = mheTest;

    }

    ManHourManagementIiopSkeleton::~ManHourManagementIiopSkeleton()
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



     void ManHourManagementIiopSkeleton::initialize()
    {
   	// If the impl class has not implemented the ServicePolicy interface
    	// Create an instance of the default ServicePolicy
	 	_servicePolicy = dynamic_cast<Teamcenter::Soa::Server::ServicePolicy *>(_service);
    	if(_servicePolicy == NULL)
    	{
    		_servicePolicy = new Teamcenter::Soa::Server::ServicePolicy;
    	}
    }


Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry local , Teamcenter::Soa::Server::ServiceData*  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();
     wire->setMyUserName( local.myUserName ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyYear( local.myYear ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyMonth( local.myMonth ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyDay( local.myDay ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyDayOfWeek( local.myDayOfWeek ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyHoliday( local.myHoliday ); //BASIC_OUT_ASSIGNMENT2
     wire->setWorkRequired( local.workRequired ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyPrjName( local.myPrjName ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyPrjId( local.myPrjId ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyTaskType( local.myTaskType ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyTaskTypeDval( local.myTaskTypeDval ); //BASIC_OUT_ASSIGNMENT2
     wire->setBillType( local.billType ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyRefBR( local.myRefBR ); //BASIC_OUT_ASSIGNMENT2
     wire->setWorkingHours( local.workingHours ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyRefTE( local.myRefTE ); //BASIC_OUT_ASSIGNMENT2
     wire->setTseStatus( local.tseStatus ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyRefMHE( local.myRefMHE ); //BASIC_OUT_ASSIGNMENT2
     wire->setRow( local.row ); //BASIC_OUT_ASSIGNMENT2
     wire->setCol( local.col ); //BASIC_OUT_ASSIGNMENT2
     wire->setBillTypeInternal( local.billTypeInternal ); //BASIC_OUT_ASSIGNMENT2

     return wire;
}
 D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry   ManHourManagementIiopSkeleton::toLocal( Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> wire )
{
     D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry local;
     local.myUserName = wire->getMyUserName(); //SIMPLE_ASSIGNMENT2
     local.myYear = wire->getMyYear(); //SIMPLE_ASSIGNMENT2
     local.myMonth = wire->getMyMonth(); //SIMPLE_ASSIGNMENT2
     local.myDay = wire->getMyDay(); //SIMPLE_ASSIGNMENT2
     local.myDayOfWeek = wire->getMyDayOfWeek(); //SIMPLE_ASSIGNMENT2
     local.myHoliday = wire->getMyHoliday(); //SIMPLE_ASSIGNMENT2
     local.workRequired = wire->getWorkRequired(); //SIMPLE_ASSIGNMENT2
     local.myPrjName = wire->getMyPrjName(); //SIMPLE_ASSIGNMENT2
     local.myPrjId = wire->getMyPrjId(); //SIMPLE_ASSIGNMENT2
     local.myTaskType = wire->getMyTaskType(); //SIMPLE_ASSIGNMENT2
     local.myTaskTypeDval = wire->getMyTaskTypeDval(); //SIMPLE_ASSIGNMENT2
     local.billType = wire->getBillType(); //SIMPLE_ASSIGNMENT2
     local.myRefBR = wire->getMyRefBR(); //SIMPLE_ASSIGNMENT2
     local.workingHours = wire->getWorkingHours(); //SIMPLE_ASSIGNMENT2
     local.myRefTE = wire->getMyRefTE(); //SIMPLE_ASSIGNMENT2
     local.tseStatus = wire->getTseStatus(); //SIMPLE_ASSIGNMENT2
     local.myRefMHE = wire->getMyRefMHE(); //SIMPLE_ASSIGNMENT2
     local.row = wire->getRow(); //SIMPLE_ASSIGNMENT2
     local.col = wire->getCol(); //SIMPLE_ASSIGNMENT2
     local.billTypeInternal = wire->getBillTypeInternal(); //SIMPLE_ASSIGNMENT2

     return local;
}
Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourInfo local , Teamcenter::Soa::Server::ServiceData* pServiceData  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo();
     wire->setMyUserName( local.myUserName ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyPosition( local.myPosition ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyYear( local.myYear ); //BASIC_OUT_ASSIGNMENT2
     wire->setMyMonth( local.myMonth ); //BASIC_OUT_ASSIGNMENT2
     wire->setTotalDays( local.totalDays ); //BASIC_OUT_ASSIGNMENT2
     D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmpArray theMonthTmpArray; //VECTOR_STRUCT_OUT2
     for(size_t i = 0; i<local.theMonthTmp.size(); i++)
     {
          theMonthTmpArray.push_back( toWire( local.theMonthTmp[i], pServiceData ));
     }
     wire->setTheMonthTmpArray( theMonthTmpArray );
     D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgramArray theProgramSetArray; //VECTOR_STRUCT_OUT2
     for(size_t i = 0; i<local.theProgramSet.size(); i++)
     {
          theProgramSetArray.push_back( toWire( local.theProgramSet[i], pServiceData ));
     }
     wire->setTheProgramSetArray( theProgramSetArray );
     wire->setIsHourlyBasedUser( local.isHourlyBasedUser ); //BASIC_OUT_ASSIGNMENT2
     wire->setIsManHourEditable( local.isManHourEditable ); //BASIC_OUT_ASSIGNMENT2
     D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillTypeArray theBillTypeSetArray; //VECTOR_STRUCT_OUT2
     for(size_t i = 0; i<local.theBillTypeSet.size(); i++)
     {
          theBillTypeSetArray.push_back( toWire( local.theBillTypeSet[i], pServiceData ));
     }
     wire->setTheBillTypeSetArray( theBillTypeSetArray );

     return wire;
}
Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourMonthTmp local , Teamcenter::Soa::Server::ServiceData*  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp();
     wire->setDayOfMonth( local.dayOfMonth ); //BASIC_OUT_ASSIGNMENT2
     wire->setDayOfWeek( local.dayOfWeek ); //BASIC_OUT_ASSIGNMENT2
     wire->setIsWeekend( local.isWeekend ); //BASIC_OUT_ASSIGNMENT2
     wire->setIsHoliday( local.isHoliday ); //BASIC_OUT_ASSIGNMENT2
     wire->setHolidayName( local.holidayName ); //BASIC_OUT_ASSIGNMENT2
     wire->setIsWorkRequired( local.isWorkRequired ); //BASIC_OUT_ASSIGNMENT2

     return wire;
}
Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram local , Teamcenter::Soa::Server::ServiceData*  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram();
     wire->setPrjId( local.prjId ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjName( local.prjName ); //BASIC_OUT_ASSIGNMENT2
     wire->setTskType( local.tskType ); //BASIC_OUT_ASSIGNMENT2
     wire->setTskTypeDval( local.tskTypeDval ); //BASIC_OUT_ASSIGNMENT2
     wire->setStkTag( local.stkTag ); //BASIC_OUT_ASSIGNMENT2
     wire->setStartDay( local.startDay ); //BASIC_OUT_ASSIGNMENT2
     wire->setEndDay( local.endDay ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjStartYear( local.prjStartYear ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjStartMonth( local.prjStartMonth ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjStartDay( local.prjStartDay ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjEndYear( local.prjEndYear ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjEndMonth( local.prjEndMonth ); //BASIC_OUT_ASSIGNMENT2
     wire->setPrjEndDay( local.prjEndDay ); //BASIC_OUT_ASSIGNMENT2
     wire->setSchTag( local.schTag ); //BASIC_OUT_ASSIGNMENT2

     return wire;
}
 D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram   ManHourManagementIiopSkeleton::toLocal( Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> wire )
{
     D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram local;
     local.prjId = wire->getPrjId(); //SIMPLE_ASSIGNMENT2
     local.prjName = wire->getPrjName(); //SIMPLE_ASSIGNMENT2
     local.tskType = wire->getTskType(); //SIMPLE_ASSIGNMENT2
     local.tskTypeDval = wire->getTskTypeDval(); //SIMPLE_ASSIGNMENT2
     local.stkTag = wire->getStkTag(); //SIMPLE_ASSIGNMENT2
     local.startDay = wire->getStartDay(); //SIMPLE_ASSIGNMENT2
     local.endDay = wire->getEndDay(); //SIMPLE_ASSIGNMENT2
     local.prjStartYear = wire->getPrjStartYear(); //SIMPLE_ASSIGNMENT2
     local.prjStartMonth = wire->getPrjStartMonth(); //SIMPLE_ASSIGNMENT2
     local.prjStartDay = wire->getPrjStartDay(); //SIMPLE_ASSIGNMENT2
     local.prjEndYear = wire->getPrjEndYear(); //SIMPLE_ASSIGNMENT2
     local.prjEndMonth = wire->getPrjEndMonth(); //SIMPLE_ASSIGNMENT2
     local.prjEndDay = wire->getPrjEndDay(); //SIMPLE_ASSIGNMENT2
     local.schTag = wire->getSchTag(); //SIMPLE_ASSIGNMENT2

     return local;
}
Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntrySet local , Teamcenter::Soa::Server::ServiceData* pServiceData  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet();
     D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray mheSetArray; //VECTOR_STRUCT_OUT2
     for(size_t i = 0; i<local.mheSet.size(); i++)
     {
          mheSetArray.push_back( toWire( local.mheSet[i], pServiceData ));
     }
     wire->setMheSetArray( mheSetArray );

     return wire;
}
Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> ManHourManagementIiopSkeleton::toWire( D6::Soa::Work::_2012_02::ManHourManagement::ManHourBillType local , Teamcenter::Soa::Server::ServiceData*  )
{
     Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> wire;
     wire =  new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType();
     wire->setMyRefBR( local.myRefBR ); //BASIC_OUT_ASSIGNMENT2
     wire->setBillTypeInternal( local.billTypeInternal ); //BASIC_OUT_ASSIGNMENT2
     wire->setBillRateName( local.billRateName ); //BASIC_OUT_ASSIGNMENT2

     return wire;
}


    static D6::Soa::Work::_2012_02::ManHourManagementImpl* _service;
	static Teamcenter::Soa::Server::ServicePolicy*  	 _servicePolicy;


    void ManHourManagementIiopSkeleton::getManHourInfo( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        std::string theUserName = wireIn->getTheUserName(); //SIMPLE_ASSIGNMENT
        std::string theYear = wireIn->getTheYear(); //SIMPLE_ASSIGNMENT
        std::string theMonth = wireIn->getTheMonth(); //SIMPLE_ASSIGNMENT

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::getManHourInfo" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::getManHourInfo" );
            journal_impl = JOURNAL_ask_call_depth();
 
            D6::Soa::Work::_2012_02::ManHourManagement::ManHourInfo localOut = _service->getManHourInfo( theUserName, theYear, theMonth );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo();
            
        wireOut = toWire( localOut, NULL ); //LOCAL_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::getManHourInfo" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }

    void ManHourManagementIiopSkeleton::clearManHours( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        std::string theUser = wireIn->getTheUser(); //SIMPLE_ASSIGNMENT
        std::string theYear = wireIn->getTheYear(); //SIMPLE_ASSIGNMENT
        std::string theMonth = wireIn->getTheMonth(); //SIMPLE_ASSIGNMENT

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::clearManHours" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::clearManHours" );
            journal_impl = JOURNAL_ask_call_depth();
 
            std::string localOut = _service->clearManHours( theUser, theYear, theMonth );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::clearManHours" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput();
            
        wireOut->setOut( localOut ); //SIMPLE_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::clearManHours" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::clearManHours" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::clearManHours" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::clearManHours" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::clearManHours" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }

    void ManHourManagementIiopSkeleton::load( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry filter = toLocal( wireIn->getFilter());  //LOCAL_ASSIGNMENT

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::load" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::load" );
            journal_impl = JOURNAL_ask_call_depth();
 
            D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntrySet localOut = _service->load( filter );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::load" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet();
            
        wireOut = toWire( localOut, NULL ); //LOCAL_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::load" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::load" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::load" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::load" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::load" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }

    void ManHourManagementIiopSkeleton::save( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        std::string theUserName = wireIn->getTheUserName(); //SIMPLE_ASSIGNMENT
        std::string theYear = wireIn->getTheYear(); //SIMPLE_ASSIGNMENT
        std::string theMonth = wireIn->getTheMonth(); //SIMPLE_ASSIGNMENT
       D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray wireInManHours = wireIn->getManHourArray(); //ARRAY_ASSIGNMENT
       std::vector< D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry > manHours;
       for(size_t i = 0; i<wireInManHours.size(); i++)
       {
           manHours.push_back( toLocal( wireInManHours[i]) );
       }
       D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgramArray wireInThePrograms = wireIn->getTheProgramArray(); //ARRAY_ASSIGNMENT
       std::vector< D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram > thePrograms;
       for(size_t i = 0; i<wireInThePrograms.size(); i++)
       {
           thePrograms.push_back( toLocal( wireInThePrograms[i]) );
       }

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::save" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::save" );
            journal_impl = JOURNAL_ask_call_depth();
 
            D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntrySet localOut = _service->save( theUserName, theYear, theMonth, manHours, thePrograms );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::save" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet();
            
        wireOut = toWire( localOut, NULL ); //LOCAL_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::save" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::save" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::save" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::save" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::save" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }

    void ManHourManagementIiopSkeleton::revise( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        std::string username = wireIn->getUsername(); //SIMPLE_ASSIGNMENT
        std::string year = wireIn->getYear(); //SIMPLE_ASSIGNMENT
        std::string month = wireIn->getMonth(); //SIMPLE_ASSIGNMENT
       D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray wireInManHours = wireIn->getManHourArray(); //ARRAY_ASSIGNMENT
       std::vector< D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry > manHours;
       for(size_t i = 0; i<wireInManHours.size(); i++)
       {
           manHours.push_back( toLocal( wireInManHours[i]) );
       }

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::revise" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::revise" );
            journal_impl = JOURNAL_ask_call_depth();
 
            D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntrySet localOut = _service->revise( username, year, month, manHours );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::revise" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet();
            
        wireOut = toWire( localOut, NULL ); //LOCAL_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::revise" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::revise" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::revise" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::revise" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::revise" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }

    void ManHourManagementIiopSkeleton::mheTest( const std::string& xmlIn, std::string& xmlOut )
    {
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest" );
        JOURNAL_routine_start ("D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest");
        int journal_skeleton = JOURNAL_ask_call_depth();
         
        // Parse the Doc/Literal input document
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::parseXML" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::parseXML" );
        int journal_parseXml = JOURNAL_ask_call_depth();
         

        Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput> wireIn =
                                     new D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput();
        std::string contentType = Teamcenter::Soa::Common::Xml::XmlUtil::parse( xmlIn, wireIn );


        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::parseXML", "Bytes", xmlIn.length() );
        JOURNAL_assert_call_depth (journal_parseXml);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();


        // Extract the c++ method arguments
        Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::wireToLocal" );
        JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::wireToLocal" );
        int journal_toLocal = JOURNAL_ask_call_depth();
         
        std::string userName = wireIn->getUserName(); //SIMPLE_ASSIGNMENT

        
        Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::wireToLocal" );
        JOURNAL_assert_call_depth (journal_toLocal);
        JOURNAL_return_value (0);
        JOURNAL_routine_end();

        int journal_impl = 0;
        try
        {

            // Execute the method
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementImpl::mheTest" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementImpl::mheTest" );
            journal_impl = JOURNAL_ask_call_depth();
 
            std::string localOut = _service->mheTest( userName );
 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementImpl::mheTest" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();

        
            // Construct the Doc/Literal return object           
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::localToWire" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::localToWire" );
            int journal_toWire = JOURNAL_ask_call_depth();
 
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput> wireOut;
            wireOut = new D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput();
            
        wireOut->setOut( localOut ); //SIMPLE_OUT_ASSIGNMENT

 
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::localToWire" );
            JOURNAL_assert_call_depth (journal_toWire);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            // Serialize the document
            Teamcenter::Soa::Internal::Common::Monitor::markStart( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::serializeXML" );
            JOURNAL_routine_start( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::serializeXML" );
            int journal_serializeXml = JOURNAL_ask_call_depth();
         
            Teamcenter::Soa::Common::Xml::XmlUtil::serialize( contentType, wireOut, xmlOut );

            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest::serializeXML", "Bytes", xmlOut.length() );
            JOURNAL_assert_call_depth (journal_serializeXml);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();


            
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::ManHourManagementIiopSkeleton::mheTest" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            return;
        }
        catch( ServiceException& se)
        {
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::mheTest" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::mheTest" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }
        catch( IFail& )
        {
            ServiceException se;
            Teamcenter::Schemas::Soa::_2006_03::Exceptions::ServiceException wireExp = se.getWireData();
            stringstream expXML;
            ExceptionMapper::writeSoaException( contentType, wireExp, expXML);
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}Impl::mheTest" );
            JOURNAL_assert_call_depth (journal_impl);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            Teamcenter::Soa::Internal::Common::Monitor::markEnd( "D6::Soa::Work::_2012_02::${service.class.name}IiopSkeleton::mheTest" );
            JOURNAL_assert_call_depth (journal_skeleton);
            JOURNAL_return_value (0);
            JOURNAL_routine_end();
            xmlOut = expXML.str();
            return;
        }

    }




D6::Soa::Work::_2012_02::ManHourManagementImpl* ManHourManagementIiopSkeleton::_service = new D6::Soa::Work::_2012_02::ManHourManagementImpl;
Teamcenter::Soa::Server::ServicePolicy*  	  ManHourManagementIiopSkeleton::_servicePolicy = NULL;
static T2LService* me_ManHourManagement = TcServiceManager::instance().registerService( "Work-2012-02-ManHourManagement", new ManHourManagementIiopSkeleton );

 //register the service for getServiceList()
 static std::string registeredServiceOnStartup_ManHourManagement = ServiceManager::instance().registerService("Work-2012-02-ManHourManagement");

}}}}    // End Namespace

