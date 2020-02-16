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

//#ifndef TEAMCENTER_SERVICES_Work__2012_02_ManHourManagement_HXX 
//#define TEAMCENTER_SERVICES_Work__2012_02_ManHourManagement_HXX

#include <unidefs.h>
#if defined(SUN)
#    include <unistd.h>
#endif

#include <string>
#include <iostream>
#include <sstream>

#include <tcgateway/tcsvcmgr.hxx>

#include <teamcenter/soa/server/ServiceData.hxx>
#include <teamcenter/soa/server/ServiceException.hxx>
#include <teamcenter/soa/server/PartialErrors.hxx>
#include <teamcenter/soa/server/Preferences.hxx>
#include <teamcenter/soa/server/ServicePolicy.hxx> 
#include <teamcenter/soa/internal/server/IiopSkeleton.hxx>
#include <teamcenter/soa/internal/server/Utils.hxx>
#include <teamcenter/soa/internal/server/ServiceManager.hxx>
#include <teamcenter/soa/common/xml/SaxToNodeParser.hxx>
#include <teamcenter/soa/common/xml/XmlUtils.hxx>
#include <teamcenter/soa/common/exceptions/ExceptionMapper.hxx>
#include <teamcenter/soa/common/exceptions/DomException.hxx>
#include <teamcenter/schemas/soa/_2006_03/exceptions/ServiceException.hxx>

#include <manhourmanagement1202impl.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/GetManHourInfoInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourInfo.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ClearManHoursInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ClearManHoursOutput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/LoadInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourEntrySet.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/SaveInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ReviseInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/MheTestInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/MheTestOutput.hxx>


#include <Work_exports.h>  
namespace Teamcenter
{
    namespace Services
    {
    
        namespace Work
        {
             namespace _2012_02
             {


class SOAWORK_API ManHourManagementIiopSkeleton : public Teamcenter::Soa::Internal::Server::IiopSkeleton
{

public:

    ManHourManagementIiopSkeleton();
 
    ~ManHourManagementIiopSkeleton();
    virtual void initialize();
   

static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry local  , Teamcenter::Soa::Server::ServiceData*  );
static D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntry  toLocal( Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> wire );
static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourInfo local  , Teamcenter::Soa::Server::ServiceData* pServiceData );
static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourMonthTmp local  , Teamcenter::Soa::Server::ServiceData*  );
static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram local  , Teamcenter::Soa::Server::ServiceData*  );
static D6::Soa::Work::_2012_02::ManHourManagement::ManHourProgram  toLocal( Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> wire );
static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourEntrySet local  , Teamcenter::Soa::Server::ServiceData* pServiceData );
static Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> toWire(D6::Soa::Work::_2012_02::ManHourManagement::ManHourBillType local  , Teamcenter::Soa::Server::ServiceData*  );

private:

    static D6::Soa::Work::_2012_02::ManHourManagementImpl* _service;
	static Teamcenter::Soa::Server::ServicePolicy*  	 _servicePolicy;


    static void getManHourInfo( const std::string& xmlIn, std::string& xmlOut );
    
    static void clearManHours( const std::string& xmlIn, std::string& xmlOut );
    
    static void load( const std::string& xmlIn, std::string& xmlOut );
    
    static void save( const std::string& xmlIn, std::string& xmlOut );
    
    static void revise( const std::string& xmlIn, std::string& xmlOut );
    
    static void mheTest( const std::string& xmlIn, std::string& xmlOut );
    


};    // End Class



}}}}    // End Namespace
#include <Work_undef.h>
//#endif   

