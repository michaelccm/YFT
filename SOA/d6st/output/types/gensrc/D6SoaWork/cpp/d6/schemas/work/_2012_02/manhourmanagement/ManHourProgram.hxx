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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURPROGRAM_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURPROGRAM_HXX


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

                    class ManHourProgram;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<ManHourProgram> > ManHourProgramArray;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    ManHourProgram(   );


    int getEndDay() const;
    void setEndDay( int endDay );
    std::string& getStkTag() ;
    const std::string& getStkTag() const;
    void setStkTag( const std::string& stkTag );
    int getPrjStartYear() const;
    void setPrjStartYear( int prjStartYear );
    std::string& getSchTag() ;
    const std::string& getSchTag() const;
    void setSchTag( const std::string& schTag );
    int getPrjStartMonth() const;
    void setPrjStartMonth( int prjStartMonth );
    int getStartDay() const;
    void setStartDay( int startDay );
    int getPrjEndMonth() const;
    void setPrjEndMonth( int prjEndMonth );
    int getPrjEndYear() const;
    void setPrjEndYear( int prjEndYear );
    int getPrjStartDay() const;
    void setPrjStartDay( int prjStartDay );
    std::string& getPrjId() ;
    const std::string& getPrjId() const;
    void setPrjId( const std::string& prjId );
    int getPrjEndDay() const;
    void setPrjEndDay( int prjEndDay );
    std::string& getPrjName() ;
    const std::string& getPrjName() const;
    void setPrjName( const std::string& prjName );
    std::string& getTskTypeDval() ;
    const std::string& getTskTypeDval() const;
    void setTskTypeDval( const std::string& tskTypeDval );
    std::string& getTskType() ;
    const std::string& getTskType() const;
    void setTskType( const std::string& tskType );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "ManHourProgram" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual ManHourProgram* reallyClone();

         int   m_endDay;
    std::string   m_stkTag;
    int   m_prjStartYear;
    std::string   m_schTag;
    int   m_prjStartMonth;
    int   m_startDay;
    int   m_prjEndMonth;
    int   m_prjEndYear;
    int   m_prjStartDay;
    std::string   m_prjId;
    int   m_prjEndDay;
    std::string   m_prjName;
    std::string   m_tskTypeDval;
    std::string   m_tskType;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

