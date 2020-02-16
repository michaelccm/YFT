#include "yfjc_ebp_head.h"
extern logical YFJC_OPT_DEBUG;

//logical YFJC_OPT_DEBUG = TRUE;
logical YFJC_OPT_DEBUG = FALSE;

FILE* fileLog=NULL;
FILE* logFile = NULL;

logical isDebug=FALSE;

void ask_opt_debug()
{
  // int value_count1=0;
	//char **pref_values=NULL;
	//ITKCALL( PREF_set_search_scope( TC_preference_site ));
	//ITKCALL( PREF_ask_char_values( "YFJC_ISDEBUG", &value_count1, &pref_values ) );
    //if( value_count1>0 )
   // {
		//if(tc_strcmp(pref_values[0],"1")==0){
			//YFJC_OPT_DEBUG = FALSE;
			//isDebug=FALSE;
		//}
    //}

	//SAFE_SM_FREE(pref_values);

	 logical preValue = FALSE;
    ITKCALL( PREF_ask_logical_value( "YFJC_ISDEBUG", 0, &preValue ) );
    if( preValue )
    {
		YFJC_OPT_DEBUG=TRUE;
        isDebug = TRUE;
    }

}


void ECHO(char *format, ...)
{
	//if (!YFJC_OPT_DEBUG)
	//	return;

	char msg[1024] = { '\0' };
	va_list args;

	va_start(args, format);
	vsprintf(msg, format, args);
	va_end(args);

	string time_str = st_get_time_stamp("code log %m-%d %H:%M:%S INFO ");
	printf("%s%s\n", time_str.c_str(), msg);
	TC_write_syslog("%s%s\n", time_str.c_str(), msg);
}

logical get_opt_debug(){
    return YFJC_OPT_DEBUG;
}

//add by wuw
void getNonBudgetCostinfo( char* costInfoName,int *retInt )
{
   tag_t          query_tag=NULLTAG;       /**< (I) Tag of the saved query */
    int        num_found=0;       /**< (O) Number of found objects */
    tag_t*        results=NULL;         /**< (OF) num_found List of found object tags */
	int  num_clauses;
	char **  attr_names1=NULL; 
	char **  entry_names1=NULL;  
	char **  logical_ops=NULL;  
	char **  math_ops=NULL;  
	char **  values1=NULL;  
	tag_t *  lov_tags=NULL;  
	int * attr_types=NULL;

	int nx=0;

	char **entries2     = NULL,
           **values2      = NULL;
	string vStr;
	
	//ECHO("1.YFJC Query for Non-budget CostInfo\n");
	

	char *entries[2]; //={"CostPhaseType","Object Name"};
	char *values[2]; // ={"Actual",""};

	vStr.append(costInfoName);
	
	ITKCALL(QRY_find("YFJC Query for Non-budget CostInfo", &query_tag));

	

	if(query_tag==NULLTAG)
	{
		ECHO("not find query:YFJC Query for Non-budget CostInfo\n");
		*retInt=0;
		return ;
	}

		
		//ITKCALL(QRY_describe_query(query_tag,&num_clauses,&attr_names1,&entry_names1,&logical_ops,&math_ops,&values1,&lov_tags,&attr_types));
		//printf("num_clauses:%d \n",num_clauses);
		ITKCALL(QRY_find_user_entries(query_tag,&num_clauses,&entries2,&values2));
		
		for(int n=0;n<num_clauses;n++)
		{
			//ECHO("  entries2:%s \n",entries2[n]);
			if (strcmp(entries2[n], "CostPhaseType") == 0){
				entries[nx]=(char*)MEM_alloc(sizeof(char*)*512);

				//strcpy(entries[0],aa.c_str());
				
				//printf("查询条件1 entries2[0]----->%s\n",entries2[nx]);
				strcpy(entries[nx],entries2[nx]);
				nx++;
			}
			else if(strcmp(entries2[n], "Object Name") == 0){
				
				entries[nx]=(char*)MEM_alloc(sizeof(char*)*512);
				//printf("查询条件2 entries2[0]----->%s\n",entries2[nx]);
				strcpy(entries[nx],entries2[nx]);
				nx++;
			}
		}
		SAFE_SM_FREE(attr_names1);
		SAFE_SM_FREE(entry_names1);
		SAFE_SM_FREE(logical_ops);
		SAFE_SM_FREE(math_ops);
		SAFE_SM_FREE(values1);
		SAFE_SM_FREE(lov_tags);
		SAFE_SM_FREE(attr_types);

		//
		SAFE_SM_FREE(entries2);
		SAFE_SM_FREE(values2);

		
		//strcpy(entries[0],"Year");
		//strcpy(entries[0],"CostPhaseType");
		//strcpy(entries[1],"Object Name");

		values[0]=(char*)MEM_alloc(sizeof(char*)*512);
		values[1]=(char*)MEM_alloc(sizeof(char*)*512);
		
		
		strcpy(values[0], "Actual");
		strcpy(values[1],vStr.c_str());
		
		

		
		ITKCALL(QRY_execute(query_tag, nx, entries, values, &num_found, &results));

		//1VZtWe9gopEFZA
		
	
	SAFE_SM_FREE(entries[0]);
	SAFE_SM_FREE(entries[1]);
	SAFE_SM_FREE(values[0]);
	SAFE_SM_FREE(values[1]);

	*retInt=num_found;
	SAFE_SM_FREE(results);
	


	//ECHO("getNonBudgetCostinfo:: retInt-->%d \n",*retInt);
  
}

int getDatasetFile(char *dsName,char ** dsFilePath)
{
	tag_t spec_dataset_rev = NULLTAG,
			dataset= NULLTAG,
			ref_object = NULLTAG;
	AE_reference_type_t reference_type;	

	ITKCALL(AE_find_dataset(dsName,&dataset));
	ITKCALL(AE_ask_dataset_latest_rev(dataset, &spec_dataset_rev));
	
	char ref_name[WSO_name_size_c + 1] = "Text";
	ITKCALL(AE_ask_dataset_named_ref(spec_dataset_rev, ref_name, &reference_type, &ref_object));
	if(reference_type == AE_PART_OF)
	{
		char new_ds_name[WSO_name_size_c + 1] = "";
		char *new_file_name = USER_new_file_name(new_ds_name, ref_name, "txt", 0);
		char *temp_dir = getenv("TC_LOG");
		char temp_file[SS_MAXPATHLEN] = "";
		strcpy(temp_file, temp_dir);
		strcat(temp_file, "/");
		strcat(temp_file, new_file_name);
		ITKCALL(IMF_export_file(ref_object, temp_file));
		*dsFilePath = (char*)MEM_alloc((strlen(temp_file)+10)*sizeof(char));
		strcpy(*dsFilePath,temp_file);

		#ifdef linux
			char * execLine=(char*)MEM_alloc((strlen(temp_file)+20)*2*sizeof(char));
			strcpy(execLine,"iconv -f gbk -t utf8 ");
			strcat(execLine,temp_file);
			strcat(execLine," > ");
			strcat(execLine,temp_file);
			strcat(execLine,"utf8.txt");
			ECHO("execLine==%s \n",execLine);
			system(execLine);
			MEM_free(execLine);
			strcat(*dsFilePath,"utf8.txt");
 		#endif
			
	}
	ITKCALL(AOM_delete(dataset));
	return 0;
}  


int do_revise_XSOInfo( tag_t task)
{
	int customError	  = ITK_ok, 
        target_count  = 0,
        xso_cnt       = 0;

    char object_type[WSO_name_size_c+1]  = "";

	tag_t pRoot                 = NULLTAG, 
          *target_objects       = NULL,
          xso_revision_tag      = NULLTAG,
          new_xso_revision_tag  = NULLTAG;

	/* -- Attachmetns -- */

	customError =  EPM_ask_root_task( task, &pRoot );
	TEST_ERROR( customError )

	if( pRoot != NULLTAG)
    {
        ITKCALL( EPM_ask_attachments( pRoot, EPM_target_attachment,
                                     &target_count, &target_objects
                                    ) );

		for( int iCnt = 0; iCnt < target_count; iCnt++ )
        {
            ITKCALL( WSOM_ask_object_type( target_objects[iCnt], object_type ) );
            if( tc_strcmp( object_type, JCI6_XSOREVISION_TYPE ) == 0)
            {
                xso_cnt++;
                xso_revision_tag = target_objects[iCnt];
            }
        }

        MEM_free( target_objects );

		if( target_count == 0 || xso_cnt > 1)
        {
			HANDLE_ERROR( ERROR_XSOINFO_NULL_OR_MORETHANONE )
        }
        POM_AM__set_application_bypass( TRUE ) ;
        ITKCALL( ITEM_copy_rev(xso_revision_tag, NULL, &new_xso_revision_tag ) );
        // POM_AM__set_application_bypass( FALSE ) ;
        int count = 0;
        tag_t *deep_copy_objects = NULL;
       //  POM_AM__set_application_bypass( TRUE ) ;
        ITKCALL( ITEM_perform_deepcopy( new_xso_revision_tag, ITEM_revise_operation, 
            xso_revision_tag, &count, &deep_copy_objects ) );
       POM_AM__set_application_bypass( FALSE );
        MEM_free( deep_copy_objects );

        ITKCALL( AOM_lock( new_xso_revision_tag ) );
        ITKCALL( customError = AOM_set_value_date( new_xso_revision_tag, JCI6_REVIEWDATE, NULLDATE ) );
	    if( customError )
        {
            ITKCALL( AOM_unlock( new_xso_revision_tag ) );
            HANDLE_ERROR( ERROR_CLEAR_XSOREV_REVIEWDATE )
        }
        tag_t owning_user = NULLTAG,
              owning_group = NULLTAG;
        ITKCALL( AOM_ask_owner( xso_revision_tag, &owning_user ));
	    ITKCALL( AOM_ask_group( xso_revision_tag, &owning_group ));
        ITKCALL( AOM_set_ownership( new_xso_revision_tag,owning_user,owning_group ) );
        ITKCALL( AOM_save( new_xso_revision_tag ) );
        ITKCALL( AOM_unlock( new_xso_revision_tag ) );

        POM_AM__set_application_bypass( TRUE ) ;
        int target_types = EPM_target_attachment;
        ITKCALL( customError = EPM_add_attachments( pRoot, 1, &new_xso_revision_tag, &target_types ) ); 
        if( customError )
        {
            HANDLE_ERROR( ERROR_ADD_NEWXSOREV_TO_TARGET )
        }
      // POM_AM__set_application_bypass( FALSE );

      // POM_AM__set_application_bypass( TRUE );
        ITKCALL( customError = EPM_remove_attachments( pRoot, 1, &xso_revision_tag ) ); 
        if( customError )
        {
            HANDLE_ERROR( ERROR_REMOVE_OLDXSOREV_FORM_TAEGET )
        }
       // POM_AM__set_application_bypass( FALSE ) ;

      // POM_AM__set_application_bypass( TRUE ) ;
        target_types = EPM_reference_attachment;
        ITKCALL( customError = EPM_add_attachments( pRoot, 1, &xso_revision_tag, &target_types ) );
        if( customError )
        {
            HANDLE_ERROR( ERROR_ADD_OLDXSOREV_TO_REFERENCE )
        }
        POM_AM__set_application_bypass( FALSE ) ;
    }

	return customError;
}

/*************************************************************************************
 * CCL_switch_objects()
 *
 * Description:
 *    This handler will switch objects between workflow target and reference.
 *
 *    The parameter syntax is as follows:
 *
 *         CUST_switch_objects
 *                             -from=target|reference
 *                             -to=reference|target
 *                             [-status=<status type>]
 *                             [-type=<target type>]
 *                             [-debug=true|false]
 *
 *
 *************************************************************************************/

int splitStr(char* sourceStr, char splitChar, char*** value)
{
	int i, n;
	int num = 1;
	int value_size = 500;

	for (i = 0; i < strlen(sourceStr); i++)
	{
		if (sourceStr[i] == splitChar)
		{
			num++;
		}
	}

	*value = (char **) malloc(sizeof(char*) * num);
	for (i = 0; i < num; i++)
	{
		(*value)[i] = (char*) malloc(sizeof(char) * (value_size + 1));
		memset((*value)[i], 0, sizeof(char) * (value_size + 1));
	}

	i = 0;
	n = 0;

	while (*sourceStr != 0)
	{
		if ((*sourceStr == splitChar) || (*sourceStr == '\0'))
		{
			n++;
			i = 0;
		}
		else
		{
			(*value)[n][i++] = *sourceStr;
		}
		sourceStr++;
	}

	return num;
}

tag_t createCostInfoTag( tag_t  folderTag , char * costPropertys)
{
    char costInfoName[64]="",
         *propertys        = NULL;

    char  *jci6_CPT        = NULL,
          *jci6_RateLevel  = NULL,
          *jci6_Division   = NULL,
          *jci6_Year       = NULL,
          *jci6_Jan        = NULL,
          *jci6_Feb        = NULL,
          *jci6_Mar        = NULL,
          *jci6_Apr        = NULL,
          *jci6_May        = NULL,
          *jci6_Jun        = NULL,
          *jci6_Jul        = NULL,
          *jci6_Aug        = NULL,
          *jci6_Sep        = NULL,
          *jci6_Oct        = NULL,
          *jci6_Nov        = NULL,
          *jci6_Dec        = NULL;

    tag_t rateLeve_tag = NULLTAG,
          division_tag = NULLTAG,
          cost_tag     = NULLTAG,
          relationTag  = NULLTAG,
          relation     = NULLTAG;

    propertys = (char*)MEM_alloc( ( tc_strlen( costPropertys ) + 1) * sizeof(char));
    tc_strcpy( propertys , costPropertys);

    jci6_CPT        = tc_strtok(propertys,"|");
	jci6_RateLevel  = tc_strtok(NULL,"|");
	jci6_Division   = tc_strtok(NULL,"|");

	jci6_Year       = tc_strtok(NULL,"|");
	jci6_Jan        = tc_strtok(NULL,"|");
	jci6_Feb        = tc_strtok(NULL,"|");
	jci6_Mar        = tc_strtok(NULL,"|");
	jci6_Apr        = tc_strtok(NULL,"|");
	jci6_May        = tc_strtok(NULL,"|");
	jci6_Jun        = tc_strtok(NULL,"|");
	jci6_Jul        = tc_strtok(NULL,"|");
	jci6_Aug        = tc_strtok(NULL,"|");
	jci6_Sep        = tc_strtok(NULL,"|");
	jci6_Oct        = tc_strtok(NULL,"|");
	jci6_Nov        = tc_strtok(NULL,"|");
	jci6_Dec        = tc_strtok(NULL,"|");

    sprintf( costInfoName, "%s%s%s%s%s%s%s",jci6_CPT,"_",jci6_Year,"_",jci6_Division ,"_",jci6_RateLevel);

    ECHO("costInfoName146 = %s\n", costInfoName);

    createCostInfo(costInfoName,&cost_tag);

    ITKCALL( AOM_lock ( cost_tag ));

    if( jci6_CPT != NULL && tc_strcmp( jci6_CPT, "") != 0 )
    {
        ITKCALL( AOM_set_value_string( cost_tag , JCI6_CPT , jci6_CPT ) );
    }
    if( jci6_RateLevel != NULL && tc_strcmp( jci6_RateLevel, "") != 0 )
    {
        SA_find_discipline( jci6_RateLevel , &rateLeve_tag );
        if(rateLeve_tag != NULLTAG)
        {
            ITKCALL( AOM_set_value_tag( cost_tag , JCI6_RATELEVEL , rateLeve_tag ) );
        }
    }
    if( jci6_Division != NULL && tc_strcmp( jci6_Division, "") != 0 )
    {
        SA_find_group( jci6_Division , &division_tag );
        ITKCALL( AOM_set_value_tag( cost_tag , JCI6_DIVISION , division_tag ) );
    }

    if( jci6_Year != NULL && tc_strcmp( jci6_Year, "") != 0 )
    {
        ITKCALL( AOM_set_value_int( cost_tag , JCI6_YEAR , atoi( jci6_Year ) ) );
    }
    if( jci6_Jan != NULL && tc_strcmp( jci6_Jan, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JAN , strtod( jci6_Jan , NULL ) ) );
    }
    if( jci6_Feb != NULL && tc_strcmp( jci6_Feb, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_FEB , strtod( jci6_Feb , NULL ) ) );
    }
    if( jci6_Mar != NULL && tc_strcmp( jci6_Mar, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_MAR , strtod( jci6_Mar , NULL ) ) );
    }
    if( jci6_Apr != NULL && tc_strcmp( jci6_Apr, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_APR , strtod( jci6_Apr , NULL ) ) );
    }
    if( jci6_May != NULL && tc_strcmp( jci6_May, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_MAY , strtod( jci6_May , NULL ) ) );
    }
    if( jci6_Jun != NULL && tc_strcmp( jci6_Jun, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JUN , strtod( jci6_Jun , NULL ) ) );
    }
    if( jci6_Jul != NULL && tc_strcmp( jci6_Jul, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JUL , strtod( jci6_Jul , NULL ) ) );
    }
    if( jci6_Aug != NULL && tc_strcmp( jci6_Aug, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_AUG , strtod( jci6_Aug , NULL ) ) );
    }
    if( jci6_Sep != NULL && tc_strcmp( jci6_Sep, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_SEP , strtod( jci6_Sep , NULL ) ) );
    }
    if( jci6_Oct != NULL && tc_strcmp( jci6_Oct, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_OCT , strtod( jci6_Oct , NULL ) ) );
    }
    if( jci6_Nov != NULL && tc_strcmp( jci6_Nov, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_NOV , strtod( jci6_Nov , NULL ) ) );
    }
    if( jci6_Dec != NULL && tc_strcmp( jci6_Dec, "") != 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_DEC , strtod( jci6_Dec , NULL ) ) );
    }


    ITKCALL( AOM_save( cost_tag ) );

	ITKCALL( AOM_unlock( cost_tag ) );

    ITKCALL( AOM_lock ( folderTag ));

    FL_insert( folderTag,cost_tag , 1 );

    ITKCALL( AOM_save( folderTag ) );

    ITKCALL( AOM_unlock( folderTag ) );

    ITKCALL( AOM_refresh( folderTag,true)); 

    if( propertys != NULL )
    {
        MEM_free( propertys );
        propertys = NULL;
    }

	return cost_tag;
}

