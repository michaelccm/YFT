/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   : yfjc_ebp_Delete_schedule_task_check.cxx
    Module : Extension
    
    Periodic driving task

    SP-CUS-006／不允许删除已填写工时的任务 
 ============================================================================================================ 
 DATE           Name             Description of Change 
 08-Apr-2014    chenkai          creation $HISTORY$
 16-Jun-2014	mengyawei		 modify  BMF to user service
 ============================================================================================================*/
#include <ss/ss_errors.h>
#include "yfjc_ebp_head.h"
#ifdef __cplusplus
extern "C" {
#endif
	int Delete_schedule_task_check( void * returnValueType )
	//int Delete_schedule_task_check( METHOD_message_t *msg, va_list args )
	{
		int ifail = ITK_ok,
			ifail1 = ITK_ok,
			ref_count = 0,
			release_num = 0,
			process_num = 0,

			*levels = NULL, //WSOM_where_referenced

			w_count = 0,
			i_count = 0,
			h_count = 0,
			t_value = 0; 

		

		tag_t scheduleTask_tag  = NULLTAG,
			relation_type = NULLTAG,
			//*ref_classes = NULL, //POM_referencers_of_instance
			*ref_objects = NULL,
			*release_values = NULL,
			*process_values = NULL;




		char object_type_name[WSO_name_size_c+1]  = "",
			*option_value = NULL,
			**relations = NULL, //WSOM_where_referenced
			scheduleTask_name[WSO_name_size_c+1] = "";

		//2014-6-16 mengyawei added
		char * rtnValue = NULL;
		char rtn_err[256] = {'\0'};
		char tmp_err[256] = {'\0'};
		string tmp_error;
		int schedule_task_cnt = 0;
		tag_t *schedule_tasks = NULL;
		int j = 0;


		
		//1.DEBUG------true/faluse
		ask_opt_debug();

		//USERARG_get_tag_argument(&scheduleTask_tag);

		//5取得首选项t_value

		ITKCALL(ifail1 = PREF_ask_char_value("YFJC_delete_Schedule_types",0,&option_value));
		if (ifail == PF_NOTFOUND)
		{
			EMH_clear_last_error( PF_NOTFOUND );
			ECHO("=====首选项YFJC_delete_Schedule_types未配置！默认值为0======\n");
			t_value = 0;
		} 
		else if (option_value == NULL || tc_strcmp( option_value, "") == 0 )
		{
			t_value = 0;
			ECHO("=====首选项YFJC_delete_Schedule_types的值未设定！默认值为0======\n");

		} 
		else 
		{
			t_value = atoi(option_value);
			ECHO("=====首选项YFJC_delete_Schedule_types的值为 %d！======\n",t_value);
		}



		USERARG_get_tag_array_argument(&schedule_task_cnt , &schedule_tasks);
		ECHO("--->		schedule_task_cnt = %d\n",schedule_task_cnt);
		for(j = 0; j <schedule_task_cnt;j ++)
		{
			scheduleTask_tag = schedule_tasks[j];

			ITKCALL(AOM_load(scheduleTask_tag));
			ITKCALL(AOM_lock_for_delete(scheduleTask_tag));

			ITKCALL(WSOM_ask_name(scheduleTask_tag,scheduleTask_name));
		
			//3.取得ScheduleTask相关的TimeSheetEntry

			ITKCALL(WSOM_where_referenced(scheduleTask_tag,1,&ref_count,&levels,&ref_objects,&relations));

			for (int i = 0;i < ref_count; i++)
			{	
				ITKCALL( WSOM_ask_object_type( ref_objects[i], object_type_name ) );

				if (!tc_strcmp(object_type_name,TIMESHEETENTRY))
				{
					//4取得TimeSheetEntry的状态，x1（Working：w_count）、x2（In-Process: i_count）、x3(Has-Status :h_count)

					//获得发布状态
					ITKCALL(AOM_ask_value_tags(ref_objects[i],"release_status_list",&release_num,&release_values));
					int a = release_num;
					//获得流程
					ITKCALL(AOM_ask_value_tags(ref_objects[i],"process_stage_list",&process_num,&process_values));
					int b = process_num;
				
					//a发布状态，b流程状态
					//if (a == 0 && b == 0)//working
					//{
					//	++w_count;;
					//}
					//else if(a != 0 && b == 0)//has status
					//{
					//	++i_count;
					//}
					//else if (a == 0 && b != 0)//inprocess
					//{
					//	++h_count;
					//}else{

					//}
					if(a==0 && b==0){
						++w_count;
					}else if(a!=0 && b==0 ){
						++i_count;
					}else if(a==0 && b!=0){
						++h_count;
					}

					//释放内存
					if (release_values)
					{
						MEM_free(release_values);
						release_values = NULL;
					}

					if (process_values)
					{
						MEM_free(process_values);
						process_values = NULL;
					}
				}
			}
			ECHO("********Working---------->%d！********\n",w_count);
			ECHO("****In-Process---------->%d！*********\n",h_count);
			ECHO("****Has-Status---------->%d！*********\n",i_count);
			//6 逻辑判断，弹出错误
			if((t_value == 0) && (w_count + i_count + h_count > 0) )
			{
				sprintf(tmp_err , "[ERROR] %s 存在已定义工时，不允许删除！\n",scheduleTask_name);
				ECHO("%s",tmp_err);
				sprintf(rtn_err , "[ERROR] 该任务或子任务已填写工时，不允许删除!");
				//tmp_error.append(tmp_err);
			}
			else if((t_value == 1) && (i_count + h_count > 0))
			{	
				sprintf(tmp_err,"[ERROR] %s 存在已提交流程或归档的工时，不允许删除!\n",scheduleTask_name);
				ECHO("%s",tmp_err);
				sprintf(rtn_err , "[ERROR] 该任务或子任务存在审批中及已生效的工时，不允许删除！");
				//tmp_error.append(tmp_err);
			}
			else if((t_value == 2) && (i_count > 0))
			{
				sprintf(tmp_err,"[ERROR] %s 存在已归档的工时，不允许删除！\n",scheduleTask_name);
				ECHO("%s\n",tmp_err);
				sprintf(rtn_err , "[ERROR] 该任务或子任务存在已生效的工时，不允许删除！");
				//tmp_error.append(tmp_err);
			}
			if (ref_objects)
			{
				MEM_free(ref_objects);
				ref_objects = NULL;
			}

		}
		if (option_value)
		{
			MEM_free(option_value);
			option_value = NULL;
		}
		if(schedule_tasks)
		{
			MEM_free(schedule_tasks);
			schedule_tasks = NULL;
		}
		tmp_error.assign(rtn_err);
		rtnValue = (char*)MEM_alloc(sizeof(char)*(tmp_error.length()+1));
		tc_strcpy(rtnValue,tmp_error.c_str());
		ECHO("--->		rtnValue = %s\n",rtnValue);

		 *((char **) returnValueType) = rtnValue;

		return ITK_ok ;
	}

#ifdef __cplusplus
}
#endif
