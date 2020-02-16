/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@
*/

#ifndef TEAMCENTER_SERVICES_WORK_2018_06_MANHOURMANAGESERVICE_IMPL_HXX 
#define TEAMCENTER_SERVICES_WORK_2018_06_MANHOURMANAGESERVICE_IMPL_HXX


#include <manhourmanageservice1806.hxx>

#include <Work_exports.h>

namespace D9
{
    namespace Soa
    {
        namespace Work
        {
            namespace _2018_06
            {
                class ManHourManageServiceImpl;
            }
        }
    }
}


class SOAWORK_API D9::Soa::Work::_2018_06::ManHourManageServiceImpl : public D9::Soa::Work::_2018_06::ManHourManageService

{
public:

    virtual std::string clearManHoursOP ( const std::string& theUser, const std::string& theAction, const std::string& thePara );
    virtual ManHourManageServiceImpl::ManHourInfo getManHourInfoOP ( const std::string& theUserName, const std::string& theYear, const std::string& theMonth );
    virtual ManHourManageServiceImpl::ManHourEntrySet loadOP ( const ManHourEntry& manHourFilter );
    virtual std::string mheTest ( const std::string& theUser );
    virtual ManHourManageServiceImpl::ManHourEntrySet reviseOP ( const std::string& username, const std::string& year, const std::string& month, const std::vector< ManHourEntry >& manHours );
    virtual ManHourManageServiceImpl::ManHourEntrySet saveOP ( const std::string& username, const std::string& year, const std::string& month, const std::vector< ManHourEntry >& manHours, const std::vector< ManHourProgram >& programs );


};

#include <Work_undef.h>
#endif
