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

#include <d6/schemas/work/_2012_02/manhourmanagement/ReviseInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourBillType.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/MheTestInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/SaveInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourEntry.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourInfo.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/GetManHourInfoInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ClearManHoursOutput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/MheTestOutput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ClearManHoursInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourMonthTmp.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourEntrySet.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/LoadInput.hxx>
#include <d6/schemas/work/_2012_02/manhourmanagement/ManHourProgram.hxx>


#include <teamcenter/soa/common/xml/XmlUtils.hxx>
#include <teamcenter/soa/common/exceptions/DomException.hxx>
#include <teamcenter/soa/internal/common/Monitor.hxx>
#include <teamcenter/soa/common/xml/SaxToNodeParser.hxx>
#include <teamcenter/soa/internal/json/Value.hxx>
#include <teamcenter/soa/internal/json/JSONArray.hxx>
#include <teamcenter/soa/internal/json/JSONObject.hxx>
#include <teamcenter/soa/internal/json/BooleanValue.hxx>
#include <teamcenter/soa/internal/json/DateValue.hxx>
#include <teamcenter/soa/internal/json/DoubleValue.hxx>
#include <teamcenter/soa/internal/json/IntegerValue.hxx>
#include <teamcenter/soa/internal/json/StringValue.hxx>


using namespace Teamcenter::Soa::Common;
using namespace Teamcenter::Soa::Common::Xml;
using namespace Teamcenter::Soa::Common::Exceptions;
using namespace Teamcenter::Soa::Internal::Common;
using namespace Teamcenter::Soa::Internal::Json;

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

ReviseInput::ReviseInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ReviseInput" ) {
}

ReviseInput* ReviseInput::reallyClone()
{
    return new ReviseInput( *this );
}

std::string& ReviseInput::getYear() 
{
   return m_year;
}

const std::string& ReviseInput::getYear() const
{
   return m_year;
}

void ReviseInput::setYear( const std::string& year )
{
   m_year = year;
}

std::string& ReviseInput::getMonth() 
{
   return m_month;
}

const std::string& ReviseInput::getMonth() const
{
   return m_month;
}

void ReviseInput::setMonth( const std::string& month )
{
   m_month = month;
}

std::string& ReviseInput::getUsername() 
{
   return m_username;
}

const std::string& ReviseInput::getUsername() const
{
   return m_username;
}

void ReviseInput::setUsername( const std::string& username )
{
   m_username = username;
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& ReviseInput::getManHourArray() const
{
   return m_manHours;
}

void ReviseInput::setManHourArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& manHours )
{
   m_manHours = manHours;
}

void ReviseInput::addManHour( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& manHour )
{
    m_manHours.push_back( manHour );
}

void ReviseInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput::outputXML");
        out.writeAttribute  ( "year", toXmlString( m_year ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "month", toXmlString( m_month ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "username", toXmlString( m_username ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, false );//XML_ELEMENT_SERIALIZATION_IMPL
    std::string prefix = out.getNamespacePrefix( m_xsdNamespace ) + ":";
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray::const_iterator it = m_manHours.begin(); it != m_manHours.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"manHours" );
    }

    out.writeCloseElement( m_xsdNamespace, elementName  );


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput::outputXML");
}

void ReviseInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "year", m_year, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "month", m_month, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "username", m_username, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    JsonStream::writeVectorIfJsonSerializeable( m_manHours, "manHours",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    stream->writeObjectClose();
}

void ReviseInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput::parse");

        setYear( ( node.getAttr("year") ) ); //PARSE_REQUIRED_ATTR
    setMonth( ( node.getAttr("month") ) ); //PARSE_REQUIRED_ATTR
    setUsername( ( node.getAttr("username") ) ); //PARSE_REQUIRED_ATTR


        //XML_ELEMENT_PARSING_IMPL 
    m_manHours.reserve( node.children.size() );
    for ( XMLNodeIterator it = node.children.begin(); it != node.children.end(); ++it )
    {
        const XMLNode& childNode = **it;
        if ( childNode.name == "manHours" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();

           newObj->parse( childNode );
           addManHour( newObj );
       }
       else 
        {
            // Ignore these elements, may be for the parent type
        }
    }


    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput::parse");
}

void ReviseInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setYear( node->getString( "year" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMonth( node->getString( "month" ) ); //PARSE_REQUIRED_JSON_ATTR
    setUsername( node->getString( "username" ) ); //PARSE_REQUIRED_JSON_ATTR


    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "manHours" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "manHours" );
        m_manHours.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addManHour( newObj );
        }
    }


}

void ReviseInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

        for( size_t i=0; i<m_manHours.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_manHours[i]->getNamespaces( namespaces );
    }

}


