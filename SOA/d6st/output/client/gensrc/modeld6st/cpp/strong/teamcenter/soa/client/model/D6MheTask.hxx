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

#ifndef TEAMCENTER_SOA_CLIENT_MODEL_D6MHETASK_HXX
#define TEAMCENTER_SOA_CLIENT_MODEL_D6MHETASK_HXX

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
                class Schedule;


class TCSOAD6STMODEL_API D6MheTask : public Teamcenter::Soa::Client::Model::WorkspaceObject
{
public:
    const std::string& get_d6TaskType();
    const std::string& get_d6PrjId();
    const std::string& get_d6PrjName();
    const std::string& get_d6UserName();
    const Teamcenter::Soa::Common::DateTime& get_d6PrjStartDate();
    const Teamcenter::Soa::Common::DateTime& get_d6PrjEndDate();
    const std::string& get_d6Status();
    Schedule* get_d6Schedule_tag();


   SOA_CLASS_NEW_OPERATORS_WITH_IMPL("D6MheTask")

   virtual ~D6MheTask();
};
            }
        }
    }
}
#include <teamcenter/soa/client/model/D6st_undef.h>
#endif
