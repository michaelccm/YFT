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

#ifndef TEAMCENTER_SERVICES_WORK_2012_02_MANHOURMANAGEMENT_HXX 
#define TEAMCENTER_SERVICES_WORK_2012_02_MANHOURMANAGEMENT_HXX



#include <teamcenter/soa/server/ServiceException.hxx>
#include <metaframework/BusinessObjectRef.hxx>

#include <Work_exports.h>

namespace D6
{
    namespace Soa
    {
        namespace Work
        {
            namespace _2012_02
            {
                class ManHourManagement;
            }
        }
    }
}


class SOAWORK_API D6::Soa::Work::_2012_02::ManHourManagement

{
public:

    struct ManHourEntry;
    struct ManHourInfo;
    struct ManHourMonthTmp;
    struct ManHourProgram;
    struct ManHourEntrySet;
    struct ManHourBillType;

    /**
     * man hour enty
     */
    struct ManHourEntry
    {
        /**
         * myUserName
         */
        std::string myUserName;
        /**
         * the year for man hour.
         */
        std::string myYear;
        /**
         * the month for man hour
         */
        std::string myMonth;
        /**
         * the day of year
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
         * bill rate reference
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
         * tse status
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
    };

    /**
     * basic man hour info.
     */
    struct ManHourInfo
    {
        /**
         * user name
         */
        std::string myUserName;
        /**
         * myPosition
         */
        std::string myPosition;
        /**
         * the year for man hour.
         */
        std::string myYear;
        /**
         * the month for the man hour.
         */
        std::string myMonth;
        /**
         * Total days for the give year and month.
         */
        int totalDays;
        /**
         * the month template.
         */
        std::vector< ManHourMonthTmp > theMonthTmp;
        /**
         * a set of program
         */
        std::vector< ManHourProgram > theProgramSet;
        /**
         * identify if the user is a hourly based user
         */
        bool isHourlyBasedUser;
        /**
         * identify if the man hour can be edited.
         */
        bool isManHourEditable;
        /**
         * theBillTypeSet
         */
        std::vector< ManHourBillType > theBillTypeSet;
    };

    /**
     * the month template for man hour.
     */
    struct ManHourMonthTmp
    {
        /**
         * the day of month.
         */
        int dayOfMonth;
        /**
         * the day of week.
         */
        std::string dayOfWeek;
        /**
         * is the day weekend
         */
        bool isWeekend;
        /**
         * is the day holiday
         */
        bool isHoliday;
        /**
         * the holiday name.
         */
        std::string holidayName;
        /**
         * is the day required to work.
         */
        bool isWorkRequired;
    };

    /**
     * the program info for the man hour.
     */
    struct ManHourProgram
    {
        /**
         * Project ID
         */
        std::string prjId;
        /**
         * Project Name
         */
        std::string prjName;
        /**
         * task type
         */
        std::string tskType;
        /**
         * tskTypeDval
         */
        std::string tskTypeDval;
        /**
         * Schdeule Task Tag
         */
        std::string stkTag;
        /**
         * start day in given month
         */
        int startDay;
        /**
         * end day in given month
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
    };

    /**
     * a set of man hour entries
     */
    struct ManHourEntrySet
    {
        /**
         * a sef of man hour entries
         */
        std::vector< ManHourEntry > mheSet;
    };

    /**
     * BillType
     */
    struct ManHourBillType
    {
        /**
         * bill rate ref.
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
    };




    /**
     * get the man hour info for give year and month
     *
     * @param theUserName
     *        the user name
     *
     * @param theYear
     *        the given year
     *
     * @param theMonth
     *        the given month
     *
     * @return
     *         the man hour info
     *
     *
     * @exception ServiceException
     */
    virtual ManHourInfo getManHourInfo ( const std::string& theUserName,
        const std::string& theYear,
        const std::string& theMonth ) = 0;

    /**
     * clear man hours
     *
     * @param theUser
     *        the user name
     *
     * @param theYear
     *        the year
     *
     * @param theMonth
     *        the month
     *
     * @return
     *         return string value
     *
     *
     * @exception ServiceException
     */
    virtual std::string clearManHours ( const std::string& theUser,
        const std::string& theYear,
        const std::string& theMonth ) = 0;

    /**
     * load man hour entires
     *
     * @param filter
     *        filter
     *
     * @return
     *         a set of man hour entries
     *
     *
     * @exception ServiceException
     */
    virtual ManHourEntrySet load ( const ManHourEntry& filter ) = 0;

    /**
     * save man hours
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
     * @param manHours
     *        manHours
     *
     * @param thePrograms
     *        the programs
     *
     * @return
     *         save man hours
     *
     *
     * @exception ServiceException
     */
    virtual ManHourEntrySet save ( const std::string& theUserName,
        const std::string& theYear,
        const std::string& theMonth,
        const std::vector< ManHourEntry >& manHours,
        const std::vector< ManHourProgram >& thePrograms ) = 0;

    /**
     * revise
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
     *         man hour entry set
     *
     *
     * @exception ServiceException
     */
    virtual ManHourEntrySet revise ( const std::string& username,
        const std::string& year,
        const std::string& month,
        const std::vector< ManHourEntry >& manHours ) = 0;

    /**
     * mheTest
     *
     * @param userName
     *        userName
     *
     * @return
     *         test result
     *
     *
     * @exception ServiceException
     */
    virtual std::string mheTest ( const std::string& userName ) = 0;


};

#include <Work_undef.h>
#endif

