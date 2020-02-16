/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename:yfjc_ebp_head.h

   Function declarations
       
	   (I)      indicates input argument
	   (O)      indicates output argument
       (OF)     indicates output argument which should be passed to POM_free after use

   Declaration:
       
	   Functions head.
============================================================================================================
DATE           Name             Description of Change
04-Mar-2013    zhanggl          creation
$HISTORY$
============================================================================================================*/
#include <bom/bom.h>
#include <sa/am.h>
#include <tccore/aom.h>
#include <tccore/aom_prop.h>
#include <epm/epm.h>
#include <user_exits/epm_toolkit_utils.h>
#include <tc/folder.h>
#include <tccore/grm.h>
#include <sa/group.h>
#include <ict/ict_userservice.h>
#include <tccore/item.h>
#include <lov/lov.h>
#include <lov/lov_msg.h>
#include <tccore/method.h>
#include <tc/preferences.h>
#include <tccore/project.h>
#include <qry/qry.h>
#include <sa/tccalendar.h>
#include <epm/signoff.h>
#include <textsrv/textserver.h>
#include <tc/tc.h>
#include <fclasses/tc_date.h>
#include <tc/tc_macros.h>
#include <tc/tc_util.h>
#include <sa/user.h>
#include <sa/groupmember.h>
#include <ug_va_copy.h>
#include <vector>
#include <string>
#include <map>
#include <ae/dataset.h>
#include <ae/ae.h>
#include <sa/tcfile.h>
#include <tc/envelope.h>
#include <fclasses/tc_date.h>

#include <math.h>
#include <stdlib.h>   
#include <locale.h> 

#include "yfjc_ebp_custom_define.h"
#include "yfjc_ebp_error.h"
#include "yfjc_time.h"
#include "yfjc_ebp_ccl_macros.h"
//#include <liuSOA/jci6transtocostinfo1202impl.hxx>
#include <metaframework/CreateInput.hxx>

using namespace Teamcenter;
//using namespace JCI6::Soa::TransToCostInfo::_2012_02;
using namespace std;
#include <bmf/libuserext_exports.h>

#ifdef WIN32
#include <io.h>
#include <direct.h>
#else
#include <unistd.h>
#endif

