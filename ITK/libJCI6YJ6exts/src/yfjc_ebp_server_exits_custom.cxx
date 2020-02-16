/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_server_exits_custom.cxx
   Module  : user_exits

   Load the custom library(ies) registered in the preference file and
   Call the entry point function pointer to register custom exits
============================================================================================================
DATE           Name             Description of Change
04-Mar-2013    zhanggl		    creation
$HISTORY$
============================================================================================================*/
#include <tc/preferences.h>
#include <sa/sa.h>
#include <ss/ss_errors.h>
#include "yfjc_ebp_server_exits_custom.h"
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

static int JCI6_Copy_DRT(METHOD_message_t *msg, va_list args);
static int  JCI6_ask_schedule_tasks( METHOD_message_t *  message, va_list  args );

/**************************************************************************
*
*  JCI6_ask_schedule_tasks
*
*  This method is to get all the schedule tasks by project object.
*
***************************************************************************/
int  JCI6_ask_schedule_tasks( METHOD_message_t *  message, va_list  args )
{
	int ifail = ITK_ok;
	tag_t objTag = null_tag;
	char* project_id = NULL;
	char *entries[1];
	char *values[1];
	tag_t query = NULLTAG;
	tag_t   prop_tag = va_arg( args, tag_t );
	int*   tasks_num = va_arg( args, int* );
    tag_t**  tasks = va_arg( args, tag_t** );

	entries[0] = TC_text("item_id");
	printf("Information: query's entry is %s\n", entries[0]);

    METHOD_PROP_MESSAGE_OBJECT(message, objTag);

	*tasks_num = 0;
	*tasks = NULL;

	ITKCALL(ifail = AOM_ask_value_string(objTag, "item_id", &project_id));


	ITKCALL(ifail = QRY_find("YFJC_Query_ScheduleTasks_by_Schedule", &query) );
    if (NULLTAG == query)
    {
        printf("Error: can't find query %s\n", "YFJC_Query_ScheduleTasks_by_Schedule");
        return ifail;
    }
    values[0] = project_id;
    ITKCALL( QRY_execute(query, 1, entries, values, tasks_num, tasks) );
	printf("Information: Find %d scheduletasks with schedule id: %s. \n", *tasks_num, project_id);
	MEM_free(project_id);

    return ifail;

}

/**************************************************************************
*
*  JCI6_ask_hasObserver
*
*  This method is to ask whether there is observer in the schedule.
*
***************************************************************************/
int  JCI6_ask_hasObserver( METHOD_message_t *  message, va_list  args )
{
	int ifail = ITK_ok;
	tag_t objTag = null_tag;
	int num_member = 0;
	tag_t* members = NULL; /*free*/
	int i = 0, j = 0;
	int member_priv = 2;
	tag_t resource = NULLTAG;
	tag_t type_tag = NULLTAG;
	tag_t user_type_tag = NULLTAG;
	TC_preference_search_scope_t current_scope;
	int pref_count = 0;
	char** pref_value = NULL; /*free*/
	char  userid[SA_user_size_c+1] = {'\0'};
	tag_t   prop_tag = va_arg( args, tag_t );
    logical*  value = va_arg( args, logical* );

    METHOD_PROP_MESSAGE_OBJECT(message, objTag);
	*value = false;

    ifail = PREF_ask_search_scope( &current_scope );
    if ( ifail != ITK_ok )
    {
        return ifail;
    }

    /* Set the scope to site preference */
    ifail = PREF_set_search_scope( TC_preference_site );
    if ( ifail != ITK_ok )
    {
        return ifail;
    }


    ifail = PREF_ask_char_values( "YFJC_user_ids_in_observers_of_schedule", &pref_count, &pref_value);
    if ( ( ifail != ITK_ok ) && ( ifail != PF_NOTFOUND ) )
    {
        return ifail;
    }
    if ( ifail == PF_NOTFOUND )
    {
        ifail = ITK_ok;
        pref_count = 0;
        EMH_clear_last_error( PF_NOTFOUND );
		ifail = PREF_set_search_scope( current_scope );
		return ifail;
    }

    if ( pref_count == 0 )
    {
		ifail = PREF_set_search_scope( current_scope );
        return ITK_ok;
    }
    ifail = PREF_set_search_scope( current_scope );



	ITKCALL(ifail = TCTYPE_find_type("User", "User", &user_type_tag));

	//modify by wuwei --- schedulemember_taglist 
	ITKCALL(ifail = AOM_ask_value_tags(objTag, "fnd0Schedulemember_taglist", &num_member, &members));
	for(i=0; i<num_member; i++)
	{
		ITKCALL(ifail = AOM_ask_value_int(members[i], "member_priv", &member_priv));
		if(member_priv == 0)
		{
			ITKCALL(ifail = AOM_ask_value_tag(members[i], "resource_tag", &resource));
			ITKCALL(ifail = TCTYPE_ask_object_type (resource, &type_tag));
			if(type_tag == user_type_tag)
			{
				ITKCALL(ifail = SA_ask_user_identifier(resource, userid));
				for(j=0; j<pref_count; j++)
				{
					if(tc_strcmp("*", pref_value[j]) == 0 || tc_strcmp(userid, pref_value[j]) == 0)
					{
						*value = true;
				        break;
					}
				}
				if((*value) == true)
				    break;
			}			
		}
	}

	if(num_member>0)
	{
		MEM_free(members);
		members = NULL;
		num_member = 0;
	}

	if(pref_count > 0)
	{
		MEM_free( pref_value );
		pref_value = NULL;
		pref_count = 0;
	}

    return ifail;

}

