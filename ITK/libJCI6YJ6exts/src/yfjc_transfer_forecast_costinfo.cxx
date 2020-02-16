/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   : yfjc_transfer_forecast_costinfo.cxx
    Module : user_exits

    SP-CUS-001 检查审批时限及转化Forecast

	根据ExtSupportPlan新建CostInfo对象

	Action Handler
	-debug			true , false	//write log

============================================================================================================
DATE           Name             Description of Change
1_ARG_2014    mengyawei          creation
$HISTORY$

============================================================================================================*/
#include <tc/tc.h>
#include <epm/epm.h>
#include <tccore/aom.h>
#include <tccore/aom_prop.h>
#include <tccore/tctype.h>
#include <tccore/workspaceobject.h>
#include <tc/envelope.h>
#include <tc/folder.h>
#include <lov/lov.h>
#include <qry/qry.h>
#include <tc/emh.h>
#include <epm/cr.h>
#include <map>
#include <vector>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <unidefs.h>
#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
#include "yfjc_time.h"
using namespace std;



#define ExtSupportPlanForm		"JCI6_ExtSupPlan"		//用人计划表单
#define ExtSupport				"JCI6_ExtSupport"		//用人计划
#define CostInfo_QRY			"YFJC_SearchCostInfo"	//
#define CostInfo_QRYSN			"YFJC_SearchCostInfobySN"
#define NormalHoursLOV			"JCI6_NormalHours"		//LOV
#define External_object_link	"IMAN_external_object_link"
#define ProgramInfo				"JCI6_ProgramInfo"
#define RateLeval_QRY			"YFJC_Search_Discipline"
#define RateLevelName				"ExtSupporter"

#define MAIL_NAME				"超结算日需删除"
#define MAIL_CONTENT			"已经超过结账日，请删除该流程以及用人计划表单."


extern "C" int POM_AM__set_application_bypass(logical bypass);

tag_t get_pre_task_resp_party( logical is_debug , tag_t current_task );
int send_mail(logical is_debug , tag_t user , tag_t target);
int operate_support( logical is_debug , tag_t support , tag_t rate_level , map<string , int> & error_map );
int compute_cost_info( logical is_debug , tag_t support , tag_t cost_info , map<string , int> & error_map );
tag_t create_cost_info(logical is_debug , tag_t support , char * unit_value , tag_t rate_level , map<string , int> & error_map);

int  compute_cost_info_percent(logical is_debug ,double &output , char * lov_value, double percent , char * unit ,tag_t user , map<string , int> & error_map );

