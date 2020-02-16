
#include <tccore/project.h>
#include <tccore/aom_prop.h>
#include <property/prop.h>
#include <tccore/aom.h>
#include <tccore/method.h>
#include <time.h>
#include <fclasses/tc_date.h>
#include "yfjc_ebp_head.h"

extern "C" int POM_AM__set_application_bypass(logical bypass);
void getLaterDate(date_t date_val,date_t *next_date);
#define JCI6_PROGRAMINFO				 "JCI6_ProgramInfo"//JCI6_ProgramInfo
#define TC_PROGRAM_PREFERRED_ITEMS		 "TC_Program_Preferred_Items"
#define JCI6_TASKTYPE                    "jci6_TaskType"
#define JCI6_SOP                         "jci6_SOP"
#define JCI6_ENDDATE                     "jci6_EndDate"
#define JCI6_STARTDATE                   "jci6_StartDate"
#define DATE_DISPLAY_STR			     "%Y-%m-%d %H:%M"//时间格式

/*************************************************************************************************
* JCI6_updateProgramInfoDate(void *retValType)
*
* Description:
*    This userservice will return send result
*
* Syntax:
*         
*     
*
* Placement:
*    no request
*
**************************************************************************************************/

int JCI6_updateProgramInfoDate(void *retValType)
{
	ECHO("**********************JCI6_updateProgramInfoDate start****************************\n");
	tag_t       programinfo_tag = NULLTAG,
				project_tag = NULLTAG,
				schedule_tag = NULLTAG,
				rev_tag = NULLTAG,
				scheduletask_tag = NULLTAG;
	int			task_type = 0;
	char		*jci6_taskType = NULL;
	int			num = 0,
				i = 0;
	tag_t		*values = NULLTAG;
	char       object_type[WSO_name_size_c+1] = "";

	date_t     start_time = NULLDATE,
			   startdate = NULLDATE,
			   end_date = NULLDATE,
			   sop_date = NULLDATE;

	char       *start_date_str = NULL,
		       *jci6_StartDate_str = NULL,
			   *end_date_str = NULL,
			   *next_date_str = NULL,
			   *sop_date_str = NULL;


	date_t now_date=NULLDATE,next_date=NULLDATE;
	
	//接收JAVA传递数据,数据字符
	ITKCALL(USERARG_get_tag_argument( &scheduletask_tag ));
	
	//得到时间表任务对象的task_type
	ITKCALL(AOM_ask_value_int(scheduletask_tag,"task_type",&task_type));

	ECHO("task_type--->%d\n",task_type);
	if(task_type == 1)//是里程碑任务
	{
		//得到jci6_TaskType属性的值
		ITKCALL(AOM_ask_value_string(scheduletask_tag,JCI6_TASKTYPE,&jci6_taskType));
		ECHO("jci6_taskType--->%s\n",jci6_taskType);
		if(tc_strcmp(jci6_taskType,"gatetype13") == 0)
		{
			//得到时间表
			ITKCALL(AOM_ask_value_tag(scheduletask_tag,"schedule_tag",&schedule_tag));
			//得到时间表所属项目
			ITKCALL(AOM_ask_value_tag(schedule_tag,"owning_project",&project_tag));
			if(project_tag == NULLTAG)
			{
				ECHO("schedule is not assign project,skip...\n");
				SAFE_SM_FREE(jci6_taskType);
				return 0;
			}
			//得到项目信息对象
			ITKCALL(AOM_ask_value_tags(project_tag,TC_PROGRAM_PREFERRED_ITEMS,&num,&values)); 
			ECHO("%s count is %d\n",TC_PROGRAM_PREFERRED_ITEMS,num);
			for( i = 0;i < num;i++ )
			{
				//得到对象的类型
				ITKCALL(WSOM_ask_object_type(values[i],object_type));
				ECHO("%d type is %s\n",i + 1,object_type);
				if( tc_strcmp(object_type,JCI6_PROGRAMINFO) == 0 )
				{
					programinfo_tag = values[i];
					break;
				}
			}
			if(programinfo_tag != NULLTAG)
			{
				//得到项目信息的最新版本
				ITKCALL(ITEM_ask_latest_rev(programinfo_tag,&rev_tag));
				// tyl  2015/01/13 项目关闭冻结信息
		       logical jci6_ActiveStatus = TRUE;
		       ITKCALL(AOM_ask_value_logical(rev_tag, "jci6_ActiveStatus", &jci6_ActiveStatus));
		      if(!jci6_ActiveStatus)
		       {
		         return 0;
		       }
		      // tyl  2015/01/13 项目关闭冻结信息
		
				//得到时间表任务的start_date
				ITKCALL(AOM_ask_value_date(scheduletask_tag,"start_date",&start_time)); 
				//得到rev的SOPDate时间
				ITKCALL(AOM_ask_value_date(rev_tag,JCI6_SOP,&sop_date));
				ITKCALL(AOM_ask_value_date(rev_tag,JCI6_ENDDATE,&end_date));
				ITKCALL(DATE_date_to_string(start_time,DATE_DISPLAY_STR,&start_date_str));
				ITKCALL(DATE_date_to_string(sop_date,DATE_DISPLAY_STR,&sop_date_str));
				ITKCALL(DATE_date_to_string(end_date,DATE_DISPLAY_STR,&end_date_str));
				//得到时间表任务开始时间3个月后的时间
				getLaterDate(start_time,&next_date);
				ITKCALL(DATE_date_to_string(next_date,DATE_DISPLAY_STR,&next_date_str));
				ECHO("start_date--->%s--->%s-->%s\n",start_date_str,JCI6_SOP,sop_date_str);
				if(tc_strcmp(start_date_str,sop_date_str) != 0 && tc_strcmp(end_date_str,next_date_str) != 0)
				{
					ECHO("%s与start_date时间不一样,%s与start_date3个月后的时间也不一样，需要重新设置\n",JCI6_SOP,JCI6_ENDDATE);
					POM_AM__set_application_bypass(TRUE);
					//设置SOPDate和EndDate属性
					ITKCALL(AOM_lock(rev_tag));
					//设置SOPDate属性
					ITKCALL(AOM_set_value_date(rev_tag,JCI6_SOP,start_time));
					ITKCALL(AOM_set_value_date(rev_tag,JCI6_ENDDATE,next_date));
					ITKCALL(AOM_save(rev_tag));
					ITKCALL(AOM_unlock(rev_tag));
					POM_AM__set_application_bypass(FALSE);
				}else if(tc_strcmp(start_date_str,sop_date_str) != 0 && tc_strcmp(end_date_str,next_date_str) == 0)
				{
					ECHO("%s与start_date时间不一样,需要重新设置,%s与start_date3个月后的时间一样,不需要重新设置\n",JCI6_SOP,JCI6_ENDDATE);
					POM_AM__set_application_bypass(TRUE);
					//设置SOPDate
					ITKCALL(AOM_lock(rev_tag));
					//设置SOPDate属性
					ITKCALL(AOM_set_value_date(rev_tag,JCI6_SOP,start_time));
					ITKCALL(AOM_save(rev_tag));
					ITKCALL(AOM_unlock(rev_tag));
					POM_AM__set_application_bypass(FALSE);
				}else if(tc_strcmp(start_date_str,sop_date_str) == 0 && tc_strcmp(end_date_str,next_date_str) != 0)
				{
					ECHO("%s与start_date时间一样,不需要重新设置,%s与start_date3个月后的时间一样,需要重新设置\n",JCI6_SOP,JCI6_ENDDATE);
					POM_AM__set_application_bypass(TRUE);
					//设置EndDate
					ITKCALL(AOM_lock(rev_tag));
					//设置EndDate属性
					ITKCALL(AOM_set_value_date(rev_tag,JCI6_ENDDATE,next_date));
					ITKCALL(AOM_save(rev_tag));
					ITKCALL(AOM_unlock(rev_tag));
					POM_AM__set_application_bypass(FALSE);
				}else
				{
					ECHO("%s与start_date时间,%s与start_date3个月后的时间都一样,都不需要重新设置\n",JCI6_SOP,JCI6_ENDDATE);
				}
				SAFE_SM_FREE(start_date_str);
				SAFE_SM_FREE(sop_date_str);
				SAFE_SM_FREE(end_date_str);
			}else{
				ECHO("project has no programinfo,skip....\n");
			}
			SAFE_SM_FREE(values);
		}
		if(tc_strcmp(jci6_taskType,"gatetype01") == 0)
		{
			//得到时间表
			ITKCALL(AOM_ask_value_tag(scheduletask_tag,"schedule_tag",&schedule_tag));
			//得到时间表所属项目
			ITKCALL(AOM_ask_value_tag(schedule_tag,"owning_project",&project_tag));
			if(project_tag == NULLTAG)
			{
				ECHO("schedule is not assign project,skip...\n");
				SAFE_SM_FREE(jci6_taskType);
				return 0;
			}
			//得到项目信息对象
			ITKCALL(AOM_ask_value_tags(project_tag,TC_PROGRAM_PREFERRED_ITEMS,&num,&values)); 
			ECHO("%s count is %d\n",TC_PROGRAM_PREFERRED_ITEMS,num);
			for( i = 0;i < num;i++ )
			{
				//得到对象的类型
				ITKCALL(WSOM_ask_object_type(values[i],object_type));
				ECHO("%d type is %s\n",i + 1,object_type);
				if( tc_strcmp(object_type,JCI6_PROGRAMINFO) == 0 )
				{
					programinfo_tag = values[i];
					break;
				}
			}
			if(programinfo_tag != NULLTAG)
			{
				//得到项目信息的最新版本
				ITKCALL(ITEM_ask_latest_rev(programinfo_tag,&rev_tag));
			   // tyl  2015/01/13 项目关闭冻结信息
		       logical jci6_ActiveStatus = TRUE;
		       ITKCALL(AOM_ask_value_logical(rev_tag, "jci6_ActiveStatus", &jci6_ActiveStatus));
		       ECHO("jci6_ActiveStatus=====%d\n",jci6_ActiveStatus);
		      if(!jci6_ActiveStatus)
		       {
		       return 0;
		       }
		       // tyl  2015/01/13 项目关闭冻结信息
				//得到时间表任务的start_date
				ITKCALL(AOM_ask_value_date(scheduletask_tag,"start_date",&start_time)); 
				//得到rev的SOPDate时间
				ITKCALL(AOM_ask_value_date(rev_tag,JCI6_STARTDATE,&startdate));
				ITKCALL(DATE_date_to_string(start_time,DATE_DISPLAY_STR,&start_date_str));
				ITKCALL(DATE_date_to_string(startdate,DATE_DISPLAY_STR,&jci6_StartDate_str));
				ECHO("start_date--->%s--->%s-->%s\n",start_date_str,JCI6_STARTDATE,jci6_StartDate_str);
				if(tc_strcmp(start_date_str,jci6_StartDate_str) != 0)
				{
					ECHO("%s与start_date时间不一样，需要重新设置\n",JCI6_STARTDATE);
					POM_AM__set_application_bypass(TRUE);
					//设置StartDate属性
					ITKCALL(AOM_lock(rev_tag));
					//设置jci6_StartDate属性
					ITKCALL(AOM_set_value_date(rev_tag,JCI6_STARTDATE,start_time));
					ITKCALL(AOM_save(rev_tag));
					ITKCALL(AOM_unlock(rev_tag));
					POM_AM__set_application_bypass(FALSE);
				}else
				{
					ECHO("%d与start_date时间一样，不需要设置\n",JCI6_STARTDATE);
				}
				SAFE_SM_FREE(start_date_str);
				SAFE_SM_FREE(jci6_StartDate_str);
			
			}else{
				ECHO("project has no programinfo,skip....\n");
			}
			SAFE_SM_FREE(values);
		}
		SAFE_SM_FREE(jci6_taskType);
	}
	ECHO("**********************JCI6_updateProgramInfoDate end****************************\n");
	return 0;
}


