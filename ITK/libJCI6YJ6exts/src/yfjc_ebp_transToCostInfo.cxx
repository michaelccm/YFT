/*==========================================================================================================
Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
Unpublished - All Rights Reserved
============================================================================================================
File description:

Filename: yfjs_transToCostInfo.cxx
Module  : user_exits

synchronize users' info
============================================================================================================
DATE           Name             Description of Change
03-Mar-2013    liujm            creation
$HISTORY$
07-Mar-2013    liujm            modification
============================================================================================================*/
#include <vector>
#include <string>
#include "yfjc_ebp_head.h"    `			//dll或.so
#include "yfjc_ebp_custom_define.h"		//dll或.so
//#include "yfjc_ebp_transToCostInfo.h"//utilty
//下面所有的HANDLE_ERROR_S1     dll或.so也得释放出

#define  YFJC_EXT_USER_MAPPING_LIST   "YFJC_ext_user_mapping_list"//"YFJC_ext_user_mapping_list"

using namespace std;
tag_t base_tccalendar = NULLTAG;
//记录历史计算结果
map<string, int>* intervalOfDateMap;

//add 8-12
//void tr_ECHO(char *format, ...);//新增写日志
void tr_getUserPostion( char * user_id ,tag_t* postion );
//void getExtUser(vector<string> &extUserVec);
//logical isExtUser(char *jci6_User,vector<string> extUserVec,int extCnt);
void getExtUserByDiscipline(vector<tag_t>  &extUserVec);
void tr_ask_opt_debug();
logical tr_isExtUser(tag_t user,vector<tag_t> extUserVec,int extCnt);

void soa_createCostInfoByLabOrNon(double costNum,char* labourOrNon,char* costType, char* cpt, char* taskType, tag_t division, int year, tag_t section, tag_t rateLevel, tag_t user, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec,vector<tag_t> &costInfo_vec);
void fillEveryMonth(int year,date_t *nextjan1,date_t *dec1,date_t *nov1,date_t *oct1,date_t *sep1,date_t *aug1,date_t *jul1,date_t *jun1,date_t *may1,date_t *apr1,date_t *mar1,date_t *feb1,date_t *jan1);
void soa_createCostInfoByLabOrNon(double costNum,char* labourOrNon,char* costType, char* cpt, char* taskType, tag_t division, int year, tag_t section, tag_t rateLevel, tag_t user, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec,vector<tag_t> &costInfo_vec);
tag_t soa_createCostInfo(char * object_name,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, char* jci6_Unit, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec);
void soa_cycleScheduleTask( tag_t scheduleSummaryTask,  tag_t *schedulemembers, tag_t *resource_tags, int schedulemembersNum, vector<tag_t> extUserVec,int extCnt,vector<tag_t> &costInfo_vec);


logical isWriteLog = FALSE;
static char project_id[PROJ_id_size_c + 1]={'\0'};
//FILE* fileLog=NULL;

/**
*刷新某个项目的forecast数据(主要函数)
*/
int transToCostInfo(tag_t programInfoItem,vector<tag_t> extUserVec,int extCnt)
{
	int ifail             = ITK_ok,
		projectNum        = 0,
		preferred_ItemNum = 0;

	tag_t programInfoItemRev = NULLTAG,
          *project_lists      = NULL,
		  *preferred_Items    = NULL,
		  schedule            = NULLTAG,
		  scheduleSummaryTask = NULLTAG;
    char* jci6_Product = NULL;

	base_tccalendar = NULLTAG;
	intervalOfDateMap = new map<string, int>();

	tr_ECHO("info:programInfoItem==%u！2015-11-26\n",programInfoItem);
    /*ITKCALL(ITEM_ask_latest_rev(programInfoItem,&programInfoItemRev));
	ITKCALL(AOM_ask_value_string(programInfoItemRev,"jci6_Product",&jci6_Product));
    if(tc_strcmp(jci6_Product,"None Product")==0)
    {
        DOFREE(jci6_Product);
        ECHO("info:此项目属于非项目类项目！\n");
        return ifail;
    }
    DOFREE(jci6_Product);*/
	ITKCALL(AOM_ask_value_tags(programInfoItem,"project_list",&projectNum,&project_lists));

	if(projectNum>0)
	{
		tr_ECHO("projectNum===%d！\n",projectNum);
		ITKCALL(AOM_ask_value_tags(project_lists[0],"TC_Program_Preferred_Items",&preferred_ItemNum,&preferred_Items));
		ITKCALL(PROJ_ask_id(project_lists[0],project_id));
	}
	else
	{
		tr_ECHO("error:programInfoItem belongs project not exist！\n");
		 return ifail;
	}
	tr_ECHO("info:preferred_ItemNum===%d！\n",preferred_ItemNum);
	if(preferred_ItemNum>0)
	{
		for(int i=0;i<preferred_ItemNum;i++)
		{
			char object_type[WSO_name_size_c+1]={'\0'};
			ITKCALL(WSOM_ask_object_type(preferred_Items[i],object_type));
			tr_ECHO("object_type-->%s\n",object_type);
			if(tc_strcmp(object_type,"Schedule")==0)
			{
				schedule = preferred_Items[i];
				//modify by wuwei--sch_summary_task 
				ITKCALL(AOM_ask_value_tag(schedule,"fnd0SummaryTask",&scheduleSummaryTask));
				break;
			}
		}
		DOFREE(preferred_Items);
	}
	if(schedule==NULLTAG)
	{
		tr_ECHO("error:schedule==NULLTAG！\n");
		 return ifail;
	}
	tr_ECHO("info:schedule===%u！\n",schedule);
	tag_t relation_type = NULLTAG,
		  *acttach_list = NULL;
	int attch_count = 0;

	ITKCALL(GRM_find_relation_type("IMAN_external_object_link",&relation_type));
	ITKCALL(GRM_list_secondary_objects_only(programInfoItem,relation_type,&attch_count,&acttach_list));
	tr_ECHO("info:attch_count===%d！\n",attch_count);


	//2016-03-31	mengyawei modify
	 POM_AM__set_application_bypass(TRUE);
		
	tag_t current_groupmember_tag = NULLTAG;
	tag_t current_group = NULLTAG;
	tag_t current_user = NULLTAG;
	ITKCALL( SA_ask_current_groupmember( & current_groupmember_tag ) );
	ITKCALL( SA_ask_groupmember_group( current_groupmember_tag , &current_group ) );
	ITKCALL( SA_ask_groupmember_user( current_groupmember_tag , &current_user ) );

	if(attch_count>0)
	{
		
		for(int i=0;i<attch_count;i++)
		{
			char object_type[WSO_name_size_c+1];
			ITKCALL(WSOM_ask_object_type(acttach_list[i],object_type));
			if(tc_strcmp(object_type,"JCI6_CostInfo")==0)
			{
				char* jci6_CPT = NULL;
				ITKCALL(AOM_ask_value_string(acttach_list[i],"jci6_CPT",&jci6_CPT));
				if(tc_strcmp(jci6_CPT,"Forecast")==0)
				{
					tr_ECHO("------------wheather user in option--------------------\n");
					//得jci6_User的user_id
					tag_t JCI6_User = NULLTAG;
					ITKCALL(AOM_ask_value_tag(acttach_list[i],"jci6_User",&JCI6_User));
					if(JCI6_User != NULLTAG)
					{
						tr_ECHO("当前forecast的User not null\n");
						char *user_id = NULL;
						ITKCALL(AOM_ask_value_string(JCI6_User,"user_id",&user_id));
						if(user_id != NULL){
							if(tr_isExtUser(JCI6_User,extUserVec,extCnt))
							{
								tr_ECHO("当前forecast的jci6_User为%s,是外包代表,该forecast不需要删除\n",user_id);
								if(user_id != NULL)
								{
									MEM_free(user_id);
									user_id = NULL;
								}
								continue;
							}else
							{
								tr_ECHO("当前forecast的jci6_User为%s,",user_id);
							}
						}
						if(user_id != NULL)
						{
						    MEM_free(user_id);
							user_id = NULL;
						}
					}else
					{
					    tr_ECHO("当前forecast的User为空,");
					}
					tr_ECHO("不是外包代表,该forecast需要删除\n");
					
					tag_t relation = NULLTAG;
					tag_t costInfoTag = *(acttach_list+i);
					int ifail = ITK_ok;
                  // POM_AM__set_application_bypass(TRUE);

					//ITKCALL(ifail=GRM_find_relation(programInfoItem,costInfoTag,relation_type,&relation));
					//tr_ECHO(true,"relation==%u\n",relation);
					//ITKCALL(ifail=GRM_delete_relation(relation));
					char* user_name_string = NULL;
					tag_t user = NULLTAG;
					ITKCALL(POM_get_user( &user_name_string, &user ));
					logical verdict = false;
					ITKCALL(AM_check_users_privilege(user,costInfoTag,"DELETE",&verdict));
					if(verdict)
					{
						tr_ECHO("用户对该costinfo对象有删除权限\n");
					}
					else
					{
						tr_ECHO("用户对该costinfo对象没有删除权限\n");
					}
					//2016-03-31	mengyawei moidfy
					//current user has delete privilege to the costinfo 
					//but use function AOM_delete_from_parent failed
					//AOM_delete also failed 							

					ITKCALL( ifail = AOM_unlock( costInfoTag ) );
					tr_ECHO("AOM_unlock ==%d\n",ifail);
					ITKCALL( ifail = AOM_lock( costInfoTag ) );
					tr_ECHO("AOM_lock ==%d\n",ifail);
					ITKCALL( ifail = AOM_set_ownership( costInfoTag , current_user , current_group ) );
					tr_ECHO("AOM_set_ownership ==%d\n",ifail);
					ITKCALL( ifail = AOM_save( costInfoTag ) );
					tr_ECHO("AOM_save ==%d\n",ifail);
					ITKCALL( ifail = AOM_unlock( costInfoTag ) );
					tr_ECHO("AOM_unlock ==%d\n",ifail);
					
					ITKCALL(ifail = AOM_lock_for_delete(costInfoTag));
					tr_ECHO("251 ifail==%d\n",ifail);
					ITKCALL(ifail = AOM_delete_from_parent(costInfoTag,programInfoItem));
					tr_ECHO("253 ifail==%d\n",ifail);

/*
					tag_t tmp_relation = NULLTAG;
					ITKCALL( ifail = GRM_find_relation( programInfoItem , costInfoTag , relation_type , &tmp_relation  ) );
					tr_ECHO("GRM_find_relation == %d\n" , ifail );
					ITKCALL( ifail = GRM_delete_relation( tmp_relation ) );
					tr_ECHO("GRM_delete_relation == %d\n" , ifail );
					ITKCALL( ifail = AOM_lock_for_delete(costInfoTag));
					tr_ECHO("251 ifail==%d\n",ifail);
					ITKCALL( ifail = AOM_delete( costInfoTag ) );
					tr_ECHO(true,"253 ifail==%d\n",ifail);
					//2016-03-31	mengyawei modify
/*
					ITKCALL(ifail = AOM_lock_for_delete(costInfoTag));
					tr_ECHO("251 ifail==%d\n",ifail);
					ITKCALL(ifail = AOM_delete_from_parent(costInfoTag,programInfoItem));
					tr_ECHO("253 ifail==%d\n",ifail);
					if(ifail == ITK_ok)
                    {
						ITKCALL(AOM_delete(costInfoTag));
					}
					else
					{
						ITKCALL(ifail = AOM_refresh(costInfoTag,true));
						tr_ECHO("259 ifail==%d\n",ifail);
						ITKCALL(ifail = AOM_delete(costInfoTag));
						tr_ECHO("261 ifail==%d\n",ifail);
						char   *user_name = NULL,
                               *node_name = NULL;
                        date_t login_date = NULLDATE;
                            
                        ITKCALL(POM_who_locked_instance(costInfoTag,&user_name,&login_date,&node_name));
                        DOFREE(node_name);
						tr_ECHO("user_name==%s\n",user_name);
						POM_AM__set_application_bypass(FALSE);
					   HANDLE_ERROR_S1(ERROR_OTHER_USER_LOCK,user_name);
					} */
                   //  POM_AM__set_application_bypass(FALSE);
				}
				DOFREE(jci6_CPT);
			}
		}
		DOFREE(acttach_list);
	}
	int schedulemembersNum = 0;
	tag_t *schedulemembers = NULL;

	//modify by wuwei ----schedulemember_taglist   
	ITKCALL(AOM_ask_value_tags(schedule,"fnd0Schedulemember_taglist",&schedulemembersNum,&schedulemembers));
	tr_ECHO("info:schedulemembersNum===%d！\n",schedulemembersNum);
	vector<tag_t> costInfo_vec;
	if(schedulemembersNum>0)
	{
		tag_t *resource_tags = (tag_t*)MEM_alloc(schedulemembersNum* sizeof(tag_t));
		for (int i = 0; i < schedulemembersNum; i++)
		{
			ITKCALL(AOM_ask_value_tag(schedulemembers[i],"resource_tag",&resource_tags[i]));
		}
		soa_cycleScheduleTask(scheduleSummaryTask, schedulemembers, resource_tags,schedulemembersNum,extUserVec,extCnt,costInfo_vec);
		DOFREE(schedulemembers);
		DOFREE(resource_tags);
	}
	int costInfoNum = costInfo_vec.size();
	tr_ECHO("info:costInfoNum===%d！\n",costInfoNum);
	if(costInfoNum>0)
	{
		tag_t *costInfos =(tag_t*)MEM_alloc(costInfoNum * sizeof(tag_t));
		for(int j=0;j<costInfoNum;j++)
		{
			costInfos[j] = costInfo_vec[j];
			ITKCALL(ITEM_attach_object_tag(programInfoItem,costInfos[j],relation_type));
		}
		tag_t* projects =(tag_t*)MEM_alloc(sizeof(tag_t));
		projects[0] = project_lists[0];

		//2016-04-01	mengyawei modify
		//current_group 
		//current_user
/*
		POM_AM__set_application_bypass(TRUE);
		tag_t old_user = NULLTAG;
		tag_t old_group = NULLTAG;
		ITKCALL( AOM_ask_value_tag( project_lists[0] , "owning_user" , &old_user ) );
		ITKCALL( AOM_ask_value_tag( project_lists[0] , "owning_group" , &old_group ) );


		ITKCALL( ifail = AOM_lock( project_lists[0] ) );
		ITKCALL( ifail = AOM_set_ownership( project_lists[0] , current_user , current_group ) );
		tr_ECHO("1111111		AOM_set_ownership ==%d\n",ifail);
		ITKCALL( ifail = AOM_save( project_lists[0] ) );
		ITKCALL( ifail = AOM_unlock( project_lists[0] ) );
*/	

		ITKCALL(PROJ_assign_objects(1,projects,costInfoNum,costInfos));
		costInfo_vec.clear();
		DOFREE(costInfos);
		DOFREE(projects);
/*
		ITKCALL( ifail = AOM_lock( project_lists[0] ) );
		ITKCALL( ifail = AOM_set_ownership( project_lists[0] , old_user , old_group ) );
		tr_ECHO("1111111		AOM_set_ownership ==%d\n",ifail);
		ITKCALL( ifail = AOM_save( project_lists[0] ) );
		ITKCALL( ifail = AOM_unlock( project_lists[0] ) );
		POM_AM__set_application_bypass(FALSE);
*/
	}
	POM_AM__set_application_bypass(FALSE);
	tr_ECHO( "info:%d CostInfo has been created!\n",costInfoNum);
	DOFREE(project_lists);
    return ifail;
}