void setCostInfoTagPropertys( tag_t cost_tag , char * costPropertys )
{
    int year    = 0,
        month   = 0;

    char  *jci6_CPT        = NULL,
          *jci6_RateLevel  = NULL,
          *jci6_Division   = NULL,
          *jci6_Year       = NULL,
          *jci6_Jan        = NULL,
          *jci6_Feb        = NULL,
          *jci6_Mar        = NULL,
          *jci6_Apr        = NULL,
          *jci6_May        = NULL,
          *jci6_Jun        = NULL,
          *jci6_Jul        = NULL,
          *jci6_Aug        = NULL,
          *jci6_Sep        = NULL,
          *jci6_Oct        = NULL,
          *jci6_Nov        = NULL,
          *jci6_Dec        = NULL,
          *propertys       = NULL;

    time_t T ;
    struct tm *timenow;

    time ( &T );
	timenow = localtime ( &T );


    year  = 1900+timenow->tm_year;
    month = timenow->tm_mon+1;

    propertys = (char*)MEM_alloc( ( tc_strlen( costPropertys ) + 1) * sizeof(char));
    tc_strcpy( propertys , costPropertys);

    jci6_CPT        = tc_strtok(propertys,"|");
	jci6_RateLevel  = tc_strtok(NULL,"|");
	jci6_Division   = tc_strtok(NULL,"|");

	jci6_Year       = tc_strtok(NULL,"|");
	jci6_Jan        = tc_strtok(NULL,"|");
	jci6_Feb        = tc_strtok(NULL,"|");
	jci6_Mar        = tc_strtok(NULL,"|");
	jci6_Apr        = tc_strtok(NULL,"|");
	jci6_May        = tc_strtok(NULL,"|");
	jci6_Jun        = tc_strtok(NULL,"|");
	jci6_Jul        = tc_strtok(NULL,"|");
	jci6_Aug        = tc_strtok(NULL,"|");
	jci6_Sep        = tc_strtok(NULL,"|");
	jci6_Oct        = tc_strtok(NULL,"|");
	jci6_Nov        = tc_strtok(NULL,"|");
	jci6_Dec        = tc_strtok(NULL,"|");

    ITKCALL(AOM_lock ( cost_tag ));

    if( jci6_Jan != NULL && tc_strcmp( jci6_Jan, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 1 > month ) ) )
    {
        ECHO("set JCI6_JAN   309  %s \n",jci6_Jan);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JAN , strtod( jci6_Jan , NULL ) ) );
    }
    if( jci6_Feb != NULL && tc_strcmp( jci6_Feb, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 2 > month ) ) )
    {
        ECHO("set JCI6_FEB   309  %s \n",jci6_Feb);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_FEB , strtod( jci6_Feb , NULL ) ) );
    }
    if( jci6_Mar != NULL && tc_strcmp( jci6_Mar, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 3 > month ) ) )
    {
        ECHO("set JCI6_MAR   309  %s \n",jci6_Mar);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_MAR , strtod( jci6_Mar , NULL ) ) );
    }
    if( jci6_Apr != NULL && tc_strcmp( jci6_Apr, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 4 > month ) ) )
    {
        ECHO("set JCI6_APR   309  %s \n",jci6_Apr);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_APR , strtod( jci6_Apr , NULL ) ) );
    }
    if( jci6_May != NULL && tc_strcmp( jci6_May, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 5 > month ) ) )
    {
        ECHO("set JCI6_MAY   309  %s \n",jci6_May);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_MAY , strtod( jci6_May , NULL ) ) );
    }
    if( jci6_Jun != NULL && tc_strcmp( jci6_Jun, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 6 > month ) ) )
    {
        ECHO("set JCI6_JUN   309  %s \n",jci6_Jun);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JUN , strtod( jci6_Jun , NULL ) ) );
    }
    if( jci6_Jul != NULL && tc_strcmp( jci6_Jul, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 7 > month ) ) )
    {
        ECHO("set JCI6_JUL   309  %s \n",jci6_Jul);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_JUL , strtod( jci6_Jul , NULL ) ) );
    }
    if( jci6_Aug != NULL && tc_strcmp( jci6_Aug, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 8 > month ) ) )
    {
        ECHO("set JCI6_AUG   309  %s \n",jci6_Aug);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_AUG , strtod( jci6_Aug , NULL ) ) );
    }
    if( jci6_Sep != NULL && tc_strcmp( jci6_Sep, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 9 > month ) ) )
    {
        ECHO("set JCI6_SEP   309  %s \n",jci6_Sep);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_SEP , strtod( jci6_Sep , NULL ) ) );
    }
    if( jci6_Oct != NULL && tc_strcmp( jci6_Oct, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 10 > month ) ) )
    {
        ECHO("set JCI6_OCT   309  %s \n",jci6_Oct);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_OCT , strtod( jci6_Oct , NULL ) ) );
    }
    if( jci6_Nov != NULL && tc_strcmp( jci6_Nov, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 11 > month ) ) )
    {
        ECHO("set JCI6_NOV   309  %s \n",jci6_Nov);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_NOV , strtod( jci6_Nov , NULL ) ) );
    }
    if( jci6_Dec != NULL && tc_strcmp( jci6_Dec, "") != 0 )//&& ( atoi( jci6_Year ) > year || ( atoi( jci6_Year ) == year && 12 > month ) ) )
    {
        ECHO("set JCI6_DEC   309  %s \n",jci6_Dec);
        ITKCALL( AOM_set_value_double( cost_tag , JCI6_DEC , strtod( jci6_Dec , NULL ) ) );
    }

    ITKCALL( AOM_save( cost_tag ) );

	ITKCALL( AOM_unlock( cost_tag ) );

    if(propertys != NULL)
    {
        MEM_free( propertys );
        propertys = NULL;
    }
}

int getQueryeCostInfoTag( tag_t query_tag , char * costPropertys , char * error ,tag_t * costInfoTag)
{
    char *postion_name    = NULL,
         *object_name     = NULL,
         *propertys       = NULL,
         **entries        = NULL,
         **values         = NULL,
	     *other_values[3];

    char *jci6_CPT        = NULL,
         *jci6_RateLevel  = NULL,
         *jci6_Division   = NULL,
         *jci6_Year       = NULL,
         *display_name    = NULL;

    int  entry_count      = 0,
         num_found        = 0,
         ifail            = ITK_ok;

    tag_t *results        = NULL,
          rateLevel_tag   = NULLTAG,
          division_tag    = NULLTAG;


    propertys = (char*)MEM_alloc( ( tc_strlen( costPropertys ) + 1) * sizeof(char));
    tc_strcpy( propertys , costPropertys);

    jci6_CPT        = tc_strtok(propertys,"|");
	jci6_RateLevel  = tc_strtok(NULL,"|");
	jci6_Division   = tc_strtok(NULL,"|");
	jci6_Year       = tc_strtok(NULL,"|");

    SA_find_group( jci6_Division , &division_tag );

    if( division_tag == NULLTAG )
    {
        if( tc_strstr( error , jci6_Division ) == NULL )
        {
            tc_strcat( error , jci6_Division );
            tc_strcat( error , "," );
        }
        return 10;
    }

    ITKCALL( AOM_ask_value_string( division_tag , DISPLAY_NAME , &display_name));

    ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

    other_values[0] = (char*)MEM_alloc( ( tc_strlen( jci6_CPT ) + 1) * sizeof(char));
    other_values[1] = (char*)MEM_alloc( ( tc_strlen( display_name ) + 1) * sizeof(char));
    other_values[2] = (char*)MEM_alloc( ( tc_strlen( jci6_Year ) + 1) * sizeof(char));

    tc_strcpy( other_values[0] , jci6_CPT );
    tc_strcpy( other_values[1] , display_name );
    tc_strcpy( other_values[2] , jci6_Year );

    ITKCALL( QRY_execute( query_tag , entry_count , entries , other_values , &num_found , &results ) );

    ECHO("num_found417 = %d\n", num_found);

    if( num_found > 0 )
    {
        for(int i = 0 ; i < num_found ; i++ )
        {  
            ITKCALL( AOM_ask_value_tag( results[i]  , JCI6_RATELEVEL , &rateLevel_tag));
            ECHO("jci6_RateLevel422 = %s\n", jci6_RateLevel);
            ECHO("rateLevel_tag423 = %u\n", rateLevel_tag);
            if( rateLevel_tag != NULLTAG )
            {
                ITKCALL( AOM_ask_value_string( rateLevel_tag , OBJECT_NAME , &object_name));
                ECHO("object_name429 = %s\n", object_name);
                if( tc_strcmp( object_name , jci6_RateLevel) == 0 )
                {
                    if( object_name != NULL)
                    {
	                    MEM_free( object_name );
	                    object_name = NULL;
                    }

                    *costInfoTag = results[i];
                    break;
                }
                if( object_name != NULL )
                {
                    MEM_free( object_name );
                    object_name = NULL;
                }
            
            }else if( tc_strcmp( jci6_RateLevel , "Internal" ) == 0 )
            {
                *costInfoTag = results[i] ;

                break;
            }
            
        }
        
    }

    if( other_values[0] != NULL )
    {
        MEM_free( other_values[0] );
        other_values[0] = NULL;
    }

    if( other_values[1] != NULL )
    {
        MEM_free( other_values[1] );
        other_values[1] = NULL;
    }

    if( other_values[2] != NULL )
    {
        MEM_free( other_values[2] );
        other_values[2] = NULL;
    }

    if( entries != NULL )
    {
        MEM_free( entries );
        entries = NULL;
    }

    if( values != NULL )
    {
        MEM_free( values );
        values = NULL;
    }

    if( propertys != NULL )
    {
        MEM_free( propertys );
        propertys = NULL;
    }

    if( display_name != NULL )
    {
        MEM_free( display_name );
        display_name = NULL;
    }

    if( results != NULL )
    {
        MEM_free( results );
        results = NULL;
    }

    return ifail;

}


int open_or_close_pass ( void * returnValueType )
{
	int	 ifail = 0,
         key   = 0;

    USERARG_get_int_argument( &key );
	if(key>0){
		POM_AM__set_application_bypass( TRUE ) ;
	}else{
		 POM_AM__set_application_bypass( FALSE );
	}

	

    return ifail;
}

int Check_Preference( char *pName)
{
	int customError = ITK_ok, pref_count = 0;

	ITKCALL( PREF_set_search_scope( TC_preference_site ) );

	ITKCALL( PREF_ask_value_count( pName , &pref_count ) );

	if( pref_count == 0 )
	{
		TEST_ERROR( ERROR_PREFERENCE_NOT_FOUND ); 
	}

	return customError;
}

/****************************************************************************** 
 * FUNCTION: gbk2utf8 
 * DESCRIPTION: 实现由gbk编码到utf8编码的转换  
 *  
 * Input: utfStr,转换后的字符串;  srcStr,待转换的字符串; maxUtfStrlen, utfStr的最 
            大长度 
 * Output: utfStr 
 * Returns: -1,fail;>0,success 
 *  
 * modification history 
 * -------------------- 
 *  2013-may-4, zhanggl written 
 * -------------------- 
 ******************************************************************************/  
int gbk2utf8( char *utfStr, const char *srcStr, int maxUtfStrlen )  
{  
    if( NULL == srcStr )  
    {  
        printf("Bad Parameter\n");  
        return -1;  
    }  
  
    //首先先将gbk编码转换为unicode编码   
    if( NULL == setlocale( LC_ALL, "zh_CN.gbk" ) )//设置转换为unicode前的码,当前为gbk编码   
    {  
        printf("Bad Parameter\n");  
        return -1;  
    }  
  
    int unicodeLen = mbstowcs( NULL, srcStr, 0 );//计算转换后的长度   
    if( unicodeLen <= 0)  
    {  
        printf("Can not Transfer!!!\n");  
        return -1;  
    }  
    wchar_t *unicodeStr=(wchar_t *)calloc(sizeof(wchar_t),unicodeLen+1);  
    mbstowcs( unicodeStr, srcStr, strlen( srcStr ) );//将gbk转换为unicode   
      
    //将unicode编码转换为utf8编码   
    if( NULL == setlocale( LC_ALL, "zh_CN.utf8" ) )//设置unicode转换后的码,当前为utf8   
    {  
        printf("Bad Parameter\n");  
        return -1;  
    }  
    int utfLen = wcstombs( NULL, unicodeStr, 0 );//计算转换后的长度   
    if( utfLen <= 0 )  
    {  
        printf("Can not Transfer!!!\n");  
        return -1;  
    }  
    else if( utfLen >= maxUtfStrlen )//判断空间是否足够   
    {  
        printf("Dst Str memory not enough\n");  
        return -1;  
    }  
    wcstombs( utfStr, unicodeStr, utfLen );  
    utfStr[utfLen] = 0;//添加结束符   
    free( unicodeStr );  
    return 0;  
}
int get_china_text( char *uid, char value_one[], char value_two[])
{
	tag_t dataset_tag    = NULLTAG;

	ITKCALL( POM_string_to_tag( uid, &dataset_tag ) );

	if( dataset_tag )
	{
		tag_t  ref_object = NULLTAG;
		AE_reference_type_t reference_type;	
		char ref_name[WSO_name_size_c + 1] = "Text";

		ITKCALL(AE_ask_dataset_named_ref(dataset_tag, ref_name, &reference_type, &ref_object));
		if(reference_type == AE_PART_OF)
		{
			char new_ds_name[WSO_name_size_c + 1] = "";
			char *new_file_name = USER_new_file_name( new_ds_name, ref_name, "txt", 0);
			char temp_file[SS_MAXPATHLEN] = "";

			sprintf( temp_file, "%s/%s", TC_getenv("TMPDIR"), new_file_name );
			ECHO("temp_file = %s\n", temp_file);

			ITKCALL( IMF_export_file( ref_object, temp_file ) );

			FILE   *fp          = NULL;
			char read_line[SS_MAXMSGLEN]    = "";

			if(( fp = fopen( temp_file, "rt" ) ) != NULL )
			{
				fgets( read_line, SS_MAXMSGLEN, fp);
				if(tc_strcmp(read_line, "\n") != 0)
				{
					read_line[tc_strlen(read_line)-1] = '\0';
					gbk2utf8( value_one, read_line, SS_MAXMSGLEN );
				} 
				fgets( read_line, SS_MAXMSGLEN, fp);
				if(tc_strcmp(read_line, "\n") != 0)
				{
					read_line[tc_strlen(read_line)-1] = '\0';
					gbk2utf8( value_two, read_line, SS_MAXMSGLEN );
				} 
			}
			fclose( fp );
		}
		ITKCALL( AOM_delete(dataset_tag ) );
	}
	return 0;
}
int create_object(void * returnValueType)
{
    int ifail      = ITK_ok,
        prop_len   = 0,
        value_len  = 0,
		str_cnt    = 0;

    char *object_type  = NULL,
         **prop_name   = NULL,
         **prop_value  = NULL,
		 *uid          = NULL,
		 *gbk_str      = NULL,
		 value_one[SS_MAXMSGLEN]    = "",
		 value_two[SS_MAXMSGLEN]    = "",
		 utf_str[100]  = "";

    tag_t item_tag      = NULLTAG,
          item_Rev_tag  = NULLTAG;

	ask_opt_debug();

    ifail = USERARG_get_string_argument( &object_type );
    ifail = USERARG_get_string_array_argument( &prop_len, &prop_name );
    ifail = USERARG_get_string_array_argument( &value_len, &prop_value );
	ifail = USERARG_get_string_argument( &uid );

	/*char *strTest = "你好";
	printf("strTest = %s\n", strTest);
	str_cnt = 2 * tc_strlen( strTest );
	gbk_str = (char*)MEM_alloc( str_cnt * sizeof( char ) );
	if( gbk2utf8( gbk_str, strTest, str_cnt ) )
	{
		ECHO("GBK to UTF8 error \n");
	}
	else
	{
		printf("gbk_str = %s\n", gbk_str);
	}
	MEM_free( gbk_str );
	gbk_str = NULL;*/
	if( uid )
	{
		ECHO("uid = %s \n", uid );
		get_china_text( uid, value_one, value_two);
		ECHO("value_one = %s, value_two = %s\n", value_one, value_two );
	}

    if( object_type )
    {
        ITKCALL( ITEM_create_item( NULL, "DisignRequest", object_type, NULL, &item_tag, &item_Rev_tag) );

        if( item_Rev_tag )
        {
            ITKCALL( AOM_save( item_tag ) );
            ITKCALL( AOM_unlock( item_tag ) );
            ITKCALL( AOM_save( item_Rev_tag ) );
            ITKCALL( AOM_unlock( item_Rev_tag ) );

            ITKCALL( AOM_lock( item_Rev_tag ) );
            for(int ix = 0; ix < prop_len; ix++ )
            {
				ECHO("prop_name[%d] = %s, prop_value[%d] = %s\n", ix, prop_name[ix], ix, prop_value[ix]);
    //            if( ix == 0 || ix == 1 )
    //            {
    //                ITKCALL( AOM_set_value_string( item_Rev_tag, prop_name[ix], prop_value[ix] ) );
    //                /*ITKCALL( AOM_UIF_set_localized_value_string( item_Rev_tag, prop_name[ix],
				//		LANGUAGENAME, prop_value[ix+8], TC_TRANSLATIONSTATUS_approved, FALSE ) );*/
				//	ECHO("prop_name[%d] = %s, prop_value[%d] = %s\n", ix+8, prop_name[ix], ix, prop_value[ix+8]);
				//	str_cnt = 2 * tc_strlen( prop_value[ix+8] );
				//	gbk_str = (char*)MEM_alloc( str_cnt * sizeof( char ) );
				//	tc_strcpy( utf_str, prop_value[ix+8]);
				//	if( gbk2utf8( gbk_str, utf_str, str_cnt ) )
				//	{
				//		ECHO("GBK to UTF8 error \n");
				//	}
				//	else
				//	{
				//		ITKCALL( AOM_UIF_set_localized_value_string( item_Rev_tag, prop_name[ix],
				//			LANGUAGENAME, gbk_str, TC_TRANSLATIONSTATUS_approved, FALSE ) );
				//	}
				//	MEM_free( gbk_str );
				//	gbk_str = NULL;
				//}
				if( ix == 0 )
				{
					ITKCALL( AOM_set_value_string( item_Rev_tag, prop_name[ix], prop_value[ix] ) );
					ITKCALL( AOM_UIF_set_localized_value_string( item_Rev_tag, prop_name[ix],
						LANGUAGENAME, value_one, TC_TRANSLATIONSTATUS_approved, FALSE ) );
				}
				else if( ix == 1 )
				{
					ITKCALL( AOM_set_value_string( item_Rev_tag, prop_name[ix], prop_value[ix] ) );
					ITKCALL( AOM_UIF_set_localized_value_string( item_Rev_tag, prop_name[ix],
						LANGUAGENAME, value_two, TC_TRANSLATIONSTATUS_approved, FALSE ) );
				}
                else if( ix == 4 || ix == 5 )
                {
                    ITKCALL( AOM_set_value_double( item_Rev_tag, prop_name[ix], strtod( prop_value[ix], NULL ) ) );
                }
                else
                {
                    ITKCALL( ifail = AOM_set_value_string( item_Rev_tag, prop_name[ix], prop_value[ix] ) );
                    if( ifail )
                    {
                        ECHO("prop_name[%d] = %s, prop_value[%d] = %s\n", ix, prop_name[ix], ix, prop_value[ix]);
                    }
                }
            }
            ITKCALL( AOM_save( item_Rev_tag ) );
            ITKCALL( AOM_unlock( item_Rev_tag ) );

            *((tag_t*) returnValueType) = item_Rev_tag;
        }
    }

    MEM_free( object_type );
    MEM_free( prop_name );
    MEM_free( prop_value );
	MEM_free( uid );

    return ifail;
}

int revise_object(void * returnValueType)
{
    int ifail      = ITK_ok,
        prop_len   = 0,
        value_len  = 0,
        count      = 0,
		str_cnt    = 0;

    char **prop_name   = NULL,
         **prop_value  = NULL,
		 *uid          = NULL,
		 *gbk_str      = NULL,
		 value_one[SS_MAXMSGLEN]    = "",
		 value_two[SS_MAXMSGLEN]    = "",
		 utf_str[100]  = "";

    tag_t new_rev       = NULLTAG,
          item_Rev_tag  = NULLTAG,
          *objects      = NULL;

    ifail = USERARG_get_tag_argument( &item_Rev_tag );
    ifail = USERARG_get_string_array_argument( &prop_len, &prop_name );
    ifail = USERARG_get_string_array_argument( &value_len, &prop_value );
	ifail = USERARG_get_string_argument( &uid );

	if( uid )
	{
		ECHO("uid = %s \n", uid );
		get_china_text( uid, value_one, value_two);
		ECHO("value_one = %s, value_two = %s\n", value_one, value_two );
	}

    if( item_Rev_tag )
    {
        ITKCALL( ITEM_copy_rev( item_Rev_tag, NULL, &new_rev) );

        if( new_rev )
        {
            ITKCALL( ITEM_perform_deepcopy( new_rev, ITEM_revise_operation, 
                item_Rev_tag, &count, &objects) );

            MEM_free( objects );

            //ITKCALL( AOM_save( new_rev ) );
            //ITKCALL( AOM_unlock( new_rev ) );

            ITKCALL( AOM_lock( new_rev ) );
            for(int ix = 0; ix < prop_len; ix++ )
            {
				if( ix == 0 )
				{
					ITKCALL( AOM_set_value_string( new_rev, prop_name[ix], prop_value[ix] ) );
					ITKCALL( AOM_UIF_set_localized_value_string( new_rev, prop_name[ix],
						LANGUAGENAME, value_one, TC_TRANSLATIONSTATUS_approved, FALSE ) );
				}
				else if( ix == 1 )
				{
					ITKCALL( AOM_set_value_string( new_rev, prop_name[ix], prop_value[ix] ) );
					ITKCALL( AOM_UIF_set_localized_value_string( new_rev, prop_name[ix],
						LANGUAGENAME, value_two, TC_TRANSLATIONSTATUS_approved, FALSE ) );
				}
                else if( ix == 4 || ix == 5 )
                {
                    ITKCALL( AOM_set_value_double( new_rev, prop_name[ix], strtod( prop_value[ix], NULL ) ) );
                }
                else
                {
                    ITKCALL( ifail = AOM_set_value_string( new_rev, prop_name[ix], prop_value[ix] ) );
                    if( ifail )
                    {
                        printf("prop_name[%d] = %s, prop_value[%d] = %s\n", ix, prop_name[ix], ix, prop_value[ix]);
                    }
                }
            }
            ITKCALL( AOM_save( new_rev ) );
            ITKCALL( AOM_unlock( new_rev ) );

            *((tag_t*) returnValueType) = new_rev;
        }
    }

    MEM_free( prop_name );
    MEM_free( prop_value );

    return ifail;
}

void Split( string strArg, char spliter, vector<string> &ans )
{
	ans.clear(); 
	size_t index0 = 0;
	string one_arg;
    if (strArg.find_first_not_of(' ')==string::npos)
		strArg = "";
	while( strArg.size()>0 )
	{
		index0 = strArg.find_first_of( spliter );
		if( index0 != string::npos )
		{
			one_arg = strArg.substr( 0,index0 );
			strArg = strArg.substr( index0 + 1 );
			ans.push_back( one_arg );
		}
		else
		{
			ans.push_back( strArg );
			break;
		}
	}
}

