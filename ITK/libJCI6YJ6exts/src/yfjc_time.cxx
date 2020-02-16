/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: yfjc_time.h
# File description: 										   	
#=============================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-04	zhangyn  		Ini		Time comparison function									   
#=============================================================================
 */
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

//To achieve a leap year of the judge, is to return true, otherwise it returns false
int IsLeapYear(int year)
{
	bool bleap = true;
	if(( year%4 == 0 && year%100 != 0) || year%400 == 0)
		bleap = true;
	else
		bleap = false;
	return true;
}

//The realization of the current date minus beforeday  day
void GetBeforeDate( date_t finish_date ,int beforeday , date_t *before_Date )
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

	year = finish_date.year;
	month = finish_date.month;
	day = finish_date.day;
	hour = finish_date.hour;
	minute = finish_date.minute;
	second = finish_date.second;

    time ( &rawtime );
    target_time = localtime ( &rawtime ); // 其它参数

    target_time->tm_year = year - 1900; //年
    target_time->tm_mon= month ;     // 月
    target_time->tm_mday = day ;   // 日
    target_time->tm_hour = hour ;   // 时
    target_time->tm_min = minute ; // 分
    target_time->tm_sec = second ;// 秒

    t1 = mktime (target_time);

    t1 = t1-beforeday*24*60*60;
    p = localtime(&t1);	

	before_Date->year    = 1900+p->tm_year;
	before_Date->month   = p->tm_mon;
	before_Date->day     = p->tm_mday;
	before_Date->hour    = p->tm_hour;
	before_Date->minute  = p->tm_min;
	before_Date->second  = p->tm_sec;
}


//For the two time gap, by xiusong
//void GapTime(date_t start_date, date_t end_date, int * gapday) {
//	struct tm start;
//	struct tm end;
//	//date_t to tm for start_date
//	start.tm_year = start_date.year - 1900;
//	start.tm_mon = start_date.month;
//	start.tm_mday = start_date.day;
//	start.tm_hour = 0;
//	start.tm_min = 0;
//	start.tm_sec = 0;
//	//date_t to tm for end_date
//	end.tm_year = end_date.year - 1900;
//	end.tm_mon = end_date.month;
//	end.tm_mday = end_date.day;
//	end.tm_hour = 0;
//	end.tm_min = 0;
//	end.tm_sec = 0;
//	//tm to time_t
//	time_t s = mktime(&start);
//	time_t e = mktime(&end);
//
//	//Calculation differ time 
//	double sec_s = e > s ? e - s : s - e;
//	long days = ((long)sec_s) / 86400;
//
//	printf("GapTime start_date:%4d-%02d-%02d time_t %f end_date::%4d-%02d-%02d time_t %f, differ day:%d\n",
//		start_date.year, start_date.month + 1, start_date.day, s,
//		end_date.year, end_date.month + 1, end_date.day, e, days);
//
//	*gapday = days;
//	printf("GapTime days:%d\n", *gapday);
//}

void GapTime(date_t start_date, date_t end_date, int * gapday)
{
	
	if ( (start_date.year == end_date.year) && (start_date.month == end_date.month)) {
		int days = start_date.day - end_date.day;
		*gapday = days;
		tr_ECHO("GapTime form %4d-%02d-%02d  to %4d-%02d-%02d, gap days:%d", end_date.year, end_date.month, end_date.day,
			start_date.year, start_date.month, start_date.day, *gapday);
		return;
		//return start_date.d
	}
	int day_table[2][12] =
	{ {31,29,31,30,31,30,31,31,30,31,30,31},
	{31,28,31,30,31,30,31,31,30,31,30,31} };
	int y = 0,
		m = 0;
	long startday = 0,
		enddays = 0;
	int base_year = start_date.year > end_date.year ? end_date.year : start_date.year;
	
	for (y = base_year; y <= start_date.year; y++)
		if (IsLeapYear(y))
			startday += 366;
		else
			startday += 365;
	bool currentIsLeapYear1 = IsLeapYear(start_date.year);
	for (m = 0; m < start_date.month; m++)
		if (currentIsLeapYear1)
			startday += day_table[0][m];
		else
			startday += day_table[1][m];
	startday += start_date.day;

	for (y = base_year; y <= end_date.year; y++)
		if (IsLeapYear(y))
			enddays += 366;
		else
			enddays += 365;
	bool currentIsLeapYear2 = IsLeapYear(end_date.year);
	for (m = 0; m < end_date.month; m++)
		if (currentIsLeapYear2)
			enddays += day_table[0][m];
		else
			enddays += day_table[1][m];

	enddays += end_date.day;

	*gapday = startday - enddays;
	tr_ECHO("GapTime form %4d-%02d-%02d  to %4d-%02d-%02d, gap days:%d", end_date.year, end_date.month, end_date.day,
		start_date.year, start_date.month, start_date.day, *gapday);
}
//For the two time gap
//void GapTime( date_t start_date, date_t end_date, int * gapday)
//{
//	int day_table[2][12]=
//	{{31,29,31,30,31,30,31,31,30,31,30,31},
//	{31,28,31,30,31,30,31,31,30,31,30,31}};
//	int y = 0,
//        m = 0;
//	long startday = 0,
//         enddays  = 0;
//
//	for( y = 1; y <= start_date.year ; y++ )
//		if( IsLeapYear(y) )
//			startday += 366;
//		else
//			startday += 365;
//	for( m = 0; m < start_date.month ; m++ )
//		if( IsLeapYear(start_date.year) )
//			startday += day_table[0][m];
//		else
//			startday += day_table[1][m];
//
//	startday += start_date.day;
//
//	for( y = 1; y <= end_date.year ; y++ )
//		if( IsLeapYear(y) )
//			enddays += 366;
//		else
//			enddays += 365;
//	for( m = 0; m < end_date.month ; m++ )
//		if( IsLeapYear(end_date.year) )
//			enddays += day_table[0][m];
//		else
//			enddays += day_table[1][m];
//
//	enddays += end_date.day;
//
//	*gapday = startday - enddays;
//}