/**
*isWriteLog(I)                是否写日志信息
*programInfo(I)				  需要刷新的forecast信息对象
*start_no(I)				  开始的下标
*end_no  (I)                  结束的下标
*/
int refreshForecast(FILE* logFile,logical writeLog,tag_t *programInfos,int start_no,int end_no)
{
	tr_ECHO("******************refreshForecast begin *******************\n");
	//fileLog = logFile;
	isWriteLog = writeLog;

	tr_ECHO("start_no:%d   end_no:%d\n",start_no,end_no);
	if(end_no - start_no > 0)
	{
		//读首选项YFJC_ext_user_mapping_list
		//add 2014-8-7
		vector<tag_t> extUserVec;
		int extCnt = 0;
		//add 2014-8-7
		getExtUserByDiscipline(extUserVec);
		extCnt = extUserVec.size();
		tr_ECHO("extCnt-->%d\n",extCnt);
		for(int i=start_no;i<end_no;i++){
			char *programID=NULL;
			ITKCALL(AOM_ask_value_string(programInfos[i],"item_id",&programID));
			tr_ECHO("programID-->%s\n",programID);//P0118029
			SAFE_SM_FREE(programID);
		    transToCostInfo(programInfos[i],extUserVec,extCnt);
		}
	}
	tr_ECHO("******************refreshForecast end*******************\n");
	return 0;
}

/**
* 将项目信息对象下面的费用信息对象全部删除掉，然后将项目信息所在的项目对象下的时间表对象下面的时间表任务中的人工成本信息和非人工成本信息
* 都转换成费用信息对象，并将所有费用信息对象全部指派到项目。
* @param returnValueType
*/
int getNewestCostInfo(METHOD_message_t* msg, va_list args)
{ 
	tr_ask_opt_debug();
	tr_ECHO("enter into getNewestCostInfo！\n");
	if(msg->object_tag ==NULLTAG)
	{
		tr_ECHO("error:programInfoItem==NULLTAG！\n");
		return ITK_ok;
	}
	vector<tag_t> extUserVec;
	int extCnt = 0;
	return transToCostInfo(msg->object_tag,extUserVec,extCnt);
}


int yfjc_ebp_transToCostInfo( void * returnValueType)
{
	tr_ECHO("*******yfjc_ebp_transToCostInfo start11********\n");
	tag_t programInfoItem = NULLTAG;
	tag_t *programInfos = NULLTAG;
	tr_ask_opt_debug();
	tr_ECHO("enter into yfjc_ebp_transToCostInfo！\n");
	USERARG_get_tag_argument(&programInfoItem);
	if(programInfoItem == NULLTAG)
	{
		tr_ECHO("error:programInfoItem==NULLTAG！\n");
		return 0;
	}
	
	programInfos = (tag_t*)MEM_alloc(1*sizeof(tag_t));
	programInfos[0] = programInfoItem;
	refreshForecast(NULL,isWriteLog,programInfos,0,1);
	if(programInfos != NULLTAG)
	{
	    MEM_free(programInfos); 
	    programInfos = NULLTAG;
	}


	tr_ECHO("*******yfjc_ebp_transToCostInfo finish*******\n");

	return 0;
}



//判断一个对象在一个map中是否存在,找到返回true，否则返回false。
void existMap(map<tag_t,tag_t> &target_map,tag_t user,logical *existOrNot)
{
	map<tag_t,tag_t>::iterator iter = target_map.find(user);
	if(iter != target_map.end())
	{
		*existOrNot = true;
	}
	else
	{
		*existOrNot = false;
	}	
}



/******************************************************************************
*根据开始日期和实际开始日期的比较，确定开始日期，根据结束日期与实际结束日期的比较，确定结束日期（返回对象在结束日期之后加1表明计算的时候包括当天）
* @param date1
* @param date2
******************************************************************************/
void soa_getStartAndFinishDate(tag_t scheduleTask,date_t *start_date, date_t *finish_date)
{
	date_t start_dateS         = NULLDATE,
		   actual_start_dateS  = NULLDATE;
	char *taskName=NULL;
	ITKCALL(AOM_ask_value_string(scheduleTask,"object_name",&taskName));
	ITKCALL(AOM_ask_value_date(scheduleTask,"start_date",&start_dateS));
	ITKCALL(AOM_ask_value_date(scheduleTask,"actual_start_date",&actual_start_dateS));
	tr_ECHO("info:soa_getStartAndFinishDate method start taskName:%s\n",taskName);
	DOFREE(taskName);

	/*
	//2016-04-19	mengyawei modify
	//use start_date as start_date
	if(!DATE_IS_NULL(actual_start_dateS))
	{
		*start_date = actual_start_dateS;
	}
	else
	{*/
		*start_date = start_dateS;
	//}
	date_t finish_dateS        = NULLDATE,
		   actual_finish_dateS = NULLDATE;

	ITKCALL(AOM_ask_value_date(scheduleTask,"finish_date",&finish_dateS));
	ITKCALL(AOM_ask_value_date(scheduleTask,"actual_finish_date",&actual_finish_dateS));
	/*
	//2016-04-19	mengyawei	modify
	//use finish_date as finish_date
	if(!DATE_IS_NULL(actual_finish_dateS))
	{
		GetBeforeDate(actual_finish_dateS,-1,finish_date);
	}
	else
	{*/
		GetBeforeDate(finish_dateS,-1,finish_date);
	//}
}

