 /*
#===========================================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#===========================================================================================
# File name: yfjc_ebp_updateCostAfterLogTime.cxx
# File description: 										   	
#===========================================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-14	zhangyn  		Ini		function for Man-hour Report count cost		
#	2013-4-08	zhangyn  		mod		costinfo_name
#	2013-4-13	    zhanggl     mod		division name
                                        Schedule must be assign project and person must be in
										project team
#	2014-4-08	zhangyn  		mod		过滤比较换为obid，如果status为released并且timesheet已发布，过滤
#===========================================================================================
 */
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

////SP-EXT-FUN-17.工时填报产生费用

//比较date, hour , user, group, name, bill type,jci6_Schedule属性，将重复的timeSheet过滤
void timesheetFilter_old(tag_t timeSheet,vector<tag_t> * timeSheet_vec)
{
	int timesheetNum = (*timeSheet_vec).size();

	if(timesheetNum==0)
	{
                ECHO("timesheetNum==0  first\n");
		(*timeSheet_vec).push_back(timeSheet);
	}
	else
	{
                int isExist = 0;//是否已经存在

		for( int i = 0 ; i < timesheetNum ; i++ )
		{
			char * object_name  = NULL;
			tag_t  user_tag     = NULLTAG;
			tag_t  owning_group = NULLTAG;
			date_t date;
			int    year         = 0;
			int    month        = 0;
			int    day          = 0;
			int    hour         = 0;
			int    minute       = 0;
			int    second       = 0;
			char * bill_type  = NULL;
			int    minutes      = 0;
                        char * jci6_Schedule=NULL;

			char * old_object_name  = NULL;
			tag_t  old_user_tag     = NULLTAG;
			tag_t  old_owning_group = NULLTAG;
			date_t old_date;
			int    old_year         = 0;
			int    old_month        = 0;
			int    old_day          = 0;
			int    old_hour         = 0;
			int    old_minute       = 0;
			int    old_second       = 0;
			char * old_bill_type  = NULL;
			int    old_minutes      = 0;
                        char * old_jci6_Schedule=NULL;

			tag_t old_tag = (*timeSheet_vec)[i];

			ITKCALL( AOM_ask_value_string( timeSheet , OBJECT_NAME , &object_name ) );//名称
			ITKCALL( AOM_ask_value_tag( timeSheet , USER_TAG , &user_tag ) );//填报用户
			ITKCALL( AOM_ask_value_tag( timeSheet , OWNING_GROUP , &owning_group ) );//填报用户组
			ITKCALL( AOM_ask_value_date( timeSheet , DATE , &date ) );//填报日期
			year   = date.year;//年
			month  = date.month;//月
			day    = date.day;//天
			hour   = date.hour;//小时
			minute = date.minute;//分钟
			second = date.second;//秒
			ITKCALL( AOM_ask_value_string( timeSheet , BILL_TYPE , &bill_type ) );//清单类型
			ITKCALL( AOM_ask_value_int( timeSheet , MINUTES , &minutes ) );//填报工时
                        ITKCALL( AOM_ask_value_string( timeSheet , "jci6_Schedule" , &jci6_Schedule ) );

			ITKCALL( AOM_ask_value_string( old_tag , OBJECT_NAME , &old_object_name ) );//名称
			ITKCALL( AOM_ask_value_tag( old_tag , USER_TAG , &old_user_tag ) );//填报用户
			ITKCALL( AOM_ask_value_tag( old_tag , OWNING_GROUP , &old_owning_group ) );//填报用户组
			ITKCALL( AOM_ask_value_date( old_tag , DATE , &old_date ) );//填报日期
			old_year   = old_date.year;//年
			old_month  = old_date.month;//月
			old_day    = old_date.day;//天
			old_hour   = old_date.hour;//小时
			old_minute = old_date.minute;//分钟
			old_second = old_date.second;//秒
			ITKCALL( AOM_ask_value_string( old_tag , BILL_TYPE , &old_bill_type ) );//清单类型
			ITKCALL( AOM_ask_value_int( old_tag , MINUTES , &old_minutes ) );//填报工时
                        ITKCALL( AOM_ask_value_string( old_tag , "jci6_Schedule" , &old_jci6_Schedule ) );

                        ECHO("object_name = %s,user_tag = %u,owning_group = %u,year = %d,month = %d,day = %d,hour = %d,minute = %d,second = %d,bill_type = %s,minutes = %d,jci6_Schedule = %s \n", object_name ,user_tag,owning_group,year,month,day,hour,minute,second,bill_type,minutes,jci6_Schedule );
                        ECHO("old_object_name = %s,old_user_tag = %u,old_owning_group = %u,old_year = %d,old_month = %d,old_day = %d,old_hour = %d,old_minute = %d,old_second = %d,old_bill_type = %s,old_minutes = %d,old_jci6_Schedule = %s \n", old_object_name ,old_user_tag,old_owning_group,old_year,old_month,old_day,old_hour,old_minute,old_second,old_bill_type,old_minutes,old_jci6_Schedule );

			if( tc_strcmp( object_name, old_object_name ) == 0 && user_tag == old_user_tag && owning_group == old_owning_group && year == old_year && month == old_month && day == old_day && hour == old_hour && minute == old_minute && second == old_second && tc_strcmp( bill_type, old_bill_type ) == 0 && minutes == old_minutes && tc_strcmp( jci6_Schedule, old_jci6_Schedule ) == 0 )
			{
                            isExist = 1;    
			}
                        if( object_name != NULL )
                        {
                            MEM_free( object_name );
                            object_name = NULL;
                        }
                        if( bill_type != NULL )
                        {
                            MEM_free( bill_type );
                            bill_type = NULL;
                        }
                        if( jci6_Schedule != NULL )
                        {
                            MEM_free( jci6_Schedule );
                            jci6_Schedule = NULL;
                        }
                        if( old_object_name != NULL )
                        {
                            MEM_free( old_object_name );
                            old_object_name = NULL;
                        }
                        if( old_bill_type != NULL )
                        {
                            MEM_free( old_bill_type );
                            old_bill_type = NULL;
                        }
                        if( old_jci6_Schedule != NULL )
                        {
                            MEM_free( old_jci6_Schedule );
                            old_jci6_Schedule = NULL;
                        }
		}
                ECHO("isExist = %d\n", isExist);
                ECHO("--------------------------------------------------------\n");
                if( isExist == 0 )
                {
					  ECHO("timesheet_vec add \n" );
                    (*timeSheet_vec).push_back(timeSheet);
                }
	}
}