void RemoveLiner( string &in, string &out )
{
	char buf[BUFSIZ] = "";
	for( int i=0; i<(int)in.size(); i++ )
	{
		if( in[i] == '\n')
			continue;
		else
			sprintf( buf,"%s%c", buf, in[i] );
	}
	out.assign(buf);
}

int filter_preferences( char *preferenceName, char *filterValue, char **templatePath)
{
	int customError  = ITK_ok,
		option_cnt   = 0,
		iCnt         = 0;

	char **option_values     = NULL,
		 projectType[32]     = "",
		 tempPath[128]       = "";

	ITKCALL( PREF_ask_char_values( preferenceName, &option_cnt, &option_values ) );

	for( iCnt = 0; iCnt < option_cnt; iCnt++ )
	{
		sscanf( option_values[iCnt], "%[^=]=%[^\n]", projectType, tempPath );
		#ifdef isDebug
		printf("projectType = %s; \t tempPath = %s\n", projectType, tempPath);
		#endif

		if( tc_strcmp( projectType, filterValue ) == 0)
		{
			*templatePath = (char *)MEM_alloc( (tc_strlen( tempPath ) + 1) * sizeof( char ) );
			tc_strcpy( *templatePath, tempPath);
		}
	}

	MEM_free( option_values );

	return customError;
}

int folder_deep_copy( tag_t new_folder_tag, tag_t *project_tag, tag_t *template_tag)
{
	int customError  = ITK_ok;

	tag_t   relation_type     = NULLTAG,
            relation          = NULLTAG; 

	
    ITKCALL( PROJ_assign_objects( 1 , project_tag, 1, &new_folder_tag ) );

    ITKCALL( GRM_find_relation_type( TC_PROGRAM_PREFERRED_ITEMS, &relation_type ) );

    if( relation_type )
    {
        ITKCALL( GRM_create_relation( *project_tag, new_folder_tag, relation_type ,NULLTAG, &relation ) );

        if( relation )
        {
            ITKCALL( GRM_save_relation( relation ) );
            ITKCALL( AOM_unlock( relation ) );
        }
    }

    create_child_folder( template_tag, &new_folder_tag, project_tag );

	return customError;
}

int create_child_folder( tag_t *template_tag, tag_t *parent_tag, tag_t *project_tag )
{
	int customError        = ITK_ok,
        num_of_references  = 0,
        iNum               = 0,
        n_ids              = 0,
        jn                 = 0;

     char object_type[WSO_name_size_c+1]  = "",
          object_name[WSO_name_size_c+1]  = "",
          *class_name                     = NULL;

	tag_t   *list_of_references = NULL,
            class_id            = NULLTAG,
            *superclass_ids     = NULL,
			relation_type       = NULLTAG,
			relation            = NULLTAG; 

	ITKCALL( GRM_find_relation_type( TC_PROGRAM_PREFERRED_ITEMS, &relation_type ) );

    ITKCALL( FL_ask_references( *template_tag, FL_fsc_no_order, &num_of_references, &list_of_references ) );

    for( iNum = 0; iNum < num_of_references; iNum++ )
    {
        ITKCALL( WSOM_ask_object_type( list_of_references[iNum], object_type ) );
        tag_t   new_folder_tag      = NULLTAG;
        if( tc_strcmp( object_type, FOLDER ) == 0 )
        {   
            char *  src_folder_type  =  NULL;
            ITKCALL( WSOM_ask_name( list_of_references[iNum], object_name ) );
            
            //ITKCALL( FL_create( object_name, "" , &new_folder_tag) );
            //ITKCALL( AOM_save( new_folder_tag ) );
           // ITKCALL( AOM_unlock( new_folder_tag ) );

             ITKCALL( AOM_ask_value_string( list_of_references[iNum],"object_type",&src_folder_type));
              createFolder( object_name,src_folder_type,  &new_folder_tag);
						MEM_free(src_folder_type);
						
            ITKCALL( AOM_lock( *parent_tag ) );
            FL_insert( *parent_tag, new_folder_tag, iNum+1);
            ITKCALL( AOM_save( *parent_tag ) );
            ITKCALL( AOM_unlock( *parent_tag ) );

			ITKCALL( PROJ_assign_objects( 1 , project_tag, 1, &new_folder_tag ) );
            copyAcl(list_of_references[iNum],new_folder_tag);
			//ITKCALL( GRM_create_relation( *project_tag, new_folder_tag, relation_type ,NULLTAG, &relation ) );
			//ITKCALL( GRM_save_relation( relation ) );
   //         ITKCALL( AOM_unlock( relation ) );

            create_child_folder( &(list_of_references[iNum]), &new_folder_tag, project_tag );
        }
        else
        {
            ITKCALL( POM_class_of_instance( list_of_references[iNum], &class_id ) );

            if( class_id )
            {
                ITKCALL( POM_superclasses_of_class(class_id, &n_ids, &superclass_ids ) );

                for(int jn = 0; jn < n_ids; jn++)
                {
                    ITKCALL( POM_name_of_class( superclass_ids[jn], &class_name ) );

                    if( tc_strcmp( class_name, FOLDER ) == 0 )
                    {   
                          char * src_folder_type      = NULL;
                        ITKCALL( WSOM_ask_name( list_of_references[iNum], object_name ) );

                        //ITKCALL( FL_create( object_name, "" , &new_folder_tag) );
                       
                       // ITKCALL( AOM_save( new_folder_tag ) );
                       // ITKCALL( AOM_unlock( new_folder_tag ) );

                        ITKCALL( AOM_ask_value_string( list_of_references[iNum],"object_type",&src_folder_type));
                         createFolder( object_name,src_folder_type,  &new_folder_tag);
												MEM_free(src_folder_type);
												
                        ITKCALL( AOM_lock( *parent_tag ) );
                        FL_insert( *parent_tag, new_folder_tag, iNum+1);
                        ITKCALL( AOM_save( *parent_tag ) );
                        ITKCALL( AOM_unlock( *parent_tag ) );

						ITKCALL( PROJ_assign_objects( 1 , project_tag, 1, &new_folder_tag ) );
                        copyAcl(list_of_references[iNum],new_folder_tag);
						//ITKCALL( GRM_create_relation( *project_tag, new_folder_tag, relation_type ,NULLTAG, &relation ) );
						//ITKCALL( GRM_save_relation( relation ) );
						//ITKCALL( AOM_unlock( relation ) );

                        create_child_folder( &(list_of_references[iNum]), &new_folder_tag, project_tag );
                    }
                    MEM_free( class_name );
                    class_name = NULL;
                }
                MEM_free( superclass_ids );
                superclass_ids = NULL;
            }
        }
    }

    MEM_free( list_of_references ); 

	return customError;
}

int createFolder(char * object_name,char * type,tag_t * obj_tag)
{
	tag_t boTag=NULLTAG,
		createInputTag=NULLTAG,
		itemType=NULLTAG;
	const char ** teststr = (const char **)MEM_alloc(1*sizeof(char*));
	teststr[0] = object_name;

	TCTYPE_find_type(type,type,&itemType);
    ECHO("createFolder itemType==%d \n",itemType);

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



	TCTYPE_construct_create_input ( itemType, &createInputTag );

	TCTYPE_set_create_display_value( createInputTag, "object_name", 1, teststr );
	TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, teststr );	
	//TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str );
	//TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr );	
	//TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str );

	ITKCALL(TCTYPE_create_object ( createInputTag, &boTag) );
	ITKCALL(AOM_save ( boTag ));
	ITKCALL(AOM_unlock ( boTag ));//remember unlock
	*obj_tag = boTag;
	SAFE_SM_FREE(teststr);
	SAFE_SM_FREE(ucpt_str);
	SAFE_SM_FREE(unitstr);
	SAFE_SM_FREE(year_str);

	return 0;
}

int copyAcl(tag_t src_tag,tag_t obj_tag)
{
	int	acl_len		=0,
        acl_len2		=0,
		n_granted	=0,
		n_revoked	=0;
	tag_t   eff_acl_tag	=NULLTAG,
		source_acl		=NULLTAG,
		accessor		=NULLTAG,
		*granted		=NULL,
		*revoked		=NULL;
    POM_AM__set_application_bypass( TRUE );
	ITKCALL(AM_get_effective_acl(src_tag,&eff_acl_tag,&acl_len));
	ECHO("eff_acl_tag==%d \n",eff_acl_tag);

	for(int i=0;i<acl_len;i++)
	{	
        char *acl_name = NULL;
	    ITKCALL(AM_effective_acl_line(eff_acl_tag,i,&source_acl,&accessor,&n_granted,&granted,&n_revoked,&revoked));
	    AM_ask_acl_name(source_acl, &acl_name);
	    //ECHO("acl_name = %s\n", acl_name );
	    if(tc_strcmp(acl_name, "OBJECT") == 0 )
	    {     
		    for(int j=0;j<n_granted;j++)
		    {
			    ITKCALL(AM_grant_privilege(obj_tag,accessor,granted[j]));
			    ITKCALL( AM_save_acl( obj_tag ));
			   // ECHO(" grant \n" );
		    }

		    for(int j=0;j<n_revoked;j++)
		    {
			    ITKCALL(AM_revoke_privilege(obj_tag,accessor,revoked[j]));
			    ITKCALL( AM_save_acl( obj_tag ));
			   // ECHO(" revoked \n" );
		    }	

	    }
        if(granted)
        {
            MEM_free(granted);
            granted = NULL;
        }
		
        if(revoked)
        {
		    MEM_free(revoked);
            revoked = NULL;
        }
	}
	ITKCALL(AM_free_effective_acl(eff_acl_tag));
	 POM_AM__set_application_bypass( FALSE ) ;

	return 0;
}


int list_division( TAGLIST *head, int *allDivisionCnt ) 
{
	int status                       = ITK_ok,
		first_level_children_cnt     = 0,
		second_level_children_cnt    = 0,
		xfCnt                        = 0,
		ysCnt                        = 0;

	tag_t parent_group_tag                    = NULLTAG,
		  *first_level_children_group_tag     = NULL,
		  *second_level_children_group_tag    = NULL;

	TAGLIST *node = NULL,
			*cNew = NULL;

	node = head;

	ITKCALL( SA_find_group( TECHCENTER, &parent_group_tag ) );
	if( !parent_group_tag )
	{
		HANDLE_ERROR_S1( ERROR_NO_FIND_GROUP, TECHCENTER);
	}

	ITKCALL( SA_ask_group_child_groups( parent_group_tag, 1, 
		&first_level_children_cnt, &first_level_children_group_tag ) );

	if( first_level_children_cnt )
	{
		for( xfCnt= 0; xfCnt < first_level_children_cnt; xfCnt++)
		{
			ITKCALL( SA_ask_group_child_groups( first_level_children_group_tag[xfCnt], 1, 
				&second_level_children_cnt, &second_level_children_group_tag ) );

			for( ysCnt = 0; ysCnt < second_level_children_cnt; ysCnt++ )
			{
				if( !check_the_same_group( head->next, second_level_children_group_tag[ysCnt] ) )
				{
					cNew = ( TAGLIST* )MEM_alloc( NODELEN );
					cNew->ds_tag = second_level_children_group_tag[ysCnt];
					cNew->next = NULL;
					node->next = cNew;
					node = cNew;
					(*allDivisionCnt)++;
				}
			}

			MEM_free( second_level_children_group_tag );
			second_level_children_group_tag = NULL;
		}
	}
	MEM_free( first_level_children_group_tag ); 

	if( *allDivisionCnt == 0 )
	{
		HANDLE_ERROR( ERROR_NO_FIND_DIVISION);
	}

	return status; 
}

int list_section( TAGLIST *head, int *allDivisionCnt ) 
{
	int status                       = ITK_ok,
		first_level_children_cnt     = 0,
		second_level_children_cnt    = 0,
		third_level_children_cnt     = 0,
		xfCnt                        = 0,
		ysCnt                        = 0,
		ztCnt                        = 0;

	tag_t parent_group_tag                    = NULLTAG,
		  *first_level_children_group_tag     = NULL,
		  *second_level_children_group_tag    = NULL,
		  *third_level_children_group_tag     = NULL;

	TAGLIST *node = NULL,
			*cNew = NULL;

	node = head;

	ITKCALL( SA_find_group( TECHCENTER, &parent_group_tag ) );
	if( !parent_group_tag )
	{
		HANDLE_ERROR_S1( ERROR_NO_FIND_GROUP, TECHCENTER);
	}

	ITKCALL( SA_ask_group_child_groups( parent_group_tag, 1, 
		&first_level_children_cnt, &first_level_children_group_tag ) );

	if( first_level_children_cnt )
	{
		for( xfCnt= 0; xfCnt < first_level_children_cnt; xfCnt++)
		{
			ITKCALL( SA_ask_group_child_groups( first_level_children_group_tag[xfCnt], 1, 
				&second_level_children_cnt, &second_level_children_group_tag ) );

			for( ysCnt = 0; ysCnt < second_level_children_cnt; ysCnt++ )
			{
				ITKCALL( SA_ask_group_child_groups( second_level_children_group_tag[ysCnt], 1, 
					&third_level_children_cnt, &third_level_children_group_tag ) );

				for( ztCnt = 0; ztCnt < third_level_children_cnt; ztCnt++)
				{
					if( !check_the_same_group( head->next, third_level_children_group_tag[ztCnt] ) )
					{
						cNew = ( TAGLIST* )MEM_alloc( NODELEN );
						cNew->ds_tag = third_level_children_group_tag[ztCnt];
						cNew->next = NULL;
						node->next = cNew;
						node = cNew;
						(*allDivisionCnt)++;
					}
				}
				MEM_free( third_level_children_group_tag );
				third_level_children_group_tag = NULL;
			}

			MEM_free( second_level_children_group_tag );
			second_level_children_group_tag = NULL;
		}
	}
	MEM_free( first_level_children_group_tag ); 

	if( *allDivisionCnt == 0 )
	{
		HANDLE_ERROR( ERROR_NO_FIND_DIVISION);
	}

	return status; 
}

int freelist( TAGLIST *head )
{
	TAGLIST *point = head,
		    *pointNext;

	while( point ) 
	{
		pointNext = point->next;
		point->next = NULL;
		MEM_free( point );
		point = pointNext;
	}

	return 0;
} 

static int check_the_same_group( TAGLIST *head, tag_t group_tag )
{
	TAGLIST *point = head;

	while( point ) 
	{
		if( point->ds_tag == group_tag )
		{
			return 1;
		}
		point = point->next;
	}

	return ITK_ok;
}

