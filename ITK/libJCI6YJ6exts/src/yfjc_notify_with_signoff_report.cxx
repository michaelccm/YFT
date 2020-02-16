/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   : yfjc_notify_with_signoff_report.cxx
    Module : user_exits

    SP-CUS-003	自动添加开发的任务签发报告作为邮件附件

	Action Handler
	-debug			true , false	//write log
	-recipient		user:user_id|$USER|$REVIEWERS|$TARGETOWNER|$RESPONSIBLE_PARTY	//mail reciver
	-subject		//mail head
	-comments		//mail comments
	-attachment		$TARGET|$PROCESS|$REFERENCE		//mail contents

============================================================================================================
DATE           Name             Description of Change
18_ARG_2014    mengyawei          creation
$HISTORY$

============================================================================================================*/
#include <tc/tc.h>
#include <epm/epm.h>
#include <tccore/aom.h>
#include <tccore/aom_prop.h>
//#include <tccore/tctype.h>
#include <tccore/workspaceobject.h>
#include <user_exits/epm_toolkit_utils.h>
#include <tc/envelope.h>
#include <tc/folder.h>
//#include <lov/lov.h>
//#include <qry/qry.h>
#include <tc/emh.h>
//#include <epm/cr.h>
#include <sa/groupmember.h>
#include <sa/user.h>


#include <map>
#include <vector>
#include <string>
#include <stdio.h>
#include <stdlib.h>
#include <unidefs.h>


using namespace std;

#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
//#include "yfjc_time.h"