/******************************************************************************
*返回两个日期之间相差的天数，除去基本日历中的不工作日
* @param date1
* @param date2
******************************************************************************/
//int soa_intervalOfDate(date_t date1,date_t date2)
//{
//	tag_t tccalendar = NULLTAG;
//
//	string datatime;
//
//
//	
//	tr_ECHO("%s soa_intervalOfDate方法--开始  \n",getNowtime2().c_str());
//
//
//	
//	tr_ECHO("%s TCCALENDAR_get_base_tccalendar方法--开始  \n",getNowtime2().c_str());
//
//	ITKCALL(TCCALENDAR_get_base_tccalendar(&tccalendar));
//
//	
//	tr_ECHO("%s TCCALENDAR_get_base_tccalendar方法--结束  \n",getNowtime2().c_str());
//	
//	
//	int no_of_nonwd = 0;
//	if(tccalendar!=NULLTAG)
//	{
//		char fromDate[30] = "",
//			toDate[30]   = "",
//			**nonWorkingDates =NULL;
//
//		int year1 = date1.year,
//			month1 = date1.month+1,
//			day1 = date1.day,
//			year2 = date2.year,
//			month2 = date2.month+1,
//			day2 = date2.day;
//		tr_ECHO("date1===%d-%d-%d！\n",year1,month1,day1);
//		tr_ECHO("date2===%d-%d-%d！\n",year2,month2,day2);
//
//		if(month1<10)
//		{
//			if(day1<10)
//			{
//				sprintf( toDate, "%d%s%d%s%d%s",year1,"-0",month1,"-0",day1," 00:00:00");
//			}
//			else
//			{
//				sprintf( toDate, "%d%s%d%s%d%s",year1,"-0",month1,"-",day1," 00:00:00");
//			}
//		}
//		else
//		{
//			if(day1<10)
//			{
//				sprintf( toDate, "%d%s%d%s%d%s",year1,"-",month1,"-0",day1," 00:00:00");
//			}
//			else
//			{
//				sprintf( toDate, "%d%s%d%s%d%s",year1,"-",month1,"-",day1," 00:00:00");
//			}
//		}
//		if(month2<10)
//		{
//			if(day2<10)
//			{
//				sprintf( fromDate, "%d%s%d%s%d%s",year2,"-0",month2,"-0",day2," 00:00:00");
//			}
//			else
//			{
//				sprintf( fromDate, "%d%s%d%s%d%s",year2,"-0",month2,"-",day2," 00:00:00");
//			}
//		}
//		else
//		{
//			if(day2<10)
//			{
//				sprintf( fromDate, "%d%s%d%s%d%s",year2,"-",month2,"-0",day2," 00:00:00");
//			}
//			else
//			{
//				sprintf( fromDate, "%d%s%d%s%d%s",year2,"-",month2,"-",day2," 00:00:00");
//			}
//		}
//
//		
//		tr_ECHO("%s TCCALENDAR_get_resource_non_working_dates方法--开始  \n",getNowtime2().c_str());
//
//		ITKCALL(TCCALENDAR_get_resource_non_working_dates(tccalendar,fromDate,toDate,&no_of_nonwd,&nonWorkingDates));
//		
//		
//		tr_ECHO("%s TCCALENDAR_get_resource_non_working_dates方法--结束  \n",getNowtime2().c_str());
//		
//		DOFREE(nonWorkingDates);
//	}
//	int gapday = 0;
//	GapTime(date1,date2,&gapday);
//	tr_ECHO("gapday===%d,no_of_nonwd===%d！\n",gapday,no_of_nonwd);
//
//
//	
//	tr_ECHO("%s soa_intervalOfDate方法--结束  \n",getNowtime2().c_str());
//	return gapday-no_of_nonwd;
//
//}


/******************************************************************************
*返回两个日期之间相差的天数，除去基本日历中的不工作日
* @param date1
* @param date2
******************************************************************************/
int soa_intervalOfDate(date_t date1, date_t date2)
{
	int no_of_nonwd = 0;
	char** nonWorkingDates = NULL;
	char toDate[64] = {};
	char fromDate[64] = {};

	if (base_tccalendar == NULLTAG) {
		ITKCALL(TCCALENDAR_get_base_tccalendar(&base_tccalendar));
	}

	sprintf(toDate, "%4d-%2d-%2d 00:00:00", date1.year, date1.month + 1, date1.day);
	sprintf(fromDate, "%4d-%2d-%2d 00:00:00", date2.year, date2.month + 1, date2.day);
	//查找历史计算结果
	string key_of_intervalOfDate = fromDate;
	key_of_intervalOfDate = key_of_intervalOfDate + toDate;
	map<string, int>::iterator ifind = intervalOfDateMap->find(key_of_intervalOfDate);
	if (ifind != intervalOfDateMap->end()) {

		return ifind->second;
	}
	TCCALENDAR_get_resource_non_working_dates(base_tccalendar, fromDate, toDate, &no_of_nonwd, &nonWorkingDates);
	DOFREE(nonWorkingDates);
	int gapday = 0;
	GapTime(date1, date2, &gapday);
	int val_of_intervalOfDate = gapday - no_of_nonwd;
	intervalOfDateMap->insert(map<string, int>::value_type(key_of_intervalOfDate, val_of_intervalOfDate));
	return val_of_intervalOfDate;
}


//根据key值查找在map中的value值，未发现则返回空字符串
tag_t getSecond(map<tag_t,tag_t> &target_map,tag_t user)
{
	map<tag_t,tag_t>::iterator iter = target_map.find(user);
	if(iter != target_map.end())
	{
		return (tag_t)iter->second;
	}
	return NULLTAG;
}

/**
 * 根据用户TAG获得用户组division和学科section
 * @param user_tag
 * @param postion
 * @return
 */
void getDivisionSectionByPositionName(tag_t user_tag ,char* position_name ,tag_t* division ,tag_t* section)
{
    char *group_name_str = NULL;

    tag_t costvalue  = NULLTAG,
          query_tag  = NULLTAG,
          *results   = NULL;

    int num_found   = 0,
        entry_count = 0;


    if( tc_strstr( position_name , "ExtSupporter_" ))
    {
        group_name_str = ( char* )MEM_alloc( ( tc_strlen( position_name) + 1 ) * sizeof( char ) );
        tc_strcpy(group_name_str, position_name+13);
    }
     
    tag_t default_group = NULLTAG;

    TC_write_syslog( "group_name_str==%s\n",group_name_str);

    if(group_name_str == NULL)
    {
	    ITKCALL(AOM_ask_value_tag(user_tag,"default_group",&default_group));   
    }
    else
    {
         char **entries2     = NULL,
              **values2      = NULL,
              *other_entries2[1],
              *other_values2[1];
         const char query_name[ QRY_name_size_c + 1] = "__WEB_group";

         ITKCALL( QRY_find( query_name , &query_tag ) );

         if( query_tag == NULLTAG )
         {
              TC_write_syslog( "没有找到查询构建器 %s\n",query_name);
              return ;
         }
         int entry_count = 0;
         ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries2 , &values2 ) );
         
         other_entries2[0] = ( char* )MEM_alloc( ( tc_strlen( entries2[0] ) + 1 ) * sizeof( char ) );

         tc_strcpy( other_entries2[0] , entries2[0] );

         other_values2[0] = ( char* )MEM_alloc( ( tc_strlen( group_name_str ) + 1 ) * sizeof( char ) );

         tc_strcpy( other_values2[0] , group_name_str );
         DOFREE( entries2);
         DOFREE( values2);
         int num_found = 0;
         tag_t *results = NULL;
         ITKCALL( QRY_execute( query_tag , 1 , other_entries2 , other_values2 , &num_found , &results ) );
         DOFREE( other_entries2[0]);
         DOFREE( other_values2[0]);
         tr_ECHO("1207 num_found====%u\n",num_found);
         if(num_found>0)
         {
              default_group = results[0];
         }
         DOFREE(results);
    }
	
    if(default_group)
    {
        char* group_name = NULL;
        ITKCALL(AOM_ask_value_string(default_group,"full_name",&group_name));
        TC_write_syslog( "group_name=== %s\n",group_name);
        vector<string> group_vec;

	    char *tokenPtr=tc_strtok(group_name,".");

	    group_vec.push_back(tokenPtr);

	    while(tokenPtr!=NULL)
	    {
		    tokenPtr=tc_strtok(NULL,".");

		    if(tokenPtr!=NULL)
		    {
			    group_vec.push_back(tokenPtr);
		    }
	    }
	    int groupNum = group_vec.size();

	    for(int n=0;n<groupNum;n++)
        {
            TC_write_syslog( "n = %d,str = %s\n",n,group_vec[n].c_str());
		    if(tc_strcmp(group_vec[n].c_str(),TECHCENTER)==0||tc_strcmp(group_vec[n].c_str(),"技术中心")==0)
		    {
			    if(n==2)
			    {
				    *division = default_group;
			    }
			    else if(n==3)
			    {
				    *section = default_group;
				    ITKCALL(AOM_ask_value_tag(default_group,"parent",division));
			    }
                tr_ECHO("1250 section=%u,division=%u\n",*section,*division);
			    break;
		    }
	    }
        DOFREE(group_name);
    }
}