void getTimeSheetToCount( tag_t timeSheet , char * status )
{
    int ifail               = ITK_ok,
        count               = 0,
		arg_count           = 0,
		minutes             = 0,
		year                = 0,
		month               = 0,
		entry_count         = 0,
		num_found           = 0,
		preferredNum        = 0,
		schedulemembersNum  = 0,
        billrate_type       = 0;

    double  default_rate    = 0;

	char  *arg_str_value    = NULL,
          *attachment_type  = NULL,
          *bill_type        = NULL,
          *jci6_TaskType    = NULL,
		  *project_ids      = NULL,
		  *project_id       = NULL,
		  **entries         = NULL,
		  **values          = NULL,
		  *user_id          = NULL,
          *other_entries[7],
		  *other_values[7],
          string_year[25];

	//const char query_name[ QRY_name_size_c + 1] = YFJC_SEARCHCOSTINFOBYDISCIPLINE;

	BMF_extension_arguments_t *input_args = NULL;

	date_t date                 = NULLDATE, 
		   finish_date          = NULLDATE;

	tag_t rootTask              = NULLTAG,
          *attachments          = NULL,
          *release_status_list  = NULL,
		  *schedulemembers      = NULL,
		  scheduletask_tag      = NULLTAG,
		  user_tag              = NULLTAG,
          owning_group          = NULLTAG,
		  schedule_tag          = NULLTAG,
		  billrate_tag          = NULLTAG,
		  *preferred_items      = NULL,
		  relation_tag          = NULLTAG,
		  item_tag              = NULLTAG,
		  jci6_Division         = NULLTAG,
          jci6_Section          = NULLTAG,
          *results              = NULL,
          project_tag           = NULLTAG,
          query_tag             = NULLTAG,
          query_tag_TaskTypeIsNull=NULLTAG,
          postion               = NULLTAG;

	ECHO("==== getTimeSheetToCount 创建costinfo start ====\n");

    ITKCALL( GRM_find_relation_type( IMAN_EXTERNAL_OBJECT_LINK , &relation_tag ) );

	ITKCALL( AOM_ask_value_tag( timeSheet , SCHEDULETASK_TAG , &scheduletask_tag ) );//所属任务

	ITKCALL( AOM_ask_value_tag( timeSheet , USER_TAG , &user_tag ) );//填报用户

    ITKCALL( AOM_ask_value_tag( timeSheet , OWNING_GROUP , &owning_group ) );//填报用户组

	ITKCALL( AOM_ask_value_string( user_tag , USER_ID , &user_id ) );//填报用户ID

//	ECHO("user_id = %s\n", user_id );
    getUserPostion( user_id , &postion );
//	ECHO("postion = %u\n" ,postion );

    if( postion != NULLTAG )
    {
        char* postion_name = NULL;

        ITKCALL( AOM_ask_value_string( postion , OBJECT_NAME , &postion_name ) );

        if(tc_strcmp(postion_name,"ExtSupporter")==0)
        {
			// Modify by zhanggl MAY-5-2013
            tag_t CostValue          = NULLTAG,
				  relation_type      = NULLTAG,
				  *secondary_objects = NULL;
			int sec_cnt = 0;

			ITKCALL( GRM_find_relation_type( "CostValue_Relation", &relation_type ) );

			if( relation_type )
			{
				ITKCALL( GRM_list_secondary_objects_only( user_tag, relation_type, &sec_cnt, &secondary_objects) );
				if( sec_cnt )
				{
					CostValue = secondary_objects[0];
					char* costNumStr = NULL;

					ITKCALL(AOM_ask_value_string(CostValue,"cost",&costNumStr));

					default_rate = strtod( costNumStr, NULL );//用户费率

					if( costNumStr != NULL )
					{
						MEM_free(costNumStr);
						costNumStr = NULL;
					}

					MEM_free( secondary_objects );
					secondary_objects = NULL;
				}
			}
            //ITKCALL(AOM_ask_value_tag(user_tag,"CostValue_Relation",&CostValue));
            //if(CostValue!=NULLTAG)
            //{
            //    char* costNumStr = NULL;

            //    ITKCALL(AOM_ask_value_string(CostValue,"cost",&costNumStr));

            //    default_rate = strtod(costNumStr,NULL);//用户费率

            //    if(costNumStr!=NULL)
            //    {
            //        MEM_free(costNumStr);
            //        costNumStr = NULL;
            //    }
            //}
			//END
        }
        else
        {
            ITKCALL(AOM_ask_value_double( postion , DEFAULT_RATE , &default_rate ) );//学科费率
        }
         
        if(postion_name!=NULL)
	    {
		    MEM_free(postion_name);
		    postion_name = NULL;
	    }
        
    }

  //  ECHO("default_rate = %lf\n" ,default_rate );

	char *timesheetName=NULL;
	string strTimesheetName;

	ITKCALL( AOM_ask_value_date( timeSheet , DATE , &date ) );//填报日期

	year  = date.year;//年度
	month = date.month + 1;//月

	//  ECHO("year=%d  month=%d\n" ,year, month);

	ITKCALL( AOM_ask_value_int( timeSheet , MINUTES , &minutes ) );//填报工时

	ITKCALL( AOM_ask_value_string( timeSheet , BILL_TYPE , &bill_type ) );//清单类型

	ITKCALL( AOM_ask_value_string( scheduletask_tag , JCI6_TASKTYPE , &jci6_TaskType ) );//任务类型

	ITKCALL( AOM_ask_value_tag( scheduletask_tag , SCHEDULE_TAG , &schedule_tag ) );//时间表任务

	ITKCALL( AOM_ask_value_tag( timeSheet , BILLRATE_TAG , &billrate_tag ) );//获得倍率或制定费率 timeSheet

	ITKCALL( AOM_ask_value_string( timeSheet , "object_name" , &timesheetName ) );//获得倍率或制定费率 timeSheet
	strTimesheetName.assign(timesheetName);
	SAFE_SM_FREE(timesheetName);

	int proNum1=0;
	tag_t *projlists=NULL;
	ITKCALL(AOM_ask_value_tags(timeSheet,"project_list",&proNum1,&projlists));
	project_tag=NULLTAG;
	string strPrjID=strTimesheetName.substr(0,strTimesheetName.find_first_of("-"));//find_last_of
	project_id = ( char* )MEM_alloc( ( strPrjID.size()  + 1 )*sizeof( char ) );
	if(proNum1>0){
		char *PRJ_id=NULL;
		project_tag=projlists[0];
	//	 ECHO("lala project_id-->%s \n",project_id);
		ITKCALL( AOM_ask_value_string(project_tag,"project_id",&PRJ_id));
		strPrjID.append(PRJ_id);
		SAFE_SM_FREE(PRJ_id);
		strcpy(project_id,strPrjID.c_str());
	//	 ECHO("lala project_id-->%s \n",project_id);
	}else{

	//	ECHO("lala strPrj-->%s \n",strPrjID.c_str());
		
		strcpy(project_id,strPrjID.c_str());
	//	 ECHO("lala project_id-->%s \n",project_id);
		 //modify by wuwei
		ITKCALL( PROJ_find( project_id , &project_tag ) );
	}


	
	 

    getGroupDivisionSection(owning_group,&jci6_Division,&jci6_Section);

	//ITKCALL( AOM_ask_value_string( schedule_tag , PROJECT_IDS , &project_ids ) );//所属项目ID
	//ECHO("project_ids = %s\n", project_ids);

	//if( tc_strstr( project_ids , "," ) == NULL )
	//{
	//	project_id = ( char* )MEM_alloc( ( tc_strlen( project_ids ) + 1 )*sizeof( char ) );

	//	tc_strcpy( project_id , project_ids );
	//}
	//else
	//{
	//    project_id = tc_strtok( project_ids , "," );
	//}
 //   if( tc_strcmp( "" , project_id ) == 0 )
 //   {
 //       return ;
 //   }
	
	

	
//	 ECHO("ww schedule_tag-->%u  scheduletask_tag:%u jci6_TaskType:%s\n", schedule_tag,scheduletask_tag,jci6_TaskType);
   // ITKCALL( AOM_ask_value_tag( schedule_tag, OWNING_PROJECT, &project_tag ) );

  //  ECHO("project_tag258 = %u\n", project_tag);

    //ITKCALL( AOM_ask_value_string( project_tag, PROJECT_ID, &project_id ) );

//    ECHO("project_id262 = %s\n", project_id);

	ITKCALL( AOM_ask_value_tags( project_tag , TC_PROGRAM_PREFERRED_ITEMS , &preferredNum, &preferred_items ) );

 //   ECHO("preferredNum262 = %d\n", preferredNum);

	for( int i = 0; i < preferredNum ; i++ )
	{
		char  *object_type = NULL;

		ITKCALL( AOM_ask_value_string( preferred_items[i] , OBJECT_TYPE , &object_type ) );

		if( tc_strcmp( JCI6_PROGRAMINFO , object_type ) == 0 )
		{
			//ECHO("Find project info \n");

		    item_tag = preferred_items[i];

		    break;
		}
		if( object_type != NULL )
		{
			MEM_free( object_type );
            
			object_type = NULL;
		}
	}

	ITKCALL( QRY_find( YFJC_SEARCHCOSTINFOBYDISCIPLINE , &query_tag ) );
    ITKCALL( QRY_find( "YFJC_SearchCostInfoByDiscipline_TaskTypeIsNull", &query_tag_TaskTypeIsNull ) );

	if( query_tag == NULLTAG )
	{
		return ;
	}
        if( query_tag_TaskTypeIsNull == NULLTAG )
	{
		return ;
	}

	//ITK__convert_uid_to_tag("DVVpozwPopEFZA",&item_tag);
   // ECHO("item_tag293 = %u\n", item_tag);

    if( item_tag == NULLTAG )
	{
		return ;
	}

//	ECHO("Find %s qry\n", YFJC_SEARCHCOSTINFOBYDISCIPLINE);
  //  ECHO("Find YFJC_SearchCostInfoByDiscipline_TaskTypeIsNull qry\n");

//	ECHO("billrate_tag-->%u \n", billrate_tag);

	// 得到学科或者用户对应的费率
    if( billrate_tag != NULLTAG )
	{
        tag_t costvalue  = NULLTAG;

        char *costNumStr = NULL;

        ITKCALL( AOM_ask_value_int( billrate_tag , TC_TYPE , &billrate_type ) );//任务类型

        ITKCALL( AOM_ask_value_tag( billrate_tag , COSTVALUE_FORM_TAG , &costvalue ) );

        ITKCALL( AOM_ask_value_string( costvalue , COST , &costNumStr ) );

        double tempcostNum = strtod( costNumStr , NULL );
	//	ECHO("tempcostNum = %lf\n", tempcostNum);

        if( costNumStr != NULL )
	    {
		    MEM_free( costNumStr );

		    costNumStr = NULL;
	    }

        if( billrate_type == 0 )//倍数
        {
            default_rate = default_rate * tempcostNum;
        }
        else if( billrate_type == 1 )//固定费率
        {
            default_rate = tempcostNum;
        }
	}

    sprintf( string_year, "%d" ,year);

    char   *division_name = NULL,
           *section_name  = NULL,
           *jci6_cpt      = NULL;
    int    nx = 0,
           mx = 0;

    if( jci6_Division != NULLTAG )
    {
         //ITKCALL( AOM_ask_value_string( jci6_Division , DISPLAY_NAME , &division_name ) );
		//ITKCALL( AOM_ask_value_string( jci6_Division , "name" , &division_name ) );
		 ITKCALL( AOM_ask_value_string( jci6_Division , "full_name" , &division_name ) );
    }
    if( jci6_Section != NULLTAG )
    {
        //ITKCALL( AOM_ask_value_string( jci6_Section , DISPLAY_NAME , &section_name ) );
		ITKCALL( AOM_ask_value_string( jci6_Section , "full_name" , &section_name ) );
    }

	ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

    if( project_id != NULL && tc_strcmp( "" , project_id ) != 0 )
    {
		other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx]) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( project_id ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx]  ,project_id );

        nx++;
    }
    mx++;
    if( user_id != NULL && tc_strcmp( "" , user_id ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx]) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( user_id ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , user_id );

        nx++;
    }
    mx++;
    if( division_name != NULL && tc_strcmp( "" , division_name ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( division_name ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , division_name );

        nx++;
    }
    mx++;
    if( jci6_TaskType != NULL && tc_strcmp( "" , jci6_TaskType ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( jci6_TaskType ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , jci6_TaskType );

        nx++;
    }
    //else
    //{
       // other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        //other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( jci6_TaskType ) + 1 ) * sizeof( char ) );

        //tc_strcpy( other_entries[nx] , entries[mx] );

        //tc_strcpy( other_values[nx] , "" );

        //nx++;
    //}
    mx++;
    if( string_year != NULL && tc_strcmp( "" , string_year ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( string_year ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , string_year );

        nx++;
    }
    mx++;
    if( bill_type != NULL && tc_strcmp( "" , bill_type ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( bill_type ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , bill_type );

        nx++;
    }
    mx++;
    if( section_name != NULL && tc_strcmp( "" , section_name ) != 0 )
    {
        other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

        other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( section_name ) + 1 ) * sizeof( char ) );

        tc_strcpy( other_entries[nx] , entries[mx] );

        tc_strcpy( other_values[nx] , section_name );

        nx++;
    }
    //2013.4.11 add start
    mx++;

    other_entries[nx] = ( char* )MEM_alloc( ( tc_strlen( entries[mx] ) + 1 ) * sizeof( char ) );

    other_values[nx]  = ( char* )MEM_alloc( ( tc_strlen( "Actual" ) + 1 ) * sizeof( char ) );

    tc_strcpy( other_entries[nx] , entries[mx] );

    tc_strcpy( other_values[nx] , "Actual" );

    nx++;
    //2013.4.11 add  end

	//ECHO("division_name = %s\n", division_name);
	//ECHO("jci6_TaskType = %s\n", jci6_TaskType);
	//ECHO("string_year = %s\n", string_year);
	//ECHO("bill_type = %s\n", bill_type);
	//ECHO("section_name = %s\n", section_name);

    if( jci6_TaskType != NULL && tc_strcmp( "" , jci6_TaskType ) != 0 )
    {
        ITKCALL( QRY_execute( query_tag , nx , other_entries , other_values , &num_found , &results ) );
    }
    else
    {
       // ECHO("query_tag_TaskTypeIsNull \n");
        ITKCALL( QRY_execute( query_tag_TaskTypeIsNull , nx , other_entries , other_values , &num_found , &results ) );
    }

	
	//ECHO("num_found = %d\n", num_found);

    if( division_name != NULL )
    {
        MEM_free( division_name );
        
        division_name = NULL;
    }

    if( section_name != NULL )
    {
        MEM_free( section_name );

        section_name = NULL;
    }

    for( int n = 0 ; n < nx ; n++ )
    {
        if( other_entries[n] != NULL )
	    {
		    MEM_free( other_entries[n] );

		    other_entries[n] = NULL;
	    }
        if( other_values[n] != NULL )
	    {
		    MEM_free( other_values[n] );

		    other_values[n] = NULL;
	    }
    }

	if( num_found > 0 )
	{
		if( tc_strcmp( "cancel release" , status ) == 0 )//取消发布
		{
			for( int i = 0 ; i < num_found ; i++ )
            {
                char * jci6_Unit   = NULL;

                tag_t  RateLevel   = NULLTAG;
                
                ITKCALL( AOM_ask_value_tag( results[i] , JCI6_RATELEVEL , &RateLevel ) );

                if( RateLevel == postion )
                {
                    ITKCALL( AOM_ask_value_string( results[i] , JCI6_UNIT , &jci6_Unit ) );

                    POM_AM__set_application_bypass( TRUE ) ;

                    ITKCALL( AOM_lock ( results[i] ) );

                    setMonthInfo( results[i] , month , minutes , jci6_Unit , default_rate , "remove" );

                    ITKCALL( AOM_save ( results[i] ) );

	                ITKCALL( AOM_unlock ( results[i] ) );

                    //ITKCALL( AOM_refresh( results[i] ,true ) );

                    POM_AM__set_application_bypass( FALSE ) ;

                    if( jci6_Unit != NULL )
	                {
		                MEM_free( jci6_Unit );

		                jci6_Unit = NULL;
	                }
                }
            } 
		}
		else if( tc_strcmp( "release" , status ) == 0 )//发布
		{
                    int isfound = 0;//如果是0说明学科变了，要新建
			for( int i = 0 ; i < num_found ; i++ )
            {
                char * jci6_Unit   = NULL;

                tag_t  RateLevel   = NULLTAG;

                ITKCALL( AOM_ask_value_tag( results[i] , JCI6_RATELEVEL , &RateLevel ) );
             //   ECHO("RateLevel==%u\n",RateLevel);
              //  ECHO("postion==%u\n",postion);
                if( RateLevel == postion )
                {
                    ITKCALL( AOM_ask_value_string( results[i] , JCI6_UNIT , &jci6_Unit ) );

					 POM_AM__set_application_bypass( TRUE ) ;

                    ITKCALL( AOM_lock ( results[i] ) );

                    setMonthInfo( results[i] , month , minutes , jci6_Unit , default_rate , "add" );

                    ITKCALL( AOM_save ( results[i] ) );

	                ITKCALL( AOM_unlock ( results[i] ) );

                    //ITKCALL( AOM_refresh( results[i] ,true ) );

					 POM_AM__set_application_bypass( FALSE ) ;
                    isfound++;
                    if( jci6_Unit != NULL )
	                {
		                MEM_free( jci6_Unit );

		                jci6_Unit = NULL;
	                }
                }
            }
           // ECHO("isfound==%d\n",isfound);
            if( isfound==0 )
            {
                createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITHOUR , default_rate);

			createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITMANMONTH , default_rate);

			createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITYUAN , default_rate);
            }
		}
	}
	else if( num_found == 0 )
	{
		if( tc_strcmp( "release" , status ) == 0 )//发布
		{
			createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITHOUR , default_rate);

			createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITMANMONTH , default_rate);

			createCostInfoTag_and_set_property( item_tag , relation_tag , project_tag , bill_type , year , month , minutes , user_tag , 
                jci6_TaskType , jci6_Division , postion , jci6_Section, JCI6_UNITYUAN , default_rate);
		}
	}
    if( project_ids != NULL )
	{
		MEM_free( project_ids );

		project_ids = NULL;
	}

    if( project_id != NULL )
	{
		MEM_free( project_id );

		project_id = NULL;
	}

    if( preferred_items != NULL )
	{
		MEM_free( preferred_items );

		preferred_items = NULL;
	}

    if( entries != NULL )
    {
        MEM_free( entries );

        entries = NULL;
    }

    if( values != NULL )
    {
        MEM_free( values );

        values = NULL;
    }

    if( results != NULL )
    {
        MEM_free( results );

        results = NULL;
    }

    if( attachments != NULL )
    {
        MEM_free( attachments );

        attachments = NULL;
    }


	ECHO("==== getTimeSheetToCount 创建costinfo end ====\n");

}

