
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


int JCI6ScheduleTaskDate(METHOD_message_t* msg, va_list args)
{
	printf("**********************JCI6ScheduleTaskDate start****************************\n");
	tag_t       prop_tag = NULLTAG,
				programinfo_tag = NULLTAG,
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

	date_t     start_time;

	prop_tag = va_arg(args, tag_t);//����

	//�õ��޸ĵ�ʱ����������
	ITKCALL(PROP_ask_owning_object(prop_tag,&scheduletask_tag));
	//�õ�ʱ�����������task_type
	ITKCALL(AOM_ask_value_int(scheduletask_tag,"task_type",&task_type));
	if(task_type == 0)//����̱�����
	{
		//�õ�jci6_TaskType���Ե�ֵ
		ITKCALL(AOM_ask_value_string(scheduletask_tag,JCI6_TASKTYPE,&jci6_taskType));
		if(tc_strcmp(jci6_taskType,"gatetype13") == 0)
		{
			//�õ�ʱ���
			ITKCALL(AOM_ask_value_tag(scheduletask_tag,"schedule_tag",&schedule_tag));
			//�õ�ʱ���������Ŀ
			ITKCALL(AOM_ask_value_tag(schedule_tag,"owning_project",&project_tag));
			//�õ���Ŀ��Ϣ����
			ITKCALL(AOM_ask_value_tags(project_tag,TC_PROGRAM_PREFERRED_ITEMS,&num,&values)); 
			printf("%s count is %d\n",TC_PROGRAM_PREFERRED_ITEMS,num);

			for( i = 0;i < num;i++ )
			{
				//�õ����������
				ITKCALL(WSOM_ask_object_type(values[i],object_type));
				printf("%d type is %s\n",i + 1,object_type);
				if( tc_strcmp(object_type,JCI6_PROGRAMINFO) == 0 )
				{
					programinfo_tag = values[i];
					break;
				}
			}
			if(programinfo_tag != NULLTAG)
			{
				//�õ���Ŀ��Ϣ�����°汾
				ITKCALL(ITEM_ask_latest_rev(programinfo_tag,&rev_tag));
				//�õ�ʱ��������start_time
				ITKCALL(AOM_ask_value_date(scheduletask_tag,"start_date",&start_time)); 
				//����SOPDate��EndDate����
				ITKCALL(AOM_lock(rev_tag));
				//����jci6_CloseDate����
				ITKCALL(AOM_set_value_date(rev_tag,JCI6_SOP,start_time));//"jci6_SOP"
				//�õ�ʱ�������ʼʱ��3���º��ʱ��

				ITKCALL(AOM_save(rev_tag));
				ITKCALL(AOM_unlock(rev_tag));
			
			}
			
		}
		SAFE_SM_FREE(jci6_taskType);
	}



	printf("**********************JCI6ScheduleTaskDate end****************************\n");
	return 0;
}