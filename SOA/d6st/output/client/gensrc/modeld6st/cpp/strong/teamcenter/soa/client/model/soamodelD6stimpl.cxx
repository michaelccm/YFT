/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

  Auto-generated source from Teamcenter Data Model.
                 DO NOT EDIT

 ==================================================
*/

#include <teamcenter/soa/client/internal/ModelManagerImpl.hxx>
#include <teamcenter/soa/client/ModelObjectFactory.hxx>
#include <teamcenter/soa/client/Property.hxx>
#include <teamcenter/soa/client/RuntimeException.hxx>
#include <teamcenter/soa/client/model/D6stObjectFactory.hxx>


// Type/Classes implemented in this CXX file
#include <teamcenter/soa/client/model/D6ManHourEntry.hxx>
#include <teamcenter/soa/client/model/D6MheTask.hxx>

// Referenced Type/Classes
#include <teamcenter/soa/client/model/Schedule.hxx>



using namespace std;
using namespace Teamcenter::Soa::Client;
using namespace Teamcenter::Soa::Client::Model;





Teamcenter::Soa::Client::Model::D6ManHourEntry::~D6ManHourEntry()
{
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6BillType()
{
    return getProperty("d6BillType")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6Holiday()
{
    return getProperty("d6Holiday")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6UserName()
{
    return getProperty("d6UserName")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6WorkingHours()
{
    return getProperty("d6WorkingHours")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6WorkRequired()
{
    return getProperty("d6WorkRequired")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6DayOfWeek()
{
    return getProperty("d6DayOfWeek")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6Month()
{
    return getProperty("d6Month")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6Year()
{
    return getProperty("d6Year")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6Day()
{
    return getProperty("d6Day")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6RefTE()
{
    return getProperty("d6RefTE")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6PrjName()
{
    return getProperty("d6PrjName")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6PrjId()
{
    return getProperty("d6PrjId")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6TaskType()
{
    return getProperty("d6TaskType")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6ManHourEntry::get_d6RefBR()
{
    return getProperty("d6RefBR")->getStringValue();
}

Teamcenter::Soa::Client::Model::D6MheTask::~D6MheTask()
{
}

const std::string& Teamcenter::Soa::Client::Model::D6MheTask::get_d6TaskType()
{
    return getProperty("d6TaskType")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6MheTask::get_d6PrjId()
{
    return getProperty("d6PrjId")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6MheTask::get_d6PrjName()
{
    return getProperty("d6PrjName")->getStringValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6MheTask::get_d6UserName()
{
    return getProperty("d6UserName")->getStringValue();
}

const Teamcenter::Soa::Common::DateTime& Teamcenter::Soa::Client::Model::D6MheTask::get_d6PrjStartDate()
{
    return getProperty("d6PrjStartDate")->getDateTimeValue();
}

const Teamcenter::Soa::Common::DateTime& Teamcenter::Soa::Client::Model::D6MheTask::get_d6PrjEndDate()
{
    return getProperty("d6PrjEndDate")->getDateTimeValue();
}

const std::string& Teamcenter::Soa::Client::Model::D6MheTask::get_d6Status()
{
    return getProperty("d6Status")->getStringValue();
}

Schedule* Teamcenter::Soa::Client::Model::D6MheTask::get_d6Schedule_tag()
{
    Teamcenter::Soa::Client::ModelObject* temp = getProperty("d6Schedule_tag")->getModelObjectValue();
    Schedule* ret = dynamic_cast< Schedule* >( temp );
    if ( ret == 0  && temp != 0)
    {
        throw Teamcenter::Soa::Client::RuntimeException( Teamcenter::Soa::Client::RuntimeException::TypeMismatch, "Wrong type of object stored");
    }
    return ret;
}



void D6stObjectFactory::init0( map< std::string, ModelObjectFactory* >& factoryMap )
{
    factoryMap[ "D6ManHourEntry" ] = new ModelObjectTypeFactory< D6ManHourEntry >();
    factoryMap[ "D6MheTask" ] = new ModelObjectTypeFactory< D6MheTask >();

}