//modify by wuwei --相同的costinfo没有合并
void createCostInfoTag_and_set_property( tag_t item_tag , tag_t relation_tag , tag_t project_tag , char * bill_type , int year ,
                       int month , int minutes , tag_t user_tag ,  char * jci6_TaskType , tag_t jci6_Division , 
                       tag_t jci6_RateLevel , tag_t jci6_Section , char * jci6_Unit , double costNum)
{
	char costInfoName[256]              = {'\0'},
		project_id[PROJ_id_size_c + 1] ={'\0'},
         *division_name                 = NULL,
         *ratelevel_name                = NULL;

    tag_t cost_tag = NULLTAG,
         // project_tags[1],
         // costinfo_tags[1],
		  release_status = NULLTAG;

	ECHO("createCostInfoTag_and_set_property===start \n");
    ITKCALL( PROJ_ask_id( project_tag , project_id ) );
    if( jci6_Division != NULLTAG )
    {
         ITKCALL( AOM_ask_value_string( jci6_Division , "name" , &division_name ) );
    }
    if( jci6_RateLevel != NULLTAG )
    {
        ITKCALL( AOM_ask_value_string( jci6_RateLevel , "discipline_name" , &ratelevel_name ) );
    }
    char timestamp[13] = "";
  	time_t T;
    struct tm * timenow;
    time ( &T );
    timenow = localtime ( &T );
    char monthStr[3] = "",
         day[3]   = "",
         hour[3]  = "",
         min[3]   = "";
    if(timenow->tm_mon<9)
    {
        sprintf( monthStr, "%d%d",0,timenow->tm_mon+1);
    }
    else
    {
        sprintf( monthStr, "%d",timenow->tm_mon+1);
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
    sprintf( timestamp, "%d%s%s%s%s",1900+timenow->tm_year,monthStr,day,hour,min);


    if(division_name == NULL && ratelevel_name == NULL)
    {
         sprintf( costInfoName, "%s%s%s%s%d%s%s%s%s",project_id,"_Actual_",bill_type,"_",year ,"_",jci6_Unit,"_",timestamp);
    }
    else if(division_name != NULL && ratelevel_name != NULL)
    {
        sprintf( costInfoName, "%s%s%s%s%d%s%s%s%s%s%s%s%s",project_id,"_Actual_",bill_type,"_",year ,"_",division_name,"_",ratelevel_name,"_",jci6_Unit,"_",timestamp);
    }
    else if(division_name == NULL && ratelevel_name != NULL)
    {
        sprintf( costInfoName, "%s%s%s%s%d%s%s%s%s%s%s",project_id,"_Actual_",bill_type,"_",year ,"_",ratelevel_name,"_",jci6_Unit,"_",timestamp);
    }
    else if(division_name != NULL && ratelevel_name == NULL)
    {
        sprintf(costInfoName, "%s%s%s%s%d%s%s%s%s%s%s",project_id,"_Actual_",bill_type,"_",year ,"_",division_name,"_",jci6_Unit,"_",timestamp);
    }
	tag_t m_relationTag=NULLTAG;
	int hasSameTag=0;
	/*int hascnt=0;
	tag_t *secondList=NULL;
	ITKCALL( GRM_list_secondary_objects_only(item_tag,relation_tag,&hascnt,&secondList));
	for(int a=0;a<hascnt;a++){
		char *cost_name_1=NULL;
		ITKCALL(AOM_ask_value_string(secondList[a],"object_name",&cost_name******JCI6TimesheetMinAlert start************************
_1));
		ECHO("relation  cost_name_1===%s\n",cost_name_1);
		if(tc_strcasecmp(cost_name_1,costInfoName)==0){
			cost_tag=secondList[a];
			hasSameTag++;
			
			SAFE_SM_FREE(cost_name_1);
			break;
		}
		SAFE_SM_FREE(cost_name_1);
	}
	SAFE_SM_FREE(secondList);*/
	 ECHO("getNonBudgetCostinfo \n");
	 getNonBudgetCostinfo(costInfoName,&hasSameTag);
	 ECHO("getNonBudgetCostinfo over\n");
	 ECHO("createCostInfo \n");
	//new by wuwei
	if(hasSameTag==0){
		ECHO("hasSameTag and createCostInfo process \n");
		createCostInfo( costInfoName , &cost_tag );
		ECHO("hasSameTag and  createCostInfo process done \n");
	}
	ECHO("createCostInfo done and ready to set property\n");

	ECHO("setMonthInfo  done and ready to create relation start \n" );
	ITKCALL(GRM_find_relation(item_tag,cost_tag,relation_tag,&m_relationTag));
	if(m_relationTag==NULLTAG){
		POM_AM__set_application_bypass( TRUE ) ;
		ECHO("ITEM_attach_object_tag   start \n" );

		//ITKCALL( AOM_lock( item_tag ) );
		
		tag_t relationTag=NULLTAG;
		ITKCALL(GRM_create_relation(item_tag,cost_tag,relation_tag,NULLTAG,&relationTag));
		ITKCALL(GRM_save_relation(relationTag));
		
		//ITKCALL( ITEM_attach_object_tag( item_tag , cost_tag , relation_tag ) );
		//ITKCALL( AOM_save( item_tag ) );
		//ITKCALL( AOM_unlock( item_tag ) );

		ECHO("ITEM_attach_object_tag  finish \n" );
		POM_AM__set_application_bypass( FALSE ) ;
	}
	ECHO("create relation over and ready to create status \n" );


	ITKCALL(AOM_lock ( cost_tag ) );
    if( tc_strcmp( bill_type , UNASSIGNED ) != 0 && tc_strcmp( bill_type , UNASSIGNED ) != 0 )
    {
        ITKCALL( AOM_set_value_string( cost_tag , JCI6_COSTTYPE , bill_type ) );//bill_type
    }
	ITKCALL( AOM_set_value_int( cost_tag , JCI6_YEAR , year ) );
	ITKCALL( AOM_set_value_tag( cost_tag , JCI6_USER , user_tag ) );
    if(tc_strcmp( "" , jci6_TaskType ) != 0)
    {
        ITKCALL( AOM_set_value_string( cost_tag , JCI6_TASKTYPE , jci6_TaskType) );
    }
    else
    {
        ITKCALL( AOM_set_value_string( cost_tag , JCI6_TASKTYPE , "" ) );
    }
    ITKCALL( AOM_set_value_tag ( cost_tag , JCI6_DIVISION , jci6_Division ) );//部门
    ITKCALL( AOM_set_value_tag ( cost_tag , JCI6_RATELEVEL , jci6_RateLevel ) );//postion
    ITKCALL( AOM_set_value_tag ( cost_tag , JCI6_SECTION , jci6_Section ) );//科室
	ITKCALL( AOM_set_value_string(cost_tag , JCI6_UNIT , jci6_Unit ) );
    ITKCALL( AOM_set_value_string(cost_tag , JCI6_CPT , "Actual" ) );
	ECHO("set property done and ready to setMonthInfo \n" );

	if(hasSameTag==0)
		setMonthInfo( cost_tag , month , minutes , jci6_Unit , costNum , "create" );
	else
		setMonthInfo( cost_tag , month , minutes , jci6_Unit , costNum , "add" );
	ITKCALL( AOM_save ( cost_tag ) );
	ITKCALL( AOM_unlock ( cost_tag ) );
    //ITKCALL( AOM_refresh( cost_tag , true ) );
	

	ITKCALL( CR_create_release_status( RELEASEDSTATUS, &release_status ) );
	ITKCALL( AOM_save( release_status ) );
	ITKCALL( AOM_unlock( release_status ) );
	ITKCALL( EPM_add_release_status( release_status, 1, &cost_tag, NULL ) );

    // assign cost info to project 
    /*if( project_tag !=  NULLTAG )
    {
	    project_tags[0] = project_tag;

	    costinfo_tags[0] = cost_tag;

		 POM_AM__set_application_bypass( TRUE ) ;

	    ITKCALL( PROJ_assign_objects( 1 , project_tags , 1 , costinfo_tags ));

		 POM_AM__set_application_bypass( FALSE );
    }*/
	ECHO("create relation over and ready to create status done \n" );
	ECHO("createCostInfoTag_and_set_property===end \n");

}


void setMonthInfo( tag_t cost_tag , int finish_month , int minutes , char* jci6_Unit , double costNum , char* act )
{
	double hours = getManHours( minutes );

	double manMonth = getManMonthByHours( hours );

	double yuan = getYuanByHours( hours , costNum );

	if( tc_strcmp( "Hour" , jci6_Unit ) == 0 )
	{
		switch ( finish_month )
		{
			case 1:
                setMonthInfoByAtion( cost_tag , JCI6_JAN, hours , act );
				break;
			case 2:
                setMonthInfoByAtion( cost_tag , JCI6_FEB, hours , act );
				break;
			case 3:
                setMonthInfoByAtion( cost_tag , JCI6_MAR, hours , act );
				break;
			case 4:
                setMonthInfoByAtion( cost_tag , JCI6_APR, hours , act );
				break;
			case 5:
                setMonthInfoByAtion( cost_tag , JCI6_MAY, hours , act );
				break;
			case 6:
                setMonthInfoByAtion( cost_tag , JCI6_JUN, hours , act );
				break;
			case 7:
                setMonthInfoByAtion( cost_tag , JCI6_JUL, hours , act );
				break;
			case 8:
                setMonthInfoByAtion( cost_tag , JCI6_AUG, hours , act );
				break;
			case 9:
                setMonthInfoByAtion( cost_tag , JCI6_SEP, hours , act );
				break;
			case 10:
                setMonthInfoByAtion( cost_tag , JCI6_OCT, hours , act );
				break;
			case 11:
                setMonthInfoByAtion( cost_tag , JCI6_NOV, hours , act );
				break;
			case 12:
                setMonthInfoByAtion( cost_tag , JCI6_DEC, hours , act );
				break;
			default:
				break;
		}
	}
	else if( tc_strcmp( "ManMonth" , jci6_Unit ) == 0 )
	{
		switch ( finish_month )
		{
			case 1:
                setMonthInfoByAtion( cost_tag , JCI6_JAN, manMonth , act );
				break;
			case 2:
                setMonthInfoByAtion( cost_tag , JCI6_FEB, manMonth , act );
				break;
			case 3:
                setMonthInfoByAtion( cost_tag , JCI6_MAR, manMonth , act );
				break;
			case 4:
                setMonthInfoByAtion( cost_tag , JCI6_APR, manMonth , act );
				break;
			case 5:
                setMonthInfoByAtion( cost_tag , JCI6_MAY, manMonth , act );
				break;
			case 6:
                setMonthInfoByAtion( cost_tag , JCI6_JUN, manMonth , act );
				break;
			case 7:
                setMonthInfoByAtion( cost_tag , JCI6_JUL, manMonth , act );
				break;
			case 8:
                setMonthInfoByAtion( cost_tag , JCI6_AUG, manMonth , act );
				break;
			case 9:
                setMonthInfoByAtion( cost_tag , JCI6_SEP, manMonth , act );
				break;
			case 10:
                setMonthInfoByAtion( cost_tag , JCI6_OCT, manMonth , act );
				break;
			case 11:
                setMonthInfoByAtion( cost_tag , JCI6_NOV, manMonth , act );
				break;
			case 12:
                setMonthInfoByAtion( cost_tag , JCI6_DEC, manMonth , act );
				break;
			default:
				break;
		}
	}
	else if( tc_strcmp( "Yuan" , jci6_Unit ) == 0 )
	{
		switch ( finish_month )
		{
			case 1:
                setMonthInfoByAtion( cost_tag , JCI6_JAN, yuan , act );
				break;
			case 2:
                setMonthInfoByAtion( cost_tag , JCI6_FEB, yuan , act );
				break;
			case 3:
                setMonthInfoByAtion( cost_tag , JCI6_MAR, yuan , act );
				break;
			case 4:
                setMonthInfoByAtion( cost_tag , JCI6_APR, yuan , act );
				break;
			case 5:
                setMonthInfoByAtion( cost_tag , JCI6_MAY, yuan , act );
				break;
			case 6:
                setMonthInfoByAtion( cost_tag , JCI6_JUN, yuan , act );
				break;
			case 7:
                setMonthInfoByAtion( cost_tag , JCI6_JUL, yuan , act );
				break;
			case 8:
                setMonthInfoByAtion( cost_tag , JCI6_AUG, yuan , act );
				break;
			case 9:
                setMonthInfoByAtion( cost_tag , JCI6_SEP, yuan , act );
				break;
			case 10:
                setMonthInfoByAtion( cost_tag , JCI6_OCT, yuan , act );
				break;
			case 11:
                setMonthInfoByAtion( cost_tag , JCI6_NOV, yuan , act );
				break;
			case 12:
                setMonthInfoByAtion( cost_tag , JCI6_DEC, yuan , act );
				break;
			default:
				break;
		}
	}
	
}

/**
 * 设置cost_tag 的月份值
 * @param cost_tag
 * @param prop_name
 * @param costVaule
 * @param act
 * @return
 */
void setMonthInfoByAtion( tag_t cost_tag , char* prop_name, double costVaule , char* act)
{
    double jci6_costVaule;

    if( tc_strcmp( "create" , act ) == 0 )
    {
        ITKCALL( AOM_set_value_double( cost_tag , prop_name , costVaule ) );
    }
    else if( tc_strcmp( "add" , act ) == 0 )
    {
        ECHO("setMonthInfoByAtion    add");
        ITKCALL( AOM_ask_value_double( cost_tag , prop_name , &jci6_costVaule ) );
		POM_AM__set_application_bypass( TRUE ) ;
	    ITKCALL( AOM_set_value_double( cost_tag , prop_name ,  jci6_costVaule + costVaule) );
        ITKCALL( AOM_save ( cost_tag ) );
		POM_AM__set_application_bypass( FALSE ) ;
    }
    else if( tc_strcmp( "remove" ,act) == 0 )
    {
        ITKCALL( AOM_ask_value_double( cost_tag  ,prop_name , &jci6_costVaule ) );

	    ECHO("jci6_costVaule==%lf\n",jci6_costVaule);
        ECHO("costVaule==%lf\n",costVaule);
        ECHO("prop_name==%s\n",prop_name);

        if(jci6_costVaule==costVaule)
        {
            ECHO("same\n");
            POM_AM__set_application_bypass( TRUE ) ;
            ITKCALL( AOM_set_value_double( cost_tag , prop_name  ,0 ) );
            ITKCALL( AOM_save ( cost_tag ) );
           POM_AM__set_application_bypass( FALSE ) ;
        }else
        {
            ECHO("unsame\n");
           POM_AM__set_application_bypass( TRUE ) ;
            ECHO("(jci6_costVaule - costVaule)==%lf\n",(jci6_costVaule - costVaule));
            if( (jci6_costVaule - costVaule) < 0.000001 )
            {
                ITKCALL( AOM_set_value_double( cost_tag , prop_name  ,0 ) );
            }
            else
            {
                if(jci6_costVaule==0)
                {
                    ITKCALL( AOM_set_value_double( cost_tag , prop_name  ,0 ) );
                }
                else
                {
                    ECHO("remove\n");
                    ITKCALL( AOM_set_value_double( cost_tag , prop_name  ,jci6_costVaule - costVaule ) );
                }   
            }
		
            ITKCALL( AOM_save ( cost_tag ) ); 
            POM_AM__set_application_bypass( FALSE ) ;
        }
    }
}

/**
 * 根据工时得到小时
 * @param minutes
 * @return
 */
double getManHours( double minutes ) 
{
	return minutes/60;
}

/**
 * 根据工时得到人月
 * @param hours
 * @return
 */
double getManMonthByHours( double hours ) 
{
	return hours/170;
}

/**
 * 根据工时得到元
 * @param hours
 * @param costNum
 * @return
 */
long double getYuanByHours( double hours , double costNum )
{
	return hours*costNum;
}

/**
 * 根据用户ID得到用户Postion学科
 * @param user_id
 * @param postion
 * @return
 */
void getUserPostion( char * user_id ,tag_t* postion )
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

    ITKCALL( QRY_find( query_name , &query_tag ) );

    if( query_tag == NULLTAG )
	{
        TC_write_syslog( "没有找到查询构建器 %s\n",query_name);
		return ;
	}

	ITKCALL(PREF_ask_char_value("YF_Ratelevel", 0, &postions));

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


/******************************************************************************
* 得到学科对应的费率
* @param rateLevel
******************************************************************************/
double getCostNumOfPosition(tag_t rateLevel,tag_t user)
{
    double costNum = 0;
	// 得到用户所在孤本学科对应的费率
	if(rateLevel)
    {
        char* postion_name = NULL;
        ITKCALL( AOM_ask_value_string( rateLevel , OBJECT_NAME , &postion_name ) );
        if(tc_strcmp(postion_name,"ExtSupporter")==0)
        {
            if(user)
            {
                // Modify by zhanggl MAY-5-2013
			    tag_t CostValue          = NULLTAG,
				      relation_type      = NULLTAG,
				      *secondary_objects = NULL;
			    int sec_cnt = 0;

			    ITKCALL( GRM_find_relation_type( "CostValue_Relation", &relation_type ) );

			    if( relation_type )
			    {
				    ITKCALL( GRM_list_secondary_objects_only( user, relation_type, &sec_cnt, &secondary_objects) );
				    if( sec_cnt )
				    {
					    CostValue = secondary_objects[0];
					    char* costNumStr = NULL;

					    ITKCALL(AOM_ask_value_string(CostValue,"cost",&costNumStr));

					    costNum = strtod( costNumStr, NULL );

					    DOFREE(costNumStr);
					    DOFREE(secondary_objects);
				    }
			    }
            }
            else
            {
                ITKCALL(AOM_ask_value_double(rateLevel,DEFAULT_RATE,&costNum));
            }
        }
        else
        {
            ITKCALL(AOM_ask_value_double(rateLevel,DEFAULT_RATE,&costNum));
        }
        DOFREE(postion_name);
    }
    return costNum;
}


/**
	设置费用信息属性
	char * contentold <I> a line of Excel content 
	char * jci6_CPT <I>  jci6_CPT  prop value
	tag_t boTag  <I>  costInfo tag
*/
int setCostInfoProp(char * contentold,char * jci6_CPT,char * jci6_Unit,tag_t boTag, logical isRev)
{
	tag_t itemType     =NULLTAG,
		createInputTag   =NULLTAG,
		user_tag				 =NULLTAG,
		group_tag        =NULLTAG,
		discipline_tag   =NULLTAG;
		
	char *object_name  =NULL,
		*jci6_CostType   =NULL,
		*jci6_TaskType   =NULL,
		*jci6_Division   =NULL,
		*jci6_Section    =NULL,
		*jci6_RateLevel  =NULL,
		
		*jci6_User      =NULL,
		*jci6_Year      =NULL,
		*jci6_Jan       =NULL,
		*jci6_Feb       =NULL,
		*jci6_Mar       =NULL,
		*jci6_Apr       =NULL,
		*jci6_May       =NULL,
		*jci6_Jun       =NULL,
		
		*jci6_Jul       =NULL,
		*jci6_Aug       =NULL,
		*jci6_Sep       =NULL,
		*jci6_Oct       =NULL,
		*jci6_Nov       =NULL,
		*jci6_Dec       =NULL,
		*userNum        =NULL;
	double rate=0.00;	
	bool isManMonth=false,
		 isRate=false;
	char * content=(char *)MEM_alloc((tc_strlen(contentold)+10)*sizeof(char));
		tc_strcpy(content,contentold);

	if(tc_strcmp(jci6_Unit,JCI6_UNITMANMONTH)==0)
	{
		isManMonth=true;
	}
	if(tc_strcmp(jci6_Unit,JCI6_UNITYUAN)==0)
	{
		isRate=true;
	}

	jci6_Division=tc_strtok(content,VERTICALBAR);
	jci6_RateLevel=tc_strtok(NULL,VERTICALBAR);
	if(tc_strcmp(jci6_CPT,"Budget")!=0)
	{ //不是预算才有这2列
		jci6_User  =tc_strtok(NULL,VERTICALBAR);
		userNum    =tc_strtok(NULL,VERTICALBAR);  //工号
	}
	jci6_Year    =tc_strtok(NULL,VERTICALBAR);
	jci6_Jan     =tc_strtok(NULL,VERTICALBAR);
	jci6_Feb     =tc_strtok(NULL,VERTICALBAR);
	jci6_Mar     =tc_strtok(NULL,VERTICALBAR);
	jci6_Apr     =tc_strtok(NULL,VERTICALBAR);
	jci6_May     =tc_strtok(NULL,VERTICALBAR);
	jci6_Jun     =tc_strtok(NULL,VERTICALBAR);
	jci6_Jul     =tc_strtok(NULL,VERTICALBAR);
	
	jci6_Aug     =tc_strtok(NULL,VERTICALBAR);
	jci6_Sep     =tc_strtok(NULL,VERTICALBAR);
	jci6_Oct     =tc_strtok(NULL,VERTICALBAR);
	jci6_Nov     =tc_strtok(NULL,VERTICALBAR);
	jci6_Dec     =tc_strtok(NULL,VERTICALBAR);

	ITKCALL(AOM_lock ( boTag ));
	ITKCALL(AOM_set_value_string(boTag,JCI6_COSTTYPE,"Normal Hours"));
	ITKCALL(AOM_set_value_string(boTag,JCI6_CPT,jci6_CPT));
	ITKCALL(AOM_set_value_string(boTag,JCI6_UNIT,jci6_Unit));
	 if(!tc_strstr(jci6_Division,"NULL"))
	 {
		SA_find_group(jci6_Division,&group_tag);
		ECHO("jci6_Division==%s  group_tag==%d \n ",jci6_Division,group_tag);
		ITKCALL(AOM_set_value_tag(boTag,JCI6_DIVISION,group_tag));
	 }
     if(userNum && !tc_strstr(userNum,"NULL"))
	 {	getUserByUserNum(userNum,&user_tag);
		ITKCALL(AOM_set_value_tag(boTag,JCI6_USER,user_tag));
	 }
	if(!tc_strstr(jci6_RateLevel,"NULL"))
	{	
		 ITKCALL(SA_find_discipline(jci6_RateLevel, &discipline_tag));
		 ECHO("jci6_RateLevel==%s  discipline_tag==%d \n ",jci6_RateLevel,discipline_tag);
		 if(discipline_tag){
			 ITKCALL(AOM_set_value_tag(boTag,JCI6_RATELEVEL,discipline_tag));
			 //ITKCALL(AOM_ask_value_double(discipline_tag,DEFAULT_RATE,&rate));
             rate = getCostNumOfPosition(discipline_tag,user_tag);
		 }
	 }	
	
	if(!tc_strstr(jci6_Year,"NULL"))
		ITKCALL(AOM_set_value_int(boTag,JCI6_YEAR,atoi(jci6_Year)));
	if(!tc_strstr(jci6_Jan,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL( AOM_set_value_double( boTag, JCI6_JAN,strtod( jci6_Jan, NULL) ) );
			}
			else
			{
				DataPrecision( strtod( jci6_Jan, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JAN,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Jan,NULL)*rate*170.0, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JAN,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Jan,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JAN,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JAN,strtod(jci6_Jan,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JAN,strtod(jci6_Jan,NULL)));
			}
		}
	}
		
	if(!tc_strstr(jci6_Feb,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,strtod( jci6_Feb, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Feb, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Feb,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Feb,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,strtod(jci6_Feb,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_FEB,strtod(jci6_Feb,(char**)NULL)));
			}
		}
	}
		
	if(!tc_strstr(jci6_Mar,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,strtod( jci6_Mar, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Mar, (char**)NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Mar,(char**)NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Mar,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,strtod(jci6_Mar,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAR,strtod(jci6_Mar,NULL)));
			}
		}
	}
		
	if(!tc_strstr(jci6_Apr,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,strtod( jci6_Apr, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Apr, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Apr,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Apr,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,strtod(jci6_Apr,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_APR,strtod(jci6_Apr,(char**)NULL)));
			}
		}
	}
		
	if(!tc_strstr(jci6_May,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,strtod( jci6_May, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_May, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_May,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_May,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,strtod(jci6_May,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_MAY,strtod(jci6_May,(char**)NULL)));
			}
		}
	}	

	if(!tc_strstr(jci6_Jun,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,strtod( jci6_Jun, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Jun, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Jun,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Jun,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,strtod(jci6_Jun,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUN,strtod(jci6_Jun,(char**)NULL)));
			}
		}
	}		
	if(!tc_strstr(jci6_Jul,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,strtod( jci6_Jul, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Jul, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Jul,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Jul,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,strtod(jci6_Jul,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_JUL,strtod(jci6_Jul,(char**)NULL)));
			}
		}
	}	
	if(!tc_strstr(jci6_Aug,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,strtod( jci6_Aug, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Aug, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Aug,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Aug,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,strtod(jci6_Aug,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_AUG,strtod(jci6_Aug,(char**)NULL)));
			}
		}
	}	
	if(!tc_strstr(jci6_Sep,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,strtod( jci6_Sep, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Sep, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Sep,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Sep,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,strtod(jci6_Sep,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_SEP,strtod(jci6_Sep,(char**)NULL)));
			}
		}
	}	
	if(!tc_strstr(jci6_Oct,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,strtod( jci6_Oct, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Oct, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Oct,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Oct,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,strtod(jci6_Oct,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_OCT,strtod(jci6_Oct,(char**)NULL)));
			}
		}
	}
		
	if(!tc_strstr(jci6_Nov,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,strtod( jci6_Nov, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Nov, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Nov,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Nov,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,strtod(jci6_Nov,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_NOV,strtod(jci6_Nov,(char**)NULL)));
			}
		}
	}	
	if(!tc_strstr(jci6_Dec,"NULL"))
	{	
		if(isManMonth)
		{	
			long double manMonth=0.0;
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,strtod( jci6_Dec, NULL)));
			}
			else
			{
				DataPrecision( strtod( jci6_Dec, NULL)/170.0, &manMonth);
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,manMonth));
			}
		}
		else if(isRate)
		{
			long double rateTemp=0.0;
			if(isRev)
			{
				DataPrecision(strtod(jci6_Dec,NULL)*rate*170, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,rateTemp));
			}
			else
			{
				DataPrecision(strtod(jci6_Dec,NULL)*rate, &rateTemp);
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,rateTemp));
			}
		}
		else
		{
			if(isRev)
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,strtod(jci6_Dec,(char**)NULL)*170.0));
			}
			else
			{
				ITKCALL(AOM_set_value_double(boTag,JCI6_DEC,strtod(jci6_Dec,(char**)NULL)));
			}
		}
	}		
	ITKCALL(AOM_save ( boTag ));
	ITKCALL(AOM_unlock ( boTag ));
	MEM_free(content);
	return 0;
}

/**
	创建费用信息
	char * object_name <I>  costInfo Name
	tag_t * obj_tag  <O> costInfo tag 
*/

int createCostInfo(char * object_name,tag_t * obj_tag)
{
	tag_t boTag=NULLTAG,
		createInputTag=NULLTAG,
		itemType=NULLTAG;
	const char ** teststr = (const char **)MEM_alloc(1*sizeof(char*));
	teststr[0] = object_name;
	
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

	TCTYPE_find_type(JCI6_COSTINFO,JCI6_COSTINFO,&itemType);
	TCTYPE_construct_create_input ( itemType, &createInputTag );
	TCTYPE_set_create_display_value( createInputTag, "object_name", 1, teststr );
	TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, teststr );	

	TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str );
	TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr );	
	TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str );


	ITKCALL(TCTYPE_create_object ( createInputTag, &boTag) );
	ITKCALL(AOM_save ( boTag ));
	ITKCALL(AOM_unlock ( boTag ));//remember unlock
	*obj_tag = boTag;
	SAFE_SM_FREE(teststr);
	SAFE_SM_FREE(unitstr);
	SAFE_SM_FREE(ucpt_str);
	SAFE_SM_FREE(year_str);
	return 0;
}

/**
	创建费用信息并指派到项目
	char * object_name <I>  costInfo Name
	tag_t * obj_tag  <O> costInfo tag 
    tag_t project_tag  <O> project tag 
*/

int createCostInfoAndAssignToProj(char * object_name,tag_t * obj_tag,tag_t project_tag)
{
	tag_t boTag=NULLTAG,
		createInputTag=NULLTAG,
		itemType=NULLTAG;
	const char ** teststr = (const char **)MEM_alloc(1*sizeof(char*));
	teststr[0] = object_name;

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

	TCTYPE_find_type(JCI6_COSTINFO,JCI6_COSTINFO,&itemType);
	TCTYPE_construct_create_input ( itemType, &createInputTag );
	TCTYPE_set_create_display_value( createInputTag, "object_name", 1, teststr );
	TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, teststr );	

	TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str );
	TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr );	
	TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str );

	ITKCALL(TCTYPE_create_object ( createInputTag, &boTag) );
	ITKCALL(AOM_save ( boTag ));
	ITKCALL(AOM_unlock ( boTag ));//remember unlock
    if(project_tag)
    {
        //ITKCALL(AOM_refresh ( project_tag, true ));
        //ITKCALL(AOM_refresh ( boTag, true ));
        int result = 0;
        ITKCALL(result = PROJ_assign_objects(1,&project_tag,1,&boTag));
        printf("1020 result===%d",result);
        //ITKCALL(AOM_save ( boTag));
        //ITKCALL(AOM_save ( project_tag ));
        //ITKCALL(AOM_refresh ( boTag, false ));
	 //ITKCALL(AOM_refresh ( project_tag, false ));
    }
	*obj_tag = boTag;
    
    
	SAFE_SM_FREE(teststr);
	SAFE_SM_FREE(unitstr);
	SAFE_SM_FREE(ucpt_str);
	SAFE_SM_FREE(year_str);
	return 0;
}

