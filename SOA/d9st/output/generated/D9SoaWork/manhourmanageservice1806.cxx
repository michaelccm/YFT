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

#include <string>
#include <map>

#include <manhourmanageservice1806.hxx>


#include <teamcenter/soa/internal/server/SdmParser.hxx>
#include <teamcenter/soa/internal/server/SdmStream.hxx>

using namespace D9::Soa::Work::_2018_06;

const std::string ManHourManageService::XSD_NAMESPACE ="http://d9.com/Schemas/Work/2018-06/ManHourManageService";




void ManHourManageService::ManHourBillType::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeStructMember( "myRefBR", myRefBR );
    _sdmStream->writeStructMember( "billTypeInternal", billTypeInternal );
    _sdmStream->writeStructMember( "billRateName", billRateName );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, true );

}

void ManHourManageService::ManHourEntry::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeStructMember( "myUserName", myUserName );
    _sdmStream->writeStructMember( "myYear", myYear );
    _sdmStream->writeStructMember( "myMonth", myMonth );
    _sdmStream->writeStructMember( "myDay", myDay );
    _sdmStream->writeStructMember( "myDayOfWeek", myDayOfWeek );
    _sdmStream->writeStructMember( "myHoliday", myHoliday );
    _sdmStream->writeStructMember( "workRequired", workRequired );
    _sdmStream->writeStructMember( "myPrjName", myPrjName );
    _sdmStream->writeStructMember( "myPrjId", myPrjId );
    _sdmStream->writeStructMember( "myTaskType", myTaskType );
    _sdmStream->writeStructMember( "myTaskTypeDval", myTaskTypeDval );
    _sdmStream->writeStructMember( "billType", billType );
    _sdmStream->writeStructMember( "myRefBR", myRefBR );
    _sdmStream->writeStructMember( "workingHours", workingHours );
    _sdmStream->writeStructMember( "myRefTE", myRefTE );
    _sdmStream->writeStructMember( "tseStatus", tseStatus );
    _sdmStream->writeStructMember( "myRefMHE", myRefMHE );
    _sdmStream->writeStructMember( "row", (int)row );
    _sdmStream->writeStructMember( "col", (int)col );
    _sdmStream->writeStructMember( "billTypeInternal", billTypeInternal );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, true );

}

void ManHourManageService::ManHourEntry::parse( Teamcenter::Soa::Internal::Server::SdmParser* _sdmParser )
{
    _sdmParser->parseStructMember( "myUserName", myUserName );
    _sdmParser->parseStructMember( "myYear", myYear );
    _sdmParser->parseStructMember( "myMonth", myMonth );
    _sdmParser->parseStructMember( "myDay", myDay );
    _sdmParser->parseStructMember( "myDayOfWeek", myDayOfWeek );
    _sdmParser->parseStructMember( "myHoliday", myHoliday );
    _sdmParser->parseStructMember( "workRequired", workRequired );
    _sdmParser->parseStructMember( "myPrjName", myPrjName );
    _sdmParser->parseStructMember( "myPrjId", myPrjId );
    _sdmParser->parseStructMember( "myTaskType", myTaskType );
    _sdmParser->parseStructMember( "myTaskTypeDval", myTaskTypeDval );
    _sdmParser->parseStructMember( "billType", billType );
    _sdmParser->parseStructMember( "myRefBR", myRefBR );
    _sdmParser->parseStructMember( "workingHours", workingHours );
    _sdmParser->parseStructMember( "myRefTE", myRefTE );
    _sdmParser->parseStructMember( "tseStatus", tseStatus );
    _sdmParser->parseStructMember( "myRefMHE", myRefMHE );
    _sdmParser->parseStructMember( "row", row );
    _sdmParser->parseStructMember( "col", col );
    _sdmParser->parseStructMember( "billTypeInternal", billTypeInternal );
}

void ManHourManageService::ManHourEntrySet::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, false );
    std::string _prefix =  _sdmStream->getNamespacePrefix( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE );

    _sdmStream->writeStructMember( _prefix+"mheSet", mheSet );
    _sdmStream->writeCloseElement(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName  );
}