int yfjc_transfer_forecast_costinfo(EPM_action_message_t msg)
{
	int ifail = ITK_ok;

	//handler  argument
	int arg_cnt = 0;
	char * arg_str = NULL;
	char * para_name = NULL;
	char * para_value = NULL;

	//char account_day[32] = {'\0'};		//-account_day
	//int ccnt_day = 0;

	//flag
	logical is_debug = FALSE;		//-debug //是否写日志

	//target attachment
	tag_t root_task = NULLTAG;

	int att_cnt = 0;
	tag_t * atts = NULL;

	tag_t tmp_att = NULLTAG;
	//tag_t tmp_att_object_type = NULLTAG;
	//char tmp_att_class_name[TCTYPE_class_name_size_c+1] = {'\0'};
	char * tmp_att_class_name = NULL;
	char tmp_att_name[WSO_name_size_c+1] = {'\0'};
	int tmp_att_sta_cnt = 0;
	tag_t * tmp_att_stautus = NULL;

	vector<tag_t> form_vec;		//用人计划表单
	//ext support plan
	tag_t tmp_sup_plan = NULLTAG;
	//ext support
	tag_t tmp_support = NULLTAG;
	int support_cnt = 0;
	tag_t * supports = NULL;

	date_t nowDate ;
	int tmp_year = 0;
	int tmp_month = 0;

	//for
	int i = 0;
	int j = 0;

	//qry rate_level 
	tag_t qry = NULLTAG;

	char ** entry_names = NULL;
	char ** values = NULL;
	int num_clauses = 0;

	char ** qry_entry = NULL;
	char ** qry_values = NULL;

	int res_cnt = 0;
	tag_t * results = NULL;

	tag_t rate_level = NULLTAG;

	//errors
	char tmp_error[128] = {'\0'};
	map<string , int> error_map;

	tag_t mail_reciver = NULLTAG;

		
		
	TC_write_syslog("-----------------------yfjc_transfer_forecast_costinfo started----------------------\n");

	arg_cnt = TC_number_of_arguments(msg.arguments);
	for( i  = 0; i < arg_cnt ; i ++)
	{
		arg_str = TC_next_argument(msg.arguments);

		ITKCALL(ITK_ask_argument_named_value( arg_str , &para_name , &para_value ));
		//if(!tc_strcasecmp(para_name,"account_day"))
		//{
		//	if(tc_strlen(para_value)==2)//account_day的参数必须为2位
		//	{
		//		tc_strcpy(account_day,para_value);
		//	}
		//}
		//else
			if(!tc_strcasecmp(para_name,"debug"))
		{
			if(!tc_strcasecmp(para_value,"true"))
			{
				is_debug = TRUE;
				TC_write_syslog("	Debug ...\n");
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

	/*if(tc_strlen(account_day)==0)
	{
		sprintf(tmp_error , "未配置参数account_day或不是两位数，请配置该参数！");
		string err_str;
		err_str.assign(tmp_error);
		if(is_debug)
			TC_write_syslog("%s\n",err_str.c_str());
		error_map.insert( pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
	}
	else*/
	{

		ITKCALL(QRY_find( RateLeval_QRY , &qry ));
		if(qry != NULLTAG)
		{
			fprintf(stdout , "aaaaaaaaaaaaaaaaaaa\n");
			ITKCALL(QRY_find_user_entries( qry , &num_clauses , &entry_names ,&values));
			qry_entry = (char **)MEM_alloc(sizeof(char *)*num_clauses);
			qry_values = (char **)MEM_alloc(sizeof(char *)*num_clauses);
			for(i = 0; i < num_clauses ; i ++)
			{
				qry_entry[i] = (char *)MEM_alloc(sizeof(char)*128);
				qry_values[i] = (char *) MEM_alloc(sizeof(char)*128);
			}
			for(i = 0; i < num_clauses; i ++)
			{
				fprintf(stdout , "entry_names[%d] = %s\n",i , entry_names[i]);
				if(tc_strcmp(entry_names[i],"Discipline_name"))
				{
					tc_strcpy(qry_entry[i],entry_names[i]);
					tc_strcpy(qry_values[i],RateLevelName);
				}//else{}
			}
			ITKCALL(QRY_execute(qry , num_clauses , qry_entry , qry_values , &res_cnt , &results ));
			if(res_cnt > 0)
			{
				fprintf(stdout , "bbbbbbbbbbbb\n");
				rate_level = results[0];
			}
			else
			{
				if(is_debug)
					TC_write_syslog("---------->	查询：%s 未找到 ExtSupporter\n",RateLeval_QRY);
			}
			if(results)
			{
				MEM_free(results);
				results = NULL;
			}
			for(i = 0; i < num_clauses ; i ++)
			{
				MEM_free(qry_entry[i]);
				MEM_free(qry_values[i]);
			}
			if(qry_entry)
			{
				MEM_free(qry_entry);
				qry_entry = NULL;
			}
			if(qry_values)
			{
				MEM_free(qry_values);
				qry_values = NULL;
			}
		}
		else
		{
			sprintf(tmp_error , "查询：%s 没有定义，请定义该查询！",RateLeval_QRY);
			string err_str;
			err_str.assign(tmp_error);
			if(is_debug)
				TC_write_syslog("%s\n",err_str.c_str());
			error_map.insert( pair< string , int >( err_str , ERROR_QRY_NOT_DEFINE ));
		}
			
			
		//ccnt_day = atoi(account_day);
		//if(ccnt_day!=0)
		{
			ITKCALL(EPM_ask_root_task(msg.task , &root_task));
			ITKCALL(EPM_ask_attachments(root_task , EPM_target_attachment , &att_cnt , &atts ));

			//getNowTime( &nowDate );

			for( i = 0; i < att_cnt ; i ++)
			{
				tmp_att = atts[i] ;
					
				ITKCALL(AOM_ask_value_string( tmp_att , "object_type" , &tmp_att_class_name));
				if(is_debug)
				{
					TC_write_syslog("---->		att[%d].object_type = %s \n",i , tmp_att_class_name );
					//fprintf(stdout,"---->		att[%d].object_type = %s \n",i , tmp_att_class_name );
				}	
				if(!tc_strcmp( tmp_att_class_name ,ExtSupportPlanForm ))
				{

					ITKCALL(WSOM_ask_name( tmp_att, tmp_att_name ));
					ITKCALL(WSOM_ask_release_status_list( tmp_att , &tmp_att_sta_cnt , &tmp_att_stautus )) ;

					if(tmp_att_stautus)
					{
						MEM_free(tmp_att_stautus);
						tmp_att_stautus = NULL;
					}

					if(tmp_att_sta_cnt > 0)
					{
						string err_str;
						//sprintf(tmp_error , "流程目标对象中的用人计划表单: %s 已经发布！",tmp_att_name);
						sprintf(tmp_error,"流程目标对象中的用人计划表单:%s 已经发布!",tmp_att_name);

						err_str.assign(tmp_error);

						if(is_debug)
							TC_write_syslog("%s\n",err_str.c_str());
						error_map.insert( pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
					}
					else
					{
						form_vec.push_back(tmp_att);
						if(is_debug)
							TC_write_syslog("流程目标对象中的用人计划表单: %s 未发布！\n",tmp_att_name);
					}
				}
				if(tmp_att_class_name)
				{
					MEM_free(tmp_att_class_name);
					tmp_att_class_name = NULL;
				}
			}

			if(atts)
			{
				MEM_free(atts);
				atts = NULL;
			}
			if((form_vec.size())==0&&(error_map.size()==0))
			{
				sprintf(tmp_error , "流程目标对象中没有用人计划表单！");
				string err_str;
				err_str.assign(tmp_error);
				if(is_debug)
					TC_write_syslog("%s\n",err_str.c_str());
				error_map.insert( pair<string , int>(err_str , ERROR_HAS_NO_FORM));
			}
			else 
				if(error_map.size()==0&&(form_vec.size()>0))
			{ 
				// mail_reciver = get_pre_task_resp_party(is_debug , msg.task );
				// send_mail(is_debug , mail_reciver , root_task );
				fprintf(stdout,"form_vec.size() = %d\n",form_vec.size());
					for(i = 0; i < form_vec.size() ; i ++)
				{
					tmp_sup_plan = form_vec[i];

					ITKCALL(WSOM_ask_name( tmp_sup_plan , tmp_att_name ));

					ITKCALL(AOM_ask_value_tags( tmp_sup_plan , "jci6_ExtSupportArray" , &support_cnt , &supports));

					if(is_debug)
					{
						TC_write_syslog("用人计划表单: %s 下有%d个 用人计划\n" , tmp_att_name , support_cnt );
						fprintf(stdout,"用人计划表单: %s 下有%d个 用人计划\n" , tmp_att_name , support_cnt );
					}
							
					for(j = 0; j < support_cnt; j ++)
					{
						tmp_support = supports[j];

						ITKCALL(AOM_ask_value_int(tmp_support , "jci6_Year" , &tmp_year ));
						ITKCALL(AOM_ask_value_int(tmp_support , "jci6_Month" , &tmp_month));
						if(is_debug)
						{
							TC_write_syslog("nowDate = %4d-%02d-%02d %02d:%02d:%02d\n",nowDate.year , nowDate.month , nowDate.day , nowDate.hour , nowDate.minute , nowDate.second );
							TC_write_syslog("-->	tmp_year = %d	tmp_month = %d\n",tmp_year , tmp_month);
						}


						operate_support(is_debug , tmp_support , rate_level , error_map );

						//if((nowDate.year < tmp_year)||((nowDate.year == tmp_year )&&((nowDate.month+1) < tmp_month)))//无论 在 何种 时候 ，下一个月 及以后的 都是 会处理的
						//{
						//	if(is_debug)
						//		TC_write_syslog("111111111\n");
						//	operate_support(is_debug , tmp_support , error_map );
						//}else {
						//	if(nowDate.day < ccnt_day)//如果 未 超过 结账日 
						//	{
						//		if(nowDate.month ==0 )//如果 为 1月
						//		{
						//			if((tmp_year+1==nowDate.year)&&(tmp_month == 12))//上一年的 12月份 
						//			{
						//				if(is_debug)
						//					TC_write_syslog("2222222\n");
						//				operate_support(is_debug , tmp_support , error_map );
						//			}
						//		}else{	//如果 不为 1月 
						//			if((tmp_year == nowDate.year )&&(nowDate.month+1 == tmp_month))
						//			{
						//				if(is_debug)
						//					TC_write_syslog("33333333\n");
						//				operate_support(is_debug , tmp_support , error_map );
						//			}
						//		}
						//	}
						//}
					}
							
					if(supports)
					{
						MEM_free(supports);
						supports = NULL;
					}
							
				}
			}
		}
		/*else
		{
			sprintf(tmp_error , "参数account_day的值必须为数字且不为0，请配置该参数！");
			string err_str;
			err_str.assign(tmp_error);
			if(is_debug)
				TC_write_syslog("%s\n",err_str.c_str());
			error_map.insert( pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		}
		*/
			
	}

	map<string , int>::iterator error_map_ite;
	for( error_map_ite = error_map.begin() ; error_map_ite != error_map.end(); error_map_ite ++)
	{
		ifail = CR_error_in_handler;
		if(is_debug)
			TC_write_syslog("--->		%d :	%s\n" , error_map_ite->second , error_map_ite->first.c_str() );
		EMH_store_error_s1( EMH_severity_error, error_map_ite->second , error_map_ite->first.c_str() );
	}
		

	TC_write_syslog("------------------------yfjc_transfer_forecast_costinfo ended-----------------------\n");

	return ifail;
}

//操作 support对象
int operate_support( logical is_debug , tag_t support , tag_t rate_level , map<string , int> & error_map )
{
	int ifail = ITK_ok;

	//error
	char tmp_err[256] = {'\0'};

	tag_t qry = NULLTAG;
	int num_clauses = 0;
	char ** entry_names = NULL;
	char ** values = NULL;

	char ** qry_entry = NULL;
	char ** qry_values = NULL;

	int num_found = 0;
	tag_t * results = NULL;
	tag_t tmp_cost_info = NULLTAG;

	int support_year = 0;
	//int support_month = 0;
	tag_t support_ownProxy = NULLTAG;
	tag_t support_division = NULLTAG;
	tag_t support_section = NULLTAG;

	char * user_id = NULL;
	char * division_name = NULL;
	char * section_name = NULL;

	int i = 0;

	if(is_debug)
		TC_write_syslog("------------------------operate_support started-----------------------\n");
		
	ITKCALL(QRY_find( CostInfo_QRY , &qry ));
	if(qry == NULLTAG)
	{
		sprintf(tmp_err , "查询：%s 没有定义，请定义该查询！",CostInfo_QRY);
		string err_str;
		err_str.assign(tmp_err);
		if(is_debug)
			TC_write_syslog("%s\n",err_str.c_str());
		error_map.insert( pair< string , int >( err_str , ERROR_QRY_NOT_DEFINE ));
	}
	else
	{

		ITKCALL(AOM_ask_value_int( support , "jci6_Year" , &support_year ));
		ITKCALL(AOM_ask_value_tag( support , "jci6_ownProxy", &support_ownProxy));
		ITKCALL(AOM_ask_value_tag( support , "jci6_Section" , &support_section));
		ITKCALL(AOM_ask_value_tag( support , "jci6_Division" , &support_division));

		if((support_year == 0)||(support_ownProxy == NULLTAG)||(support_division == NULLTAG))
		{
			if(is_debug)
			{
				TC_write_syslog("(support_year == 0)||(support_ownProxy == NULLTAG)||(support_division == NULLTAG)\n");
				TC_write_syslog("------------------------operate_support end-----------------------\n");
				fprintf(stdout,"(support_year == 0)||(support_ownProxy == NULLTAG)||(support_division == NULLTAG)\n");
			}
			return ITK_ok;
		}


		ITKCALL(AOM_ask_value_string( support_ownProxy , "user_id" , &user_id ));
			
		ITKCALL(SA_ask_group_display_name(support_division , &division_name));
		if(support_section != NULLTAG)
		{
			ITKCALL(SA_ask_group_display_name(support_section , &section_name));
			ITKCALL(QRY_find( CostInfo_QRY , &qry ));
		}
		else
		{
			section_name = (char *) MEM_alloc(sizeof(char)*64);
			tc_strcpy(section_name , "*");
			ITKCALL(QRY_find( CostInfo_QRYSN , &qry ));
		}

		if(is_debug)
		{
			TC_write_syslog(" year = %d , user_id = %s , division_name = %s , section_name = %s\n", support_year , user_id , division_name , section_name );
			fprintf(stdout," year = %d , user_id = %s , division_name = %s , section_name = %s\n", support_year , user_id , division_name , section_name );
		}

		//ITKCALL(QRY_describe_query( qry ,&num_clauses,&attr_names,&entry_names,&logical_ops,&math_ops,&values,&lov_tags,&attr_types)); 
		ITKCALL(QRY_find_user_entries( qry , &num_clauses , &entry_names ,&values));

		qry_entry = (char **)MEM_alloc(sizeof(char *)*num_clauses);
		qry_values = (char **)MEM_alloc(sizeof(char *)*num_clauses);
		//需要 获取 当前 support的 属性 ，向查询的 条件 中 赋值 ，这中间，针对 这个查询 可能 会存在 问题 
		for( i = 0 ;i < num_clauses ; i ++)
		{
			qry_entry[i] = (char *)MEM_alloc(sizeof(char)*256);
			qry_values[i] = (char *)MEM_alloc(sizeof(char)*256);

		}
		fprintf(stdout , "num_clauses = %d\n" , num_clauses);
		for( i = 0; i < num_clauses; i ++)
		{
			TC_write_syslog("--->	entry_names[%d] = %s\n" , i , entry_names[i] );
			fprintf(stdout , "--->	entry_names[%d] = %s\n" , i , entry_names[i] );
				

			if(!tc_strcmp( entry_names[i] , "jci6_Year"))
			{
				tc_strcpy(qry_entry[0],entry_names[i]);
				sprintf(qry_values[0],"%d",support_year);
			}
			else if(!tc_strcmp(entry_names[i] , "Id"))
			{
				tc_strcpy(qry_entry[1],entry_names[i]);
				tc_strcpy(qry_values[1],user_id);//invalid attr

			}
			else if(!tc_strcmp(entry_names[i] , "Division"))
			{
				tc_strcpy(qry_entry[2],entry_names[i]);
				tc_strcpy(qry_values[2],division_name);
			}
			else if(!tc_strcmp(entry_names[i] , "jci6_CostType"))
			{
				tc_strcpy(qry_entry[3],entry_names[i]);
				tc_strcpy(qry_values[3],"Normal Hours");
			}else if(!tc_strcmp(entry_names[i] , "jci6_CPT")){
				tc_strcpy(qry_entry[4],entry_names[i]);
				tc_strcpy(qry_values[4],"Forecast");
			}else if(!tc_strcmp(entry_names[i] , "Section"))
			{
				if(tc_strcmp(section_name,"*"))
				{
					tc_strcpy(qry_entry[5],entry_names[i]);
					tc_strcpy(qry_values[5],section_name);	
				}

			}
		}
		if(user_id)
		{
			MEM_free(user_id);
			user_id = NULL;
		}
		if(division_name)
		{
			MEM_free(division_name);
			division_name = NULL;
		}
		if(section_name)
		{
			MEM_free(section_name);
			section_name = NULL;
		}
		if(entry_names)
		{
			MEM_free(entry_names);
			entry_names = NULL;
		}
		if(values)
		{
			MEM_free(values);
			values = NULL;
		}
			
			
		ITKCALL(QRY_execute( qry ,num_clauses, qry_entry ,qry_values , &num_found,&results)); 
		fprintf(stdout,"num_found = %d\n",num_found);
		if(is_debug)
			TC_write_syslog(" QRY_execute	num_found = %d\n",num_found);

		if(num_found >0)
		{
			for( i = 0 ; i < num_found ; i ++ )
			{
				tmp_cost_info = results[i];
				compute_cost_info(is_debug ,support ,tmp_cost_info , error_map);
			}
		}
		else
		{
				tmp_cost_info = create_cost_info(is_debug ,support ,"Hour" , rate_level , error_map);
				tmp_cost_info = create_cost_info(is_debug ,support ,"ManMonth" , rate_level , error_map);
				tmp_cost_info = create_cost_info(is_debug ,support ,"Yuan" , rate_level , error_map);
		}
		for( i = 0 ;i < num_clauses ; i ++)
		{
			MEM_free(qry_entry[i]);
			MEM_free(qry_values[i]);

		}
		if(qry_entry)
		{
			MEM_free(qry_entry);
			qry_entry = NULL;
		}
		if(qry_values)
		{
			MEM_free(qry_values);
			qry_values = NULL;
		}

		if(results)
		{
			MEM_free(results);
			results = NULL;
		}
	}

	if(is_debug)
		TC_write_syslog("------------------------operate_support end-----------------------\n");

	return ifail;
}

//获取 当前节点的 上一节点 的责任人
tag_t get_pre_task_resp_party( logical is_debug , tag_t current_task )
{
	tag_t resp_party = NULLTAG;
	int pred_cnt = 0;
	tag_t * pred_tasks = NULL;
	tag_t pre_task = NULLTAG;
	tag_t parent_task = NULLTAG;

	char user_name[SA_person_name_size_c + 1] = {'\0'};

	ITKCALL(AOM_ask_value_tags( current_task , "predecessors" , &pred_cnt ,&pred_tasks ));
	if(pred_cnt>0)
		pre_task = pred_tasks[0];

	if(pre_task == NULLTAG)
	{
		ITKCALL( EPM_ask_parent_task( current_task , &parent_task ));
		ITKCALL( EPM_ask_responsible_party( parent_task , &resp_party ));
	}
	else
	{
		ITKCALL( EPM_ask_responsible_party( pre_task , &resp_party ));
	}

	ITKCALL(SA_ask_user_person_name( resp_party , user_name ));
		
	fprintf(stdout,"user_name = %s\n",user_name);

	return resp_party;
}

//发送邮件
//user为邮件接受者
//target挂在邮件上的programview对象
//logical is_debug(I)
//tag_t user(I)
//tag_t target(I)
int send_mail(logical is_debug , tag_t user , tag_t target)
{
	int ifail = ITK_ok;
	tag_t envelope = NULLTAG;
	if(is_debug)
		TC_write_syslog("-----------------send_mail started----------------\n");
	ITKCALL (MAIL_create_envelope( MAIL_NAME , MAIL_CONTENT , &envelope ));
	ITKCALL (MAIL_add_envelope_receiver( envelope , user ));
	ITKCALL (FL_insert( envelope , target , 0 ));
	//CALL (AOM_save( envelope ));
	ITKCALL (MAIL_send_envelope( envelope ));
	if(is_debug)
		TC_write_syslog("-----------------send_mail ended----------------\n");
	return ifail;
}

//unit_value的值
//小时	Hour
//人月	ManMonth
//元	Yuan
//根据support对象创建costInfo 
tag_t create_cost_info(logical is_debug , tag_t support , char * unit_value , tag_t rate_level , map<string , int> & error_map)
{
	tag_t cost_info = NULLTAG;
	long double sum_percent = 0.00;
	char month[12][32] = {
		"jci6_Jan","jci6_Feb","jci6_Mar",
		"jci6_Apr","jci6_May","jci6_Jun",
		"jci6_Jul","jci6_Aug","jci6_Sep",
		"jci6_Oct","jci6_Nov","jci6_Dec"
	};
	char object_name[512] = {'\0'};
	date_t nowDate ;
	char division_name[SA_group_name_size_c + 1] = {'\0'};

	tag_t itemType = NULLTAG;
	tag_t createInputTag = NULLTAG;
	tag_t boTag = NULLTAG;

	int support_year = 0;
	int support_month = 0;
	tag_t support_ownProxy = NULLTAG;
	double support_percent = 0.00;
	tag_t support_section = NULLTAG;
	tag_t support_division = NULLTAG;

	int support_year_value = 0;
	double support_month_value = 0.00;
	tag_t support_user_value = NULLTAG;
	tag_t support_section_value = NULLTAG;
	tag_t support_division_value = NULLTAG;
	char cost_info_name[512] = "CostInfo";
	char support_cost_type_value[128] = {"Normal Hours"};
	char support_cpt_value[128] = {"Forecast"};

	char lov_value[128] = {'\0'};

	double cost_info_d = 0.00;
		

	const char ** costInfoName = (const char **)MEM_alloc(sizeof(char*));
	costInfoName[0] = cost_info_name;


	POM_AM__set_application_bypass( TRUE );

	ITKCALL(AOM_ask_value_int( support , "jci6_Year" , &support_year ));
	ITKCALL(AOM_ask_value_int( support , "jci6_Month" , &support_month ));
	ITKCALL(AOM_ask_value_tag( support , "jci6_ownProxy" , &support_ownProxy ));
	ITKCALL(AOM_ask_value_double( support , "jci6_Percent" , &support_percent ));
	ITKCALL(AOM_ask_value_tag( support , "jci6_Section" , &support_section ));
	ITKCALL(AOM_ask_value_tag( support , "jci6_Division" , &support_division ));

	if(is_debug)
	{
		TC_write_syslog("jci6_Year = %d , cpt = Forecast , unit_value = %s \n",support_year , unit_value );
	}
		
	getNowTime(&nowDate);
	ITKCALL(SA_ask_group_name(support_division,division_name));
	sprintf(cost_info_name,"Forecast_%04d_%s_ExtSupport_%s_%04d%02d%02d%02d%02d%02d",support_year,division_name,unit_value,
		nowDate.year,nowDate.month+1,nowDate.day,nowDate.hour,nowDate.minute,nowDate.second);

	if(is_debug)
	{
		TC_write_syslog("cost_info name = %s\n",cost_info_name);
		fprintf(stdout,"cost_info_name = %s\n",cost_info_name);
	}


	//modify by wuwei
	const char ** unitstr = (const char **)MEM_alloc(1*sizeof(char*));
	char unit[128]="Hour";
	unitstr[0]=unit;

	const char ** ucpt_str = (const char **)MEM_alloc(1*sizeof(char*));
	char CPT[128]="Budget";
	ucpt_str[0]=CPT;

	const char ** year_str = (const char **)MEM_alloc(1*sizeof(char*));
	char YEAR[128]="2018";
	year_str[0]=YEAR;
	//modify by wuwei		



	TCTYPE_find_type( "JCI6_CostInfo" , "JCI6_CostInfo" , &itemType);
	TCTYPE_construct_create_input ( itemType, &createInputTag );
	TCTYPE_set_create_display_value( createInputTag, "object_name", 1, costInfoName );
	TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, costInfoName );
	TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr );
	TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str );
	TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str );


	ITKCALL(TCTYPE_create_object ( createInputTag, &boTag) );
	ITKCALL(AOM_save ( boTag ));


	SAFE_SM_FREE(costInfoName);
	SAFE_SM_FREE(unitstr);
	SAFE_SM_FREE(ucpt_str);
	SAFE_SM_FREE(year_str);

	ITKCALL(AOM_lock( boTag ));
	ITKCALL(AOM_load( boTag ));

	sprintf(lov_value , "%d.%02d",support_year , support_month);

	ITKCALL(AOM_set_value_int( boTag , "jci6_Year" , support_year ));
	ITKCALL(AOM_set_value_tag( boTag , "jci6_User" , support_ownProxy ));	
	if(rate_level != NULLTAG)
		ITKCALL(AOM_set_value_tag( boTag , "jci6_RateLevel" , rate_level));

	compute_cost_info_percent(is_debug ,cost_info_d , lov_value , support_percent , unit_value ,support_ownProxy ,error_map);
	soa_DataPrecision( cost_info_d , &sum_percent );

	ITKCALL(AOM_set_value_double( boTag , month[support_month - 1] , sum_percent ));
	if(support_section!=NULLTAG)
		ITKCALL(AOM_set_value_tag( boTag , "jci6_Section" , support_section ));	
	ITKCALL(AOM_set_value_tag( boTag , "jci6_Division" , support_division ));
	ITKCALL(AOM_set_value_string( boTag , "jci6_CostType" , support_cost_type_value ));
	ITKCALL(AOM_set_value_string( boTag , "jci6_CPT" , support_cpt_value ));
	ITKCALL(AOM_set_value_string( boTag , "jci6_Unit" , unit_value));
	ITKCALL(AOM_save ( boTag ));

	ITKCALL(AOM_unlock( boTag ));		

	POM_AM__set_application_bypass(FALSE);

	return cost_info;
}