SOA_CLASS_NEW_OPERATORS_IMPL(ReviseInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::ReviseInput")

ManHourBillType::ManHourBillType(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourBillType" ) {
}

ManHourBillType* ManHourBillType::reallyClone()
{
    return new ManHourBillType( *this );
}

std::string& ManHourBillType::getBillTypeInternal() 
{
   return m_billTypeInternal;
}

const std::string& ManHourBillType::getBillTypeInternal() const
{
   return m_billTypeInternal;
}

void ManHourBillType::setBillTypeInternal( const std::string& billTypeInternal )
{
   m_billTypeInternal = billTypeInternal;
}

std::string& ManHourBillType::getBillRateName() 
{
   return m_billRateName;
}

const std::string& ManHourBillType::getBillRateName() const
{
   return m_billRateName;
}

void ManHourBillType::setBillRateName( const std::string& billRateName )
{
   m_billRateName = billRateName;
}

std::string& ManHourBillType::getMyRefBR() 
{
   return m_myRefBR;
}

const std::string& ManHourBillType::getMyRefBR() const
{
   return m_myRefBR;
}

void ManHourBillType::setMyRefBR( const std::string& myRefBR )
{
   m_myRefBR = myRefBR;
}

void ManHourBillType::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType::outputXML");
        out.writeAttribute  ( "billTypeInternal", toXmlString( m_billTypeInternal ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "billRateName", toXmlString( m_billRateName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myRefBR", toXmlString( m_myRefBR ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType::outputXML");
}

void ManHourBillType::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "billTypeInternal", m_billTypeInternal, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "billRateName", m_billRateName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myRefBR", m_myRefBR, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ManHourBillType::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType::parse");

        setBillTypeInternal( ( node.getAttr("billTypeInternal") ) ); //PARSE_REQUIRED_ATTR
    setBillRateName( ( node.getAttr("billRateName") ) ); //PARSE_REQUIRED_ATTR
    setMyRefBR( ( node.getAttr("myRefBR") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType::parse");
}

void ManHourBillType::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setBillTypeInternal( node->getString( "billTypeInternal" ) ); //PARSE_REQUIRED_JSON_ATTR
    setBillRateName( node->getString( "billRateName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyRefBR( node->getString( "myRefBR" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ManHourBillType::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourBillType, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType")

MheTestInput::MheTestInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "MheTestInput" ) {
}

MheTestInput* MheTestInput::reallyClone()
{
    return new MheTestInput( *this );
}

std::string& MheTestInput::getUserName() 
{
   return m_userName;
}

const std::string& MheTestInput::getUserName() const
{
   return m_userName;
}

void MheTestInput::setUserName( const std::string& userName )
{
   m_userName = userName;
}

void MheTestInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput::outputXML");
        out.writeAttribute  ( "userName", toXmlString( m_userName ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput::outputXML");
}

void MheTestInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "userName", m_userName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void MheTestInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput::parse");

        setUserName( ( node.getAttr("userName") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput::parse");
}

void MheTestInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setUserName( node->getString( "userName" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void MheTestInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(MheTestInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestInput")

SaveInput::SaveInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "SaveInput" ) {
}

SaveInput* SaveInput::reallyClone()
{
    return new SaveInput( *this );
}

std::string& SaveInput::getTheMonth() 
{
   return m_theMonth;
}

const std::string& SaveInput::getTheMonth() const
{
   return m_theMonth;
}

void SaveInput::setTheMonth( const std::string& theMonth )
{
   m_theMonth = theMonth;
}

std::string& SaveInput::getTheUserName() 
{
   return m_theUserName;
}

const std::string& SaveInput::getTheUserName() const
{
   return m_theUserName;
}

void SaveInput::setTheUserName( const std::string& theUserName )
{
   m_theUserName = theUserName;
}

std::string& SaveInput::getTheYear() 
{
   return m_theYear;
}

const std::string& SaveInput::getTheYear() const
{
   return m_theYear;
}

void SaveInput::setTheYear( const std::string& theYear )
{
   m_theYear = theYear;
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& SaveInput::getManHourArray() const
{
   return m_manHours;
}

void SaveInput::setManHourArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& manHours )
{
   m_manHours = manHours;
}

void SaveInput::addManHour( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& manHour )
{
    m_manHours.push_back( manHour );
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& SaveInput::getTheProgramArray() const
{
   return m_thePrograms;
}

void SaveInput::setTheProgramArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& thePrograms )
{
   m_thePrograms = thePrograms;
}

void SaveInput::addTheProgram( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram>& theProgram )
{
    m_thePrograms.push_back( theProgram );
}

void SaveInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput::outputXML");
        out.writeAttribute  ( "theMonth", toXmlString( m_theMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theUserName", toXmlString( m_theUserName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theYear", toXmlString( m_theYear ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, false );//XML_ELEMENT_SERIALIZATION_IMPL
    std::string prefix = out.getNamespacePrefix( m_xsdNamespace ) + ":";
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray::const_iterator it = m_manHours.begin(); it != m_manHours.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"manHours" );
    }
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgramArray::const_iterator it = m_thePrograms.begin(); it != m_thePrograms.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"thePrograms" );
    }

    out.writeCloseElement( m_xsdNamespace, elementName  );


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput::outputXML");
}

void SaveInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "theMonth", m_theMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theUserName", m_theUserName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theYear", m_theYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    JsonStream::writeVectorIfJsonSerializeable( m_manHours, "manHours",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    JsonStream::writeVectorIfJsonSerializeable( m_thePrograms, "thePrograms",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    stream->writeObjectClose();
}

void SaveInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput::parse");

        setTheMonth( ( node.getAttr("theMonth") ) ); //PARSE_REQUIRED_ATTR
    setTheUserName( ( node.getAttr("theUserName") ) ); //PARSE_REQUIRED_ATTR
    setTheYear( ( node.getAttr("theYear") ) ); //PARSE_REQUIRED_ATTR


        //XML_ELEMENT_PARSING_IMPL 
    m_manHours.reserve( node.children.size() );
    m_thePrograms.reserve( node.children.size() );
    for ( XMLNodeIterator it = node.children.begin(); it != node.children.end(); ++it )
    {
        const XMLNode& childNode = **it;
        if ( childNode.name == "manHours" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();

           newObj->parse( childNode );
           addManHour( newObj );
       }
       else if ( childNode.name == "thePrograms" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram();

           newObj->parse( childNode );
           addTheProgram( newObj );
       }
       else 
        {
            // Ignore these elements, may be for the parent type
        }
    }


    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput::parse");
}

void SaveInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setTheMonth( node->getString( "theMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheUserName( node->getString( "theUserName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheYear( node->getString( "theYear" ) ); //PARSE_REQUIRED_JSON_ATTR


    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "manHours" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "manHours" );
        m_manHours.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addManHour( newObj );
        }
    }
    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "thePrograms" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "thePrograms" );
        m_thePrograms.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addTheProgram( newObj );
        }
    }


}

void SaveInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

        for( size_t i=0; i<m_manHours.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_manHours[i]->getNamespaces( namespaces );
    }
    for( size_t i=0; i<m_thePrograms.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_thePrograms[i]->getNamespaces( namespaces );
    }

}