#ifdef __cplusplus
extern "C" {
#endif

//#include <direct.h>
//#include <io.h>
//#include <unistd.h>
//#include <iconv.h>  //转编码


#define TC_specification	"IMAN_specification"

#define AUDIT_FILE_FOLDER	"EBP报表配置模板"
#define AUDIT_FILE_NAME		"NotifyReport"

#define DoNodeDone			"完成"
#define RejectDecision		"拒绝"
#define ApproveDecision		"批准"
#define NoDecition			"未作决定"

#define BUF_SIZE			1024

	typedef struct  Signoff_Node
	{
		string date_string;
		string node_name;
		string user_name;
		string operate;
		string opinion;
	}*  Signoff_Node_t;

	extern int POM_AM__set_application_bypass(logical bypass);
	tag_t get_login_user();
	int get_task_resp_party(tag_t task , map<tag_t , tag_t >& reciver_map);
	int get_target_owner(tag_t task ,  map<tag_t , tag_t >& reciver_map );
	int get_task_reviewers( tag_t task ,  map<tag_t , tag_t >& reciver_map );
	int get_task_target_attachment(tag_t task , vector<tag_t> & content_vec );
	int get_task_reference_attachment(tag_t task , vector<tag_t> & content_vec );
	int send_report_mail(char * mail_head , char * mail_comment , map<tag_t , tag_t> receiver_map , vector<tag_t> content_vec );
	tag_t get_audit_file_model( map<string , int>& error_map);

	int get_task_signoff_info(tag_t task , vector< Signoff_Node>& signoff_vec);

	static int create_dataset(char *ds_type,char *ref_name,char *ds_name,char *ext, char *fullfilename, char* filename, tag_t parent_rev, tag_t *dataset);
	int create_and_fill_txt_file(char * file_path , vector<Signoff_Node> signoff_info);
	int isExist(char *filename);

	//int copy_fill_signoff_report(tag_t report_mode ,char * report_name , tag_t & report , vector<Signoff_Node> report_info);
	//int fill_xml_file(char * file_path , vector<Signoff_Node> signoff_info);

	//UTF-8 to GB2312
	int UTFToGBK(char *inbuf, size_t inlen, char *outbuf, size_t outlen);

	int yfjc_notify_with_signoff_report(EPM_action_message_t msg)
	{
		int ifail = ITK_ok;
		
		//att
		tag_t root_task = NULLTAG;
		tag_t job = NULLTAG;

		char * job_name = NULL;

		tag_t audit_file = NULLTAG;	//审计文件

		tag_t audit_file_mode = NULLTAG;

		char audit_file_name[512] = {'\0'};

		//para
		int arg_cnt = 0;
		char * tmp_arg = NULL;
		char * para_name = NULL;
		char * para_value = NULL;

		int parse_cnt = 0;
		char ** parse_values = NULL;

		char tmp_user_id[128] = {'\0'};
		tag_t tmp_user = NULLTAG;
		char * user_id = NULL;

		char mail_head[256] = {'\0'};		//邮件标题
		char mail_comment[1024] = {'\0'};	//邮件内容
		vector<tag_t> content_vec ;			//邮件附件
		map<tag_t , tag_t> reciver_map ;			//邮件接收者

		logical is_debug = FALSE;//是否写日志 

		//error
		char tmp_error[512] = {'\0'};
		map<string , int> error_map ;

		vector< Signoff_Node> signoff_info;

		char txt_file_name[64] = {"Signoff_Report.txt"};
		char txt_file_path[256] = {'\0'};

		//for
		int i = 0;
		int j = 0;

		TC_write_syslog("----------------------yfjc_notify_with_signoff_report started----------------------\n");

		ITKCALL(EPM_ask_root_task( msg.task , &root_task ));
		ITKCALL(EPM_ask_job( msg.task , &job ));
		ITKCALL(AOM_ask_value_string( job , "object_name" , &job_name));

		TC_write_syslog("---->		job_name = %s\n",job_name);

		//ITKCALL(EPM_ask_audit_file( job , &audit_file ));

		arg_cnt = TC_number_of_arguments(msg.arguments);
		for( i = 0; i < arg_cnt ; i ++)
		{
			tmp_arg = TC_next_argument(msg.arguments);
			TC_write_syslog("--->	tmp_arg = %s\n",tmp_arg);
			fprintf(stdout , "--->		tmp_arg = %s\n",tmp_arg);
			ITKCALL(ITK_ask_argument_named_value( tmp_arg , &para_name , &para_value ));
			if(!tc_strcmp(para_name , "debug"))
			{
				if(!tc_strcasecmp(para_value , "true"))
				{
					is_debug = TRUE;
				}
			}
			else
				if(!tc_strcmp(para_name , "recipient"))
			{
				ITKCALL(EPM__parse_string(para_value , "|" , &parse_cnt , &parse_values ));
				for( j = 0; j < parse_cnt; j ++)
				{
					if(!tc_strcmp(parse_values[j] , "$PROCESSOWNER"))
					{
						ITKCALL(AOM_ask_owner( root_task , &tmp_user ));
						ITKCALL(AOM_ask_value_string( tmp_user , "user_id" , &user_id ));
						TC_write_syslog("$PROCESSOWNER  user_id = %s\n" , user_id );
						reciver_map.insert(pair<tag_t , tag_t>(tmp_user , tmp_user) );
						if( user_id )
						{
							MEM_free( user_id );
							user_id = NULL;
						}
					}
					else if(!tc_strcmp(parse_values[j] , "$USER"))
					{
						tmp_user = get_login_user();
						reciver_map.insert(pair<tag_t , tag_t>(tmp_user , tmp_user) );
					}
					else
						if(!tc_strcmp(parse_values[j] , "$REVIEWERS"))
					{
						get_task_reviewers( root_task ,  reciver_map );
					}
					else
						if(!tc_strcmp(parse_values[j] , "$RESPONSIBLE_PARTY"))
					{
						get_task_resp_party(root_task , reciver_map);
					}
					else
						if(!tc_strcmp(parse_values[j] , "$TARGETOWNER"))
					{
						 get_target_owner(root_task ,reciver_map);
					}
					else
						if(tc_strstr(parse_values[j] , "user:"))
					{
						tc_strcpy(tmp_user_id , (tc_strstr(parse_values[j] , ":")+1));
						TC_write_syslog("--->	user:%s\n",tmp_user_id);
						ITKCALL(SA_find_user(tmp_user_id , &tmp_user));
						reciver_map.insert(pair<tag_t , tag_t>(tmp_user , tmp_user));
					}
				}
			}
			else
				if(!tc_strcmp(para_name , "subject"))
			{
				tc_strcpy(mail_head , para_value);
			}
			else
				if(!tc_strcmp(para_name , "comments"))
			{
				tc_strcpy(mail_comment , para_value);
			}
			else
				if(!tc_strcmp(para_name , "attachment"))
			{
				//content_vec.push_back(audit_file);
				ITKCALL(EPM__parse_string(para_value , "|" , &parse_cnt , &parse_values ));
				for( j = 0; j < parse_cnt ; j ++)
				{
					if(!tc_strcmp(parse_values[j] , "$TARGET"))
					{
						get_task_target_attachment( root_task , content_vec );
					}
					else 
						if(!tc_strcmp(parse_values[j] , "$PROCESS"))
					{
						content_vec.push_back(root_task);
					}
					else
						if(!tc_strcmp(parse_values[j] , "$REFERENCE"))
					{
						get_task_reference_attachment( root_task , content_vec );
					}
				}
				if(parse_values)
				{
					MEM_free(parse_values);
					parse_values = NULL;
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


		if(tc_strlen(mail_head) == 0)
		{
			sprintf(tmp_error , "未配置参数-subject=[邮件标题],请通知管理员！");
			string err_str;
			err_str.assign(tmp_error);
			TC_write_syslog("	ERROR : %s\n",err_str.c_str());
			error_map.insert(pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		}
		if(tc_strlen(mail_comment) == 0)
		{
			sprintf(tmp_error , "未配置参数-comments=[邮件内容],请通知管理员！");
			string err_str;
			err_str.assign(tmp_error);
			TC_write_syslog("	ERROR : %s\n",err_str.c_str());
			error_map.insert(pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		}
		//-attachment参数不为必填项
		//if(content_vec.size() == 0)
		//{
		//	sprintf(tmp_error , "未配置参数-attachment,请通知管理员！");
		//	string err_str;
		//	err_str.assign(tmp_error);
		//	TC_write_syslog("	ERROR : %s\n",err_str.c_str());
		//	error_map.insert(pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		//}
		if(reciver_map.size() == 0)
		{
			sprintf(tmp_error , "未配置参数-recipient=[user:user_id|$USER|$REVIEWERS|$TARGETOWNER|$RESPONSIBLE_PARTY],请通知管理员！");
			string err_str;
			err_str.assign(tmp_error);
			TC_write_syslog("	ERROR : %s\n",err_str.c_str());
			error_map.insert(pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		}

		if(error_map.size() == 0)
		{	
				get_task_signoff_info( root_task , signoff_info);

				TC_write_syslog("---->		signoff_info.size() = %d\n",signoff_info.size());

				if(signoff_info.size() > 0)//确保签审文件中有内容的情况下才会发送邮件
				{
					sprintf(audit_file_name , "%s_Signoff_Report" , job_name);

					TC_write_syslog("--->	audit_file_name = %s\n",audit_file_name);

					//copy_fill_signoff_report( audit_file_mode , audit_file_name , audit_file  , signoff_info );

					sprintf(txt_file_path , "%s//%s", getenv("TC_SHARED_MEMORY_DIR") , txt_file_name );

					if(isExist(txt_file_path))
					{
						remove(txt_file_path);
					}

					create_and_fill_txt_file(txt_file_path , signoff_info );

					if(isExist(txt_file_path))
					{
							ifail = create_dataset("Text","Text",audit_file_name,"txt",txt_file_path,txt_file_name,NULLTAG,&audit_file);
							if(ifail != ITK_ok)
							{
								fprintf(stdout,"create TEXT dataset %s failed！\n",audit_file_name);
								TC_write_syslog("create TEXT dataset %s failed！\n",audit_file_name);
								
							}else{
								fprintf(stdout,"create TEXT dataset %s successful！\n",audit_file_name);
								TC_write_syslog("create TEXT dataset %s successful！\n",audit_file_name);
							}
							content_vec.push_back(audit_file);
							send_report_mail( mail_head ,  mail_comment ,  reciver_map ,  content_vec );
					}
					else
					{
					}
					if(isExist(txt_file_path))
					{
						remove(txt_file_path);
					}

					if(audit_file != NULLTAG)
					{
						TC_write_syslog("------>		audit_file != NULL\n");
						content_vec.push_back( audit_file );
					}
					
				}	
		}

		if(job_name)
		{
			MEM_free(job_name);
			job_name = NULL;
		}

		map<string , int>::iterator error_map_ite;
		for(error_map_ite = error_map.begin(); error_map_ite != error_map.end(); error_map_ite ++)
		{
			ifail = CR_error_in_handler;
			if(is_debug)
				TC_write_syslog("--->		%d :	%s\n" , error_map_ite->second , error_map_ite->first.c_str() );
			EMH_store_error_s1( EMH_severity_error, error_map_ite->second , error_map_ite->first.c_str() );
		}
	
		TC_write_syslog("-----------------------yfjc_notify_with_signoff_report ended-----------------------\n");

		return ifail;
	}

	//获取当前登陆用户
	tag_t get_login_user()
	{
		int ifail = ITK_ok;
		tag_t groupmember = NULLTAG;
		tag_t login_user = NULLTAG;
		char * user_id = NULL;

		ITKCALL(SA_ask_current_groupmember(&groupmember));
		ITKCALL(SA_ask_groupmember_user(groupmember , &login_user));
		ITKCALL(AOM_ask_value_string(login_user , "user_id" , &user_id ));
		fprintf(stdout , "login_user . user_id = %s\n" , user_id);
		TC_write_syslog("login_user . user_id = %s\n" , user_id);

		return login_user;
	}

	//获取流程节点的责任人
	//tag_t root_task (I)
	//map<tag_t , tag_t> & reciver_map(O)
	int get_task_resp_party(tag_t task , map<tag_t , tag_t >& reciver_map)
	{
		int ifail = ITK_ok;

		int sub_cnt = 0;
		tag_t * sub_tasks = NULL;
		tag_t sub_task = NULLTAG;

		tag_t resp_party = NULLTAG;

		char task_name[WSO_name_size_c+1] = {'\0'};
		char * user_id = NULL;

		//for
		int i = 0;

		ITKCALL(EPM_ask_sub_tasks( task , &sub_cnt , &sub_tasks));
		ITKCALL(EPM_ask_responsible_party( task , &resp_party));
		
		reciver_map.insert(pair<tag_t , tag_t>(resp_party , resp_party));

		ITKCALL(EPM_ask_name(task , task_name ));

		ITKCALL(AOM_ask_value_string( resp_party , "user_id" , &user_id ));
		TC_write_syslog("Task Node Name = %s	UserId = %s\n" , task_name , user_id );

		if(user_id)
		{
			MEM_free(user_id);
			user_id = NULL;
		}

		for( i = 0 ; i < sub_cnt ; i ++)
		{
			sub_task = sub_tasks[i];
			get_task_resp_party(sub_task , reciver_map);
		}

		if(sub_tasks)
		{
			MEM_free(sub_tasks);
			sub_tasks = NULL;
		}
		
		return ifail;
	}

	//获取流程目标的所有者
	//tag_t root_task(I)
	//map<tag_t , tag_t>&reciver_map(O)
	int get_target_owner(tag_t task ,  map<tag_t , tag_t >& reciver_map )
	{
		int ifail = ITK_ok;

		int att_cnt = 0;
		tag_t * atts = NULL;
		tag_t tmp_att = NULLTAG;
		
		int i = 0;

		char att_name[WSO_name_size_c + 1] = {'\0'};
		char * user_id = NULL;
		tag_t tmp_att_owner = NULLTAG;

		ITKCALL(EPM_ask_attachments( task , EPM_target_attachment , &att_cnt , &atts ));

		for( i = 0; i < att_cnt ; i ++)
		{
			tmp_att = atts[i];
			ITKCALL(WSOM_ask_name( tmp_att , att_name ));
			ITKCALL(AOM_ask_owner( tmp_att , &tmp_att_owner ));
			if(tmp_att_owner!= NULLTAG)
			{
				ITKCALL(AOM_ask_value_string( tmp_att_owner , "user_id" , &user_id ));
				TC_write_syslog("att_name = %s		user_id = %s\n" ,att_name , user_id );
				if(user_id)
				{
					MEM_free(user_id);
					user_id = NULL;
				}
				reciver_map.insert(pair<tag_t , tag_t>( tmp_att_owner , tmp_att_owner));
			}
		}

		if(atts)
		{
			MEM_free(atts);
			atts = NULL;
		}
		return ifail;
	}

	//获取流程中的reviwers
	//tag_t root_task(I)
	//map<tag_t , tag_t>&reciver_map(O)
	int get_task_reviewers( tag_t task ,  map<tag_t , tag_t >& reciver_map )
	{
		int ifail = ITK_ok;
		
		tag_t viewed_by = NULLTAG;

		int sub_cnt = 0;
		tag_t * sub_tasks = NULL;
		
		tag_t sub_task = NULLTAG;
		char * sub_task_type = NULL;

		int signoff_cnt = 0;
		tag_t *signoffs = NULL;
		tag_t signoff = NULLTAG;

		char * user_id = NULL;
		//for
		int i = 0;
		int j = 0;

		ITKCALL(EPM_ask_sub_tasks( task , &sub_cnt , &sub_tasks ))
		
		for( i = 0; i < sub_cnt; i++)
		{
			sub_task = sub_tasks[i];
			ITKCALL(AOM_ask_value_string( sub_task , "object_type" , &sub_task_type ));
			TC_write_syslog("--->		sub_task_type = %s\n",sub_task_type);
			if((!tc_strcmp(sub_task_type , "EPMReviewTask"))||(!tc_strcmp(sub_task_type , "EPMAcknowledgeTask")))
			{
				TC_write_syslog("(!tc_strcmp(sub_task_type , 'EPMReviewTask'))||(!tc_strcmp(sub_task_type , 'EPMAcknowledgeTask'))\n");
				ITKCALL(AOM_ask_value_tags( sub_task , "valid_signoffs" , &signoff_cnt , &signoffs));
				TC_write_syslog("---->	signoff_cnt = %s\n",signoff_cnt);
				for( j = 0; j < signoff_cnt ; j ++)
				{
					signoff = signoffs[j];
					ITKCALL(AOM_ask_owner( signoff , &viewed_by));
					reciver_map.insert(pair<tag_t , tag_t>(viewed_by , viewed_by));
					ITKCALL(AOM_ask_value_string(viewed_by , "user_id" , &user_id));
					TC_write_syslog("--->		user_id = %s\n",user_id);
					if(user_id)
					{
						MEM_free(user_id);
						user_id = NULL;
					}
				}
				if(signoffs)
				{
					MEM_free(signoffs);
					signoffs = NULL;
				}
			}

			if(sub_task_type)
			{
				MEM_free(sub_task_type);
				sub_task_type = NULL;
			}
		}		

		if(sub_tasks)
		{
			MEM_free(sub_tasks);
			sub_tasks = NULL;
		}

		return ifail;
	}

	//获取流程的target
	//tag_t root_task(I)
	//vector<tag_t> & content_vec(O)
	int get_task_target_attachment(tag_t task , vector<tag_t> & content_vec )
	{
		int ifail = ITK_ok;

		int att_cnt = 0;
		tag_t * atts = NULL;
		tag_t tmp_att = NULLTAG;
		int i = 0;

		ITKCALL(EPM_ask_attachments(task , EPM_target_attachment , &att_cnt , &atts ));

		for( i = 0; i < att_cnt; i ++)
		{
			tmp_att = atts[i];
			content_vec.push_back(tmp_att);
		}

		if(atts)
		{
			MEM_free(atts);
			atts = NULL;
		}
		return ifail;
	}
	//获取reference
	//tag_t root_task(I)
	//vector<tag_t>& content_vec(O)
	int get_task_reference_attachment(tag_t task , vector<tag_t> & content_vec )
	{
		int ifail = ITK_ok;

		int ref_cnt = 0;
		tag_t * refs = NULL;
		tag_t tmp_ref = NULLTAG;

		int i = 0;

		ITKCALL(EPM_ask_attachments( task , EPM_reference_attachment , &ref_cnt , &refs ));

		for( i = 0; i < ref_cnt ; i ++)
		{
			tmp_ref = refs[i];
			content_vec.push_back(tmp_ref);
		}

		if(refs)
		{
			MEM_free(refs);
			refs = NULL;
		}

		return ifail;
	}

	//发送邮件
	//user为邮件接受者
	//target挂在邮件上的programview对象
	//logical is_debug(I)
	//tag_t user(I)
	//tag_t target(I)
	int send_report_mail(char * mail_head , char * mail_comment , map<tag_t , tag_t> receiver_map , vector<tag_t> content_vec )
	{
		int ifail = ITK_ok;
		
		int content_cnt = content_vec.size();
		tag_t * contents = (tag_t*)MEM_alloc(sizeof(tag_t)*content_cnt);
		int i = 0;

		tag_t user = NULLTAG;
		tag_t person_tag = NULLTAG;
		char * email_address = NULL;

		tag_t envelope = NULLTAG;

		
		TC_write_syslog("-----------------send_mail started----------------\n");

		ITKCALL (MAIL_create_envelope( mail_head , mail_comment , &envelope ));

		map<tag_t , tag_t>::iterator receiver_map_ite;
		for(receiver_map_ite = receiver_map.begin(); receiver_map_ite != receiver_map.end(); receiver_map_ite ++ )
		{
			user = receiver_map_ite->first;
			ITKCALL (SA_ask_user_person( user , &person_tag ));
			ITKCALL (SA_ask_person_email_address( person_tag , &email_address ));
			if((email_address== NULL)||(tc_strlen(email_address) == 0))
			{
				ITKCALL (MAIL_add_envelope_receiver( envelope , user ));
			}
			else
			{
				ITKCALL(MAIL_add_external_receiver( envelope , MAIL_send_to , email_address ));
			}
			if(email_address)
			{
				MEM_free(email_address);
				email_address = NULL;
			}
		}
		if(content_cnt >0)
		{
			for( i = 0; i < content_cnt ; i ++)
			{
				contents[i] = content_vec[i];
			}

			ITKCALL(FL_insert_instances( envelope , content_cnt , contents ,0 ));	
		}
		

		ITKCALL (MAIL_send_envelope( envelope ));

		if(contents)
		{
			MEM_free(contents);
			contents = NULL;
		}
		
		TC_write_syslog("-----------------send_mail ended----------------\n");
		return ifail;
	}

	//获取infodba　home下的 "EBP报表配置模板" "NotifyReport"
	//map<string , int>& error_map
	tag_t get_audit_file_model( map<string , int>& error_map)
	{
		tag_t audit_file = NULLTAG;

		tag_t infodba = NULLTAG;
		tag_t home_folder = NULLTAG;

		int ref_cnt = 0;
		tag_t * refs = NULL;
		tag_t tmp_ref = NULLTAG;

		char * tmp_ref_type = NULL;
		char * tmp_ref_name = NULL;

		tag_t signoff_folder = NULLTAG;

		//error
		char tmp_err[512] = {'\0'};


		//for
		int i = 0;
		int j = 0;
		

		TC_write_syslog("---------------get_audit_file_model started---------------\n");

		ITKCALL(SA_find_user( "infodba" , &infodba ));
		ITKCALL(SA_ask_user_home_folder( infodba , &home_folder ));

		ITKCALL(FL_ask_references( home_folder , FL_fsc_by_type , &ref_cnt , &refs ));
		for( i = 0; i < ref_cnt ; i ++)
		{
			tmp_ref = refs[i];
			ITKCALL(AOM_ask_value_string( tmp_ref , "object_type" , &tmp_ref_type ));
			ITKCALL(AOM_ask_value_string( tmp_ref , "object_name" , &tmp_ref_name ));
			TC_write_syslog("---->		refs[%d]	object_name = %s	object_type = %s\n", i , tmp_ref_name , tmp_ref_type );
			if((!tc_strcmp(tmp_ref_type , "Folder"))&&(!tc_strcmp(tmp_ref_name , AUDIT_FILE_FOLDER)))
			{

				signoff_folder = tmp_ref;
			}
			if(tmp_ref_name)
			{
				MEM_free(tmp_ref_name);
				tmp_ref_name = NULL;
			}
			if(tmp_ref_type)
			{
				MEM_free(tmp_ref_type);
				tmp_ref_type = NULL;
			}
			if(signoff_folder != NULL)
				break;
		}
		if(refs)
		{
			MEM_free(refs);
			refs = NULL;
		}

		if(signoff_folder == NULLTAG)
		{
			string err_str;
			sprintf(tmp_err,"未在infodba的 home下 找到 %s 文件夹!" , AUDIT_FILE_FOLDER );

			err_str.assign(tmp_err);

			TC_write_syslog("ERROR	:	%s\n",err_str.c_str());

			error_map.insert(pair<string , tag_t>( err_str , ERROR_FIND_NO_DATASET ));
		}
		else
		{
			TC_write_syslog("在 infodba 的 home 下 找到 %s 文件夹!\n", AUDIT_FILE_FOLDER );

			ITKCALL(FL_ask_references( signoff_folder , FL_fsc_no_order , &ref_cnt , &refs ));
			for( i = 0; i < ref_cnt ; i ++)
			{
				tmp_ref = refs[i];
				ITKCALL(AOM_ask_value_string(tmp_ref , "object_name" , &tmp_ref_name));
				ITKCALL(AOM_ask_value_string(tmp_ref , "object_type" , &tmp_ref_type));
				if((!tc_strcmp(tmp_ref_type , "Text"))&&(!tc_strcmp(tmp_ref_name , "NotifyReport")))
				{
					TC_write_syslog("在 infodba 的 home 下的 %s文件夹下找到文件%s\n", AUDIT_FILE_FOLDER ,AUDIT_FILE_NAME );
					audit_file = tmp_ref;
				}
				if(tmp_ref_name)
				{
					MEM_free(tmp_ref_name);
					tmp_ref_name = NULL;
				}
				if(tmp_ref_type)
				{
					MEM_free(tmp_ref_type);
					tmp_ref_type = NULL;
				}
				if(audit_file != NULL)
					break;

			}
			if(audit_file == NULL)
			{
				sprintf(tmp_err,"未在infodba的 home下的 %s 文件夹下找到%s文件" , AUDIT_FILE_FOLDER ,AUDIT_FILE_NAME );

				string err_str;
				err_str.assign(tmp_err);

				TC_write_syslog("ERROR	:	%s\n",err_str.c_str());

				error_map.insert(pair<string , tag_t>( err_str , ERROR_FIND_NO_DATASET ));
			}
			if(refs)
			{
				MEM_free(refs);
				refs = NULL;
			}

		}



		TC_write_syslog("----------------get_audit_file_model ended----------------\n");

		return audit_file ;
	}

	//
	//tag_t task(I)
	//vector< Signoff_Node> & signoff_vec(O)
	int get_task_signoff_info(tag_t task , vector< Signoff_Node>& signoff_vec)
	{
		int ifail = ITK_ok;

		int sub_cnt = 0;
		tag_t * sub_tasks = NULL;

		tag_t sub_task = NULLTAG;

		EPM_state_t task_state;
		char state_string[WSO_name_size_c + 1]  = {'\0'};
		char task_name[WSO_name_size_c + 1] = {'\0'};

		char * sub_task_type = NULL;

		//do
		tag_t resp_party = NULLTAG;
		char * resp_party_name = NULL;
		date_t last_mod_date ;

		char tmp_ch[256] = {'\0'};

		//reviewer
		int sign_cnt = 0;
		tag_t * signs = NULL;
		tag_t tmp_sign = NULLTAG;


		tag_t owner = NULLTAG;
		char * owner_name = NULL;

		CR_signoff_decision_t decision;
		char comments[CR_comment_size_c + 1] = {'\0'};
		date_t decision_date;




		//for
		int i = 0;
		int j = 0;

		ITKCALL(EPM_ask_sub_tasks( task , &sub_cnt , &sub_tasks));

		for( i = 0; i < sub_cnt ; i ++)
		{
			sub_task = sub_tasks[i];
			ITKCALL(EPM_ask_name( sub_task , task_name));
			ITKCALL(EPM_ask_state( sub_task , &task_state));
			ITKCALL(EPM_ask_state_string( task_state , state_string ));
			ITKCALL(AOM_ask_value_string( sub_task , "object_type", &sub_task_type ));

			TC_write_syslog("	task_name = %s		sub_task_type = %s		state_string = %s\n" , task_name , sub_task_type , state_string );

			if(task_state == EPM_completed)
			{
				if(!tc_strcmp( sub_task_type , "EPMDoTask"))//do节点
				{
					Signoff_Node node;
					ITKCALL(AOM_ask_value_date(sub_task , "last_mod_date" , &last_mod_date ));
					sprintf(tmp_ch , "%04d-%02d-%02d",last_mod_date.year,last_mod_date.month,last_mod_date.day);
					node.date_string.assign(tmp_ch);
					ITKCALL(EPM_ask_responsible_party(sub_task , &resp_party));
					ITKCALL(AOM_ask_value_string(resp_party , "user_name" , &resp_party_name));
					node.user_name.assign(resp_party_name);
					node.node_name.assign(task_name);
					node.operate.assign(DoNodeDone);
					node.opinion.assign("");

					TC_write_syslog("	time = %s	node_name = %s	operator = %s	operate = %s	process_opinion = %s\n",
						node.date_string.c_str() , node.node_name.c_str() , node.user_name.c_str() , node.operate.c_str() , node.opinion.c_str());

					signoff_vec.push_back(node);
					if(resp_party_name)
					{
						MEM_free(resp_party_name);
						resp_party_name = NULL;
					}
				}
				else
					if((!tc_strcmp( sub_task_type , "EPMReviewTask" ))||(!tc_strcmp( sub_task_type , "EPMAcknowledgeTask" )))//签审，通知节点
				{
					

					ITKCALL(AOM_ask_value_tags( sub_task , "valid_signoffs" , &sign_cnt , &signs));

					for( j = 0; j < sign_cnt; j ++)
					{
						Signoff_Node node;
						tmp_sign = signs[j];
						
						node.node_name.assign(task_name);

						ITKCALL(AOM_ask_owner(tmp_sign , &owner));
						ITKCALL(AOM_ask_value_string(owner , "user_name" , &owner_name));
						//ITKCALL(AOM_ask_value_string(owner , "user_id" , &owner_name));
						node.user_name.assign(owner_name);
						ITKCALL(CR_ask_signoff_decision(tmp_sign , &decision , comments , &decision_date));
						sprintf(tmp_ch , "%04d-%02d-%02d",decision_date.year,decision_date.month,decision_date.day);
						node.date_string.assign(tmp_ch);
						node.opinion.assign(comments);
	
						if(decision == CR_no_decision)
							node.operate.assign(NoDecition);
						else if(decision == CR_approve_decision)
							node.operate.assign(ApproveDecision);
						else if(decision == CR_reject_decision)
							node.operate.assign(RejectDecision);

						TC_write_syslog("	time = %s	node_name = %s	operator = %s	operate = %s	process_opinion = %s\n",
							node.date_string.c_str() , node.node_name.c_str() , node.user_name.c_str() , node.operate.c_str() , node.opinion.c_str());

						signoff_vec.push_back(node);

						if(owner_name)
						{
							MEM_free(owner_name);
							owner_name = NULL;
						}



					}

					if(signs)
					{
						MEM_free(signs);
						signs = NULL;
					}
					

				}
			}
			else
			{
				TC_write_syslog("节点未完成，不做处理！\n");
			}

			if(sub_task_type)
			{
				MEM_free(sub_task_type);
				sub_task_type = NULL;
			}
		}

		if(sub_tasks)
		{
			MEM_free(sub_tasks);
			sub_tasks = NULL;
		}

		return ifail;
	}


	int isExist(char *filename)  
	{  
		return (access(filename, 0) == 0);  
	} 

	static int FindTargetTool( tag_t ds_type, const char *ref_name, tag_t *target_tool )
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
	static int create_dataset(char *ds_type,char *ref_name,char *ds_name,char *ext, char *fullfilename, char* filename, tag_t parent_rev, tag_t *dataset)
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
		ITKCALL( AE_add_dataset_named_ref( new_ds, ref_name,AE_PART_OF, new_file_tag ) );
		ITKCALL( AOM_save( new_ds ) );
		ITKCALL( AOM_unlock( new_ds ) );
		*dataset = new_ds;
		//create relation with item revision
		
		if(parent_rev != NULLTAG)
		{
			ITKCALL( GRM_find_relation_type( TC_specification, &spec_relation ) );

			ITKCALL( GRM_create_relation( parent_rev, new_ds, spec_relation, NULLTAG, &relation ) );
			ITKCALL( GRM_save_relation( relation ) );
			ITKCALL( AOM_unlock( relation ) );
		}
		POM_AM__set_application_bypass(FALSE);
		return ITK_ok;
	}


	int create_and_fill_txt_file(char * file_path , vector<Signoff_Node> signoff_info)
	{
		int ifail = ITK_ok;

		FILE * fp ;

		int i = 0;
		char utf_buf[BUF_SIZE] = {'\0'};
		char gbk_buf[BUF_SIZE] = {'\0'};


		fp = fopen(file_path,"wb+");

		fprintf(fp,"\t%-16s\t|","Time");
		fprintf(fp,"\t%-16s\t|","Node Name");
		fprintf(fp,"\t%-32s\t|","Operator");
		fprintf(fp,"\t%-16s\t\t|","Operate");
		fprintf(fp,"\t%-32s\n","Process opinion");

		fprintf(stdout,"\t%-16s\t|","Time");
		fprintf(stdout,"\t%-16s\t|","Node Name");
		fprintf(stdout,"\t%-32s\t|","Operator");		
		fprintf(stdout,"\t%-16s\t|","Operate");
		fprintf(stdout,"\t%-32s\n","Process opinion");



		for( i = 0; i < signoff_info.size(); i ++)
		{
			tc_strcpy(utf_buf,signoff_info[i].date_string.c_str());
			UTFToGBK(utf_buf,512,gbk_buf,512);
			fprintf(fp , "\t%-16s\t|" , gbk_buf);
			fprintf(stdout , "\t-16%s\t|" , gbk_buf);
			
			tc_strcpy(utf_buf,signoff_info[i].node_name.c_str());
			UTFToGBK(utf_buf,512,gbk_buf,512);
			fprintf(fp , "\t%-16s\t|" ,gbk_buf);
			fprintf(stdout , "\t%-16s\t|" , gbk_buf);
			
			tc_strcpy(utf_buf,signoff_info[i].user_name.c_str());
			UTFToGBK(utf_buf,512,gbk_buf,512);
			fprintf(fp , "\t%-32s\t|" , gbk_buf);
			fprintf(stdout , "\t%-32s\t|" , gbk_buf);
			
			tc_strcpy(utf_buf,signoff_info[i].operate.c_str());
			UTFToGBK(utf_buf,512,gbk_buf,512);
			fprintf(fp , "\t%-16s\t|" , gbk_buf);
			fprintf(stdout , "\t%-16s\t|" , gbk_buf);
			
			tc_strcpy(utf_buf,signoff_info[i].opinion.c_str());
			UTFToGBK(utf_buf,512,gbk_buf,512);
			fprintf(fp , "\t%-32s\n" , gbk_buf);
			fprintf(stdout , "\t%-32s\n" , gbk_buf);
			
		}

		fflush(fp);
        	fclose(fp);

		return ifail;
	}


	////字符转编码
	int code_convert(char *from_charset,char *to_charset,char *inbuf, size_t inlen,char *outbuf, size_t outlen)
	{
		/* iconv_t cd;
		 char **pin = &inbuf;
		 char **pout = &outbuf;
		 cd = iconv_open(to_charset,from_charset);
		 if (cd==0) 
		      return -1;
		 memset(outbuf,0,outlen);
			 
		 if (iconv(cd, pin, &inlen,pout, &outlen)==-1)
		     return -1;
		 iconv_close(cd);*/
		 return 0;
	}


	//UTF-8 to GB2312
	int UTFToGBK(char *inbuf, size_t inlen, char *outbuf, size_t outlen)
	{
		 return code_convert("UTF-8","GB2312",inbuf,inlen,outbuf,outlen);
	}

#ifdef __cplusplus
}
#endif