//通过OBID，将重复的timeSheet过滤
void timesheetFilter(tag_t timeSheet,vector<tag_t> * timeSheet_vec)
{
	int timesheetNum = (*timeSheet_vec).size();

	if(timesheetNum==0)
	{
        ECHO("timesheetNum==0  first\n");
		(*timeSheet_vec).push_back(timeSheet);
	}
	else
	{
        int isExist = 0;//是否已经存在

		for( int i = 0 ; i < timesheetNum ; i++ )
		{
			char * tag_id  = NULL;
			
			char * old_tag_id  = NULL;

			tag_t old_tag = (*timeSheet_vec)[i];

			ITKCALL( AOM_tag_to_string( timeSheet , &tag_id ) );//OBID

            ITKCALL( AOM_tag_to_string( old_tag , &old_tag_id ) );//OBID

            if( tc_strcmp( tag_id, old_tag_id ) == 0)
			{
                isExist = 1;    
			}
            
            if( tag_id != NULL )
            {
                MEM_free( tag_id );
                tag_id = NULL;
            }
            if( old_tag_id != NULL )
            {
                MEM_free( old_tag_id );
                old_tag_id = NULL;
            }
		}
       // ECHO("isExist = %d\n", isExist);
      //  ECHO("--------------------------------------------------------\n");
        if( isExist == 0 )
        {
		    ECHO("timesheet_vec add \n" );
            (*timeSheet_vec).push_back(timeSheet);
        }
	}
}

