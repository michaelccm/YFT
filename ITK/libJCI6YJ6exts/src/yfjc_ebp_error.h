/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: YJ6_error.h

   Declaration:
       
	   Error codes returned by the ITK module.
============================================================================================================
DATE           Name             Description of Change
24-Feb-2013    zhanggl          creation
$HISTORY$
============================================================================================================*/

#ifndef FOTON_REPORT_SERVICE_DEFINES
#define FOTON_REPORT_SERVICE_DEFINES


//user errors define

#define ERROR_Gate_NOT_FOUND (EMH_USER_error_base +2)

#define ERROR_XSOINFO_NULL_OR_MORETHANONE (EMH_USER_error_base + 101)

#define ERROR_NO_FIND_GROUP (EMH_USER_error_base + 102)

#define ERROR_NO_FIND_DIVISION (EMH_USER_error_base + 103)

#define ERROR_CLEAR_XSOREV_REVIEWDATE (EMH_USER_error_base + 104)

#define ERROR_ADD_NEWXSOREV_TO_TARGET (EMH_USER_error_base + 105)

#define ERROR_REMOVE_OLDXSOREV_FORM_TAEGET (EMH_USER_error_base + 106)

#define ERROR_ADD_OLDXSOREV_TO_REFERENCE (EMH_USER_error_base + 107)

#define ERROR_SCHEDULE_ALREADY_EXISTS_IN_OTHER_PROJECTS (EMH_USER_error_base + 110)

#define ERROR_EXITSTING_SCHEDULE_OF_THE_PROJECT (EMH_USER_error_base + 111)

#define ERROR_NO_FIND_LATEST_PROGRAMINFOREV (EMH_USER_error_base + 141)

#define ERROR_PARAMETER_NAME_NULL (EMH_USER_error_base + 161)

#define ERROR_PARAMETER_VALUE_NULL (EMH_USER_error_base + 162)

#define ERROR_PREFERENCE_NOT_FOUND (EMH_USER_error_base + 181)

#define ERROR_CREATE_ITEM_FAILED	(EMH_USER_error_base + 200)

#define ERROR_PROGRAMINFO_NOT_FOUND (EMH_USER_error_base + 201)

#define ERROR_PFAREVISION_NOT_FOUND (EMH_USER_error_base + 202)

#define ERROR_PREFERENCE_INPROCESS_NOT_FOUND (EMH_USER_error_base + 203)

#define ERROR_PEF_LATEST_REVISION_NOT_FOUND (EMH_USER_error_base + 204)

#define ERROR_PFA_REVISION_NOT_FOUND (EMH_USER_error_base + 205)

#define ERROR_CURRENTGATE_NOT_FOUND (EMH_USER_error_base + 206)

#define ERROR_PROJECTTL_NOT_FOUND (EMH_USER_error_base + 207)

#define ERROR_SMTELEADER_NOT_FOUND (EMH_USER_error_base + 208)

#define ERROR_PKRSET_NOT_FOUND (EMH_USER_error_base + 209)

#define ERROR_OVER_EIGHT_HOURS ( EMH_USER_error_base + 210 )

#define ERROR_OTHER_USER_LOCK ( EMH_USER_error_base + 211 )

#define ERROR_TimeSheetEntry_Create_Date_Error (EMH_USER_error_base + 300)

#define ERROR_TimeSheetEntry_Entry_Date_Error (EMH_USER_error_base + 301)

#define ERROR_TimeSheetEntry_Entry_minute_Error (EMH_USER_error_base + 302)

#define ERROR_TimeSheetEntry_Entry_ItsProject_Inactive_Error (EMH_USER_error_base + 310)

#define ERROR_NO_FIND_GATE (EMH_USER_error_base + 350)

#define ERROR_NO_FIND_PROPERTY (EMH_USER_error_base + 351)

#define ERROR_NO_FIND_PREFER (EMH_USER_error_base + 352)

#define ERROR_NO_FIND_VALUE (EMH_USER_error_base + 353)

#define ERROR_NOT_LEADER_ENGINEER ( EMH_USER_error_base + 401 )

#define ERROR_NO_PROJECT ( EMH_USER_error_base + 402 )

#define ERROR_NO_PROGRAMINFO ( EMH_USER_error_base + 403 )

//2014-6-4 mengyawei added
//yfjc_ebp_Delete_schedule_task_check

#define ERROR_SCHEDULE_DELETE_ERROR1 ( EMH_USER_error_base + 404 )

#define ERROR_SCHEDULE_DELETE_ERROR2 ( EMH_USER_error_base + 405 )

#define ERROR_SCHEDULE_DELETE_ERROR3 ( EMH_USER_error_base + 406 )

#define ERROR_TIME_SHEET_ENTRY_PAST_DUE 	( EMH_USER_error_base + 407 )//001 TimeSheetEntry over due

#define ERROR_HANDLER_PRE_NOT_FOUND 	( EMH_USER_error_base + 408 )//handler para not set

//2014-6-4 mengyawei added

//2014-8-5	mengyawei added

#define ERROR_HANDLER_ARG_NOT_DEFINE		(EMH_USER_error_base + 409)		//handler参数没有配置

#define ERROR_HAS_NO_FORM					(EMH_USER_error_base + 410)		//流程目标对象中没有用人计划表单！

#define ERROR_FORM_ALREAD_RELEASED			(EMH_USER_error_base + 411)		//流程目标对象中的用人计划表单已经发布！

#define ERROR_ALREAD_PASS_ACCOUNT_DATE		(EMH_USER_error_base + 412)		//已经超过结账日，不可审批！

#define ERROR_QRY_NOT_DEFINE				(EMH_USER_error_base + 413)		//查询没有定义

#define ERROR_LOV_NOT_DEFINE				(EMH_USER_error_base + 414)		//LOV

#define ERROR_FIND_NO_RATE				(EMH_USER_error_base + 415)

#define ERROR_FIND_NO_DATASET				(EMH_USER_error_base + 416)

//2014-8-5	mengyawei added

// tyl 2015/01/14 检查审批时限及转化Forecast
#define ERROR_SET_PREFERENCE		(EMH_USER_error_base + 417)	 //首选项%1s配置有误
#define ERROR_SET_PROCESS_PARAMETER		(EMH_USER_error_base + 418)	   //流程中的%1s参数设置异常
#define ERROR_EXTPLAN_OVERDUE		(EMH_USER_error_base + 419)	   //超过结账日表单：\n%1s
// tyl 2015/01/14 检查审批时限及转化Forecast


#endif
