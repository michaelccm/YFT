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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURBILLTYPE_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURBILLTYPE_HXX


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

                    class ManHourBillType;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<ManHourBillType> > ManHourBillTypeArray;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    ManHourBillType(   );


    std::string& getBillTypeInternal() ;
    const std::string& getBillTypeInternal() const;
    void setBillTypeInternal( const std::string& billTypeInternal );
    std::string& getBillRateName() ;
    const std::string& getBillRateName() const;
    void setBillRateName( const std::string& billRateName );
    std::string& getMyRefBR() ;
    const std::string& getMyRefBR() const;
    void setMyRefBR( const std::string& myRefBR );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "ManHourBillType" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual ManHourBillType* reallyClone();

         std::string   m_billTypeInternal;
    std::string   m_billRateName;
    std::string   m_myRefBR;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

