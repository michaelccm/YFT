/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

   Filename: yfjc_ebp_importProgramInfo.cxx
   Module  : user_exits

   import programInfo 
============================================================================================================
DATE           Name             Description of Change
25-Feb-2013    liuc             creation
$HISTORY$
04-Mar-2013    liuc             modification
07-Apr-2013    zhanggl          Copy project lib to project, add project team leader
13-Apr-2013    zhanggl          Search user by id
14-Apr-2013    zhanggl          Re write. Note project_id and project_name must be only
============================================================================================================*/
#include "yfjc_ebp_head.h"

// SP-RICH-FUN-12.项目信息导入工具   liuc

#ifdef __cplusplus
extern "C" {
#endif

//2016-04-15	mengyawei modify
#define CostControllerUserId	"ebp1"

 int create_dataset_without_parent(char *ds_type,char *ref_name,char *ds_name, char *fullfilename,tag_t * dataset);

int importProgramInfo( void * returnValueType )
{
	int ifail = ITK_ok;
	int length=0,i=0;
	char *dsName       = NULL,
		 *dsfilePath   = NULL;
	
	FILE   *fp			= NULL,
		   *fplog		= NULL;
	tag_t info_ds		=NULLTAG;

	char   readLine[SS_MAXMSGLEN]   = "";
	
	char *temp_dir = getenv("TC_LOG");
	char log_file[SS_MAXPATHLEN] = "";
    
	strcpy(log_file, temp_dir);
	strcat(log_file, "/importProgramInfo.txt");

	ECHO("log_file==%s \n",log_file,getNowtime2().c_str());

    ask_opt_debug();

	ifail=USERARG_get_string_argument(&dsName);
	//vector<string> log_vec;

	getDatasetFile(dsName,&dsfilePath);
	ECHO("dsfilePath==%s time:%s \n",dsfilePath,getNowtime2().c_str());
	
		

	if((fp=fopen(dsfilePath,"rt"))!=NULL)
	{	
		if((fplog=fopen(log_file,"w"))!=NULL)
		{	
			while( fgets(readLine, SS_MAXMSGLEN, fp) != NULL && tc_strcmp(readLine, "\n") != 0 )
			{	
				char str [10];
				string logstr="";
				readLine[tc_strlen(readLine)-1] = '\0';
				ECHO("readLine==%s \n",readLine);
                if(get_opt_debug() )
                {
                    logstr.append("row======");
				    sprintf(str,"%d",i);
				    logstr.append(str);
                }
				   
				string logTemStr=create(readLine);
				i++;
                logstr.append(logTemStr);
				//log_vec.push_back(logstr);
				ECHO("logstr==%s \n",logstr.c_str());
				fputs(logstr.c_str(),fplog);
			}
			fclose(fplog);
			create_dataset_without_parent("Text","Text","importProgramInfoLog",log_file,&info_ds);
		}	
	}
	if(dsName)
		MEM_free(dsName);
	 *((tag_t *) returnValueType)= info_ds;
	ECHO("info_ds==%d \n",info_ds);
    

	//  *((char**) returnValueType) = importProgramInfoLog;

	//const char ** log_arr = ( const char**)MEM_alloc(log_vec.size() * sizeof(char*));
	//for(i=0;i<log_vec.size();i++)
	//{
	//	//log_arr[i]=(char*)MEM_alloc(log_vec[i].length()*2 * sizeof(char));
	//	log_arr[i]=log_vec[i].c_str();
	//}

	//	USERSERVICE_array_t  arrStruct;
	//	USERSERVICE_return_string_array(log_arr,log_vec.size(),&arrStruct);

	//	if (arrStruct.length != 0){
	//		*((USERSERVICE_array_t*) returnValueType) = arrStruct;
	//	}

	return 0;
} 



/**
 创建项目信息并指派到项目
char * content <I> a line of Excel content 
*/
int create_old(char * content)
{
	char *prjID         =NULL,
		 *prjName       =NULL,
		 *prjDesc       =NULL,
		 *jci6_OEMName  =NULL,
		
		*jci6_ProgramSec     =NULL,
		*jci6_ProgramDivi    =NULL,
		*jci6_ProjectTL      =NULL,
		*jci6_state          =NULL,

		*jci6_ProgramState   =NULL,
		*jci6_Category       =NULL,
		*jci6_Product        =NULL,
		*jci6_Type           =NULL,
		
		*jci6_TargetPos      =NULL,
		*jci6_StartDate      =NULL,
		*jci6_EndDate        =NULL,
		*jci6_SOP            =NULL,

		*jci6_SOPFY          =NULL,
		*jci6_ModelYear      =NULL,
		*jci6_EPIC_PN        =NULL,
		*jci6_Function       =NULL,
		
		*jci6_FrontSS        =NULL,
		*jci6_RearSS         =NULL,
		*jci6_Track          =NULL,
		*jci6_Recliner       =NULL,

		*jci6_PUMP           =NULL,
		*jci6_VTA            =NULL,
		*jci6_Latch          =NULL,
		*jci6_OEMModelName   =NULL;
		
	tag_t project_tag    =NULLTAG,
		item_tag           =NULLTAG,
		rev_tag            =NULLTAG,
		user_tag           =NULLTAG,
		group_tag          =NULLTAG,
		relation_type	   =NULLTAG,
		relation		   =NULLTAG,
		*secondary_objects  =NULL,
		project_tags[1],
		item_tags[1];

	date_t   jci6_StartDateDt   = NULLDATE,
		jci6_EndDateDt		    = NULLDATE,
		jci6_SOPDt              = NULLDATE;
	int rcode					=0,
		  count					=0;

	prjID         =tc_strtok(content,VERTICALBAR);
	prjName       =tc_strtok(NULL,VERTICALBAR);
	prjDesc       =tc_strtok(NULL,VERTICALBAR); //要判断是否为NULL
	jci6_OEMName  =tc_strtok(NULL,VERTICALBAR);

	jci6_ProgramSec  =tc_strtok(NULL,VERTICALBAR);
	jci6_ProgramDivi =tc_strtok(NULL,VERTICALBAR);
	jci6_ProjectTL   =tc_strtok(NULL,VERTICALBAR);
	jci6_state       =tc_strtok(NULL,VERTICALBAR);

	jci6_ProgramState =tc_strtok(NULL,VERTICALBAR);
	jci6_Category     =tc_strtok(NULL,VERTICALBAR);
	jci6_Product      =tc_strtok(NULL,VERTICALBAR);
	jci6_Type         =tc_strtok(NULL,VERTICALBAR);

	jci6_TargetPos   =tc_strtok(NULL,VERTICALBAR);
	jci6_StartDate   =tc_strtok(NULL,VERTICALBAR);
	jci6_EndDate     =tc_strtok(NULL,VERTICALBAR);
	tc_strtok(NULL,VERTICALBAR);

	jci6_SOP       =tc_strtok(NULL,VERTICALBAR);
	jci6_SOPFY     =tc_strtok(NULL,VERTICALBAR);
	jci6_ModelYear =tc_strtok(NULL,VERTICALBAR);
	jci6_EPIC_PN   =tc_strtok(NULL,VERTICALBAR);

	jci6_Function  =tc_strtok(NULL,VERTICALBAR);
	jci6_FrontSS   =tc_strtok(NULL,VERTICALBAR);
	jci6_RearSS    =tc_strtok(NULL,VERTICALBAR);
	jci6_Track     =tc_strtok(NULL,VERTICALBAR);

	jci6_Recliner  =tc_strtok(NULL,VERTICALBAR);
	jci6_PUMP      =tc_strtok(NULL,VERTICALBAR);
	jci6_VTA       =tc_strtok(NULL,VERTICALBAR);
	jci6_Latch     =tc_strtok(NULL,VERTICALBAR);

	jci6_OEMModelName = tc_strtok(NULL,VERTICALBAR);
	
	ITKCALL( PROJ_find( prjID, &project_tag ) );
	if( project_tag == NULLTAG )
	{
		if( tc_strcmp( prjDesc, "NULL" ) == 0 )
		{
			ITKCALL(PROJ_create_project(prjID,prjName,"",&project_tag));
		}
		else
		{
			ITKCALL(PROJ_create_project(prjID,prjName,prjDesc,&project_tag));
		}
	}
	ECHO("project_tag==%d \n",project_tag);
    if(project_tag==NULLTAG)
    {
        return 0;
    }
	ITKCALL(AOM_lock ( project_tag ));
	ITKCALL( ITEM_find_item( prjID, &item_tag ) );
	if( item_tag == NULLTAG )
	{
		ITKCALL(ITEM_create_item(prjID,prjName,JCI6_PROGRAMINFO,JCI6_REVSEQINIT,&item_tag,&rev_tag));
	}
	else
	{
		ITKCALL( ITEM_ask_latest_rev( item_tag, &rev_tag ) );
	}

	ITKCALL(AOM_lock ( item_tag ));
	ITKCALL(AOM_lock ( rev_tag ));
	ITKCALL(AOM_set_value_string( item_tag, JCI6_EPIC_PN, jci6_EPIC_PN ) );
	ITKCALL(AOM_set_value_double( rev_tag, JCI6_EQU,0.00 ) );

	if(!tc_strstr(jci6_ProgramSec,"NULL"))
	{
		tag_t group_tag=NULLTAG;
		ITKCALL(SA_find_group( jci6_ProgramSec, &group_tag ) );
		ITKCALL(AOM_set_value_tag( rev_tag, JCI6_PROGRAMSEC, group_tag ) );
	}
	if(!tc_strstr(jci6_ProgramDivi,"NULL"))
	{
		tag_t group_tag=NULLTAG;
		ITKCALL(SA_find_group( jci6_ProgramDivi, &group_tag ) );
		ITKCALL(AOM_set_value_tag( rev_tag, JCI6_PROGRAMDIVI, group_tag ) );
	}
	if(!tc_strstr(jci6_ProjectTL, "NULL" ))
	{
		// Remove and add by zhanggl 13-Apr 
		//getUserByUserName(jci6_ProjectTL, &user_tag);
		ITKCALL( SA_find_user( jci6_ProjectTL, &user_tag) );
		ITKCALL(AOM_set_value_tag(rev_tag,JCI6_PROJECTTL,user_tag));
	}

	if(!tc_strstr(jci6_OEMName,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_OEMNAME,jci6_OEMName));

	if(!tc_strstr(jci6_ProgramState,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_PROGRAMSTATE,jci6_ProgramState));

	if(!tc_strstr(jci6_Product,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_PRODUCT,jci6_Product));

	if(!tc_strstr(jci6_Category,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_CATEGORY,jci6_Category));
	
	if(!tc_strstr(jci6_Type,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_TYPE,jci6_Type));

	if(!tc_strstr(jci6_StartDate,"NULL"))
		validate_date(jci6_StartDate,&jci6_StartDateDt);

	if(!tc_strstr(jci6_EndDate,"NULL"))
		validate_date(jci6_EndDate,&jci6_EndDateDt);

	if(!tc_strstr(jci6_SOP,"NULL"))
		validate_date(jci6_SOP,&jci6_SOPDt);

	if(!tc_strstr(jci6_TargetPos,"NULL"))
		ITKCALL(AOM_set_value_double( rev_tag, JCI6_TARGETPOS, strtod(jci6_TargetPos,NULL) ) );

	ITKCALL(AOM_set_value_date( rev_tag, JCI6_STARTDATE, jci6_StartDateDt ) );
	ITKCALL(AOM_set_value_date( rev_tag, JCI6_ENDDATE, jci6_EndDateDt));
	ITKCALL(AOM_set_value_date( rev_tag, JCI6_SOP, jci6_SOPDt ) );

	if(!tc_strstr(jci6_SOPFY,"NULL"))
		ITKCALL(AOM_set_value_int(rev_tag,JCI6_SOPFY,atoi(jci6_SOPFY)));

	if(!tc_strstr(jci6_ModelYear,"NULL"))
		ITKCALL(AOM_set_value_int(rev_tag,JCI6_MODELYEAR,atoi(jci6_ModelYear)));

	if(!tc_strstr(jci6_Function,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_FUNCTION,jci6_Function));

	if(!tc_strstr(jci6_FrontSS,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_FRONTSS,jci6_FrontSS));

	if(!tc_strstr(jci6_RearSS,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_REARSS,jci6_RearSS));

	if(!tc_strstr(jci6_Track,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_TRACK,jci6_Track));

	if(!tc_strstr(jci6_Recliner,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_RECLINER,jci6_Recliner));

	if(!tc_strstr(jci6_PUMP,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_PUMP,jci6_PUMP));

	if(!tc_strstr(jci6_VTA,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_VTA,jci6_VTA));	

	if(!tc_strstr(jci6_Latch,"NULL"))
		ITKCALL(AOM_set_value_string(rev_tag,JCI6_LATCH,jci6_Latch));

	if( !tc_strstr( jci6_OEMModelName, "NULL" ) )
		ITKCALL( AOM_set_value_string( rev_tag, JCI6_OEMMODELNAME, jci6_OEMModelName ) );
	
	ITKCALL(rcode=ITEM_save_item( item_tag ) );
	if(rcode)
	{
		ECHO("save item failed==%d  \n", rcode );
		return rcode;
	}
	ITKCALL(AOM_unlock ( item_tag ));
	ITKCALL(AOM_unlock ( rev_tag ));
	ITKCALL(AOM_save ( project_tag ));

	ITKCALL(GRM_find_relation_type(TC_PROGRAM_PREFERRED_ITEMS, &relation_type));
	ECHO("relation_type==%d \n",relation_type);
	ITKCALL(GRM_list_secondary_objects_only(project_tag,relation_type,&count,&secondary_objects));
	ECHO("count==%d  \n",count);
	if(count==0)
	{
		ITKCALL(GRM_create_relation(project_tag,item_tag,relation_type,NULLTAG,&relation));
		ITKCALL(AOM_save(relation));
		ITKCALL(AOM_unlock(relation));
	}
	
	ITKCALL(AOM_set_value_tag(item_tag,OWNING_PROJECT,project_tag));

	ECHO("item_tag==%d \n",item_tag);
	project_tags[0]=project_tag;
	item_tags[0]=item_tag;

	ITKCALL(PROJ_assign_objects(1,project_tags,1,item_tags));
	if(tc_strstr(jci6_state,"false"))
	{
		PROJ_activate_project(project_tag,0);
	}
	ITKCALL(AOM_save ( project_tag ));
	ITKCALL(AOM_unlock ( project_tag ));

	//Add 07-Apr by zhanggl
	int  num_of_members = 0;

	char role_name[SA_person_name_size_c + 1]  = "";

	logical has_team_leader = FALSE;
	tag_t *member_tags    = NULL,
			default_group_tag  =NULLTAG,
		  project_team    = NULLTAG,
		  role_tag        = NULLTAG,
		  admin_role_tag  = NULLTAG,
		  team_user_tag   = NULLTAG,
		  admin_member    = NULLTAG;

	ITKCALL( AOM_lock( project_tag ) );
	ITKCALL(POM_ask_user_default_group(user_tag,& default_group_tag));
	ITKCALL(SA_find_groupmembers(user_tag,default_group_tag,&num_of_members, &member_tags));
	if(num_of_members>0)
	{
		ITKCALL( PROJ_add_members( project_tag, 1, &member_tags[0]) );
		MEM_free( member_tags );
		member_tags = NULLTAG;
	}else
	{
		ITKCALL( PROJ_add_members( project_tag, 1,&user_tag) );
	}
	
	ITKCALL( PROJ_add_author_members( project_tag, 1, &user_tag) );
	ITKCALL( AOM_save( project_tag ) );
	ITKCALL( AOM_unlock( project_tag ) );
	ITKCALL( AOM_refresh( project_tag, TRUE ) );

	ITKCALL( AOM_ask_value_tag( project_tag, PROJECT_TEAM, &project_team ) );
	ECHO("project_team==%d \n",project_team);
	if( project_team )
	{
		ITKCALL( SA_find_groupmembers_by_group( project_team, &num_of_members, &member_tags ) );
		ECHO("num_of_members==%d \n",num_of_members);
		for(int jn = 0; jn < num_of_members; jn++)
		{
			/*ITKCALL( SA_ask_groupmember_role( member_tags[jn], &role_tag ) );
			ITKCALL( SA_ask_role_name( role_tag, role_name ) );
			ECHO("role_name==%s \n",role_name);
			if( tc_strcmp( role_name, JCI6_PROJECT_TEAMROLE ) == 0 )
			{
				has_team_leader = TRUE;
				break;
			}*/

			ITKCALL( SA_ask_groupmember_user( member_tags[jn], &team_user_tag ) );
			if( team_user_tag == user_tag )
			{
				admin_member = member_tags[jn];
				break;
			}
		}

		MEM_free( member_tags );
		member_tags = NULLTAG;

		//if( !has_team_leader )
		//{
			//ECHO("The project %s has not team leader\n", prjID);
			ITKCALL( SA_find_role( JCI6_PROJECT_TEAMROLE, &admin_role_tag ) );
			if( admin_role_tag && admin_member )
			{
				ITKCALL( AOM_lock( admin_member ) );
				ITKCALL( SA_set_groupmember_role( admin_member, admin_role_tag ) );
				ITKCALL( AOM_save( admin_member ) );
				ITKCALL( AOM_unlock( admin_member ) );
			}
		//}
	}
	//End add

	return 0;
}







/*
	创建数据集
   
*/

 int create_dataset_without_parent(char *ds_type,char *ref_name,char *ds_name, char *fullfilename,tag_t * dataset)
{
	int ifail = ITK_ok;
	tag_t ref_object = NULLTAG,
		datasettype = NULLTAG,
		new_ds = NULLTAG,
		tool = NULLTAG,
		folder_tag = NULLTAG;
	AE_reference_type_t reference_type;
	tag_t new_file_tag = NULLTAG;
	IMF_file_t file_descriptor;		
	char new_ds_name[WSO_name_size_c + 1] = "";
	char *new_file_name;
	tag_t relation = NULLTAG;
	tag_t   spec_relation = NULLTAG;
	char *file_ext = NULL;
	char *filename = NULL;

	if(fullfilename==NULL)
		return ITK_ok;

	file_ext = strrchr(fullfilename,'.') + 1;
	if (file_ext == NULL)
		return ITK_ok;

	filename = strrchr(fullfilename,'/') + 1;
	if (filename == NULL)
		return ITK_ok;
	new_file_name = USER_new_file_name("Text", "Text", "txt", 0);

	ITKCALL(ifail= IMF_import_file(fullfilename, new_file_name, SS_TEXT, &new_file_tag, &file_descriptor));

	if (ifail != ITK_ok)
		return ITK_ok;

	ITKCALL(IMF_set_original_file_name(new_file_tag, filename));
	ITKCALL(IMF_close_file(file_descriptor));
	ITKCALL(AOM_save(new_file_tag));
	ITKCALL(AOM_unlock(new_file_tag));

	ITKCALL( AE_find_datasettype (ds_type, &datasettype));
	ECHO("filename=%s\n",filename);

	ITKCALL( AE_create_dataset_with_id(datasettype, ds_name,
			"", "", "1", &new_ds ));
	ECHO("create dataset sucess\n");
	ITKCALL( AE_add_dataset_named_ref( new_ds, ref_name,
		AE_PART_OF, new_file_tag ) );
	ITKCALL( AOM_save( new_ds ) );
	ITKCALL( AOM_unlock( new_ds ) );
	*dataset = new_ds;

	return ITK_ok;
}

#ifdef __cplusplus
}
#endif
