/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_ebp_timesheetEntry_isOverDue.cxx
    Module  : user_exits

============================================================================================================
DATE			     Name             Description of Change
24-Oct-2013          zhanggl		  creation
$HISTORY$
============================================================================================================*/
#include "yfjc_ebp_head.h"
#ifdef __cplusplus
extern "C" {
#endif

int JCI6_ask_timesheetentry_isOverDue( METHOD_message_t *msg, va_list args )
{
	int index = 0,
        count = 0,
        customerror = ITK_ok,
        year_local  = 0, 
        month_local = 0, 
        day_local   = 0;;

	char **optvalues         = NULL,
		 *option_value       = NULL,
		 *interval           = NULL,
		 *default_interval   = NULL,
	     *week_interval      = "PerWeek",
		 *month_interval     = "PerMonth";

	date_t date;
    struct tm *local;
	time_t nowtime;

	va_list local_args;

	char day[80],
		 year[80],
		 month[80],
         rolename[SA_name_size_c + 1] = "";

	bool isNull = false,
	     isOk   = false,
         isbeyongmonth = false; //是否可以超出本月

	char *group_name   = NULL,
         *grouprole    = NULL,
         *tempinterval = NULL;

	tag_t timesheetEntry_tag     = NULLTAG,
          group_tag              = NULLTAG,
          current_role_tag       = NULLTAG, 
          time_schedule_task_tag = NULLTAG,
          time_schedule_tag      = NULLTAG, 
          currentProject         = NULLTAG;

	ask_opt_debug();
	ECHO("--------------JCI6_ask_timesheetentry_isOverDue----------------------- \n");

    tag_t     prop_tag   = va_arg( args, tag_t );
    logical*  prop_value = va_arg( args, logical* );

    METHOD_PROP_MESSAGE_OBJECT( msg, timesheetEntry_tag );

    ITKCALL( AOM_ask_value_date( timesheetEntry_tag, TIMESHEET_DATE, &date ) );

	ITKCALL( AOM_ask_value_tag( timesheetEntry_tag, SCHEDULETASK_TAG, &time_schedule_task_tag ) );

    //--------------------------------------------------------------------------------------------------------
    // 判断项目是不是激活状态，非激活状态，设置属性为FALSE
    logical is_active = FALSE;
    if( time_schedule_task_tag )
    {
        ECHO("time_schedule_task_tag = %u \n", time_schedule_task_tag );
        ITKCALL( AOM_ask_value_tag( time_schedule_task_tag , SCHEDULE_TAG , &time_schedule_tag ) );

        ITKCALL( AOM_ask_value_tag( time_schedule_tag , OWNING_PROJECT , &currentProject ) );

        ECHO("currentProject = %u \n", currentProject );
        if( currentProject )
        {
            ITKCALL(PROJ_is_project_active(currentProject, &is_active));
            if( !is_active )
            {
                ECHO("project is unactive \n");
                *prop_value = TRUE;
                goto FOUT;
            }
        }
    }
    //--------------------------------------------------------------------------------------------------------

	nowtime = time( NULL );         //获取日历时间
	local = localtime( &nowtime );  //获取当前系统时间

	

	year_local = local->tm_year+1900;
    month_local = local->tm_mon+1;
    day_local = local->tm_mday;
	ECHO("year = %d\t month = %d\t day = %d\n", year_local, month_local, day_local);

	ITKCALL( PREF_ask_char_values( PREFERENCE_YFJC_INTERVAL_OF_LOG_TIME, &count, &optvalues ) );
	if( count == 0 )
	{
		ECHO("preference name:YFJC_Interval_Of_Log_Time is not defined\n");
		interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
		tc_strcpy(interval, week_interval );
	}
	else
    {
        logical is_first_line = TRUE;      // add by zhanggl 11-oct, defualt use first line, if don't find group or rule
        logical is_in_current_month = TRUE;// add by zhanggl 11-oct, flag whether to allow this month, if use first line.
		ECHO("检查首选项的值\n");
		/*将当前用户所在组及角色与检查首选项配置的值比较，首选项值形式：
		Division1｛.｝External Supporter=PerWeek (允许本周，但不得超过本月)
		&Division2｛.｝External Supporter=PerWeek （允许本周，可以超过本月)
		*/

		char *strgroup_name = NULL; 
		ITKCALL( POM_ask_group( &strgroup_name, &group_tag ) );
		ECHO("strgroup_name = %s\n",strgroup_name );
        MEM_free( strgroup_name );
        strgroup_name = NULL;

		ITKCALL( SA_ask_group_full_name( group_tag, &group_name ) );
		ECHO("group_name = %s\n",group_name );

		ITKCALL( SA_ask_current_role( &current_role_tag ) );
		ITKCALL( SA_ask_role_name( current_role_tag, rolename ) );
		ECHO("rolename = %s\n",rolename );

		for(int i = 0; i < count; i ++)
        {
			isbeyongmonth = false;
			char *option = NULL;

            if(tc_strstr(optvalues[i], "&") != NULL)
            {
                ECHO("contain & \n");
                isbeyongmonth = true; 
                STRNG_replace_str( optvalues[i], "&", "", &option );
                if( i == 0 )
                {
                    is_in_current_month = FALSE;
                }
            }
            else
			{
                option = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(optvalues[i]) + 1));
				ECHO("not contain & \n");
				tc_strcpy( option, optvalues[i] );
			}

			if( tc_strstr( option, "=" ) )
            {
				ECHO("contain = \n");
				grouprole = tc_strtok( option, "=" );
				tempinterval = tc_strtok( NULL, "=" );
				ECHO("grouprole = %s\n",grouprole);
                ECHO("tempinterval = %s\n",tempinterval);

                if( tc_strstr( grouprole, "{.}" ) )
                {
                    //既要判断组也要判断角色
                    ECHO("You need to determine group and role \n");
                    char *option_group = NULL;

                    STRNG_replace_str( grouprole, "{.}", "$", &option_group );
                    char *tmpgroup = tc_strtok( option_group, "$" );
                    ECHO("tmpgroup = %s\n", tmpgroup );	
                    char *option_role = tc_strtok( NULL, "$" );
                    ECHO("option_role = %s\n", option_role );

                    if(tc_strcmp(group_name, option_group) == 0  && tc_strcmp(rolename, option_role) == 0)
                    {
                        is_first_line = FALSE;
                        option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(tempinterval) + 1));
                        tc_strcpy( option_value, tempinterval );
                        break;
                    }
                }
				else
				{
					//如果没有{.}则只判断角色
					if(tc_strcasecmp( rolename, grouprole ) == 0)
					{
                        is_first_line = FALSE;
						option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(tempinterval) + 1));
					    tc_strcpy( option_value, tempinterval );
                        break;
					}
				}
			}
			else
			{
				ECHO(" not contain = \n");
				//默认值（Perweek或者Permonth或者（如7~7））
				default_interval = (char*)MEM_alloc(sizeof(char) * (tc_strlen( option ) + 1 ) );
                tc_strcpy( default_interval, option );
			}
		}

        ECHO("is_first_line=%d,is_in_current_month=%d \n", is_first_line, is_in_current_month );
        if( is_first_line && is_in_current_month)
        {
            if(date.month != local->tm_mon)
			{
				ECHO("日期不在区间内\n");
				*prop_value = TRUE;
                goto FOUT;
			}
        }
        else
        {
            ECHO("isbeyongmonth===%d\n",isbeyongmonth);
            if(!isbeyongmonth) 
            {
                ECHO("不可以超出本月\n");
                if(date.month != local->tm_mon)
                {
                    ECHO("日期不在区间内\n");
                    *prop_value = TRUE;
                    goto FOUT;
                }
            }
        }
        
		//默认值作为区间
		if(option_value == NULL)
		{
            ECHO("option_value is null \n");
			if(default_interval != NULL)
			{
				option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(default_interval) + 1));
				tc_strcpy( option_value, default_interval );
			}
            else
			{
				ECHO("default value for preference name:YFJC_Interval_Of_Log_Time is not defined\n");
				option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
				tc_strcpy( option_value, week_interval );
			}
	    }
        ECHO("238 option_value===%s\n",option_value);
		if( tc_strcasecmp( option_value, week_interval) == 0 )
		{
			interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
			tc_strcpy(interval, week_interval );
		}
		else if(tc_strcasecmp(option_value, month_interval) == 0)
		{
			interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(month_interval) + 1));
			tc_strcpy(interval, month_interval);
		}
		else if(tc_strstr(option_value,"~"))
		{
            interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(option_value) + 1));
			tc_strcpy(interval, option_value);
		}
        else if(tc_strstr(option_value,"～") )
        {
            STRNG_replace_str(option_value,"～","~",&interval);
        }
	}

	ECHO("interval = %s\n",interval);
	
	if(tc_strcasecmp(interval,week_interval) == 0)
	{
		isOk = checkdatebyweek(date,nowtime, year_local, month_local, day_local);
	}
	else if(tc_strcasecmp(interval,month_interval) == 0)
	{
		isOk = checkdatebymonth(date,year_local,month_local);
	}
	else
	{
		isOk = checkdatebydays(date,nowtime,interval, year_local, month_local, day_local);
	}

	if( !isOk )
	{
		*prop_value = TRUE;
        goto FOUT;
	}
    *prop_value = FALSE;

FOUT:

    if( group_name )
    {
        MEM_free( group_name );
        group_name = NULL;
    }
    if( interval )
    {
        MEM_free( interval );
        interval = NULL;
    }
    if( default_interval )
    {
        MEM_free( default_interval );
        default_interval = NULL;
    }
    if( option_value )
    {
        MEM_free( option_value );
        option_value = NULL;
    }
    ECHO("---------------------------------------------------------------------------------------------\n");
	return customerror;
}

 #ifdef __cplusplus
}
#endif