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

#ifndef TEAMCENTER_SERVICES_WORK_2018_06_MANHOURMANAGESERVICE_HXX 
#define TEAMCENTER_SERVICES_WORK_2018_06_MANHOURMANAGESERVICE_HXX





#include <teamcenter/soa/server/ServiceException.hxx>
#include <metaframework/BusinessObjectRef.hxx>

#include <Work_exports.h>

namespace Teamcenter { namespace Soa {  namespace Internal { namespace Server { class SdmStream; }}}}
namespace Teamcenter { namespace Soa {  namespace Internal { namespace Server { class SdmParser; }}}}
namespace D9 { namespace Services { namespace Work { namespace _2018_06 { class ManHourManageServiceIiopSkeleton; }}}}


namespace D9
{
    namespace Soa
    {
        namespace Work
        {
            namespace _2018_06
            {
                class ManHourManageService;
            }
        }
    }
}


class SOAWORK_API D9::Soa::Work::_2018_06::ManHourManageService

{
public:

    static const std::string XSD_NAMESPACE;

    struct ManHourBillType;
    struct ManHourEntry;
    struct ManHourEntrySet;
    struct ManHourInfo;
    struct ManHourMonthTmp;
    struct ManHourProgram;

    /**
     * ManHourBillType
     */
    struct  ManHourBillType
    {
        /**
         * myRefBR
         */
        std::string myRefBR;
        /**
         * billTypeInternal
         */
        std::string billTypeInternal;
        /**
         * billRateName
         */
        std::string billRateName;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourBillType" );
    };

    /**
     * ManHourEntry
     */
    struct  ManHourEntry
    {
        /**
         * myUserName,
         */
        std::string myUserName;
        /**
         * myYear
         */
        std::string myYear;
        /**
         * myMonth
         */
        std::string myMonth;
        /**
         * myDay
         */
        std::string myDay;
        /**
         * myDayOfWeek
         */
        std::string myDayOfWeek;
        /**
         * myHoliday
         */
        std::string myHoliday;
        /**
         * workRequired
         */
        std::string workRequired;
        /**
         * myPrjName
         */
        std::string myPrjName;
        /**
         * myPrjId
         */
        std::string myPrjId;
        /**
         * myTaskType
         */
        std::string myTaskType;
        /**
         * myTaskTypeDval
         */
        std::string myTaskTypeDval;
        /**
         * billType
         */
        std::string billType;
        /**
         * myRefBR
         */
        std::string myRefBR;
        /**
         * workingHours
         */
        std::string workingHours;
        /**
         * myRefTE
         */
        std::string myRefTE;
        /**
         * tseStatus
         */
        std::string tseStatus;
        /**
         * myRefMHE
         */
        std::string myRefMHE;
        /**
         * row
         */
        int row;
        /**
         * col
         */
        int col;
        /**
         * billTypeInternal
         */
        std::string billTypeInternal;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class Teamcenter::Soa::Internal::Server::SdmParser;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourEntry" );
        void parse    ( Teamcenter::Soa::Internal::Server::SdmParser* _sdmParser );
    };

    /**
     * ManHourEntrySet
     */
    struct  ManHourEntrySet
    {
        /**
         * mheSet
         */
        std::vector< ManHourEntry > mheSet;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourEntrySet" );
    };

    /**
     * ManHourInfo
     */
    struct  ManHourInfo
    {
        /**
         * myUserName
         */
        std::string myUserName;
        /**
         * myPosition
         */
        std::string myPosition;
        /**
         * myYear
         */
        std::string myYear;
        /**
         * myMonth
         */
        std::string myMonth;
        /**
         * totalDays
         */
        int totalDays;
        /**
         * theMonthTmp
         */
        std::vector< ManHourMonthTmp > theMonthTmp;
        /**
         * theProgramSet
         */
        std::vector< ManHourProgram > theProgramSet;
        /**
         * isHourlyBasedUser
         */
        bool isHourlyBasedUser;
        /**
         * isManHourEditable
         */
        bool isManHourEditable;
        /**
         * theBillTypeSet
         */
        std::vector< ManHourBillType > theBillTypeSet;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourInfo" );
    };

    /**
     * ManHourMonthTmp
     */
    struct  ManHourMonthTmp
    {
        /**
         * dayOfMonth
         */
        int dayOfMonth;
        /**
         * dayOfWeek
         */
        std::string dayOfWeek;
        /**
         * isWeekend
         */
        bool isWeekend;
        /**
         * isHoliday
         */
        bool isHoliday;
        /**
         * holidayName
         */
        std::string holidayName;
        /**
         * isWorkRequired
         */
        bool isWorkRequired;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourMonthTmp" );
    };

    /**
     * ManHourProgram
     */
    struct  ManHourProgram
    {
        /**
         * prjId
         */
        std::string prjId;
        /**
         * prjName
         */
        std::string prjName;
        /**
         * tskType
         */
        std::string tskType;
        /**
         * tskTypeDval
         */
        std::string tskTypeDval;
        /**
         * stkTag
         */
        std::string stkTag;
        /**
         * startDay
         */
        int startDay;
        /**
         * endDay
         */
        int endDay;
        /**
         * prjStartYear
         */
        int prjStartYear;
        /**
         * prjStartMonth
         */
        int prjStartMonth;
        /**
         * prjStartDay
         */
        int prjStartDay;
        /**
         * prjEndYear
         */
        int prjEndYear;
        /**
         * prjEndMonth
         */
        int prjEndMonth;
        /**
         * prjEndDay
         */
        int prjEndDay;
        /**
         * schTag
         */
        std::string schTag;

    private:
        friend class Teamcenter::Soa::Internal::Server::SdmStream;
        friend class Teamcenter::Soa::Internal::Server::SdmParser;
        friend class D9::Services::Work::_2018_06::ManHourManageServiceIiopSkeleton;

        void serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName="ManHourProgram" );
        void parse    ( Teamcenter::Soa::Internal::Server::SdmParser* _sdmParser );
    };



    ManHourManageService();
    virtual ~ManHourManageService();
    

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
    virtual std::string clearManHoursOP ( const std::string& theUser,
        const std::string& theAction,
        const std::string& thePara ) = 0;

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
    virtual ManHourInfo getManHourInfoOP ( const std::string& theUserName,
        const std::string& theYear,
        const std::string& theMonth ) = 0;

    /**
     * .
     *
     * @param manHourFilter
     *        manHourFilter
     *
     * @return
     *
     */
    virtual ManHourEntrySet loadOP ( const ManHourEntry& manHourFilter ) = 0;

    /**
     * .
     *
     * @param theUser
     *        theUser
     *
     * @return
     *
     */
    virtual std::string mheTest ( const std::string& theUser ) = 0;

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
    virtual ManHourEntrySet reviseOP ( const std::string& username,
        const std::string& year,
        const std::string& month,
        const std::vector< ManHourEntry >& manHours ) = 0;

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
    virtual ManHourEntrySet saveOP ( const std::string& username,
        const std::string& year,
        const std::string& month,
        const std::vector< ManHourEntry >& manHours,
        const std::vector< ManHourProgram >& programs ) = 0;


};

#include <Work_undef.h>
#endif

