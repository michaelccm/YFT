/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@
*/

#include <mhfuns.hxx>


#ifdef __cplusplus
extern "C" {
#endif


/*
 * GUTIL_time_to_date
 */
void GUTIL_time_to_date( time_t time, date_t *date )
{
   struct tm *oldDate = localtime( &time );
   date->year   = (short)(1900 + oldDate->tm_year);
   date->month  =(byte) (oldDate->tm_mon);
   date->day    =(byte) (oldDate->tm_mday);
   date->hour   = 0;
   date->minute = 0;
   date->second = 0;
}


/*
 * GUTIL_date_to_time
 */
time_t GUTIL_date_to_time( const date_t &date )
{
   struct tm t;
   time_t createTime;
   time( &createTime );
   struct tm *oldDate = localtime( &createTime );

   memset( &t, 0, sizeof(tm) );
   t.tm_isdst = oldDate->tm_isdst;
   t.tm_year  = date.year - 1900;
   t.tm_mon   = date.month;
   t.tm_mday  = date.day;
   t.tm_hour  = date.hour;
   t.tm_min   = date.minute;
   t.tm_sec   = date.second;
   return mktime(&t);
}


/*
 * GUTIL_is_null_date
 */
logical GUTIL_is_null_date( const date_t &date )
{
   return ( date.year == 0 && date.month == 0 && date.day == 0 &&
            date.hour == 0 && date.minute == 0 && date.second == 0 );
}


/*
 * GUTIL_current
 */
void GUTIL_current_date(date_t *date)
{
   time_t time_t;
   time(&time_t);
   GUTIL_time_to_date( time_t, date );
}


/*
 * GSTR_Clone.
 */
char* GSTR_clone( char **dst, const char *src )
{
   char *retVal = NULL;
   int  srcLen  = 0;

   *dst = NULL;
   if (src == NULL) 
      return NULL;

   srcLen = (int)tc_strlen( src ) + 1;
   *dst   = (char*)MEM_alloc( srcLen * sizeof(char) );
   retVal = tc_strncpy( *dst, src, srcLen );
   (*dst)[srcLen - 1] = '\0';

   return retVal;
}


/*
 * GCOMM_qry
 */
int GCOMM_qry( const char *qry_name, int *num_of_obj, tag_t **objs, int num_of_param, ... )
{
   int ifail = ITK_ok;

   tag_t queryTag = NULLTAG;
   int   entryCount  = 0;
   char  **entries   = NULL;
   char  **values    = NULL;
   int   i = 0;
   va_list argptr;

   *num_of_obj = 0;
   *objs = NULL;


   //TC_write_syslog( "ylong:GCOMM_qry - qry_name: %s\n", qry_name);
   ITKCALL( ifail = QRY_find( qry_name, &queryTag ) );

   if (queryTag == NULLTAG)
   {
      ifail = QRY_invalid_query;
      EMH_store_error_s1( EMH_severity_error, ifail, qry_name );
      goto FUNC_EXIT;
   }

   ITKCALL( ifail = QRY_find_user_entries( queryTag, &entryCount, &entries, &values ) );

   if (entryCount < num_of_param)
   {
      ifail = QRY_invalid_list_of_user_entries;
      EMH_store_error_s1( EMH_severity_error, ifail, qry_name );
      goto FUNC_EXIT;
   }

   va_start( argptr, num_of_param );
   for( i = 0; i < num_of_param; i ++ )
   {
      GSTR_clone( &values[i], va_arg( argptr, char* ) );
   }
   va_end( argptr );


   ITKCALL( ifail = QRY_execute( queryTag, entryCount, entries, values, num_of_obj, objs ) );
   if (ifail != ITK_ok)
   {
      goto FUNC_EXIT;
   }


FUNC_EXIT:
   for ( i = 0; i < num_of_param && values != NULL; i ++ )
   {
      MEM_free( values[i] );
   }
   MEM_free( entries );
   MEM_free( values );
   return ifail;
}


#ifdef __cplusplus
}
#endif

