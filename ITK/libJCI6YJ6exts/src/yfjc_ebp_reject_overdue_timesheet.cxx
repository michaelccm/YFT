/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   : yfjc_ebp_regect_overdue_timesheet.cpp
    Module : user_exits
    
    

    SP-CUS-001／设置外包工时批准时限
============================================================================================================
DATE           Name             Description of Change
11_JUN_2014    mengyawei          creation
$HISTORY$

============================================================================================================*/
#include <tc/envelope.h>
#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
#include "yfjc_time.h"
#include <stdio.h>
//#include <io.h>
#define TimeSheetOverDueMailName		"TimeSheet过期未审批通知"
#define TimeSheetOverDueMailContent		"过期未审批的TimeSheet明细详见附件"
#define TimeSheetOverDueDatasetName			"TimeSheet过期未评审明细!"
#ifdef __cplusplus
extern "C" {
#endif
	
	//create_dataset("PDF","PDF_Reference",pdf_ds_name,"pdf",pdf_file_name,named_ref_file,rev,&dataset);
	//static int create_dataset(char *ds_type,char *ref_name,char *ds_name,char *ext, char *fullfilename, char* filename, tag_t *dataset);
	
	//#include <unistd.h>
	extern int POM_AM__set_application_bypass(logical bypass);

	

int FindTargetTool( tag_t ds_type, const char *ref_name, tag_t *target_tool )
{
	int ifail = 0, tool_count = 0 , j = 0, i = 0,no_refs = 0, *export_flags = NULL;
	tag_t *tool_list = NULL;
	logical found = FALSE;
	char **ref_names = NULL;
	ITKCALL( AE_ask_datasettype_tools(ds_type, &tool_count, &tool_list ) );


	for(i=0; i<tool_count; i++ )
	{


		ifail = AE_ask_tool_oper_refs( ds_type, tool_list[i], ACTION_open, &no_refs, &ref_names, &export_flags );

		if( ifail == ITK_ok )
		{
			for( j=0; j<no_refs; j++ )
			{
				if( strcmp( ref_names[j], ref_name) == 0 && export_flags[j] == 1 )
				{
					*target_tool = tool_list[i];
					found = TRUE;
					break;
				}
			}
		}
		MEM_free( export_flags );
		MEM_free( ref_names );

		if( found )
			break;
	}

	MEM_free( tool_list );

	return ifail;
}

int create_dataset(char *ds_type,char *ref_name,char *ds_name,char *ext, char *fullfilename, char* filename, tag_t *dataset)
{
	int ifail = ITK_ok;
	tag_t ref_object = NULLTAG,
		datasettype = NULLTAG,
		new_ds = NULLTAG,
		tool = NULLTAG,
		folder_tag = NULLTAG;
	AE_reference_type_t reference_type;
	tag_t new_file_tag = NULLTAG;
	IMF_file_t file_descriptor;		
	char new_ds_name[WSO_name_size_c + 1] = {'\0'};
	char *new_file_name;
	tag_t relation = NULLTAG;
	tag_t   spec_relation = NULLTAG;


	new_file_name = USER_new_file_name(new_ds_name, ref_name, ext, 0);

	//ifail =IMF_import_file(fullfilename, new_file_name, SS_BINARY, &new_file_tag, &file_descriptor);
	//SS_TEXT
	ifail =IMF_import_file(fullfilename, new_file_name, SS_TEXT, &new_file_tag, &file_descriptor);
	if(ifail != 0)
	{
		return 1;
	}
	remove(fullfilename);
	ITKCALL(IMF_set_original_file_name(new_file_tag, filename));
	ITKCALL(IMF_close_file(file_descriptor));
	ITKCALL(AOM_save(new_file_tag));
	ITKCALL(AOM_unlock(new_file_tag));

	ITKCALL( AE_find_datasettype (ds_type, &datasettype));
	POM_AM__set_application_bypass(TRUE);
	ITKCALL( AE_create_dataset_with_id(datasettype, ds_name,"", "", "1", &new_ds ));
	//ITKCALL( AE_create_dataset ( datasettype, ds_name, "", &new_ds));
	printf("create dataset success\n");

	if( FindTargetTool( datasettype, ref_name, &tool ) != ITK_ok ||	tool == NULLTAG )
	{
		ITKCALL( AE_ask_datasettype_def_tool( datasettype, &tool ) );
	}
	ITKCALL( AE_set_dataset_tool( new_ds, tool ) );
	ITKCALL( AE_set_dataset_format( new_ds, TEXT_REF ) );
	ITKCALL( AE_save_myself( new_ds) );
	ITKCALL (AE_add_dataset_named_ref( new_ds, ref_name,AE_PART_OF, new_file_tag ));
	ITKCALL( AOM_save( new_ds ) );
	ITKCALL( AOM_unlock( new_ds ) );
	*dataset = new_ds;
	
	return ITK_ok;
}

int yfjc_reject_overdue_timesheet( EPM_action_message_t msg )
	{
		int ifail = ITK_ok;
		//handler 参数
		char pref_name[128] = {'\0'};
		int pre_cnt = 0;
		char ** pre_values = NULL;
		logical is_debug = FALSE;
		//para
		int arg_cnt = 0;
		char * tmp_arg = NULL;
		char * para_name = NULL;
		char * para_value = NULL;
		//root_task
		tag_t root_task = NULLTAG;
		int att_cnt = 0;
		tag_t * atts = NULL;
		tag_t tmp_att = NULLTAG;
		tag_t tmp_att_object_type = NULLTAG;
		char tmp_att_class_name[ TCTYPE_class_name_size_c+1 ] = {'\0'};

		char * time_sheet_name = NULL;
		//now date
		date_t current_date ;
		date_t time_sheet_date ;
		//截止日期
		int day = 0;

		//是否过期标志位
		logical over_due = FALSE;

		//error
		map< string , int > error_map;
		char tmp_error[256] = {'\0'};

		//for
		int i = 0;
		int j = 0;

		//txt
		char file_full_name[256] = {'\0'};
		char ref_file_name[64] = {'\0'};
		FILE *fp = NULL;
		tag_t txt_dataset = NULLTAG;

		//mile
		tag_t mile_reciver = NULLTAG;

		vector<tag_t> over_due_vec;
		tag_t * remove_tags = NULL;
		int remove_cnt = 0;

		//dataset test
		//tag_t current_groupmember = NULLTAG;
		//tag_t user = NULLTAG;
		//tag_t home_folder = NULLTAG;

		//send mail
		tag_t envelope = NULLTAG;
		

		ECHO("---------------------yfjc_reject_overdue_timesheet started---------------------\n");

		arg_cnt = TC_number_of_arguments(msg.arguments);
		for( i = 0; i < arg_cnt ; i ++)
		{
			tmp_arg =  TC_next_argument(msg.arguments);
			ECHO("--->		tmp_arg = %s\n" , tmp_arg );
			ITKCALL ( ITK_ask_argument_named_value( tmp_arg , &para_name , &para_value));
			if(!tc_strcasecmp( para_name , "pref_name"))
			{
				tc_strcpy( pref_name , para_value );
			}else if(!tc_strcasecmp( para_name , "debug" ))
			{
				if(!tc_strcasecmp( para_value , "true" ))
				{
					is_debug = TRUE;
					ECHO("		Debug ....\n");
				}
			}
			if(para_name)
			{
				MEM_free(para_name);
				para_name = NULL;
			}
			if(para_value)
			{
				MEM_free(para_value);
				para_value = NULL;
			}
		}
		if(!tc_strcmp(pref_name , ""))
		{
			sprintf( tmp_error , "未配置参数pref_name，请配置该参数！");
			string tmp_err ;
			tmp_err.assign(tmp_err);
			error_map.insert(pair<string , int>( tmp_error , ERROR_HANDLER_PRE_NOT_FOUND ));

		}else{
			ITKCALL(PREF_ask_char_values( pref_name , &pre_cnt , &pre_values ));
			if(is_debug)
				ECHO("--->	pre_cnt = %d\n" , pre_cnt );
			
			if( (pre_values != NULL))
			{ 
				day = atoi(pre_values[0]);
				if(is_debug)
					ECHO("--->	day = %d\n" , day);
				if(day == 0)
				{
					sprintf(tmp_error ,"%s" , pref_name );
					string tmp_err;
					tmp_err.assign(tmp_error);
					error_map.insert(pair<string , int>( tmp_err , ERROR_PREFERENCE_NOT_FOUND));
				}else{
					getNowTime( &current_date );
					if(is_debug)
						ECHO("---->		current_date : %04d-%02d-%02d %02d:%02d:%02d\n",current_date.year,current_date.month+1,current_date.day,current_date.hour,current_date.minute,current_date.second);
					ITKCALL(EPM_ask_root_task( msg.task , &root_task ));

					ITKCALL(EPM_ask_attachments( root_task , EPM_target_attachment , &att_cnt , &atts ));
					
					
					for( i = 0 ; i < att_cnt ; i ++)
					{
						over_due = FALSE;
						tmp_att = atts[i];
						ITKCALL( TCTYPE_ask_object_type( tmp_att , &tmp_att_object_type ));
						ITKCALL( TCTYPE_ask_class_name( tmp_att_object_type , tmp_att_class_name ));
						if(is_debug)
						{
							ECHO("--->		atts[%d].object_type = %s\n" , i , tmp_att_class_name );
						}
						if(!tc_strcmp( tmp_att_class_name , "TimeSheetEntry"))//如果是TimeSheetEntry对象
						{
							//date date_t
							ITKCALL( AOM_ask_value_string( tmp_att , "object_name" , &time_sheet_name ));
							ITKCALL( AOM_ask_value_date( tmp_att , "date" , &time_sheet_date ) );
							if(is_debug)
								ECHO("===>		TimeSheetEntry:	%s的日期为：%4d-%2d-%2d %2d:%2d:%2d\n" , time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.day );
							if((current_date.day < day)||(current_date.day == day ))//只能提交上一个月的
							{
								if(is_debug)
									ECHO("---->		current_date.day < day || current_date == day \n");
								if(current_date.month == 0)//如果为新年
								{
									if(is_debug)
										ECHO("--->	current_date.month == 0 \n");
									if(time_sheet_date.year < current_date.year-1)
									{
										over_due = TRUE;
										if(is_debug)
											ECHO("time_sheet_date.year < current_date.year-1\n");
										sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
										if(is_debug)
											ECHO("--->	tmp_error = %s\n",tmp_error);
										string tmp_err;
										tmp_err.assign(tmp_error);
										error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
									}
									else if((time_sheet_date.year == current_date.year-1)&&(time_sheet_date.month < 11))
									{
										over_due = TRUE;
										if(is_debug)
											ECHO("(time_sheet_date.year == current_date.year-1)&&(time_sheet_date.month < 11)\n");
										//time_sheet_date.name 日期为time_sheet_date.date ，当前月份%d的结账日已经过去，视为过期
										sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
										if(is_debug)
											ECHO("--->	tmp_error = %s\n",tmp_error);
										string tmp_err;
										tmp_err.assign(tmp_error);
										error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
									}
								}else{//如果不为新年
									if(time_sheet_date.year < current_date.year)
									{
										over_due = TRUE;
										if(is_debug)
											ECHO("time_sheet_date.year < current_date.year\n");
										sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
										if(is_debug)
											ECHO("--->	tmp_error = %s\n",tmp_error);
										string tmp_err;
										tmp_err.assign(tmp_error);
										error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
									}else if((time_sheet_date.year == current_date.year)&&(time_sheet_date.month<current_date.month-1)){
										over_due = TRUE;
										if(is_debug)
											ECHO("(time_sheet_date.year == current_date.year)&&(time_sheet_date.month<current_date.month-1)\n");
										sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
										if(is_debug)
											ECHO("--->	tmp_error = %s\n",tmp_error);
										string tmp_err;
										tmp_err.assign(tmp_error);
										error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
									}
								}
							}else{//只能提交当月的
								if(is_debug)
									ECHO("--->	current_date.day > day\n");
								if(time_sheet_date.year < current_date.year)
								{
									over_due = TRUE;
									if(is_debug)
										ECHO("time_sheet_date.year < current_date.year\n");
									sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
									if(is_debug)
										ECHO("--->	tmp_error = %s\n",tmp_error);
									string tmp_err;
									tmp_err.assign(tmp_error);
									error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
								}else if((time_sheet_date.year == current_date.year)&&(time_sheet_date.month < current_date.month ))
								{
									over_due = TRUE;
									if(is_debug)
										ECHO("(time_sheet_date.year == current_date.year)&&(time_sheet_date.month < current_date.month )\n");
									sprintf(tmp_error , "%s \t的日期为：%04d-%02d-%02d %02d:%02d:%02d,当前月份%d的结账日为%d，视为过期！",time_sheet_name , time_sheet_date.year , time_sheet_date.month+1 , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second ,current_date.month +1 , day );
									if(is_debug)
										ECHO("--->	tmp_error = %s\n",tmp_error);
									string tmp_err;
									tmp_err.assign(tmp_error);
									error_map.insert(pair<string , int>( tmp_err , ERROR_TIME_SHEET_ENTRY_PAST_DUE));
								}
							}
					
							if(over_due)
							{
								if(is_debug)
									ECHO("---->		从流程中删除%s\n",time_sheet_name);
								over_due_vec.push_back(tmp_att);
							}
							if(time_sheet_name)
							{
								MEM_free(time_sheet_name);
								time_sheet_name = NULL;
							}
						}
						

					}
					if(atts)
					{
						MEM_free( atts );
						atts = NULL;
					}
					
					
				}
				
				
			}else{
				//当前首选项pref_name没有设置或设置存在问题（当前首选项的值必须为非零整数），请联系系统管理员！
				sprintf(tmp_error ,"%s" , pref_name );
				if(is_debug)
					ECHO("--->	tmp_error = %s\n",tmp_error);
				string tmp_err;
				tmp_err.assign(tmp_error);
				error_map.insert(pair<string , int>( tmp_err , ERROR_PREFERENCE_NOT_FOUND));
			}
 
		}
		
    if(over_due_vec.size()>0)
	{
		remove_cnt = over_due_vec.size();
		remove_tags = (tag_t *)MEM_alloc(sizeof(tag_t)*remove_cnt);
		for(i = 0; i < remove_cnt; i ++)
		{
			remove_tags[i] = over_due_vec[i];
		}
		POM_AM__set_application_bypass(TRUE);
		ITKCALL(AOM_lock( root_task ));
		ITKCALL(EPM_remove_attachments( root_task , remove_cnt , remove_tags ));
		ITKCALL(EMH_clear_errors());
		ITKCALL(AOM_unlock( root_task ));
		POM_AM__set_application_bypass(FALSE);
    }
		
		
		if(is_debug)
			ECHO("---->error_map.size() = %d\n" , error_map.size() );
		if(error_map.size() > 0)
		{
			ifail = CR_error_in_handler;
			ITKCALL(AOM_ask_value_tag(root_task , "owning_user" , &mile_reciver));
			
			sprintf(file_full_name , "%s/%s%04d%02d%02d%02d%02d%02d.txt",getenv("TC_SHARED_MEMORY_DIR"),TimeSheetOverDueDatasetName,current_date.year,current_date.month+1,current_date.day,current_date.hour,current_date.minute,current_date.second);
			sprintf(ref_file_name,"%s.txt",TimeSheetOverDueDatasetName);
			if(is_debug)
			{
				ECHO("file_name = %s\n",file_full_name);
				ECHO("ref_file_name = %s\n",ref_file_name);
			}
			fp = fopen(file_full_name,"w");
			map<string , int >::iterator error_map_ite;
			for( error_map_ite = error_map.begin() ; error_map_ite != error_map.end();error_map_ite ++)
			{
				fprintf(fp,"%s\n",error_map_ite->first.c_str());
			}
			fflush(fp);
			fclose(fp);
			//创建数据集
			ifail = create_dataset("Text","Text",TimeSheetOverDueDatasetName,"txt", file_full_name, ref_file_name, &txt_dataset);
			if(ifail != ITK_ok)
			{
				EMH_store_error_s1( EMH_severity_error , ERROR_TIME_SHEET_ENTRY_PAST_DUE ,TimeSheetOverDueDatasetName);
			}else{
				ITKCALL(MAIL_create_envelope( TimeSheetOverDueMailName , TimeSheetOverDueMailContent ,&envelope));
				ITKCALL(MAIL_add_envelope_receiver(envelope,mile_reciver));
				ITKCALL(FL_insert(envelope , txt_dataset , 0));
				ITKCALL(MAIL_send_envelope(envelope));
				
			}
			
			for( error_map_ite = error_map.begin() ; error_map_ite != error_map.end();error_map_ite ++)
			{
				if(is_debug)
					ECHO("---> %d :%s\n" , error_map_ite->second , error_map_ite->first.c_str() );
				EMH_store_error_s1( EMH_severity_error, error_map_ite->second , error_map_ite->first.c_str() );
				
			}
			
		}
		ECHO("---------------------yfjc_reject_overdue_timesheet ended---------------------\n");
		return ifail;
	}


#ifdef __cplusplus
}
#endif
