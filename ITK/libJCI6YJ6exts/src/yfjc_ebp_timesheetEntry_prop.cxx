/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   :yfjc_ebp_timesheetentry_prop.cxx
    Module : user_exits

    SP-CUS-007 工时所属项目阶段

	计算 TimeSheetEntry 对象 上 的动态属性 ： jci6_ownPhase

	import programInfo

	首选项 ： YFJC_gate_phase_mapping_list
		的 值 格式 为 ： prop_value1 = gatetype1,gatetype2

============================================================================================================
DATE           Name             Description of Change
14_ARG_2014    mengyawei          creation
$HISTORY$

============================================================================================================*/
#include "yfjc_ebp_head.h"
#include "yfjc_time.h"
#include "yfjc_ebp_error.h"

#include <stdio.h>
#include <stdlib.h>
#include <vector>
#include <map>
#include <string>

using namespace std;

#define GATE_PHASE_PRE		"YFJC_gate_phase_mapping_list"
#define GATE_PHASE_QRY		"YFJC_SearchGateByTSE"


int getTimeSheetEntryOwnPhase(METHOD_message_t* msg, va_list args)
{
	int ifail = ITK_ok;

	tag_t time_sheet_entry = NULLTAG;
	tag_t prop_tag = NULLTAG;

	char ** prop_value = NULL;
	
	//pre
	int pre_cnt = 0;
	char ** pre_values = NULL;

	int par_cnt = 0;
	char ** par_values = NULL;
	
	map<string , string> gate_map ;

	//qry
	tag_t qry_tag = NULLTAG;
	int entry_cnt = 0;
	char ** entries = NULL;
	char ** values = NULL;

	char ** qry_entry = NULL;
	char ** qry_value = NULL;

	int res_cnt = NULL;
	tag_t * results = NULL;

	//冒泡法
	tag_t pre_task = NULLTAG;
	tag_t cur_task = NULLTAG;
	tag_t tmp_task = NULLTAG;
	date_t pre_date ;
	date_t cur_date ;

	//
	tag_t start_task = NULLTAG;
	tag_t end_task = NULLTAG;
	char * start_task_type = NULL;
	char * end_task_type = NULL;

	char gate_map_key[256] = {'\0'};
	
	date_t time_sheet_date ;

	//schedule_task
	tag_t schedule_task = NULLTAG;
	tag_t schedule = NULLTAG;
	char * schedule_name = NULL;

	//for
	int i = 0;
	int j = 0;

	fprintf(stdout,"-------------getTimeSheetEntryOwnPhase started--------------\n");

	TC_write_syslog("-------------getTimeSheetEntryOwnPhase started--------------\n");
	time_sheet_entry = msg->object_tag;
	prop_tag = va_arg(args,tag_t );
	//ITKCALL(PROP_ask_value_type(prop_tag, &valtype,&valtype_n));
	prop_value = va_arg( args, char ** ); 
	
	ITKCALL(AOM_ask_value_tag(time_sheet_entry , "scheduletask_tag" , &schedule_task));
	if(schedule_task == NULLTAG)
	{
		fprintf(stdout , "timesheetEntry . jci6_Schedule == NULL!\n");
		TC_write_syslog("timesheetEntry . jci6_Schedule == NULL!\n");
		return 0;
	}
	ITKCALL(AOM_ask_value_tag(schedule_task , "schedule_tag" , &schedule));
	ITKCALL(AOM_ask_value_string(schedule , "object_name" , &schedule_name ));
	fprintf(stdout , "schedule . object_name = %s\n",schedule_name);
	TC_write_syslog("schedule . object_name = %s\n",schedule_name);
	

	ITKCALL(PREF_ask_char_values( GATE_PHASE_PRE , &pre_cnt , &pre_values ));
	if(pre_cnt >0)
	{
		TC_write_syslog("首选项  %s :\n" , GATE_PHASE_PRE );
		for( i = 0; i < pre_cnt ; i ++)
		{
			TC_write_syslog("	%s\n",pre_values[i]);
			ITKCALL(EPM__parse_string( pre_values[i] , "=" , &par_cnt , &par_values ));
			if(par_cnt == 2)
			{
				string key_str;
				key_str.assign(par_values[1]);
				string value_str;
				value_str.assign(par_values[0]);
				TC_write_syslog("	key_str.c_str() = %s		value_str.c_str() = %s \n" , key_str.c_str() ,value_str.c_str() );
				gate_map.insert(pair<string , string>( key_str , value_str ));
			}
			if(par_values)
			{
				MEM_free(par_values);
				par_values = NULL;
			}
		}

		ITKCALL(QRY_find( GATE_PHASE_QRY , &qry_tag ));
		if(qry_tag==NULLTAG)
		{
			fprintf(stdout , "qry : %s not exits \n",GATE_PHASE_QRY);
			TC_write_syslog("qry : %s not exits \n",GATE_PHASE_QRY);
			return 0;
		}
		ITKCALL(QRY_find_user_entries( qry_tag , &entry_cnt , &entries , &values ));
		qry_entry = (char **) MEM_alloc(sizeof(char *)*entry_cnt);
		qry_value = (char **) MEM_alloc(sizeof(char *)*entry_cnt);
		for( i = 0; i < entry_cnt ; i ++)
		{
			qry_entry[i] = (char *) MEM_alloc(256);
			qry_value[i] = (char *) MEM_alloc(256);
		}
		for( i = 0; i < entry_cnt ; i ++)
		{
			
			
			if(!tc_strcmp(entries[i],"Name"))
			{
				tc_strcpy( qry_entry[i] , entries[i]);
				tc_strcpy( qry_value[i] , schedule_name);
			}
			else
			{
				tc_strcpy( qry_entry[i] , entries[i] );
				tc_strcpy( qry_value[i] , values[i] );
			}
			TC_write_syslog("-->	qry_entry[%d] = %s	qry_value[%d] = %s\n" , i , qry_entry[i] , i , qry_value[i] );
			fprintf(stdout , "-->	qry_entry[%d] = %s	qry_value[%d] = %s\n" , i , qry_entry[i] , i , qry_value[i] );
			
		}
		if(entries)
		{
			MEM_free(entries);
			entries = NULL;
		}
		if(values)
		{
			MEM_free(values);
			values = NULL;
		}
		ITKCALL(QRY_execute( qry_tag , entry_cnt , qry_entry , qry_value , &res_cnt , &results));
		TC_write_syslog("---->		res_cnt = %d\n" , res_cnt );
		fprintf(stdout , "----->		res_cnt = %d\n" , res_cnt );
		//根据 查询到的 门 对象的 finish_date做冒泡排序
		for(i = 0; i < res_cnt-1 ; i ++)
		{
			for(j = i+1; j < res_cnt ; j ++)
			{
				pre_task = results[i];
				ITKCALL(AOM_ask_value_date( pre_task , "finish_date" , &pre_date ));
				cur_task = results[j];
				ITKCALL(AOM_ask_value_date( cur_task , "finish_date" , &cur_date ));
				TC_write_syslog("results[%d].pre_date = %04d-%02d-%02d %02d:%02d:%02d\n",i,pre_date.year,pre_date.month,pre_date.day,pre_date.hour,pre_date.minute,pre_date.second);
				TC_write_syslog("results[%d].cur_date = %04d-%02d-%02d %02d:%02d:%02d\n",j,cur_date.year,cur_date.month,cur_date.day,cur_date.hour,cur_date.minute,cur_date.second);
				if((pre_date.year>cur_date.year)
					||((pre_date.year==cur_date.year)&&(pre_date.month>cur_date.month))
					||((pre_date.year==cur_date.year)&&(pre_date.month==cur_date.month)&&(pre_date.day>cur_date.day))
					||((pre_date.year==cur_date.year)&&(pre_date.month==cur_date.month)&&(pre_date.day==cur_date.day)
						&&(pre_date.hour>cur_date.hour)))

				{
					TC_write_syslog("		Change\n");
					results[i] = results[j];
					results[j] = pre_task;
				}
			}
		}
		for(i = 0; i < res_cnt ;  i ++)
		{
			pre_task = results[i];
			ITKCALL(AOM_ask_value_date(pre_task , "finish_date" , &pre_date ));
			TC_write_syslog("start_task = results[%d]	finish_date = %04d-%02d-%02d %02d:%02d:%02d\n" , i , pre_date.year , pre_date.month , pre_date.day , pre_date.hour , pre_date.minute , pre_date.second );
		}
		//当前 timesheetentry的date属性与冒泡排序的 结果集 进行 比较
		ITKCALL(AOM_ask_value_date(time_sheet_entry , "date" , &time_sheet_date ));
		TC_write_syslog("TimeSheetEntry  date = %04d-%02d-%02d %02d:%02d:%02d\n",time_sheet_date.year , time_sheet_date.month , time_sheet_date.day , time_sheet_date.hour , time_sheet_date.minute , time_sheet_date.second );
		for( i = 0; i < res_cnt-1; i ++ )
		{
			pre_task = results[i];
			cur_task = results[i+1];
			ITKCALL(AOM_ask_value_date(pre_task , "finish_date" , &pre_date ));
			ITKCALL(AOM_ask_value_date(cur_task , "finish_date" , &cur_date ));
			TC_write_syslog("start_task = results[%d]	finish_date = %04d-%02d-%02d %02d:%02d:%02d\n" , i , pre_date.year , pre_date.month , pre_date.day , pre_date.hour , pre_date.minute , pre_date.second );
			TC_write_syslog("end_task = results[%d]		finish_date = %04d-%02d-%02d %02d:%02d:%02d\n" , i+1 , cur_date.year , cur_date.month , cur_date.day , cur_date.hour , cur_date.minute , cur_date.second );
			if((time_sheet_date.year > pre_date.year)
				||((time_sheet_date.year == pre_date.year)&&(time_sheet_date.month > pre_date.month))
				||((time_sheet_date.year == pre_date.year)&&(time_sheet_date.month == pre_date.month)&&(time_sheet_date.day > pre_date.day))
				||((time_sheet_date.year == pre_date.year)&&(time_sheet_date.month == pre_date.month)&&(time_sheet_date.day == pre_date.day)
					&&(time_sheet_date.hour > pre_date.hour)))
			{
				fprintf(stdout , "----------111-----------\n");
				TC_write_syslog("----------111-----------\n");
				if((time_sheet_date.year < cur_date.year)
					||((time_sheet_date.year == cur_date.year)&&(time_sheet_date.month < cur_date.month))
					||((time_sheet_date.year == cur_date.year)&&(time_sheet_date.month == cur_date.month)&&(time_sheet_date.day < cur_date.day))
					||((time_sheet_date.year == cur_date.year)&&(time_sheet_date.month == cur_date.month)&&(time_sheet_date.day == cur_date.day)
						&&(time_sheet_date.hour < cur_date.hour )))
				{
					fprintf(stdout , "------------222-----------\n");
					TC_write_syslog("------------222-----------\n");
					start_task = pre_task;
					end_task = cur_task;
					break;
				}
			}

		}
		//判断所处 的 门 间隙，设置 属性值
		if((start_task!=NULLTAG)&&(end_task!=NULLTAG))
		{
			ITKCALL(AOM_ask_value_string(start_task , "jci6_TaskType" , &start_task_type ));
			ITKCALL(AOM_ask_value_string(end_task , "jci6_TaskType" , &end_task_type));
			fprintf(stdout , "---->		strat_task_type = %s\n",start_task_type);
			fprintf(stdout , "---->		end_task_type = %s\n",end_task_type);
			TC_write_syslog("---->		strat_task_type = %s\n",start_task_type);
			TC_write_syslog("---->		end_task_type = %s\n",end_task_type);
			sprintf(gate_map_key , "%s,%s",start_task_type , end_task_type);
			TC_write_syslog("---->		gate_map_key = %s\n",gate_map_key);
			fprintf(stdout , "---->		gate_map_key = %s\n",gate_map_key);
			map<string,string>::iterator gate_map_ite;
			gate_map_ite = gate_map.find(gate_map_key);
			if(gate_map_ite!=gate_map.end())
			{
				TC_write_syslog("---->	%s的值为： %s\n",gate_map_key ,gate_map_ite->second.c_str() );
				fprintf(stdout , "---->	%s的值为： %s\n",gate_map_key ,gate_map_ite->second.c_str());
				*prop_value=MEM_string_copy(gate_map_ite->second.c_str());

			}
			else
			{
				TC_write_syslog("---->		gate_map_ite == gate_map.end()\n");
				TC_write_syslog("未在首选项 %s 中定义 %s 对应的取值\n" , GATE_PHASE_PRE , gate_map_key );
				fprintf(stdout , "未在首选项 %s 中定义 %s 对应的取值\n" , GATE_PHASE_PRE , gate_map_key );
			}
			
			if(start_task_type)
			{
				MEM_free(start_task_type);
				start_task_type = NULL;
			}
			if(end_task_type)
			{
				MEM_free(end_task_type);
				end_task_type = NULL;
			}
		}
		if(results)
		{
			MEM_free(results);
			results = NULL;
		}
		

	}
	else
	{
		TC_write_syslog("首选项 ： %s 未定义！\n" , GATE_PHASE_PRE );
	}
	if(pre_values)
	{
		MEM_free(pre_values);
		pre_values = NULL;
	}
	if(schedule_name )
	{
		MEM_free(schedule_name );
		schedule_name  = NULL;
	}

	TC_write_syslog("--------------getTimeSheetEntryOwnPhase ended---------------\n");
	return ifail;
}