/**
**getUserByUserNUM:: 根据用户工号查找用户对象
**/
int getUserByUserNum(char* userNum,tag_t* usertag){
	tag_t query_tag	  = NULLTAG,
	      user        = NULLTAG,
	      *qresult	  = NULL ;
	char **qkey		=NULL,
	     **qvalue		=NULL,
	     **entries		=NULL,
	     **values		=NULL;
	int  i  		=0,
	     icount		=0,
	     num_found		=0;

	qvalue=(char **) MEM_alloc(1* sizeof(char*));
	qvalue[0] = (char *)MEM_alloc((tc_strlen(userNum) + 1)* sizeof(char));
	tc_strcpy(qvalue[0],userNum);

	ITKCALL(QRY_find("YFJC_SearchUserByUserNum",&query_tag));	
	if(query_tag != 0){
		ITKCALL(QRY_find_user_entries(query_tag,&icount,&entries,&values));
		ITKCALL(QRY_execute(query_tag,1,entries,qvalue,&num_found,&qresult));
		MEM_free(entries);
		MEM_free(values);
	}
	if(num_found != 0){
		user = qresult[0];
	}
	*usertag = user;
	MEM_free(qvalue[0]);
	MEM_free(qvalue);
	if(qresult)
		MEM_free(qresult);
	return ITK_ok;
}

/*
 解析字符串 ，返回日期
   char *curPath            <I>  date string (format "%y/%m/%d")
     date_t *the_dt         <O> The date
*/
int validate_date ( char *date , date_t *the_dt )
{
	int      retcode    = ITK_ok;     /* Function return code */
	date_t   dt         = NULLDATE;   /* Date structure */
	int      month      = 0;          /* Month */
	int      day        = 0;          /* Day */
	int      year       = 0;          /* Year */
	int      hour       = 0;          /* Hour */ 
	int      minute     = 0;          /* Minutes */
	int      second     = 0;          /* Seconds */

	/* Converts a date_t structure into the format specified  */  //"%d-%b-%Y %H:%M:%S"
	retcode = DATE_string_to_date ( date , "%y/%m/%d" , &month , &day ,
		&year , &hour , &minute , &second);
	if ( retcode != ITK_ok )
	{
		ECHO("retcode==%d \n",retcode);;
	}
	else
	{
		dt.month = month;
		dt.day   = day;
		dt.year  = year;
		dt.hour  = hour;
		dt.minute= minute;
		dt.second= second;
		*the_dt = dt;
	}

	return retcode;
}

//2016-05-04	mengyawei added

string create(char * content)
{
	int ifail    = ITK_ok,
        num_of_members = 0;

	char project_id[32]     = "",
		 project_name[128]  = "",
		 project_desc[128]  = "",
		 jci6_EPIC_PN[128]  = "",
		 jci6_state[12]     = "",
		 division[128]      = "",
		 section[128]       = "",
		 *point             = NULL,
		 *valtype_n         = NULL,
		 equal_left[32]     = "",
		 equal_right[128]   = "";

	tag_t project_tag           = NULLTAG,
		  prop_tag              = NULLTAG,
		  programInfo_tag       = NULLTAG,
		  programInfo_rev_tag   = NULLTAG,
		  user_tag              = NULLTAG,
		  relation_type         = NULLTAG,
		  *secondary_objects    = NULL,
		  relation              = NULLTAG;
	
	date_t str_to_date   = NULLDATE;

	PROP_value_type_t valtype;

	//2016-05-04	mengyawei modify
	char division_c[256] = {'\0'};
	
	
	char role_name[SA_person_name_size_c + 1]  = "";
	string logstr	=	"";
	logical has_team_leader = FALSE;
	tag_t *member_tags    = NULL,
		  project_team    = NULLTAG,
		  role_tag        = NULLTAG,
		  admin_role_tag  = NULLTAG,
		  team_user_tag   = NULLTAG,
		  admin_member    = NULLTAG;
	tag_t	default_group_tag = NULLTAG;
  tag_t del_user_tag    = NULLTAG;

  ECHO("create project  start:%s\n",getNowtime2().c_str());

	char *content_copy = (char *)MEM_alloc( (tc_strlen(content)+1)*sizeof(char));
	tc_strcpy( content_copy, content );
	point = tc_strtok( content, VERTICALBAR );

	while( point )
	{
		tc_strcpy( equal_right, "");
		sscanf( point, "%[^=]=%[^=]", equal_left, equal_right);
		if( tc_strcmp( equal_left, PROJECT_ID ) == 0 )
		{
			tc_strcpy( project_id, equal_right );
		}
		else if( tc_strcmp( equal_left, OBJECT_NAME ) == 0 )
		{
			tc_strcpy( project_name, equal_right );
		}
		else if( tc_strcmp( equal_left, OBJECT_DESC ) == 0 )
		{
			tc_strcpy( project_desc, equal_right );
		}
		else if( tc_strcmp( equal_left, JCI6_EPIC_PN ) == 0 )
		{
			tc_strcpy( jci6_EPIC_PN, equal_right );
		}
		else if( tc_strcmp( equal_left, IS_ACTIVE ) == 0 )
		{
			tc_strcpy( jci6_state, equal_right );
		}
		else if( tc_strcmp( equal_left, "Division" ) == 0 )
		{
			tc_strcpy( division, equal_right );
		}
		else if( tc_strcmp( equal_left, "Section" ) == 0 )
		{
			tc_strcpy( section, equal_right );
		}

		point = tc_strtok( NULL, VERTICALBAR );
	}
	//ECHO("project_id = %s; \t project_name = %s\n", project_id, project_name);
	ECHO("project_id = %s; \t project_name = %s  time:%s\n", project_id, project_name,getNowtime2().c_str());
      if(get_opt_debug() )
      {
            logstr.append(" project_id = ");logstr.append(project_id);
            logstr.append(" project_name = ");logstr.append(project_name);
      }
	if( tc_strcmp( project_id, "" ) == 0 || tc_strcmp( project_name, "" ) == 0 )
	{
		goto FOUT;
	}
	if(tc_strstr( project_name, "." )  || tc_strlen(project_name)>32)
	{
		  logstr.append(" project name ");logstr.append(project_name); logstr.append("  is not right");
		goto FOUT;
	}

   POM_AM__set_application_bypass( TRUE ) ;
	ITKCALL( PROJ_find( project_id, &project_tag ) );
	if( !project_tag )
	{
		ITKCALL( PROJ_create_project( project_id, project_name, project_desc, &project_tag ) );
		if(project_tag==NULLTAG)
		{		if(get_opt_debug())		
				    logstr.append(" create project failed");	
				goto FOUT;
		}
        ITKCALL( AOM_lock ( project_tag ) );
        ITKCALL( AOM_set_value_logical( project_tag, "use_program_security", true ) );
		ITKCALL( AOM_save ( project_tag ) );
		ITKCALL( AOM_unlock ( project_tag ) );
	}
	else
	{
		ITKCALL( AOM_lock ( project_tag ) );
        ITKCALL( AOM_set_value_string( project_tag, OBJECT_NAME, project_name ) );
		ITKCALL( AOM_set_value_string( project_tag, OBJECT_DESC, project_desc ) );
        ITKCALL( AOM_set_value_logical( project_tag, "use_program_security", true ) );
		ITKCALL( AOM_save ( project_tag ) );
		ITKCALL( AOM_unlock ( project_tag ) );
	}

    ITKCALL( ITEM_find_item( project_id, &programInfo_tag ) );
	if( !programInfo_tag )
	{
		ITKCALL( ITEM_create_item( project_id, project_name, JCI6_PROGRAMINFO, JCI6_REVSEQINIT, &programInfo_tag, &programInfo_rev_tag ) );
	}
	else
	{
		ITKCALL( ITEM_ask_latest_rev( programInfo_tag, &programInfo_rev_tag ) );
		ITKCALL( AOM_lock ( programInfo_tag ) );
		ITKCALL( AOM_lock ( programInfo_rev_tag ) );
        ITKCALL( AOM_set_value_string( programInfo_tag, OBJECT_NAME, project_name ) );
        ITKCALL( AOM_set_value_string( programInfo_rev_tag, OBJECT_NAME, project_name ) );
	}

	ITKCALL( AOM_set_value_string( programInfo_tag, JCI6_EPIC_PN, jci6_EPIC_PN ) );
   
	ITKCALL( AOM_set_value_double( programInfo_rev_tag, JCI6_EQU, 0.00 ) );
	
	ECHO("---------------------------------set property start:%s\n",getNowtime2().c_str());
    if(get_opt_debug() )  
	    logstr.append(" set property start::: 	");

	point = tc_strtok( content_copy, VERTICALBAR );
	while( point )
	{
		tc_strcpy( equal_right, "");
		sscanf( point, "%[^=]=%[^=]", equal_left, equal_right);
		//ECHO("equal_left = %s; \t equal_right = %s\n", equal_left, equal_right );
		
		if( tc_strcmp( equal_left, PROJECT_ID ) == 0 || tc_strcmp( equal_left, OBJECT_NAME ) == 0 || 
			tc_strcmp( equal_left, OBJECT_DESC ) == 0 || tc_strcmp( equal_left, JCI6_EPIC_PN ) == 0 ||
			tc_strcmp( equal_left, IS_ACTIVE ) == 0 )
		{
			point = tc_strtok( NULL, VERTICALBAR );
			continue;
		}
        if(get_opt_debug() ) 
        {
		    logstr.append(";	"); logstr.append(equal_left);logstr.append("===");logstr.append(equal_right);
        }
		ITKCALL( PROP_ask_property_by_name( programInfo_rev_tag, equal_left, &prop_tag ) );

		if( prop_tag )
		{
			if((strcmp(equal_left,"jci6_TargetPos")==0)&&(strcmp(equal_right,"")==0))
			{
				sprintf(equal_right,"%s","100");
			}
			ITKCALL( PROP_ask_value_type( prop_tag, &valtype, &valtype_n ));
			//ECHO("valtype = %d\n", valtype);
            	if( valtype == PROP_int )
			{
				ITKCALL( PROP_set_value_int( prop_tag, atoi(equal_right) ) );
			}

			if( valtype == PROP_string )
			{
				ITKCALL( PROP_set_value_string( prop_tag, equal_right ) );
			}
			else if( valtype == PROP_double )
			{
				ITKCALL( PROP_set_value_double( prop_tag, atof( equal_right ) ) );
			}
			else if( valtype == PROP_date )
			{	
				str_to_date=NULLDATE;
				validate_date( equal_right, &str_to_date );
				ITKCALL( PROP_set_value_date( prop_tag, str_to_date ) );
			}
			else if( valtype == PROP_typed_reference )
			{
				if( tc_strcmp( equal_left, JCI6_PROGRAMDIVI ) == 0 )
				{
					tc_strcpy( division_c , equal_right );
					tag_t group_tag = NULLTAG;
					ITKCALL( SA_find_group( equal_right, &group_tag ) );
					ITKCALL( PROP_set_value_tag( prop_tag, group_tag ) );
				}
				else if( tc_strcmp( equal_left, JCI6_PROGRAMSEC ) == 0 )
				{
					tag_t group_tag = NULLTAG;
					ITKCALL( SA_find_group( equal_right, &group_tag ) );
					ITKCALL( PROP_set_value_tag( prop_tag, group_tag ) );
				}
				else if( tc_strcmp( equal_left, JCI6_PROJECTTL ) == 0 )
				{
					ITKCALL( SA_find_user( equal_right, &user_tag) );
					ITKCALL( PROP_set_value_tag( prop_tag, user_tag ) );
				}
			}

			MEM_free( valtype_n );
			valtype_n = NULL;
		}
		point = tc_strtok( NULL, VERTICALBAR );
	}
	ECHO("-------------------set property---------End:%s\n",getNowtime2().c_str());
    if(get_opt_debug() ) 
    {
	    logstr.append("	set property End====== ");
    }
	ITKCALL( AOM_set_value_tag( programInfo_tag, OWNING_PROJECT, project_tag ) );
	if( ( ifail = AOM_save( programInfo_tag ) ) != ITK_ok )
	{
		ECHO("save item failed. error_id=%d  \n", ifail );
        if(get_opt_debug() ) 
        {
		    logstr.append("	save item failed===");
            logstr.append("	========row end ");
        }
		goto FOUT;
	}
	ITKCALL( AOM_unlock ( programInfo_tag ) );
	//ITKCALL( AOM_save ( programInfo_rev_tag ) );
	if( ( ifail = AOM_save( programInfo_rev_tag ) ) != ITK_ok )
	{	if(get_opt_debug() ) 
        {
		    logstr.append("	save item rev failed====");
        }
	}

	ITKCALL( AOM_unlock ( programInfo_rev_tag ) );

	ITKCALL( GRM_find_relation_type( TC_PROGRAM_PREFERRED_ITEMS, &relation_type ) );
	//ITKCALL( GRM_list_secondary_objects_only( project_tag, relation_type, &count, &secondary_objects ) );
	ITKCALL( GRM_find_relation( project_tag, programInfo_tag, relation_type, &relation ) );
	//ECHO("count==%d  \n",count);
	ITKCALL( AOM_lock ( project_tag ) );
	if( !relation )
	{
		ITKCALL( GRM_create_relation( project_tag, programInfo_tag, relation_type, NULLTAG, &relation ) );
	//	ITKCALL( AOM_save( relation ) );
		if( ( ifail = AOM_save( relation ) ) != ITK_ok )
		{	
            if(get_opt_debug() ) 
            {
			   		    logstr.append("	save relation  failed====");
            }
		 }

		ITKCALL( AOM_unlock( relation ));
		ITKCALL( PROJ_assign_objects( 1, &project_tag, 1, &programInfo_tag ) );
	}
	//MEM_free( secondary_objects );
	//secondary_objects = NULL;
	
	if( tc_strstr( jci6_state, "false" ) )
	{
		ITKCALL( PROJ_activate_project( project_tag, 0 ) );
	}
	else
	{
		ITKCALL( PROJ_activate_project( project_tag, 1 ) );
	}

	ITKCALL(POM_ask_user_default_group(user_tag,& default_group_tag));
	ITKCALL(SA_find_groupmembers(user_tag,default_group_tag,&num_of_members, &member_tags));
	if(num_of_members>0)
	{	tag_t tem_members[1] ;
		for(int i=0;i<num_of_members;i++)
		{
			logical not_active = TRUE;
			ITKCALL(AOM_ask_value_logical(member_tags[i],"status",&not_active));//groupmember是否处于活动状态 false=active
			if(not_active==FALSE)
			{	tem_members[0] =member_tags[i] ;
				ITKCALL( PROJ_add_members( project_tag, 1, tem_members) );
				break;
			}
		}
		
		MEM_free( member_tags );
		member_tags = NULLTAG;
	}else
	{
		ITKCALL( PROJ_add_members( project_tag, 1,&user_tag) );
	}

	//2016-05-04	mengyawei modify
	//add Cost Controller(ebp1) for Project
	//逻輯有变化需要重新了解逻辑
	
	{
		map< string , vector<string> > division_default_user_map;
		parse_division_default_preference(division_default_user_map);
		ECHO("2016-05-04	division_default_user_map.size() = %d\n" , division_default_user_map.size() );
		ECHO("2016-05-04	division_c = %s\n" , division_c );
		map< string , vector<string> >::iterator div_ite = division_default_user_map.find( division_c );
		if( div_ite != division_default_user_map.end() )
		{
			int id_cnt = div_ite->second.size();
			//ECHO("2016-05-04	id_cnt = %d\n" , id_cnt);
			int id_idx = 0;
			for( id_idx = 0; id_idx < id_cnt; id_idx ++ )
			{
				tag_t tmp_costing_user = NULLTAG;
				//ECHO("2016-05-04	div_ite->second[%d].c_str() = %s\n" , id_idx , div_ite->second[id_idx].c_str());
				ITKCALL( SA_find_user( div_ite->second[id_idx].c_str() , &tmp_costing_user ) );
				//ITKCALL( SA_find_user( CostControllerUserId , &tmp_costing_user ) );
				
				if( tmp_costing_user != NULLTAG )
				{
					ITKCALL(POM_ask_user_default_group(tmp_costing_user,& default_group_tag));
					ITKCALL(SA_find_groupmembers(tmp_costing_user,default_group_tag,&num_of_members, &member_tags));
					if(num_of_members>0)
					{	tag_t tem_members[1] ;
						for(int i=0;i<num_of_members;i++)
						{
							logical not_active = TRUE;
							ITKCALL(AOM_ask_value_logical(member_tags[i],"status",&not_active));//groupmember是否处于活动状态 false=active
							if(not_active==FALSE)
							{	tem_members[0] =member_tags[i] ;
								ITKCALL( PROJ_add_members( project_tag, 1, tem_members) );
								break;
							}
						}
		
						MEM_free( member_tags );
						member_tags = NULLTAG;
					}else
					{
						ITKCALL( PROJ_add_members( project_tag, 1,&tmp_costing_user) );
					}
				}
			}
			
		}else
		{
			ECHO("2016-05-04	preference=[YFJC_ImportProgramInfo_default_role] din't difine division=[%s] 's default userid\n" , division);
		}
	}
	
	//2016-05-04	mengyawei modify

	ITKCALL( AOM_ask_value_tag( project_tag, PROJECT_TEAM, &project_team ) );
	ECHO("project_team==%d time:%s\n",project_team,getNowtime2().c_str());
	if( project_team )
	{   
        int author_number       = 0;
        tag_t *  author_tags    =NULL;

        ITKCALL( AOM_ask_value_tags( project_team,"project_members", &num_of_members, &member_tags ) );
        ITKCALL(PROJ_ask_author_members( project_tag, &author_number, &author_tags ) );
		ECHO("author_number==%d \n",author_number);
		ECHO("num_of_members==%d \n",num_of_members);
		ITKCALL(PROJ_assign_team(project_tag,num_of_members,member_tags,user_tag,author_number,author_tags));
       
		MEM_free( member_tags );
		member_tags = NULLTAG;
        MEM_free( author_tags );
		author_tags = NULLTAG;
	}
    if( ( ifail = AOM_save( project_tag ) ) != ITK_ok )
	{	if(get_opt_debug() ) 
        {
		    logstr.append("	save project  failed====");
         }
	 }	


	ITKCALL( AOM_unlock ( project_tag ) );

FOUT:
	MEM_free( content_copy );
	content_copy = NULL;
    if(get_opt_debug() ) 
	    logstr.append("	========row end ");
    POM_AM__set_application_bypass( FALSE ) ;

	ECHO("create project--------------------------FEND:%s\n",getNowtime2().c_str());

	return logstr;
}

//2016-05-04	mengyawei added
int parse_division_default_preference(map< string , vector<string> > &division_default_user_map)
{
	int ifail = ITK_ok;
	int i , j ;
	int pref_cnt = 0;
	char ** pref_value = NULL;
	char division_name[256] = {'\0'};
	char user_ids[256] = {'\0'};
	int id_cnt = 0;
	char ** ids = NULL;
	
	ECHO("---------parse_division_default_preference started---------%s\n",getNowtime2().c_str());
	
	ITKCALL( PREF_ask_char_values( "YFJC_ImportProgramInfo_default_role" , &pref_cnt , &pref_value ));
	for( i = 0; i < pref_cnt ; i ++ )
	{
		//ECHO("	pref_value[%d] = %s\n" , i , pref_value[i]);
		sscanf( pref_value[i] , "%[^=]=%[^=]" , division_name , user_ids );
		//ECHO( "division_name = %s users_ids = %s\n" , division_name , user_ids );
		string divname;
		divname.assign( division_name );
		ITKCALL( EPM__parse_string ( user_ids , ","  , &id_cnt , &ids) );
		if( ids != NULL )
		{
			vector<string> id_vec ;
			for( j = 0; j < id_cnt; j ++ )
			{
				if( tc_strlen( ids[j] ) > 0 )
				{
					string tmpid ;
					tmpid.assign( ids[j] );
					//ECHO("tmpid = %s\n" , ids[j]);
					id_vec.push_back( tmpid );
				}
			}
			division_default_user_map.insert( pair<string , vector<string> >( divname , id_vec ) );
		}
	}
	if( pref_value != NULL )
	{
		MEM_free( pref_value );
		pref_value = NULL;
	}
	
	ECHO("----------parse_division_default_preference ended----------%s\n",getNowtime2().c_str());
	return ifail;
}