void ManHourManageService::ManHourInfo::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeStructMember( "myUserName", myUserName );
    _sdmStream->writeStructMember( "myPosition", myPosition );
    _sdmStream->writeStructMember( "myYear", myYear );
    _sdmStream->writeStructMember( "myMonth", myMonth );
    _sdmStream->writeStructMember( "totalDays", (int)totalDays );
    _sdmStream->writeStructMember( "isHourlyBasedUser", (bool)isHourlyBasedUser );
    _sdmStream->writeStructMember( "isManHourEditable", (bool)isManHourEditable );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, false );
    std::string _prefix =  _sdmStream->getNamespacePrefix( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE );

    _sdmStream->writeStructMember( _prefix+"theMonthTmp", theMonthTmp );
    _sdmStream->writeStructMember( _prefix+"theProgramSet", theProgramSet );
    _sdmStream->writeStructMember( _prefix+"theBillTypeSet", theBillTypeSet );
    _sdmStream->writeCloseElement(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName  );
}

void ManHourManageService::ManHourMonthTmp::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeStructMember( "dayOfMonth", (int)dayOfMonth );
    _sdmStream->writeStructMember( "dayOfWeek", dayOfWeek );
    _sdmStream->writeStructMember( "isWeekend", (bool)isWeekend );
    _sdmStream->writeStructMember( "isHoliday", (bool)isHoliday );
    _sdmStream->writeStructMember( "holidayName", holidayName );
    _sdmStream->writeStructMember( "isWorkRequired", (bool)isWorkRequired );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, true );

}

void ManHourManageService::ManHourProgram::serialize( Teamcenter::Soa::Internal::Server::SdmStream* _sdmStream, const std::string& elementName )
{


    _sdmStream->writeOpenElement2( D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, elementName );
    _sdmStream->writeStructMember( "prjId", prjId );
    _sdmStream->writeStructMember( "prjName", prjName );
    _sdmStream->writeStructMember( "tskType", tskType );
    _sdmStream->writeStructMember( "tskTypeDval", tskTypeDval );
    _sdmStream->writeStructMember( "stkTag", stkTag );
    _sdmStream->writeStructMember( "startDay", (int)startDay );
    _sdmStream->writeStructMember( "endDay", (int)endDay );
    _sdmStream->writeStructMember( "prjStartYear", (int)prjStartYear );
    _sdmStream->writeStructMember( "prjStartMonth", (int)prjStartMonth );
    _sdmStream->writeStructMember( "prjStartDay", (int)prjStartDay );
    _sdmStream->writeStructMember( "prjEndYear", (int)prjEndYear );
    _sdmStream->writeStructMember( "prjEndMonth", (int)prjEndMonth );
    _sdmStream->writeStructMember( "prjEndDay", (int)prjEndDay );
    _sdmStream->writeStructMember( "schTag", schTag );
    _sdmStream->writeOpenElementClose(  D9::Soa::Work::_2018_06::ManHourManageService::XSD_NAMESPACE, true );

}

void ManHourManageService::ManHourProgram::parse( Teamcenter::Soa::Internal::Server::SdmParser* _sdmParser )
{
    _sdmParser->parseStructMember( "prjId", prjId );
    _sdmParser->parseStructMember( "prjName", prjName );
    _sdmParser->parseStructMember( "tskType", tskType );
    _sdmParser->parseStructMember( "tskTypeDval", tskTypeDval );
    _sdmParser->parseStructMember( "stkTag", stkTag );
    _sdmParser->parseStructMember( "startDay", startDay );
    _sdmParser->parseStructMember( "endDay", endDay );
    _sdmParser->parseStructMember( "prjStartYear", prjStartYear );
    _sdmParser->parseStructMember( "prjStartMonth", prjStartMonth );
    _sdmParser->parseStructMember( "prjStartDay", prjStartDay );
    _sdmParser->parseStructMember( "prjEndYear", prjEndYear );
    _sdmParser->parseStructMember( "prjEndMonth", prjEndMonth );
    _sdmParser->parseStructMember( "prjEndDay", prjEndDay );
    _sdmParser->parseStructMember( "schTag", schTag );
}




ManHourManageService::ManHourManageService()
{
}

ManHourManageService::~ManHourManageService()
{
}