//工时填报产生费用
int JCI6updateCostAfterLogTime( EPM_action_message_t msg )
{
	int ifail                   = ITK_ok,
        count                   = 0,
        *attachment_types       = NULL,
        att_cnt                 = 0,
		arg_count               = 0,
        timesheetNum            = 0;

    double  default_rate        = 0;

	char  *arg_str_value        = NULL,
          *status               = NULL,
          *attachment_type      = NULL;

	tag_t rootTask              = NULLTAG,
          job_tag               = NULLTAG,
          *attachments          = NULL,
          *release_status_list  = NULL,
		  timeSheet             = NULLTAG;

	vector<tag_t> timeSheet_vec;

	ask_opt_debug();
	ECHO("JCI6updateCostAfterLogTime 流程开始 \n" );

    arg_count = TC_number_of_arguments( msg.arguments ); 
	//ECHO("arg_count = %d\n", arg_count );
    if( arg_count != 1)
    {
        return ifail;
    }
    arg_str_value = TC_next_argument( msg.arguments );
	//ECHO("arg_str_value = %s\n", arg_str_value );
  
	status = tc_strstr( arg_str_value , "=" );
    status = status + 1;

    ITKCALL(EPM_ask_root_task( msg.task , &rootTask ) );

    CALL(ifail = EPM_ask_job(msg.task, &job_tag));

    CALL(ifail = EPM_ask_all_attachments(rootTask, &att_cnt, &attachments, &attachment_types));
    if(attachment_types!=NULL)
    {
	    MEM_free(attachment_types);
	    attachment_types = NULL;
    }
	

	// force to refresh
	//CALL(ifail = EPM_refresh_entire_job(job_tag));
    ITKCALL(ifail = AOM_refresh(job_tag, FALSE));
	for (int i=0;i<att_cnt;i++)
	{
		CALL(ifail = AOM_refresh(attachments[i], FALSE));
	}
    if(attachments!=NULL)
    {
	    MEM_free(attachments);
	    attachments = NULL;
    }

    ITKCALL(EPM_ask_attachments( rootTask , EPM_target_attachment , &count , &attachments ) );	

	//ECHO("count = %d\n", count );

    for( int i = 0 ; i < count ; i++ )
    {       
        ITKCALL( AOM_ask_value_string( attachments[i] , OBJECT_TYPE , &attachment_type ) );
		//ECHO("attachment_type = %s\n", attachment_type );
        if( tc_strcmp( TIMESHEETENTRY , attachment_type ) == 0 )
        {
            timeSheet = attachments[i];
            timesheetFilter(timeSheet,&timeSheet_vec);
            //getTimeSheetToCount( timeSheet , status );
        }
        if( attachment_type != NULL )
		{
			MEM_free( attachment_type );
			attachment_type = NULL;
		}
    }

    timesheetNum = timeSheet_vec.size();
  //  ECHO("timesheetNum = %d\n", timesheetNum );

    for( int i = 0 ; i < timesheetNum ; i++ )
    {
        
        if( tc_strcmp( "release" , status ) == 0 )//发布
        {
            int status_num = 0;

            tag_t* release_status = NULL;

            ITKCALL( AOM_ask_value_tags(timeSheet_vec[i],"release_status_list",&status_num,&release_status));
            if( status_num > 0 )
            {
                continue;
            }
            
            if( release_status != NULL )
		    {
			    MEM_free( release_status );
			    release_status = NULL;
		    }
            
        }

        getTimeSheetToCount( timeSheet_vec[i] , status );  
    }

	ECHO("JCI6updateCostAfterLogTime 流程结束 \n" );
	return ifail;
}





#ifdef __cplusplus
}
#endif