/******************************************************************************
*根据开始日期和结束日期的时间将费用按比例分配到每一个月份，并创建费用信息对象
* @param costOfEachDay
* @param labourMemoNum
* @param start_date
* @param finish_date
* @param costType
* @param cpt
* @param taskType
* @param division
* @param section
* @param rateLevel
* @param user
* @param costNum
* @param labourOrNon
******************************************************************************/
void soa_getCostOfEachMonth(double costOfEachDay, double labourMemoNum, date_t start_date, date_t finish_date, char* costType, char* cpt, char* taskType, tag_t division, tag_t section, tag_t rateLevel, tag_t user, double costNum, char* labourOrNon,vector<tag_t> &costInfo_vec)
{
	int start_year = start_date.year;
	int start_month = start_date.month+1;
	int finish_year = finish_date.year;
	int finish_month = finish_date.month+1;
	if (start_year == finish_year)
	{
		int year = start_year;
		double jci6_Jan = 0,
		       jci6_Feb = 0,
		       jci6_Mar = 0,
		       jci6_Apr = 0,
		       jci6_May = 0,
		       jci6_Jun = 0,
		       jci6_Jul = 0,
		       jci6_Aug = 0,
		       jci6_Sep = 0,
		       jci6_Oct = 0,
		       jci6_Nov = 0,
		       jci6_Dec = 0;

		date_t nextjan1 = NULLDATE,
	           dec1     = NULLDATE,
	           nov1     = NULLDATE,
	           oct1     = NULLDATE,
	           sep1     = NULLDATE,
	           aug1     = NULLDATE,
	           jul1     = NULLDATE,
	           jun1     = NULLDATE,
	           may1     = NULLDATE,
	           apr1     = NULLDATE,
	           mar1     = NULLDATE,
	           feb1     = NULLDATE,
	           jan1     = NULLDATE;
		fillEveryMonth(year,&nextjan1,&dec1,&nov1,&oct1,&sep1,&aug1,&jul1,&jun1,&may1,&apr1,&mar1,&feb1,&jan1);
		switch (start_month)
		{
		case 12:
			jci6_Dec = labourMemoNum;
			break;
		case 11:
			if (finish_month == 11)
			{
				jci6_Nov = labourMemoNum;
			}
			else
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, start_date);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			break;
		case 10:
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, start_date);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = labourMemoNum;
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1,nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date, nov1);
			}
			break;
		case 9:
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, start_date);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = labourMemoNum;
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10) {
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11) {
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 8:
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, start_date);
			}
			else if (finish_month == 8) {
				jci6_Aug = labourMemoNum;
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11) {
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 7:
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, start_date);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = labourMemoNum;
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10) {
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 6:
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, start_date);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = labourMemoNum;
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11) {
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 5:
			if (finish_month > 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, start_date);
			}
			else if (finish_month == 5)
			{
				jci6_May = labourMemoNum;
			}
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 4:
			if (finish_month > 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, start_date);
			}
			else if (finish_month == 4)
			{
				jci6_Apr = labourMemoNum;
			}
			if (finish_month > 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
			}
			else if (finish_month == 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(finish_date, may1);
			}
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 3:
			if (finish_month > 3)
			{
				jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, start_date);
			}
			else if (finish_month == 3)
			{
				jci6_Mar = labourMemoNum;
			}
			if (finish_month > 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);

			}
			else if (finish_month == 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(finish_date, apr1);

			}
			if (finish_month > 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
			}
			else if (finish_month == 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(finish_date, may1);
			}
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}

			break;
		case 2:
			if (finish_month > 2)
			{
				jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, start_date);
			}
			else if (finish_month == 2)
			{
				jci6_Feb = labourMemoNum;
			}
			if (finish_month > 3)
			{
				jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
			}
			else if (finish_month == 3)
			{
				jci6_Mar = costOfEachDay*soa_intervalOfDate(finish_date, mar1);
			}
			if (finish_month > 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
			}
			else if (finish_month == 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(finish_date, apr1);
			}
			if (finish_month > 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
			}
			else if (finish_month == 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(finish_date, may1);
			}
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		case 1:
			if (finish_month > 1)
			{
				jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, start_date);
			}
			else if (finish_month == 1)
			{
				jci6_Jan = labourMemoNum;
			}
			if (finish_month > 2)
			{
				jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
			}
			else if (finish_month == 2)
			{
				jci6_Feb = costOfEachDay*soa_intervalOfDate(finish_date, feb1);
			}
			if (finish_month > 3)
			{
				jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
			}
			else if (finish_month == 3) {
				jci6_Mar = costOfEachDay*soa_intervalOfDate(finish_date, mar1);
			}
			if (finish_month > 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
			}
			else if (finish_month == 4)
			{
				jci6_Apr = costOfEachDay*soa_intervalOfDate(finish_date, apr1);
			}
			if (finish_month > 5) {
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
			}
			else if (finish_month == 5)
			{
				jci6_May = costOfEachDay*soa_intervalOfDate(finish_date, may1);
			}
			if (finish_month > 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
			}
			else if (finish_month == 6)
			{
				jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
			}
			if (finish_month > 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
			}
			else if (finish_month == 7)
			{
				jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
			}
			if (finish_month > 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
			}
			else if (finish_month == 8)
			{
				jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
			}
			if (finish_month > 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
			}
			else if (finish_month == 9)
			{
				jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
			}
			if (finish_month > 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
			}
			else if (finish_month == 10)
			{
				jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
			}
			if (finish_month > 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
			}
			else if (finish_month == 11)
			{
				jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date,nov1);
			}
			break;
		default:
			break;
		}
		soa_createCostInfoByLabOrNon(costNum,labourOrNon,costType, cpt, taskType, division, year, section, rateLevel, user, jci6_Jan, jci6_Feb, jci6_Mar, jci6_Apr, jci6_May, jci6_Jun, jci6_Jul, jci6_Aug, jci6_Sep, jci6_Oct, jci6_Nov, jci6_Dec,costInfo_vec);
	} else {
		for (int yearCount = start_year; yearCount <= finish_year; yearCount++) {
			int year = yearCount;
			double jci6_Jan = 0,
				   jci6_Feb = 0,
				   jci6_Mar = 0,
				   jci6_Apr = 0,
				   jci6_May = 0,
				   jci6_Jun = 0,
				   jci6_Jul = 0,
				   jci6_Aug = 0,
				   jci6_Sep = 0,
				   jci6_Oct = 0,
				   jci6_Nov = 0,
				   jci6_Dec = 0;

			date_t nextjan1 = NULLDATE,
				   dec1     = NULLDATE,
				   nov1     = NULLDATE,
				   oct1     = NULLDATE,
				   sep1     = NULLDATE,
				   aug1     = NULLDATE,
				   jul1     = NULLDATE,
				   jun1     = NULLDATE,
				   may1     = NULLDATE,
				   apr1     = NULLDATE,
				   mar1     = NULLDATE,
				   feb1     = NULLDATE,
				   jan1     = NULLDATE;
			fillEveryMonth(year,&nextjan1,&dec1,&nov1,&oct1,&sep1,&aug1,&jul1,&jun1,&may1,&apr1,&mar1,&feb1,&jan1);
			if (yearCount == start_year)
			{
				switch (start_month)
				{
				case 12:
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, start_date);
					break;
				case 11:
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, start_date);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 10:
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, start_date);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 9:
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, start_date);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 8:
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, start_date);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 7:
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, start_date);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 6:
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, start_date);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 5:
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, start_date);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 4:
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, start_date);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 3:
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, start_date);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 2:
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, start_date);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				case 1:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, start_date);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
					break;
				default:
					break;
				}
			}
			else if (yearCount == finish_year)
			{
				switch (finish_month)
				{
				case 1:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(finish_date, jan1);
					break;
				case 2:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(finish_date, feb1);
					break;
				case 3:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(finish_date, mar1);
					break;
				case 4:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(finish_date,apr1);
					break;
				case 5:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(finish_date, may1);
					break;
				case 6:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(finish_date, jun1);
					break;
				case 7:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(finish_date, jul1);
					break;
				case 8:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(finish_date, aug1);
					break;
				case 9:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(finish_date, sep1);
					break;
				case 10:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(finish_date, oct1);
					break;
				case 11:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(finish_date, nov1);
					break;
				case 12:
					jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
					jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
					jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
					jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
					jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
					jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
					jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
					jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
					jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
					jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
					jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1, nov1);
					jci6_Dec = costOfEachDay*soa_intervalOfDate(finish_date, dec1);
					break;
				default:
					break;
				}
			}
			else
			{
				jci6_Jan = costOfEachDay*soa_intervalOfDate(feb1, jan1);
				jci6_Feb = costOfEachDay*soa_intervalOfDate(mar1, feb1);
				jci6_Mar = costOfEachDay*soa_intervalOfDate(apr1, mar1);
				jci6_Apr = costOfEachDay*soa_intervalOfDate(may1, apr1);
				jci6_May = costOfEachDay*soa_intervalOfDate(jun1, may1);
				jci6_Jun = costOfEachDay*soa_intervalOfDate(jul1, jun1);
				jci6_Jul = costOfEachDay*soa_intervalOfDate(aug1, jul1);
				jci6_Aug = costOfEachDay*soa_intervalOfDate(sep1, aug1);
				jci6_Sep = costOfEachDay*soa_intervalOfDate(oct1, sep1);
				jci6_Oct = costOfEachDay*soa_intervalOfDate(nov1, oct1);
				jci6_Nov = costOfEachDay*soa_intervalOfDate(dec1,nov1);
				jci6_Dec = costOfEachDay*soa_intervalOfDate(nextjan1, dec1);
			}
			soa_createCostInfoByLabOrNon(costNum,labourOrNon,costType, cpt, taskType, division, year, section, rateLevel, user, jci6_Jan, jci6_Feb, jci6_Mar, jci6_Apr, jci6_May, jci6_Jun, jci6_Jul, jci6_Aug, jci6_Sep, jci6_Oct, jci6_Nov, jci6_Dec,costInfo_vec);
		}
	}
}


