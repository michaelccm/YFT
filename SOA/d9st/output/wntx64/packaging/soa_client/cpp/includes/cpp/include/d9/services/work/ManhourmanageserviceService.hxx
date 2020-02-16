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

#ifndef D9_SERVICES_WORK_MANHOURMANAGESERVICESERVICE_HXX
#define D9_SERVICES_WORK_MANHOURMANAGESERVICESERVICE_HXX

#include <d9/services/work/_2018_06/Manhourmanageservice.hxx>



#include <teamcenter/soa/client/Connection.hxx>
#include <new> // for size_t
#include <teamcenter/soa/common/MemoryManager.hxx>

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
            class ManhourmanageserviceService;

/**
 * ManHourManageService
 * <br>
 * <br>
 * <br>
 * <b>Library Reference:</b>
 * <ul>
 * <li type="disc">libd9soaworkstrong.dll
 * </li>
 * </ul>
 */

class D9SOAWORKSTRONG_API ManhourmanageserviceService
    : public D9::Services::Work::_2018_06::Manhourmanageservice
{
public:
    static ManhourmanageserviceService* getService( Teamcenter::Soa::Client::Connection* );


    /**
     * .
     *
     * @param theUser
     *        theUser
     *
     * @param theAction
     *        theAction
     *
     * @param thePara
     *        thePara
     *
     * @return
     *
     */
    virtual std::string clearManHoursOP ( const std::string&  theUser,
        const std::string&  theAction,
        const std::string&  thePara ) = 0;

    /**
     * .
     *
     * @param theUserName
     *        theUserName
     *
     * @param theYear
     *        theYear
     *
     * @param theMonth
     *        theMonth
     *
     * @return
     *
     */
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourInfo getManHourInfoOP ( const std::string&  theUserName,
        const std::string&  theYear,
        const std::string&  theMonth ) = 0;

    /**
     * .
     *
     * @param manHourFilter
     *        manHourFilter
     *
     * @return
     *
     */
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet loadOP ( const D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry&  manHourFilter ) = 0;

    /**
     * .
     *
     * @param theUser
     *        theUser
     *
     * @return
     *
     */
    virtual std::string mheTest ( const std::string&  theUser ) = 0;

    /**
     * .
     *
     * @param username
     *        username
     *
     * @param year
     *        year
     *
     * @param month
     *        month
     *
     * @param manHours
     *        manHours
     *
     * @return
     *
     */
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet reviseOP ( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours ) = 0;

    /**
     * .
     *
     * @param username
     *        username
     *
     * @param year
     *        year
     *
     * @param month
     *        month
     *
     * @param manHours
     *        manHours
     *
     * @param programs
     *        programs
     *
     * @return
     *
     */
    virtual D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntrySet saveOP ( const std::string&  username,
        const std::string&  year,
        const std::string&  month,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry >& manHours,
        const std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram >& programs ) = 0;


    SOA_CLASS_NEW_OPERATORS_WITH_IMPL("ManhourmanageserviceService")

};
        }
    }
}


#ifdef WIN32
#pragma warning ( pop )
#endif

#include <d9/services/work/Work_undef.h>
#endif

