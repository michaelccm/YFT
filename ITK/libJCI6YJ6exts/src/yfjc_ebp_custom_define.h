/*=============================================================================
                Copyright (c++) 2011 ORIGIN Corporation
                   Unpublished - All Rights Reserved
===============================================================================
File description:

   Filename: yfjc_ebp_custom_define.h

   Function declarations
       
	   costom define
===============================================================================
DATE           Name             Description of Change
07-Mar-2013    zhanggl          creation
$HISTORY$
===============================================================================*/
#ifdef __cplusplus
extern "C" {
#endif

#define TEST_ERROR(E) \
	if ((E) != ITK_ok) return(E);

#define HANDLE_ERROR(E) \
        { EMH_store_error(EMH_severity_error,(E)); return(E); }

#define HANDLE_ERROR_S1(E,S) \
        { EMH_store_error_s1(EMH_severity_error,(E),(S)); return(E); }

#define DOFREE(obj)								      \
{												\
	if(obj)										\
	{											\
		MEM_free(obj);							\
		obj = NULL;								\
	}											\
}

// Workflow

#define YJ6_DEBUG_STR      "debug"
#define YJ6_TRUE           "true"
#define YFJC_DEBUG         1

//Search
#define YFJC_SEARCHCOSTINFOBYDISCIPLINE				"YFJC_SearchCostInfoByDiscipline"
#define YFJC_SEARCHGROUPOFDISCIPLINE				"YFJC_SearchGroupOfDiscipline"
#define YFJC_SEARCHDISCIPLINE	        			"YFJC_SearchDiscipline"
#define YFJC_SEARCHDISCIPLINEBYID                   "YFJC_SearchDisciplineByID"
#define YFJC_SEARCHCOSTINFOBYIMPORTPERSONPLAN	    "YFJC_SearchCostInfoByImportPersonPlan"
#define YFJC_SEARCHTIMESHEETENTRY	                "YFJC Query for Timesheet"
//2016-04-07	modify by ck
#define YFJC_SEARCHTIMESHEETENTRY_BYSCHEDULE        "YFJC_Search_Timesheet"
//2016-04-07	modify by ck

// relation type

#define IMAN_EXTERNAL_OBJECT_LINK            "IMAN_external_object_link"
#define TC_PROGRAM_PREFERRED_ITEMS           "TC_Program_Preferred_Items"

// preference

#define YFJC_GATE_PHASE_MAPPING                                "YFJC_Gate_Phase_Mapping"
#define YFJC_GATE_TYPES_MAPPING                                "YFJC_Gate_Types_Mapping"
#define YFJC_PROJECT_REPOSITORY_TEMPLATE_LOCATION              "YFJC_Project_Repository_Template_Location"
#define YFJC_XSO_Dates_Before_Gate		                       "YFJC_XSO_Dates_Before_Gate"
#define PREFERENCE_YFJC_INTERVAL_OF_LOG_TIME                   "YFJC_Interval_Of_Log_Time"
#define YFJC_PKRSET_GATE_MAPPING                               "YFJC_PKRSet_Gate_Mapping"
#define YFJC_USER_RATE_MAPPING                                 "YFJC_User_Rate_Mapping"
#define YFJC_HOLIDAY_ALSO_WEEKEND                              "YFJC_Holiday_Also_Weekend"
#define YFJC_PROGRAMSTATES_TOUPDATE_CLOSEDATE                  "YFJC_programStates_ToUpdate_CloseDate"

// Object type name

#define YJ6_XSO_REVISION_TYPE               "ItemRevision"
#define JCI6_COSTINFO                   	"JCI6_CostInfo"
#define JCI6_PROGRAMINFO                   	"JCI6_ProgramInfo"
#define JCI6_XSOREVISION_TYPE               "JCI6_XSORevision"
#define FOLDER                              "Folder"
#define TIMESHEETENTRY                   	"TimeSheetEntry"
#define SCHEDULE                         	"Schedule"

// properties

#define JCI6_CATEGORY                   	"jci6_Category"
#define JCI6_PROGRAMID                   	"jci6_ProgramID"	
#define JCI6_REVISION                   	"revision"
#define JCI6_ACUTALVALUE                    "jci6_AcutalValue"
#define JCI6_VALIDVALUE                     "jci6_ValidValue"
#define JCI6_MAXVALUE                       "jci6_MaxValue"
#define JCI6_MINVALUE                       "jci6_MinValue"
#define JCI6_REVIEWDATE                     "jci6_ReviewDate"
#define SCHEDULE_TAG                        "schedule_tag"
#define PROJECT_IDS                         "project_ids"
#define PROJECT_ID                          "project_id"
#define PROJECT_TEAM                        "project_team"

#define JCI6_COSTTYPE						"jci6_CostType"
#define JCI6_CPT                   			"jci6_CPT"
#define JCI6_DIVISION                   	"jci6_Division"
#define JCI6_RATELEVEL                   	"jci6_RateLevel"
#define JCI6_USER                   		"jci6_User"               	
#define JCI6_YEAR                   		"jci6_Year"

#define JCI6_JAN                  			"jci6_Jan"
#define JCI6_FEB                   			"jci6_Feb"
#define JCI6_MAR                   			"jci6_Mar"
#define JCI6_APR                   			"jci6_Apr"
#define JCI6_MAY                   			"jci6_May"	
#define JCI6_JUN                  			"jci6_Jun"
#define JCI6_JUL                   			"jci6_Jul"               	
#define JCI6_AUG                  			"jci6_Aug"
#define JCI6_SEP                  			"jci6_Sep"
#define JCI6_OCT							"jci6_Oct"
#define JCI6_NOV                  			"jci6_Nov"
#define JCI6_DEC                  			"jci6_Dec"

#define JCI6_OEMNAME                 		"jci6_OEMName"
#define JCI6_EPIC_PN                   		"jci6_EPIC_PN"
#define JCI6_EQU                   			"jci6_EQU"
#define JCI6_PROGRAMSEC                   	"jci6_ProgramSec"
#define JCI6_PROGRAMDIVI                   	"jci6_ProgramDivi"	
#define JCI6_PROJECTTL                  	"jci6_ProjectTL"
#define JCI6_RESPONSIBILITY             	"jci6_Responsibility"
#define JCI6_PROGRAMSTATE                   "jci6_ProgramState"               	
#define JCI6_PRODUCT                  		"jci6_Product"
#define JCI6_CATEGORY                  		"jci6_Category"
#define JCI6_TYPE							"jci6_Type"

#define JCI6_TARGETPOS                  	"jci6_TargetPos"
#define JCI6_STARTDATE                  	"jci6_StartDate"
#define JCI6_ENDDATE                  		"jci6_EndDate"
#define JCI6_SOP                   			"jci6_SOP"
#define JCI6_SOPFY                   		"jci6_SOPFY"
#define JCI6_MODELYEAR                   	"jci6_ModelYear"
#define JCI6_FUNCTION                   	"jci6_Function"	
#define JCI6_FRONTSS                  		"jci6_FrontSS"
#define JCI6_REARSS                   		"jci6_RearSS"               	
#define JCI6_TRACK                  		"jci6_Track"
#define JCI6_RECLINER                  		"jci6_Recliner"
#define JCI6_PUMP							"jci6_PUMP"
#define JCI6_VTA                  			"jci6_VTA"
#define JCI6_LATCH                  		"jci6_Latch"
#define JCI6_OEMMODELNAME                   "jci6_OEMModelName"
#define JCI6_UNIT                  			"jci6_Unit"

#define DEFAULT_RATE                  	    "default_rate"
#define OWNING_PROJECT                      "owning_project"
#define JCI6_GATEDATE			     		"jci6_GateDate"
#define RELEASE_STATUS_LIST			    	"release_status_list"
#define OBJECT_NAME			    		    "object_name"
#define OBJECT_DESC			    		    "object_desc"
#define DATE_RELEASED				    	"date_released"
#define ITEMS_TAG					        "items_tag"
#define REVISION_LIST					    "revision_list"
#define JCI6_GATE					        "jci6_Gate"
#define ITEM_ID					            "item_id"
#define FINISH_DATE					        "finish_date"
#define TIMESHEET_DATE					    "date"
#define TIMESHEET_MINUTES					"minutes"
#define TIMESHEET_USERTAG					"user_tag"
//2016-04-07	modify by ck
#define TIMESHEET_SCHEDULETASK				"scheduletask_tag"
//2016-04-07	modify by ck

#define OBJECT_TYPE					                "object_type"
#define SCHEDULETASK_TAG				            "scheduletask_tag"
#define USER_TAG				                    "user_tag"
#define OWNING_GROUP				                "owning_group"
#define USER_ID				                        "user_id"
#define JCI6_OPERATION                              "jci6_Operation"
#define IS_ACTIVE                                   "is_active"

#define DATE				                        "date"
#define MINUTES				                        "minutes"
#define BILL_TYPE				                    "bill_type"
#define JCI6_TASKTYPE				                "jci6_TaskType"
#define BILLRATE_TAG				                "billrate_tag"

//modify by wuwei ---schedulemember_taglist
#define SCHEDULEMEMBER_TAGLIST				        "fnd0Schedulemember_taglist"
#define RESOURCE_TAG				                "resource_tag"
#define DISCIPLINE				                    "Discipline"
#define TC_DISCIPLINE_MEMBER				        "TC_discipline_member"

#define USER				                        "User"
#define LIST_OF_DISCIPLINE				            "list_of_discipline"
#define DISPLAY_NAME				                "display_name"
#define PARENT				                        "parent"
#define DEFAULT_GROUP				                "default_group"
#define TC_TYPE				                        "tc_type"
#define COSTVALUE_FORM_TAG				            "costvalue_form_tag"
#define COST				                        "cost"
#define JCI6_SECTION				                "jci6_Section"
#define UNASSIGNED					                "unassigned"

// split char

#define VERTICALBAR                         "|"

// other
#define RELEASEDSTATUS						"Released"
#define JCI6_REVSEQINIT                   	"A"
#define JCI6_UNITYUAN						"Yuan"
#define JCI6_UNITMANMONTH					"ManMonth"
#define JCI6_UNITHOUR						"Hour"
#define JCI6_PROJECT_TEAMROLE               "Project Team Administrator"

#define MSGPAR                              "projectRepositoryTemplateLocation"
#define TECHCENTER                          "Technical Center"
#define JCI6_DIVISION_LOV                   "JCI6_Division" 
#define JCI6_SECTION_LOV                    "JCI6_Section"
#define JCI6_SMTELEADER_LOV                 "JCI6_SMTELeader"
#define SCOPE                               "Range"
#define JCI6_Budget							"Budget"
#define JCI6_PASS					        "JCI6_Pass"
#define JCI6_FAIL					        "JCI6_Fail"
#define ONE					                "1"
#define TWO					                "2"
#define THREE					            "3"
#define FOUR					            "4"
#define FIVE					            "5"
#define LANGUAGENAME                        "zh_CN"

#define EQUAL                               "="
#define NOTEQUAL                            "!="
#define MIDDLE                              "<X<"
#define MIDDLEEQU                           "<=X<="

#ifdef __cplusplus
}
#endif