/******************************************************************************
*创建人工成本相关的费用信息对象
* @param scheduleSummaryTask
* @param *schedulemembers
* @param *resource_tags
* @param int schedulemembersNum
******************************************************************************/
void soa_getLabourMemo( tag_t scheduleTask,  tag_t *schedulemembers, tag_t *resource_tags,int schedulemembersNum,vector<tag_t> extUserVec,int extCnt,vector<tag_t> &costInfo_vec)
{
	char* taskStatus = NULL;
	tr_ECHO("info:soa_getLabourMemo method begin！  %s\n",getNowtime2().c_str());
	ITKCALL(AOM_ask_value_string(scheduleTask,"fnd0status",&taskStatus));
	tr_ECHO("info:taskStatus===%s   scheduleTask:%u\n",taskStatus,scheduleTask);
	if(tc_strcmp(taskStatus,"complete")==0||tc_strcmp(taskStatus,"closed")==0)
	{
		DOFREE(taskStatus);
		return;
	}
	int resourceAssignmentsNum = 0,
		fnd0AssignmentsNum     = 0,
		work_estimate          = 0,
		duration               = 0,
		fixed_type             = 0;
	//tag_t *resourceAssignments = NULL,
	tag_t	  *fnd0Assignments     = NULL;
	char* jci6_TaskType=NULL;

	int  n_referencers=0;  
	int *  levels=NULL;
	int n_instances=0;

	tag_t *  referencers=NULL;  
	char **  relations =NULL,*obj_class_name=NULL;


	//POM_referencers_of_instance
	tag_t*     ref_instances=NULL;          /**< (OF) n_instances Tags of referencing instances */
    int*       instance_levels=NULL;         /**< (OF) n_instances Levels of referencing instances */
    int*      instance_where_found=NULL;   /**< (OF) n_instances Where the instance was found */
    int        n_classes=0;             /**< (O) Number of referencing classes */
    tag_t*     ref_classes=NULL;            /**< (OF) n_classes Tags of referencing classes */
    int*       class_levels=NULL;           /**< (OF) n_classes Levels of referencing classes */
    int*      class_where_found=NULL;       /**< (OF) n_classes Where the class was found (always DB) */
	//POM_referencers_of_instance

	vector<tag_t> resourceAssignments_Vec;


	//ITKCALL(AOM_ask_value_tags(scheduleTask,"jci6_fnd0Assignments",&resourceAssignmentsNum,&resourceAssignments));

	//AOM_refresh

	ITKCALL(POM_referencers_of_instance(scheduleTask,1,POM_in_db_only,&n_instances,&ref_instances,&instance_levels,&instance_where_found,&n_classes,&ref_classes,&class_levels,&class_where_found));
	
	//ITKCALL(WSOM_where_referenced(scheduleTask,1,&n_referencers,&levels,&referencers,&relations));
	
	tr_ECHO("ww n_instances-->%d\n",n_instances);

	for(int i=0;i<n_instances;i++){
		//char *obj_class_name2=NULL;
		//ITKCALL( WSOM_ask_object_type2(ref_instances[i],&obj_class_name2 ) );
		char object_type2[TCTYPE_name_size_c+1]={'\0'};
		tag_t type_tag=NULLTAG;
		ITKCALL(TCTYPE_ask_object_type(ref_instances[i],&type_tag));

		if(type_tag!=NULLTAG)
			ITKCALL(TCTYPE_ask_name(type_tag,object_type2));
		
		if(strcmp(object_type2,"ResourceAssignment")==0)
		{
			resourceAssignmentsNum++;
			resourceAssignments_Vec.push_back(ref_instances[i]);
		}

	}

	tr_ECHO("resourceAssignmentsNum: %d\n ",resourceAssignmentsNum);

	double *resource_levelNumArr=NULL;
	double totalResource_levelNum = 0;
	int isfreeDouble=0;
	if(resourceAssignmentsNum>0){
		resource_levelNumArr=(double *)MEM_alloc((resourceAssignmentsNum* sizeof(double)));
	}else{
		isfreeDouble=1;
	}
		  
	map<tag_t,tag_t> user_to_discipline_map;

	for(int i=0;i<resourceAssignments_Vec.size();i++){
		//char *obj_class_name2=NULL;
		//ITKCALL( WSOM_ask_object_type2(ref_instances[i],&obj_class_name2 ) );
		
		char obj_class_name2[TCTYPE_name_size_c+1]={'\0'};
		tag_t type_tag=NULLTAG;
		ITKCALL(TCTYPE_ask_object_type(resourceAssignments_Vec[i],&type_tag));

		if(type_tag!=NULLTAG)
			ITKCALL(TCTYPE_ask_name(type_tag,obj_class_name2));

		tr_ECHO("obj_class_name2:%s\n",obj_class_name2);
		if(strcmp(obj_class_name2,"ResourceAssignment")==0)
		{
			//tag_t resourceAssignments=ref_instances[i];
			tag_t resourceAssignments=resourceAssignments_Vec[i];
			ITKCALL(AOM_ask_value_double(resourceAssignments,"resource_level",&resource_levelNumArr[i]));
			tag_t user_tag    = NULLTAG,
            discipline_tag = NULLTAG;
			 
			ITKCALL(AOM_ask_value_tag(resourceAssignments,"secondary_object",&user_tag));
			ITKCALL(AOM_ask_value_tag(resourceAssignments,"discipline",&discipline_tag));

			logical existItemIdMap = false;
			existMap(user_to_discipline_map,user_tag,&existItemIdMap);
			if(!existItemIdMap)
			{
				user_to_discipline_map.insert(map<tag_t,tag_t>::value_type(user_tag,discipline_tag));
			}
		}

	}

	tr_ECHO("user_to_discipline_map.size(): %d\n ",user_to_discipline_map.size());
	DOFREE(levels);
	DOFREE(relations);
	DOFREE(referencers);

	DOFREE(ref_instances);
	DOFREE(instance_levels);
	DOFREE(instance_where_found);
	DOFREE(ref_classes);
	DOFREE(class_levels);
	DOFREE(class_where_found);
		

	//modify by wuwei --2018
	//ITKCALL(AOM_ask_value_tags(scheduleTask,"fnd0Assignments",&fnd0AssignmentsNum,&fnd0Assignments));
	//double *resource_levelNumArr=(double *)MEM_alloc((resourceAssignmentsNum* sizeof(double))),
	//	   totalResource_levelNum = 0;
	/**modify by wuwei --2018
	double *resource_levelNumArr=(double *)MEM_alloc((schedulemembersNum* sizeof(double))),
		   totalResource_levelNum = 0;
	**/
  
	
	//modify by wuwei --2018
	/**
	for(int i=0;i<fnd0AssignmentsNum;i++)
	{
		ITKCALL(AOM_ask_value_double(fnd0Assignments[i],"resource_level",&resource_levelNumArr[i]));
        tag_t user_tag       = NULLTAG,
              discipline_tag = NULLTAG;
        ITKCALL(AOM_ask_value_tag(fnd0Assignments[i],"secondary_object",&user_tag));
        ITKCALL(AOM_ask_value_tag(fnd0Assignments[i],"discipline",&discipline_tag));
        logical existItemIdMap = false;
		existMap(user_to_discipline_map,user_tag,&existItemIdMap);
        if(!existItemIdMap)
        {
            user_to_discipline_map.insert(map<tag_t,tag_t>::value_type(user_tag,discipline_tag));
        }
		//totalResource_levelNum = totalResource_levelNum + resource_levelNumArr[i];
	}
	**/





	//ITKCALL(AOM_ask_value_int(scheduleTask,"work_estimate",&work_estimate));
	ITKCALL(AOM_ask_value_int(scheduleTask,"duration",&duration));
	//ITKCALL(AOM_ask_value_int(scheduleTask,"fixed_type",&fixed_type));
	ITKCALL(AOM_ask_value_string(scheduleTask,"jci6_TaskType",&jci6_TaskType));

	tr_ECHO("ww jci6_TaskType:%s\n",jci6_TaskType);

	double work_estimateNum = work_estimate,
		   durationNum      = duration;

	char *costType = "Normal Hours",
		 *cpt      = "Forecast",
		 *taskType = jci6_TaskType;

	date_t start_date  = NULLDATE,
		   finish_date = NULLDATE;
	soa_getStartAndFinishDate(scheduleTask,&start_date, &finish_date);

	tr_ECHO("296info:start_date==%d-%d-%d,finish_date==%d-%d-%d！\n",start_date.year,start_date.month+1,start_date.day,finish_date.year,finish_date.month+1,finish_date.day);

	for(int i=0;i<resourceAssignmentsNum;i++)
	{
		tag_t user_tag = NULLTAG;

		ITKCALL(AOM_ask_value_tag(resourceAssignments_Vec[i], "secondary_object", &user_tag));

		for(int j=0;j<schedulemembersNum;j++)
		{
			
			//if(resource_tags[j]==resourceAssignments[i])
			if(resource_tags[j]==user_tag)
            {

				double labourMemoNum = 0;
				// 得到学科或者用户对应的人工成本
				//if (fixed_type == 0 || fixed_type == 2)
				//{
				//	if(totalResource_levelNum!=0)
				//	{
				//		labourMemoNum = (work_estimateNum*resource_levelNumArr[i])/(totalResource_levelNum * 60);
				//	}
				//}
				//else if (fixed_type == 1)
				//{
					labourMemoNum = (durationNum*resource_levelNumArr[i])/6000;
				//}
				tr_ECHO("find person  info:labourMemoNum=%lf！\n",labourMemoNum);
                double costOfEachDay = 0;
                if(soa_intervalOfDate(finish_date, start_date)>0)
                {
                    costOfEachDay = labourMemoNum/soa_intervalOfDate(finish_date, start_date);
                }

				tr_ECHO("info:costOfEachDay==%lf！\n",costOfEachDay);
				tag_t rateLevel = NULLTAG,
					  user      = NULLTAG,
					  division  = NULLTAG,
					  section   = NULLTAG;

				//modify by wuwei
				char object_type[TCTYPE_name_size_c+1]={'\0'};
				tag_t type_tag=NULLTAG;
				ITKCALL(TCTYPE_ask_object_type(user_tag,&type_tag));
				ITKCALL(TCTYPE_ask_name(type_tag,object_type));
				tr_ECHO("info:object_type=%s！\n",object_type);
                double costNum = 0;
				if(tc_strcmp(object_type,"User")==0)
                {
				//	user = resourceAssignments_Vec[i];
					user=user_tag;
					char *user_id = NULL;
                    int  user_status = 0;
                    ITKCALL(AOM_ask_value_int(user,"status",&user_status));
                    if(user_status == 0)
                    {
                        ITKCALL(AOM_ask_value_string(user,"user_id",&user_id));
                        tr_ECHO("info:user_id=%s！\n",user_id);
						logical needCreate = TRUE;
					
						if(tr_isExtUser(user,extUserVec,extCnt))
						{
							needCreate = FALSE;
							tr_ECHO("资源指派为%s,不需要重新生成forecast\n",user_id);
						}

						
						if(needCreate)
						{
							tr_ECHO("ww needCreate=true！\n");
							//tr_ECHO(true,"资源指派为%s,需要重新生成forecast\n",user_id);
							tag_t postion = NULLTAG;
							tr_getUserPostion(user_id ,&rateLevel );
							costNum = getCostNumOfPosition( rateLevel,user);
							tag_t disciplineTag = getSecond(user_to_discipline_map,user);
							tr_ECHO("info:disciplineTag=%u！\n",disciplineTag);
							if(disciplineTag)
							{
								char *postion_name = NULL;
								ITKCALL( AOM_ask_value_string( disciplineTag, OBJECT_NAME , &postion_name ) );
								getDivisionSectionByPositionName(user,postion_name,&division,&section);
								tr_ECHO("info:rateLevel==%u,user=%u,division=%u,section=%u！\n",rateLevel,user,division,section);
							}
							soa_getCostOfEachMonth(costOfEachDay, labourMemoNum, start_date, 
										finish_date, costType, cpt, taskType,division, 
										section, rateLevel, user, costNum, "labour",costInfo_vec);
							break;			
						}
						DOFREE(user_id);
					
                    }
               } 
			}
		}
	}
	DOFREE(taskStatus);
	//DOFREE(resourceAssignments);
	DOFREE(fnd0Assignments);
	DOFREE(jci6_TaskType);

	tr_ECHO("isfreeDouble:%d\n",isfreeDouble);
	if(isfreeDouble>0)
		DOFREE(resource_levelNumArr);
	
	tr_ECHO("info:soa_getLabourMemo method end！ %s\n",getNowtime2().c_str());
}

/******************************************************************************
*创建非人工成本相关的费用信息对象
* @param scheduleSummaryTask
******************************************************************************/
void soa_getNonLabourMemo( tag_t scheduleTask, vector<tag_t> &costInfo_vec)
{
	char* taskStatus = NULL;
	char *taskName=NULL;
	ITKCALL(AOM_ask_value_string(scheduleTask,"object_name",&taskName));
	tr_ECHO("info:soa_getNonLabourMemo method start！  taskName: %s\n",taskName);
	ITKCALL(AOM_ask_value_string(scheduleTask,"fnd0status",&taskStatus));
	tr_ECHO("%s info:taskStatus===%s\n",taskName,taskStatus);
	

	if(tc_strcmp(taskStatus,"complete")==0||tc_strcmp(taskStatus,"closed")==0)
	{
		DOFREE(taskStatus);
		return;
	}
	char *cpt     = "Forecast",
		 *taskType = NULL;

	int fixed_costsLength = 0;

	tag_t* fixed_costs=NULL;
	ITKCALL(AOM_ask_value_tags(scheduleTask,"fixed_cost_taglist",&fixed_costsLength,&fixed_costs));
	ITKCALL(AOM_ask_value_string(scheduleTask,"jci6_TaskType",&taskType));

	date_t start_date  = NULLDATE,
		   finish_date  = NULLDATE;
	soa_getStartAndFinishDate(scheduleTask,&start_date, &finish_date);

	tr_ECHO("577 info:start_date==%d-%d-%d,finish_date==%d-%d-%d！\n",start_date.year,start_date.month+1,start_date.day,finish_date.year,finish_date.month+1,finish_date.day);
	tr_ECHO("info fixed_costsLength==%d\n",fixed_costsLength);
	if(fixed_costsLength>0)
	{
		for(int i=0;i<fixed_costsLength;i++)
		{
			char *costType   = NULL,
				*costNumStr = NULL;
			tr_ECHO("info fixed_costs[%d]==%u\n",i,fixed_costs[i]);
			if(fixed_costs[i])
			{
				ITKCALL(AOM_ask_value_string(fixed_costs[i],"bill_type",&costType));
				tag_t estCostvalue = NULLTAG;
				ITKCALL(AOM_ask_value_tag(fixed_costs[i],"est_costvalue_form_tag",&estCostvalue));
				tr_ECHO("info estCostvalue==%u\n",estCostvalue);
				if(estCostvalue)
                {
					ITKCALL(AOM_ask_value_string(estCostvalue,"cost",&costNumStr));
					double costNum = strtod(costNumStr,NULL);
                    double costOfEachDay = 0;
                    if(soa_intervalOfDate(finish_date, start_date)>0)
                    {
                        costOfEachDay = costNum/soa_intervalOfDate(finish_date, start_date);
                    }
					tr_ECHO("info costOfEachDay==%lf",costOfEachDay);
					soa_getCostOfEachMonth(costOfEachDay, costNum, start_date, finish_date, costType, cpt, taskType, NULL, NULL, NULL, NULL, 0, "nonLabour",costInfo_vec);
					tr_ECHO("info:soa_getCostOfEachMonth over！\n");
					DOFREE(costNumStr);
				}
				DOFREE(costType);
			}
		}
		DOFREE(fixed_costs);
	}
	DOFREE(taskStatus);
	DOFREE(taskType);


	tr_ECHO("info:soa_getNonLabourMemo method end！taskName:%s\n",taskName);
	DOFREE(taskName);
}

