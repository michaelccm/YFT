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

#ifndef TEAMCENTER_SOA_CLIENT_MODEL_D6MANHOURENTRY_HXX
#define TEAMCENTER_SOA_CLIENT_MODEL_D6MANHOURENTRY_HXX

#include <new>
#include <teamcenter/soa/common/MemoryManager.hxx>
#include <teamcenter/soa/common/DateTime.hxx>
#include <teamcenter/soa/client/ModelObject.hxx>

#include <teamcenter/soa/client/model/WorkspaceObject.hxx>

#include <teamcenter/soa/client/model/D6st_exports.h>

namespace Teamcenter
{
    namespace Soa
    {
        namespace Client
        {
            namespace Model
            {


class TCSOAD6STMODEL_API D6ManHourEntry : public Teamcenter::Soa::Client::Model::WorkspaceObject
{
public:
    const std::string& get_d6BillType();
    const std::string& get_d6Holiday();
    const std::string& get_d6UserName();
    const std::string& get_d6WorkingHours();
    const std::string& get_d6WorkRequired();
    const std::string& get_d6DayOfWeek();
    const std::string& get_d6Month();
    const std::string& get_d6Year();
    const std::string& get_d6Day();
    const std::string& get_d6RefTE();
    const std::string& get_d6PrjName();
    const std::string& get_d6PrjId();
    const std::string& get_d6TaskType();
    const std::string& get_d6RefBR();


   SOA_CLASS_NEW_OPERATORS_WITH_IMPL("D6ManHourEntry")

   virtual ~D6ManHourEntry();
};
            }
        }
    }
}
#include <teamcenter/soa/client/model/D6st_undef.h>
#endif