int compute_cost_info( logical is_debug , tag_t support , tag_t cost_info , map<string , int> & error_map )
{
	int ifail = ITK_ok;

	//for
	int i = 0;

	//2014-8-14
		
	char month[12][32] = {
		"jci6_Jan","jci6_Feb","jci6_Mar",
		"jci6_Apr","jci6_May","jci6_Jun",
		"jci6_Jul","jci6_Aug","jci6_Sep",
		"jci6_Oct","jci6_Nov","jci6_Dec"
	};
	int support_year = 0;
	int support_month = 0;
	char * unit = NULL;

	char lov_value[128] = {'\0'};
	tag_t ownProxy = NULLTAG;
	tag_t division = NULLTAG;
	tag_t section = NULLTAG;
	char * user_id = NULL;
	char * division_name = NULL;
	char * section_name = NULL;

	double tmp_double = 0.00;

	double support_percent = 0.00;

	double cost_info_percent = 0.00;
		
	long double sum_percent = 0.00;

		
	ITKCALL(AOM_ask_value_tag( support , "jci6_ownProxy" , &ownProxy));
		

	ITKCALL(AOM_ask_value_int( support , "jci6_Year" , &support_year));
	ITKCALL(AOM_ask_value_double( support , "jci6_Percent" , &support_percent ));
	ITKCALL(AOM_ask_value_int( support , "jci6_Month" , &support_month ));
	ITKCALL(AOM_ask_value_double( cost_info , month[support_month-1] , &cost_info_percent ));
	ITKCALL(AOM_ask_value_string( cost_info , "jci6_Unit" , &unit ));

	sprintf(lov_value , "%d.%02d", support_year , support_month);
		
	if(is_debug)
	{
		TC_write_syslog("--->	lov_value = %s\n",lov_value);
		TC_write_syslog("--->	unit = %s\n",unit);
	}

	compute_cost_info_percent(is_debug , cost_info_percent , lov_value ,support_percent , unit ,ownProxy, error_map);

	soa_DataPrecision( cost_info_percent , &sum_percent );

	ITKCALL(AOM_lock(cost_info));

	ITKCALL(AOM_set_value_double( cost_info , month[support_month-1] , sum_percent));

	ITKCALL(AOM_save(cost_info));

	ITKCALL(AOM_unlock(cost_info));


	return ifail;
}

