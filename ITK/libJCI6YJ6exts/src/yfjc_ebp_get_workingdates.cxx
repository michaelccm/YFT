/*
#=============================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#=============================================================================
# File name: yfjc_ebp_get_workingdates.cxx
# File description: 										   	
#=============================================================================
#	Date				Name		Action	Description of Change					   
#	2013-3-15	liqz  			Ini		function to get working dates								   
#=============================================================================
 */
#include "yfjc_ebp_head.h"

int getWorkingDates(void * returnValueType)
{
		string datatime;
		tag_t tccalendar = NULLTAG;
		int inonworkingdate = 0,
			 monthcount,
			 ifail = ITK_ok;

		char *fromDate = NULL,
				*toDate   = NULL,
				**monthvalue = NULL,
				*newfromdate = NULL,
				*newtodate = NULL,
				**nonWorkingDates = NULL;
		int* workingdate;


		
		
		ECHO("%s--getWorkingDates-----开始 \n" , getNowtime2().c_str() );

		ifail = USERARG_get_tag_argument(&tccalendar);
		ifail = USERARG_get_string_array_argument(&monthcount,&monthvalue);

		workingdate = (int *)MEM_alloc(sizeof(int) * (monthcount + 1));
	
		for(int i = 0; i < monthcount; i++ )
		{
			if(tc_strstr(monthvalue[i],"~"))
			{
				fromDate = tc_strtok(monthvalue[i],"~");
				toDate = tc_strtok(NULL,"~");
				inonworkingdate =  0;

				
				ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----开始 \n" , getNowtime2().c_str() );

				ITKCALL(TCCALENDAR_get_resource_non_working_dates(tccalendar,fromDate,toDate,&inonworkingdate,&nonWorkingDates));
  				
				
				ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----END \n" , getNowtime2().c_str() );
				
				if(nonWorkingDates!=NULL){
  					MEM_free(nonWorkingDates);
  					nonWorkingDates = NULL;
  				}
				
				STRNG_replace_str(fromDate,"-","",&newfromdate);
				STRNG_replace_str(toDate,"-","",&newtodate);
		
				int ifrom = atoi(newfromdate);
				int ito = atoi(newtodate);
				workingdate[i] = (ito - ifrom + 1)- inonworkingdate;
			}
		}

		*((int**) returnValueType) = workingdate;

		USERSERVICE_array_t  arrStruct;
		USERSERVICE_return_int_array(workingdate,monthcount,&arrStruct);


		
		ECHO("%s--getWorkingDates-----结束 \n" , getNowtime2().c_str() );
		
		if (arrStruct.length != 0){
			*((USERSERVICE_array_t*) returnValueType) = arrStruct;
		}

		return ITK_ok;
}