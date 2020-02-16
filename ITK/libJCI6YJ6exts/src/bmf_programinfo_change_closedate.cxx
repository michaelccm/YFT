#include <tccore/project.h>
#include <tccore/aom_prop.h>
#include <property/prop.h>
#include <tccore/aom.h>
#include <tccore/method.h>
#include <time.h>
#include "yfjc_ebp_head.h"

extern "C" int POM_AM__set_application_bypass(logical bypass);
#define JCI6_PROGRAMINFO				 "JCI6_ProgramInfo"//JCI6_ProgramInfo
#define TC_PROGRAM_PREFERRED_ITEMS		 "TC_Program_Preferred_Items"
#define JCI6_TASKTYPE                    "jci6_TaskType"
#define JCI6_SOP                         "jci6_SOP"
#define JCI6_ENDDATE                     "jci6_EndDate"
#define JCI6_STARTDATE                   "jci6_StartDate"

void close_current_time( date_t * date_tag );

int bmf_programinfo_change_closedate(METHOD_message_t* msg, va_list args)
{
	printf("**********************bmf_programinfo_change_closedate start****************************\n");
	logical     is_active_val;
	tag_t       prop_tag = NULLTAG,
				programinfo_tag = NULLTAG,
				project_tag = NULLTAG;
	char		project_name[PROJ_name_size_c + 1] = "",
				object_type[WSO_name_size_c+1] = "";
	int			num = 0,
				i = 0;
	tag_t		*values = NULLTAG;
	date_t date_val;
    prop_tag = va_arg(args, tag_t);//属性
	ITKCALL(PROP_ask_value_logical(prop_tag,&is_active_val));
	//得到修改的项目
	ITKCALL(PROP_ask_owning_object(prop_tag,&project_tag));
	//得到项目的名称
	ITKCALL(PROJ_ask_name(project_tag,project_name));
	printf("project name is %s\n",project_name);
	//得到项目信息对象
	ITKCALL(AOM_ask_value_tags(project_tag,TC_PROGRAM_PREFERRED_ITEMS,&num,&values)); 
	printf("%s count is %d\n",TC_PROGRAM_PREFERRED_ITEMS,num);
	for( i = 0;i < num;i++ )
	{
		//得到对象的类型
		ITKCALL(WSOM_ask_object_type(values[i],object_type));
		printf("%d type is %s\n",i + 1,object_type);
		if( tc_strcmp(object_type,JCI6_PROGRAMINFO) == 0 )
		{
			programinfo_tag = values[i];
			break;
		}
	
	}
	if( programinfo_tag != NULLTAG )
	{
		//POM_AM__set_application_bypass(TRUE);
		ITKCALL(AOM_lock(programinfo_tag));
		if(is_active_val)
		{
			printf("is_active--->true\n");
			//清空jci6_CloseDate属性
			date_val = NULLDATE;
		}
		else
		{
			printf("is_active--->false\n");
			close_current_time(&date_val);
		}
		//设置jci6_CloseDate属性
		ITKCALL(AOM_set_value_date(programinfo_tag,"jci6_CloseDate",date_val));
		ITKCALL(AOM_save(programinfo_tag));
		ITKCALL(AOM_unlock(programinfo_tag));
		//POM_AM__set_application_bypass(FALSE);	
	}
	SAFE_SM_FREE(values);
	printf("**********************bmf_programinfo_change_closedate end****************************\n");
	return 0;
}


//得到当前时间
void close_current_time( date_t * date_tag )
{
date_t modify_date;  
	struct tm *p;
	time_t now;
	time(&now);
	p = localtime(&now); /* 取得当前时间 */
	modify_date.day = p->tm_mday;
	modify_date.hour = p->tm_hour;
	modify_date.minute = p->tm_min;
	modify_date.month = p->tm_mon;
	modify_date.second = p->tm_sec;
	modify_date.year = p->tm_year + 1900;
	*date_tag = modify_date;
}






