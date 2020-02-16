/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_importTimeLog.cxx
   Module  : user_exits

   import timelog
============================================================================================================
DATE           Name             Description of Change
25-Feb-2013    liuc          creation
$HISTORY$
04-Mar-2013    liuc          modification
============================================================================================================*/
#include "yfjc_ebp_head.h"
//SP-RICH-FUN-13.Timelog导入工具 
/**
	timelog到入
*/

#ifdef __cplusplus
extern "C" {
#endif

int importTimeLog( void * returnValueType )
{
	int ifail          = ITK_ok,
		costInfoLen    = 0,
		i              = 0,
		object_nameLen = 0;

	tag_t item_tag       = NULLTAG,
		rev_tag          = NULLTAG,
		relation_tag     = NULLTAG,
		release_status   = NULLTAG,
		project_tag	     = NULLTAG,
		project_tags[1],
		item_tags[3];
	char *programInfoContent = NULL,
		**costInfoContent    = NULL,
		**object_names       = NULL,
		*jci6_CPT            = NULL,
		*programID           = NULL,
		*programRevID        = NULL,
		*dsName              = NULL,
		*dsfilePath          = NULL;

	logical          active = true ;
	FILE   *fp = NULL;
	char   readLine[SS_MAXMSGLEN]   = "",
		   copy_readLine[SS_MAXMSGLEN]   = "";

	logical isRev = FALSE;

	ifail=USERARG_get_string_argument(&programInfoContent); //项目信息
	ifail=USERARG_get_string_array_argument(&object_nameLen,&object_names);  //费用信息名称  
	ifail=USERARG_get_string_argument(&dsName);//费用信息属性  
	ask_opt_debug();

	programID    = tc_strtok( programInfoContent, VERTICALBAR);
	programRevID = tc_strtok( NULL, VERTICALBAR);
	jci6_CPT     = tc_strtok( NULL, VERTICALBAR);

	ITKCALL( PROJ_find( programID, &project_tag ) );
  
	if(project_tag)
	{	
		ITKCALL(PROJ_is_project_active(project_tag,&active));
        ECHO("711 active==%d\n",active);
		if(!active)
		{	
			ITKCALL(AOM_refresh(project_tag, true));
			ITKCALL(PROJ_activate_project(project_tag,true));
			ITKCALL(AOM_save(project_tag));
			ITKCALL(AOM_refresh(project_tag, false));
		}
             logical active2;
             ITKCALL(PROJ_is_project_active(project_tag,&active2));
        ECHO("81 active2==%d\n",active2);
	}
	
	ITKCALL( ITEM_find_item( programID, &item_tag ) );
	if( !tc_strstr( programRevID, "NULL" ) )
	{
		ITKCALL( ITEM_find_rev( programID, programRevID, &rev_tag ) );
		isRev = TRUE;
	}

	ITKCALL( GRM_find_relation_type( IMAN_EXTERNAL_OBJECT_LINK, &relation_tag ) );
	ITKCALL( CR_create_release_status( RELEASEDSTATUS, &release_status ) );
	ITKCALL( AOM_lock( release_status ) );
	ITKCALL( AOM_load( release_status ) );
	ITKCALL( AOM_save( release_status ) );
	ITKCALL( AOM_unlock( release_status ) );

	getDatasetFile( dsName, &dsfilePath );
	ECHO("dsfilePath==%s \n",dsfilePath);

	if((fp=fopen(dsfilePath,"rt"))!=NULL)
	{	
		while( fgets(readLine, SS_MAXMSGLEN, fp) != NULL && tc_strcmp(readLine, "\n") != 0 )
		{	
			readLine[tc_strlen(readLine)-1] = '\0';

			tag_t  costTagHour			= NULLTAG,
				   costTagManMonth		= NULLTAG,
				   costTagRate			= NULLTAG;
			char costInfoName1[128]    = "",
				 costInfoName2[128]    = "",
				 costInfoName3[128]    = "";
			char timestamp[13] = "";
			time_t T;
			struct tm * timenow;
			time ( &T );
			timenow = localtime ( &T );
			char month[3] = "",
				 day[3]   = "",
				 hour[3]  = "",
				 min[3]   = "";
			if( timenow->tm_mon < 9 )
			{
				sprintf( month, "%d%d", 0, timenow->tm_mon+1 );
			}
			else
			{
				sprintf( month, "%d",timenow->tm_mon+1);
			}
			if( timenow->tm_mday < 10 )
			{
				sprintf( day, "%d%d",0,timenow->tm_mday);
			}
			else
			{
				sprintf( day, "%d",timenow->tm_mday);
			}
			if( timenow->tm_hour < 10)
			{
				sprintf( hour, "%d%d", 0, timenow->tm_hour );
			}
			else
			{
				sprintf( hour, "%d",timenow->tm_hour);
			}
			if( timenow->tm_min < 10 )
			{
				sprintf( min, "%d%d",0,timenow->tm_min);
			}
			else
			{
				sprintf( min, "%d",timenow->tm_min);
			}
			ECHO("month==%s！\n",month);
			sprintf( timestamp, "%d%s%s%s%s",1900+timenow->tm_year, month, day, hour, min );

			ECHO("object_names[i]==%s \n",object_names[i]);
			ECHO("costInfoContent[i]==%s \n",readLine);

			sprintf(costInfoName1, "%s%s%s%s%s", object_names[i], "_", JCI6_UNITHOUR, "_", timestamp );
			ECHO("costInfoName1 ==%s \n ",costInfoName1);

			createCostInfo( costInfoName1, &costTagHour);
			ECHO("costTag1 ==%u \n ",costTagHour);
			tc_strcpy( copy_readLine, readLine );
			setCostInfoProp(copy_readLine,jci6_CPT,JCI6_UNITHOUR,costTagHour, isRev);

			sprintf( costInfoName2, "%s%s%s%s%s", object_names[i], "_", JCI6_UNITMANMONTH, "_", timestamp );
			ECHO("costInfoName2 ==%s \n ",costInfoName2);

			createCostInfo( costInfoName2, &costTagManMonth );
			ECHO("costTag2 ==%u \n ", costTagManMonth);
			tc_strcpy( copy_readLine, readLine );
			setCostInfoProp( copy_readLine, jci6_CPT, JCI6_UNITMANMONTH, costTagManMonth, isRev );

			sprintf( costInfoName3, "%s%s%s%s%s", object_names[i], "_", JCI6_UNITYUAN, "_", timestamp );
			ECHO("costInfoName3 ==%s \n ",costInfoName3);

			createCostInfo( costInfoName3, &costTagRate );
			ECHO("costTag3 ==%u \n ",costTagRate);
			tc_strcpy( copy_readLine, readLine );
			setCostInfoProp( copy_readLine, jci6_CPT, JCI6_UNITYUAN, costTagRate, isRev );

			if(rev_tag)
			{
				ITKCALL(ITEM_attach_rev_object_tag(rev_tag,costTagHour,relation_tag));
				ITKCALL(ITEM_attach_rev_object_tag(rev_tag,costTagManMonth,relation_tag));
				ITKCALL(ITEM_attach_rev_object_tag(rev_tag,costTagRate,relation_tag));
			}
			else
			{	
				ITKCALL( ITEM_attach_object_tag( item_tag, costTagHour, relation_tag ) );
				ITKCALL( ITEM_attach_object_tag( item_tag, costTagManMonth, relation_tag ) );
				ITKCALL( ITEM_attach_object_tag( item_tag, costTagRate, relation_tag ) );
			}
			project_tags[0]=project_tag;
			item_tags[0]=costTagHour;
			item_tags[1]=costTagManMonth;
			item_tags[2]=costTagRate;

			ITKCALL(EPM_add_release_status(release_status,3,item_tags,NULL));
			if(project_tag)
			{	
				ITKCALL(PROJ_assign_objects(1,project_tags,3,item_tags));
                            ECHO("---------------------------------------- \n ");
			}
			i++;
		}
		fclose(fp);
	}
	if(project_tag)
	{	
		if(!active)
		{	
			ITKCALL(AOM_lock(project_tag));
			ITKCALL(PROJ_activate_project(project_tag,false));
			ITKCALL(AOM_save(project_tag));
			ITKCALL(AOM_unlock(project_tag));
		}
	}

	if(dsfilePath)
		MEM_free(dsfilePath);

	MEM_free(programInfoContent);
	MEM_free(object_names);
	MEM_free(dsName);

	return 0;
}
int set_property_value(tag_t costInfo_tag, char* propname, char* propvalue, logical flag)
{
	return 0;
}
/**
	创建项目信息版本
*/
int createProgramInfoRev( void * returnValueType )
{
	int ifail = ITK_ok,
		len   = 0,
		i     = 0;

	char **content     = NULL,
		 * item_id     = NULL,
		 *item_name    = NULL,
		 *rev_desc     = NULL,
		 *jci6_EQU     = NULL,
		 * revision_id = NULL,
		 *dsName       = NULL,
		 *dsfilePath      = NULL;

	tag_t item_tag    = NULLTAG,
		  baseRev_tag = NULLTAG,
		  new_rev_tag = NULLTAG;
		
	FILE   *fp = NULL;
	char   readLine[SS_MAXMSGLEN]   = "";

	ifail=USERARG_get_string_argument(&dsName); //项目信息
    ask_opt_debug();

	getDatasetFile(dsName,&dsfilePath);
	ECHO("dsfilePath==%s \n",dsfilePath);

	if((fp=fopen(dsfilePath,"rt"))!=NULL)
	{	
		while( fgets(readLine, SS_MAXMSGLEN, fp) != NULL && tc_strcmp(readLine, "\n") != 0 )
		{	
			readLine[tc_strlen(readLine)-1] = '\0';
			
			new_rev_tag=NULLTAG;
			item_id     =tc_strtok(readLine,VERTICALBAR);
			item_name   =tc_strtok(NULL,VERTICALBAR);
			rev_desc    =tc_strtok(NULL,VERTICALBAR);
			jci6_EQU    =tc_strtok(NULL,VERTICALBAR);
			revision_id =tc_strtok(NULL,VERTICALBAR);
			if(i==0)
			{
				ITKCALL(ITEM_find_rev(item_id,"A",&baseRev_tag));
				ECHO("baseRev_tag==%d \n ",baseRev_tag);
			}	
			ITKCALL(ITEM_find_rev(item_id,revision_id,&new_rev_tag));
			if(new_rev_tag==NULLTAG)
				ITKCALL(ITEM_copy_rev(baseRev_tag,revision_id,&new_rev_tag));
			ECHO("new_rev_tag==%d \n ",new_rev_tag);
			ITKCALL(AOM_set_value_string(new_rev_tag,"object_desc",rev_desc));
			ITKCALL(AOM_set_value_double(new_rev_tag,"jci6_EQU",strtod(jci6_EQU,NULL)));
			ITKCALL(AOM_set_value_string(new_rev_tag,"jci6_PDxSeq",revision_id));
			ITKCALL(AOM_save ( new_rev_tag ));
			ITKCALL(AOM_unlock ( new_rev_tag ));
		}
	fclose(fp);
	}
	if(dsfilePath)
		MEM_free(dsfilePath);
	MEM_free(dsName);
	return 0;
}



 
long double DataPrecision_old(const long double old_double)
{
    long double new_data;

	char old_char[32],
	     intNew_char[32],
	     fNew_char[4]="\0";

	sprintf(old_char,"%.3lf",old_double);
	char *intPtr   = tc_strtok(old_char,"."),
	     *pointPtr = tc_strtok(NULL,".");

	tc_strcpy(intNew_char, intPtr);
	tc_strcpy(fNew_char ,"");
	tc_strcat(fNew_char ,pointPtr);

	bool istrue =false; 
	if(fNew_char[2] >= '5')
	{
		istrue = true;
	}
	fNew_char[2] = '0';
	long l;
	l =atol(intNew_char);
	double f;
	f = atof(fNew_char)/1000;
	if(istrue){
		new_data=(double)l+(double)f+0.01;
	}else{
	    new_data=l+f;
	}
	return new_data;
 }




/**
	create costInfo  used for java call c 	
*/

  int yfjs_createCostInfo(void * returnValue)
  {

	char* object_name=NULL;
	tag_t boTag = NULLTAG;
	tag_t createInputTag=NULLTAG,
		itemType=NULLTAG;

	int ifail=USERARG_get_string_argument(&object_name); //项目信息
	createCostInfo(object_name,&boTag);
	ECHO("yfjs_createCostInfo boTag==%d \n ",boTag);
	*((tag_t*) returnValue) = boTag;
	return 0;
}





#ifdef __cplusplus
}
#endif