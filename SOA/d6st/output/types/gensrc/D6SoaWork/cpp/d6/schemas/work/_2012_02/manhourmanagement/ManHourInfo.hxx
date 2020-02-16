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

#ifndef D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURINFO_HXX
#define D6__SCHEMAS__WORK___2012_02__MANHOURMANAGEMENT_MANHOURINFO_HXX


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

#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourMonthTmp.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourProgram.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourBillType.hxx>


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

                    class ManHourInfo;
                    typedef std::vector< Teamcenter::Soa::Common::AutoPtr<ManHourInfo> > ManHourInfoArray;
                    class ManHourMonthTmp;
                    class ManHourProgram;
                    class ManHourBillType;

                }
            }
        }
    }
}





class MANHOURMANAGEMENT_API D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo : 
        public Teamcenter::Soa::Common::Xml::BaseObject 
{
 public:
    
    ManHourInfo(   );


    bool getIsHourlyBasedUser() const;
    void setIsHourlyBasedUser( bool isHourlyBasedUser );
    std::string& getMyMonth() ;
    const std::string& getMyMonth() const;
    void setMyMonth( const std::string& myMonth );
    std::string& getMyYear() ;
    const std::string& getMyYear() const;
    void setMyYear( const std::string& myYear );
    std::string& getMyUserName() ;
    const std::string& getMyUserName() const;
    void setMyUserName( const std::string& myUserName );
    bool getIsManHourEditable() const;
    void setIsManHourEditable( bool isManHourEditable );
    int getTotalDays() const;
    void setTotalDays( int totalDays );
    std::string& getMyPosition() ;
    const std::string& getMyPosition() const;
    void setMyPosition( const std::string& myPosition );
    const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> >& getTheMonthTmpArray() const;
    void setTheMonthTmpArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> >& theMonthTmp );
    void addTheMonthTmp( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp>& theMonthTmp );
    const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& getTheProgramSetArray() const;
    void setTheProgramSetArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& theProgramSet );
    void addTheProgramSet( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram>& theProgramSet );
    const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> >& getTheBillTypeSetArray() const;
    void setTheBillTypeSetArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> >& theBillTypeSet );
    void addTheBillTypeSet( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType>& theBillTypeSet );


    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out ) const { outputXML( out, "ManHourInfo" ); }
    virtual void outputXML( Teamcenter::Soa::Common::Xml::XmlStream& out, const std::string& elementName ) const;
    virtual void parse    ( const Teamcenter::Soa::Common::Xml::XMLNode& node );
    virtual void getNamespaces( std::set< std::string >& namespaces ) const;
  
    virtual void writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const;
    virtual void parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node );

    
    SOA_CLASS_NEW_OPERATORS
    

protected:
    virtual ManHourInfo* reallyClone();

         bool   m_isHourlyBasedUser;
    std::string   m_myMonth;
    std::string   m_myYear;
    std::string   m_myUserName;
    bool   m_isManHourEditable;
    int   m_totalDays;
    std::string   m_myPosition;
    std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> >   m_theMonthTmp;
    std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >   m_theProgramSet;
    std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> >   m_theBillTypeSet;

};


#include <d6/schemas/work/_2012_02/manhourmanagement/Manhourmanagement_undef.h>
#endif

