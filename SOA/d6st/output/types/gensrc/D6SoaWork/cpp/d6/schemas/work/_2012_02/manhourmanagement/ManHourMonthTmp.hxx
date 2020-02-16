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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURMONTHTMP_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURMONTHTMP_HXX


#include <vector>
#include <string>
#include <set>
#include <ostream>
#include <new> // for size_t

#include <teamcenter/soa/common/MemoryManager.hxx>
#include <teamcenter/soa/common/DateTime.hxx>
#include <teamcenter/soa/common/AutoPtr.hxx>
#include <teamcenter/soa/common/xml/BaseObject.hxx>
#include <teamcenter/soa/common/xml/XmlUtils.hxx>
#include <teamcenter/soa/common/xml/XmlStream.hxx>
#include <teamcenter/soa/common/xml/JsonStream.hxx>



#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_exports.h>

namespace D6
{
    namespace Schemas
    {
        namespace Work
        {
            namespace _2012_02
            {
                namespace Manhourmanagement
                {

                    class ManHourMonthTmp;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<ManHourMonthTmp> > ManHourMonthTmpArray;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    ManHourMonthTmp(   );


    std::string& getDayOfWeek() ;
    const std::string& getDayOfWeek() const;
    void setDayOfWeek( const std::string& dayOfWeek );
    std::string& getHolidayName() ;
    const std::string& getHolidayName() const;
    void setHolidayName( const std::string& holidayName );
    bool getIsHoliday() const;
    void setIsHoliday( bool isHoliday );
    int getDayOfMonth() const;
    void setDayOfMonth( int dayOfMonth );
    bool getIsWorkRequired() const;
    void setIsWorkRequired( bool isWorkRequired );
    bool getIsWeekend() const;
    void setIsWeekend( bool isWeekend );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "ManHourMonthTmp" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual ManHourMonthTmp* reallyClone();

         std::string   m_dayOfWeek;
    std::string   m_holidayName;
    bool   m_isHoliday;
    int   m_dayOfMonth;
    bool   m_isWorkRequired;
    bool   m_isWeekend;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