int  compute_cost_info_percent(logical is_debug ,double &output , char * lov_value, double percent , char * unit ,tag_t user , map<string , int> & error_map)
{
	int ifail = ITK_ok;

	char tmp_error[256] = {'\0'};
	//lov
	int lov_cnt = 0;
	tag_t * lovs = NULL;

	int value_cnt = 0;
	char ** values = NULL;

	LOV_usage_t usage;
	int desc_cnt = 0;
	char ** descs = NULL;
	logical * is_null = NULL;
	logical * is_empty = NULL;

	char lov_value_desc[128] = {'\0'};
	int desc_int = 0;
	//for
	int i = 0;

	double tmp_value = 0.00;

	tag_t relation_type = NULLTAG;
	int sec_cnt = 0;
	tag_t * secs = NULL;

	char * cost = NULL;
	double cost_d = 0.00;

	ITKCALL(LOV_find( NormalHoursLOV , &lov_cnt , &lovs ));
	if(lov_cnt >0)
	{
		if(is_debug)
			TC_write_syslog("找到LOV：%s\n",NormalHoursLOV);
		ITKCALL(LOV_ask_values_string(lovs[0] , &value_cnt , &values ));
		ITKCALL(LOV_ask_value_descriptions(lovs[0] , &usage , &desc_cnt , &descs , &is_null , &is_empty ));
		for( i = 0; i < value_cnt ; i ++)
		{
			if(!tc_strcmp(lov_value , values[i]))
			{
				tc_strcpy(lov_value_desc , descs[i]);
			}
		}
		if(tc_strlen(lov_value_desc)>0)
		{
			if(is_debug)
				TC_write_syslog("LOV : %s 的值 %s的描述为：%s\n",NormalHoursLOV,lov_value,lov_value_desc);

			desc_int = atoi(lov_value_desc);
			if(desc_int != 0)
			{
				if(is_debug)
					TC_write_syslog("desc_int = %d\n",desc_int);
				if(!tc_strcmp(unit , "Hour"))
				{
					tmp_value = desc_int*percent/100;
					if(is_debug)
						TC_write_syslog("--->	tmp_value = %lf \n",tmp_value);
				}
				else
					if(!tc_strcmp(unit , "ManMonth"))
				{
					tmp_value = desc_int*percent/170/100;
					if(is_debug)
						TC_write_syslog("--->	tmp_value = %lf \n",tmp_value);
				}
				else
					if(!tc_strcmp(unit , "Yuan"))
				{
					ITKCALL( GRM_find_relation_type( "CostValue_Relation", &relation_type ) );
					ITKCALL( GRM_list_secondary_objects_only( user, relation_type, &sec_cnt, &secs) );
					if(sec_cnt>0)
					{
						ITKCALL(AOM_ask_value_string(secs[0] , "cost" , &cost));
						cost_d = atof(cost);
						if(is_debug)
						{
							TC_write_syslog("cost = %s\n",cost);
						}
						if(!( abs(cost_d) <= 1e-15))
						{
							tmp_value = desc_int*percent*cost_d/100;
							if(is_debug)
								TC_write_syslog("--->	tmp_value = %lf \n",tmp_value);
						}
					}
					if(secs)
					{
						MEM_free(secs);
						secs = NULL;
					}
				}
					output+=tmp_value;
			}
			else
			{
				if(is_debug)
					TC_write_syslog("%s无法转换为整数！\n",lov_value_desc);
			}
		}
		else
		{
			sprintf(tmp_error ,"LOV : %s 不存在值 %s ，请联系系统管理员！",NormalHoursLOV,lov_value );
			string err_str;
			err_str.assign(tmp_error);
			error_map.insert(pair<string , int>(err_str , ERROR_LOV_NOT_DEFINE));
			if(is_debug)
				TC_write_syslog("%s\n",err_str.c_str());
		}
		if(values)
		{
			MEM_free(values);
			values = NULL;
		}
		if(descs)
		{
			MEM_free(descs);
			descs = NULL;
		}
		if(is_null)
		{
			MEM_free(is_null);
			is_null = NULL;
		}
		if(is_empty)
		{
			MEM_free(is_empty);
			is_empty = NULL;
		}
	}
	else
	{
		sprintf(tmp_error ,"未找到查询：%s,请与管理员联系！",NormalHoursLOV);
		string err_str;
		err_str.assign(tmp_error);
		if(is_debug)
			TC_write_syslog("%s\n",err_str.c_str());
		error_map.insert(pair<string , int>(err_str , ERROR_LOV_NOT_DEFINE));
	}
	if(lovs)
	{
		MEM_free(lovs);
		lovs = NULL;
	}
	return ifail;
}
	

