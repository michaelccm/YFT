/*=============================================================================
                Copyright (c) 2003-2005 UGS Corporation
                   Unpublished - All Rights Reserved

    File   : yfjc_transfer_timesheetentry.cxx
    Module : user_exits
    
    SP-CUS-002 TimesheetEntry

	yfjc_transfer_timesheetentry

	Action Handler
	-debug	true , false	//

============================================================================================================
DATE           Name             Description of Change
1_ARG_2014    mengyawei          creation
$HISTORY$

============================================================================================================*/
#include <tc/tc.h>
#include <epm/epm.h>
#include <epm/epm_task_template_itk.h>
#include <tccore/aom.h>
#include <tccore/aom_prop.h>
#include <tccore/tctype.h>
#include <tccore/workspaceobject.h>
#include <pom/pom/pom.h>

#include <sa/groupmember.h>
#include <sa/user.h>

#include <map>
#include <vector>
#include <string>

#include <stdio.h>
#include <stdlib.h>


#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
#include "yfjc_time.h"

using namespace std;

#ifdef __cplusplus
extern "C" {
#endif


#define EXTWORKDAYHOURFORM	"JCI6_ExtWDHrForm"		
#define EXTWORKDAYHOUR		"JCI6_ExtWorkDayHr"		
#define EXTWORKPROCESSPRE	"YFJC_transfer_tms_wf"	
#define PROCESSNAME			"PROCESS"				
#define PROCESSDESC			"PROCESSDESC"			
#define TIMESHEETTYPE		"TimeSheetEntry"
#define BILL_TYPE_NORMAL	"Normal Hours"
#define BILL_TYPE_NOT_COUNT	"Non-billable Hours"
#define CostInfo_QRY			"YFJC_SearchCostInfo"	//
#define CostInfo_QRYSN			"YFJC_SearchCostInfobySN"

#define External_object_link	"IMAN_external_object_link"


extern int POM_AM__set_application_bypass(logical bypass);

int get_rate(logical is_debug , tag_t *rate0 , tag_t * rate1);
int create_process(logical is_debug , char * process_name , tag_t resp_party , tag_t reference , vector<tag_t> targets);
int assign_task_resp_party(logical is_debug , tag_t task , tag_t resp_party );
int base_on_workday_create_timesheetentry( logical is_debug , tag_t daywork , tag_t rate0 , tag_t rate1 , map<tag_t , vector<tag_t> >& schedule_timesheet_map);
int delete_cost_info( logical is_debug , tag_t cost_info );
int search_cost_info(logical is_debug , int year , char * user_id , char * division_name , char * section_name ,vector<tag_t >& cost_info);

	int yfjc_transfer_timesheetentry(EPM_action_message_t msg)
	{
		int ifail = ITK_ok;

		//handler argument
		int arg_cnt = 0; 
		char * arg_str = NULL;
		char * para_name = NULL;
		char * para_value = NULL;

		logical over_due = FALSE;
	
		char account_day[32] = {'\0'};		//-account_day
		int ccnt_day = 0;
		logical is_debug = FALSE;

		//target attachment
		tag_t root_task = NULLTAG;

		int att_cnt = 0;
		tag_t * atts = NULL;

		tag_t tmp_att = NULLTAG;
		char * tmp_att_class_name = NULL;
		char tmp_att_name[WSO_name_size_c + 1] = {'\0'};
		int tmp_att_sta_cnt = 0;
		tag_t * tmp_att_stautus = NULL;

		date_t nowDate ;

		//process
		char process_name[256] = {'\0'};
		int pre_cnt = 0;
		char ** pre_values = NULL;

		tag_t groupmember = NULLTAG;
		tag_t resp_party = NULLTAG;

		int workday_cnt = 0;
		tag_t * workdays = NULL;
		tag_t tmp_workday = NULLTAG;

		//for
		int i = 0;
		int j = 0;

		vector<tag_t> form_vec;

		tag_t tmp_form = NULLTAG;

		map<tag_t ,vector<tag_t> > schedule_timesheet_map;

		tag_t rate0 = NULLTAG;
		tag_t rate1 = NULLTAG;

		//error
		char tmp_error[128] = {'\0'};
		map<string , int>error_map;

		//
		int tmp_year = 0;
		int tmp_month = 0;

		TC_write_syslog("-----------------------yfjc_transfer_timesheetentry started----------------------\n");

		arg_cnt = TC_number_of_arguments(msg.arguments);
		for(i = 0; i < arg_cnt ; i ++)
		{
			arg_str = TC_next_argument(msg.arguments);
			ITKCALL(ITK_ask_argument_named_value(arg_str , &para_name , &para_value));
			if(!tc_strcasecmp(para_name,"account_day"))
			{
				if(tc_strlen(para_value)==2)
				{
					tc_strcpy(account_day,para_value);
				}
			}
			else
				if(!tc_strcasecmp(para_name , "debug"))
			{
				if(!tc_strcasecmp(para_value , "true"))
				{
					is_debug = TRUE;
					TC_write_syslog("	Debug ....\n");
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

		if(tc_strlen(account_day)==0)
		{
			sprintf(tmp_error , "未配置参数account_day或不是两位数，请配置该参数！");
			string err_str;
			err_str.assign(tmp_error);
			if(is_debug)
				TC_write_syslog("%s\n",err_str.c_str());
			error_map.insert( pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
		}
		else
		{
			ccnt_day = atoi(account_day);
			if(ccnt_day!=0)
			{
				getNowTime( &nowDate );
				
			
				PREF_ask_char_values( EXTWORKPROCESSPRE, &pre_cnt , &pre_values );
				if(pre_values == NULL)
				{
					sprintf(tmp_error , "首选项：%s 未定义或未设置值，请联系系统管理员!",EXTWORKPROCESSPRE);
					string err_str;
					err_str.assign(tmp_error);
					if(is_debug)
						TC_write_syslog("%s\n",err_str.c_str());
					error_map.insert(pair< string , int >( err_str , ERROR_HANDLER_PRE_NOT_FOUND ));
				}
				else
				{
					tc_strcpy(process_name , pre_values[0]);
					if(is_debug)
						TC_write_syslog("process_name = %s\n",process_name);

					ITKCALL(EPM_ask_root_task(msg.task , &root_task));
					ITKCALL(EPM_ask_attachments(root_task , EPM_target_attachment , &att_cnt , &atts));
					for(i = 0; i < att_cnt; i ++)
					{
						tmp_att = atts[i];
						ITKCALL(AOM_ask_value_string( tmp_att , "object_type" , &tmp_att_class_name ));
						if(!tc_strcmp(tmp_att_class_name , EXTWORKDAYHOURFORM))
						{

							ITKCALL(WSOM_ask_name( tmp_att, tmp_att_name ));
							ITKCALL(WSOM_ask_release_status_list( tmp_att , &tmp_att_sta_cnt , &tmp_att_stautus ));
							if(tmp_att_stautus)
							{
								MEM_free(tmp_att_stautus);
								tmp_att_stautus = NULL;
							}
							if(tmp_att_sta_cnt>0)
							{
								sprintf(tmp_error , "流程目标对象中的外包工作日工时表单: %s 已经发布!",tmp_att_name);
								string err_str;
								err_str.assign(tmp_error);
								if(is_debug)
									TC_write_syslog("%s\n",err_str.c_str());
								error_map.insert(pair< string , int >( err_str , ERROR_FORM_ALREAD_RELEASED ));
							}
							else
							{
								if(is_debug)
									TC_write_syslog("流程目标对象中的外包工作日工时表单: %s 未发布！",tmp_att_name);
								form_vec.push_back(tmp_att);
							}
						}
						if(tmp_att_class_name)
						{
							MEM_free(tmp_att_class_name);
							tmp_att_class_name = NULL;
						}
					}

					get_rate(is_debug ,&rate0 ,&rate1);
					if((rate0 == NULLTAG)||(rate1 == NULLTAG))
					{
						sprintf(tmp_error,"未在 infodba的 home目录下RATE文件夹中找到x0,x1，请联系系统管理员！");
						string err_str;
						err_str.assign(tmp_error);
						if(is_debug)
							TC_write_syslog("%s\n",err_str.c_str());
						error_map.insert(pair<string , int>(err_str , ERROR_FIND_NO_RATE));
					}

					if((form_vec.size()==0)&&(error_map.size()==0))
					{
						sprintf(tmp_error , "流程目标对象中没有外包工作日工时表单!");
						string err_str;
						err_str.assign(tmp_error);
						if(is_debug)
							TC_write_syslog("%s\n",err_str.c_str());
						error_map.insert( pair<string , int >( err_str , ERROR_HAS_NO_FORM ));
					}
					else
						if((form_vec.size()>0)&&(error_map.size()==0))
					{
						for(i = 0; i < form_vec.size(); i ++)
						{
							over_due  = FALSE;
							tmp_form = form_vec[i];
							ITKCALL(AOM_ask_value_tags( tmp_form , "jci6_ExtHourArray" , &workday_cnt , &workdays ));
							ITKCALL(WSOM_ask_name( tmp_form, tmp_att_name ));
							if(is_debug)
								TC_write_syslog("外包工作日工时表单：%s 下挂了 %d 个 外包工作日工时对象!\n",tmp_att_name,workday_cnt);
							ITKCALL(AOM_ask_value_int(workdays[0] , "jci6_Year" , &tmp_year ));//
							ITKCALL(AOM_ask_value_int(workdays[0] , "jci6_Month" , &tmp_month ));
							if(is_debug)
							{
								TC_write_syslog("nowDate = %4d-%02d-%02d %02d:%02d:%02d\n",nowDate.year , nowDate.month , nowDate.day , nowDate.hour , nowDate.minute , nowDate.second );
								TC_write_syslog("-->	tmp_year = %d	tmp_month = %d\n",tmp_year , tmp_month);
							}
							if( tmp_year == nowDate.year )
							{
								if(nowDate.day <= ccnt_day )
								{
									if((nowDate.month == tmp_month)||((nowDate.month+1) == tmp_month ))
									{
										over_due = FALSE;
									}
									else
									{
										over_due = TRUE;
										break;
									}
								}else
								{
									if((nowDate.month+1) != tmp_month)
									{
										//
										over_due = TRUE;
										break;
									}
								}
							}
							else if( ( tmp_year + 1 ) == nowDate.year )
							{
								if( ( nowDate.month == 0 )&&( tmp_month == 12 ) && ( nowDate.day <= ccnt_day ) )//如果当前为1月
								{
									over_due = FALSE;
								}else
								{
									over_due = TRUE;
								}
							}
							

							for(j = 0; j < workday_cnt ; j ++ )
							{
								tmp_workday = workdays[j];
								base_on_workday_create_timesheetentry( is_debug , tmp_workday ,rate0 ,rate1 , schedule_timesheet_map);

							}
							if(workdays)
							{
								MEM_free(workdays);
								workdays = NULL;
							}
						}
						if(over_due)
						{
							sprintf(tmp_error,"外包工作日工时表单已过期，请联系系统管理员！");
							string err_str;
							err_str.assign(tmp_error);
							if(is_debug)
								TC_write_syslog("%s\n",err_str.c_str());
							error_map.insert(pair<string , int>(err_str , ERROR_ALREAD_PASS_ACCOUNT_DATE));
						}
						else
						{
							ITKCALL(SA_ask_current_groupmember(&groupmember));
							ITKCALL(SA_ask_groupmember_user(groupmember , &resp_party));
							map<tag_t , vector<tag_t> >::iterator map_ite;
							for(map_ite = schedule_timesheet_map.begin(); map_ite!=schedule_timesheet_map.end(); map_ite++ )
							{
								fprintf(stdout,"map_ite->second.size() = %d\n",map_ite->second.size());
								tag_t schedule = map_ite->first;
								vector<tag_t> tmp_target;
								tmp_target.assign( map_ite->second.begin(),map_ite->second.end() );
								create_process(is_debug , process_name , resp_party , schedule , tmp_target );
							}	
						}
						
					}

					if(atts)
					{
						MEM_free(atts);
						atts = NULL;
					}
				}
			}
			else
			{
				sprintf(tmp_error , "参数account_day的值必须为数字且不为0，请配置该参数！");
				string err_str;
				err_str.assign(tmp_error);
				if(is_debug)
					TC_write_syslog("%s\n",err_str.c_str());
				error_map.insert( pair<string , int>(err_str , ERROR_HANDLER_ARG_NOT_DEFINE));
			}	
		}

		map<string , int>::iterator error_map_ite;
		for(error_map_ite = error_map.begin(); error_map_ite != error_map.end(); error_map_ite ++)
		{
			ifail = CR_error_in_handler;
			if(is_debug)
				TC_write_syslog("--->		%d :	%s\n" , error_map_ite->second , error_map_ite->first.c_str() );
			EMH_store_error_s1( EMH_severity_error, error_map_ite->second , error_map_ite->first.c_str() );
		}
		TC_write_syslog("------------------------yfjc_transfer_timesheetentry ended-----------------------\n");
		return ifail;
	}


	int get_rate(logical is_debug , tag_t *rate0 , tag_t * rate1)
	{
		int ifail = ITK_ok;

		tag_t user = NULLTAG;
		tag_t home_folder = NULLTAG;
		int ref_cnt = 0;
		tag_t * refs = NULL;

		tag_t tmp_ref = NULLTAG;
		tag_t ref_object_type = NULLTAG;
		char ref_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

		char ref_name[WSO_name_size_c + 1] = {'\0'};

		int i = 0;
		int j = 0;

		int rate_cnt = 0;
		tag_t * rates = NULL;
		tag_t tmp_rate = NULL;
		char rate_name[WSO_name_size_c + 1] = {'\0'};

		POM_AM__set_application_bypass(TRUE);

		ITKCALL(SA_find_user("infodba" , &user));
		ITKCALL(SA_ask_user_home_folder(user , &home_folder));

		ITKCALL(FL_ask_references( home_folder , FL_fsc_no_order , &ref_cnt , &refs ));
		for( i = 0; i < ref_cnt ; i ++)
		{
			tmp_ref = refs[i];
			ITKCALL(TCTYPE_ask_object_type( tmp_ref , &ref_object_type ));
			ITKCALL(TCTYPE_ask_class_name( ref_object_type , ref_class_name ));
			ITKCALL(WSOM_ask_name( tmp_ref , ref_name ));
			if(is_debug)
				TC_write_syslog("name = %s		type = %s \n", ref_name , ref_class_name );
			if((!tc_strcmp(ref_class_name,"Folder"))&&(!tc_strcmp(ref_name,"RATE")))
			{
				if(is_debug)
					TC_write_syslog("---->		找到 存放  RATE 的 文件夹 ！\n");
				ITKCALL(FL_ask_references(tmp_ref , FL_fsc_no_order , &rate_cnt , &rates ));
				for( j = 0; j < rate_cnt ; j ++)
				{
					tmp_rate = rates[j];
					ITKCALL(WSOM_ask_name( tmp_rate , rate_name ));
					if(is_debug)
						TC_write_syslog("---->		rate_name = %s\n", rate_name );
					if(!tc_strcmp(rate_name , "x1"))
					{
						if(is_debug)
							TC_write_syslog("找到 x1 rate !\n");
						*rate1 = tmp_rate;
					}else if(!tc_strcmp(rate_name , "x0"))
					{
						if(is_debug)
							TC_write_syslog("找到 x0 rate !\n");
						*rate0 = tmp_rate;
					}
				}
				if((*rate1!=NULL)&&(*rate0!=NULL))
					break;
				if(rates)
				{
					MEM_free(rates);
					rates = NULL;
				}
			}
		}
		if(refs)
		{
			MEM_free(refs);
			refs = NULL;
		}

		POM_AM__set_application_bypass(FALSE);

		return ifail;
	}


	int base_on_workday_create_timesheetentry( logical is_debug , tag_t daywork , tag_t rate0 , tag_t rate1 ,map<tag_t , vector<tag_t> >& schedule_timesheet_map)
	{
		int ifail = ITK_ok;

		char month[12][32] = {
			"jci6_Jan","jci6_Feb","jci6_Mar",
			"jci6_Apr","jci6_May","jci6_Jun",
			"jci6_Jul","jci6_Aug","jci6_Sep",
			"jci6_Oct","jci6_Nov","jci6_Dec"
		};

		vector<tag_t> cost_info_vec ;
		int i = 0;
		tag_t cost_info = NULLTAG;
		
		int daywork_year = 0;
		int daywork_month = 0;
		tag_t daywork_ownProxy = NULLTAG;
		double daywork_hour = 0.00;
		double daywork_extra_hour = 0.00;
		tag_t daywork_task = NULLTAG;
		tag_t daywork_division = NULLTAG;
		tag_t daywork_section = NULLTAG;

		char * user_id = NULL;
		char * division_name = NULL;
		char * section_name = NULL;

		char * daywork_task_name = NULL;

		date_t timesheet_date_value;
		int timesheet_minute_value = 0;

		tag_t time_sheet_type = NULLTAG;
		tag_t time_sheet = NULLTAG;
		char time_sheet_name[256] = {'\0'};
		char time_sheet_desc[128] = {"SummaryReportExtHours"};
		tag_t input_tag = NULLTAG;
		//const char ** time_sheet_name_p = (const char **)MEM_alloc(sizeof(char *)*1);
		char ** time_sheet_name_p = (char **)MEM_alloc(sizeof(char *)*1);
		time_sheet_name_p[0] = time_sheet_name;
		//const char ** time_sheet_desc_p = (const char **)MEM_alloc(sizeof(char *)*1);
		char ** time_sheet_desc_p = (char **)MEM_alloc(sizeof(char *)*1);
		time_sheet_desc_p[0] = time_sheet_desc;
		//const char ** time_sheet_date_p = (const char **)MEM_alloc(sizeof(char *)*1);
		char ** time_sheet_date_p = (char **)MEM_alloc(sizeof(char *)*1);
		char * time_sheet_date = NULL;


		fprintf(stdout , "---------------------base_on_workday_create_timesheetentry strated---------------------\n" );

		if(is_debug)
			TC_write_syslog("---------------------base_on_workday_create_timesheetentry strated---------------------\n");


		
		ITKCALL(AOM_ask_value_int( daywork , "jci6_Year" , &daywork_year ));
		ITKCALL(AOM_ask_value_int( daywork , "jci6_Month" , &daywork_month ));
		ITKCALL(AOM_ask_value_tag( daywork , "jci6_ownProxy" , &daywork_ownProxy ));
		ITKCALL(AOM_ask_value_double( daywork , "jci6_Hour" , &daywork_hour ));
		ITKCALL(AOM_ask_value_double( daywork , "jci6_ExtraHour" , &daywork_extra_hour ));
		ITKCALL(AOM_ask_value_tag( daywork , "jci6_Task" , &daywork_task ));
		
		ITKCALL(AOM_ask_value_tag( daywork , "jci6_Division" , &daywork_division ));
		ITKCALL(AOM_ask_value_tag( daywork , "jci6_Section" , &daywork_section ));

		if((daywork_year == 0)||(daywork_month == 0)||(daywork_ownProxy == NULLTAG)
			||(daywork_task == NULLTAG)||(daywork_division == NULLTAG))
		{
			if(is_debug)
				TC_write_syslog("(daywork_year == 0)||(daywork_month == 0)||(daywork_ownProxy == NULLTAG)||(daywork_task == NULLTAG)||(daywork_division == NULLTAG)\n");
		}
		else
		{

			ITKCALL(AOM_ask_value_string( daywork_ownProxy , "user_id" , &user_id ));
			ITKCALL(SA_ask_group_display_name( daywork_division , &division_name));
			if(daywork_section!=NULLTAG)
			{
				ITKCALL(SA_ask_group_display_name( daywork_section , &section_name));
			}
			else
			{
				section_name = (char *) MEM_alloc(sizeof(char)*64);
				tc_strcpy(section_name,"*");
			}
			fprintf(stdout , "---->	user_id = %s	division = %s	section = %s \n" , user_id , division_name , daywork_section );
			TC_write_syslog("---->	user_id = %s	division = %s	section = %s \n" , user_id , division_name , section_name );
			if(daywork_month == 12)//jci6_Month 
			{
				search_cost_info(is_debug , daywork_year-1 , user_id , division_name , section_name ,cost_info_vec);
			}
			else
			{
				search_cost_info(is_debug , daywork_year , user_id , division_name , section_name ,cost_info_vec);
			}
			

			for( i  = 0; i < cost_info_vec.size(); i ++)
			{
				cost_info = cost_info_vec[i];
				if(daywork_month != 12)
				{
					ITKCALL(AOM_lock(cost_info));
					ITKCALL(AOM_set_value_double( cost_info , month[daywork_month-1] , 0.00 ));
					ITKCALL(AOM_save(cost_info));
					ITKCALL(AOM_unlock(cost_info));
				}
				else
				{
					delete_cost_info(is_debug , cost_info);
				}
			}

			ITKCALL(AOM_ask_value_string(daywork_task , "object_name" , &daywork_task_name ));
			sprintf(time_sheet_name,"%s:TSE",daywork_task_name);
			if(is_debug)
			{
				TC_write_syslog("daywork_task . object_name = %s\n",daywork_task_name );
				TC_write_syslog(" TimeSheetEntry . object_name = %s \n",time_sheet_name);
				fprintf(stdout , "daywork_task . object_name = %s\n",daywork_task_name );
				fprintf(stdout , " TimeSheetEntry . object_name = %s \n",time_sheet_name);
			}

			timesheet_date_value.year = daywork_year;
			timesheet_date_value.month = daywork_month-1;
			timesheet_date_value.day = 15;
			timesheet_date_value.hour = 0;
			timesheet_date_value.minute = 0;
			timesheet_date_value.second = 0;
			ITKCALL(ITK_date_to_string( timesheet_date_value , &time_sheet_date ));
				
			time_sheet_date_p[0] = time_sheet_date;

			if(abs(daywork_hour) > 1e-15)
			{
				fprintf(stdout , "daywork_hour > 1e -15\n");
				if(is_debug)
					TC_write_syslog("daywork_hour > 1e -15\n");

				timesheet_minute_value = daywork_hour*60;
				if(is_debug)
					TC_write_syslog("daywork_hour = %lf		timesheet_minute_value = %d\n",daywork_hour , timesheet_minute_value);
				fprintf(stdout , "daywork_hour = %lf		timesheet_minute_value = %d\n",daywork_hour , timesheet_minute_value);

				//POM_AM__set_application_bypass(TRUE);
				ITKCALL(TCTYPE_find_type(TIMESHEETTYPE,TIMESHEETTYPE,&time_sheet_type));
				ITKCALL(TCTYPE_construct_create_input(time_sheet_type,&input_tag));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "object_name" , 1 , (const char **)time_sheet_name_p));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "object_desc" , 1 , (const char **) time_sheet_desc_p));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "date" , 1 , (const char **)time_sheet_date_p));
				ITKCALL(TCTYPE_create_object( input_tag , &time_sheet ));
				ITKCALL(AOM_save(time_sheet));
				
				ITKCALL(AOM_lock( time_sheet ));
				ITKCALL(AOM_load( time_sheet ));
				ITKCALL(POM_set_owning_group( time_sheet , daywork_division ));
				ITKCALL(POM_set_owning_user ( time_sheet , daywork_ownProxy ));
				//ITKCALL(AOM_set_value_tag( time_sheet , "owning_user" , daywork_ownProxy ));
				//ITKCALL(AOM_set_value_tag( time_sheet , "owning_group" , daywork_division ));
				ITKCALL(AOM_set_value_tag( time_sheet , "user_tag" , daywork_ownProxy ));
				ITKCALL(AOM_set_value_int( time_sheet , "minutes" , timesheet_minute_value ));
				ITKCALL(AOM_set_value_string( time_sheet , "bill_type" , BILL_TYPE_NORMAL ));
				ITKCALL(AOM_set_value_tag( time_sheet , "billrate_tag" , rate1));
				ITKCALL(AOM_set_value_tag( time_sheet , "scheduletask_tag" , daywork_task ));

				ITKCALL(AOM_save ( time_sheet ));
				ITKCALL(AOM_unlock( time_sheet ));
				//POM_AM__set_application_bypass(FALSE);
				

				map<tag_t ,vector<tag_t> >::iterator map_ite ;
				map_ite = schedule_timesheet_map.find(daywork_task);
				if(map_ite == schedule_timesheet_map.end())
				{
					vector<tag_t> targets;
					//targets.push_back(daywork_task);
					targets.push_back(time_sheet);
					fprintf(stdout,"---->		targets.size() = %d\n",targets.size());

					schedule_timesheet_map.insert(pair<tag_t , vector<tag_t> >(daywork_task , targets));
				}
				else
				{
					map_ite->second.push_back(time_sheet);
				}
			}

			if(abs(daywork_extra_hour) > 1e-15)
			{
				
				fprintf(stdout , "daywork_extra_hour > 1e -15\n" );
				if(is_debug)
					TC_write_syslog("daywork_extra_hour > 1e -15\n");
				timesheet_minute_value = daywork_extra_hour*60;
				if(is_debug)
					TC_write_syslog("daywork_hour = %lf		timesheet_minute_value = %d\n",daywork_extra_hour , timesheet_minute_value);
				//POM_AM__set_application_bypass(TRUE);
				ITKCALL(TCTYPE_find_type(TIMESHEETTYPE,TIMESHEETTYPE,&time_sheet_type));
				ITKCALL(TCTYPE_construct_create_input(time_sheet_type,&input_tag));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "object_name" , 1 , (const char **) time_sheet_name_p));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "object_desc" , 1 , (const char **)time_sheet_desc_p));
				ITKCALL(TCTYPE_set_create_display_value(input_tag , "date" , 1 , (const char **)time_sheet_date_p));
				ITKCALL(TCTYPE_create_object( input_tag , &time_sheet ));
				ITKCALL(AOM_save ( time_sheet ));

				ITKCALL(AOM_lock( time_sheet ));
				ITKCALL(AOM_load( time_sheet ));
				ITKCALL(POM_set_owning_group( time_sheet , daywork_division ));
				ITKCALL(POM_set_owning_user ( time_sheet , daywork_ownProxy ));
				//ITKCALL(AOM_set_value_tag( time_sheet , "owning_user" , daywork_ownProxy ));
				//ITKCALL(AOM_set_value_tag( time_sheet , "owning_group" , daywork_division ));
				ITKCALL(AOM_set_value_tag( time_sheet , "user_tag" , daywork_ownProxy ));
				ITKCALL(AOM_set_value_int( time_sheet , "minutes" , timesheet_minute_value ));
				ITKCALL(AOM_set_value_string( time_sheet , "bill_type" , BILL_TYPE_NOT_COUNT ));
				ITKCALL(AOM_set_value_tag( time_sheet , "billrate_tag" , rate0 ));
				ITKCALL(AOM_set_value_tag( time_sheet , "scheduletask_tag" , daywork_task ));

				ITKCALL(AOM_save ( time_sheet ));
				ITKCALL(AOM_unlock( time_sheet ));
				//POM_AM__set_application_bypass(FALSE);

				fprintf(stdout , "---------map  started----------------\n");

				map<tag_t ,vector<tag_t> >::iterator map_ite ;
				map_ite = schedule_timesheet_map.find(daywork_task);
				if(map_ite == schedule_timesheet_map.end())
				{
					vector<tag_t> targets;
					//targets.push_back(daywork_task);
					targets.push_back(time_sheet);

					schedule_timesheet_map.insert(pair<tag_t , vector<tag_t> >(daywork_task , targets));
				}
				else
				{
					map_ite->second.push_back(time_sheet);
				}
				fprintf(stdout , "---------map  ended----------------\n");
			}
			if(time_sheet_date)
			{
				TC_write_syslog( "------------>			MEM_free(time_sheet_date)\n");
				MEM_free(time_sheet_date);
				time_sheet_date = NULL;
			}
			if(user_id)
			{
				fprintf(stdout , "------------>			MEM_free(user_id)\n");
				MEM_free(user_id);
				user_id = NULL;
			}
			if(division_name)
			{
				TC_write_syslog( "------------>			MEM_free(division_name)\n");
				MEM_free(division_name);
				division_name = NULL;
			}
			if(daywork_task_name)
			{
				TC_write_syslog( "------------>			MEM_free(daywork_task_name)\n");
				MEM_free(daywork_task_name);
				daywork_task_name = NULL;
			}
			if(section_name)
			{
				TC_write_syslog( "------------>			MEM_free(section_name)\n");
				MEM_free(section_name);
				section_name = NULL;
			}
			
		}

		if(time_sheet_name_p)
		{
			TC_write_syslog( "------------>			MEM_free(time_sheet_name_p)\n");
			MEM_free(time_sheet_name_p);
			time_sheet_name_p = NULL;
		}
		if(time_sheet_desc_p)
		{
			TC_write_syslog( "------------>			MEM_free(time_sheet_desc_p)\n");
			MEM_free(time_sheet_desc_p);
			time_sheet_desc_p = NULL;
		}
		if(time_sheet_date_p)
		{
			TC_write_syslog( "------------>			MEM_free(time_sheet_date_p)\n");
			MEM_free(time_sheet_date_p);
			time_sheet_date_p = NULL;
		}
		

		if(is_debug)
			TC_write_syslog("---------------------base_on_workday_create_timesheetentry ended---------------------\n");

		fprintf(stdout , "---------------------base_on_workday_create_timesheetentry ended---------------------\n" );

		return ifail;
	}

	int create_process(logical is_debug , char * process_name , tag_t resp_party , tag_t reference , vector<tag_t> targets)
	{
		int ifail = ITK_ok;
		tag_t process_template = NULLTAG;
		tag_t process = NULLTAG;
		tag_t root_task = NULLTAG;
		int target_type = EPM_target_attachment;
		int reference_type = EPM_reference_attachment;
		int i = 0;
		tag_t schedule_task = reference;
		tag_t schedule = NULLTAG;
		const int att_cnt = targets.size()+2;
		tag_t * atts = (tag_t *)MEM_alloc(sizeof(tag_t)*att_cnt);
		int * att_type = (int *)MEM_alloc(sizeof(int)*att_cnt);

		if(is_debug)
			TC_write_syslog("-------------------create_process started-------------------\n");


		fprintf(stdout,"====>		att_cnt = %d \n",att_cnt);
		ITKCALL(AOM_ask_value_tag(schedule_task , "schedule_tag" , &schedule ));
		atts[0] = reference;
		atts[1] = schedule;
		att_type[0] = EPM_reference_attachment;
		att_type[1] = EPM_reference_attachment;
		for(i = 0; i < targets.size(); i ++)
		{
			atts[i+2] = targets[i];
			att_type[i+2] = EPM_target_attachment;
		}

		ITKCALL(EPM_find_process_template( process_name ,&process_template));
		ITKCALL(EPM_create_process( PROCESSNAME , PROCESSDESC ,process_template , att_cnt , atts , att_type ,&process ));


		//ITKCALL(EPM_ask_root_task( process , &root_task ));

		//ITKCALL(EPM_add_attachments( root_task , att_cnt , atts , &target_type ));
		
		//ITKCALL(EPM_add_attachments( root_task , 1 , &schedule , &reference_type ));
		

		//assign_task_resp_party( is_debug , root_task ,  resp_party );

		if(atts)
		{
			MEM_free(atts);
			atts = NULL;
		}
		if(att_type)
		{
			MEM_free(att_type);
			att_type = NULL;
		}

		if(is_debug)
			TC_write_syslog("-------------------create_process ended-------------------\n");

		return ifail;
	}

	int assign_task_resp_party(logical is_debug , tag_t task , tag_t resp_party )
	{
		int ifail = ITK_ok;

		int sub_cnt = 0; 
		tag_t * subs = NULL;
		tag_t tmp_sub = NULLTAG;

		int i = 0;

		if(is_debug)
			TC_write_syslog("-------------------assign_task_resp_party started-------------------\n");


		ITKCALL(EPM_assign_responsible_party( task , resp_party ));

		ITKCALL(EPM_ask_sub_tasks( task , &sub_cnt , &subs));

		for( i = 0; i < sub_cnt ; i ++)
		{
			tmp_sub = subs[i];
			assign_task_resp_party( is_debug , tmp_sub , resp_party );
		}

		if(subs)
		{
			MEM_free(subs);
		}


		if(is_debug)
			TC_write_syslog("-------------------assign_task_resp_party ended-------------------\n");

		return ifail;
	}

	int delete_cost_info( logical is_debug , tag_t cost_info )
	{
		int ifail = ITK_ok;

		tag_t relation_type = NULLTAG;
		tag_t relation = NULLTAG;

		int primary_cnt = 0;
		tag_t * primary_objects = NULL;
		tag_t tmp_primary = NULLTAG;

		tag_t object_type = NULLTAG;
		char class_name[TCTYPE_class_name_size_c+1] = {'\0'};

		int i = 0;
		
		fprintf(stdout , "==========delete_cost_info===========\n");

		ITKCALL(GRM_find_relation_type(External_object_link , &relation_type));
		ITKCALL(GRM_list_primary_objects_only(cost_info , relation_type , &primary_cnt , &primary_objects));
		for(i = 0; i < primary_cnt; i ++)
		{
			tmp_primary = primary_objects[i];
			ITKCALL(TCTYPE_ask_object_type( tmp_primary , &object_type));
			ITKCALL(TCTYPE_ask_class_name( object_type , class_name));
			fprintf(stdout,"--->	class_name = %s\n", class_name );
			ITKCALL(GRM_find_relation( tmp_primary , cost_info , relation_type , &relation ));
			ITKCALL(GRM_delete_relation( relation ));
		}
	
		ITKCALL(POM_delete_instances(1,&cost_info));
		if(primary_objects)
		{
			MEM_free(primary_objects);
			primary_objects = NULL;
		}


		return ifail;
	}


	int search_cost_info(logical is_debug , int year , char * user_id , char * division_name , char * section_name ,vector<tag_t >& cost_info)
	{
		int ifail = ITK_ok;

		//qry
		tag_t qry = NULLTAG;

		int num_clauses = 0;
		char ** entry_names = NULL;
		char ** values = NULL;

		char ** qry_entry = NULL;
		char ** qry_values = NULL;

		int num_found = 0;
		tag_t * results = NULL;


		//for
		int i = 0;



		fprintf(stdout , "============search_cost_info============\n");
		fprintf(stdout , " year = %d		user_id = %s	division_name = %s		section_name = %s\n",year , user_id , division_name , section_name );
		if(!tc_strcmp(section_name , "*"))
		{
			ITKCALL(QRY_find( CostInfo_QRYSN , &qry ));
		}
		else
		{
			ITKCALL(QRY_find( CostInfo_QRY , &qry ));
		}
		
		ITKCALL(QRY_find_user_entries( qry , &num_clauses , &entry_names ,&values));
			
			qry_entry = (char **)MEM_alloc(sizeof(char *)*num_clauses);
			qry_values = (char **)MEM_alloc(sizeof(char *)*num_clauses);
			//
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
					//*(int *)qry_values[0] = support_year;
					sprintf(qry_values[0],"%d",year);
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
					if(tc_strcmp(section_name , "*"))
					{
						tc_strcpy(qry_entry[5],entry_names[i]);
						tc_strcpy(qry_values[5],section_name);
					}

				}
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
			/*
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
			*/
			ITKCALL(QRY_execute( qry ,num_clauses, qry_entry ,qry_values , &num_found,&results)); 
			fprintf(stdout,"num_found = %d\n",num_found);
			if(is_debug)
				TC_write_syslog(" QRY_execute	num_found = %d\n",num_found);
			cost_info.clear();
			for(i = 0 ; i < num_found ; i ++)
			{
				cost_info.push_back(results[i]);
			}
			if(results)
			{
				MEM_free(results);
				results = NULL;
			}


		return ifail;
	}

#ifdef __cplusplus
}
#endif
