/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_check_overduelogtime.cxx
    Module  : user_exits

============================================================================================================
DATE			     Name             Description of Change
07-Mar-2013          liqz			  creation
$HISTORY$
07-Apr-2013          zhanggl          Judge sunday
08-Aug-2013		     liqz			  modified preference support multiple values
11-Oct-2013          zhanggl          To determine whether ownering_project property is inactive. True:Don't save 
                                      Modified preference support multiple values.--Bug
14-Nov-2013          zhanggl          Change the three variables type from char* to int: year , month , day.
                                      Error: Invalid trailing byte 80, because STRNG_replace_str
============================================================================================================*/
#include "yfjc_ebp_head.h"
#include <metaframework/CreateInput.hxx>

using namespace Teamcenter;

#ifdef __cplusplus
extern "C" {
#endif


/**
*checkOverDueLogTime::precondition for TimeSheetEntry
*/
int JCI6checkOverDueLogTime( METHOD_message_t *msg, va_list args )
{
	int index = 0,
        count = 0;

	char  **optvalues        = NULL,
		  *option_value      = NULL,
		  *interval          = NULL,
		  *default_interval  = NULL,
          *week_interval = "PerWeek",
		  *month_interval= "PerMonth";

	date_t date;
	va_list local_args;

    //Change by zhanggl 2013-11-14
	//char day[80] = "", year[80] = "", month[80] = "";
    int year_local = 0, month_local = 0, day_local = 0;
    //End

	struct tm *local;
	time_t nowtime;
	bool isNull = false, isOk = false;

	int customerror = ITK_ok;
	char *group_name = NULL,*grouprole = NULL, *tempinterval = NULL;

	tag_t group_tag = NULLTAG,current_role_tag = NULLTAG, 
         time_schedule_task_tag = NULLTAG, time_schedule_tag = NULLTAG, currentProject = NULLTAG;

	char rolename[SA_name_size_c + 1] = "" ;
	//是否可以超出本月
	bool isbeyongmonth = false; 

	ask_opt_debug();
	ECHO("start===JCI6checkOverDueLogTime=== \n");

	va_copy( local_args, args);
	CreateInput *pCreInput = va_arg( local_args, CreateInput* );
	if( pCreInput )
	{
		pCreInput->getDate(TIMESHEET_DATE, date, isNull);
        pCreInput->getTag(SCHEDULETASK_TAG, time_schedule_task_tag, isNull);//add by zhanggl 11-oct
	}
	va_end( local_args );

    //add by zhanggl 11-oct
    logical is_active = FALSE;
    if( time_schedule_task_tag )
    {
       // ECHO("time_schedule_task_tag = %u \n", time_schedule_task_tag );
        ITKCALL( AOM_ask_value_tag( time_schedule_task_tag , SCHEDULE_TAG , &time_schedule_tag ) );

        ITKCALL( AOM_ask_value_tag( time_schedule_tag , OWNING_PROJECT , &currentProject ) );

       // ECHO("currentProject = %u \n", currentProject );
        if( currentProject )
        {
            ITKCALL(PROJ_is_project_active(currentProject, &is_active));
            if( !is_active )
            {
                HANDLE_ERROR( ERROR_TimeSheetEntry_Entry_ItsProject_Inactive_Error );
                customerror = ERROR_TimeSheetEntry_Entry_ItsProject_Inactive_Error;
                goto FOUT;
            }
        }
    }
    //End add 

	nowtime = time(NULL); //获取日历时间
	local=localtime(&nowtime);  //获取当前系统时间

	/*strftime(year,80,"%Y",local);
	strftime(month,80,"%m",local);
	strftime(day,80,"%d",local);*/
	year_local = local->tm_year+1900;
    month_local = local->tm_mon+1;
    day_local = local->tm_mday;
//	ECHO("year = %d\t month = %d\t day = %d\n", year_local, month_local, day_local);

	//ITKCALL(PREF_ask_char_value(PREFERENCE_YFJC_INTERVAL_OF_LOG_TIME,index,&option_value));

	ITKCALL(PREF_ask_char_values(PREFERENCE_YFJC_INTERVAL_OF_LOG_TIME,&count,&optvalues));
	if(count == 0 )
	{
		TC_write_syslog("preference name:YFJC_Interval_Of_Log_Time is not defined\n");
		interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
		tc_strcpy(interval,week_interval);
	}
	else
	{
		//test by wuwei
        logical is_first_line = FALSE;      // add by zhanggl 11-oct, defualt use first line, if don't find group or rule
       
		logical is_in_current_month = TRUE;// add by zhanggl 11-oct, flag whether to allow this month, if use first line.
	//	ECHO("检查首选项的值\n");
		/*将当前用户所在组及角色与检查首选项配置的值比较，首选项值形式：
		Division1｛.｝External Supporter=PerWeek (允许本周，但不得超过本月)
		&Division2｛.｝External Supporter=PerWeek （允许本周，可以超过本月)
		*/

		char *strgroup_name = NULL; 
		ITKCALL(POM_ask_group(&strgroup_name,&group_tag));
	//	ECHO("strgroup_name = %s\n",strgroup_name );

		ITKCALL(SA_ask_group_full_name(group_tag,&group_name));
	//	ECHO("group_name = %s\n",group_name );

		ITKCALL(SA_ask_current_role(&current_role_tag));
		ITKCALL(SA_ask_role_name(current_role_tag,rolename));

	//	ECHO("rolename = %s\n",rolename );
		for(int i = 0; i < count; i ++ )
        {
			isbeyongmonth = false;
			char *option = NULL;

			if(tc_strstr(optvalues[i],"&") != NULL)
			{
				//   ECHO("包含&\n");
					isbeyongmonth = true; 
					STRNG_replace_str(optvalues[i],"&","",&option);
                    //add by zhanggl 11-oct
                    if( i == 0 )
                    {
                        is_in_current_month = FALSE;
                    }
                    //end add
			}else
			{
			//	ECHO("不包含&\n");
                option = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(optvalues[i]) + 1));
				strcpy(option,optvalues[i]);
			}

			if(option && tc_strstr(option,"="))
            {
			//	ECHO("包含= \n");
				grouprole = tc_strtok(option,"=");
				tempinterval = tc_strtok(NULL,"=");
			//	ECHO("grouprole = %s\n",grouprole);
			//	ECHO("tempinterval = %s\n",tempinterval);
				if(tc_strstr(grouprole,"{.}"))
				{
					//既要判断组也要判断角色
					//	ECHO("需要判断组及角色\n");
						char *option_group = NULL;

						STRNG_replace_str(grouprole,"{.}","$",&option_group);
						char *tmpgroup = tc_strtok(option_group,"$");
					//	ECHO("tmpgroup = %s\n",tmpgroup);	
						char *option_role = tc_strtok(NULL,"$");
					//	ECHO("option_role = %s\n",option_role);

						if(tc_strcmp(group_name,option_group) == 0  && tc_strcmp(rolename,option_role) == 0)
						{
                            is_first_line = FALSE;
                            option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(tempinterval) + 1));
							tc_strcpy(option_value,tempinterval);
							break;
						}
				}
				else
				{
					//如果没有{.}则只判断角色
					if(tc_strcasecmp(rolename,grouprole) == 0)
					{
                        is_first_line = FALSE;
						option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(tempinterval) + 1));
					    tc_strcpy(option_value,tempinterval);
                        break;
					}
				}
			}
			else
			{
			//	ECHO("不包含= \n");
				//默认值（Perweek或者Permonth或者（如7~7））
                default_interval = (char*)MEM_alloc( (tc_strlen(option)+1)*sizeof(char));
				tc_strcpy(default_interval ,option);
			}
		}
       // ECHO("is_first_line=%d,is_in_current_month=%d \n",is_first_line,is_in_current_month);
        if( is_first_line && is_in_current_month)
        {
            if(date.month != local->tm_mon)
			{
			//	ECHO("日期不在区间内\n");
				HANDLE_ERROR(ERROR_TimeSheetEntry_Entry_Date_Error);
				customerror = ERROR_TimeSheetEntry_Entry_Date_Error;
			}
        }
        else
        {
        //    ECHO("isbeyongmonth===%d\n",isbeyongmonth);
			//modify by wuwei
			isbeyongmonth=true;
            if(!isbeyongmonth) {
           //     ECHO("不可以超出本月\n");
                if(date.month != local->tm_mon)
                {
              //      ECHO("日期不在区间内\n");
                    HANDLE_ERROR(ERROR_TimeSheetEntry_Entry_Date_Error);
                    customerror = ERROR_TimeSheetEntry_Entry_Date_Error;
                }
            }
        }
		//默认值作为区间
		if(option_value == NULL)
		{
			if(default_interval != NULL)
			{
				option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(default_interval) + 1));
				tc_strcpy(option_value,default_interval);
                MEM_free( default_interval );
                default_interval = NULL;
			}else
			{
			//	ECHO("default value for preference name:YFJC_Interval_Of_Log_Time is not defined\n");
				option_value = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
				tc_strcpy(option_value,week_interval);
			}
	    }
       // ECHO("238 option_value===%s\n",option_value);
		if(tc_strcasecmp(option_value,week_interval) == 0)
		{
			interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(week_interval) + 1));
			tc_strcpy(interval, week_interval);
		}
		else if(tc_strcasecmp(option_value,month_interval) == 0)
		{
			interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(month_interval) + 1));
			tc_strcpy(interval, month_interval);
		}
		else if(tc_strstr(option_value,"~") )
		{
            interval = (char *)MEM_alloc(sizeof( char ) * (tc_strlen(option_value) + 1));
			tc_strcpy(interval, option_value);
		}
        else if(tc_strstr(option_value,"～") )
        {
            STRNG_replace_str(option_value,"～","~",&interval);
        }
	}

	//ECHO("interval = %s\n",interval);
	
	//if(tc_strcasecmp(interval,week_interval) == 0)
	//{
	//	isOk = checkdatebyweek(date,nowtime,(char*)year,(char*)month,(char*)day);
	//}
	//else if(tc_strcasecmp(interval,month_interval) == 0)
	//{
	//	isOk = checkdatebymonth(date,(char*)year,(char*)month);
	//}
	//else
	//{
	//	/* commented by liqz on 20130819
	//	if(date.month != local->tm_mon)
	//	{
	//		HANDLE_ERROR(ERROR_TimeSheetEntry_Entry_Date_Error);
	//		customerror = ERROR_TimeSheetEntry_Entry_Date_Error;
	//	}
	//	*/
	//	isOk = checkdatebydays(date,nowtime,interval,(char*)year,(char*)month,(char*)day);
	//}
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

	if(!isOk)
	{
		HANDLE_ERROR(ERROR_TimeSheetEntry_Create_Date_Error);
		customerror = ERROR_TimeSheetEntry_Create_Date_Error;
	}
	//ECHO("lala customerror = %d\n" , customerror);

	ECHO("end===JCI6checkOverDueLogTime=== \n");

FOUT:
	return customerror;
}








 

 #ifdef __cplusplus
}
#endif
