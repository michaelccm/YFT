/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_remove_default_assignee.cxx
   Module  : Handler

   Remove default assigneeo
============================================================================================================
DATE           Name             Description of Change
08-Apr-2013    zhanggl		    creation
$HISTORY$
============================================================================================================*/
#include "yfjc_ebp_head.h"

static logical isDebug = FALSE;

int yfjc_remove_default_assignee_handler(EPM_action_message_t msg)
{
	tag_t  root_task   = NULLTAG,
		   *sub_tasks  = NULL;

	int ifail	   = ITK_ok,
		tasks_cnt  = 0,      //流程模板节点数
		chLen      = 0,      //tasks参数长度
		chCnt      = 0;    

	char   *trans_args_value         = NULL,
		   *para_flag                = NULL, 
		   *para_value               = NULL,
		   *para_tasks               = NULL,
		   **remove_task_name        = NULL,
		   *point                    = NULL,
		   task_name[WSO_name_size_c + 1] = "";

	logical isFind  = FALSE;

	ask_opt_debug();
	ECHO("*******************************************************************************\n");
	ECHO("*          yfjc_remove_default_assignee_handler is comming ！                 *\n");
	ECHO("*******************************************************************************\n");

	ITKCALL( EPM_ask_root_task( msg.task, &root_task ) );
	
	tasks_cnt=TC_number_of_arguments( msg.arguments );
	
	for(int i = 0; i < tasks_cnt; i++ )
	{
		trans_args_value =TC_next_argument( msg.arguments );

		ITKCALL( ITK_ask_argument_named_value( trans_args_value, &para_flag, &para_value ) );
		ECHO("第 %d 个参数是：%s； 值是：%s\n", i, para_flag, para_value);

		if( tc_strcmp( para_flag, YJ6_DEBUG_STR ) == 0 )
		{
			if( tc_strcasecmp( para_value, YJ6_TRUE ) == 0 )
			{
				isDebug = TRUE;
			}
		}
		else if( tc_strcmp( para_flag, "tasks" ) == 0 )
		{
			chLen = tc_strlen( para_value );
			para_tasks = (char*)MEM_alloc( (chLen+1) * sizeof(char) );
			tc_strcpy( para_tasks, para_value );
		}

		if( para_flag != NULL )
		{
			MEM_free( para_flag );
			para_flag = NULL;
		}

		if( para_value != NULL )
		{
			MEM_free( para_value );
			para_value = NULL;
		}
	}

	if( isDebug )
		TC_write_syslog("para_tasks = %s\n",para_tasks);

	for( int il = 0; il < chLen; il++ )
	{
		if( para_tasks[il] == ';' )
		{
			chCnt++;
		}
	}

	if( isDebug )
		TC_write_syslog("chCnt = %d\n",chCnt);

	if( chLen )
	{
		int step = 0;
		chCnt = chCnt + 1;
		remove_task_name = (char**)MEM_alloc( chCnt * sizeof(char*) );

		point = tc_strtok( para_tasks, ";");
		while( point )
		{
			remove_task_name[step] = (char*)MEM_alloc( (tc_strlen(point)+1) * sizeof(char) );
			tc_strcpy( remove_task_name[step], point ); 
			point = tc_strtok( NULL, ";");
			step++;
		}
		MEM_free( para_tasks );
		para_tasks = NULL;
	}
	else
	{
		ITKCALL( EPM_ask_sub_tasks( root_task, &tasks_cnt, &sub_tasks ) );
		for( int it = 0; it < tasks_cnt; it++ )
		{
			if( ifail = clear_default_user( sub_tasks[it] ) != ITK_ok )
			{
				if( isDebug )
					TC_write_syslog("Remove %s default user error\n", task_name);
				goto HOUT;
			}
			else if( isDebug )
				TC_write_syslog("Remove %s default user success\n", task_name);
		}
		goto HOUT;
	}

	ITKCALL( EPM_ask_sub_tasks( root_task, &tasks_cnt, &sub_tasks ) );
	for( int ic = 0; ic < chCnt; ic++ )
	{
		if( isDebug )
			TC_write_syslog("remove_task_name[ic] = %s\n", remove_task_name[ic] );

		isFind  = FALSE;
		for( int it = 0; it < tasks_cnt; it++ )
		{
			ITKCALL( EPM_ask_name( sub_tasks[it], task_name ) );
			if( isDebug )
				TC_write_syslog("task_name = %s\n", task_name );

			if( tc_strcmp( task_name, remove_task_name[ic] ) == 0 )
			{
				if( isDebug )
					TC_write_syslog("Find %s is right\n", task_name);

				isFind  = TRUE;
				break;
			}
		}
		if( !isFind )
		{
			ITKCALL( EMH_store_initial_error( EMH_severity_error, ERROR_NO_FIND_VALUE ) );
			ifail = ERROR_NO_FIND_VALUE;
			goto HOUT;
		}
	}

	for( int ic = 0; ic < chCnt; ic++ )
	{
		for( int it = 0; it < tasks_cnt; it++ )
		{
			ITKCALL( EPM_ask_name( sub_tasks[it], task_name ) );
			if( tc_strcmp( task_name, remove_task_name[ic] ) == 0 )
			{
				if( ifail = clear_default_user( sub_tasks[it] ) != ITK_ok )
				{
					if( isDebug )
						TC_write_syslog("Remove %s default user error\n", task_name);
					goto HOUT;
				}
				else if( isDebug )
					TC_write_syslog("Remove %s default user success\n", task_name);
				break;
			}
		}
	}

HOUT:
	for( int ic = 0; ic < chCnt; ic++ )
	{
		if( remove_task_name[ic] != NULL )
		{
			MEM_free( remove_task_name[ic] );
			remove_task_name[ic] = NULL;
		}
	}
	if( remove_task_name != NULL )
	{
		MEM_free( remove_task_name );
		remove_task_name = NULL;
	}

	if( sub_tasks != NULL )
	{
		MEM_free( sub_tasks );
		sub_tasks = NULL;
	}
	
	ECHO("*******************************************************************************\n");
	ECHO("*        yfjc_remove_default_assignee_handler is end ！                       *\n");
	ECHO("*******************************************************************************\n");
	return ifail;
}


