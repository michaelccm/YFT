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

//#ifndef TEAMCENTER_SERVICES_Work__2018_06_ManHourManageService_HXX 
//#define TEAMCENTER_SERVICES_Work__2018_06_ManHourManageService_HXX

#include <unidefs.h>
#if defined(SUN)
#    include <unistd.h>
#endif

#include <string>
#include <iostream>
#include <sstream>



#include <teamcenter/soa/server/ServiceData.hxx>
#include <teamcenter/soa/server/ServiceException.hxx>
#include <teamcenter/soa/server/PartialErrors.hxx>
#include <teamcenter/soa/server/Preferences.hxx>
#include <teamcenter/soa/server/ServicePolicy.hxx> 
#include <teamcenter/soa/internal/server/IiopSkeleton.hxx>
#include <teamcenter/soa/internal/server/ServiceManager.hxx>
#include <teamcenter/soa/internal/gateway/TcServiceManager.hxx>
#include <teamcenter/soa/common/xml/SaxToNodeParser.hxx>
#include <teamcenter/soa/common/xml/XmlUtils.hxx>
#include <teamcenter/soa/common/exceptions/ExceptionMapper.hxx>
#include <teamcenter/soa/common/exceptions/DomException.hxx>
#include <teamcenter/schemas/soa/_2006_03/exceptions/ServiceException.hxx>

#include <manhourmanageservice1806impl.hxx>


#include <Work_exports.h>  
namespace D9
{
    namespace Services
    {
    
        namespace Work
        {
             namespace _2018_06
             {


class SOAWORK_API ManHourManageServiceIiopSkeleton : public Teamcenter::Soa::Internal::Server::IiopSkeleton
{

public:

     ManHourManageServiceIiopSkeleton();
    ~ManHourManageServiceIiopSkeleton();
    
    virtual void initialize();
   


private:

    static D9::Soa::Work::_2018_06::ManHourManageServiceImpl* _service;
	static Teamcenter::Soa::Server::ServicePolicy*  	 _servicePolicy;


    static void clearManHoursOP( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    
    static void getManHourInfoOP( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    
    static void loadOP( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    
    static void mheTest( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    
    static void reviseOP( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    
    static void saveOP( const std::string& xmlIn,  Teamcenter::Soa::Internal::Gateway::GatewayResponse& gatewayResponse );
    


};    // End Class



}}}}    // End Namespace
#include <Work_undef.h>
//#endif   

