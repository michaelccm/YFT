/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_ebp_timesheetEntryControl.cxx
    Module  : user_exits

============================================================================================================
DATE           Name             Description of Change
08-May-2013    liujm          creation
15-Nov-2013    zhanggl          Error: Invalid trailing byte 80, because STRNG_replace_str
============================================================================================================*/
#include "yfjc_ebp_head.h"

#include <metaframework/CreateInput.hxx>

using namespace Teamcenter;

#ifdef __cplusplus
extern "C" {
#endif




char* rtrim(char *s)
{// 去掉尾部的空格
    char buf[1024];
    strcpy(buf,s);

    int l=0,p=0;
    l = strlen(buf);
    if( l == 0 ) 
        return NULL;
    p = l -1;
    while( buf[p] == ' ' || buf[p] == '\t'|| buf[p] == '\r' || buf[p] == '\n') {
        buf[p--] = '\0';
        if( p < 0 ) break;
    }
    return buf;
}


//int JCI6TimesheetMinAlert( METHOD_message_t *msg, va_list args )
//{
//    int ifail      = ITK_ok;
//
//    tag_t timesheetentry_tag    = NULLTAG,
//          user_tag              = NULLTAG,
//		//change by ck 2016-04-0
//		scheduletask_tag		= NULLTAG,
//		//change by ck 2016-04-07
//          prop_tag              = va_arg( args, tag_t );
//    int   minutesProValue       = va_arg( args, int );
//    date_t date = NULLDATE;
//
//	ask_opt_debug();
//    ECHO("*****************JCI6TimesheetMinAlert start************************\n");
//
//    METHOD_PROP_MESSAGE_OBJECT(msg, timesheetentry_tag);
//    ITKCALL(AOM_ask_value_date(timesheetentry_tag, TIMESHEET_DATE, &date));
//    ITKCALL(AOM_ask_value_tag(timesheetentry_tag, TIMESHEET_USERTAG, &user_tag));
//    //change by ck 2016-04-07
//	ITKCALL(AOM_ask_value_tag(timesheetentry_tag, TIMESHEET_SCHEDULETASK, &scheduletask_tag));
//	//change by ck 2016-04-07
//    ECHO("user_tag=%u\n",user_tag);
//    if(user_tag!=NULLTAG&& scheduletask_tag!=NULLTAG)
//	{
//        char *user_id = NULL;
//	char *scheduletask_id = NULL;
//        ITKCALL(AOM_ask_value_string(user_tag, USER_ID, &user_id));
//        //int minutesCount = getMinutesForDay(timesheetentry_tag,user_id,date);
//		//change by ck 2016-04-07
//		int minutesCount = getMinutesForDay(timesheetentry_tag,user_id,date);
//		//change by ck 2016-04-07
//        tag_t position = NULLTAG;
//        getUserPostion(user_id ,&position );
//        ECHO("position = %u\n",position);
//        if(position)
//        {
//        	char *postion_name   = NULL;
//            ITKCALL( AOM_ask_value_string( position , OBJECT_NAME , &postion_name ) );
//           
//            char *newRightStr = NULL;
//            getPreValue(postion_name,user_id,&newRightStr);
//            char part1[30] = "",
//                 part2[30] = "",
//                 part3[30] = "",
//                 part4[30] = "";
//            sscanf( newRightStr, "%[^{]{;}%[^{]{;}%[^{]{;}%[^{]", part1,part2,part3,part4);
//            
//            if(tc_strcmp(part1,part2)==0)
//            {
//				if( postion_name )
//	        	{
//		        	MEM_free( postion_name );
//		        	postion_name = NULL;
//	        	}
//	        	 if( user_id )
//				{	
//					MEM_free( user_id );
//					user_id = NULL;
//				}
//				ITKCALL( AOM_lock( timesheetentry_tag ) );
//				ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
//				ITKCALL( AOM_save( timesheetentry_tag ) );
//				ITKCALL( AOM_unlock( timesheetentry_tag ) );
//				ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
//			//change by ck 2016-04-07
//	    		//return ifail;
//			//change by ck 2016-04-07
//        	
//            }
//        }
//        if( user_id )
//	    {
//		    MEM_free( user_id );
//		    user_id = NULL;
//	    }
//        ECHO("minutesCount = %d,minutesProValue = %d\n",minutesCount,minutesProValue);
//        if(minutesCount<480&&minutesCount+minutesProValue>480)
//        {
//        		int billType = getBillTypeByDate(date);
//        		if(billType==1)
//        		{
//        		 	HANDLE_ERROR( ERROR_OVER_EIGHT_HOURS );
//        		} 
//        }
//        ITKCALL( AOM_lock( timesheetentry_tag ) );
//        ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
//	    ITKCALL( AOM_save( timesheetentry_tag ) );
//	    ITKCALL( AOM_unlock( timesheetentry_tag ) );
//	    ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
//    }
//    ECHO("*****************JCI6TimesheetMinAlert end*****************\n");
//
//    return ifail;
//}

//this is the real preAction for creating timesheetEntry


void getPreValue(char *postion_name,char* user_id,char **newRightStr)
{
	char *rightPrefItem  = NULL,
         **option_values = NULL;
   
    int option_value_count = 0;
    ITKCALL(PREF_ask_char_values(YFJC_USER_RATE_MAPPING,&option_value_count,&option_values));
    ECHO("postion_name = %s\n",postion_name);
    if(tc_strcmp(postion_name,"Manager")==0||tc_strcmp(postion_name,"Engineer Assistant")==0)
    {
        for(int i = 0; i < option_value_count; i++)
        {
            if(tc_strstr(option_values[i],postion_name)!=NULL)
            {
				rightPrefItem = ( char* )MEM_alloc( ( tc_strlen( option_values[i] ) + 1 ) * sizeof( char ) );
				tc_strcpy(rightPrefItem,option_values[i]);
                //*rightPrefItem = option_values[i];
                break;
            }
        }
    }
    else if(tc_strcmp(postion_name,"ExtSupporter")==0)
    {
        char *extTmp = NULL;
        char *extTmp2 = NULL;
        for(int i = 0; i < option_value_count; i++)
        {
            if(tc_strstr(option_values[i],postion_name)!=NULL)
            {
                extTmp = option_values[i];
                if(tc_strstr(option_values[i],user_id)!=NULL)
                {
                    extTmp2 = option_values[i];
                }
            }
         }
        if(extTmp2!=NULL)
        {
			rightPrefItem = ( char* )MEM_alloc( ( tc_strlen( extTmp2 ) + 1 ) * sizeof( char ) );
			tc_strcpy(rightPrefItem,extTmp2);
        }
        else  if(extTmp!=NULL)
        {
			rightPrefItem = ( char* )MEM_alloc( ( tc_strlen( extTmp ) + 1 ) * sizeof( char ) );
			tc_strcpy(rightPrefItem,extTmp);
        }  
    }
    else
    {
        for(int i = 0; i < option_value_count; i++)
        {
            if(tc_strstr(option_values[i],"General")!=NULL)
            {
				rightPrefItem = ( char* )MEM_alloc( ( tc_strlen( option_values[i] ) + 1 ) * sizeof( char ) );
				tc_strcpy(rightPrefItem,option_values[i]);
                break;
            }
        }
    }
    ECHO("rightPrefItem=%s\n",rightPrefItem);
    char  left[30] = "",
          right[200] = "";
          sscanf(rightPrefItem, "%[^=]=%[^=]", left,right );
          ECHO("left=%s,right=%s\n",left,right);
    char *newRightStr2 = NULL;
    /*ITKCALL(STRNG_replace_str(right,"；",";",&newRightStr2));
	*newRightStr = ( char* )MEM_alloc( ( tc_strlen( newRightStr2 ) + 1 ) * sizeof( char ) );
	tc_strcpy(*newRightStr,newRightStr2);*/
	*newRightStr = ( char* )MEM_alloc( ( tc_strlen( right ) + 1 ) * sizeof( char ) );
	tc_strcpy(*newRightStr,right);
	if( option_values )
	{
		MEM_free( option_values );
		option_values = NULL;
	}
}
//根据参数中的日期确定是工作日(返回1)，节假日(返回2)还是周末(返回3)
int getBillTypeByDate(date_t date)
{// 去掉所有回车
	int ifail = 0;
	string datatime;
   	tag_t tccalendar = NULLTAG;
	


	ECHO("%s--getBillTypeByDate-----开始 \n" , getNowtime2().c_str() );



	ECHO("%s--TCCALENDAR_get_base_tccalendar-----开始 \n" , getNowtime2().c_str() );

	ITKCALL(TCCALENDAR_get_base_tccalendar(&tccalendar));

	
	ECHO("%s--TCCALENDAR_get_base_tccalendar-----结束 \n" , getNowtime2().c_str() );

	int no_of_nonwd = 0;
	if(tccalendar!=NULLTAG)
	{
		char fromDate[30] = "",
			 toDate[30]   = "",
			 **nonWorkingDates =NULL;
        date_t tomorrowDate = NULLDATE;
        GetBeforeDate(date,-1,&tomorrowDate);
		int year1 = date.year,
			month1 = date.month+1,
			day1 = date.day,
			year2 = tomorrowDate.year,
			month2 = tomorrowDate.month+1,
			day2 = tomorrowDate.day;
		ECHO("date1===%d-%d-%d！\n",year1,month1,day1);
		ECHO("date2===%d-%d-%d！\n",year2,month2,day2);

		if(month1<10)
		{
			if(day1<10)
			{
				sprintf( fromDate, "%d%s%d%s%d%s",year1,"-0",month1,"-0",day1," 00:00:00");
			}
			else
			{
				sprintf( fromDate, "%d%s%d%s%d%s",year1,"-0",month1,"-",day1," 00:00:00");
			}
		}
		else
		{
			if(day1<10)
			{
				sprintf( fromDate, "%d%s%d%s%d%s",year1,"-",month1,"-0",day1," 00:00:00");
			}
			else
			{
				sprintf( fromDate, "%d%s%d%s%d%s",year1,"-",month1,"-",day1," 00:00:00");
			}
		}
		if(month2<10)
		{
			if(day2<10)
			{
				sprintf( toDate, "%d%s%d%s%d%s",year2,"-0",month2,"-0",day2," 00:00:00");
			}
			else
			{
				sprintf( toDate, "%d%s%d%s%d%s",year2,"-0",month2,"-",day2," 00:00:00");
			}
		}
		else
		{
			if(day2<10)
			{
				sprintf( toDate, "%d%s%d%s%d%s",year2,"-",month2,"-0",day2," 00:00:00");
			}
			else
			{
				sprintf( toDate, "%d%s%d%s%d%s",year2,"-",month2,"-",day2," 00:00:00");
			}
		}


		
		ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----开始 \n" , getNowtime2().c_str() );

		ITKCALL(TCCALENDAR_get_resource_non_working_dates(tccalendar,fromDate,toDate,&no_of_nonwd,&nonWorkingDates));
	
		
		ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----结束 \n" , getNowtime2().c_str() );
		
		if(nonWorkingDates)
		{
			MEM_free(nonWorkingDates);
			nonWorkingDates = NULL;
		}
        if(no_of_nonwd==0)
        {
            return 1;
        }
        else
        {
            int option_value_count = 0;
            char **option_values = NULL,
                 dateStr[15]   = "";
            ITKCALL(ifail = PREF_ask_char_values(YFJC_HOLIDAY_ALSO_WEEKEND,&option_value_count,&option_values));
            if(ifail)
		    {	
			    ECHO("not found preference %s \n",YFJC_HOLIDAY_ALSO_WEEKEND);
		    }
            sprintf( dateStr, "%d%s%d%s%d",year1,"-",month1,"-",day1);
            for(int i = 0; i < option_value_count; i++)
            {
                char *option_value = rtrim(option_values[i]);
                ECHO("option_value==%s \n",option_value);
                if(tc_strcmp(option_value,dateStr)==0)
                {
                   return 2;
                }
            }
            return 3;
        }
	}

	
	ECHO("%s--getBillTypeByDate-----结束 \n" , getNowtime2().c_str() );
    return 0;
}

//得到一个日期多少小时之前的日期
void GetBeforeEightHoursDate( date_t current_date ,int beforehours , date_t *before_Date )
{
    struct tm * target_time;
    struct tm * p;
    time_t rawtime;
    time_t t1;

	int year         = 0,
	    month        = 0,
	    day          = 0,
	    hour         = 0,
	    minute       = 0,
	    second       = 0,
        beforemonths = 0;

	year = current_date.year;
	month = current_date.month;
	day = current_date.day;
	hour = current_date.hour;
	minute = current_date.minute;
	second = current_date.second;

    time ( &rawtime );
    target_time = localtime ( &rawtime ); // 其它参数

    target_time->tm_year = year - 1900; //年
    target_time->tm_mon= month ;     // 月
    target_time->tm_mday = day ;   // 日
    target_time->tm_hour = hour ;   // 时
    target_time->tm_min = minute ; // 分
    target_time->tm_sec = second ;// 秒

    t1 = mktime (target_time);

    t1 = t1-beforehours*60*60;
    p = localtime(&t1);	

	before_Date->year    = 1900+p->tm_year;
	before_Date->month   = p->tm_mon;
	before_Date->day     = p->tm_mday;
	before_Date->hour    = p->tm_hour;
	before_Date->minute  = p->tm_min;
	before_Date->second  = p->tm_sec;
}


//change by ck 2016-04-07 
int getMinutesForDayAndSchedule(tag_t timesheetentry_tag,char* user_id,date_t date,char* scheduletask_id)
{
	const char query_name[ QRY_name_size_c + 1] = YFJC_SEARCHTIMESHEETENTRY_BYSCHEDULE;
	tag_t query_tag    = NULLTAG,
              *results     = NULL;
        char  **entries    = NULL,
              **values     = NULL,
              *dateStr      = NULL;
        int   num_found    = 0,
              entry_count  = 0;
        date_t newdate = NULLDATE;
        GetBeforeEightHoursDate(date,8,&newdate);
		
		ITKCALL( QRY_find( query_name , &query_tag ) );
		
		if( query_tag == NULLTAG )
	    {
            TC_write_syslog( "Do note find search build: %s\n",query_name);
	    }else
		{
			ECHO("Find search build: %s\n",query_name);
		}
		
		ITKCALL(QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

        ITKCALL(ITK_date_to_string( newdate, &dateStr ));
		
		//split the given time ,used to search before and after by ck
		int count_dateStr_split = 0;
		char** value_dateStr_split = NULL;
		EPM__parse_string(dateStr," ",&count_dateStr_split , &value_dateStr_split);

        ECHO("dateStr=%s,user_id=%s\n",dateStr,user_id);
		//if(scheduletask_id != NULL)
		//{
			ECHO("561 \n");
			char   *other_entries[4],
              *other_values[4];

			for(int ii=0 ;ii < entry_count;ii++)
			{
				ECHO("entries[%d]: %s\n",ii,entries[ii]);
			}
			
			for(int ii=0 ;ii < entry_count;ii++)
			{
				if(!tc_strcmp(entries[ii],"date_before"))
				{
					//ECHO("entries %d : %s\n",ii,entries[ii]);
					other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( "date_before" ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_entries[0] ,"date_before" );

					other_values[0] = ( char* )MEM_alloc( ( tc_strlen( dateStr ) + 1 ) * sizeof( char ) );

					

					sprintf( other_values[0] ,"%s 00:00" , value_dateStr_split[0] );
				}

				else if(!tc_strcmp(entries[ii],"date_after"))
				{
					//ECHO("entries %d : %s\n",ii,entries[ii]);
					other_entries[3] = ( char* )MEM_alloc( ( tc_strlen( "date_after" ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_entries[3] ,"date_after" );

					other_values[3] = ( char* )MEM_alloc( ( tc_strlen( dateStr ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_values[3] , dateStr );
					sprintf( other_values[3] ,"%s 23:59" , value_dateStr_split[0] );
				}


				else if(!tc_strcmp(entries[ii],"Bill Type"))
				{
					other_entries[1] = ( char* )MEM_alloc( ( tc_strlen( "Bill Type" ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_entries[1] , "Bill Type" );

					other_values[1] = ( char* )MEM_alloc( ( tc_strlen( "Normal Hours" ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_values[1] , "Normal Hours" );
				}

				else if(!tc_strcmp(entries[ii],"UID"))
				{
					other_entries[2] = ( char* )MEM_alloc( ( tc_strlen( "UID" ) + 1 ) * sizeof( char ) );

					tc_strcpy( other_entries[2] , "UID" );

					other_values[2] = ( char* )MEM_alloc( ( tc_strlen(user_id) + 1 ) * sizeof( char ) );

					tc_strcpy( other_values[2] , user_id );
				}
				/*
				else if(!tc_strcmp(entries[ii],"TaskID"))
				{
					other_entries[3] = ( char* )MEM_alloc( ( tc_strlen( "TaskID" ) + 1 ) * sizeof( char ) );

       				tc_strcpy( other_entries[3] , "TaskID" );


					other_values[3] = ( char* )MEM_alloc( ( tc_strlen(scheduletask_id) + 1 ) * sizeof( char ) );

        				tc_strcpy( other_values[3] , scheduletask_id );
				}
				*/
			}
			for(int jj = 0; jj < 4 ; jj++ )
			{
				ECHO("other_entries[%d] = %s\n" , jj , other_entries[jj]);
				ECHO("other_values[%d] = %s\n" , jj , other_values[jj]);
			}

		
			ITKCALL( QRY_execute( query_tag , 4, other_entries , other_values , &num_found , &results ) );

			ECHO("616 \n");

			 if( other_entries[0]  )
			{
			    MEM_free( other_entries[0] );

			    other_entries[0] = NULL;
			}

			if( other_values[0]  )
			{
			    MEM_free( other_values[0] );

			    other_values[0] = NULL;
			}

			if( other_entries[1]  )
			{
			    MEM_free( other_entries[1] );

			    other_entries[1] = NULL;
			}

			if( other_values[1]  )
			{
			    MEM_free( other_values[1] );

			    other_values[1] = NULL;
			}

			if( other_entries[2]  )
			{
			    MEM_free( other_entries[2] );

			    other_entries[2] = NULL;
			}

			if( other_values[2]  )
			{
			    MEM_free( other_values[2] );

			    other_values[2] = NULL;
			}
		
				if( other_entries[3]  )
			{
			    MEM_free( other_entries[3] );

			    other_entries[3] = NULL;
			}

			if( other_values[3]  )
			{
			    MEM_free( other_values[3] );

			    other_values[3] = NULL;
			}
		
			ECHO("674 \n");
/***
		}
		else
		{
			
			char  *other_entries[3],
            	  *other_values[3];

			other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( entries[0] ) + 1 ) * sizeof( char ) );

			tc_strcpy( other_entries[0] , entries[0] );

			other_values[0] = ( char* )MEM_alloc( ( tc_strlen( dateStr ) + 1 ) * sizeof( char ) );

			tc_strcpy( other_values[0] , dateStr );

			other_entries[1] = ( char* )MEM_alloc( ( tc_strlen( entries[1] ) + 1 ) * sizeof( char ) );

			tc_strcpy( other_entries[1] , entries[1] );

			other_values[1] = ( char* )MEM_alloc( ( tc_strlen( "Normal Hours" ) + 1 ) * sizeof( char ) );

			tc_strcpy( other_values[1] , "Normal Hours" );

			other_entries[2] = ( char* )MEM_alloc( ( tc_strlen( entries[3] ) + 1 ) * sizeof( char ) );

			tc_strcpy( other_entries[2] , entries[3] );

			other_values[2] = ( char* )MEM_alloc( ( tc_strlen(user_id) + 1 ) * sizeof( char ) );

			tc_strcpy( other_values[2] , user_id );
	
			ITKCALL( QRY_execute( query_tag , 3, other_entries , other_values , &num_found , &results ) );

			if( other_entries[0]  )
			{
			    MEM_free( other_entries[0] );

			    other_entries[0] = NULL;
			}

			if( other_values[0]  )
			{
			    MEM_free( other_values[0] );

			    other_values[0] = NULL;
			}

			if( other_entries[1]  )
			{
			    MEM_free( other_entries[1] );

			    other_entries[1] = NULL;
			}

			if( other_values[1]  )
			{
			    MEM_free( other_values[1] );

			    other_values[1] = NULL;
			}

			if( other_entries[2]  )
			{
			    MEM_free( other_entries[2] );

			    other_entries[2] = NULL;
			}

			if( other_values[2]  )
			{
			    MEM_free( other_values[2] );

			    other_values[2] = NULL;
			}

			
			ECHO("Does not find the schedule task which is needed!!!!!!");
		}
**/
        ECHO("num_found = %d\n",num_found);
        int minutesCount = 0;
        for( int i = 0 ;i < num_found ; i++ )
        {
            if( results[i]!= timesheetentry_tag){
                int min = 0;
                ITKCALL( AOM_ask_value_int( results[i] , TIMESHEET_MINUTES , &min ) );
                ECHO("min = %d\n",min);
                minutesCount = minutesCount+min;
            }
           
        }
        ECHO("minutesCount = %d\n",minutesCount);
		
		if(entries  )
        {
            MEM_free( entries );

            entries = NULL;
        }

        if( values  )
        {
            MEM_free( values );

            values = NULL;
        }

       
        if( results  )
        {
            MEM_free( results );
            results = NULL;
        }
        return minutesCount;
}



int getMinutesForDay(tag_t timesheetentry_tag,char* user_id,date_t date)
{
        const char query_name[ QRY_name_size_c + 1] = YFJC_SEARCHTIMESHEETENTRY;
        tag_t query_tag    = NULLTAG,
              *results     = NULL;
        char  **entries    = NULL,
              **values     = NULL,
              *dateStr      = NULL,
              *other_entries[3],
              *other_values[3];
        int   num_found    = 0,
              entry_count  = 0;
        date_t newdate = NULLDATE;
        GetBeforeEightHoursDate(date,8,&newdate);

        ITKCALL( QRY_find( query_name , &query_tag ) );

        if( query_tag == NULLTAG )
	    {
            TC_write_syslog( "没有找到查询构建器 %s\n",query_name);
	    }
        
        ITKCALL(QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

        ITKCALL(ITK_date_to_string( newdate, &dateStr ));
        ECHO("dateStr=%s,user_id=%s\n",dateStr,user_id);

        other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( entries[0] ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[0] , entries[0] );

        other_values[0] = ( char* )MEM_alloc( ( tc_strlen( dateStr ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_values[0] , dateStr );

        other_entries[1] = ( char* )MEM_alloc( ( tc_strlen( entries[1] ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[1] , entries[1] );

        other_values[1] = ( char* )MEM_alloc( ( tc_strlen( "Normal Hours" ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_values[1] , "Normal Hours" );

        other_entries[2] = ( char* )MEM_alloc( ( tc_strlen( entries[3] ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[2] , entries[3] );

        other_values[2] = ( char* )MEM_alloc( ( tc_strlen(user_id) + 1 ) * sizeof( char ) );

        tc_strcpy( other_values[2] , user_id );

	    ITKCALL( QRY_execute( query_tag , 3, other_entries , other_values , &num_found , &results ) );
        ECHO("num_found = %d\n",num_found);
        int minutesCount = 0;
        for( int i = 0 ;i < num_found ; i++ )
        {
            if( results[i]!= timesheetentry_tag){
                int min = 0;
                ITKCALL( AOM_ask_value_int( results[i] , TIMESHEET_MINUTES , &min ) );
                ECHO("min = %d\n",min);
                minutesCount = minutesCount+min;
            }
           
        }
        ECHO("minutesCount = %d\n",minutesCount);
        if(entries  )
        {
            MEM_free( entries );

            entries = NULL;
        }

        if( values  )
        {
            MEM_free( values );

            values = NULL;
        }

        if( other_entries[0]  )
        {
            MEM_free( other_entries[0] );

            other_entries[0] = NULL;
        }

        if( other_values[0]  )
        {
            MEM_free( other_values[0] );

            other_values[0] = NULL;
        }

        if( other_entries[1]  )
        {
            MEM_free( other_entries[1] );

            other_entries[1] = NULL;
        }

        if( other_values[1]  )
        {
            MEM_free( other_values[1] );

            other_values[1] = NULL;
        }

        if( other_entries[2]  )
        {
            MEM_free( other_entries[2] );

            other_entries[2] = NULL;
        }

        if( other_values[2]  )
        {
            MEM_free( other_values[2] );

            other_values[2] = NULL;
        }

        if( results  )
        {
            MEM_free( results );
            results = NULL;
        }
        return minutesCount;
}


  //判断年份是否为闰年
 int isLeap(int year)
 {
     if((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
         return 1;
     return 0;       
 }
//判断输入的日期是星期几，如果是周六和周日则返回1，周一到周五返回0。
 int getWeekendOrNot(int year,int month,int day){
    char date[7][7] = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
     //平年各个月的天数
     int year1[13] = {0,31,28,31,30,31,30,31,31,30,31,30,31};
     //闰年各个月的天数
     int year2[13] = {0,31,29,31,30,31,30,31,31,30,31,30,31};
     //days为距离公元第一天的天数
     int days = 0, k = 0, j = 0;
     char *getDate;
     //现在的日期与公元第一天开始相隔的天数
        
     for(k = 1;k < year;k++)
     {
         if(isLeap(k))
             days = days + 366;
         else
             days = days + 365;
     }
    
     for(j = 0; j < month; j++)
     {
         if(isLeap(year))
             days = days + year2[j];
         else
             days = days + year1[j];
     }
     days = days + day;
     //除以7求余就可以求出星期
     getDate = date[days % 7];
     printf("%s\n", getDate);
     if(tc_strcmp(getDate,"Sat")==0||tc_strcmp(getDate,"Sun")==0)
     {
        return 1;
     }
     return 0;
 }




//int JCI6TimesheetMinAlert( METHOD_message_t *msg, va_list args )
//{
//    int ifail      = ITK_ok;
//
//    tag_t timesheetentry_tag    = NULLTAG,
//          user_tag              = NULLTAG,
//          prop_tag              = va_arg( args, tag_t );
//    int   minutesProValue       = va_arg( args, int );
//    date_t date = NULLDATE;
//
//	ask_opt_debug();
//    ECHO("*****************JCI6TimesheetMinAlert start************************\n");
//
//    METHOD_PROP_MESSAGE_OBJECT(msg, timesheetentry_tag);
//    ITKCALL(AOM_ask_value_date(timesheetentry_tag, TIMESHEET_DATE, &date));
//    ITKCALL(AOM_ask_value_tag(timesheetentry_tag, TIMESHEET_USERTAG, &user_tag));
//    ECHO("user_tag=%u\n",user_tag);
//    if(user_tag!=NULLTAG)
//	{
//        char *user_id = NULL;
//        ITKCALL(AOM_ask_value_string(user_tag, USER_ID, &user_id));
//        int minutesCount = getMinutesForDay(timesheetentry_tag,user_id,date);
//        tag_t position = NULLTAG;
//        getUserPostion(user_id ,&position );
//        ECHO("position = %u\n",position);
//        if(position)
//        {
//        	char *postion_name   = NULL;
//            ITKCALL( AOM_ask_value_string( position , OBJECT_NAME , &postion_name ) );
//           
//            char *newRightStr = NULL;
//            getPreValue(postion_name,user_id,&newRightStr);
//            char part1[30] = "",
//                 part2[30] = "",
//                 part3[30] = "",
//                 part4[30] = "";
//            sscanf( newRightStr, "%[^{]{;}%[^{]{;}%[^{]{;}%[^{]", part1,part2,part3,part4);
//            
//            if(tc_strcmp(part1,part2)==0)
//            {
//				if( postion_name )
//	        	{
//		        	MEM_free( postion_name );
//		        	postion_name = NULL;
//	        	}
//	        	 if( user_id )
//				{	
//					MEM_free( user_id );
//					user_id = NULL;
//				}
//				ITKCALL( AOM_lock( timesheetentry_tag ) );
//				ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
//				ITKCALL( AOM_save( timesheetentry_tag ) );
//				ITKCALL( AOM_unlock( timesheetentry_tag ) );
//				ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
//	    		return ifail;
//        	
//            }
//        }
//        if( user_id )
//	    {
//		    MEM_free( user_id );
//		    user_id = NULL;
//	    }
//        ECHO("minutesCount = %d,minutesProValue = %d\n",minutesCount,minutesProValue);
////        if(minutesCount<480&&minutesCount+minutesProValue>480)
////2016-06-07	mengyawei modify
//        if(minutesCount<=480&&minutesCount+minutesProValue>480)
//        {
//        		int billType = getBillTypeByDate(date);
//        		if(billType==1)
//        		{
//        		 	HANDLE_ERROR( ERROR_OVER_EIGHT_HOURS );
//        		} 
//        }
//        ITKCALL( AOM_lock( timesheetentry_tag ) );
//        ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
//	    ITKCALL( AOM_save( timesheetentry_tag ) );
//	    ITKCALL( AOM_unlock( timesheetentry_tag ) );
//	    ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
//    }
//    ECHO("*****************JCI6TimesheetMinAlert end*****************\n");
//
//    return ifail;
//}

int JCI6preActionFillInfo( METHOD_message_t *msg, va_list args )
{
	int ifail      = ITK_ok;
	
    tag_t user_tag              = NULLTAG;
    //change by ck 2016-04-0
    tag_t scheduletask_tag		= NULLTAG;
    //change by ck 2016-04-07

    int   minutes = 0;
    date_t date = NULLDATE;

    va_list local_args;
    bool isNull;

	ask_opt_debug();
	//ECHO("**************************************************************************\n");
	ECHO("*                JCI6preActionFillInfo is comming                    *\n");
	//ECHO("**************************************************************************\n");

    va_copy( local_args, args);
	CreateInput *pCreInput = va_arg( local_args, CreateInput* );
	if( pCreInput )
	{
        pCreInput->getInt(TIMESHEET_MINUTES,minutes,isNull);
		pCreInput->getDate(TIMESHEET_DATE, date,isNull);
        pCreInput->getTag(TIMESHEET_USERTAG,user_tag,isNull);
		//change by ck 2016-04-08
 		pCreInput->getTag(TIMESHEET_SCHEDULETASK,scheduletask_tag,isNull);
		//change by ck 2016-04-08
	}
	va_end( local_args );

    //ECHO("minutes = %d,date==%d-%d-%d %d:%d,user_tag=%u  scheduletask_tag:%u\n",minutes,date.year,date.month,date.day,date.hour,date.minute,user_tag,scheduletask_tag);
    if(user_tag)
	{
        char *user_id = NULL;
		//change by ck 2016-04-08
		char *scheduletask_id = NULL;
        	ITKCALL(AOM_ask_value_string(user_tag, USER_ID, &user_id));
		if(scheduletask_tag != NULLTAG)
		{
			ITKCALL(AOM_ask_value_string(scheduletask_tag, ITEM_ID, &scheduletask_id));
		}		
		//change by ck 2016-04-08
        tag_t position = NULLTAG;
        int minutesCount = 0;
		int billType = 0;
		//change by ck 2016-04-08
        //minutesCount = getMinutesForDay(NULLTAG,user_id,date);
		minutesCount = getMinutesForDayAndSchedule(NULLTAG,user_id,date,scheduletask_id);
		//change by ck 2016-04-08
      //  ECHO("minutesCount = %d,minutes = %d\n",minutesCount,minutes);
        getUserPostion(user_id ,&position );
      //  ECHO("position = %u\n",position);
        if(position)
        {
            char *postion_name   = NULL;
            ITKCALL( AOM_ask_value_string( position , OBJECT_NAME , &postion_name ) );
           
            char *newRightStr = NULL;
            getPreValue(postion_name,user_id,&newRightStr);
            char part1[30] = "",
                 part2[30] = "",
                 part3[30] = "",
                 part4[30] = "";
            sscanf( newRightStr, "%[^{]{;}%[^{]{;}%[^{]{;}%[^{]", part1,part2,part3,part4);
           
            billType = getBillTypeByDate(date);
         //   ECHO("billType=%d\n",billType);
			char *rightPart = NULL;
            if(billType==1)
            {
                if(minutesCount+minutes>480)
                {
                     rightPart = part2;
                }
                else
                {
                     rightPart = part1;
                }
            }
            else if(billType==2)
            {
                rightPart = part3;
            }
            else if(billType==3)
            {
                rightPart = part4;
            }
         //   ECHO("rightPart=%s\n",rightPart);

            char *newStr = NULL;
            //change by zhanggl 2013-11-15
            if(tc_strstr(rightPart,",") )
            {
                newStr = (char*)MEM_alloc( (tc_strlen(rightPart) + 1)*sizeof(char));
                tc_strcpy(newStr,rightPart);

            }
            else
            {
                ITKCALL(STRNG_replace_str(rightPart,"，",",",&newStr));
            }
            //ITKCALL(STRNG_replace_str(rightPart,"，",",",&newStr)); remove
            //END
            char billTypeStr[30] = "",
                 rateName[3]     = "";
            sscanf(newStr, "%[^,],%s", billTypeStr,rateName );
          //  ECHO("billTypeStr=%s,rateName=%s\n",billTypeStr,rateName);
            pCreInput->setString(BILL_TYPE, billTypeStr,isNull);

			

            tag_t query_tag    = NULLTAG,
                  *results     = NULL;
            char  **entries    = NULL,
                  **values     = NULL,
                  *other_entries[2],
                  *other_values[2];
            int entry_count = 0,
                num_found   = 0;

            ITKCALL( QRY_find( "General..." , &query_tag ) );

            ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );
   
            other_entries[0] = ( char* )MEM_alloc( ( tc_strlen( entries[0] ) + 1 ) * sizeof( char ) );

            tc_strcpy( other_entries[0] , entries[0] );

            other_values[0] = ( char* )MEM_alloc( ( tc_strlen( rateName ) + 1 ) * sizeof( char ) );

            tc_strcpy( other_values[0] , rateName );

            other_entries[1] = ( char* )MEM_alloc( ( tc_strlen( entries[2] ) + 1 ) * sizeof( char ) );

            tc_strcpy( other_entries[1] , entries[2] );

            other_values[1] = ( char* )MEM_alloc( ( tc_strlen( "RateModifier" ) + 1 ) * sizeof( char ) );

            tc_strcpy( other_values[1] , "RateModifier" );

            ITKCALL( QRY_execute( query_tag , 2 , other_entries , other_values , &num_found , &results ) );
          //  ECHO("num_found=%d\n",num_found);
            if(num_found>0)
            {
               pCreInput->setTag(BILLRATE_TAG, results[0],isNull);
            }

            if(entries)
            {
                MEM_free( entries );

                entries = NULL;
            }

            if( values )
            {
                MEM_free( values );

                values = NULL;
            }
           
            if( other_entries[0])
            {
                MEM_free( other_entries[0] );

                other_entries[0] = NULL;
            }

            if( other_values[0]  )
            {
                MEM_free( other_values[0] );

                other_values[0] = NULL;
            }

            if( other_entries[1]  )
            {
                MEM_free( other_entries[1] );

                other_entries[1] = NULL;
            }

            if( other_values[1]  )
            {
                MEM_free( other_values[1] );

                other_values[1] = NULL;
            }
           
            if( postion_name )
	        {
		        MEM_free( postion_name );
		        postion_name = NULL;
	        }

            if(results )
            {
                MEM_free( results );
                results = NULL;
            }
			if(tc_strcmp(part1,part2)==0)
            {
				if( user_id )
	    		{
		   			MEM_free( user_id );
		    		user_id = NULL;
	    		}
	    		return ifail;
            }
        }
        if( user_id )
	    {
		    MEM_free( user_id );
		    user_id = NULL;
	    }
        if(minutesCount<480&&minutesCount+minutes>480)
        {
        	if(billType==1)
        	{
        		 HANDLE_ERROR( ERROR_OVER_EIGHT_HOURS );
        	}   
        }

	
    }

	//ECHO("*******************************************************\n");
	ECHO("*          JCI6preActionFillInfo is end           *\n");
	//ECHO("*******************************************************\n");

	return ifail;
}


#ifdef __cplusplus
}
#endif