/**
getUserByUserName:: 根据用户名查找用户对象
*/
int getUserByUserName(char* username,tag_t* usertag){
	tag_t query_tag	  = NULLTAG,
	      user        = NULLTAG,
	      *qresult	  = NULL ;
	char **qkey		=NULL,
	     **qvalue		=NULL,
	     **entries		=NULL,
	     **values		=NULL;
	int  i  		=0,
	     icount		=0,
	     num_found		=0;
	
	qkey= (char **) MEM_alloc(1* sizeof(char*));
	qvalue=(char **) MEM_alloc(1* sizeof(char*));
	
	qkey[0] = (char *)MEM_alloc(tc_strlen("userName") + 1);
	qvalue[0] = (char *)MEM_alloc(tc_strlen(username) + 1);

	tc_strcpy(qkey[0],"userName");
	tc_strcpy(qvalue[0],username);
	ITKCALL(QRY_find("YFJC_SearchUser",&query_tag));	

	//ECHO("qvalue[0] = %s\n",qvalue[0]);

	if(query_tag != 0){
		//ITKCALL(QRY_find_user_entries(query_tag,&icount,&entries,&values));
		ITKCALL(QRY_execute(query_tag,1,qkey,qvalue,&num_found,&qresult));
	}

	if(num_found != 0){
		user = qresult[0];
	}
	*usertag = user;
	if(qresult)
		MEM_free(qresult);
	MEM_free(qkey[0]);
	MEM_free(qvalue[0]);
	MEM_free(qkey);
	MEM_free(qvalue);
	return ITK_ok;
}

int do_auto_assign_PKR( tag_t task,char* task1,char* task2)
{
	int customError	    = ITK_ok,
        reference_count = 0;

	tag_t pRoot              = NULLTAG,
          *reference_objects = NULL;

	customError =  EPM_ask_root_task( task, &pRoot );

	ECHO("==do_auto_assign_PKR==task1:%s  task2:%s\n",task1,task2);

	if( customError == ITK_ok && pRoot != NULLTAG)
    {
        customError = EPM_ask_attachments( pRoot, EPM_reference_attachment, &reference_count, &reference_objects);
        if(reference_count==0)
        {
            HANDLE_ERROR( ERROR_PREFERENCE_INPROCESS_NOT_FOUND )
        }
        tag_t pefRev = NULLTAG;
        for(int i=0;i<reference_count;i++)
        {
			char object_type[WSO_name_size_c+1]={'\0'};
		    ITKCALL(WSOM_ask_object_type(reference_objects[i],object_type));
		    if(tc_strcmp(object_type,"ItemRevision")==0)//
            {
                pefRev = reference_objects[i];
                break;
		    }
        }
        if(reference_objects!=NULL)
	    {
            MEM_free(reference_objects);
            reference_objects = NULL;
        }
      
		ECHO("pefRev:%d\n",pefRev);
		if(pefRev!=NULLTAG)
        {
            int subcount           = 0,
                projectNum         = 0,
                option_value_count = 0;

            tag_t  *subtasks       = NULL,
                   confirmOverTask = NULLTAG,
                   signoffPFA      = NULLTAG,
                   *project_lists  = NULL,
                   currentGate     = NULLTAG;

            char *jci6_TaskType   = NULL,
                 *rightSet        = NULL,
                 ** option_values = NULL,
                 jci6_gateN_PEF[20] = "";

            ITKCALL(EPM_ask_sub_tasks(pRoot,&subcount,&subtasks));
            for(int m=0;m<subcount;m++)
            {
                char *task_name = NULL;
                ITKCALL(AOM_ask_value_string(subtasks[m],"object_name",&task_name));
                if(task1!=NULL&&tc_strcmp(task_name,task1)==0)
                {
                   confirmOverTask = subtasks[m];
                }
                else if(task2!=NULL&&tc_strcmp(task_name,task2)==0)
                {
                    signoffPFA = subtasks[m];
                }
                if(task_name!=NULL)
                {
                    MEM_free(task_name);
                    task_name = NULL;
                }
            }

			ECHO("confirmOverTask:%d  signoffPFA:%d\n",confirmOverTask,signoffPFA);
            if(subtasks!=NULL)
		    {
			   MEM_free(subtasks);
			   subtasks = NULL;
		    }

            ITKCALL(AOM_ask_value_tags(pefRev,"project_list",&projectNum,&project_lists));

			ECHO("projectNum:%d \n",projectNum);
            if(projectNum>0)
            {
                int preferred_ItemNum = 0;
                tag_t *preferred_Items = NULL;
                ITKCALL(AOM_ask_value_tags(project_lists[0],"TC_Program_Preferred_Items",&preferred_ItemNum,&preferred_Items));
                time_t T;
                struct tm * timenow;
                time ( &T );
                timenow = localtime ( &T );
                time_t currentDate = mktime (timenow);
                tag_t programInfoRev = NULLTAG,
                      jci6_ProjectTL = NULLTAG;

				ECHO("preferred_ItemNum:%d \n",preferred_ItemNum);
                for(int k=0;k<preferred_ItemNum;k++)
                {
                    char object_type[WSO_name_size_c+1] = "";
                    ITKCALL(WSOM_ask_object_type(preferred_Items[k],object_type));

					ECHO("object_type:%s \n",object_type);
                    if(tc_strcmp(object_type,"JCI6_ProgramInfo")==0)
                    {
                        ITEM_ask_latest_rev(preferred_Items[k],&programInfoRev);
                        ITKCALL(AOM_ask_value_tag(programInfoRev,"jci6_ProjectTL",&jci6_ProjectTL));
                        if(isDebug)
                             TC_write_syslog("jci6_ProjectTL值为：%u\n",jci6_ProjectTL);
                    }
                    else if(tc_strcmp(object_type,"Schedule")==0)
                    {
                        tag_t scheduleSummaryTask = NULLTAG;
						//modify by wuwei --- sch_summary_task
					    ITKCALL(AOM_ask_value_tag(preferred_Items[k],"fnd0SummaryTask",&scheduleSummaryTask));
                        int child_task_taglistNum = 0;
	                    tag_t* child_task_taglist =NULL;
	                    ITKCALL(AOM_ask_value_tags(scheduleSummaryTask,"child_task_taglist",&child_task_taglistNum,&child_task_taglist));
                       
                        int tempNum = 0;
                        for (int j = 0; j < child_task_taglistNum; j++)
                        {
                            int task_type = 0;
                            char* jci6_TaskType = NULL;
                            ITKCALL(AOM_ask_value_int(child_task_taglist[j], "task_type", &task_type));
                            ITKCALL(AOM_ask_value_string(child_task_taglist[j], "jci6_TaskType", &jci6_TaskType));
                            if(task_type==1&&tc_strstr(jci6_TaskType,"gatetype")!=NULL)
                            {
                                time_t finish_time = getFinishDate(child_task_taglist[j]);
                                int tempNum2 = compareDate(finish_time,currentDate);
                                if(tempNum2>0)
                                {
                                    if(tempNum!=0)
                                    {
                                        if(tempNum2<tempNum)
                                        {
                                             tempNum = tempNum2;
                                             currentGate = child_task_taglist[j];
                                        }
                                    }
                                    else
                                    {
                                         tempNum = tempNum2;
                                         currentGate = child_task_taglist[j];
                                    }
                                }
                            }
                            int child_task_taglistNum2 = 0;
	                        tag_t* child_task_taglist2 =NULL;
	                        ITKCALL(AOM_ask_value_tags(child_task_taglist[j],"child_task_taglist",&child_task_taglistNum2,&child_task_taglist2));
                            for(int k = 0; k < child_task_taglistNum2; k++)
                            {
                                int task_type2 = 0;
                                char* jci6_TaskType2 = NULL;
                                ITKCALL(AOM_ask_value_int(child_task_taglist2[k], "task_type", &task_type2));
                                ITKCALL(AOM_ask_value_string(child_task_taglist2[k], "jci6_TaskType", &jci6_TaskType2));
                                if(task_type2==1&&tc_strstr(jci6_TaskType2,"gatetype")!=NULL)
                                {
                                    time_t finish_time = getFinishDate(child_task_taglist2[k]);
                                    int tempNum2 = compareDate(finish_time,currentDate);
                                    if(tempNum2>0)
                                    {
                                        if(tempNum!=0)
                                        {
                                            if(tempNum2<tempNum)
                                            {
                                                 tempNum = tempNum2;
                                                 currentGate = child_task_taglist2[k];
                                            }
                                        }
                                        else
                                        {
                                             tempNum = tempNum2;
                                             currentGate = child_task_taglist2[k];
                                        }
                                    }
                                }
                                if(jci6_TaskType2!=NULL)
                                {
	                                MEM_free(jci6_TaskType2);
	                                jci6_TaskType2 = NULL;
                                }
                            }
                            if(jci6_TaskType!=NULL)
                            {
	                            MEM_free(jci6_TaskType);
	                            jci6_TaskType = NULL;
                            }
	                    }
                    }
                }
                if(project_lists!=NULL)
                {
	                MEM_free(project_lists);
	                project_lists = NULL;
                }
                if(preferred_Items!=NULL)
                {
	                MEM_free(preferred_Items);
	                preferred_Items = NULL;
                }
                if(jci6_ProjectTL!=NULLTAG)
                {
                    assignUser(confirmOverTask,jci6_ProjectTL);
                }
                else
                {
                    HANDLE_ERROR( ERROR_PROJECTTL_NOT_FOUND )
                }
                
                if(currentGate)
                {
                    if(isDebug)
                            TC_write_syslog("currentGate==%u\n",currentGate);
                    ITKCALL(AOM_ask_value_string(currentGate, "jci6_TaskType", &jci6_TaskType));
                    sprintf(jci6_gateN_PEF,"%s%s%s","jci6_gate",jci6_TaskType+(tc_strlen(jci6_TaskType)-1),"_PEF");
                }
                else
                {
                     if(isDebug)
                            TC_write_syslog("未找到当前门对象...\n");
                     HANDLE_ERROR( ERROR_CURRENTGATE_NOT_FOUND )
                }
               
                if( customError = Check_Preference( YFJC_PKRSET_GATE_MAPPING ) )
	            {
		            HANDLE_ERROR_S1( customError, YFJC_PKRSET_GATE_MAPPING);
	            }
                
                ITKCALL(PREF_ask_char_values(YFJC_PKRSET_GATE_MAPPING,&option_value_count,&option_values));
	            for(int i = 0; i < option_value_count; i++)
                {
                    if(tc_strstr(option_values[i],jci6_TaskType)!=NULL)
                    {
                        char task1[30] = "",
                             task2[30] = "";
                        sscanf( option_values[i], "%[^=]=%s", task1,task2 );
                        rightSet = task1;
                        break;
                    }
	            }
                if(option_values!=NULL)
                {
	                MEM_free(option_values);
	                option_values = NULL;
                }
                if(isDebug)
                    TC_write_syslog("需要的检查项集是：%s\n",rightSet);
            }
            else
            {
                 if(isDebug)
                        TC_write_syslog("PEF最新版本对象不属于任何项目\n");
            }
 
            tag_t *child_list = NULL;
            int child_count     = 0,
                pfaRevCount     = 0,
                assignUserCount = 0,
                pkrSetCount     = 0;
            ITKCALL(AOM_ask_value_tags(pefRev,"ps_children",&child_count,&child_list));
		    for(int j=0;j<child_count;j++)
            {
 	            char object_type[WSO_name_size_c+1];
	            ITKCALL(WSOM_ask_object_type(child_list[j],object_type));
	            if(tc_strcmp(object_type,"JCI6_PFARevision")==0)
                {
                    pfaRevCount++;
                    tag_t jci6_SMTELeader = NULLTAG,
                          relation_type   = NULLTAG,
			              *acttach_list   = NULL;
                    int attch_count = 0;
                    
                    char *jci6_gateN_PEF_value = NULL;
                    if(debug)
                         TC_write_syslog("356 jci6_gateN_PEF==%s\n",jci6_gateN_PEF);
                    ITKCALL(AOM_ask_value_string(child_list[j],jci6_gateN_PEF,&jci6_gateN_PEF_value));
                    if(debug)
                         TC_write_syslog("359 jci6_gateN_PEF_value==%s\n",jci6_gateN_PEF_value);
                    if(tc_strcmp(jci6_gateN_PEF_value,"Monitor")==0||tc_strcmp(jci6_gateN_PEF_value,"N/A")==0)
                    {
                        if(isDebug)
                            TC_write_syslog("%s的属性值是%s\n",jci6_gateN_PEF,jci6_gateN_PEF_value);
                    }
                    else
                    {
                        ITKCALL(AOM_ask_value_tag(child_list[j],"jci6_SMTELeader",&jci6_SMTELeader));
                        if(isDebug)
                             TC_write_syslog("jci6_SMTELeader值为：%u\n",jci6_SMTELeader);
                        if(jci6_SMTELeader != NULLTAG)
                        {
                             assignUser(signoffPFA,jci6_SMTELeader);
                             assignUserCount++;
                        }
                    }
                    
                    ITKCALL(GRM_find_relation_type("FND_TraceLink",&relation_type));
		            ITKCALL(GRM_list_secondary_objects_only(child_list[j],relation_type,&attch_count,&acttach_list));
		            if(attch_count>0)
                    {
			            for(int i=0;i<attch_count;i++)
			            {
				            char object_type[WSO_name_size_c+1];
				            ITKCALL(WSOM_ask_object_type(acttach_list[i],object_type));
				            if(tc_strstr(object_type,rightSet)!=NULL)
				            {
                                if(isDebug)
                                    TC_write_syslog("添加了一个检查项集到流程目标下...\n");
                                tag_t *attachments = (tag_t*)MEM_alloc(sizeof(tag_t));
					            attachments[0] = acttach_list[i];
                                int *attachment_types = (int*)MEM_alloc(sizeof(int));
					            attachment_types[0] = EPM_target_attachment;
                                ITKCALL(EPM_add_attachments(pRoot,1,attachments,attachment_types));
                                pkrSetCount++;
                                if(attachments!=NULL)
			                    {
				                    MEM_free(attachments);
				                    attachments = NULL;
			                    }
                                if(attachment_types!=NULL)
			                    {
				                    MEM_free(attachment_types);
				                    attachment_types = NULL;
			                    }
				            }
			            }
			            if(acttach_list!=NULL)
			            {
				            MEM_free(acttach_list);
				            acttach_list = NULL;
			            }
		            }
	            }   
            }
            if(child_list!=NULL)
            {
                MEM_free(child_list);
                child_list = NULL;
            }
            if(jci6_TaskType!=NULL)
            {
                MEM_free(jci6_TaskType);
                jci6_TaskType = NULL;
            } 
            if(pfaRevCount==0)
            {
                HANDLE_ERROR( ERROR_PFA_REVISION_NOT_FOUND )
            }
            if(assignUserCount==0)
            {
                HANDLE_ERROR( ERROR_SMTELEADER_NOT_FOUND )
            }
            if(pkrSetCount==0)
            {
                HANDLE_ERROR( ERROR_PKRSET_NOT_FOUND )
            }  
        }
        else
        {
            HANDLE_ERROR( ERROR_PEF_LATEST_REVISION_NOT_FOUND )
        }
       
    }
	return customError;
}

/*********************************
assignUser:: 将用户指派给流程节点
**********************************/
int assignUser(tag_t task,tag_t user)
{

	char tasktype[WSO_name_size_c+1] = "",
	     user_id[WSO_name_size_c+1]  = "";

    int		signoff_count = 0,
            perform_count = 0,
			count         = 0;

	tag_t	*signoffs         = NULL,
	        responsible_party = NULLTAG,
			user_tag		  = NULLTAG,
            *perform_attaches  = NULL,  
	        *subtasks         = NULL;

	ITKCALL(WSOM_ask_object_type(task,tasktype));
	ITKCALL(SA_ask_user_identifier(user,user_id));
	ITKCALL(EPM_assign_responsible_party(task,user));
	ITKCALL(EPM_ask_sub_tasks(task,&count,&subtasks));

	
	for(int i = 0 ; i < count;i++)
    {   
        ITKCALL(WSOM_ask_object_type(subtasks[i],tasktype));
		if(tc_strcmp(tasktype,"EPMSelectSignoffTask")==0)
        {	
			bool isSameUser=0;
            ITKCALL( EPM_ask_attachments(subtasks[i], EPM_signoff_attachment, &perform_count, &perform_attaches));
		//	printf("perform_count............%d\n",perform_count);
			for(int j=0;j<perform_count;j++)
			{
				tag_t memberTag = NULLTAG;
				SIGNOFF_TYPE_t memberType;
				char comments[CR_comment_size_c + 1] = "";
				tag_t user_tag = NULLTAG;

				ITKCALL( EPM_ask_signoff_member( perform_attaches[j], &memberTag, &memberType ) );
				ITKCALL( SA_ask_groupmember_user( memberTag, &user_tag ) );	
				if(user_tag==user)
					isSameUser=1;

			}
            if(isDebug)
				TC_write_syslog("user_tag==%d \n",user_tag);
			if(!isSameUser){
				ITKCALL(EPM_create_adhoc_signoff(subtasks[i], user, &signoff_count, &signoffs ) );
				ITKCALL(EPM_set_adhoc_signoff_selection_done(subtasks[i],true) );
			}
			
		}
	}
	
	return ITK_ok;
}

 /******************************************************************************
*根据结束日期和实际结束日期的比较，确定结束日期，并返回一个time_t对象
* @param date1
* @param date2
******************************************************************************/
  time_t getFinishDate(tag_t scheduleTask)
  {
	  date_t finish_dateS        = NULLDATE,
	   		 actual_finish_dateS = NULLDATE;

	  ITKCALL(AOM_ask_value_date(scheduleTask,"finish_date",&finish_dateS));
	  ITKCALL(AOM_ask_value_date(scheduleTask,"actual_finish_date",&actual_finish_dateS));
	  if(!DATE_IS_NULL(actual_finish_dateS)){
		 return  transtoTime_t(actual_finish_dateS);
	  }
	  return  transtoTime_t(finish_dateS);
  }

  /************************************************
transtoTime_t:: 将date_t类型转换为相应的time_t类型
*************************************************/
time_t transtoTime_t(date_t date)
{
    time_t rT;
    struct tm * target_time;
    int year = date.year,
        month = date.month,
        day = date.day,
        hour = date.hour,
        minute = date.minute,
        second = date.second;
    time ( &rT );
    target_time = localtime ( &rT ); 
    target_time->tm_year = year - 1900;
    target_time->tm_mon= month;    
    target_time->tm_mday = day;  
    target_time->tm_hour = hour;
    target_time->tm_min = minute;
    target_time->tm_sec = second;
    rT = mktime (target_time);
    return rT;
}

/******************************************************************************
*比较两个time_t对象，返回数字，如果大于0，前者在后者之前；反之，前者在后者之后
* @param time1
* @param time2
******************************************************************************/
int compareDate(time_t time1,time_t time2)
{
    return time1-time2;
}

int clear_default_user( tag_t task )
{
	int   ifail              = ITK_ok;
	tag_t cur_perform_task   = NULLTAG;
		  
	ifail = EPM_ask_sub_task(task, "select-signoff-team", &cur_perform_task );
	
	if( cur_perform_task != NULLTAG )
	{
		ITKCALL( AOM_lock( cur_perform_task ) );
		//ITKCALL( ifail = AOM_set_value_logical( cur_perform_task, "done", FALSE ) );
		ITKCALL( ifail = AOM_set_value_tags( cur_perform_task, "signoff_attachments", 0, NULL ) );
		//ITKCALL( ifail = AOM_set_value_logical( cur_perform_task, "done", TRUE ) );
		ITKCALL( AOM_save( cur_perform_task ) );
		ITKCALL( AOM_unlock( cur_perform_task ) );
		ITKCALL( AOM_refresh( cur_perform_task, FALSE ));
	}
	
	return ifail;
}

