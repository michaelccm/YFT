/*
#===========================================================================================
#																			   
#			Copyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#===========================================================================================
# File name: yfjc_assign_manager.cxx
# File description: 										   	
#===========================================================================================
#	Date			Name		Action	Description of Change					   
#	2017-7-11	    wuh   		Ini		6.2 US-FUN-05／设置Group的Manager为Reviewer		
#===========================================================================================
*/
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

	#define ERROR_ASSIGN_REVIEW (EMH_USER_error_base + 501)

	int yfjc_assign_manager( EPM_action_message_t msg )
	{
		ECHO("**************************yfjc_assign_manager start***************************\n");
		int i= 0,j=0,arg_cnt = 0,att_cnt = 0,ifail = 0;
		char *tmp_arg = NULL,*para_name = NULL,*para_value = NULL;
		logical isDebug = FALSE;
		char proName[32] = "",roleName[128] = "";
		tag_t root_task = NULLTAG,*taskAttches = NULL,suppForm = NULLTAG,group = NULLTAG,query_tag = NULLTAG;
		char object_type[WSO_name_size_c+1]= "",groupname[SA_group_name_size_c + 1] = "";
		char **query_keys = NULL,**query_values = NULL;
		int num_found =0,sub_task_count= 0,signoff_count=0;
		tag_t *results = NULL,*sub_tasks = NULL,*signoffs = NULL;


		//解析handler参数
		arg_cnt = TC_number_of_arguments(msg.arguments);
		for(i = 0; i < arg_cnt ; i ++)
		{
			tmp_arg = TC_next_argument(msg.arguments);
			ITKCALL(ITK_ask_argument_named_value( tmp_arg , &para_name , &para_value ));
			if(!tc_strcmp(para_name , "debug"))
			{
				if(!tc_strcasecmp(para_value , "true"))
				{
					isDebug = TRUE;
				}
			}else if(!tc_strcmp(para_name , "prop"))
			{
				tc_strcpy(proName,para_value);
			}else if(!tc_strcmp(para_name , "role"))
			{
				tc_strcpy(roleName,para_value);
			}
			DOFREE(para_name);
			DOFREE(para_value);
		}


		//获得流程目标中所有的JCI6_Ext2Supp表单
		ITKCALL(EPM_ask_root_task(msg.task, &root_task));
		ITKCALL(EPM_ask_attachments(root_task, EPM_target_attachment, &att_cnt, &taskAttches));
		
		for(i=0;i<att_cnt;i++)
		{
			//获得对象类型
			ITKCALL(WSOM_ask_object_type(taskAttches[i],object_type));
			if(!tc_strcmp(object_type,"JCI6_Ext2Supp"))
			{			
				suppForm = taskAttches[i];
				break;
			}
		}

		//获得指定ExtSupporter指定属性的值
		if(suppForm != NULLTAG)
		{
			ITKCALL(AOM_ask_value_tag(suppForm,proName,&group));
			//获得groupname
			ITKCALL(SA_ask_group_name(group,groupname));
			//ITKCALL(QRY_find( "Admin – Group/Role Membership", &query_tag) );
			ITKCALL(QRY_find( "__YFJC_Group/Role Membership", &query_tag) );
			//给定查询条件
			query_keys = (char **) MEM_alloc(2 * sizeof(char*));
			query_values = (char **) MEM_alloc(2 * sizeof(char*));
			char *key_group = TC_text("Group");
			char *key_role = TC_text("Role");
			query_keys[0] = (char *) MEM_alloc( (tc_strlen(key_group)+ 1) * sizeof(char));
			query_keys[1] = (char *) MEM_alloc( (tc_strlen(key_role)+ 1) * sizeof(char));
			query_values[0] = (char *) MEM_alloc( (tc_strlen(groupname)+ 2) * sizeof(char));
			query_values[1] = (char *) MEM_alloc( (tc_strlen(roleName)+ 2) * sizeof(char));
			tc_strcpy(query_keys[0],key_group);
			tc_strcpy(query_keys[1],key_role);
			tc_strcpy(query_values[0],groupname);
			tc_strcat(query_values[0],"*");
			tc_strcpy(query_values[1],roleName);
			tc_strcat(query_values[1],"*");
			ECHO("查询条件为%s===%s,查询条件为%s====%s\n",query_keys[0],query_values[0],query_keys[1],query_values[1]);
			
			ITKCALL(QRY_execute( query_tag , 2 , query_keys , query_values , &num_found , &results ) );
			if(isDebug)
				ECHO("result count is %d\n",num_found);
			if(num_found > 0)
			{
				//指派审核人
				ITKCALL(EPM_ask_sub_tasks(msg.task,&sub_task_count,&sub_tasks));
				if(isDebug)
					ECHO("sub_task_count = %d\n",sub_task_count);
				for(i=0;i<sub_task_count;i++)
				{
					ITKCALL(WSOM_ask_object_type(sub_tasks[i],object_type));
					if(isDebug)
						ECHO("子任务类型为%s\n",object_type);
					if(tc_strcmp(object_type,"EPMSelectSignoffTask")==0)
					{
						ECHO("准备指派用户...\n");
						for(j=0;j<num_found;j++)
						{
							ITKCALL(ifail = EPM_create_adhoc_signoff(sub_tasks[i], results[j], &signoff_count, &signoffs ) );
							if(ifail == 0)
							{
								ECHO("成功\n");
								ITKCALL(EPM_set_adhoc_signoff_selection_done(sub_tasks[i],true));
							}else if(ifail == 22109)
							{
								EMH_clear_last_error(ifail);
								ECHO("存在相同的审核者....\n");
								ifail = 0;
							}else
							{
								char* err_string = NULL;                                                                            
								EMH_ask_error_text (ifail, &err_string);                                                            
								ECHO("*!ERROR!* ifail: %d ERROR MSG: %s.\n", ifail, err_string); 
							}
							if(ifail != 0)
							{
								ITKCALL(EMH_store_error_s1(EMH_severity_error,ERROR_ASSIGN_REVIEW,"节点指派负责人失败"));
								ifail = CR_error_in_handler;
								break;
							}
						}
					}
				}
				DOFREE(sub_tasks);
			}
			DOFREE(results);
			//释放
			for(i=0;i<2;i++)
			{
				MEM_free(query_keys[i]);
				MEM_free(query_values[i]);
			}
			DOFREE(query_keys);
			DOFREE(query_values);
		}
		DOFREE(taskAttches);
		ECHO("**************************yfjc_assign_manager end***************************\n");
		return ifail;
	}


#ifdef __cplusplus
}
#endif
