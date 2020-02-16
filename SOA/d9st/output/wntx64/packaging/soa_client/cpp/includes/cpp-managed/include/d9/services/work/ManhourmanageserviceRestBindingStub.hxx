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

#ifndef D9_SERVICES_WORK_MANHOURMANAGESERVICERESTBINDINGSTUB_HXX
#define D9_SERVICES_WORK_MANHOURMANAGESERVICERESTBINDINGSTUB_HXX


#include <new> // for size_t
#include <teamcenter/soa/common/MemoryManager.hxx>
#include <teamcenter/soa/client/internal/ModelManagerImpl.hxx>
#include <teamcenter/soa/client/internal/Sender.hxx>
#include <teamcenter/soa/client/internal/ServiceStub.hxx>

#include <d9/services/work/ManhourmanageserviceService.hxx>


#ifdef WIN32
#pragma warning ( push )
#pragma warning ( disable : 4996  )
#endif

#include <d9/services/work/Work_exports.h>
namespace D9
{
    namespace Services
    {
        namespace Work
        {
            class ManhourmanageserviceRestBindingStub;
        }
    }
}


class D9SOAWORKSTRONGMNGD_API D9::Services::Work::ManhourmanageserviceRestBindingStub : public D9::Services::Work::ManhourmanageserviceService, public Teamcenter::Soa::Client::ServiceStub
{
public:
    virtual std::string clearManHoursOP ( const std::string&  theUser,
        const std::string&  theAction,
        const std::string&  thePara );
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourInfo getManHourInfoOP ( const std::string&  theUserName,
        const std::string&  theYear,
        const std::string&  theMonth );
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet loadOP ( const D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry&  manHourFilter );
    virtual std::string mheTest ( const std::string&  theUser );
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet reviseOP ( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours );
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet saveOP ( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram >& programs );




    ManhourmanageserviceRestBindingStub( Teamcenter::Soa::Client::Sender* restSender, Teamcenter::Soa::Client::ModelManagerImpl* modelManager );
    
    SOA_CLASS_NEW_OPERATORS_WITH_IMPL("D9::Services::Work::ManhourmanageserviceRestBindingStub")

private:
    Teamcenter::Soa::Client::Sender*              m_restSender;
    Teamcenter::Soa::Client::ModelManagerImpl*    m_modelManager;
};

#ifdef WIN32
#pragma warning ( pop )
#endif

#include <d9/services/work/Work_undef.h>
#endif

