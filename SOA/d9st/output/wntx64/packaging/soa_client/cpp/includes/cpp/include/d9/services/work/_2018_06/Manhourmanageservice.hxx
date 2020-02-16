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

#ifndef D9_SERVICES_WORK__2018_06_MANHOURMANAGESERVICE_HXX
#define D9_SERVICES_WORK__2018_06_MANHOURMANAGESERVICE_HXX

#include <teamcenter/soa/client/Marshaller.hxx>


#include <teamcenter/soa/client/ModelObject.hxx>
#include <teamcenter/soa/client/ServiceData.hxx>
#include <teamcenter/soa/client/PartialErrors.hxx>
#include <teamcenter/soa/client/Preferences.hxx>

#include <d9/services/work/Work_exports.h>

namespace Teamcenter { namespace Soa {  namespace Client { class CdmStream; }}}
namespace Teamcenter { namespace Soa {  namespace Client { class CdmParser; }}}
namespace D9 { namespace Services { namespace Work { class ManhourmanageserviceRestBindingStub; }}}


namespace D9
{
    namespace Services
    {
        namespace Work
        {
            namespace _2018_06
            {
                class Manhourmanageservice;

class D9SOAWORKSTRONG_API Manhourmanageservice
{
public:
    static const std::string XSD_NAMESPACE;

    class ManHourBillType;
    class ManHourEntry;
    class ManHourEntrySet;
    class ManHourInfo;
    class ManHourMonthTmp;
    class ManHourProgram;

    /**
     * ManHourBillType
     */
    class D9SOAWORKSTRONG_API ManHourBillType : public Teamcenter::Soa::Client::Marshaller
    {
    public:
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
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };

    /**
     * ManHourEntry
     */
    class D9SOAWORKSTRONG_API ManHourEntry : public Teamcenter::Soa::Client::Marshaller
    {
    public:
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
        friend class Teamcenter::Soa::Client::CdmStream;
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void serialize( Teamcenter::Soa::Client::CdmStream* _cdmStream, const std::string& elementName="ManHourEntry" ) const;
        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };

    /**
     * ManHourEntrySet
     */
    class D9SOAWORKSTRONG_API ManHourEntrySet : public Teamcenter::Soa::Client::Marshaller
    {
    public:
        /**
         * mheSet
         */
        std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourEntry > mheSet;

    private:
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };

    /**
     * ManHourInfo
     */
    class D9SOAWORKSTRONG_API ManHourInfo : public Teamcenter::Soa::Client::Marshaller
    {
    public:
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
        std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourMonthTmp > theMonthTmp;
        /**
         * theProgramSet
         */
        std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourProgram > theProgramSet;
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
        std::vector< D9::Services::Work::_2018_06::Manhourmanageservice::ManHourBillType > theBillTypeSet;

    private:
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };

    /**
     * ManHourMonthTmp
     */
    class D9SOAWORKSTRONG_API ManHourMonthTmp : public Teamcenter::Soa::Client::Marshaller
    {
    public:
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
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };

    /**
     * ManHourProgram
     */
    class D9SOAWORKSTRONG_API ManHourProgram : public Teamcenter::Soa::Client::Marshaller
    {
    public:
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
        friend class Teamcenter::Soa::Client::CdmStream;
        friend class Teamcenter::Soa::Client::CdmParser;
        friend class D9::Services::Work::ManhourmanageserviceRestBindingStub;

        virtual void serialize( Teamcenter::Soa::Client::CdmStream* _cdmStream, const std::string& elementName="ManHourProgram" ) const;
        virtual void parse    ( Teamcenter::Soa::Client::CdmParser* _cdmParser );
    };




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


protected:
    virtual ~Manhourmanageservice() {}
};
            }
        }
    }
}

#include <d9/services/work/Work_undef.h>
#endif

