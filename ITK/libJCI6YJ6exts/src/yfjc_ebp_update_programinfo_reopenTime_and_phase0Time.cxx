/*
* tyl 2015/01/13
*设置项目重开时间&项目离开Phase0的时间
*/
#include "yfjc_ebp_head.h"
#ifdef __cplusplus
extern "C" {
#endif

int isContainsValue(int valuesCount,char** values,char* findValue)
{
   int i=0;
  for(int i = 0; i < valuesCount; i++)
    {
        if( tc_strcmp( values[i], findValue ) == 0 )
        {
           return 1;
        }
	}
	  return 0;
}

int JCI6_update_programinfo_reopenTime_and_phase0Time( METHOD_message_t *msg, va_list args )
{
    int ifail                = ITK_ok;
	int    option_value_count  = 0;
	int i;
    char **option_values    = NULL;
    tag_t   programinfo_rev_tag = NULLTAG;
	date_t current_date = NULLDATE;
    time_t now_time;
    tm *local_time;
    tag_t     prop_tag   = va_arg( args, tag_t );
    char*     prop_value = va_arg( args, char* );
	char* jci6_ProgramState=NULL;
	
	ask_opt_debug();
    METHOD_PROP_MESSAGE_OBJECT( msg, programinfo_rev_tag );
    ECHO("JCI6_update_programinfo_reopenTime_and_phase0Time::prop_value========%s\n",prop_value);
	AOM_ask_value_string(programinfo_rev_tag,"jci6_ProgramState",&jci6_ProgramState);
	ECHO("JCI6_update_programinfo_reopenTime_and_phase0Time::jci6_ProgramState========%s\n",jci6_ProgramState);
	
    time ( &now_time );
    local_time = localtime ( &now_time );
    current_date.year    = 1900 + local_time->tm_year;
    current_date.month   = local_time->tm_mon;
    current_date.day     = local_time->tm_mday;
    current_date.hour    = local_time->tm_hour;
    current_date.minute  = local_time->tm_min;
    current_date.second  = local_time->tm_sec;		
	
	//ITKCALL(AOM_lock(programinfo_rev_tag));
	if((tc_strcasecmp(jci6_ProgramState,"Phase 0")==0)&&(tc_strcasecmp(prop_value,"Phase 0")!=0))
	{
	 ITKCALL( AOM_set_value_date( programinfo_rev_tag, "jci6_Phase0Time", current_date ) );
	}
    
	ITKCALL( PREF_ask_char_values( "YFJC_programStates_ToUpdate_CloseDate", &option_value_count, &option_values ) );
	if(option_value_count>0)
	{
	    int isPreValueExist=isContainsValue(option_value_count,option_values,jci6_ProgramState);
		ECHO("JCI6_update_programinfo_reopenTime_and_phase0Time::isPreValueExist========%d\n",isPreValueExist);
		if(isPreValueExist==1)
		{
			int isCurrentValueExist=isContainsValue(option_value_count,option_values,prop_value);
			if(isCurrentValueExist==0)
			{
				ECHO("JCI6_update_programinfo_reopenTime_and_phase0Time::isCurrentValueExist========%d\n",isCurrentValueExist);
				ITKCALL( AOM_set_value_date( programinfo_rev_tag, "jci6_ReopenTime", current_date ) );
			}
		}	
	}

	//ITKCALL(AOM_save(programinfo_rev_tag));
	//ITKCALL(AOM_unlock(programinfo_rev_tag));
	SAFE_SM_FREE( jci6_ProgramState );
	SAFE_SM_FREE( option_values );
    ECHO("JCI6_update_programinfo_reopenTime_and_phase0Time  end\n");
    return ifail;
}

 #ifdef __cplusplus
}
#endif
