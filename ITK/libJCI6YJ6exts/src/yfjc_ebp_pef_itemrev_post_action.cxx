/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: yfjc_ebp_pef_itemrev_post_action.cxx
# File description: 										   	
#=============================================================================
#	Date				Name		Action	Description of Change					   
#	2013-3-20	       map  		Ini		   default attribute		
#   12-Apr-2013         zhanggl     modify  Change it to post-action
#=============================================================================
 */
#include "yfjc_ebp_head.h"


int setPFADefaultValue(METHOD_message_t *msg, va_list args)
{
	int	  ifail = ITK_ok,
		  ifail1    = 0,
		  ifail2    = 0,
		  ifail3	= 0,
		  n			= 0,
		  n1		= 0,	
		  m			= 0,
		  task_type = 0,
		  num		= 0,
		  num1		= 0,
		  num2		= 0,
		  pre_num	= 0,
		  pre_a		= 0,
		  pre_num1	= 0;
	tag_t item_tag	= NULLTAG,
		  rev_tag	= NULLTAG,
		  gate_task,
		  sch_task		= NULLTAG,
		  *project_data = NULLTAG,
		  *child_task	= NULLTAG,
		  *child_tasks	= NULLTAG;
	char  *p = NULL,
		  **d_left = NULL,
		  *d1[20],
		  *gates[30],
		  item_type = NULL, 
		  *object_name = NULL,
		  *object_type = NULL,
		  *pre_values1 = NULL,
		  **project_ids = NULL,
	      **pre_values = NULL;
	date_t item_date = NULLDATE,
		   finish_date = NULLDATE,
		   before_Date = NULLDATE,
		   before_Date1 = NULLDATE;
	
	ask_opt_debug();
	//Remove and Add by zhanggl 12-Apr
	/*USERARG_get_tag_argument( &rev_tag );

	ITKCALL(AOM_ask_value_strings(rev_tag,"project_ids",&num,&project_ids));
	
	if(num==0){
		if(project_ids != NULL)
		{
			MEM_free(project_ids);
			project_ids = NULL;
		}
		return ifail;
	}
	
	ITKCALL(PROJ_find(project_ids[0],&item_tag));*/
	tag_t assignObj = va_arg( args, tag_t ); 
    ECHO("assignObj = %u\n", assignObj);

	item_tag = va_arg( args, tag_t ); 
	ECHO("proj_tag = %u \n", item_tag );

	if( !assignObj || !item_tag )
	{
		return ifail;
	}
	ITKCALL( ITEM_ask_latest_rev( assignObj, &rev_tag) );
	//----------------------------------------------------------------

	ITKCALL(AOM_ask_value_tags(item_tag,"project_data",&num1,&project_data));			
	
	ITKCALL(ifail2=PREF_ask_char_values("YFJC_Gate_Types_Mapping",&pre_num,&pre_values));
	if(ifail2 != ITK_ok)
	{
		
		if(project_ids != NULL)
		{
			MEM_free(project_ids);
			project_ids = NULL;
		}
		if(project_data != NULL)
		{
			MEM_free(project_data);
			project_data = NULL;
		}
		//*((int*) returnValueType) = 1;
		return ifail2;
	}
	if(pre_num==0)
	{
		
		if(project_ids != NULL)
		{
			MEM_free(project_ids);
			project_ids = NULL;
		}
		if(project_data != NULL)
		{
			MEM_free(project_data);
			project_data = NULL;
		}
		//*((int*) returnValueType) = 1;
		HANDLE_ERROR( ERROR_NO_FIND_PREFER);
		return ERROR_NO_FIND_PREFER;
	}
	
	d_left = (char **)MEM_alloc( 2 * pre_num * sizeof(char*)); 
	for(int i=0;i<pre_num;i++)
	{
		p = tc_strtok(pre_values[i],"=");
		while(p!=NULL)
		{
			d_left[n] = (char*)MEM_alloc((tc_strlen(p)+1)*sizeof(char));
			tc_strcpy(d_left[n], p);
			p = tc_strtok(NULL, "=");
			n++;
		}
	}
	
	
	for(int i=0;i<n/2;i++)
    {
		char gate[30][30] = {},
			 date[30][30] = {};

		sprintf(gate[i],"%s%s%s","jci6_",d_left[2*i],"_gate");
        ECHO("gate[i]==%s\n",gate[i]);
		
		ITKCALL(ifail1=AOM_ask_value_tag(rev_tag,gate[i],&gate_task));
		
		if(ifail1 == ITK_ok)
        {
			vector<tag_t> task_vec;
			if(gate_task == NULLTAG)
            {
				d1[n1++] = d_left[2*i+1];
				
				for(int k=0;k<num1;k++)
				{
					ITKCALL(AOM_ask_value_string(project_data[k],"object_type",&object_type));
					
					if(tc_strcmp(object_type,"Schedule") == 0)
					{
						//modify by wuwei---sch_summary_task 
						ITKCALL(AOM_ask_value_tag(project_data[k],"fnd0SummaryTask",&sch_task));
						
						ITKCALL(AOM_ask_value_tags(sch_task,"child_task_taglist",&num2,&child_task));
						
						for(int j=0;j<num2;j++)
                        {
							ITKCALL(AOM_ask_value_int(child_task[j],"task_type",&task_type));
							if(task_type == 1)
							{
								char *jci6_TaskType = NULL;
								ITKCALL(AOM_ask_value_string(child_task[j],"jci6_TaskType",&jci6_TaskType));
                                
								if(tc_strcmp(jci6_TaskType,d_left[2*i+1]) == 0)
								{
                                    task_vec.push_back(child_task[j]);
									ITKCALL(AOM_lock ( rev_tag ));
									ITKCALL(AOM_set_value_tag(rev_tag,gate[i],child_task[j]));
									ITKCALL(AOM_save ( rev_tag ));
									ITKCALL(AOM_unlock ( rev_tag ));
									/*m2++;*/
								}
                                if(jci6_TaskType != NULL)
					            {
						            MEM_free(jci6_TaskType);
						            jci6_TaskType = NULL;
					            }
							}
                            int childCount = 0;
                            tag_t *childTasks = NULL;
                            ITKCALL(AOM_ask_value_tags(child_task[j],"child_task_taglist",&childCount,&childTasks));
                            for(int m=0;m<childCount;m++)
                            {
                                ITKCALL(AOM_ask_value_int(childTasks[m],"task_type",&task_type));
							    if(task_type == 1)
							    {
    								char *jci6_TaskType = NULL;
								    ITKCALL(AOM_ask_value_string(childTasks[m],"jci6_TaskType",&jci6_TaskType));
								    if(tc_strcmp(jci6_TaskType,d_left[2*i+1]) == 0)
								    {
                                        task_vec.push_back(childTasks[m]);
									    ITKCALL(AOM_lock ( rev_tag ));
									    ITKCALL(AOM_set_value_tag(rev_tag,gate[i],childTasks[m]));
									    ITKCALL(AOM_save ( rev_tag ));
									    ITKCALL(AOM_unlock ( rev_tag ));
									    /*m2++;*/
								    }
                                    if(jci6_TaskType != NULL)
					                {
						                MEM_free(jci6_TaskType);
						                jci6_TaskType = NULL;
					                }
							    }
                            }
						}
					}
					if(object_type != NULL)
					{
						MEM_free(object_type);
						object_type = NULL;
					}
				}
			}
			
		/*	ECHO("m2===========%d  n1===========%d\n",m2,n1);
			if(m2 != n1)
			{
				if(child_task != NULL)
				{
					MEM_free(child_task);
					child_task = NULL;
				}
				
				if(project_ids != NULL)
				{
					MEM_free(project_ids);
					project_ids = NULL;
				}
				
				if(project_data != NULL)
				{
					MEM_free(project_data);
					project_data = NULL;
				}
				
				for (int a=0; a<n; a++)
				{
					MEM_free(d_left[a]);
				}
				
				MEM_free(d_left);

				HANDLE_ERROR( ERROR_NO_FIND_GATE );
				return ERROR_NO_FIND_GATE;
			}*/

			sprintf(date[i],"%s%s%s","jci6_",d_left[2*i],"_pkr_date");
            ECHO("date[i]==%s\n",date[i]);
			
			ITKCALL(ifail3 = AOM_ask_value_date(rev_tag,date[i],&item_date));
			
			if(ifail3 != ITK_ok)
			{
				if(child_task != NULL)
				{
					MEM_free(child_task);
					child_task = NULL;
				}
		
				if(project_ids != NULL)
				{
					MEM_free(project_ids);
					project_ids = NULL;
				}
				if(project_data != NULL)
				{
					MEM_free(project_data);
					project_data = NULL;
				}
				for (int a=0; a<n; a++)
				{
					MEM_free(d_left[a]);
				}
				MEM_free(d_left);
				

				HANDLE_ERROR( ERROR_NO_FIND_PROPERTY );
				return ERROR_NO_FIND_PROPERTY;
			}
			
			if(item_date.month == 0)
			{
				int zop=0;
                int vecSize = task_vec.size();
                ECHO("vecSize==%d\n",vecSize);
				for(int l=0;l<vecSize;l++)
                {
					ITKCALL(ifail = AOM_ask_value_date(task_vec[l],"finish_date",&finish_date));
					
					ITKCALL(ifail=PREF_ask_char_value("YFJC_PFA_Dates_Before_Gate",pre_num1,&pre_values1));
					if(ifail != ITK_ok)
					{
						return ifail;
					}
					if( pre_values1 == NULL )
					{
						GetBeforeDate(finish_date,21,&before_Date);
						ITKCALL(AOM_lock ( rev_tag ));
						ITKCALL(AOM_set_value_date(rev_tag,date[i],before_Date));
						ITKCALL(AOM_save ( rev_tag ));
						ITKCALL(AOM_unlock ( rev_tag ));
						TC_write_syslog( "首选项YFJC_PFA_Dates_Before_Gate的值为空\n");
                        zop++;
					}
                    else
					{
						pre_a = strtoul(pre_values1,0,10);
						GetBeforeDate(finish_date,pre_a,&before_Date1);
						ITKCALL(AOM_lock ( rev_tag ));
						ITKCALL(AOM_set_value_date(rev_tag,date[i],before_Date1));
						ITKCALL(AOM_save ( rev_tag ));
						ITKCALL(AOM_unlock ( rev_tag ));
                        zop++;
					}
                    
					if(pre_values1 != NULL)
					{
						MEM_free(pre_values1);
						pre_values1 = NULL;
					}
					
					if(zop == 1)
					break;
				}
			}
		}
	}
	
	if(child_task != NULL)
	{
		MEM_free(child_task);
		child_task = NULL;
	}

	if(project_ids != NULL)
	{
		MEM_free(project_ids);
		project_ids = NULL;
	}
	if(project_data != NULL)
	{
		MEM_free(project_data);
		project_data = NULL;
	}
	for (int a=0; a<n; a++)
    {
        MEM_free(d_left[a]);
    }
	MEM_free(d_left);
	//*((int*) returnValueType) = 0;
	return ifail;
}
