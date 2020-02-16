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
#include <tc/preferences.h>
#include <tc/tc_macros.h>
#include <itk/bmf.h>
#include <stdlib.h>
#include <time.h>

#ifdef __cplusplus
extern "C" {
#endif


//To achieve a leap year of the judge, is to return true, otherwise it returns false
    int IsLeapYear(
                int year    /**< (I) */
                );

//The realization of the current date minus beforeday day
void GetBeforeDate( 
	 date_t   finish_date ,	/**< (I) */
	 int      beforeday ,	/**< (I) */
	 date_t * before_Date	/**< (O) */
	 );

//For the two time gap
void GapTime( 
	 date_t start_date,     /**< (I) */
	 date_t end_date,		/**< (I) */
	 int *  gapday			/**< (O)if start_date more than end_date return positive number,else return negative number*/
	 );

//The judgment of two time,if start_date more than end_date return true,else return false
int checktime( 
	 date_t startTime,      /**< (I) */
	 date_t endTime			/**< (I) */
	 );

//Returns the now time
void getNowTime(
	 date_t * nowDate		/**< (O) */
	 );

//Returns the last one days of the month
void getMonthLastDate(
	 date_t * monthLastDate /**< (O) */
	 );

//Returns the last one days of the next month
void getNextMonthhLastDate(
	 date_t * nextMonthhLastDate /**< (O) */
	 );

#ifdef __cplusplus
}
#endif