/**得到指定日期3个月后的日期
*date_t date_val (I)      
*date_t *next_date （O）  
**/
void getLaterDate(date_t date_val,date_t *next_date)
{
	int day_table[2][12]=
		{{31,29,31,30,31,30,31,31,30,31,30,31},
		{31,28,31,30,31,30,31,31,30,31,30,31}};
	int  oyear = date_val.year,
		 oday = date_val.day,
		 omonth = date_val.month,
		 nyear = 0,
		 nmonth = 0,
		 nday = 0;
	date_t new_date;

	//printf("%d-->%d-->%d\n",oyear,omonth,oday);

	if( omonth  + 3 >= 12)
	{
		nyear = oyear + 1;
		nmonth = omonth  + 3 - 12;
	}else
	{
		nyear = oyear;
		nmonth = omonth  + 3;
	}
	//判断新的年份是否为闰年
	if( (nyear%4 == 0 && nyear%100 != 0) || nyear%400 == 0 )
	{
		ECHO("is Leap Year--->%d\n",day_table[0][nmonth]);
		//如果新日期月份天数大于旧日期的天数,则新日期天数为旧日期天数
		if(day_table[0][nmonth] > oday)
		{
			nday = oday;
		}else
		{
			nday = day_table[0][nmonth];
		}
	}else
	{
		ECHO("not Leap Year--->%d\n",day_table[1][nmonth]);
		//如果新日期月份天数大于旧日期的天数,则新日期天数为旧日期天数
		if(day_table[1][nmonth] > oday)
		{
			nday = oday;
		}else
		{
			nday = day_table[1][nmonth];
		}
	
	}

	//printf("new date --->%d-->%d-->%d\n",nyear,nmonth,nday);
	new_date.year = nyear;
	new_date.month = nmonth;
	new_date.day = nday;
	new_date.hour = date_val.hour;
	new_date.minute = date_val.minute;
	new_date.second = date_val.second;
	*next_date = new_date;
}
