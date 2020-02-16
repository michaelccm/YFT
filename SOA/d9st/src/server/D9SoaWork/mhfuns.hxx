/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@
*/

#ifndef MHFUNS__HXX
#define MHFUNS__HXX


#include <unidefs.h>
#if defined(SUN)
#include <unistd.h>
#endif

#include <manhourmanageservice1806.hxx>
#include <manhourmanageservice1806impl.hxx>

using namespace D9::Soa::Work::_2018_06;
using namespace Teamcenter::Soa::Server;

#include <string>
#include <vector>
#include <map>
#include <iostream>
using namespace std;

#include <time.h>
#include <stdlib.h>

#include <tcinit/tcinit.h>
#include <tc/tc_startup.h>
#include <tc/emh.h>
#include <tccore/tctype.h>
#include <tccore/aom.h>
#include <tccore/aom_prop.h>
#include <tccore/grm.h>
#include <tccore/item.h>
#include <tccore/project.h>
#include <tc/folder.h>
#include <tc/preferences.h>
#include <lov/lov.h>
#include <sa/tccalendar.h>
#include <qry/qry.h>
#include <qry/qry_errors.h>
#include <fclasses/tc_date.h>
#include <fclasses/tc_string.h>

#include <sa/user.h>
#include <sa/groupmember.h>
#include <sa/group.h>

#include <base_utils/Mem.h>
#include <user_exits/epm_toolkit_utils.h>
#include <epm/epm.h>
#include <epm/epm_task_template_itk.h>


// string constants.
#define CONST_ALLPOSITIONS "Manager,Lead Engineer,Senior Engineer,Engineer,Assistant Engineer,Engineer Assistant,ExtSupporter"
#define CONST_HOURLYUSER   "Assistant Engineer"
#define CONST_OBJECT_NAME  "object_name"

// qurey constants.
#define QRY_MANHOUR_WITH_YEAR_MONTH     "YFAS_Find_ManHour_WithYM"
#define QRY_YFJC_SEARCHDISCIPLINEBYID   "YFJC_SearchDisciplineByID"
#define QRY_FIND_MANHOUR_PROEJCT        "YFAS_Find_ManHour_Project"
#define QRY_FIND_MANHOUR_TASK           "YFAS_Find_ManHour_Task"

// Preferences
#define PREF_YFAS_EDITABLE_YEAR_MONTH   "YFAS_Timesheet_Editable"
#define PREF_YFJC_HOLIDAY_ALSO_WEEKEND  "YFJC_Holiday_Also_Weekend"
#define PREF_YFAS_ENABLE_PROGRAM        "YFAS_Enable_Program"
#define PREF_YFAS_ENABLE_MANHOUR        "YFAS_Enable_MHE"

// Workflow templates
#define TM_APPROVE_WF                   "YFAS Timesheet Approval"
#define TM_REVISE_WF                    "YFAS Timesheet Revise"

// LOV
#define LOV_YFAS_TASKTYPE               "D6_YFAS_TaskType"

// String assister
#define IS_NULL(S)   ((S)==NULL)
#define IS_EMPTY(S)  (((S)==NULL) || !(*(S)))


#ifdef __cplusplus
extern "C" {
#endif

/* 
 * mhfuns.cxx
 */
int GCOMM_qry( const char *qry_name, int *num_of_obj, tag_t **objs, int num_of_param, ... );
int createManHourInfo (const char *username, const char *year, const char *month, ManHourManageServiceImpl::ManHourInfo &mhinfo);
int rmManHourInfo (const char *username, const char *year, const char *month);
void GUTIL_current_date(date_t *date);


/*
 * hminfo.cxx
 */
int isProgramEnabled (logical *);


/*
 * revise.cxx
 */
int doRevise (const char *username, 
              const char *year, 
              const char *month,
              const vector<ManHourManageService::ManHourEntry> &man_hours,
              ManHourManageService::ManHourEntrySet &mhe_set);


/*
 * save.cxx
 */
int doSave (const char *username, 
            const char *year, 
            const char *month,
            const vector<ManHourManageService::ManHourEntry> &man_hours,
            const vector<ManHourManageService::ManHourProgram> &programs,
            ManHourManageService::ManHourEntrySet &mhe_set);


int checkTSEStatus(tag_t timeSheetTag, string &status);
int bldProgramMap (const vector<ManHourManageService::ManHourProgram> &programs,
   std::map <const std::string, ManHourManageService::ManHourProgram> &prg_map);



// load.cxx.
int doLoad(const ManHourManageService::ManHourEntry &manHourFilter, ManHourManageServiceImpl::ManHourEntrySet &);


// mhtask.cxx
int getPrograms_new (const char *username, ManHourManageServiceImpl::ManHourInfo &mhinfo);


// mhmaint.cxx
int rmCostInfo (const char *username, const char *folder);


// external functions
extern int POM_AM__set_application_bypass(logical bypass);


#ifdef __cplusplus
}
#endif


#endif