/******************************************************************************
*cycleScheduleTask()
* @param scheduleSummaryTask
* @param *schedulemembers
* @param *resource_tags
* @param schedulemembersNum
******************************************************************************/
void soa_cycleScheduleTask( tag_t scheduleSummaryTask,  tag_t *schedulemembers, tag_t *resource_tags, int schedulemembersNum, vector<tag_t> extUserVec,int extCnt,vector<tag_t> &costInfo_vec)
{
	int task_type = 0;
	ITKCALL(AOM_ask_value_int(scheduleSummaryTask, "task_type", &task_type));
	if(task_type!=1)
	{
		int child_task_taglistNum = 0;
		tag_t* child_task_taglist =NULL;
		ITKCALL(AOM_ask_value_tags(scheduleSummaryTask,"child_task_taglist",&child_task_taglistNum,&child_task_taglist));
		tr_ECHO("info:child_task_taglistNum==%d！\n",child_task_taglistNum);
       // POM_AM__set_application_bypass(TRUE);
		if (child_task_taglistNum == 0)
		{
			soa_getLabourMemo(scheduleSummaryTask, schedulemembers, resource_tags,schedulemembersNum,extUserVec,extCnt,costInfo_vec);
			
			soa_getNonLabourMemo(scheduleSummaryTask,costInfo_vec);
		}
		else
		{
			soa_getNonLabourMemo(scheduleSummaryTask,costInfo_vec);
			for (int i = 0; i < child_task_taglistNum; i++)
			{
				soa_cycleScheduleTask(child_task_taglist[i], schedulemembers, resource_tags,schedulemembersNum,extUserVec,extCnt,costInfo_vec);
			}
		}
       // POM_AM__set_application_bypass(FALSE);
		DOFREE(child_task_taglist);
	}

}









void fillEveryMonth(int year,date_t *nextjan1,date_t *dec1,date_t *nov1,date_t *oct1,date_t *sep1,date_t *aug1,date_t *jul1,date_t *jun1,date_t *may1,date_t *apr1,date_t *mar1,date_t *feb1,date_t *jan1){
	nextjan1->year = (year+1);
	nextjan1->month = 0;
	nextjan1->day = 1;
	nextjan1->hour = 0;
	nextjan1->minute = 0;
	nextjan1->second = 0;

	dec1->year = year;
	dec1->month = 11;
	dec1->day = 1;
	dec1->hour = 0;
	dec1->minute = 0;
	dec1->second = 0;

	nov1->year = year;
	nov1->month = 10;
	nov1->day = 1;
	nov1->hour = 0;
	nov1->minute = 0;
	nov1->second = 0;

	oct1->year = year;
	oct1->month = 9;
	oct1->day = 1;
	oct1->hour = 0;
	oct1->minute = 0;
	oct1->second = 0;

	sep1->year = year;
	sep1->month = 8;
	sep1->day = 1;
	sep1->hour = 0;
	sep1->minute = 0;
	sep1->second = 0;

	aug1->year = year;
	aug1->month = 7;
	aug1->day = 1;
	aug1->hour = 0;
	aug1->minute = 0;
	aug1->second = 0;

	jul1->year = year;
	jul1->month = 6;
	jul1->day = 1;
	jul1->hour = 0;
	jul1->minute = 0;
	jul1->second = 0;

	jun1->year = year;
	jun1->month = 5;
	jun1->day = 1;
	jun1->hour = 0;
	jun1->minute = 0;
	jun1->second = 0;

	may1->year = year;
	may1->month = 4;
	may1->day = 1;
	may1->hour = 0;
	may1->minute = 0;
	may1->second = 0;

	apr1->year = year;
	apr1->month = 3;
	apr1->day = 1;
	apr1->hour = 0;
	apr1->minute = 0;
	apr1->second = 0;

	mar1->year = year;
	mar1->month = 2;
	mar1->day = 1;
	mar1->hour = 0;
	mar1->minute = 0;
	mar1->second = 0;

	feb1->year = year;
	feb1->month = 1;
	feb1->day = 1;
	feb1->hour = 0;
	feb1->minute = 0;
	feb1->second = 0;

	jan1->year = year;
	jan1->month = 0;
	jan1->day = 1;
	jan1->hour = 0;
	jan1->minute = 0;
	jan1->second = 0;

}




/**
* 得到费用信息对象，然后设置属性值，并添加到costInfo_vec中
* @param jci6_CostType
* @param jci6_CPT
* @param jci6_TaskType
* @param jci6_Division
* @param jci6_Year
* @param jci6_Unit
* @param jci6_Section
* @param jci6_RateLevel
* @param jci6_User
* @param jci6_Jan
* @param jci6_Feb
* @param jci6_Mar
* @param jci6_Apr
* @param jci6_May
* @param jci6_Jun
* @param jci6_Jul
* @param jci6_Aug
* @param jci6_Sep
* @param jci6_Oct
* @param jci6_Nov
* @param jci6_Dec
*/
void soa_createCostInfoAndSetPro(char* costInfoName,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, char* jci6_Unit, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec,vector<tag_t> &costInfo_vec)
{
	tag_t costInfo = soa_createCostInfo(costInfoName,jci6_CostType, jci6_CPT, jci6_TaskType, jci6_Division, jci6_Year, jci6_Unit, jci6_Section, jci6_RateLevel, jci6_User, jci6_Jan, jci6_Feb, jci6_Mar, jci6_Apr, jci6_May, jci6_Jun, jci6_Jul, jci6_Aug, jci6_Sep, jci6_Oct, jci6_Nov, jci6_Dec);
    if(costInfo)
    {
        costInfo_vec.push_back(costInfo);
    }
    
}

/**
* 根据工时得到人月
* @param hours
* @return
*/
double soa_getManMonthByHours(double hours)
{
	return hours/170;
}

/**
* 根据工时得到元
* @param hours
* @param costNum
* @return
*/
double soa_getYuanByHours(double hours, double costNum)
{
	return hours*costNum;
}



