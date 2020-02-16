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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURENTRY_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURENTRY_HXX


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

                    class ManHourEntry;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<ManHourEntry> > ManHourEntryArray;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    ManHourEntry(   );


    std::string& getMyTaskType() ;
    const std::string& getMyTaskType() const;
    void setMyTaskType( const std::string& myTaskType );
    std::string& getMyPrjId() ;
    const std::string& getMyPrjId() const;
    void setMyPrjId( const std::string& myPrjId );
    std::string& getMyRefBR() ;
    const std::string& getMyRefBR() const;
    void setMyRefBR( const std::string& myRefBR );
    std::string& getMyUserName() ;
    const std::string& getMyUserName() const;
    void setMyUserName( const std::string& myUserName );
    std::string& getMyRefMHE() ;
    const std::string& getMyRefMHE() const;
    void setMyRefMHE( const std::string& myRefMHE );
    int getCol() const;
    void setCol( int col );
    std::string& getMyTaskTypeDval() ;
    const std::string& getMyTaskTypeDval() const;
    void setMyTaskTypeDval( const std::string& myTaskTypeDval );
    std::string& getMyPrjName() ;
    const std::string& getMyPrjName() const;
    void setMyPrjName( const std::string& myPrjName );
    int getRow() const;
    void setRow( int row );
    std::string& getTseStatus() ;
    const std::string& getTseStatus() const;
    void setTseStatus( const std::string& tseStatus );
    std::string& getMyDay() ;
    const std::string& getMyDay() const;
    void setMyDay( const std::string& myDay );
    std::string& getMyMonth() ;
    const std::string& getMyMonth() const;
    void setMyMonth( const std::string& myMonth );
    std::string& getMyRefTE() ;
    const std::string& getMyRefTE() const;
    void setMyRefTE( const std::string& myRefTE );
    std::string& getMyDayOfWeek() ;
    const std::string& getMyDayOfWeek() const;
    void setMyDayOfWeek( const std::string& myDayOfWeek );
    std::string& getWorkRequired() ;
    const std::string& getWorkRequired() const;
    void setWorkRequired( const std::string& workRequired );
    std::string& getMyYear() ;
    const std::string& getMyYear() const;
    void setMyYear( const std::string& myYear );
    std::string& getWorkingHours() ;
    const std::string& getWorkingHours() const;
    void setWorkingHours( const std::string& workingHours );
    std::string& getMyHoliday() ;
    const std::string& getMyHoliday() const;
    void setMyHoliday( const std::string& myHoliday );
    std::string& getBillTypeInternal() ;
    const std::string& getBillTypeInternal() const;
    void setBillTypeInternal( const std::string& billTypeInternal );
    std::string& getBillType() ;
    const std::string& getBillType() const;
    void setBillType( const std::string& billType );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "ManHourEntry" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual ManHourEntry* reallyClone();

         std::string   m_myTaskType;
    std::string   m_myPrjId;
    std::string   m_myRefBR;
    std::string   m_myUserName;
    std::string   m_myRefMHE;
    int   m_col;
    std::string   m_myTaskTypeDval;
    std::string   m_myPrjName;
    int   m_row;
    std::string   m_tseStatus;
    std::string   m_myDay;
    std::string   m_myMonth;
    std::string   m_myRefTE;
    std::string   m_myDayOfWeek;
    std::string   m_workRequired;
    std::string   m_myYear;
    std::string   m_workingHours;
    std::string   m_myHoliday;
    std::string   m_billTypeInternal;
    std::string   m_billType;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

