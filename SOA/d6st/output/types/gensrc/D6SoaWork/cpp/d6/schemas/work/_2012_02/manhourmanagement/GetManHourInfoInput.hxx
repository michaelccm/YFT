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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_GETMANHOURINFOINPUT_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_GETMANHOURINFOINPUT_HXX


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

                    class GetManHourInfoInput;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<GetManHourInfoInput> > GetManHourInfoInputArray;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    GetManHourInfoInput(   );


    std::string& getTheMonth() ;
    const std::string& getTheMonth() const;
    void setTheMonth( const std::string& theMonth );
    std::string& getTheUserName() ;
    const std::string& getTheUserName() const;
    void setTheUserName( const std::string& theUserName );
    std::string& getTheYear() ;
    const std::string& getTheYear() const;
    void setTheYear( const std::string& theYear );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "GetManHourInfoInput" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual GetManHourInfoInput* reallyClone();

         std::string   m_theMonth;
    std::string   m_theUserName;
    std::string   m_theYear;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