SOA_CLASS_NEW_OPERATORS_IMPL(SaveInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::SaveInput")

ManHourEntry::ManHourEntry(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourEntry" ) {
}

ManHourEntry* ManHourEntry::reallyClone()
{
    return new ManHourEntry( *this );
}

std::string& ManHourEntry::getMyTaskType() 
{
   return m_myTaskType;
}

const std::string& ManHourEntry::getMyTaskType() const
{
   return m_myTaskType;
}

void ManHourEntry::setMyTaskType( const std::string& myTaskType )
{
   m_myTaskType = myTaskType;
}

std::string& ManHourEntry::getMyPrjId() 
{
   return m_myPrjId;
}

const std::string& ManHourEntry::getMyPrjId() const
{
   return m_myPrjId;
}

void ManHourEntry::setMyPrjId( const std::string& myPrjId )
{
   m_myPrjId = myPrjId;
}

std::string& ManHourEntry::getMyRefBR() 
{
   return m_myRefBR;
}

const std::string& ManHourEntry::getMyRefBR() const
{
   return m_myRefBR;
}

void ManHourEntry::setMyRefBR( const std::string& myRefBR )
{
   m_myRefBR = myRefBR;
}

std::string& ManHourEntry::getMyUserName() 
{
   return m_myUserName;
}

const std::string& ManHourEntry::getMyUserName() const
{
   return m_myUserName;
}

void ManHourEntry::setMyUserName( const std::string& myUserName )
{
   m_myUserName = myUserName;
}

std::string& ManHourEntry::getMyRefMHE() 
{
   return m_myRefMHE;
}

const std::string& ManHourEntry::getMyRefMHE() const
{
   return m_myRefMHE;
}

void ManHourEntry::setMyRefMHE( const std::string& myRefMHE )
{
   m_myRefMHE = myRefMHE;
}

int ManHourEntry::getCol() const
{
   return m_col;
}

void ManHourEntry::setCol( int col )
{
   m_col = col;
}

std::string& ManHourEntry::getMyTaskTypeDval() 
{
   return m_myTaskTypeDval;
}

const std::string& ManHourEntry::getMyTaskTypeDval() const
{
   return m_myTaskTypeDval;
}

void ManHourEntry::setMyTaskTypeDval( const std::string& myTaskTypeDval )
{
   m_myTaskTypeDval = myTaskTypeDval;
}

std::string& ManHourEntry::getMyPrjName() 
{
   return m_myPrjName;
}

const std::string& ManHourEntry::getMyPrjName() const
{
   return m_myPrjName;
}

void ManHourEntry::setMyPrjName( const std::string& myPrjName )
{
   m_myPrjName = myPrjName;
}

int ManHourEntry::getRow() const
{
   return m_row;
}

void ManHourEntry::setRow( int row )
{
   m_row = row;
}

std::string& ManHourEntry::getTseStatus() 
{
   return m_tseStatus;
}

const std::string& ManHourEntry::getTseStatus() const
{
   return m_tseStatus;
}

void ManHourEntry::setTseStatus( const std::string& tseStatus )
{
   m_tseStatus = tseStatus;
}

std::string& ManHourEntry::getMyDay() 
{
   return m_myDay;
}

const std::string& ManHourEntry::getMyDay() const
{
   return m_myDay;
}

void ManHourEntry::setMyDay( const std::string& myDay )
{
   m_myDay = myDay;
}

std::string& ManHourEntry::getMyMonth() 
{
   return m_myMonth;
}

const std::string& ManHourEntry::getMyMonth() const
{
   return m_myMonth;
}

void ManHourEntry::setMyMonth( const std::string& myMonth )
{
   m_myMonth = myMonth;
}

std::string& ManHourEntry::getMyRefTE() 
{
   return m_myRefTE;
}

const std::string& ManHourEntry::getMyRefTE() const
{
   return m_myRefTE;
}

void ManHourEntry::setMyRefTE( const std::string& myRefTE )
{
   m_myRefTE = myRefTE;
}

std::string& ManHourEntry::getMyDayOfWeek() 
{
   return m_myDayOfWeek;
}

const std::string& ManHourEntry::getMyDayOfWeek() const
{
   return m_myDayOfWeek;
}

void ManHourEntry::setMyDayOfWeek( const std::string& myDayOfWeek )
{
   m_myDayOfWeek = myDayOfWeek;
}

std::string& ManHourEntry::getWorkRequired() 
{
   return m_workRequired;
}

const std::string& ManHourEntry::getWorkRequired() const
{
   return m_workRequired;
}

void ManHourEntry::setWorkRequired( const std::string& workRequired )
{
   m_workRequired = workRequired;
}

std::string& ManHourEntry::getMyYear() 
{
   return m_myYear;
}

const std::string& ManHourEntry::getMyYear() const
{
   return m_myYear;
}

void ManHourEntry::setMyYear( const std::string& myYear )
{
   m_myYear = myYear;
}

std::string& ManHourEntry::getWorkingHours() 
{
   return m_workingHours;
}

const std::string& ManHourEntry::getWorkingHours() const
{
   return m_workingHours;
}

void ManHourEntry::setWorkingHours( const std::string& workingHours )
{
   m_workingHours = workingHours;
}

std::string& ManHourEntry::getMyHoliday() 
{
   return m_myHoliday;
}

const std::string& ManHourEntry::getMyHoliday() const
{
   return m_myHoliday;
}

void ManHourEntry::setMyHoliday( const std::string& myHoliday )
{
   m_myHoliday = myHoliday;
}

std::string& ManHourEntry::getBillTypeInternal() 
{
   return m_billTypeInternal;
}

const std::string& ManHourEntry::getBillTypeInternal() const
{
   return m_billTypeInternal;
}

void ManHourEntry::setBillTypeInternal( const std::string& billTypeInternal )
{
   m_billTypeInternal = billTypeInternal;
}

std::string& ManHourEntry::getBillType() 
{
   return m_billType;
}

const std::string& ManHourEntry::getBillType() const
{
   return m_billType;
}

void ManHourEntry::setBillType( const std::string& billType )
{
   m_billType = billType;
}

void ManHourEntry::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry::outputXML");
        out.writeAttribute  ( "myTaskType", toXmlString( m_myTaskType ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myPrjId", toXmlString( m_myPrjId ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myRefBR", toXmlString( m_myRefBR ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myUserName", toXmlString( m_myUserName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myRefMHE", toXmlString( m_myRefMHE ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "col", lexical_cast< std::string, int >( m_col ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myTaskTypeDval", toXmlString( m_myTaskTypeDval ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myPrjName", toXmlString( m_myPrjName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "row", lexical_cast< std::string, int >( m_row ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "tseStatus", toXmlString( m_tseStatus ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myDay", toXmlString( m_myDay ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myMonth", toXmlString( m_myMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myRefTE", toXmlString( m_myRefTE ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myDayOfWeek", toXmlString( m_myDayOfWeek ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "workRequired", toXmlString( m_workRequired ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myYear", toXmlString( m_myYear ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "workingHours", toXmlString( m_workingHours ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myHoliday", toXmlString( m_myHoliday ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "billTypeInternal", toXmlString( m_billTypeInternal ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "billType", toXmlString( m_billType ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry::outputXML");
}

void ManHourEntry::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "myTaskType", m_myTaskType, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myPrjId", m_myPrjId, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myRefBR", m_myRefBR, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myUserName", m_myUserName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myRefMHE", m_myRefMHE, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "col", m_col, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myTaskTypeDval", m_myTaskTypeDval, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myPrjName", m_myPrjName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "row", m_row, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "tseStatus", m_tseStatus, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myDay", m_myDay, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myMonth", m_myMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myRefTE", m_myRefTE, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myDayOfWeek", m_myDayOfWeek, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "workRequired", m_workRequired, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myYear", m_myYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "workingHours", m_workingHours, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myHoliday", m_myHoliday, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "billTypeInternal", m_billTypeInternal, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "billType", m_billType, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ManHourEntry::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry::parse");

        setMyTaskType( ( node.getAttr("myTaskType") ) ); //PARSE_REQUIRED_ATTR
    setMyPrjId( ( node.getAttr("myPrjId") ) ); //PARSE_REQUIRED_ATTR
    setMyRefBR( ( node.getAttr("myRefBR") ) ); //PARSE_REQUIRED_ATTR
    setMyUserName( ( node.getAttr("myUserName") ) ); //PARSE_REQUIRED_ATTR
    setMyRefMHE( ( node.getAttr("myRefMHE") ) ); //PARSE_REQUIRED_ATTR
    setCol( lexical_cast< int, std::string >( node.getAttr("col") ) ); //PARSE_REQUIRED_ATTR
    setMyTaskTypeDval( ( node.getAttr("myTaskTypeDval") ) ); //PARSE_REQUIRED_ATTR
    setMyPrjName( ( node.getAttr("myPrjName") ) ); //PARSE_REQUIRED_ATTR
    setRow( lexical_cast< int, std::string >( node.getAttr("row") ) ); //PARSE_REQUIRED_ATTR
    setTseStatus( ( node.getAttr("tseStatus") ) ); //PARSE_REQUIRED_ATTR
    setMyDay( ( node.getAttr("myDay") ) ); //PARSE_REQUIRED_ATTR
    setMyMonth( ( node.getAttr("myMonth") ) ); //PARSE_REQUIRED_ATTR
    setMyRefTE( ( node.getAttr("myRefTE") ) ); //PARSE_REQUIRED_ATTR
    setMyDayOfWeek( ( node.getAttr("myDayOfWeek") ) ); //PARSE_REQUIRED_ATTR
    setWorkRequired( ( node.getAttr("workRequired") ) ); //PARSE_REQUIRED_ATTR
    setMyYear( ( node.getAttr("myYear") ) ); //PARSE_REQUIRED_ATTR
    setWorkingHours( ( node.getAttr("workingHours") ) ); //PARSE_REQUIRED_ATTR
    setMyHoliday( ( node.getAttr("myHoliday") ) ); //PARSE_REQUIRED_ATTR
    setBillTypeInternal( ( node.getAttr("billTypeInternal") ) ); //PARSE_REQUIRED_ATTR
    setBillType( ( node.getAttr("billType") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry::parse");
}

void ManHourEntry::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setMyTaskType( node->getString( "myTaskType" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyPrjId( node->getString( "myPrjId" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyRefBR( node->getString( "myRefBR" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyUserName( node->getString( "myUserName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyRefMHE( node->getString( "myRefMHE" ) ); //PARSE_REQUIRED_JSON_ATTR
    setCol( node->getInteger( "col" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyTaskTypeDval( node->getString( "myTaskTypeDval" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyPrjName( node->getString( "myPrjName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setRow( node->getInteger( "row" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTseStatus( node->getString( "tseStatus" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyDay( node->getString( "myDay" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyMonth( node->getString( "myMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyRefTE( node->getString( "myRefTE" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyDayOfWeek( node->getString( "myDayOfWeek" ) ); //PARSE_REQUIRED_JSON_ATTR
    setWorkRequired( node->getString( "workRequired" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyYear( node->getString( "myYear" ) ); //PARSE_REQUIRED_JSON_ATTR
    setWorkingHours( node->getString( "workingHours" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyHoliday( node->getString( "myHoliday" ) ); //PARSE_REQUIRED_JSON_ATTR
    setBillTypeInternal( node->getString( "billTypeInternal" ) ); //PARSE_REQUIRED_JSON_ATTR
    setBillType( node->getString( "billType" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ManHourEntry::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourEntry, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry")

ManHourInfo::ManHourInfo(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourInfo" ) {
    setIsHourlyBasedUser( false );
    setIsManHourEditable( false );
}

ManHourInfo* ManHourInfo::reallyClone()
{
    return new ManHourInfo( *this );
}

bool ManHourInfo::getIsHourlyBasedUser() const
{
   return m_isHourlyBasedUser;
}

void ManHourInfo::setIsHourlyBasedUser( bool isHourlyBasedUser )
{
   m_isHourlyBasedUser = isHourlyBasedUser;
}

std::string& ManHourInfo::getMyMonth() 
{
   return m_myMonth;
}

const std::string& ManHourInfo::getMyMonth() const
{
   return m_myMonth;
}

void ManHourInfo::setMyMonth( const std::string& myMonth )
{
   m_myMonth = myMonth;
}

std::string& ManHourInfo::getMyYear() 
{
   return m_myYear;
}

const std::string& ManHourInfo::getMyYear() const
{
   return m_myYear;
}

void ManHourInfo::setMyYear( const std::string& myYear )
{
   m_myYear = myYear;
}

std::string& ManHourInfo::getMyUserName() 
{
   return m_myUserName;
}

const std::string& ManHourInfo::getMyUserName() const
{
   return m_myUserName;
}

void ManHourInfo::setMyUserName( const std::string& myUserName )
{
   m_myUserName = myUserName;
}

bool ManHourInfo::getIsManHourEditable() const
{
   return m_isManHourEditable;
}

void ManHourInfo::setIsManHourEditable( bool isManHourEditable )
{
   m_isManHourEditable = isManHourEditable;
}

int ManHourInfo::getTotalDays() const
{
   return m_totalDays;
}

void ManHourInfo::setTotalDays( int totalDays )
{
   m_totalDays = totalDays;
}

std::string& ManHourInfo::getMyPosition() 
{
   return m_myPosition;
}

const std::string& ManHourInfo::getMyPosition() const
{
   return m_myPosition;
}

void ManHourInfo::setMyPosition( const std::string& myPosition )
{
   m_myPosition = myPosition;
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> >& ManHourInfo::getTheMonthTmpArray() const
{
   return m_theMonthTmp;
}

void ManHourInfo::setTheMonthTmpArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> >& theMonthTmp )
{
   m_theMonthTmp = theMonthTmp;
}

void ManHourInfo::addTheMonthTmp( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp>& theMonthTmp )
{
    m_theMonthTmp.push_back( theMonthTmp );
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& ManHourInfo::getTheProgramSetArray() const
{
   return m_theProgramSet;
}

void ManHourInfo::setTheProgramSetArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> >& theProgramSet )
{
   m_theProgramSet = theProgramSet;
}

void ManHourInfo::addTheProgramSet( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram>& theProgramSet )
{
    m_theProgramSet.push_back( theProgramSet );
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> >& ManHourInfo::getTheBillTypeSetArray() const
{
   return m_theBillTypeSet;
}

void ManHourInfo::setTheBillTypeSetArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> >& theBillTypeSet )
{
   m_theBillTypeSet = theBillTypeSet;
}

void ManHourInfo::addTheBillTypeSet( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType>& theBillTypeSet )
{
    m_theBillTypeSet.push_back( theBillTypeSet );
}

void ManHourInfo::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo::outputXML");
        out.writeAttribute  ( "isHourlyBasedUser", lexical_cast< std::string, bool >( m_isHourlyBasedUser ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myMonth", toXmlString( m_myMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myYear", toXmlString( m_myYear ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myUserName", toXmlString( m_myUserName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "isManHourEditable", lexical_cast< std::string, bool >( m_isManHourEditable ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "totalDays", lexical_cast< std::string, int >( m_totalDays ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "myPosition", toXmlString( m_myPosition ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, false );//XML_ELEMENT_SERIALIZATION_IMPL
    std::string prefix = out.getNamespacePrefix( m_xsdNamespace ) + ":";
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmpArray::const_iterator it = m_theMonthTmp.begin(); it != m_theMonthTmp.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"theMonthTmp" );
    }
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgramArray::const_iterator it = m_theProgramSet.begin(); it != m_theProgramSet.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"theProgramSet" );
    }
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillTypeArray::const_iterator it = m_theBillTypeSet.begin(); it != m_theBillTypeSet.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"theBillTypeSet" );
    }

    out.writeCloseElement( m_xsdNamespace, elementName  );


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo::outputXML");
}

void ManHourInfo::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "isHourlyBasedUser", m_isHourlyBasedUser, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myMonth", m_myMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myYear", m_myYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myUserName", m_myUserName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "isManHourEditable", m_isManHourEditable, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "totalDays", m_totalDays, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "myPosition", m_myPosition, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    JsonStream::writeVectorIfJsonSerializeable( m_theMonthTmp, "theMonthTmp",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    JsonStream::writeVectorIfJsonSerializeable( m_theProgramSet, "theProgramSet",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    JsonStream::writeVectorIfJsonSerializeable( m_theBillTypeSet, "theBillTypeSet",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    stream->writeObjectClose();
}

void ManHourInfo::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo::parse");

        setIsHourlyBasedUser( lexical_cast< bool, std::string >( node.getAttr("isHourlyBasedUser") ) ); //PARSE_REQUIRED_ATTR
    setMyMonth( ( node.getAttr("myMonth") ) ); //PARSE_REQUIRED_ATTR
    setMyYear( ( node.getAttr("myYear") ) ); //PARSE_REQUIRED_ATTR
    setMyUserName( ( node.getAttr("myUserName") ) ); //PARSE_REQUIRED_ATTR
    setIsManHourEditable( lexical_cast< bool, std::string >( node.getAttr("isManHourEditable") ) ); //PARSE_REQUIRED_ATTR
    setTotalDays( lexical_cast< int, std::string >( node.getAttr("totalDays") ) ); //PARSE_REQUIRED_ATTR
    setMyPosition( ( node.getAttr("myPosition") ) ); //PARSE_REQUIRED_ATTR


        //XML_ELEMENT_PARSING_IMPL 
    m_theMonthTmp.reserve( node.children.size() );
    m_theProgramSet.reserve( node.children.size() );
    m_theBillTypeSet.reserve( node.children.size() );
    for ( XMLNodeIterator it = node.children.begin(); it != node.children.end(); ++it )
    {
        const XMLNode& childNode = **it;
        if ( childNode.name == "theMonthTmp" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp();

           newObj->parse( childNode );
           addTheMonthTmp( newObj );
       }
       else if ( childNode.name == "theProgramSet" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram();

           newObj->parse( childNode );
           addTheProgramSet( newObj );
       }
       else if ( childNode.name == "theBillTypeSet" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType();

           newObj->parse( childNode );
           addTheBillTypeSet( newObj );
       }
       else 
        {
            // Ignore these elements, may be for the parent type
        }
    }


    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo::parse");
}

void ManHourInfo::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setIsHourlyBasedUser( node->getBoolean( "isHourlyBasedUser" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyMonth( node->getString( "myMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyYear( node->getString( "myYear" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyUserName( node->getString( "myUserName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setIsManHourEditable( node->getBoolean( "isManHourEditable" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTotalDays( node->getInteger( "totalDays" ) ); //PARSE_REQUIRED_JSON_ATTR
    setMyPosition( node->getString( "myPosition" ) ); //PARSE_REQUIRED_JSON_ATTR


    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "theMonthTmp" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "theMonthTmp" );
        m_theMonthTmp.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addTheMonthTmp( newObj );
        }
    }
    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "theProgramSet" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "theProgramSet" );
        m_theProgramSet.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addTheProgramSet( newObj );
        }
    }
    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "theBillTypeSet" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "theBillTypeSet" );
        m_theBillTypeSet.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourBillType();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addTheBillTypeSet( newObj );
        }
    }


}

void ManHourInfo::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

        for( size_t i=0; i<m_theMonthTmp.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_theMonthTmp[i]->getNamespaces( namespaces );
    }
    for( size_t i=0; i<m_theProgramSet.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_theProgramSet[i]->getNamespaces( namespaces );
    }
    for( size_t i=0; i<m_theBillTypeSet.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_theBillTypeSet[i]->getNamespaces( namespaces );
    }

}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourInfo, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourInfo")

GetManHourInfoInput::GetManHourInfoInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "GetManHourInfoInput" ) {
}

GetManHourInfoInput* GetManHourInfoInput::reallyClone()
{
    return new GetManHourInfoInput( *this );
}

std::string& GetManHourInfoInput::getTheMonth() 
{
   return m_theMonth;
}

const std::string& GetManHourInfoInput::getTheMonth() const
{
   return m_theMonth;
}

void GetManHourInfoInput::setTheMonth( const std::string& theMonth )
{
   m_theMonth = theMonth;
}

std::string& GetManHourInfoInput::getTheUserName() 
{
   return m_theUserName;
}

const std::string& GetManHourInfoInput::getTheUserName() const
{
   return m_theUserName;
}

void GetManHourInfoInput::setTheUserName( const std::string& theUserName )
{
   m_theUserName = theUserName;
}

std::string& GetManHourInfoInput::getTheYear() 
{
   return m_theYear;
}

const std::string& GetManHourInfoInput::getTheYear() const
{
   return m_theYear;
}

void GetManHourInfoInput::setTheYear( const std::string& theYear )
{
   m_theYear = theYear;
}

void GetManHourInfoInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput::outputXML");
        out.writeAttribute  ( "theMonth", toXmlString( m_theMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theUserName", toXmlString( m_theUserName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theYear", toXmlString( m_theYear ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput::outputXML");
}

void GetManHourInfoInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "theMonth", m_theMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theUserName", m_theUserName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theYear", m_theYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void GetManHourInfoInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput::parse");

        setTheMonth( ( node.getAttr("theMonth") ) ); //PARSE_REQUIRED_ATTR
    setTheUserName( ( node.getAttr("theUserName") ) ); //PARSE_REQUIRED_ATTR
    setTheYear( ( node.getAttr("theYear") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput::parse");
}

void GetManHourInfoInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setTheMonth( node->getString( "theMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheUserName( node->getString( "theUserName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheYear( node->getString( "theYear" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void GetManHourInfoInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(GetManHourInfoInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::GetManHourInfoInput")

ClearManHoursOutput::ClearManHoursOutput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ClearManHoursOutput" ) {
}

ClearManHoursOutput* ClearManHoursOutput::reallyClone()
{
    return new ClearManHoursOutput( *this );
}

std::string& ClearManHoursOutput::getOut() 
{
   return m_out;
}

const std::string& ClearManHoursOutput::getOut() const
{
   return m_out;
}

void ClearManHoursOutput::setOut( const std::string& out )
{
   m_out = out;
}

void ClearManHoursOutput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput::outputXML");
        out.writeAttribute  ( "out", toXmlString( m_out ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput::outputXML");
}

void ClearManHoursOutput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "out", m_out, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ClearManHoursOutput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput::parse");

        setOut( ( node.getAttr("out") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput::parse");
}

void ClearManHoursOutput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setOut( node->getString( "out" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ClearManHoursOutput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ClearManHoursOutput, "D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursOutput")

MheTestOutput::MheTestOutput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "MheTestOutput" ) {
}

MheTestOutput* MheTestOutput::reallyClone()
{
    return new MheTestOutput( *this );
}

std::string& MheTestOutput::getOut() 
{
   return m_out;
}

const std::string& MheTestOutput::getOut() const
{
   return m_out;
}

void MheTestOutput::setOut( const std::string& out )
{
   m_out = out;
}

void MheTestOutput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput::outputXML");
        out.writeAttribute  ( "out", toXmlString( m_out ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput::outputXML");
}

void MheTestOutput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "out", m_out, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void MheTestOutput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput::parse");

        setOut( ( node.getAttr("out") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput::parse");
}

void MheTestOutput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setOut( node->getString( "out" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void MheTestOutput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(MheTestOutput, "D6::Schemas::Work::_2012_02::Manhourmanagement::MheTestOutput")

ClearManHoursInput::ClearManHoursInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ClearManHoursInput" ) {
}

ClearManHoursInput* ClearManHoursInput::reallyClone()
{
    return new ClearManHoursInput( *this );
}

std::string& ClearManHoursInput::getTheMonth() 
{
   return m_theMonth;
}

const std::string& ClearManHoursInput::getTheMonth() const
{
   return m_theMonth;
}

void ClearManHoursInput::setTheMonth( const std::string& theMonth )
{
   m_theMonth = theMonth;
}

std::string& ClearManHoursInput::getTheUser() 
{
   return m_theUser;
}

const std::string& ClearManHoursInput::getTheUser() const
{
   return m_theUser;
}

void ClearManHoursInput::setTheUser( const std::string& theUser )
{
   m_theUser = theUser;
}

std::string& ClearManHoursInput::getTheYear() 
{
   return m_theYear;
}

const std::string& ClearManHoursInput::getTheYear() const
{
   return m_theYear;
}

void ClearManHoursInput::setTheYear( const std::string& theYear )
{
   m_theYear = theYear;
}

void ClearManHoursInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput::outputXML");
        out.writeAttribute  ( "theMonth", toXmlString( m_theMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theUser", toXmlString( m_theUser ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "theYear", toXmlString( m_theYear ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput::outputXML");
}

void ClearManHoursInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "theMonth", m_theMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theUser", m_theUser, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "theYear", m_theYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ClearManHoursInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput::parse");

        setTheMonth( ( node.getAttr("theMonth") ) ); //PARSE_REQUIRED_ATTR
    setTheUser( ( node.getAttr("theUser") ) ); //PARSE_REQUIRED_ATTR
    setTheYear( ( node.getAttr("theYear") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput::parse");
}

void ClearManHoursInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setTheMonth( node->getString( "theMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheUser( node->getString( "theUser" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTheYear( node->getString( "theYear" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ClearManHoursInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ClearManHoursInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::ClearManHoursInput")

ManHourMonthTmp::ManHourMonthTmp(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourMonthTmp" ) {
    setIsHoliday( false );
    setIsWorkRequired( false );
    setIsWeekend( false );
}

ManHourMonthTmp* ManHourMonthTmp::reallyClone()
{
    return new ManHourMonthTmp( *this );
}

std::string& ManHourMonthTmp::getDayOfWeek() 
{
   return m_dayOfWeek;
}

const std::string& ManHourMonthTmp::getDayOfWeek() const
{
   return m_dayOfWeek;
}

void ManHourMonthTmp::setDayOfWeek( const std::string& dayOfWeek )
{
   m_dayOfWeek = dayOfWeek;
}

std::string& ManHourMonthTmp::getHolidayName() 
{
   return m_holidayName;
}

const std::string& ManHourMonthTmp::getHolidayName() const
{
   return m_holidayName;
}

void ManHourMonthTmp::setHolidayName( const std::string& holidayName )
{
   m_holidayName = holidayName;
}

bool ManHourMonthTmp::getIsHoliday() const
{
   return m_isHoliday;
}

void ManHourMonthTmp::setIsHoliday( bool isHoliday )
{
   m_isHoliday = isHoliday;
}

int ManHourMonthTmp::getDayOfMonth() const
{
   return m_dayOfMonth;
}

void ManHourMonthTmp::setDayOfMonth( int dayOfMonth )
{
   m_dayOfMonth = dayOfMonth;
}

bool ManHourMonthTmp::getIsWorkRequired() const
{
   return m_isWorkRequired;
}

void ManHourMonthTmp::setIsWorkRequired( bool isWorkRequired )
{
   m_isWorkRequired = isWorkRequired;
}

bool ManHourMonthTmp::getIsWeekend() const
{
   return m_isWeekend;
}

void ManHourMonthTmp::setIsWeekend( bool isWeekend )
{
   m_isWeekend = isWeekend;
}

void ManHourMonthTmp::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp::outputXML");
        out.writeAttribute  ( "dayOfWeek", toXmlString( m_dayOfWeek ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "holidayName", toXmlString( m_holidayName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "isHoliday", lexical_cast< std::string, bool >( m_isHoliday ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "dayOfMonth", lexical_cast< std::string, int >( m_dayOfMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "isWorkRequired", lexical_cast< std::string, bool >( m_isWorkRequired ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "isWeekend", lexical_cast< std::string, bool >( m_isWeekend ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp::outputXML");
}

void ManHourMonthTmp::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "dayOfWeek", m_dayOfWeek, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "holidayName", m_holidayName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "isHoliday", m_isHoliday, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "dayOfMonth", m_dayOfMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "isWorkRequired", m_isWorkRequired, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "isWeekend", m_isWeekend, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ManHourMonthTmp::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp::parse");

        setDayOfWeek( ( node.getAttr("dayOfWeek") ) ); //PARSE_REQUIRED_ATTR
    setHolidayName( ( node.getAttr("holidayName") ) ); //PARSE_REQUIRED_ATTR
    setIsHoliday( lexical_cast< bool, std::string >( node.getAttr("isHoliday") ) ); //PARSE_REQUIRED_ATTR
    setDayOfMonth( lexical_cast< int, std::string >( node.getAttr("dayOfMonth") ) ); //PARSE_REQUIRED_ATTR
    setIsWorkRequired( lexical_cast< bool, std::string >( node.getAttr("isWorkRequired") ) ); //PARSE_REQUIRED_ATTR
    setIsWeekend( lexical_cast< bool, std::string >( node.getAttr("isWeekend") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp::parse");
}

void ManHourMonthTmp::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setDayOfWeek( node->getString( "dayOfWeek" ) ); //PARSE_REQUIRED_JSON_ATTR
    setHolidayName( node->getString( "holidayName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setIsHoliday( node->getBoolean( "isHoliday" ) ); //PARSE_REQUIRED_JSON_ATTR
    setDayOfMonth( node->getInteger( "dayOfMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setIsWorkRequired( node->getBoolean( "isWorkRequired" ) ); //PARSE_REQUIRED_JSON_ATTR
    setIsWeekend( node->getBoolean( "isWeekend" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ManHourMonthTmp::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourMonthTmp, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourMonthTmp")

ManHourEntrySet::ManHourEntrySet(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourEntrySet" ) {
}

ManHourEntrySet* ManHourEntrySet::reallyClone()
{
    return new ManHourEntrySet( *this );
}

const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& ManHourEntrySet::getMheSetArray() const
{
   return m_mheSet;
}

void ManHourEntrySet::setMheSetArray( const std::vector< Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> >& mheSet )
{
   m_mheSet = mheSet;
}

void ManHourEntrySet::addMheSet( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& mheSet )
{
    m_mheSet.push_back( mheSet );
}

void ManHourEntrySet::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet::outputXML");
    

    
    out.writeOpenElementClose( m_xsdNamespace, false );//XML_ELEMENT_SERIALIZATION_IMPL
    std::string prefix = out.getNamespacePrefix( m_xsdNamespace ) + ":";
    for ( D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntryArray::const_iterator it = m_mheSet.begin(); it != m_mheSet.end(); ++it ) //SERIALIZE_COMPLEX_ARRAY
    {
        it->getPtr()->outputXML( out, prefix+"mheSet" );
    }

    out.writeCloseElement( m_xsdNamespace, elementName  );


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet::outputXML");
}

void ManHourEntrySet::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    JsonStream::writeVectorIfJsonSerializeable( m_mheSet, "mheSet",   stream ); // SERIALIZE_COMPLEX_JSON_ARRAY
    stream->writeObjectClose();
}

void ManHourEntrySet::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet::parse");

    

        //XML_ELEMENT_PARSING_IMPL 
    m_mheSet.reserve( node.children.size() );
    for ( XMLNodeIterator it = node.children.begin(); it != node.children.end(); ++it )
    {
        const XMLNode& childNode = **it;
        if ( childNode.name == "mheSet" ) //PARSE_COMPLEX_ARRAY
       {
           Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj;
           newObj = new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();

           newObj->parse( childNode );
           addMheSet( newObj );
       }
       else 
        {
            // Ignore these elements, may be for the parent type
        }
    }


    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet::parse");
}

void ManHourEntrySet::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{



    //PARSE_COMPLEX_JSON_ARRAY
    if ( node->containsKey( "mheSet" ) )
    {
        AutoPtr<JSONArray> childNode = node->getJSONArray( "mheSet" );
        m_mheSet.reserve( childNode->size() );
        for( size_t ii = 0; ii < childNode->size(); ++ii )
        {
            Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry> newObj =
                                         new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry();
            newObj->parseJSON( childNode->getJSONObject( ii ) );
            addMheSet( newObj );
        }
    }


}

void ManHourEntrySet::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

        for( size_t i=0; i<m_mheSet.size(); ++i ) //GETNAMESPACE_COMPLEX_ARRAY
    {
        m_mheSet[i]->getNamespaces( namespaces );
    }

}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourEntrySet, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntrySet")

LoadInput::LoadInput(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "LoadInput" ),
    m_filter ( new D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry() ){
}

LoadInput* LoadInput::reallyClone()
{
    return new LoadInput( *this );
}

Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& LoadInput::getFilter() 
{
   return m_filter;
}

const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& LoadInput::getFilter() const
{
   return m_filter;
}

void LoadInput::setFilter( const Teamcenter::Soa::Common::AutoPtr<D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourEntry>& manHourEntry )
{
   m_filter = manHourEntry;
}

void LoadInput::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput::outputXML");
    

    
    out.writeOpenElementClose( m_xsdNamespace, false );//XML_ELEMENT_SERIALIZATION_IMPL
    std::string prefix = out.getNamespacePrefix( m_xsdNamespace ) + ":";
   m_filter->outputXML( out, prefix+"filter" ); //SERIALIZE_REQUIRED_COMPLEX_ELEMENT

    out.writeCloseElement( m_xsdNamespace, elementName  );


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput::outputXML");
}

void LoadInput::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeKey( "filter" ); //SERIALIZE_REQUIRED_COMPLEX_JSON_ELEMENT
    m_filter->writeJSON( stream );
    stream->writeObjectClose();
}

void LoadInput::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput::parse");

    

        //XML_ELEMENT_PARSING_IMPL 
    for ( XMLNodeIterator it = node.children.begin(); it != node.children.end(); ++it )
    {
        const XMLNode& childNode = **it;
        if ( childNode.name == "filter" ) //PARSE_COMPLEX_ELEMENT
       {
           m_filter->parse( childNode );
       }
       else 
        {
            // Ignore these elements, may be for the parent type
        }
    }


    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput::parse");
}

void LoadInput::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{



     m_filter->parseJSON( node->getJSONObject( "filter" ) ); //PARSE_COMPLEX_JSON_ELEMENT


}

void LoadInput::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

       m_filter->getNamespaces( namespaces ); //GETNAMESPACE_REQUIRED_COMPLEX_ELEMENT

}


SOA_CLASS_NEW_OPERATORS_IMPL(LoadInput, "D6::Schemas::Work::_2012_02::Manhourmanagement::LoadInput")

ManHourProgram::ManHourProgram(   ):
    BaseObject( "http://d6.com/Schemas/Work/2012-02/ManHourManagement", "ManHourProgram" ) {
}

ManHourProgram* ManHourProgram::reallyClone()
{
    return new ManHourProgram( *this );
}

int ManHourProgram::getEndDay() const
{
   return m_endDay;
}

void ManHourProgram::setEndDay( int endDay )
{
   m_endDay = endDay;
}

std::string& ManHourProgram::getStkTag() 
{
   return m_stkTag;
}

const std::string& ManHourProgram::getStkTag() const
{
   return m_stkTag;
}

void ManHourProgram::setStkTag( const std::string& stkTag )
{
   m_stkTag = stkTag;
}

int ManHourProgram::getPrjStartYear() const
{
   return m_prjStartYear;
}

void ManHourProgram::setPrjStartYear( int prjStartYear )
{
   m_prjStartYear = prjStartYear;
}

std::string& ManHourProgram::getSchTag() 
{
   return m_schTag;
}

const std::string& ManHourProgram::getSchTag() const
{
   return m_schTag;
}

void ManHourProgram::setSchTag( const std::string& schTag )
{
   m_schTag = schTag;
}

int ManHourProgram::getPrjStartMonth() const
{
   return m_prjStartMonth;
}

void ManHourProgram::setPrjStartMonth( int prjStartMonth )
{
   m_prjStartMonth = prjStartMonth;
}

int ManHourProgram::getStartDay() const
{
   return m_startDay;
}

void ManHourProgram::setStartDay( int startDay )
{
   m_startDay = startDay;
}

int ManHourProgram::getPrjEndMonth() const
{
   return m_prjEndMonth;
}

void ManHourProgram::setPrjEndMonth( int prjEndMonth )
{
   m_prjEndMonth = prjEndMonth;
}

int ManHourProgram::getPrjEndYear() const
{
   return m_prjEndYear;
}

void ManHourProgram::setPrjEndYear( int prjEndYear )
{
   m_prjEndYear = prjEndYear;
}

int ManHourProgram::getPrjStartDay() const
{
   return m_prjStartDay;
}

void ManHourProgram::setPrjStartDay( int prjStartDay )
{
   m_prjStartDay = prjStartDay;
}

std::string& ManHourProgram::getPrjId() 
{
   return m_prjId;
}

const std::string& ManHourProgram::getPrjId() const
{
   return m_prjId;
}

void ManHourProgram::setPrjId( const std::string& prjId )
{
   m_prjId = prjId;
}

int ManHourProgram::getPrjEndDay() const
{
   return m_prjEndDay;
}

void ManHourProgram::setPrjEndDay( int prjEndDay )
{
   m_prjEndDay = prjEndDay;
}

std::string& ManHourProgram::getPrjName() 
{
   return m_prjName;
}

const std::string& ManHourProgram::getPrjName() const
{
   return m_prjName;
}

void ManHourProgram::setPrjName( const std::string& prjName )
{
   m_prjName = prjName;
}

std::string& ManHourProgram::getTskTypeDval() 
{
   return m_tskTypeDval;
}

const std::string& ManHourProgram::getTskTypeDval() const
{
   return m_tskTypeDval;
}

void ManHourProgram::setTskTypeDval( const std::string& tskTypeDval )
{
   m_tskTypeDval = tskTypeDval;
}

std::string& ManHourProgram::getTskType() 
{
   return m_tskType;
}

const std::string& ManHourProgram::getTskType() const
{
   return m_tskType;
}

void ManHourProgram::setTskType( const std::string& tskType )
{
   m_tskType = tskType;
}

void ManHourProgram::outputXML( XmlStream& out, const std::string& elementName ) const
{
    bool root = Teamcenter::Soa::Common::Xml::BaseObject::outputXMLBase(out, elementName, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram::outputXML");
        out.writeAttribute  ( "endDay", lexical_cast< std::string, int >( m_endDay ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "stkTag", toXmlString( m_stkTag ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjStartYear", lexical_cast< std::string, int >( m_prjStartYear ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "schTag", toXmlString( m_schTag ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjStartMonth", lexical_cast< std::string, int >( m_prjStartMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "startDay", lexical_cast< std::string, int >( m_startDay ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjEndMonth", lexical_cast< std::string, int >( m_prjEndMonth ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjEndYear", lexical_cast< std::string, int >( m_prjEndYear ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjStartDay", lexical_cast< std::string, int >( m_prjStartDay ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjId", toXmlString( m_prjId ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjEndDay", lexical_cast< std::string, int >( m_prjEndDay ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "prjName", toXmlString( m_prjName ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "tskTypeDval", toXmlString( m_tskTypeDval ) ); //SERIALIZE_REQUIRED_ATTR
    out.writeAttribute  ( "tskType", toXmlString( m_tskType ) ); //SERIALIZE_REQUIRED_ATTR


    
    out.writeOpenElementClose( m_xsdNamespace, true );//XML_EMPTY_ELEMENT_SERIALIZATION_IMPL


    if (root)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram::outputXML");
}

void ManHourProgram::writeJSON( Teamcenter::Soa::Common::Xml::JsonStream* stream ) const
{
    stream->writeObjectOpen( getQaulifiedName() );
    stream->writeAttribute( "endDay", m_endDay, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "stkTag", m_stkTag, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjStartYear", m_prjStartYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "schTag", m_schTag, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjStartMonth", m_prjStartMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "startDay", m_startDay, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjEndMonth", m_prjEndMonth, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjEndYear", m_prjEndYear, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjStartDay", m_prjStartDay, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjId", m_prjId, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjEndDay", m_prjEndDay, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "prjName", m_prjName, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "tskTypeDval", m_tskTypeDval, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeAttribute( "tskType", m_tskType, true ); // SERIALIZE_REQUIRED_JSON_ATTR
    stream->writeObjectClose();
}

void ManHourProgram::parse( const XMLNode& node )
{
    if(node.parent == NULL)
        Monitor::markStart("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram::parse");

        setEndDay( lexical_cast< int, std::string >( node.getAttr("endDay") ) ); //PARSE_REQUIRED_ATTR
    setStkTag( ( node.getAttr("stkTag") ) ); //PARSE_REQUIRED_ATTR
    setPrjStartYear( lexical_cast< int, std::string >( node.getAttr("prjStartYear") ) ); //PARSE_REQUIRED_ATTR
    setSchTag( ( node.getAttr("schTag") ) ); //PARSE_REQUIRED_ATTR
    setPrjStartMonth( lexical_cast< int, std::string >( node.getAttr("prjStartMonth") ) ); //PARSE_REQUIRED_ATTR
    setStartDay( lexical_cast< int, std::string >( node.getAttr("startDay") ) ); //PARSE_REQUIRED_ATTR
    setPrjEndMonth( lexical_cast< int, std::string >( node.getAttr("prjEndMonth") ) ); //PARSE_REQUIRED_ATTR
    setPrjEndYear( lexical_cast< int, std::string >( node.getAttr("prjEndYear") ) ); //PARSE_REQUIRED_ATTR
    setPrjStartDay( lexical_cast< int, std::string >( node.getAttr("prjStartDay") ) ); //PARSE_REQUIRED_ATTR
    setPrjId( ( node.getAttr("prjId") ) ); //PARSE_REQUIRED_ATTR
    setPrjEndDay( lexical_cast< int, std::string >( node.getAttr("prjEndDay") ) ); //PARSE_REQUIRED_ATTR
    setPrjName( ( node.getAttr("prjName") ) ); //PARSE_REQUIRED_ATTR
    setTskTypeDval( ( node.getAttr("tskTypeDval") ) ); //PARSE_REQUIRED_ATTR
    setTskType( ( node.getAttr("tskType") ) ); //PARSE_REQUIRED_ATTR


    

    if (node.parent == NULL)
        Monitor::markEnd("D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram::parse");
}

void ManHourProgram::parseJSON( const Teamcenter::Soa::Common::AutoPtr<Teamcenter::Soa::Internal::Json::JSONObject> node )
{

    setEndDay( node->getInteger( "endDay" ) ); //PARSE_REQUIRED_JSON_ATTR
    setStkTag( node->getString( "stkTag" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjStartYear( node->getInteger( "prjStartYear" ) ); //PARSE_REQUIRED_JSON_ATTR
    setSchTag( node->getString( "schTag" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjStartMonth( node->getInteger( "prjStartMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setStartDay( node->getInteger( "startDay" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjEndMonth( node->getInteger( "prjEndMonth" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjEndYear( node->getInteger( "prjEndYear" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjStartDay( node->getInteger( "prjStartDay" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjId( node->getString( "prjId" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjEndDay( node->getInteger( "prjEndDay" ) ); //PARSE_REQUIRED_JSON_ATTR
    setPrjName( node->getString( "prjName" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTskTypeDval( node->getString( "tskTypeDval" ) ); //PARSE_REQUIRED_JSON_ATTR
    setTskType( node->getString( "tskType" ) ); //PARSE_REQUIRED_JSON_ATTR




}

void ManHourProgram::getNamespaces( std::set< std::string >& namespaces ) const
{
    namespaces.insert(m_xsdNamespace);

    
}


SOA_CLASS_NEW_OPERATORS_IMPL(ManHourProgram, "D6::Schemas::Work::_2012_02::Manhourmanagement::ManHourProgram")

}

}

}

}

}

