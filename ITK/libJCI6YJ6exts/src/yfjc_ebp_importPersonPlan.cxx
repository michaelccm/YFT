/*
#===========================================================================================
#																			   
#			opyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#===========================================================================================
# File name: yfjc_ebp_importPersonPlan.cxx
# File description: 										   	
#===========================================================================================
#	Date			Name		Action	Description of Change					   
#	2013-3-16	zhangyn  		Ini		function for import person plan	
#	2013-4-08	zhangyn  		add		set rollback	
#	2013-4-08	zhangyn  		mod		costinfo_name
#===========================================================================================
 */
#include "yfjc_ebp_head.h"

int importPersonPlan(void * returnValueType)	
{
	int ifail = ITK_ok,
        length = 0,
        iMark = 0;

    tag_t  folderTag       = NULLTAG,
           costInfoTag     = NULLTAG,
           query_tag       = NULLTAG;

    char   **costPropertys = NULL,
           *error          = NULL,
           *excelError     = NULL;

    const char query_name[QRY_name_size_c + 1] = YFJC_SEARCHCOSTINFOBYIMPORTPERSONPLAN;

    logical bChanged = FALSE;    /* the changed state when rolling back */

	ifail = USERARG_get_tag_argument( &folderTag );

	ifail = USERARG_get_string_array_argument( &length , &costPropertys );

    ifail = USERARG_get_string_argument( &excelError );

    error = (char*)MEM_alloc((length*128+1) * sizeof(char));

    tc_strcpy( error , "");

    ITKCALL( QRY_find( query_name,&query_tag ) );

	if(query_tag == NULLTAG)
	{
        tc_strcat( error , YFJC_SEARCHCOSTINFOBYIMPORTPERSONPLAN);
        *((char**) returnValueType) = error;

		return ITK_ok;
	}

    ITKCALL( POM_place_markpoint( &iMark ) );

    ECHO("length58 = %d\n", length);

	for( int i = 0 ; i < length ; i++ )
    {

        costInfoTag     = NULLTAG;

        ifail = getQueryeCostInfoTag( query_tag , costPropertys[i] , error , &costInfoTag );

        if( ifail == ITK_ok )
        {
            if( costInfoTag == NULLTAG )
            {
                TC_write_syslog("createCostInfoTag71 \n");
               costInfoTag= createCostInfoTag( folderTag , costPropertys[i] );

				ITKCALL(AOM_lock(costInfoTag));
				//jci6_Unit
				 ITKCALL( AOM_set_value_string( costInfoTag , JCI6_UNIT , "ManMonth" ) );
				ITKCALL(AOM_save(costInfoTag));
				ITKCALL( AOM_unlock(costInfoTag));
            }
            else
            {
                TC_write_syslog("setCostInfoTagPropertys76 \n");
                setCostInfoTagPropertys( costInfoTag , costPropertys[i] );

				ITKCALL(AOM_lock(costInfoTag));
				 ITKCALL( AOM_set_value_string( costInfoTag , JCI6_UNIT , "ManMonth" ) );
				 ITKCALL(AOM_save(costInfoTag));
				ITKCALL( AOM_unlock(costInfoTag));
            }
        }
        
    }

    if( costPropertys != NULL)
    {
        MEM_free( costPropertys );
        costPropertys = NULL;
    }

    ECHO("excelError87 = %s\n", excelError);

    ECHO("error89 = %s\n", error);

    if( tc_strcmp( excelError , "" ) != 0 || tc_strcmp( error , "" ) != 0 )
    {
        ITKCALL( POM_roll_to_markpoint( iMark , &bChanged ));
    }

    *((char**) returnValueType) = error;

	return ITK_ok;
}


