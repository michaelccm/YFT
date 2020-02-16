/**
* @file common_itk_util.cxx
* @brief itk warpper utility function 
* @author James
* @history
* ===================================================================================
* Date             Name                   Description of Change
* 12-Nov-2012			Ray				created                 
*/

#pragma warning (disable: 4996) 
#pragma warning (disable: 4819) 

#include "yfjc_ebp_head.h"

#ifdef WIN32
#include <io.h>
#include <direct.h>
#else
#include <unistd.h>
#endif


#define ARGS_LENGTH 200
#define ARGS_NAME_DEBUG "-debug"
#define DEBUG "-debug="
#define MAX_PRINTLINE_LENGTH 2000
#define MAX_PATH_LENGTH 2000
#define MAX_ARGUMENT_LENGTH 400
#define MAX_PARAMNAME_LENGTH 50
#define MAX_FILE_EXT_LENGTH 10
#define TRUE_FLAG 1
#define FALSE_FLAG 0
#define DETAILLOG 1


/*=============================================================================*
 * FUNCTION: current_time
 * PURPOSE : get the current datetime
 * INPUT: 
 *     date_t* date_tag  	// current date time tag
 *
 * RETURN: 
 *     void
 *============================================================================*/
void current_time( date_t * date_tag )
{
	time_t ltime;
	struct tm *today ;

	// Set time zone from TZ environment variable. If TZ is not set,
	// the operating system is queried to obtain the default value 
	// for the variable. 
	//
	//_tzset();
	
	// Get UNIX-style time and display as number and string.
	time( &ltime );
	
	today = localtime( &ltime );
	date_tag->year = today->tm_year + 1900 ;
	date_tag->month = today->tm_mon ;
	date_tag->day = today->tm_mday ;
	date_tag->hour = today->tm_hour ;
	date_tag->minute = today->tm_min ;
	date_tag->second = today->tm_sec ;
}

//