//The judgment of two time,if start_date more than end_date return true,else return false
int checktime( date_t startTime,date_t endTime)
{
	char str_start_date[32] = "",
	     str_end_date[32]   = "";
	int ret = 0;

	if( startTime.year != 0)
	{
		sprintf( str_start_date, "%4d/%02d/%02d %02d:%02d:%02d", startTime.year, startTime.month+1, startTime.day, startTime.hour, startTime.minute, startTime.second);
	}
	if( endTime.year != 0)
	{
		sprintf( str_end_date, "%4d/%02d/%02d %02d:%02d:%02d", endTime.year, endTime.month+1, endTime.day, endTime.hour, endTime.minute, endTime.second);
	}

	ret = tc_strcmp( str_start_date , str_end_date );
	if( ret > 0)
	{
		return TRUE;
	}else
	{
		return FALSE;
	}

}

//Returns the now time
void getNowTime(date_t * nowDate)
{
	struct tm *p;
	time_t now;

	time(&now);
	p = localtime(&now);
	nowDate->year = 1900+p->tm_year;
	nowDate->month = p->tm_mon;
	nowDate->day = p->tm_mday ;
	nowDate->hour = p->tm_hour;
	nowDate->minute = p->tm_min;
	nowDate->second = p->tm_sec;

}

//Returns the last one days of the month
void getMonthLastDate(date_t * monthLastDate)
{
	int day_table[2][12]=
		{{31,29,31,30,31,30,31,31,30,31,30,31},
		{31,28,31,30,31,30,31,31,30,31,30,31}};
	int endday = 0;
	struct tm *p;
	time_t now;

	time(&now);
	p = localtime(&now);

	if( IsLeapYear(1900+p->tm_year) )
	{
		endday = day_table[0][p->tm_mon];
	}else
	{
		endday = day_table[1][p->tm_mon];
	}

	monthLastDate->year = 1900+p->tm_year;
	monthLastDate->month = p->tm_mon;
	monthLastDate->day = endday;
	monthLastDate->hour = p->tm_hour;
	monthLastDate->minute = p->tm_min;
	monthLastDate->second = p->tm_sec;

}

//Returns the last one days of the next month
void getNextMonthhLastDate(date_t * nextMonthhLastDate)
{
	int day_table[2][12]=
		{{31,29,31,30,31,30,31,31,30,31,30,31},
		{31,28,31,30,31,30,31,31,30,31,30,31}};
	int endday = 1;
	struct tm *p;
	time_t now;

	time(&now);
	p = localtime(&now);
	if( IsLeapYear( 1900 + p->tm_year) )
	{
		endday = day_table[0][p->tm_mon];
	}else
	{
		endday = day_table[1][p->tm_mon];
	}
	if( p->tm_mon + 1 == 12)
	{
		nextMonthhLastDate->year = 1900 + p->tm_year + 1;
		nextMonthhLastDate->month = 1;
		nextMonthhLastDate->day = endday;
		nextMonthhLastDate->hour = p->tm_hour;
		nextMonthhLastDate->minute = p->tm_min;
		nextMonthhLastDate->second = p->tm_sec;
	}else
	{
		nextMonthhLastDate->year = 1900 + p->tm_year;
		nextMonthhLastDate->month = p->tm_mon + 1;
		nextMonthhLastDate->day = endday;
		nextMonthhLastDate->hour = p->tm_hour;
		nextMonthhLastDate->minute = p->tm_min;
		nextMonthhLastDate->second = p->tm_sec;
	}

}

#ifdef __cplusplus
}
#endif