//shanghai, add a post-action to copy the DRT Revision to new DR with "jci6_smtet" relation while revising PKR Revision.
static int JCI6_Copy_DRT(METHOD_message_t *msg, va_list args)
{
    int ifail                         = ITK_ok;
    tag_t source_rev                  = NULLTAG;
	tag_t new_rev                    = NULL;
	tag_t relation                    = NULLTAG;
	int   count_second_tags           = 0;
	tag_t* second_tags                = NULL; /*Free*/
	tag_t  old_DR                     = NULLTAG;
	tag_t  new_DR                     = NULLTAG;
	tag_t  drt_tag                    = NULLTAG;
        tag_t  new_relation               = NULLTAG;
	int i                             = 0;
	char  type_name[TCTYPE_name_size_c+1]  = {'\0'};
	char  rev_id[ITEM_id_size_c+1] = {'\0'};

    new_rev = va_arg(args, tag_t);
    ITKCALL(ifail = ITEM_ask_item_of_rev(new_rev, &source_rev));
    ITKCALL(ifail = ITEM_list_all_revs(source_rev, &count_second_tags, &second_tags));
    source_rev = second_tags[0];
    MEM_free(second_tags);
    count_second_tags = 0;
    second_tags = NULL;
    ITEM_ask_rev_id(new_rev,rev_id);
    if(source_rev == new_rev)
    {
	ECHO("Information: This is not revise case.\n");
        return ifail;
    }
    ECHO("source rev: %d; new Rev: %d, rev id: %s\n", source_rev, new_rev, rev_id);
    ITKCALL(ifail = GRM_find_relation_type("JCI6_SMTE", &relation));
    ITKCALL(ifail = GRM_list_secondary_objects_only(source_rev, relation, &count_second_tags, &second_tags));
    if(count_second_tags <= 0 || second_tags == NULL)
    {
	ECHO("Error: Fail to find any one secondary object under PKR revision\n");
	return ifail;
    }
    for(i=0; i<count_second_tags; i++)
    {
		ITKCALL(ifail = TCTYPE_ask_object_type (second_tags[i], &relation));
		ITKCALL(ifail = TCTYPE_ask_name(relation, type_name));
		if(tc_strcmp(type_name, "JCI6_DesignReq") == 0)
		{
			old_DR = second_tags[i];
			break;
		}
	}
	if(old_DR == NULLTAG)
	{
		ECHO("Error: Fail to find any one DR under PKR revision\n");
		MEM_free(second_tags);
	    count_second_tags = 0;
	    second_tags = NULL;
		return ifail;
	}
	MEM_free(second_tags);
	count_second_tags = 0;
	second_tags = NULL;

	//get drt revision
	ITKCALL(ifail = GRM_find_relation_type("JCI6_SMTET", &relation));
	ITKCALL(ifail = GRM_list_secondary_objects_only(old_DR, relation, &count_second_tags, &second_tags));
	if(count_second_tags <= 0 || second_tags == NULL)
    {
		MEM_free(second_tags);
		count_second_tags = 0;
	    second_tags = NULL;
		ECHO("Error: Fail to find any one DRTRevision under DR\n");
		return ifail;
    }
    drt_tag = second_tags[0];
    MEM_free(second_tags);
    count_second_tags = 0;
	second_tags = NULL;

	//get new DR
	ITKCALL(ifail = GRM_find_relation_type("JCI6_SMTE", &relation));
	ITKCALL(ifail = GRM_list_secondary_objects_only(new_rev, relation, &count_second_tags, &second_tags));
	if(count_second_tags <= 0 || second_tags == NULL)
	{
		ECHO("Error: Fail to find any one secondary object under new PKR revision\n");
		return ifail;
	}
	for(i=0; i<count_second_tags; i++)
	{
		ITKCALL(ifail = TCTYPE_ask_object_type (second_tags[i], &relation));
		ITKCALL(ifail = TCTYPE_ask_name(relation, type_name));
		if(tc_strcmp(type_name, "JCI6_DesignReq") == 0)
		{
			new_DR = second_tags[i];
			break;
		}
    }
    if(new_DR == NULLTAG)
	{
		ECHO("Error: Fail to find any one DR under new PKR revision\n");
		MEM_free(second_tags);
		count_second_tags = 0;
		second_tags = NULL;
		return ifail;
	}
	MEM_free(second_tags);
	count_second_tags = 0;
	second_tags = NULL;

    //create relation between new DR and DRT Revision
    ITKCALL(ifail = GRM_find_relation_type("JCI6_SMTET", &relation));
    ITKCALL(ifail = GRM_create_relation(new_DR, drt_tag, relation, NULLTAG, &new_relation));
    ITKCALL(ifail = GRM_save_relation(new_relation));
    return ifail;
}

   extern DLLAPI   int USERSERVICE_custom_register_handlers( int *decision, va_list args )
    {
        int status         = ITK_ok;

        *decision = ALL_CUSTOMIZATIONS;

		//2014-8-5 mengyawei added
		//SP-CUS-001	检查审批时限及转化Forecast
		ITKCALL(status = EPM_register_action_handler(
			"yfjc-transfer-forecast-costinfo",
			"yfjc-transfer-forecast-costinfo",
			yfjc_transfer_forecast_costinfo));
		if(status == ITK_ok)
		{
			fprintf(stdout,"Register Action Handler yfjc-transfer-forecast-costinfo successful !\n");
		}else{
			fprintf(stdout,"Register Action Handler yfjc-transfer-forecast-costinfo failed %d !\n",status);
		}

		 //   SP-CUS-002 外包工时转化 TimesheetEntry
		ITKCALL(status = EPM_register_action_handler(
			"yfjc-transfer-timesheetentry",
			"yfjc-transfer-timesheetentry",
			yfjc_transfer_timesheetentry));
		if(status == ITK_ok)
		{
			fprintf(stdout,"Register Action Handler yfjc-transfer-timesheetentry successful !\n");
		}else{
			fprintf(stdout,"Register Action Handler yfjc-transfer-timesheetentry failed %d !\n",status);
		}
		ITKCALL(status = EPM_register_action_handler(
			"yfjc-notify-with-signoff-report",
			"yfjc-notify-with-signoff-report",
			yfjc_notify_with_signoff_report));
		if(status == ITK_ok)
		{
			fprintf(stdout,"Register Action Handler yfjc-notify-with-signoff-report successful !\n");
		}else{
			fprintf(stdout,"Register Action Handler yfjc-notify-with-signoff-report failed %d !\n",status);
		}
        //SP-CUS-001／设置外包工时批准时限
	  //2014-6-11 mengyawei added
		ITKCALL (EPM_register_action_handler(
			"yfjc-reject-overdue-timesheet",
			"yfjc-reject-overdue-timesheet", 
			yfjc_reject_overdue_timesheet ) );
        // SP-HANDLER-FUN-03. PEF发布流程自动指派参与人--liuc
        ITKCALL ( status = EPM_register_action_handler(
            "yfjc-auto-assign-PEF", 
            "yfjc-auto-assign-PEF",
             yfjc_ebp_auto_assign_PEF));

        // SP-HANDLER-FUN-01.自动升版“XSO信息”--zhanggl
        ITKCALL ( status = EPM_register_action_handler(
            "yfjc-revise-XSOInfo",
            "yfjc-revise-XSOInfo", 
             yfjc_revise_XSOInfo_Handler ) );

          // SP-HANDLER-FUN-04. PKR计划任务执行流程自动指派--liujm
        ITKCALL ( status = EPM_register_action_handler(
            "yfjc-auto-assign-PKR",
            "yfjc-auto-assign-PKR", 
            yfjc_ebp_auto_assign_PKR_Handler ) );

        // SP-EXT-FUN-17.工时填报产生费用 start --zhangyn
        ITKCALL ( status = EPM_register_action_handler(
            "yfjc-updateCostAfterLogTime",
            "yfjc-updateCostAfterLogTime", 
            JCI6updateCostAfterLogTime ) );

        // SP-HANDLER-FUN-02. 自动清除流程任务的默认参与者---map
		ITKCALL( status = EPM_register_action_handler(
			"yfjc-remove-default-assignee",
			"remove default assignee",
			yfjc_remove_default_assignee_handler ) );

		// 新增handler二次开发项，用于项目预算导入流程----xiesq
		ITKCALL (EPM_register_action_handler(
			"yfjc-deleteCostInfo",
			"yfjc-deleteCostInfo", 
			yfjc_deleteCostInfo_Handler ) );
			
		// CUS-FUN-04／圈人表单转costinfo	
		ITKCALL (EPM_register_action_handler(
			"yfjc_create_lcc_cost",
			"yfjc_create_lcc_cost", 
			yfjc_create_lcc_cost ) );

		// CUS-FUN-04／圈人表单转costinfo	
		ITKCALL (EPM_register_action_handler(
			"yfjc_delete_lcc_cost",
			"yfjc_delete_lcc_cost", 
			yfjc_delete_lcc_cost ) );
			
		// CUS-FUN-05／设置Group的Manager为Reviewer	
		ITKCALL (EPM_register_action_handler(
			"yfjc_assign_manager",
			"yfjc_assign_manager", 
			yfjc_assign_manager ) );

        // add by Cai,Pluto
		ITKCALL (EPM_register_action_handler(
			"CCL_switch_objects",
			"CCL_switch_objects", 
			CCL_switch_objects ) );


        //下面部分为注册的运行时属性（所有的运行期属性的注册方法必须放到USER_init_module，否则在thin client无法调用到）

        METHOD_id_t method;

		//2014-8-14	mengyawei added
		{

			ITKCALL( METHOD_register_prop_method ("TimeSheetEntry", "jci6_ownPhase" , PROP_ask_value_string_msg,
						(METHOD_function_t) getTimeSheetEntryOwnPhase , 0, &method ));
		}
		//2014-8-14	mengyawei added

        // SP-EXT-FUN-02.自动计算EQU - xiesq
		{
             ITKCALL(METHOD_register_prop_method( "JCI6_ProgramInfo", "jci6_EQU",
                    PROP_ask_value_double_msg,(METHOD_function_t)GTAC_ask_double_value, NULL, &method ) );
		}
        // SP-EXT-FUN-05.“项目信息”上的成本汇总信息--liuc
        {
			int i			=  0,
				ifail		=  0,
				preCount	=  0;
			char *preference_name="YFJC_ProgramInfo_CostInfo_PropNames",
				 ** preValue =NULL;

			ITKCALL(ifail=PREF_ask_char_values(preference_name,&preCount,&preValue));
			if(ifail)
			{	
				printf("not found preference %s \n",preference_name);
			}

            //Remove by zhanggl 2013-11-15
			/*ITKCALL( METHOD_find_prop_method ( "JCI6_ProgramInfo", "object_desc", PROP_ask_value_string_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                ITKCALL( METHOD_add_action( method, METHOD_pre_action_type,( METHOD_function_t ) getNewestCostInfo,NULL ) );
                printf("getNewestCostInfo 注册成功！\n");
            }*/

			for(i=0;i<preCount;i++)
			{
				ITKCALL( METHOD_register_prop_method ("JCI6_ProgramInfo", preValue[i], PROP_ask_value_double_msg,
								(METHOD_function_t) getCostTypeStatValue,0, &method));
			}
			if(preValue)
			{
				MEM_free(preValue);
				preValue=NULL;
			}
	}
	//sum programinforev property to programinfo property
		{
			int i			=  0,
				ifail		=  0,
				preCount	=  0;
			char *preference_name="YFJC_EffectiveBudget_Properties",
				 ** preValue =NULL;

			ITKCALL(ifail=PREF_ask_char_values(preference_name,&preCount,&preValue));
			if(ifail)
			{	
				printf("not found preference %s \n",preference_name);
			}

			for(i=0;i<preCount;i++)
			{
				ITKCALL( METHOD_register_prop_method ("JCI6_ProgramInfo", preValue[i], PROP_ask_value_double_msg,
								(METHOD_function_t) getPropertySum,0, &method));
			}
			if(preValue)
			{
				MEM_free(preValue);
				preValue=NULL;
			}
		}

        // SP-EXT-FUN-06.“项目信息”上和门相关的信息--liuc
        {	
			int i			=  0,
				ifail		=  0,
				preCount	=  0;
			char * preference_name="YFJC_ProgramInfo_Gate_PropNames",
				  ** preValue =NULL;

			ITKCALL(ifail=PREF_ask_char_values(preference_name,&preCount,&preValue));
			if(ifail)
			{	
				printf("not found preference %s \n", preference_name);
			}
			for(i=0;i<preCount;i++)
			{	
				if(tc_strstr(preValue[i],"date")){
					ITKCALL( METHOD_register_prop_method ("JCI6_ProgramInfo", preValue[i], PROP_ask_value_date_msg,
						(METHOD_function_t) getGateTypeInfoValue , 0, &method ));
				}else{
					ITKCALL( METHOD_register_prop_method ("JCI6_ProgramInfo", preValue[i], PROP_ask_value_string_msg,
						(METHOD_function_t) getGateTypeInfoValue, 0, &method));
				}
			}
			if(preValue)
			{
				MEM_free(preValue);
				preValue=NULL;
			}
		}

        // SP-EXT-FUN-07.“项目信息”版本上的预算汇总信息--liuc
        {	
			int i			=  0,
				ifail		=  0,
				preCount	=  0;
			char * preference_name="YFJC_ProgramInfoRevision_CostInfo_PropNames",
				  ** preValue =NULL;

			ITKCALL(ifail=PREF_ask_char_values(preference_name,&preCount,&preValue));
			if(ifail)
			{	
				printf("not found preference %s \n",preference_name);
			}
			/*ITKCALL( METHOD_find_prop_method ( "JCI6_ProgramInfoRevision", "object_desc", PROP_ask_value_string_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                ITKCALL( METHOD_add_action( method, METHOD_pre_action_type,( METHOD_function_t ) getNewestCostInfo,NULL ) );
                TC_write_syslog("yfjc_ebp_transToCostInfo 注册成功！\n");
            }*/
			for(i=0;i<preCount;i++)
			{	
				ITKCALL( METHOD_register_prop_method ("JCI6_ProgramInfoRevision", preValue[i], PROP_ask_value_double_msg,
								(METHOD_function_t) getRevBudgetStatValue,0, &method));
			}
			if(preValue)
			{
				MEM_free(preValue);
				preValue=NULL;
			}
		}
         // SP-EXT-FUN-08 “XSO信息”版本上的与门前通过率相关信息 start --zhangyn
        if ((status = METHOD_register_prop_method((const char*)"JCI6_XSORevision", "jci6_DaysGap",
	        PROP_ask_value_int_msg,  
	        yfjc_ask_jci6_DaysGap_value, 0, &method )) != ITK_ok )
        {
	        printf( "注册 yfjc_ask_jci6_DaysGap_value 失败！\n" );
        }
        if ((status = METHOD_register_prop_method((const char*)"JCI6_XSORevision", "jci6_PassByGate",
	        PROP_ask_value_logical_msg,  
	        yfjc_ask_jci6_PassByGate_value, 0, &method )) != ITK_ok )
        {
	       printf( "注册 yfjc_ask_jci6_PassByGate_value 失败！\n" );
        }
        if ((status = METHOD_register_prop_method((const char*)"JCI6_XSORevision", "jci6_DelayDays",
	        PROP_ask_value_int_msg,  
	        yfjc_ask_jci6_DelayDays_value, 0, &method )) != ITK_ok )
        {
	        printf( "注册 yfjc_ask_jci6_DelayDays_value 失败！\n" );
        }

        // SP-EXT-FUN-09. “XSO信息”版本的审核状态 start --zhangyn
        if ((status = METHOD_register_prop_method((const char*)"JCI6_XSORevision", "jci6_ReviewStatus",
	        PROP_ask_value_string_msg,  
	        yfjc_ask_jci6_ReviewStatus_value, 0, &method )) != ITK_ok )
        {
	        printf( "注册 yfjc_ask_jci6_ReviewStatus_value 失败！\n" );
        }

        // SP-EXT-FUN-10. “XSO信息”版本审核日期默认值  ---zhangyn
        if((status = METHOD_register_prop_method( "JCI6_XSORevision", "jci6_Gate", PROP_set_value_tag_msg,
            JCI6setXSODefaultDateValue, NULL, &method)) != ITK_ok)
        {
            printf( "METHOD_register_prop_method PROP_set_value_string_msg failed\n" );
        }
         // SP-EXT-FUN-15.设计要求检查是否通过 --zhanggl
        {
            ITKCALL( METHOD_register_prop_method ( "JCI6_DesignReq", "jci6_PorF", PROP_ask_value_logical_msg,
						(METHOD_function_t) JCI6getDRCheckResult, 0, &method ) );
        }

         // SP-EXT-FUN-19. XSO 信息Item 上叠加版本属性--xiesq
        {	
			int i			=  0,
				ifail		=  0,
				preCount	=  0;

			char * preference_name="YFJC_Gate_ReviewDate_Status",
				  ** preValue =NULL;

			ITKCALL( ifail = PREF_ask_char_values( preference_name, &preCount, &preValue ) );
			if(ifail)
			{	
				TC_write_syslog("not found preference %s \n",preference_name);
			}
			for( i=0; i < preCount; i ++ )
			{	
				if( tc_strstr( preValue[i], "Date" ) )
				{
					ITKCALL( METHOD_register_prop_method ( "JCI6_XSO", preValue[i], PROP_ask_value_date_msg,
						(METHOD_function_t) getXSORevInfo, 0,&method));
				}
				else
				{
					ITKCALL( METHOD_register_prop_method ( "JCI6_XSO", preValue[i], PROP_ask_value_string_msg,
						(METHOD_function_t) getXSORevInfo, 0, &method ) );
				}
			}
			if( preValue )
            {
                MEM_free( preValue );
                preValue = NULL;
            }
        }

        //zhou,William
		{


			ITKCALL( METHOD_register_prop_method( "TimeSheetEntry",
                                              "jci6_hour",
                                              PROP_ask_value_string_msg,
                                              (METHOD_function_t)JCI6_ask_timesheetentry_hour, 0,
                                              &method ))
                        ITKCALL( METHOD_register_prop_method( "Schedule",
                                              "jci6_hasObserver",
                                              PROP_ask_value_logical_msg,
                                              JCI6_ask_hasObserver, 0,
                                              &method ));
		}
        //wuw
        {
               ITKCALL( METHOD_register_prop_method( "Schedule",
                                              "jci6_allSchTasks",
                                              PROP_ask_value_tags_msg,
                                              JCI6_ask_schedule_tasks, 0,
                                              &method ));
               ITKCALL(METHOD_register_prop_method( "JCI6_DesignReq",
	            "jci6_ValidValue",
	            PROP_ask_value_string_msg,
	            JCI6_ask_DesignReq_ValidValue, 0,
	            &method ));

	        ITKCALL(METHOD_register_prop_method( "JCI6_DesignReq",
		        "jci6_MaxValue",
		        PROP_ask_value_string_msg,
		        JCI6_ask_DesignReq_ValidValue, 0,
		        &method ));

	        ITKCALL(METHOD_register_prop_method( "JCI6_DesignReq",
		        "jci6_MinValue",
		        PROP_ask_value_string_msg,
		        JCI6_ask_DesignReq_ValidValue, 0,
		        &method ));
                ITKCALL(status = METHOD_find_method  ( "JCI6_PKRRevision",  ITEM_deep_copy_msg, &method));
                if(method.id == 0)
                {
                    printf("[DEBUG] Not found the ITEM_deep_copy_msg method of JCI6_PKRRevision.\n");
                }
                else
                {
                    printf("[DEBUG] JCI6_Copy_DRT is registered as post-action of ITEM_deep_copy_msg.\n");
                    ITKCALL(status = METHOD_add_action(method, METHOD_post_action_type, JCI6_Copy_DRT, NULL));
                }

        }

	//add by wuh 2014-5-23
		{
			int ifail = 0;
			printf("---------------------is_active registy start---------------------------\n");
			ITKCALL(ifail = METHOD_find_prop_method("TC_Project","is_active",PROP_set_value_logical_msg ,&method));
			if (method.id != 0){
				ITKCALL( METHOD_add_action(method, METHOD_post_action_type, ( METHOD_function_t )bmf_programinfo_change_closedate,NULL));
				printf("Registering extension function bmf_programinfo_change_closedate completed!\n");
			}else{
				printf("Registering extension function bmf_programinfo_change_closedate faield!\n");
			}
			printf("---------------------is_active registy end---------------------------\n");
			printf("---------------------start_date registy satrt---------------------------\n");
			ITKCALL(ifail = METHOD_find_prop_method("ScheduleTask","start_date",PROP_set_value_date_msg ,&method));
			if (method.id != 0){
				ITKCALL( METHOD_add_action(method, METHOD_post_action_type, ( METHOD_function_t )JCI6ScheduleTaskDate,NULL));
				printf("Registering extension function JCI6ScheduleTaskDate completed!\n");
			}else{
				printf("Registering extension function JCI6ScheduleTaskDate faield!\n");
			}
			printf("---------------------start_date registy end---------------------------\n");
		}
		
		// SP-EXT-FUN-03.列出所有Section/Division --zhanggl
        /*ITKCALL(ifail =  METHOD_register_method( "ListOfValuesTagExtent", LOV_ask_values_msg,
            &custom_tag_lov, 0, &method ) );*/
        ITKCALL( METHOD_find_method ( "ListOfValuesTagExtent", LOV_ask_values_msg, &method ) );
        if ( method.id != NULLTAG )
        {
            ITKCALL( METHOD_add_action( method, METHOD_post_action_type, ( METHOD_function_t ) custom_tag_lov,NULL ) );
        }
        //EBP SP-EXT-FUN-03. 在TimesheetEntry上面加一个运行期属性“jci6_isOverDue” --zhanggl
        ITKCALL( METHOD_register_prop_method( "TimeSheetEntry",
                                              "jci6_isOverDue",
                                              PROP_ask_value_logical_msg,
                                              (METHOD_function_t)JCI6_ask_timesheetentry_isOverDue, 0,
                                              &method ))

        if ( status == ITK_ok )
        {
            printf("*                       Registered ListOfValuesTagExtent method sucess !                   *\n");
            printf("*******************************************************************************\n");
        }
        else
        {
            printf("*                        Registered ListOfValuesTagExtent method fail !                    *\n");
            printf("*******************************************************************************\n");
        }


        if ( status == ITK_ok )
        {
            printf("*                       Registered custom handlers sucess !                   *\n");
            printf("*******************************************************************************\n");
        }
        else
        {
            printf("*                        Registered custom handlers fail !                    *\n");
            printf("*******************************************************************************\n");
        }

        return status;
    }

	extern DLLAPI int USERSERVICE_custom_register_methods(int *decision, va_list args)
	{	
		*decision = ALL_CUSTOMIZATIONS;

		int status             = ITK_ok,
		    numberOfArguments  = 0,
		    returnValueType    = 0,
		    *argumentList      = NULL;

        	USER_function_t functionPtr;
		METHOD_id_t method;
     		//2014-6-16	mengyawei added
		//SP-CUS-006／不允许删除已填写工时的任务 
		{
			numberOfArguments = 1;
			returnValueType = USERARG_STRING_TYPE;
			functionPtr = Delete_schedule_task_check;

			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_TAG_TYPE + USERARG_ARRAY_TYPE;
			USERSERVICE_register_method("deleteScheduleTaskCheck" , functionPtr ,numberOfArguments ,
				argumentList , returnValueType );
			MEM_free(argumentList);
			argumentList = NULL;
		}

		//add by wuh 2014-5-29 JCI6_updateProgramInfoDate
		{ 
			numberOfArguments  = 1; 
			returnValueType    = USERARG_VOID_TYPE;
			functionPtr = JCI6_updateProgramInfoDate; 
			argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int)); 
			argumentList[0] = USERARG_TAG_TYPE;  
			ITKCALL(USERSERVICE_register_method("JCI6_updateProgramInfoRevDate", functionPtr, 
				numberOfArguments,argumentList, returnValueType) );
			MEM_free(argumentList);
			argumentList = NULL;
		}

		//2014-8-6 
		{
        		numberOfArguments = 2;
        		returnValueType = USERARG_INT_TYPE;
       		functionPtr = userservice_setSignOff;
        		argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int));
        		argumentList[0] = USERARG_TAG_TYPE;   
			argumentList[1] = USERARG_TAG_TYPE; 
       	 	USERSERVICE_register_method("userservice_setSignOff", functionPtr, numberOfArguments,
               			 argumentList, returnValueType );
        				MEM_free(argumentList);
        		argumentList = NULL;
     		}

		// SP-RICH-FUN-12.项目信息导入工具--liuc
		{
			numberOfArguments  = 1;
			returnValueType = USERARG_TAG_TYPE ;

			functionPtr = importProgramInfo;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_STRING_TYPE ;

			USERSERVICE_register_method( "importProgramInfo", functionPtr, numberOfArguments,
				argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}

		//SP-RICH-FUN-13.Timelog导入工具--liuc
		{
			numberOfArguments = 1;
			returnValueType = USERARG_INT_TYPE;

			functionPtr  = createProgramInfoRev;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_STRING_TYPE;

			USERSERVICE_register_method( "createProgramInfoRev", functionPtr, numberOfArguments,
				argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;

			numberOfArguments = 3;
			returnValueType = USERARG_INT_TYPE;

			functionPtr = importTimeLog;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_STRING_TYPE;
			argumentList[1] = USERARG_STRING_TYPE+USERARG_ARRAY_TYPE;
			argumentList[2] = USERARG_STRING_TYPE;

			USERSERVICE_register_method("importTimeLog", functionPtr, numberOfArguments,
				argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}
		
		{
			numberOfArguments = 1;
			returnValueType = USERARG_TAG_TYPE;

			functionPtr = yfjs_createCostInfo;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_STRING_TYPE;

			USERSERVICE_register_method("yfjs_createCostInfo", functionPtr, numberOfArguments,
				argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}

		//hyperion import function liqz
		{
			numberOfArguments = 1;
			returnValueType = USERARG_INT_TYPE;

			functionPtr = importhyperion;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;

			USERSERVICE_register_method("hyperionimport", functionPtr, numberOfArguments, argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}
        {
			numberOfArguments = 2;
			returnValueType = USERARG_INT_TYPE + USERARG_ARRAY_TYPE;

			functionPtr = getWorkingDates;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_TAG_TYPE;
			argumentList[1] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;

			USERSERVICE_register_method("getWorkingDates", functionPtr, numberOfArguments, argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}
       
        //SP-EXT-FUN-13.检查是否项目负责人---xiesq
        {
            ITKCALL( METHOD_find_prop_method ( "JCI6_PFARevision", "JCI6_SDT", PROP_set_value_tags_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                ITKCALL( METHOD_add_pre_condition( method, ( METHOD_function_t ) isLeadEngineer,NULL ) );
            }
        }   

        //SP-EXT-FUN-01.自动复制项目资料库到项目  -- zhanggl
        {
			// Remove and add by zhanggl 13-Apr-2013
            /*int numberOfArguments = 3;
            returnValueType = USERARG_INT_TYPE;

            functionPtr = JCI6postActionsForNewProj;
            int *argumentList  = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
            argumentList[0] = USERARG_TAG_TYPE;
            argumentList[1] = USERARG_TAG_TYPE;
            argumentList[2] = USERARG_TAG_TYPE;

            USERSERVICE_register_method("JCI6postActionsForNewProj", functionPtr, 
                numberOfArguments, argumentList, returnValueType );
            MEM_free( argumentList );
            argumentList = NULL;*/
			ITKCALL( METHOD_find_method ( "JCI6_ProgramInfo", TC_assign_primary_obj_to_project_msg, &method ) );
            if ( method.id != NULLTAG )
            {
				ITKCALL( METHOD_add_action( method, METHOD_post_action_type, ( METHOD_function_t )JCI6postActionsForNewProj, NULL ) );
            }
        }
		
		
        // SP-EXT-FUN-04.自动更新项目状态属性 -- zhanggl
        {
            int numberOfArguments = 2;
            returnValueType = USERARG_INT_TYPE;

            functionPtr = JCI6_updateProjInfoRevStatus;
            int *argumentList  = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
            argumentList[0] = USERARG_STRING_TYPE;
            argumentList[1] = USERARG_TAG_TYPE;

            USERSERVICE_register_method("JCI6_updateProjInfoRevStatus", functionPtr, 
                numberOfArguments, argumentList, returnValueType );
            MEM_free( argumentList );
            argumentList = NULL;
        }

	//add by wuh 2014-8-14
	{
	     int ifail = 0;
	     ITKCALL(ifail = METHOD_find_prop_method("JCI6_ProgramInfoRevision","jci6_SOP",PROP_set_value_date_msg,&method));
	     if (method.id != 0){
		   ITKCALL( METHOD_add_action(method, METHOD_post_action_type, ( METHOD_function_t )bmf_autoUpdateSOPFisYear,NULL));
		   printf("Registering extension function bmf_autoUpdateSOPFisYear completed!\n");
	     }else{
		   printf("Registering extension function bmf_autoUpdateSOPFisYear faield!\n");
	     }
	}
        // Schedule 自动指派到项目按照特定关系、设置ownering_project属性 -zhanggl
        {
			ITKCALL( METHOD_find_method ( "Schedule", TC_assign_primary_obj_to_project_msg, &method ) );
            if ( method.id != NULLTAG )
            {
				ITKCALL( METHOD_add_action( method, METHOD_post_action_type, ( METHOD_function_t )JCI6Schedule_assign_project, NULL ) );
            }
        }
        
        // Schedule 指派到项目前判断：项目下是否已经存在时间表，时间表是否已经被指派给其他项目 -zhanggl
        {
			ITKCALL( METHOD_find_method ( "Schedule", TC_assign_primary_obj_to_project_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                //ITKCALL( METHOD_add_action( method, METHOD_pre_action_type, ( METHOD_function_t )Schedule_assign_project_pre_action, NULL ) );
                ITKCALL( METHOD_add_pre_condition( method, ( METHOD_function_t ) Schedule_assign_project_pre_action,NULL ) );
            }
        }
        // create jci6_DRT -- zhanggl
        { 
			numberOfArguments  = 4; 
			returnValueType    = USERARG_TAG_TYPE;
			functionPtr = create_object; 

			argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int)); 
			argumentList[0] = USERARG_STRING_TYPE;  
            argumentList[1] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;
            argumentList[2] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;
			argumentList[3] = USERARG_STRING_TYPE; 

			ITKCALL(USERSERVICE_register_method("create_object", functionPtr, 
                numberOfArguments,argumentList, returnValueType) );
			MEM_free(argumentList);
            argumentList = NULL;
		}  
        // revise jci6_DRTRevision -- zhanggl
        { 
			numberOfArguments  = 4; 
			returnValueType    = USERARG_TAG_TYPE;
			USER_function_t functionPtr = revise_object; 

			argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int)); 
			argumentList[0] = USERARG_TAG_TYPE;  
            argumentList[1] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;
            argumentList[2] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;
			argumentList[3] = USERARG_STRING_TYPE; 

            ITKCALL(USERSERVICE_register_method("revise_object", functionPtr, 
                numberOfArguments,argumentList, returnValueType) );
            MEM_free(argumentList);
            argumentList = NULL;
        } 
        //EBP SP-EXT-FUN-04. 修改项目状态后自动设置项目结束日期并通知SAP --zhanggl
        {
            ITKCALL( METHOD_find_prop_method ( "JCI6_ProgramInfoRevision", "jci6_ProgramState", PROP_set_value_string_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                ITKCALL( METHOD_add_action( method, METHOD_post_action_type,( METHOD_function_t ) JCI6_updateProjCloseDate, NULL ) );
            }
        }

        // SP-RICH-FUN-07.维护人头计划start --zhangyn
        {
			int numberOfArguments = 3;
			int returnValueType = USERARG_STRING_TYPE;
			int *argumentList = NULL;

			functionPtr = importPersonPlan;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_TAG_TYPE;
		    argumentList[1] = USERARG_STRING_TYPE + USERARG_ARRAY_TYPE;
            argumentList[2] = USERARG_STRING_TYPE;

			USERSERVICE_register_method("importPersonPlan", functionPtr, 
                numberOfArguments, argumentList, returnValueType );
			MEM_free( argumentList );
            argumentList = NULL;
		}

		
		// open or close ByPass --zhanggl
		{ 
			int isSuccess=ITK_ok;
			numberOfArguments  = 1; 
			returnValueType    = USERARG_VOID_TYPE; 
			functionPtr = open_or_close_pass;  

			argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int)); 
			argumentList[0] = USERARG_INT_TYPE;  
            
			ITKCALL(isSuccess=USERSERVICE_register_method("open_or_close_pass", functionPtr, 
                numberOfArguments, argumentList, returnValueType) ); 
			MEM_free(argumentList); 
            argumentList = NULL;

			if(isSuccess==ITK_ok){
				printf( "Registering open_or_close_pass finished\n");
				TC_write_syslog( "Registering open_or_close_pass finished\n");
			}
			
		}

        // SP-EXT-FUN-14. 自动设置功能领域的8个门和审核日期默认值 --map
        {
			// Rremove and add by zhanggl 12-Apr-2013
			
			/*numberOfArguments = 1;
			returnValueType = USERARG_INT_TYPE;

			functionPtr = JCI6_PFARevision;
			int*argumentList = (int*)MEM_alloc(numberOfArguments*sizeof(int));
			argumentList[0] = USERARG_TAG_TYPE;

			ITKCALL( USERSERVICE_register_method("JCI6_PFARevision",functionPtr,
                numberOfArguments,argumentList,returnValueType ) );
			MEM_free(argumentList);
            argumentList = NULL;*/

			ITKCALL( METHOD_find_method ( "JCI6_PFA", TC_assign_primary_obj_to_project_msg, &method ) );
            if ( method.id != NULLTAG )
            {
				ITKCALL( METHOD_add_action( method, METHOD_post_action_type, ( METHOD_function_t )setPFADefaultValue, NULL ) );
            }
		}

        //把人工成本和非人工成本信息转换为CostInfo对象（SP-RICH-FUN-04.查看成本计划中SOA部分）
		{
			numberOfArguments = 1;
			returnValueType = USERARG_VOID_TYPE;

			USER_function_t functionPtr;
			functionPtr = yfjc_ebp_transToCostInfo;
			argumentList = (int*)MEM_alloc( numberOfArguments * sizeof(int) );
			argumentList[0] = USERARG_TAG_TYPE;

			USERSERVICE_register_method("yfjc_ebp_transToCostInfo", 
				functionPtr, numberOfArguments,
				argumentList, returnValueType );
			MEM_free( argumentList );
			printf( "Registering yfjc_ebp_transToCostInfo finished\n" );
		}


        // Shanghai
        // registering jci6_create_item
        {
            numberOfArguments = 1;
            returnValueType = USERARG_STRING_TYPE;

            functionPtr = jc6_create_item;
            argumentList = (int*)MEM_alloc(numberOfArguments * sizeof(int));
            argumentList[0] = USERARG_STRING_TYPE;
            
            USERSERVICE_register_method("jc6_create_item", functionPtr, numberOfArguments,
                argumentList, returnValueType );
            MEM_free(argumentList);
            argumentList = NULL;
        }
		
		// tyl 2015/01/13 设置项目重开时间
		// METHOD_pre_action_type   METHOD_post_action_type
		 {
            ITKCALL( METHOD_find_prop_method ( "JCI6_ProgramInfoRevision", "jci6_ProgramState", PROP_set_value_string_msg, &method ) );
            if ( method.id != NULLTAG )
            {
                ITKCALL( METHOD_add_action( method, METHOD_pre_action_type,( METHOD_function_t ) JCI6_update_programinfo_reopenTime_and_phase0Time, NULL ) );
            }
        }
	  // tyl 2015/01/13 设置项目重开时间


      	

	
        if ( status == ITK_ok )
        {
            printf("*                       Registered custom method sucess !                   *\n");
            printf("*******************************************************************************\n");
        }
        else
        {
            printf("*                        Registered custom method fail !                    *\n");
            printf("*******************************************************************************\n");
        }

		return status;
	}

#ifdef __cplusplus
}
#endif