/*
get_Weekday: 计算年月日所在的星期数
 * 蔡勒公式：W = [C/4] - 2C + y + [y/4] + [13 * (M+1) / 5] + d - 1 
 * C是世纪数减一，y是年份后两位，M是月份，d是日数。
 * 1月和2月要按上一年的13月和14月来算，这时C和y均按上一年取值。
 @param int 年份
 @param int 月份
 @param int 日期
 @return int 星期几
 * */
 int get_Weekday(int Year,int Month,int Day)
 {
	   if(Month == 1 || Month ==2)
		{
			Month += 12;
			Year --;
		}
		int week = (Day + 1 + 2 * Month + 3 * (Month+1)/5 + Year + Year/4 - Year/100 + Year/400) % 7;
		//Add 4-7 by zhanggl
		if( week == 0 )
		{
			week = 7;
		}
		// End add
		return week;
 } 

 /*
checkdatebyweek: 如果是PerWeek的检查是否同星期
@param date_t  设置的日期
@param time_t	当前时间
@param char*	 当前年份
@param char* 当前月份
@param char* 当前日
@return bool 是否允许设置
*/
bool checkdatebyweek(date_t date,time_t cur_local, int cur_year, int cur_month, int cur_day)
{
	time_t today,
		firstdayofweek,
		lastdayofweek;
	char strfirstday[30],
		strlastday[30];
	char *daytoset = NULL;
	today = cur_local;
	ITKCALL(DATE_date_to_string(date,"%Y%m%d",&daytoset)); 

	int iYear = cur_year;
	int iMonth = cur_month;
	int iDay = cur_day;

	//取得当前日期所在星期几
	int icur_weekday = get_Weekday(iYear,iMonth,iDay);
	ECHO("today's weekday is:%d\n",icur_weekday);

	int idays = icur_weekday - 1 ;
	firstdayofweek = today -  (idays * 24 * 3600); /*一天24小时，一小时3600秒*/ 

	idays = 7 - icur_weekday ;
	lastdayofweek = today +  (idays * 24 * 3600); /*一天24小时，一小时3600秒*/ 

	tm* firstlocal=localtime(&firstdayofweek);  
	strftime(strfirstday,30,"%Y%m%d",firstlocal);

	ECHO("first day in this week is:  %s\n",strfirstday);

	tm* lastlocal=localtime(&lastdayofweek);  
	strftime(strlastday,30,"%Y%m%d",lastlocal);

	ECHO("last day in this week is :  %s\n",strlastday);

	int a = atoi((char*)daytoset);
	int b = atoi((char*)strfirstday);
	int c = atoi((char*)strlastday);

	MEM_free(daytoset);
	if( a >= b && a <= c)
	{
		return true;
	}else
	{
		return false;
	}
}

/*
checkdatebymonth:如果是PerMonth,则允许设置相同月份的
@param date_t  设置的日期
@param char*	 当前年份
@param char* 当前月份
@return bool 是否允许设置
*/
bool checkdatebymonth(date_t date, int cur_year, int cur_month)
{
	char *daytoset = NULL;
	char cur_year_month[7] = "";

	ITKCALL(DATE_date_to_string(date,"%Y%m",&daytoset)); 

	/*cur_year_month =  (char *)MEM_alloc(sizeof( char ) * (tc_strlen(cur_year) + tc_strlen(cur_month) + 1));
	tc_strcpy(cur_year_month,cur_year);
	tc_strcat(cur_year_month,cur_month);*/
    sprintf(cur_year_month, "%d%d", cur_year, cur_month);

	ECHO("cur_year_month = %s\n",cur_year_month);
	ECHO("daytoset = %s\n",daytoset);

	if(tc_strcasecmp(daytoset,cur_year_month) == 0)
	{
		MEM_free(daytoset);
		return true;
	}else
	{
		MEM_free(daytoset);
		return false;
	}
}

/*
checkdatebydays: 如果是7~7这种形式的，检查是否在配置区间内
@param date_t  设置的日期
@param time_t	当前时间
@param char* 配置的参数
@param char*	 当前年份
@param char* 当前月份
@param char* 当前日
@return bool 是否允许设置
*/
bool checkdatebydays(date_t date,time_t cur_local,char* interval,int cur_year,int cur_month, int cur_day)
{
	time_t today,
		beginday,
		endday;
	char  strfirstday[30],
		strlastday[30];

	char *daytoset = NULL,
		*day_before_interval = NULL,
		*day_after_interval = NULL;

	today = cur_local;
	char* strinterval= NULL;

	strinterval = (char*)MEM_alloc(sizeof(char*) * (tc_strlen(interval) + 1));

	tc_strcpy(strinterval, interval );

	ITKCALL(DATE_date_to_string(date,"%Y%m%d",&daytoset)); 

	day_before_interval = tc_strtok(strinterval,"~");
	day_after_interval = tc_strtok(NULL,"~");

	beginday = today -  (atoi(day_before_interval) * 24 * 3600); /*一天24小时，一小时3600秒*/ 
	endday = today +  (atoi(day_after_interval) * 24 * 3600); /*一天24小时，一小时3600秒*/ 

	tm* firstlocal=localtime(&beginday);  
	strftime(strfirstday,30,"%Y%m%d",firstlocal);
	ECHO("checkdatebydays:: first day is :  %s\n",strfirstday);

	tm* lastlocal=localtime(&endday);  
	strftime(strlastday,30,"%Y%m%d",lastlocal);

	ECHO("checkdatebydays:: last day is:  %s\n",strlastday);

	int a = atoi((char*)daytoset);
	int b = atoi((char*)strfirstday);
	int c = atoi((char*)strlastday);


	ECHO("checkdatebydays:: a=%d  b=%d  c=%d \n",a,b,c);


	if( a >= b && a <= c)
	{
		MEM_free(daytoset);

		ECHO("checkdatebydays:: return true\n");
		return true;
	}else
	{
		ECHO("checkdatebydays:: return true\n");
		MEM_free(daytoset);
		//new by wuwei
		return true;
	}
}


/**
* 将一个long double类型的数据四舍五入掉
* @param old_double
* return
*/
int DataPrecision(long double old_double, long double *return_double)
{
	double inter = old_double*100;

	double db = old_double*100 + 0.5;

	ECHO("old_double = %lf\n", old_double);
	ECHO("inter = %d\n", inter);

	if(inter == floor(db))
	{
		ECHO("inter/100 = %lf\n", inter/100.0);
		*return_double = inter/100.0;
	}
	else
	{
		ECHO("inter/100 + 0.01 = %lf\n", inter/100.0 + 0.01);
		*return_double = inter/100.0 + 0.01;
	}

	return 0;
}

int listAllSMTELeader( TAGLIST *head, int *allTagCnt)
{
	int status = ITK_ok;

	char* discipline_name = NULL;

	tag_t discipline_tag = NULLTAG;

    TAGLIST *node = NULL,
			*cNew = NULL;

	node = head;

	//ITKCALL(SA_find_discipline("SMTELeads",&discipline_tag));
    ITKCALL(SA_find_discipline("SDT_SMTE-Engineer",&discipline_tag)); //modify by liwh on 2013-5-24
	if(discipline_tag!=NULLTAG)
	{
        ITKCALL(AOM_refresh(discipline_tag,1));
		tag_t* discipline_members = NULL;
		int discipline_membersNum = 0;
		ITKCALL(AOM_ask_value_tags(discipline_tag,"TC_discipline_member",&discipline_membersNum,&discipline_members));
		if(discipline_membersNum>0)
        {
			for(int i=0;i<discipline_membersNum;i++)
			{
				char* user_id = NULL;
				tag_t user_type_tag;
				char user_type[TCTYPE_name_size_c+1];
				ITKCALL(TCTYPE_ask_object_type(discipline_members[i],&user_type_tag));
				ITKCALL(TCTYPE_ask_name(user_type_tag,user_type));
				if(tc_strcmp(user_type,"User")==0)
				{
                    cNew = ( TAGLIST* )MEM_alloc( NODELEN );
				    cNew->ds_tag = discipline_members[i];
				    cNew->next = NULL;
				    node->next = cNew;
				    node = cNew;
				    (*allTagCnt)++;
				}
			}
		}
        else
	    {
			    TC_write_syslog( "the discipline 'SDT_SMTE-Engineer' has no user, please check!\n")	;
	    }
		if(discipline_members!=NULL)
		{
			MEM_free(discipline_members);
			discipline_members = NULL;
		}
	}
	else
	{
			TC_write_syslog( "the discipline 'SDT_SMTE-Engineer' doesn't exists, please check!\n")	;
	}
	return status;
}

/**
 * 根据用户TAG获得用户组division和学科section
 * @param user_tag
 * @param postion
 * @return
 */
void getUserDivisionSection(tag_t user_tag ,tag_t* division ,tag_t* section)
{
    char *postion_name = NULL,
         **entries     = NULL,
         **values      = NULL,
         *other_values[1],
         group_name_str[200] = "";

    tag_t costvalue  = NULLTAG,
          query_tag  = NULLTAG,
          *results   = NULL;

    int num_found   = 0,
        entry_count = 0;


	const char query_name[ QRY_name_size_c + 1] = YFJC_SEARCHDISCIPLINEBYID;

    ITKCALL( QRY_find( query_name , &query_tag ) );

    if( query_tag == NULLTAG )
	{
        TC_write_syslog( "没有找到查询构建器 %s\n",query_name);
		return ;
	}

    ITKCALL( QRY_find_user_entries( query_tag , &entry_count , &entries , &values ) );

    char* user_id = NULLTAG;

	ITKCALL(AOM_ask_value_string(user_tag,"user_id",&user_id));

    other_values[0] = ( char* )MEM_alloc( ( tc_strlen( user_id ) + 1 ) * sizeof( char ) );

    tc_strcpy( other_values[0] , user_id );
	
	ITKCALL( QRY_execute( query_tag , entry_count , entries , other_values , &num_found , &results ) );
    for( int i = 0 ;i < num_found ; i++ )
    {
        ITKCALL( AOM_ask_value_string( results[i] , OBJECT_NAME , &postion_name ) );

        if( tc_strstr( postion_name , "ExtSupporter_" ) != NULL )
        {
            tc_strcpy(group_name_str, postion_name+13);
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

    if( user_id != NULL )
    {
        MEM_free( user_id );

        user_id = NULL;
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
     
    tag_t default_group = NULLTAG;

    if(tc_strcmp( group_name_str , "" ) == 0)
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
         if( entries2 != NULL )
         {
            MEM_free( entries2 );

            entries2 = NULL;
         }
         if( values2 != NULL )
         {
            MEM_free( values2 );

            values2 = NULL;
         }
         int num_found = 0;
         tag_t *results = NULL;
         ITKCALL( QRY_execute( query_tag , 1 , other_entries2 , other_values2 , &num_found , &results ) );
         if( other_entries2[0] != NULL )
         {
            MEM_free( other_entries2[0] );

            other_entries2[0] = NULL;
         }
         if( other_values2[0] != NULL )
         {
            MEM_free( other_values2[0] );

            other_values2[0] = NULL;
         }
         ECHO("1207 num_found====%u\n",num_found);
         if(num_found>0)
         {
              default_group = results[0];
         }
    }
	

    if(default_group!=NULLTAG)
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
                 ECHO("1250 section=%u,division=%u\n",*section,*division);
			    break;
		    }
	    }
        if(group_name!=NULL)
	    {
		    MEM_free(group_name);
		    group_name = NULL;
	    }
    }
}

/**
 * 根据用户组获得用户组division和学科section
 * @param user_group
 * @param postion
 * @return
 */
void getGroupDivisionSection(tag_t user_group ,tag_t* division ,tag_t* section)
{
	char* group_name = NULL;

	ITKCALL(AOM_ask_value_string(user_group,"full_name",&group_name));

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
		if(tc_strcmp(group_vec[n].c_str(),TECHCENTER)==0||tc_strcmp(group_vec[n].c_str(),"技术中心")==0)
		{
			if(n==2)
			{
				*division = user_group;
			}
			else if(n==3)
			{
				*section = user_group;
				ITKCALL(AOM_ask_value_tag(user_group,"parent",division));
			}
           //  ECHO("997 section====%u\n",*section);
           //  ECHO("998 division====%u\n",*division);
			break;
		}
	}
	if(group_name!=NULL)
	{
		MEM_free(group_name);
		group_name = NULL;
	}
}

/*=============================================================================*
 * FUNCTION: CreateLogFile
 * PURPOSE : create log file
 * INPUT: 
 *     char* FunctionName  	// the funtion which need to create log file
 *     FILE** logFile		// out: the log file pointer
 *	
 * RETURN: 
 *     void
 *============================================================================*/

void CreateLogFile(char* FunctionName)
{
	int i=0, ifail = ITK_ok;
	date_t status_now;
	//char* date_string = NULL;
	char date_string[MAX_PATH_LENGTH];
	char logFileDir[MAX_PATH_LENGTH];
	char logFileName[MAX_PATH_LENGTH];

	char* session_uid = NULL;
	tag_t session_tag = NULLTAG;
	time_t now;
	struct tm *p;

	time(&now);

	logFile = NULL;
	//current_time(&status_now);
	p=localtime(&now); 

	memset(date_string, 0, sizeof(date_string));
	sprintf(date_string,"%4d%02d%02d%02d%02d%02d",1900+p->tm_year,p->tm_mon+1 ,p->tm_mday ,p->tm_hour,p->tm_min ,p->tm_sec );
	//if( DATE_date_to_string( status_now, "%Y%m%d%H%M%S", &date_string) != ITK_ok )
	//ifail = ITK_date_to_string (status_now, &date_string );
	//if (ifail)
	//{
	//	printf("!*ERROR*!: Failed to get current date time\n");
	//	goto CLEANUP;
	//}

	memset(logFileDir, 0, sizeof(logFileDir));
	memset(logFileName, 0, sizeof(logFileName));
	//get log dir
	sprintf(logFileDir, "%s", getenv("TC_USER_LOG_DIR"));
	printf("\n log file dir: %s\n", logFileDir);
	//try to change dir to TC_USER_LOG_DIR
	if(chdir(logFileDir)!=ITK_ok)
	{
		//not set TC_USER_LOG_DIR
		//log in to default TC_LOG
		memset(logFileDir, 0, sizeof(logFileDir));
		sprintf(logFileDir, "%s", getenv("TC_LOG"));
		printf("\n TC_USER_LOG_DIR invalide, log file dir: %s\n", logFileDir);
		if(chdir(logFileDir)!=ITK_ok)
		{
			//still can not change to log dir
			printf("!*ERROR*!: Failed to change dir to TC_USER_LOG_DIR\n");
			goto CLEANUP;
		}
	}
	
	//get session_uid to make sure the log file name unique
	POM_ask_session(&session_tag);
	ITK__convert_tag_to_uid(session_tag, &session_uid);
	

	//get logFileName
	sprintf(logFileName, "%s_%s_%s.log", FunctionName, session_uid, date_string);
	printf("log file name: %s\n", logFileName);

	//for(i = 0; _access((char *)logFileName, 4) == 0; i++)
	{
		memset(logFileName, 0, sizeof(logFileName));
		sprintf(logFileName, "%s_%s_%s_%d.log", FunctionName, session_uid, date_string, i);
	}
	printf("final log file name: %s\n", logFileName);

	//create log file
	logFile = fopen(logFileName, "w");	

CLEANUP:
	//DOFREE(date_string);
	DOFREE(session_uid);
}

/*=============================================================================*
 * FUNCTION: WriteLog
 * PURPOSE : write log, if debug log File not null, write log message to log File
 * INPUT: 
 *     const char* format // debug message string
 *
 * RETURN: 
 *     void
 *============================================================================*/
void WriteLog(const char* format, ...)
{
	va_list arg;
	char tmp[MAX_PRINTLINE_LENGTH]={'\0'};

	//get the message
		memset(tmp, 0, sizeof(tmp));
		va_start(arg, format);
		vsprintf(tmp, format, arg);
		va_end(arg);

		//----------print to command window for trace--------//
		printf("%s\n", tmp);
		TC_write_syslog("%s\n", tmp);
		
	if(logFile)
	{		
		//print message to log file
		fprintf(logFile, "%s\n", tmp);
		fflush(logFile);
	}
	else
	{
		printf("*!Error!*: Log File Not Exist\n");
	}
}

void CloseLog(void)
{
	if(logFile)
	{
		fclose(logFile);
		logFile = NULL;
	}
}

extern int soa_DataPrecision(long double old_double, long double *return_double)
{
	double inter = old_double*100;

	double db = old_double*100 + 0.5;

	if(inter == floor(db))
	{
		*return_double = inter/100.0;
	}
	else
	{
		*return_double = inter/100.0 + 0.01;
	}

	return 0;
}

string st_get_time_stamp(string format)
{
	char tmp[64] = { 0 };
	time_t timep;
	struct tm std;
	memset(&std, 0, sizeof(tm));
	struct tm *p = &std;
	time(&timep);
	localtime_s(p, &timep);
	strftime(tmp, sizeof(tmp), format.c_str(), p);
	return tmp;
}

//add by wuh 2014-8-12
//写日志讯息
extern void tr_ECHO(char *format, ...)
{
	if (!YFJC_OPT_DEBUG)
		return;
	//if(isWriteLog)
	//{
	va_list arg;
	char tmp[2000] = { '\0' };
	//get the message
	memset(tmp, 0, sizeof(tmp));
	va_start(arg, format);
	vsprintf(tmp, format, arg);
	va_end(arg);
	string time_str = st_get_time_stamp("code log %Y-%m-%d %H:%M:%S INFO ");
	if (fileLog == NULL)
	{

		printf("%s%s\n", time_str.c_str(), tmp);
		TC_write_syslog("%s%s\n", time_str.c_str(), tmp);
	}
	else
	{
		printf("%s%s\n", time_str.c_str(), tmp);
		fprintf(fileLog, "%s%s\n", time_str.c_str(), tmp);
		fflush(fileLog);
	}
	//}
}

//判断当前时间月份是否大于或等于10月份,是，则财年为sop年+1,则财年为sop年
int SOPFYIsYear(date_t date)
{
	int current_year = date.year;
	int current_month = date.month+1;
	printf("%s月份为%d\n",JCI6_SOP,current_month);
	if(current_month>=10)
	{
		return current_year+1;
	}
	return current_year;
}

int JCI6TimesheetMinAlert( METHOD_message_t *msg, va_list args )
{
    int ifail      = ITK_ok;

    tag_t timesheetentry_tag    = NULLTAG,
          user_tag              = NULLTAG,
          prop_tag              = va_arg( args, tag_t );
    int   minutesProValue       = va_arg( args, int );
    date_t date = NULLDATE;

	ask_opt_debug();
    ECHO("*****************JCI6TimesheetMinAlert start************************\n");

    METHOD_PROP_MESSAGE_OBJECT(msg, timesheetentry_tag);
    ITKCALL(AOM_ask_value_date(timesheetentry_tag, TIMESHEET_DATE, &date));
    ITKCALL(AOM_ask_value_tag(timesheetentry_tag, TIMESHEET_USERTAG, &user_tag));
    ECHO("user_tag=%u\n",user_tag);
    if(user_tag!=NULLTAG)
	{
        char *user_id = NULL;
        ITKCALL(AOM_ask_value_string(user_tag, USER_ID, &user_id));
        int minutesCount = getMinutesForDay(timesheetentry_tag,user_id,date);
        tag_t position = NULLTAG;
        getUserPostion(user_id ,&position );
        ECHO("position = %u\n",position);
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
            
            if(tc_strcmp(part1,part2)==0)
            {
				if( postion_name )
	        	{
		        	MEM_free( postion_name );
		        	postion_name = NULL;
	        	}
	        	 if( user_id )
				{	
					MEM_free( user_id );
					user_id = NULL;
				}
				ITKCALL( AOM_lock( timesheetentry_tag ) );
				ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
				ITKCALL( AOM_save( timesheetentry_tag ) );
				ITKCALL( AOM_unlock( timesheetentry_tag ) );
				ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
	    		return ifail;
        	
            }
        }
        if( user_id )
	    {
		    MEM_free( user_id );
		    user_id = NULL;
	    }
        ECHO("minutesCount = %d,minutesProValue = %d\n",minutesCount,minutesProValue);
//        if(minutesCount<480&&minutesCount+minutesProValue>480)
//2016-06-07	mengyawei modify
        if(minutesCount<=480&&minutesCount+minutesProValue>480)
        {
        		int billType = getBillTypeByDate(date);
        		if(billType==1)
        		{
        		 	HANDLE_ERROR( ERROR_OVER_EIGHT_HOURS );
        		} 
        }
        ITKCALL( AOM_lock( timesheetentry_tag ) );
        ITKCALL( PROP_assign_int( prop_tag, minutesProValue ) );
	    ITKCALL( AOM_save( timesheetentry_tag ) );
	    ITKCALL( AOM_unlock( timesheetentry_tag ) );
	    ITKCALL( AOM_refresh( timesheetentry_tag ,true ) );
    }
    ECHO("*****************JCI6TimesheetMinAlert end*****************\n");

    return ifail;
}

string getNowtime2(){
	string ss;
	date_t current_date=NULLDATE;
	char datatime[65]={'\0'};
	getNowTime( &current_date ); 
	sprintf(datatime,"%04d-%02d-%02d %02d:%02d:%02d:",current_date.year,current_date.month+1,current_date.day,current_date.hour,current_date.minute,current_date.second);
   
	ss.append(datatime);
	//printf("getNowtime2:%s\n",datatime);
	return ss;
}



