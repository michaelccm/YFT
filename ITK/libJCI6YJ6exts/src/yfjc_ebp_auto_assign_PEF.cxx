/*============================================================================================================
File description:

Filename: yfjc_ebp_auto_assign_PEF.cxx
Module  : user_exits

import programInfo 
============================================================================================================
DATE           Name             Description of Change
01-Mar-2013    liuc          creation
$HISTORY$
13-Mar-2013    liuc          modification
============================================================================================================*/
#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"

#ifdef __cplusplus
extern "C" {
#endif

	int yfjc_ebp_auto_assign_PEF( EPM_action_message_t msg )
	{
		int debug			= 0,
			i				= 0,
			task_count		= 0,
			arg_count		= 0,
			target_attachments_count  =0,
			num				= 0,
			ifail			=ITK_ok;

	
		char	object_name[WSO_name_size_c + 1]		= "",
			task1[150]								= "",
			task2[150] 								= "",
			task3[150] 								= "";
		char *arg_str_value		= NULL,
			*pArgTasks			= NULL,
			*para_value			=NULL,
			*para_name			=NULL,
			*task_name			=NULL;

		tag_t rootTask			=NULLTAG,
			relation_type		=NULLTAG,
			jci6_ProjectTL		=NULLTAG,
			*target_attachments =NULL,
			*subtasks			=NULL;

		ECHO("----------yfjc_ebp_auto_assign_PEF start-----------------\n");
		arg_count = TC_number_of_arguments(msg.arguments);

		ECHO("arg_count-->%d\n",arg_count);
		for(i = 0; i < arg_count; i ++)
		{
			arg_str_value = TC_next_argument(msg.arguments);
			
			ECHO("arg_str_value-->%s\n",arg_str_value);
			ITKCALL(ITK_ask_argument_named_value(arg_str_value,&para_name,&para_value));

				ECHO("para_name-->%s\n",para_name);
			if(tc_strcmp(para_name, "debug") == 0)
			{	
				if( tc_strcasecmp( para_value, YJ6_TRUE ) == 0 )
				{
					debug = 1;
				}
			}
			if(tc_strcmp(para_name, "tasks") == 0)
			{
				char *newStr = NULL; 
				ECHO("para_value-->%s\n",para_value);
				ITKCALL(STRNG_replace_str(para_value,";",";",&newStr));
				ECHO("newStr-->%s\n",newStr);
				sscanf( newStr, "%[^;];%[^;];%s", task1,task2 ,task3);

				ECHO("task1-->%s  task2-->%s  task3-->%s\n",task1,task2,task3);
				SAFE_SM_FREE( newStr );
			}
			if(para_name)
				SAFE_SM_FREE(para_name);
			if(para_value)
				SAFE_SM_FREE(para_value);
		}

		ITKCALL(EPM_ask_root_task(msg.task,&rootTask));
		ITKCALL(EPM_ask_sub_tasks(rootTask,&task_count,&subtasks));
		if(debug)
			TC_write_syslog("task_count==========%d \n" ,task_count);
		ITKCALL(EPM_ask_attachments(rootTask,EPM_target_attachment,&target_attachments_count,&target_attachments));	
		vector<tag_t> vecSmte;
        time_t T;
        struct tm * timenow;
        time ( &T );
        timenow = localtime ( &T );
        time_t currentDate = mktime (timenow);
        char jci6_gateN_PEF[20] = "";
		for(i=0;i<target_attachments_count;i++)
		{
			char *object_type=NULL;
			
			ITKCALL(WSOM_ask_object_type2(target_attachments[i],&object_type));

			ECHO("object_type-->%s\n",object_type);

			if(tc_strcmp(object_type,"ItemRevision") ==0)  //JCI6_PFARevision
			{
				char ** project_ids			= NULL;
				tag_t   *project_lists      = NULL,
					programInfoRev      = NULLTAG,
					*children           = NULL,
                    currentGate     = NULLTAG;
				int     projectNum          = 0;

				ITKCALL(AOM_ask_value_tags(target_attachments[i],"project_list",&projectNum,&project_lists));

				ECHO("projectNum-->%d\n",projectNum);
				if(projectNum>0)
				{
					int preferred_ItemNum = 0;
					tag_t *preferred_Items = NULL;
					ITKCALL(AOM_ask_value_tags(project_lists[0],"TC_Program_Preferred_Items",&preferred_ItemNum,&preferred_Items));

					for(int k=0;k<preferred_ItemNum;k++)
					{
						char object_type[WSO_name_size_c+1] = "";
						ITKCALL(WSOM_ask_object_type(preferred_Items[k],object_type));
						if(tc_strcmp(object_type,"JCI6_ProgramInfo")==0)
						{
							ITEM_ask_latest_rev(preferred_Items[k],&programInfoRev);
							ITKCALL( AOM_ask_value_tag( programInfoRev, "jci6_ProjectTL", &jci6_ProjectTL ) );
							if( debug )
								ECHO("jci6_ProjectTL值为：%u\n", jci6_ProjectTL );
						}
                        else if(tc_strcmp(object_type,"Schedule")==0)
                        {
                            tag_t scheduleSummaryTask = NULLTAG;
							//modify by wuwei----  sch_summary_task
					        ITKCALL(AOM_ask_value_tag(preferred_Items[k],"fnd0SummaryTask",&scheduleSummaryTask));
                            int child_task_taglistNum = 0;
	                        tag_t* child_task_taglist =NULL;
	                        ITKCALL(AOM_ask_value_tags(scheduleSummaryTask,"child_task_taglist",&child_task_taglistNum,&child_task_taglist));
                           
                            int tempNum = 0;
                            for (int j = 0; j < child_task_taglistNum; j++)
                            {
                                int task_type = 0;
                                char* jci6_TaskType = NULL;
                                ITKCALL(AOM_ask_value_int(child_task_taglist[j], "task_type", &task_type));
                                ITKCALL(AOM_ask_value_string(child_task_taglist[j], "jci6_TaskType", &jci6_TaskType));

								ECHO("jci6_TaskType：%s\n", jci6_TaskType );
                                if(task_type==1&&tc_strstr(jci6_TaskType,"gatetype")!=NULL)
                                {
                                    time_t finish_time = getFinishDate(child_task_taglist[j]);
                                    int tempNum2 = compareDate(finish_time,currentDate);
                                    if(tempNum2>0)
                                    {
                                        if(tempNum!=0)
                                        {
                                            if(tempNum2<tempNum)
                                            {
                                                 tempNum = tempNum2;
                                                 currentGate = child_task_taglist[j];
                                            }
                                        }
                                        else
                                        {
                                             tempNum = tempNum2;
                                             currentGate = child_task_taglist[j];
                                        }
                                    }
                                }
                                int child_task_taglistNum2 = 0;
	                            tag_t* child_task_taglist2 =NULL;
	                            ITKCALL(AOM_ask_value_tags(child_task_taglist[j],"child_task_taglist",&child_task_taglistNum2,&child_task_taglist2));
                                for(int k = 0; k < child_task_taglistNum2; k++)
                                {
                                    int task_type2 = 0;
                                    char* jci6_TaskType2 = NULL;
                                    ITKCALL(AOM_ask_value_int(child_task_taglist2[k], "task_type", &task_type2));
                                    ITKCALL(AOM_ask_value_string(child_task_taglist2[k], "jci6_TaskType", &jci6_TaskType2));
                                    if(task_type2==1&&tc_strstr(jci6_TaskType2,"gatetype")!=NULL)
                                    {
                                        time_t finish_time = getFinishDate(child_task_taglist2[k]);
                                        int tempNum2 = compareDate(finish_time,currentDate);
                                        if(tempNum2>0)
                                        {
                                            if(tempNum!=0)
                                            {
                                                if(tempNum2<tempNum)
                                                {
                                                     tempNum = tempNum2;
                                                     currentGate = child_task_taglist2[k];
                                                }
                                            }
                                            else
                                            {
                                                 tempNum = tempNum2;
                                                 currentGate = child_task_taglist2[k];
                                            }
                                        }
                                    }
                                    if(jci6_TaskType2)
                                    {
	                                    MEM_free(jci6_TaskType2);
	                                    jci6_TaskType2 = NULL;
                                    }
                                }
                                if(jci6_TaskType)
                                {
	                                MEM_free(jci6_TaskType);
	                                jci6_TaskType = NULL;
                                }
	                        }
                        }
					}
					if(project_lists){
						MEM_free(project_lists);
						project_lists = NULL;
					}
				}

				ECHO("currentGate==%u\n",currentGate);
				
				//modify by wuwei
              if(currentGate)
                {
                    if(debug)
                            ECHO("currentGate==%u\n",currentGate);
                    char *jci6_TaskType = NULL;
                    ITKCALL(AOM_ask_value_string(currentGate, "jci6_TaskType", &jci6_TaskType));
                    sprintf(jci6_gateN_PEF,"%s%s%s","jci6_gate",jci6_TaskType+(tc_strlen(jci6_TaskType)-1),"_PEF");
                }
                else
                {
                     if(debug)
                            ECHO("未找到当前门对象...\n");
                     HANDLE_ERROR( ERROR_CURRENTGATE_NOT_FOUND )
                }

				

				if(programInfoRev==NULLTAG){
					EMH_store_initial_error( EMH_severity_user_error, ERROR_PROGRAMINFO_NOT_FOUND);
					ifail =ERROR_PROGRAMINFO_NOT_FOUND;
					goto GOCON ;
				}

				if(jci6_ProjectTL==NULLTAG)
				{
					EMH_store_initial_error( EMH_severity_user_error, ERROR_PROJECTTL_NOT_FOUND);
					ifail =ERROR_PFAREVISION_NOT_FOUND;
					goto GOCON ;
				}
				ITKCALL(AOM_ask_value_tags(target_attachments[i],"ps_children",&num,&children));
				if(num==0){
					EMH_store_initial_error( EMH_severity_user_error, ERROR_PFAREVISION_NOT_FOUND);
					ifail =ERROR_PFAREVISION_NOT_FOUND;
					goto GOCON ;

				}
				for(int k=0;k<num;k++)
				{
                    char *jci6_gateN_PEF_value = NULL;
                    if(debug)
                         ECHO("241 jci6_gateN_PEF==%s\n",jci6_gateN_PEF);
                    ITKCALL(AOM_ask_value_string(children[k],jci6_gateN_PEF,&jci6_gateN_PEF_value));
                    if(debug)
                         ECHO("244 jci6_gateN_PEF_value==%s\n",jci6_gateN_PEF_value);
                    if(tc_strcmp(jci6_gateN_PEF_value,"Monitor")==0||tc_strcmp(jci6_gateN_PEF_value,"N/A")==0)
                    {
                        if(debug)
                           ECHO("%s的属性值是%s\n",jci6_gateN_PEF,jci6_gateN_PEF_value);
                    }
                    else
                    {
                        tag_t smte_user = NULLTAG;
					    ITKCALL(AOM_ask_value_tag(children[k],"jci6_SMTELeader",&smte_user));
					    if(smte_user!=NULLTAG)
					    {
						    vecSmte.push_back(smte_user);
					    }
                    }
				}

				if(project_lists)
					MEM_free(project_lists);
				if(children)
					MEM_free(children);

				break;
			}
		
			SAFE_SM_FREE(object_type);
		}
		// "指派PKR责任工程师"   "更新PKR任务参考信息"  "设置PKR检查项"

		for(i = 0 ; i < task_count;i++)
		{
			ITKCALL( AOM_ask_value_string( subtasks[i],"object_name", &task_name )); 
			if(tc_strcmp(task_name,task1)==0||tc_strcmp(task_name,task2)==0){
				if(jci6_ProjectTL){
					assignUser(subtasks[i],jci6_ProjectTL);
				}
			}
			if(tc_strcmp(task_name,task3)==0){
				int smteNum = vecSmte.size();
				for(int j=0;j<smteNum;j++)
				{
					assignUser(subtasks[i],vecSmte[j]);
				}				
			}			
		}

		if(target_attachments_count==0){
			if(debug)
				ECHO("未找到流程下挂目标关系的对象 \n");
		}
GOCON:	
		if(target_attachments)
			MEM_free(target_attachments);

		if(subtasks)
			MEM_free(subtasks);
		if(debug)		
			ECHO("yfjc_ebp_auto_assign_PEF end! \n");

		return ifail;
	}

#ifdef __cplusplus
}
#endif