/******************************************************************************
*根据开始日期和结束日期的时间将费用按比例分配到每一个月份，并创建费用信息对象
* @param costNum
* @param labourOrNon
* @param costType
* @param cpt
* @param taskType
* @param cpt
* @param taskType
* @param division
* @param year
* @param section
* @param rateLevel
* @param user
* @param jci6_Jan
* @param jci6_Feb
* @param jci6_Mar
* @param jci6_Apr
* @param jci6_May
* @param jci6_Jun
* @param jci6_Jul
* @param jci6_Aug
* @param jci6_Sep
* @param jci6_Oct
* @param jci6_Nov
* @param jci6_Dec
******************************************************************************/
void soa_createCostInfoByLabOrNon(double costNum,char* labourOrNon,char* costType, char* cpt, char* taskType, tag_t division, int year, tag_t section, tag_t rateLevel, tag_t user, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec,vector<tag_t> &costInfo_vec)
{
	char timestamp[13] = "";
	time_t T;
	struct tm * timenow;
	time ( &T );
	timenow = localtime ( &T );
	char month[3] = "",
		 day[3]   = "",
		 hour[3]  = "",
		 min[3]   = "";
	if(timenow->tm_mon<9)
	{
		sprintf( month, "%d%d",0,timenow->tm_mon+1);
	}
	else
	{
		sprintf( month, "%d",timenow->tm_mon+1);
	}
	if(timenow->tm_mday<10)
	{
		sprintf( day, "%d%d",0,timenow->tm_mday);
	}
	else
	{
		sprintf( day, "%d",timenow->tm_mday);
	}
	if(timenow->tm_hour<10)
	{
		sprintf( hour, "%d%d",0,timenow->tm_hour);
	}
	else
	{
		sprintf( hour, "%d",timenow->tm_hour);
	}
	if(timenow->tm_min<10)
	{
		sprintf( min, "%d%d",0,timenow->tm_min);
	}
	else
	{
		sprintf( min, "%d",timenow->tm_min);
	}
	sprintf( timestamp, "%d%s%s%s%s",1900+timenow->tm_year,month,day,hour,min);
	tr_ECHO("timestamp==%s！\n",timestamp);
	if (tc_strcmp(labourOrNon,"labour")==0)
	{
		char costInfoName_Hour[128] = "",
			 costInfoName_ManMonth[128] = "",
			 costInfoName_Yuan[128] = "",
			 *division_name    = NULL,
			 *ratelevel_name   = NULL;
		if(division)
		{
			ITKCALL( AOM_ask_value_string( division, "name", &division_name ) );
		}
		if(rateLevel)
		{
			ITKCALL(AOM_ask_value_string(rateLevel,"discipline_name",&ratelevel_name));
		}
		if(division_name)
		{
			if(ratelevel_name)
			{
				sprintf( costInfoName_Hour, "%s%s%d%s%s%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_",ratelevel_name,"_Hour_",timestamp);
				sprintf( costInfoName_ManMonth, "%s%s%d%s%s%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_",ratelevel_name,"_ManMonth_",timestamp);
				sprintf( costInfoName_Yuan, "%s%s%d%s%s%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_",ratelevel_name,"_Yuan_",timestamp);
			}
			else
			{
				sprintf( costInfoName_Hour, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_Hour_",timestamp);
				sprintf( costInfoName_ManMonth, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_ManMonth_",timestamp);
				sprintf( costInfoName_Yuan, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",division_name,"_Yuan_",timestamp);
			}
		}
		else
		{
			if(ratelevel_name)
			{
				sprintf( costInfoName_Hour, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",ratelevel_name,"_Hour_",timestamp);
				sprintf( costInfoName_ManMonth, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",ratelevel_name,"_ManMonth_",timestamp);
				sprintf( costInfoName_Yuan, "%s%s%d%s%s%s%s",project_id,"_Forecast_",year ,"_",ratelevel_name,"_Yuan_",timestamp);
			}
			else
			{
				sprintf( costInfoName_Hour, "%s%s%d%s%s",project_id,"_Forecast_",year,"_Hour_",timestamp);
				sprintf( costInfoName_ManMonth, "%s%s%d%s%s",project_id,"_Forecast_",year ,"_ManMonth_",timestamp);
				sprintf( costInfoName_Yuan, "%s%s%d%s%s",project_id,"_Forecast_",year ,"_Yuan_",timestamp);
			}
		}
		tr_ECHO("labour:costInfoName_Hour=%s,costInfoName_ManMonth=%s,costInfoName_Yuan=%s！\n",costInfoName_Hour,costInfoName_ManMonth,costInfoName_Yuan);
		soa_createCostInfoAndSetPro(costInfoName_Hour,costType, cpt, taskType, division, year, "Hour", section, rateLevel, user, jci6_Jan, jci6_Feb, jci6_Mar, jci6_Apr, jci6_May, jci6_Jun, jci6_Jul, jci6_Aug, jci6_Sep, jci6_Oct, jci6_Nov, jci6_Dec,costInfo_vec);
		soa_createCostInfoAndSetPro(costInfoName_ManMonth,costType, cpt, taskType, division, year, "ManMonth", section, rateLevel, user, soa_getManMonthByHours(jci6_Jan), soa_getManMonthByHours(jci6_Feb), soa_getManMonthByHours(jci6_Mar), soa_getManMonthByHours(jci6_Apr), soa_getManMonthByHours(jci6_May), soa_getManMonthByHours(jci6_Jun), soa_getManMonthByHours(jci6_Jul), soa_getManMonthByHours(jci6_Aug), soa_getManMonthByHours(jci6_Sep), soa_getManMonthByHours(jci6_Oct), soa_getManMonthByHours(jci6_Nov), soa_getManMonthByHours(jci6_Dec),costInfo_vec);
		soa_createCostInfoAndSetPro(costInfoName_Yuan,costType, cpt, taskType, division, year, "Yuan", section, rateLevel, user, soa_getYuanByHours(jci6_Jan, costNum), soa_getYuanByHours(jci6_Feb, costNum), soa_getYuanByHours(jci6_Mar, costNum), soa_getYuanByHours(jci6_Apr, costNum), soa_getYuanByHours(jci6_May, costNum), soa_getYuanByHours(jci6_Jun, costNum), soa_getYuanByHours(jci6_Jul, costNum), soa_getYuanByHours(jci6_Aug, costNum), soa_getYuanByHours(jci6_Sep, costNum), soa_getYuanByHours(jci6_Oct, costNum),
		soa_getYuanByHours(jci6_Nov, costNum), soa_getYuanByHours(jci6_Dec, costNum),costInfo_vec);
		DOFREE(division_name);
		DOFREE(ratelevel_name);
	}
	else if (tc_strcmp(labourOrNon,"nonLabour")==0)
	{
		char costInfoName[128] = {'\0'};
		sprintf( costInfoName, "%s%s%s%s%d%s%s",project_id,"_Forecast_",costType ,"_",year,"_",timestamp);
		tr_ECHO("nonLabour:costInfoName===%s   costType:%s  cpt:%s taskType:%s ！\n",costInfoName,costType,cpt,taskType);
		soa_createCostInfoAndSetPro(costInfoName,costType, cpt, taskType, NULL, year, "Yuan", NULL, NULL, NULL, jci6_Jan, jci6_Feb, jci6_Mar, jci6_Apr, jci6_May, jci6_Jun, jci6_Jul, jci6_Aug, jci6_Sep, jci6_Oct, jci6_Nov, jci6_Dec,costInfo_vec);
	}
}





/**
* 创建单个的费用信息对象
* @param object_name
* @param jci6_CostType
* @param jci6_CPT
* @param jci6_TaskType
* @param jci6_Division
* @param jci6_Year
* @param jci6_Unit
* @param jci6_Section
* @param jci6_RateLevel
* @param jci6_User
* @param jci6_Jan
* @param jci6_Feb
* @param jci6_Mar
* @param jci6_Apr
* @param jci6_May
* @param jci6_Jun
* @param jci6_Jul
* @param jci6_Aug
* @param jci6_Sep
* @param jci6_Oct
* @param jci6_Nov
* @param jci6_Dec
*/

tag_t soa_createCostInfo(char * object_name,char* jci6_CostType, char* jci6_CPT, char* jci6_TaskType, tag_t jci6_Division, int jci6_Year, char* jci6_Unit, tag_t jci6_Section, tag_t jci6_RateLevel, tag_t jci6_User, double jci6_Jan, double jci6_Feb, double jci6_Mar, double jci6_Apr, double jci6_May, double jci6_Jun, double jci6_Jul, double jci6_Aug, double jci6_Sep, double jci6_Oct, double jci6_Nov, double jci6_Dec)
{
	tag_t boTag          = NULLTAG,
		  createInputTag = NULLTAG,
		  itemType       = NULLTAG;
	long double dSetValue = 0.0;
    time_t T;
	struct tm * timenow;
	time ( &T );
	timenow = localtime ( &T );
    int currentYear = 1900+timenow->tm_year;
    tr_ECHO("1965 currentYear=%d,jci6_Year=%d\n",currentYear,jci6_Year);
    if(jci6_Year<currentYear)
    {
        return boTag;
    }
    int currentMonth = timenow->tm_mon+1;
    tr_ECHO("1971 currentMonth=%d\n",currentMonth);
	tr_ECHO("jci6_Jan=%lf,jci6_Feb=%lf,jci6_Mar=%lf,jci6_Apr=%lf\n",jci6_Jan,jci6_Feb,jci6_Mar,jci6_Apr);
	tr_ECHO("jci6_May=%lf,jci6_Jun=%lf,jci6_Jul=%lf,jci6_Aug=%lf\n",jci6_May,jci6_Jun,jci6_Jul,jci6_Aug);
	tr_ECHO("jci6_Sep=%lf,jci6_Oct=%lf,jci6_Nov=%lf,jci6_Dec=%lf\n",jci6_Sep,jci6_Oct,jci6_Nov,jci6_Dec);
    long double jci6_JanNum = 0,
                jci6_FebNum = 0,
                jci6_MarNum = 0,
                jci6_AprNum = 0,
                jci6_MayNum = 0,
                jci6_JunNum = 0,
                jci6_JulNum = 0,
                jci6_AugNum = 0,
                jci6_SepNum = 0,
                jci6_OctNum = 0,
                jci6_NovNum = 0,
                jci6_DecNum = 0;
    if(jci6_Year==currentYear)
    {
	switch (currentMonth)
		{
		    case 12:
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 11:
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 10:
	            soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 9:
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 8:
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 7:
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 6:
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 5:
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 4:
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 3:
                soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 2:
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    case 1:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
                soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    default:
			    break;
		}
	//2016-04-18	mengyawei modify
	//must compute current month and later month
	/*
        switch (currentMonth)
		{
		    case 1:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
			    break;
		    case 2:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
			    break;
		    case 3:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
			    break;
		    case 4:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
			    break;
		    case 5:
                soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
			    break;
		    case 6:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
			    break;
		    case 7:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
			    break;
		    case 8:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
	            soa_DataPrecision(jci6_Aug, &jci6_AugNum);
			    break;
		    case 9:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
	            soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
			    break;
		    case 10:
                soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
	            soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
	            soa_DataPrecision(jci6_Oct, &jci6_OctNum);
			    break;
		    case 11:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
	            soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
	            soa_DataPrecision(jci6_Jul, &jci6_JulNum);
	            soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
	            soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
			    break;
		    case 12:
	            soa_DataPrecision(jci6_Jan, &jci6_JanNum);
                soa_DataPrecision(jci6_Feb, &jci6_FebNum);
	            soa_DataPrecision(jci6_Mar, &jci6_MarNum);
	            soa_DataPrecision(jci6_Apr, &jci6_AprNum);
	            soa_DataPrecision(jci6_May, &jci6_MayNum);
	            soa_DataPrecision(jci6_Jun, &jci6_JunNum);
                soa_DataPrecision(jci6_Jul, &jci6_JulNum);
                soa_DataPrecision(jci6_Aug, &jci6_AugNum);
	            soa_DataPrecision(jci6_Sep, &jci6_SepNum);
                soa_DataPrecision(jci6_Oct, &jci6_OctNum);
	            soa_DataPrecision(jci6_Nov, &jci6_NovNum);
	            soa_DataPrecision(jci6_Dec, &jci6_DecNum);
			    break;
		    default:
			    break;
		}
		*/
    }
    else
    {
        soa_DataPrecision(jci6_Jan, &jci6_JanNum);
        soa_DataPrecision(jci6_Feb, &jci6_FebNum);
        soa_DataPrecision(jci6_Mar, &jci6_MarNum);
        soa_DataPrecision(jci6_Apr, &jci6_AprNum);
        soa_DataPrecision(jci6_May, &jci6_MayNum);
        soa_DataPrecision(jci6_Jun, &jci6_JunNum);
        soa_DataPrecision(jci6_Jul, &jci6_JulNum);
        soa_DataPrecision(jci6_Aug, &jci6_AugNum);
        soa_DataPrecision(jci6_Sep, &jci6_SepNum);
        soa_DataPrecision(jci6_Oct, &jci6_OctNum);
        soa_DataPrecision(jci6_Nov, &jci6_NovNum);
        soa_DataPrecision(jci6_Dec, &jci6_DecNum);
    }
	tr_ECHO("jci6_JanNum=%lf,jci6_FebNum=%lf,jci6_MarNum=%lf,jci6_AprNum=%lf,\n",jci6_JanNum,jci6_FebNum,jci6_MarNum,jci6_AprNum);
	tr_ECHO("jci6_MayNum=%lf,jci6_JunNum=%lf,jci6_JulNum=%lf,jci6_AugNum=%lf,\n",jci6_MayNum,jci6_JunNum,jci6_JulNum,jci6_AugNum);
	tr_ECHO("jci6_SepNum=%lf,jci6_OctNum=%lf,jci6_NovNum=%lf,jci6_DecNum=%lf,\n",jci6_SepNum,jci6_OctNum,jci6_NovNum,jci6_DecNum);
    if(jci6_JanNum==0 && jci6_FebNum==0 && jci6_MarNum==0 && jci6_AprNum==0 && jci6_MayNum==0 && jci6_JunNum==0 
        && jci6_JulNum==0 && jci6_AugNum==0 && jci6_SepNum==0 && jci6_OctNum==0 && jci6_NovNum==0 && jci6_DecNum==0)
    {
        return boTag;
    }
    const char ** costInfoName = (const char **)MEM_alloc(1*sizeof(char*));
	costInfoName[0] = object_name;
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


	tr_ECHO("costInfoName[0]==%s\n",costInfoName[0]);
	TCTYPE_find_type("JCI6_CostInfo","JCI6_CostInfo",&itemType);
	TCTYPE_construct_create_input ( itemType, &createInputTag );
	TCTYPE_set_create_display_value( createInputTag, "object_name", 1, costInfoName );
	TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, costInfoName );

	TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr );
	TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str );
	TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str );

	
	ITKCALL(TCTYPE_create_object ( createInputTag, &boTag) );
	ITKCALL(AOM_save ( boTag ));
	DOFREE(costInfoName);
	DOFREE(unitstr);
	DOFREE(ucpt_str);
	DOFREE(year_str);


	ITKCALL(AOM_lock ( boTag ));
	ITKCALL(AOM_load ( boTag ));
	tr_ECHO("2124 boTag=%u\n",boTag);
	ITKCALL(AOM_set_value_string ( boTag,"jci6_CostType",jci6_CostType ));
	ITKCALL(AOM_set_value_string ( boTag,"jci6_CPT",jci6_CPT ));
	ITKCALL(AOM_set_value_string ( boTag,"jci6_TaskType",jci6_TaskType ));
	ITKCALL(AOM_set_value_tag ( boTag,"jci6_Division",jci6_Division ));
	ITKCALL(AOM_set_value_int ( boTag,"jci6_Year",jci6_Year ));
	ITKCALL(AOM_set_value_string ( boTag,"jci6_Unit",jci6_Unit ));
	ITKCALL(AOM_set_value_tag ( boTag,"jci6_Section",jci6_Section ));
	ITKCALL(AOM_set_value_tag ( boTag,"jci6_RateLevel",jci6_RateLevel ));
	ITKCALL(AOM_set_value_tag ( boTag,"jci6_User",jci6_User ));
	ITKCALL(AOM_set_value_double ( boTag,"jci6_Jan",jci6_JanNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Feb",jci6_FebNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Mar",jci6_MarNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Apr",jci6_AprNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_May",jci6_MayNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Jun",jci6_JunNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Jul",jci6_JulNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Aug",jci6_AugNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Sep",jci6_SepNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Oct",jci6_OctNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Nov",jci6_NovNum));
    ITKCALL(AOM_set_value_double ( boTag,"jci6_Dec",jci6_DecNum));
	
	ITKCALL(AOM_save ( boTag ));
	ITKCALL(AOM_unlock ( boTag ));
	tr_ECHO("2149 boTag=%u\n",boTag);
	return boTag;
}

/**
* 将一个long double类型的数据四舍五入掉
* @param old_double
* return
*/
double soa_DataPrecision_old(const double old_double)
{
	double new_data;

	char old_char[32],
		intNew_char[32],
		fNew_char[4]="\0";
	sprintf(old_char,"%.3lf",old_double);
	char *intPtr   = tc_strtok(old_char,"."),
		*pointPtr = tc_strtok(NULL,".");
	tc_strcpy(intNew_char, intPtr);
	tc_strcpy(fNew_char ,"");
	tc_strcat(fNew_char ,pointPtr);
	bool istrue =false;
	if(fNew_char[2] >= '5')
	{
		istrue = true;
	}
	fNew_char[2] = '0';
	long l;
	l =atol(intNew_char);
	double f;
	f = atof(fNew_char)/1000;
	if(istrue)
	{
		new_data=(double)l+(double)f+0.01;
	}
	else
	{
		new_data=l+f;
	}
	return new_data;
}










void tr_getUserPostion( char * user_id ,tag_t* postion )
{
    char* postions = NULL;

    char *postion_name = NULL,
         **entries     = NULL,
         **values      = NULL,
	     *other_values[1];

    tag_t costvalue  = NULLTAG,
          query_tag  = NULLTAG,
          *results   = NULL;

    int num_found   = 0,
        entry_count = 0;


	const char query_name[ QRY_name_size_c + 1] = YFJC_SEARCHDISCIPLINEBYID;

	ITKCALL(PREF_ask_char_value("YF_Ratelevel", 0, &postions));

    ITKCALL( QRY_find( query_name , &query_tag ) );

    if( query_tag == NULLTAG )
	{
        TC_write_syslog( "没有找到查询构建器 %s\n",query_name);
		return ;
	}

    ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );


    other_values[0] = ( char* )MEM_alloc( ( tc_strlen( user_id ) + 1 ) * sizeof( char ) );

    tc_strcpy( other_values[0] , user_id );
	
	ITKCALL( QRY_execute( query_tag , entry_count , entries , other_values , &num_found , &results ) );

    for( int i = 0 ;i < num_found ; i++ )
    {
        ITKCALL( AOM_ask_value_string( results[i] , OBJECT_NAME , &postion_name ) );

        if( tc_strstr( postions , postion_name ) != NULL )
        {
            *postion = results[i];

            if( postion_name != NULL )
            {
                MEM_free( postion_name );

                postion_name = NULL;
            }
            break;
        }
        else
        {
            if( postion_name != NULL )
            {
                MEM_free( postion_name );

                postion_name = NULL;
            }
        }
    }


    if( other_values[0] != NULL )
    {
        MEM_free( other_values[0] );

        other_values[0] = NULL;
    }

    if( results != NULL )
    {
        MEM_free( results );
        results = NULL;
    }

	if (postions != NULL)
	{
		MEM_free(postions);
		postions = NULL;
	}
}