#ifdef __cplusplus
extern "C" {
#endif



typedef struct taglist
{
    tag_t ds_tag;
    struct taglist *next;

}TAGLIST;

#define NODELEN sizeof( TAGLIST )

#define ARGS_LENGTH 200
#define ARGS_NAME_DEBUG "-debug"
#define DEBUG "-debug="
#define MAX_PRINTLINE_LENGTH 2000
#define MAX_PATH_LENGTH 2000
#define MAX_ARGUMENT_LENGTH 400
#define MAX_PARAMNAME_LENGTH 50
#define MAX_FILE_EXT_LENGTH 10
#define TRUE_FLAG 1
#define FALSE_FLAG 0
#define DETAILLOG 1

extern "C" int POM_AM__set_application_bypass(logical bypass);
//extern int AM__set_application_bypass( int );
string st_get_time_stamp(string format);
void ask_opt_debug();
void ECHO(char *format, ...);
logical get_opt_debug();

void getNonBudgetCostinfo( char* costInfoName,int *retInt );

int SOPFYIsYear(date_t date);

//SP-CUS-001£¯ÉèÖÃÍâ°ü¹€Ê±Åú×ŒÊ±ÏÞ
int yfjc_reject_overdue_timesheet( EPM_action_message_t msg );
//sum programinforev property to programinfo property
int getPropertySum(METHOD_message_t* msg, va_list args);

int getDatasetFile(char *dsName,char ** dsFilePath);
//-----------------------------------------------------------------------------------------
/**
*  Revise XSOInfoRevision. The secondary function to do revise XSOInfoRevision
 */
int do_revise_XSOInfo( 
    tag_t task                         /**< (I)  Task on which action was triggered */ 
    );
//-----------------------------------------------------------------------------------------
/**
*  add by Cai,Pluto
 */
int splitStr(char* sourceStr, char splitChar, char*** value);


int CCL_switch_objects(EPM_action_message_t msg);

/**
*  Handler. Revise XSOInfoRevision. The main function.
 */
int yfjc_revise_XSOInfo_Handler(
	EPM_action_message_t msg           /**< (I) Action message tag */ 
	);

//add by wuh 2014-5-23
//userservice
int JCI6_updateProgramInfoDate(void *retValType);
//8-6
int userservice_setSignOff(void *retValType);

/***************add by wuh 2014-8-14***************/
int bmf_autoUpdateSOPFisYear(
	METHOD_message_t* msg, 
	va_list args
	);
/***************add by wuh 2014-8-14***************/

//2014-8-5	mengyawei added
//检查审批时限及转化Forecast
int yfjc_transfer_forecast_costinfo(EPM_action_message_t msg);
//外包工时转化 TimesheetEntry
int yfjc_transfer_timesheetentry(EPM_action_message_t msg);
//TimeSheetEntry 对象的 动态属性 jci6_ownPhase
int getTimeSheetEntryOwnPhase(METHOD_message_t* msg, va_list args);

int yfjc_notify_with_signoff_report(EPM_action_message_t msg);
//2014-8-5
/**
*Post action. check TC_Project and set ProgramInfo JCI6_CloseDate
*/
int bmf_programinfo_change_closedate(
    METHOD_message_t *msg,
    va_list args
    );
int JCI6ScheduleTaskDate(
	METHOD_message_t *msg, 
	va_list args
	);
//--------------------------------------------------------------------------------------------
/**
*  Post action. Check through the design requirements.
 */
int JCI6getDRCheckResult( 
    METHOD_message_t *msg,            /**< (I) A message passed with to a function registered as an action for a method. */ 
    va_list  args                     /**< (I) Method body parameters */ 
    );
//---------------------------------------------------------------------------------------------
/**
*  Automatically copy the project to the project database. The main function.
 */
int JCI6postActionsForNewProj( 
    METHOD_message_t *msg,             /**< (I) A message passed with to a function registered as an action for a method. */ 
	va_list args                       /**< (I) Method body parameters */  
	);
/**
*  Check whether the preference is to exist.
 */
int Check_Preference( 
    char *pName                       /**< (I) The preference name */ 
    );
/**
*  Find the value through the key value in the preferences.
 */
int filter_preferences( 
    char *preferenceName,             /**< (I) The preference name */ 
    char *filterValue,                /**< (I) The filter key */ 
    char **templatePath               /**< (OF) The filter value */ 
    );
/**
*  Copy the root folder form template with new name.
 */
int folder_deep_copy( 
    tag_t new_folder_tag,           /**< (I) The new root folder  */ 
    tag_t *project_tag,               /**< (I) The project tag */ 
    tag_t *template_tag               /**< (I) The old folder tag */ 
    );
/**
*  Copy the children folder form template.
 */
int create_child_folder( 
    tag_t *template_tag,              /**< (I) The data source template tag*/ 
    tag_t *parent_tag,                /**< (I) The parent folder tag */ 
	tag_t *project_tag               /**< (I) The project tag address*/ 
    );
int copyAcl(tag_t src_tag,		 /**< (I) The source  tag*/ 
			tag_t obj_tag		 /**< (I) The obj tag*/ 
	);

int createFolder(
    char * object_name,
    char * type,
    tag_t * obj_tag
    );

/**
 *  Check the ScheduleTask before it is deleted
 *  yfjc_ebp_Delete_schedule_task_check     	
 *  2014-6-4 mengyawei added   
 *  SP-CUS-006£¯²»ÔÊÐíÉŸ³ýÒÑÌîÐŽ¹€Ê±µÄÈÎÎñ
 */
int Delete_schedule_task_check( void * returnValueType );
 //int JCI6deleteScheduleTaskCheck( METHOD_message_t *msg, va_list args );
//int Delete_schedule_task_check( 
//	METHOD_message_t *msg,             /**< (I) A message passed with to a function registered as an action for a method. */ 
//	va_list args                       /**< (I) Method body parameters */ 
//	);


//---------------------------------------------------------------------------------------------
/**
*  Java to C. Automatically update project status attribute.
 */
int JCI6_updateProjInfoRevStatus(
    void * returnValue                /**< (O) The return value */  
    );
//---------------------------------------------------------------------------------------------
/**
*  Automatically assigned to the project in accordance with the specific relationship.
 */
int JCI6Schedule_assign_project( 
	METHOD_message_t *msg,             /**< (I) A message passed with to a function registered as an action for a method. */ 
	va_list args                       /**< (I) Method body parameters */ 
	);
//---------------------------------------------------------------------------------------------
/**
*  Schedule assigned to the project before judgment: the whether existing schedule of the project, schedule whether has been assigned to other projects.
 */
int Schedule_assign_project_pre_action( 
	METHOD_message_t *msg,             /**< (I) A message passed with to a function registered as an action for a method. */ 
	va_list args                       /**< (I) Method body parameters */ 
	);
//---------------------------------------------------------------------------------------------
/**
*  Java to C. Create an objcet.
 */
int create_object(
    void * returnValueType           /**< (O) The return value */  
    );
/**
*  Java to C. Revise an objcet.
 */
int revise_object(
    void * returnValueType           /**< (O) The return value */  
    );
/**
*  Java to C. Open or close pass.
 */
int open_or_close_pass (
	void * returnValueType           /**< (O) The return value */  
	);
//---------------------------------------------------------------------------------------------
/**
*  Custom LOV. The main function.
 */
int custom_tag_lov( 
    METHOD_message_t *msg,            /**< (I) A message passed with to a function registered as an action for a method. */ 
    va_list  args                     /**< (I) Method body parameters */ 
    ) ;
/**
*  List division lov.
 */
int list_division( 
    TAGLIST *head,                     /**< (I) Link tail pointer */  
    int *allDivisionCnt                /**< (I) The link point count */  
    ) ;
/**
*  List section lov.
 */
int list_section( 
    TAGLIST *head                     /**< (I) Link tail pointer */ , 
    int *allDivisionCnt               /**< (I) The link point count */ 
    ) ;
/**
*  Free link.
 */
int freelist( 
           TAGLIST *head              /**< (I) The link head pointer */ 
           );
/**
*  Check whether there is the same group in the link.
 */
static int check_the_same_group( 
           TAGLIST *head,             /**< (I) The link head pointer */ 
           tag_t group_tag            /**< (I) The group tag */ 
           );
//---------------------------------------------------------------------------------------------
int importPersonPlan(
    void * returnValueType
    );

double getCostNumOfPosition(tag_t rateLevel,tag_t user);

//The realization of the create costInfo tag
tag_t createCostInfoTag( 
    tag_t  folderTag ,     	/**< (I) folder tag */ 
    char * costPropertys    /**< (I) costInfo propertys */
    );

//The realization of the set costInfo tag propertys
void setCostInfoTagPropertys( 
    tag_t cost_tag ,        /**< (O) costInfo tag */
    char * costPropertys    /**< (O) costInfo propertys */
    );

//The realization of the get costInfo tag
int getQueryeCostInfoTag( 
    tag_t query_tag ,     	/**< (I) query tag */ 
    char * costPropertys ,  /**< (I) costInfo propertys */
    char * error,           /**< (I) error message */
    tag_t * costInfoTag     /**< (O) costInfo tag */
    );
//---------------------------------------------------------------------------------------------

//SP-EXT-FUN-17. 工时填报产生费用
extern USER_EXT_DLL_API int JCI6updateCostAfterLogTime( 
    EPM_action_message_t msg 
    );

//The realization of the count timesheet cost
void getTimeSheetToCount(
    tag_t timeSheet,       /**< (I) timeSheet tag */ 
    char * status          /**< (I) status */ 
    );

//The realization of the create costinfo tag
void createCostInfoTag_and_set_property(   
    tag_t item_tag ,       /**< (I) item tag */ 
    tag_t relation_tag ,   /**< (I) relation tag */ 
    tag_t project_tag ,    /**< (I) project tag */ 
    char * bill_type ,     /**< (I) bill type */ 
    int year ,             /**< (I) year */ 
    int month ,            /**< (I) month */ 
    int minutes ,          /**< (I) minutes */ 
    tag_t user_tag ,       /**< (I) user tag */ 
    char * jci6_TaskType , /**< (I) task type */ 
    tag_t jci6_Division ,  /**< (I) division */ 
    tag_t jci6_RateLevel , /**< (I) ratelevel */ 
    tag_t jci6_Section ,   /**< (I) section */ 
    char * jci6_Unit ,     /**< (I) unit */ 
    double costNum         /**< (I) costNum */ 
    );

//The realization of the set month info 
void setMonthInfo(
    tag_t cost_tag,        /**< (I) cost tag */ 
    int finish_month,      /**< (I) finish month */ 
    int minutes,           /**< (I) minutes */ 
    char* jci6_Unit ,      /**< (I) unit */ 
    double costNum ,       /**< (I) cost num */ 
    char* act              /**< (I) action */ 
    );

//The realization of the set month info by ation
void setMonthInfoByAtion(
    tag_t cost_tag ,       /**< (I) cost tag */ 
    char* prop_name,       /**< (I) prop name */ 
    double costVaule ,     /**< (I) cost Vaule */ 
    char* act              /**< (I) action */ 
    );

//The realization of the get man hours
double getManHours(
    double minutes         /**< (I) minutes */ 
    );

//The realization of the get man month by hours
double getManMonthByHours(
    double hours           /**< (I) hours */ 
    );

//The realization of the get yuan by hours
long double getYuanByHours(
    double hours,          /**< (I) hours */ 
    double costNum         /**< (I) cost num */ 
    );

//The realization of the get user postion
void getUserPostion(
    char * user_id ,       /**< (I) user id */ 
    tag_t* postion         /**< (O) user postion */ 
    );
//---------------------------------------------------------------------------------------------
// SP-EXT-FUN-09.¡°XSOÐÅÏ¢¡±°æ±ŸµÄÉóºË×ŽÌ¬
extern int  yfjc_ask_jci6_ReviewStatus_value( 
    METHOD_message_t *  message, 
    va_list  args 
    );
//-----------------------------------------------------------------------------------------
// SP-EXT-FUN-08 ¡°XSOÐÅÏ¢¡±°æ±ŸÉÏµÄÓëÃÅÇ°Íš¹ýÂÊÏà¹ØÐÅÏ¢
/**
*  ÉèÖÃ¡°ÌìÊý²îŸà¡±
 */
extern int  yfjc_ask_jci6_DaysGap_value( 
    METHOD_message_t *  message,
    va_list  args 
);
//-----------------------------------------------------------------------------------------
/**
*  ÉèÖÃ¡°ÊÇ·ñÃÅÇ°Íš¹ý¡±
 */
extern int  yfjc_ask_jci6_PassByGate_value( 
    METHOD_message_t *  message,
    va_list  args
);
//-----------------------------------------------------------------------------------------
/**
*  ÉèÖÃ¡°ÑÓºóÌìÊý¡±
 */
extern int  yfjc_ask_jci6_DelayDays_value( 
    METHOD_message_t *  message,
    va_list  args 
);
//-----------------------------------------------------------------------------------------
//SP-EXT-FUN-10. ¡°XSOÐÅÏ¢¡±°æ±ŸÉóºËÈÕÆÚÄ¬ÈÏÖµ
extern USER_EXT_DLL_API int JCI6setXSODefaultDateValue( 
    METHOD_message_t * message, 
    va_list args 
    );

int getXSORevInfo( 
    METHOD_message_t *message, 
    va_list args 
    );

int setPFADefaultValue(
	METHOD_message_t *msg, 
	va_list args
	);
//-----------------------------------------------------------------------------------------
//yfjs_ebp_importTimeLog
int importTimeLog( 
	void * returnValueType 
	);

int createProgramInfoRev( 
	void * returnValueType
	);

int setCostInfoProp(
	char * content,
	char * jci6_CPT,
	char * jci6_Unit,
	tag_t boTag,
	logical isRev
	);

int createCostInfo(
	char * object_name,
	tag_t * obj_tag
	);

int createCostInfoAndAssignToProj(
	char * object_name,
	tag_t * obj_tag,
    tag_t project_tag
	);

int yfjs_createCostInfo(
	void * returnValue
	);

int getUserByUserNum(
	char* userNum,
	tag_t* usertag
	);
//-----------------------------------------------------------------------------------------
//yfjs_ebp_importhyperion
int importhyperion( 
	void * returnValueType 
	);

int getWorkingDates(
    void * returnValueType
    );
//-----------------------------------------------------------------------------------------
//yfjc_ebp_importProgramInfo

int importProgramInfo(
	void * returnValueType
	);

int validate_date ( 
	char *date , 
	date_t *the_dt
	);

int parse_division_default_preference(map< string , vector<string> > &division_default_user_map);

string create(
    char * content
	);

int getUserByUserName(
	char* username,
	tag_t* usertag
	);

int GTAC_ask_double_value( 
	METHOD_message_t *m,
	va_list args
	);

int  getNewestCostInfo(
	METHOD_message_t* msg,
	va_list args
	);

int  getCostTypeStatValue(
	METHOD_message_t* msg,
	va_list args
	);

int  getGateTypeInfoValue(
	METHOD_message_t* msg,
	va_list args
	);

int  getRevBudgetStatValue(
	METHOD_message_t* msg,
	va_list args
	);
//-----------------------------------------------------------------------------------------
int yfjc_ebp_auto_assign_PEF( 
	EPM_action_message_t msg
	);
//---------------------------------------------------------------------------------------------
int yfjc_ebp_auto_assign_PKR_Handler( 
    EPM_action_message_t msg
    );
int do_auto_assign_PKR(
    tag_t task,char* task1,char* task2
    );
int assignUser(tag_t task,
    tag_t user
    );
time_t getFinishDate(
       tag_t scheduleTask
       );
time_t transtoTime_t(
       date_t date
       );
int compareDate(
    time_t time1,
    time_t time2
    );
//---------------------------------------------------------------------------------------------
int yfjc_remove_default_assignee_handler(
    EPM_action_message_t msg
    );
int clear_default_user(
    tag_t task
    );
//---------------------------------------------------------------------------------------------

int checkdata();

int get_Weekday(
    int Year,
    int Month,
    int Day
    );

bool checkdatebyweek(
     date_t date,
     time_t local,
     int cur_year,
     int cur_month,
     int cur_day
     );

bool checkdatebymonth(
     date_t date,
     int cur_year,
     int cur_month
     );

bool checkdatebydays(
     date_t date,
     time_t cur_local,
     char* interval,
     int cur_year,
     int cur_month,
     int cur_day
     );
//---------------------------------------------------------------------------------------------

//int calculation(
//    tag_t * secondary_objects,
//    int count,
//    char * unit,
//    char * costType,
//    char * costPhaseType,
//    double * value
//    );
//int searchScheduleTask(
//    char* project_id,
//    char * gageType,
//    int  * count,
//    tag_t ** obj_tags
//    );

//---------------------------------------------------------------------------------------------

//long double DataPrecision(
//            const long double old_double
//            );
int DataPrecision(
	long double old_double, 
	long double *return_double
	);
//---------------------------------------------------------------------------------------------

int listAllSMTELeader( 
    TAGLIST *head, 
    int *allTagCnt
    );
//---------------------------------------------------------------------------------------------
int isLeadEngineer( 
	METHOD_message_t *METHOD_message_t, 
	va_list           args 
	);
//---------------------------------------------------------------------------------------------
//The realization of the get user division and section
void getUserDivisionSection(
    tag_t user_tag ,      /**< (I) user id */ 
    tag_t* division ,     /**< (O) user division */ 
    tag_t* section        /**< (O) user section */ 
    );
void getGroupDivisionSection(
    tag_t user_group ,   /**< (I) group id */ 
    tag_t* division ,    /**< (O) user division */ 
    tag_t* section       /**< (O) user section */ 
    );
//---------------------------------------------------------------------------------------------
int  JCI6_ask_timesheetentry_hour( 
	METHOD_message_t *  message, 
	va_list  args
	);
//---------------------------------------------------------------------------------------------
int JCI6_ask_timesheetentry_isOverDue( 
    METHOD_message_t *msg,
    va_list args 
    );
//---------------------------------------------------------------------------------------------
int JCI6_updateProjCloseDate( 
    METHOD_message_t *msg,
    va_list args 
    );
//---------------------------------------------------------------------------------------------
int yfjc_deleteCostInfo_Handler(
	EPM_action_message_t msg
	);

int yfjc_create_lcc_cost( 
	EPM_action_message_t msg
    );
    
int yfjc_delete_lcc_cost( 
    EPM_action_message_t msg
    );

int yfjc_assign_manager( 
	EPM_action_message_t msg
	);

/**************************************************************************************************
                         Shanghai
**************************************************************************************************/
int jc6_create_item( void *retValType );
void Split( string strArg, char spliter, vector<string> &ans );
void RemoveLiner( string &in, string &out );
void CreateLogFile(char* FunctionName);
void WriteLog(const char* format, ...);
void CloseLog(void);


/**************************************************************************************************
                         œ«³É±ŸŒÆ»®¶ÔÓŠµÄÈË¹€ºÍ·ÇÈË¹€³É±Ÿ×ª»»ÎªcostInfo¶ÔÏó
**************************************************************************************************/
int getNewestCostInfo(METHOD_message_t* msg, va_list args);
int yfjc_ebp_transToCostInfo( void * returnValueType);
/*************modify by wuh 8-13*********/
void tr_ECHO(char *format, ...);
//int transToCostInfo(tag_t programInfoItem);
//void soa_cycleScheduleTask( tag_t scheduleSummaryTask,  tag_t *schedulemembers, tag_t *resource_tags,int schedulemembersNum,vector<tag_t> &costInfo_vec);
//void soa_getLabourMemo( tag_t scheduleTask,  tag_t *schedulemembers, tag_t *resource_tags,int schedulemembersNum, vector<tag_t> &costInfo_vec);
//int refreshForecast(FILE* logFile,logical writeLog,tag_t *programInfos,int start_no,int end_no);
//int transToCostInfo(tag_t programInfoItem,vector<tag_t> extUserVec,int extCnt);
//void soa_getLabourMemo( tag_t scheduleTask,  tag_t *schedulemembers, tag_t *resource_tags,int schedulemembersNum,vector<tag_t> extUserVec,int extCnt,vector<tag_t> &costInfo_vec);
//void soa_cycleScheduleTask( tag_t scheduleSummaryTask,  tag_t *schedulemembers, tag_t *resource_tags, int schedulemembersNum, vector<tag_t> extUserVec,int extCnt,vector<tag_t> &costInfo_vec);
/*************modify by wuh 8-13*********/

//double getCostNumOfPosition(tag_t rateLevel,tag_t user);
//void soa_getNonLabourMemo( tag_t scheduleTask, vector<tag_t> &costInfo_vec);
//void soa_getStartAndFinishDate(tag_t scheduleTask,date_t *start_date, date_t *toady_date);
//int soa_intervalOfDate(date_t date1,date_t date2);
//void soa_getCostOfEachMonth(double costOfEachDay, double labourMemoNum, date_t start_date, date_t finish_date, char* costType, char* cpt, char* taskType, tag_t division, tag_t section, tag_t rateLevel, tag_t user, double costNum, char* labourOrNon,vector<tag_t> &costInfo_vec);
//void fillEveryMonth(int year,date_t *nextjan1,date_t *dec1,date_t *nov1,date_t *oct1,date_t *sep1,date_t *aug1,date_t *jul1,date_t *jun1,date_t *may1,date_t *apr1,date_t *mar1,date_t *feb1,date_t *jan1);
//void soa_createCostInfoAndSetPro(char* costInfoName,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, char* jci6_Unit, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec, vector<tag_t> &costInfo_vec);
//tag_t soa_createCostInfo(char * object_name,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, char* jci6_Unit, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec);

//int soa_DataPrecision(long double old_double, long double *return_double);
//void soa_createCostInfoByLabOrNon(double costNum,char* labourOrNon,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec, vector<tag_t> &costInfo_vec);
//double soa_getManMonthByHours(double hours);
//double soa_getYuanByHours(double hours, double costNum);
//void existMap(map<tag_t,tag_t> &target_map,tag_t user,logical *existOrNot);
//tag_t getSecond(map<tag_t,tag_t> &target_map,tag_t user);
//void getDivisionSectionByPositionName(tag_t user_tag ,char* position_name ,tag_t* division ,tag_t* section);
/**************************************************************************************************
                        Timesheet±ê×¢±ØÌîÏî²¢ÏÞ¶š¿ÉÑ¡ÀàÐÍ
**************************************************************************************************/

//BMF
//modify by wuwei
extern USER_EXT_DLL_API int JCI6TimesheetMinAlert( METHOD_message_t *msg, va_list args );

//BMF
//modify by wuwei
extern USER_EXT_DLL_API int JCI6preActionFillInfo( METHOD_message_t *msg, va_list args );

//BMF
//modify by wuwei
extern USER_EXT_DLL_API int JCI6checkOverDueLogTime( 
    METHOD_message_t *msg, 
    va_list args
    ) ;
	


void getPreValue(char *postion_name,char* user_id,char **newRightStr);
int getBillTypeByDate(date_t date);
int getMinutesForDay(tag_t timesheetentry_tag,char* user_id,date_t date);
int getMinutesForDayAndSchedule(tag_t timesheetentry_tag,char* user_id,date_t date,char* scheduletask_id);
void GetBeforeEightHoursDate( date_t finish_date ,int beforehours , date_t *before_Date );
int isLeap(int year);
int getWeekendOrNot(int year,int month,int day);
char* rtrim(char *s);
/**************************************************************************************************
                       »ñÈ¡DesignReqµÄValidValue,MaxValue,MinvalueÊôÐÔµÄÖµ
**************************************************************************************************/
int JCI6_ask_DesignReq_ValidValue( METHOD_message_t *msg, va_list args );

// tyl 2015/01/14 设置项目重开时间&项目离开Phase0的时间
int JCI6_update_programinfo_reopenTime_and_phase0Time( METHOD_message_t *msg, va_list args );

int soa_DataPrecision(long double old_double, long double *return_double);

string getNowtime2();

#ifdef __cplusplus
}
#endif