/**********************************************************************/
/*																	  */
/*  Function Name:   tr_split     							  		  */
/*																	  */
/*  Program ID:      yfjc_autogenerate_newforecast_date_itk_main.c	  */
/*																	  */
/*  Description:     分割字符串										  */
/*					 split string						    		  */
/*																	  */
/*  Parameters:      string strArg			<I>Need to split strings  */
/*				     string spliter			<I>segmentation string    */
/*					 vector<string> &ans	<O>segmentation results   */
/*																	  */
/*  Return Value:   void											  */
/*																	  */
/*  Special Logic Notes:  None										  */
/*																	  */
/**********************************************************************/
void tr_split( string strArg, string spliter, vector<string> &ans)
{
	ans.clear();
	size_t index0;
	string one_arg;
	if ( strArg.find_first_not_of(' ') == string::npos )
		strArg = "";
	while( strArg.size()>0 )
	{
		index0 = strArg.find(spliter);
		if( index0 != string::npos )
		{
			one_arg = strArg.substr( 0, index0 );
			strArg = strArg.substr( index0 + spliter.size() );
			ans.push_back( one_arg );
		}
		else
		{
			ans.push_back( strArg );
			break;
		}
	}
}


//2014-8-7  得到外包代表的值
void getExtUser(vector<string>  &extUserVec)
{
	int pref_value_cnt = 0;
	char **pref_vals = NULL;
	string pref_val,username;
	vector<string> val_split_vec;
	ITKCALL(PREF_ask_value_count(YFJC_EXT_USER_MAPPING_LIST,&pref_value_cnt));
	if ( pref_value_cnt == 0 )
	{
		printf("首选项%s未配置或值未设置\n",YFJC_EXT_USER_MAPPING_LIST);
		tr_ECHO("首选项%s未配置或值未设置\n",YFJC_EXT_USER_MAPPING_LIST);
	}else
	{
		printf("首选项%s已设值\n",YFJC_EXT_USER_MAPPING_LIST);
		tr_ECHO("首选项%s已设值\n",YFJC_EXT_USER_MAPPING_LIST);
		ITKCALL(PREF_ask_char_values(YFJC_EXT_USER_MAPPING_LIST,&pref_value_cnt,&pref_vals));
		for(int i=0;i<pref_value_cnt;i++)
		{
			tr_ECHO("pref_vals[i]=%s\n",pref_vals[i]);
			pref_val.assign(pref_vals[i]);
			val_split_vec.clear();
			tr_split(pref_val,"=",val_split_vec);
			if(val_split_vec.size()== 2)
			{
				username.assign(val_split_vec[0].c_str());
				extUserVec.push_back(username);
			}
		}
		if(pref_vals != NULL)
		{
			 MEM_free( pref_vals );
			 pref_vals = NULL;
		}
	}
}

//判断是否为外包代表
logical isExtUser(char *jci6_User,vector<string> extUserVec,int extCnt)
{
	for(int i=0;i<extCnt;i++)
	{
		if(strcmp(jci6_User,extUserVec[i].c_str()) == 0)
		{
			return TRUE;
		}
	}
	return FALSE;
}


void tr_ask_opt_debug()
{
    logical preValue = FALSE;
    ITKCALL( PREF_ask_logical_value( "YFJC_ISDEBUG", 0, &preValue ) );
    if( preValue )
    {
        isWriteLog = TRUE;
    }
}


//调用查询根据学科名查找学科
void getExtUserByDiscipline(vector<tag_t>  &extUserVec)
{
	 char *group_name_str = "ExtSupporter";
	 int entry_count = 0;
	 char **entries = NULL,**values=NULL,*other_entries[1],*other_values[1];
	 tag_t query_tag = NULLTAG;
	 ITKCALL( QRY_find( "YFJC_Search_Discipline" , &query_tag ) );
     if( query_tag == NULLTAG )
     {
         printf("没有找到查询构建器YFJC_Search_Discipline\n");
         return ;
     }
     ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );
     other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( entries[0] ) + 1 ) * sizeof( char ) );
     tc_strcpy( other_entries[0] , entries[0] );
     other_values[0] = ( char* )MEM_alloc( ( tc_strlen( group_name_str ) + 1 ) * sizeof( char ) );
     tc_strcpy( other_values[0] , group_name_str );
     DOFREE( entries);
     DOFREE( values);
     int num_found = 0;
     tag_t *results = NULL;
     ITKCALL(QRY_execute( query_tag , 1 , other_entries , other_values , &num_found , &results ) );
	 //tr_ECHO("YFJC_Search_Discipline查到的学科个数为%d\n",num_found);
	 for(int i=0;i<num_found;i++)
	 {
		//得到学科下的user
		int usercnt = 0;
		tag_t *values = NULLTAG;
		ITKCALL(AOM_ask_value_tags(results[i],TC_DISCIPLINE_MEMBER,&usercnt,&values));
		for(int j=0;j<usercnt;j++)
		{
			extUserVec.push_back(values[j]);
		}
		DOFREE(values);
	 }
	 DOFREE(results);
     DOFREE( other_entries[0]);
     DOFREE( other_values[0]);
}

//判断是否为外包代表
logical tr_isExtUser(tag_t user,vector<tag_t> extUserVec,int extCnt)
{
	for(int i=0;i<extCnt;i++)
	{
		if(user == extUserVec[i])
		{
			return TRUE;
		}
	}
	return FALSE;
}
