/*==========================================================================================================
Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
Unpublished - All Rights Reserved
============================================================================================================
File description:

Filename: yfjc_ebp_programInfoExt.cxx
Module  : user_exits

import programInfo 
============================================================================================================
DATE           Name             Description of Change
01-Mar-2013    liuc           creation
$HISTORY$
13-Mar-2013    liuc          modification
10-Apr-2013    zhanggl          over-all-status 
14-Nov-2013    zhanggl          costType matching problem 
29-May-2014    mengyawei        programInfoRevision runtime attr 
                                YFJC_Cost_Types_Mapping   { jci6_TaskType=Resident Engineer}
============================================================================================================*/
#include <tc/tc.h>
#include "yfjc_ebp_head.h"
#include "yfjc_ebp_error.h"
#include <math.h>

//mengyawei added 2014-6-4
#define ProgramInfoType             "JCI6_ProgramInfo"
#define ProgramInfoRevType          "JCI6_ProgramInfoRevision"
#define CostInfoType                "JCI6_CostInfo"
#define ProjectList					"project_list"
#define ProgramPreferredItem		"TC_Program_Preferred_Items"
#define ScheduleType				"Schedule"
#define ScheduleTaskType			"ScheduleTask"
#define ScheduleChildList			"child_task_taglist"
//pr_total_cost的查询首选项
#define SearchActualforPRtotal		"YFJC_SearchActualforPRtotal"
#define SearchPRfroPRtotal			"YFJC_SearchPRforPRtotal"

void get_month_last_date(date_t month_cur_date , date_t * monthLastDate);
tag_t get_program_info_go_schdule_task( tag_t program_info );
int calculation_attr_map(tag_t * secondary_objects,int count,map< string , map< string , string > > attr_map,double * value);
int calculation_go_task(tag_t * secondary_objects,int count, tag_t go_task , map< string , map< string , string > > attr_map,double * value);
int calculation_go_task_resident_engineer(tag_t * secondary_objects,int count, tag_t go_task , map< string , map< string , string > > attr_map,double * value);
int calculation_pr_total(char * program_id , char * qry_name , char * cpt_value ,char * unit_value ,  double * value);
//mengyawei added
int calculation(tag_t * secondary_objects,int count,char * unit,char * costType,char * costPhaseType,double * value);
int searchScheduleTask(char* project_id,char * gageType,int  * count,tag_t ** obj_tags);

// SP-EXT-FUN-05.“项目信息”上的成本汇总信息
int  getCostTypeStatValue(METHOD_message_t* msg, va_list args){
    double value=0.00;
    int ifail							= ITK_ok,
        preCount						= 0,
        count							= 0,
        i								= 0;
    tag_t item_tag						= NULLTAG,
        item_rev_tag					= NULLTAG,
        *primary_objects				= NULLTAG,
        relation_type					= NULLTAG,
        *secondary_objects			    = NULLTAG,
        prop_tag						= NULLTAG;

    const char	*   preference_cpt_name ="YFJC_Cost_Phase_Types_Mapping",
                *   preference_ct_name  ="YFJC_Cost_Types_Mapping";
    char * propName					=NULL,
        propNameBefore[20]			="",    //属性下杠前面值，与首选项匹配找到费用阶段类型
        propNameBack[30]			="",    //属性下杠后面值，与首选项匹配找到费用类型
        **preValue					=NULL,
        costPhaseType[512]			=""	,   //费用阶段类型
        costType[200]				=""	,   //费用类型
        unit[20]					=""	;   //费用单位
    tag_t project_tag = NULLTAG;
    logical is_active = true;

	//mengyawei added started 2014-6-5
	map< string , map< string , string > > attr_map;//map< attr_name , map< attr_value , attr_value > >
	char tmp_prop_name[64] = {'\0'};
	char tmp_str_buf[1024] = {'\0'};
	char attr_name_values[1024] = {'\0'};
	char attr_name_value[512] = {'\0'};
	char attr_name[256] = {'\0'};
	char tmp_att_value[256] = {'\0'};
	char attr_value[512] = {'\0'};
	int attr_cnt = 0;
	char ** attrs = NULL;
	int value_cnt = 0;
	char ** values = NULL;

	
	//for
	int j = 0;
	int k = 0;

	//cpt
	string cpt_key ;
	map< string , string > cpt_value_map;

	//go_task
	tag_t go_task = NULLTAG;

	string datetime;

	//mengyawei added ended


    double * prop_value =NULL;
    long double returnValue=0.00;
    prop_tag = va_arg(args,tag_t );
    prop_value=va_arg(args, double*);
    ask_opt_debug();


	

    ECHO("getCostTypeStatValue===========start %s\n",getNowtime2().c_str());
    item_tag = msg->object_tag;

    ITKCALL(PROP_ask_name(prop_tag,&propName));
	  ECHO("propName==========%s:%f\n",propName,prop_value);
	//如果是jci6_pr_total_cost的话
	//另行处理
	if(!tc_strcmp(propName,"jci6_pr_total_cost"))
	{
		char * programInfo_id = NULL;
		ITKCALL(AOM_ask_value_string( item_tag , "item_id" , &programInfo_id));
		ECHO("item_tag==========%d\n",item_tag);
		ECHO("---->		programInfo_id = %s\n",programInfo_id);
		calculation_pr_total(programInfo_id , SearchActualforPRtotal ,"Actual","Yuan", &value);
		ECHO("------>		@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@:%f\n",value);
		calculation_pr_total(programInfo_id , SearchPRfroPRtotal ,"Actual_PR","Yuan", &value);
		if(programInfo_id!=NULL)
		{
			MEM_free(programInfo_id);
			programInfo_id = NULL;
		}
	}else{

		 ECHO("--sscanf() ---\n");
		 ECHO("item_tag==========%d\n",item_tag);
		 sscanf( propName, "jci6_%[^_]_%s", propNameBefore,propNameBack );
		 ECHO("propNameBefore ==%s propNameBack==%s\n",propNameBefore,propNameBack);

    ITKCALL(ifail=PREF_ask_char_values(preference_cpt_name,&preCount,&preValue));
    if(ifail)
    {	
        EMH_store_initial_error_s1( EMH_severity_user_error, ERROR_PREFERENCE_NOT_FOUND,preference_cpt_name);
        ifail =ERROR_PREFERENCE_NOT_FOUND;
        goto GOCON;
    }
	//mengyawei modify
	cpt_key.assign("jci6_CPT");
    for(i=0;i<preCount;i++)
    {	
	//	attr_name
	//	tmp_att_value

		 ECHO("  sscanf(preValue[i],\"%[^=]=%[^=]\",attr_name,attr_value) \n");
		sscanf(preValue[i],"%[^=]=%[^=]",attr_name,attr_value);
		 ECHO("  attr_value-->%s \n",attr_value);

		tc_strcpy(costPhaseType,attr_value);
		//ECHO("---->		attr_name = %s  attr_value = %s" , attr_name , attr_value );
        if(tc_strcmp(attr_name,propNameBefore)==0)
        {
			is_active = TRUE;
            ITKCALL( EPM__parse_string( attr_value, ",", &value_cnt , &values) );
			
			if((!tc_strcmp(attr_name , "eac"))&&(!tc_strcmp(attr_value , "Forecast,Actual")))
			{
				//ECHO("---->		11111\n");
				ITKCALL(AOM_ask_value_tag_at(item_tag, "project_list", 0, &project_tag));
				if(project_tag != NULLTAG)
				{
					ITKCALL(PROJ_is_project_active(project_tag, &is_active));
				}
			}
			for(i = 0; i < value_cnt ; i++)
			{
				tc_strcpy( tmp_att_value , values[i] );
				//ECHO("---->		values[%d] = %s     11\n",i , tmp_att_value);
				if((!is_active)&&(!tc_strcmp(tmp_att_value , "Forecast")))
				{
					continue;
				}
				string tmp_value;
				tmp_value.assign( tmp_att_value );
				cpt_value_map.insert(pair<string , string>( tmp_value , tmp_value ));
			}
			attr_map.insert(pair< string , map< string , string > >( cpt_key , cpt_value_map ));
			if(values)
			{
				MEM_free(values);
				values = NULL;
			}
            break;
        }
    }
	//mengyawei added
    if(preValue)
    {
        MEM_free(preValue);
        preValue=NULL;
    }

    ITKCALL(ifail=PREF_ask_char_values(preference_ct_name,&preCount,&preValue));
    if(ifail)
    {
        EMH_store_initial_error_s1( EMH_severity_user_error, ERROR_PREFERENCE_NOT_FOUND,preference_ct_name);
        ifail =ERROR_PREFERENCE_NOT_FOUND;
		ECHO("--- ifail =ERROR_PREFERENCE_NOT_FOUND \n" );
        goto GOCON;
    }

    //mengyawei modify started
	
	for( i = 0 ; i < preCount ; i ++)
	{
		//ECHO("--->	preValue[%d]=%s\n" , i , preValue[i] );
		sscanf( preValue[i] , "%[^=]" , tmp_prop_name);
		//ECHO("--->		tmp_prop_name = %s\n" , tmp_prop_name );
		if(!tc_strcmp( tmp_prop_name , propNameBack))
		{
			
			tc_strcpy( attr_name_values , (tc_strstr( preValue[i] , "=" ) +1 ));
			if(tc_strcmp(propNameBack,"labor_hc"))
				ECHO("--->	attr_name_values = %s\n" , attr_name_values );
			
			
			ITKCALL( EPM__parse_string( attr_name_values, "{", &attr_cnt , &attrs) );
			for( j = 0; j < attr_cnt ; j ++)
			{
				//ECHO("--->	attrs[%d] = %s\n" , j , attrs[j] );
				if(tc_strlen(attrs[j])==0)
					continue;
				
				sscanf(attrs[j] , "%[^=]=%[^}]",attr_name,attr_value);
				if(tc_strcmp(propNameBack,"labor_hc"))
					ECHO("--->		attr_name = %s attr_value = %s\n" , attr_name , attr_value );
			
				string att_key;
				att_key.assign(attr_name);
				map< string , string > attr_value_map;

				ITKCALL( EPM__parse_string( attr_value, ",", &value_cnt , &values) );
				for( k = 0; k < value_cnt ; k ++)
				{
					tc_strcpy(tmp_att_value , values[k]);
					//ECHO("--->	values[%d] = %s\n" , k , tmp_att_value );
					string att_value;
					att_value.assign(tmp_att_value);
					//ECHO("--->	att_value.c_str() = %s",att_value.c_str());
					attr_value_map.insert(pair< string , string >( att_value , att_value ));
				}
				//ECHO("---->		attr_value_map.size() = %d \n" , attr_value_map.size());
				attr_map.insert(pair<string , map< string , string > >( att_key , attr_value_map ));
				if(values)
				{
					MEM_free(values);
					values = NULL;
				}
				
			}
			if(attrs)
			{
				MEM_free(attrs);
				attrs = NULL;
			}
			break;
		}
		

	}//for( i = 0 ; i < preCount ; i ++)

    if(preValue)
    {
        MEM_free(preValue);
        preValue=NULL;
    }

    ITKCALL(GRM_find_relation_type(IMAN_EXTERNAL_OBJECT_LINK,&relation_type));
	//ITKCALL(GRM_find_relation_type("IMAN_reference",&relation_type));

	//ECHO("--->		costPhaseType = %s\n" , costPhaseType );

	go_task = get_program_info_go_schdule_task( item_tag );


    if(tc_strcmp(costPhaseType,JCI6_Budget)==0)
    {	
        tag_t * rev_list		=NULL;
        int rev_count			=0;


        ITKCALL(ITEM_list_all_revs(item_tag,&rev_count,&rev_list));
        for(i=0;i<rev_count;i++)
        {	
            char * budgetState	=NULL;
            ITKCALL(AOM_ask_value_string(rev_list[i],"jci6_BudgetState",&budgetState));
            if(budgetState && tc_strcmp(budgetState,"State4")==0)
            {
				
				//ECHO("---->		propName[tc_strlen(propName)-1] = %c \n" , propName[tc_strlen(propName)-1]);
				ITKCALL(GRM_list_secondary_objects_only(rev_list[i],relation_type,&count,&secondary_objects));
				if( propName[tc_strlen(propName)-1] == '2' )
				{
					
					if(go_task!=NULLTAG)
					{
						calculation_go_task( secondary_objects,count,go_task , attr_map,&value);
					}else{
						ECHO("111---->	未找到 GO 门！\n");
					}

				}else if(propName[tc_strlen(propName)-1] == '3')
				{
					if(go_task!=NULLTAG)
					{
						calculation_go_task_resident_engineer( secondary_objects,count,go_task , attr_map,&value);
					}else{
						ECHO("222---->	未找到 GO 门！\n");
					}
				}
				else{
					
					//calculation(secondary_objects,count,unit,costType,costPhaseType,&value);
					calculation_attr_map(secondary_objects,count,attr_map,&value);
					
				} 
				if(secondary_objects)
				{
					MEM_free(secondary_objects);
					secondary_objects = NULL;
				}
            }
            if( budgetState )
            {
                MEM_free( budgetState );
                budgetState = NULL;
            }
        }
        if( rev_list )
        {
            MEM_free( rev_list );
            rev_list = NULL;
        }
    }
    else
    {
		//ECHO("---->		propName[tc_strlen(propName)-1] = %c \n" , propName[tc_strlen(propName)-1]);
		ITKCALL(GRM_list_secondary_objects_only(item_tag,relation_type,&count,&secondary_objects));
			
		if(propName[tc_strlen(propName)-1] == '2' )
		{
			
			if(go_task != NULLTAG)
			{
				calculation_go_task( secondary_objects,count, go_task , attr_map , &value );

			}else{
				ECHO("333--->	未找到 GO门！\n");
			}
		}
		else if(propName[tc_strlen(propName)-1] == '3')
		{
			
			if(go_task != NULLTAG)
			{
				calculation_go_task_resident_engineer( secondary_objects,count, go_task , attr_map , &value );
			}else{
				ECHO("444--->	未找到 GO门！\n");
			}
		}
		else{
			
			calculation_attr_map(secondary_objects,count,attr_map,&value);
			//calculation(secondary_objects,count,unit,costType,costPhaseType,&value);
		}
		if(secondary_objects)
		{
			MEM_free(secondary_objects);
			secondary_objects = NULL;
		}
        
    }
	}
	
    //ITKCALL( PROP_set_value_double( prop_tag, value ) );
	

	ECHO("lala value-->%lf \n",value );
    soa_DataPrecision(value,&returnValue);
    ECHO("returnValue=%lf \n",returnValue);
    *prop_value = returnValue;
GOCON: 
   /* if(preValue)
    {
        MEM_free(preValue);
        preValue=NULL;
    }*/

	
    ECHO("ww getCostTypeStatValue===========end  %s \n",getNowtime2().c_str());

    return ifail;
}

//2014-6-18
//根据qry_name 查找到到对应的costinfo对象直接将这些costinfo进行相加操作
//char * program_id(I)
//char * qry_name(I)
//char * cpt_value(I)
//char * unit_value(I)
//double * value(O)
int calculation_pr_total(char * program_id , char * qry_name , char * cpt_value ,char * unit_value ,  double * value)
{
	int ifail = ITK_ok;
	tag_t qry_tag = NULLTAG;
	//describe_query
	int num_clauses = 0;
    char** attr_names = NULL;             
    char** entry_names = NULL;
    char** logical_ops = NULL;
    char** math_ops = NULL;
    char** values = NULL;
	tag_t* lov_tags = NULL;
    int* attr_types = NULL;
	//qry execute
	char ** qry_entries = NULL;
	char ** qry_values = NULL;
	int result_cnt = 0;
	tag_t * results = NULL;
	//for
	int i = 0;
	//cost info
	tag_t tmp_cost_info = NULLTAG;
	char * tmp_cpt_value = NULL;
	char * tmp_unit_value = NULL;
	double tmp_double_value = 0;
	double tmp_sum = 0;

	string datetime;

	

	ECHO("--------------------calculation_pr_total started  %s---------------\n",getNowtime2().c_str());
	ECHO("---->		program_id = %s  qry_name = %s\n" , program_id , qry_name );
	ITKCALL(QRY_find(qry_name , &qry_tag));
	if(qry_tag!=NULLTAG)
	{
		ECHO("---->		找到查询：%s\n",qry_name);
		ITKCALL(QRY_describe_query( qry_tag , &num_clauses , &attr_names , &entry_names , &logical_ops , &math_ops , &values , &lov_tags , &attr_types ));
		ECHO("---->		num_clauses = %d\n",num_clauses);
		qry_entries = (char **)MEM_alloc(sizeof(char)*2);
		qry_values = (char **)MEM_alloc(sizeof(char)*2);
		for(i = 0; i < 2 ; i ++)
		{
			qry_entries[i] = (char *)MEM_alloc(sizeof(char)*256);
			qry_values[i] = (char *)MEM_alloc(sizeof(char)*256);
		}
		for( i = 0 ;i < num_clauses ; i ++)
		{
			ECHO("attr_names[%d] = %s\n" , i , attr_names[i] );
			ECHO("entry_names[%d] = %s\n" , i , entry_names[i]);
			ECHO("values[%d] = %s\n" , i , values[i]);
			
			
			if(tc_strstr(attr_names[i],"item_id")!=NULL)
			{
				tc_strcpy(qry_entries[0],entry_names[i]);
				tc_strcpy(qry_values[0],program_id);
				ECHO("---->		qry_entries[0] = %s\n",qry_entries[0] );
				ECHO("---->		qry_values[0] = %s\n",qry_values[0]);
			}/*
			else if(!tc_strcmp(attr_names[i],"jci6_CPT"))
			{
				tc_strcpy(qry_entries[1],entry_names[i]);
				tc_strcpy(qry_values[1],"Actual");
				ECHO("---->		qry_entries[1] = %s\n",qry_entries[1] );
				ECHO("---->		qry_values[1] = %s\n",qry_values[1]);
			}else if(!tc_strcmp(attr_names[i],"jci6_Unit"))
			{
				tc_strcpy(qry_entries[2],entry_names[i]);
				tc_strcpy(qry_values[2],"Yuan");
				ECHO("---->		qry_entries[2] = %s\n",qry_entries[2] );
				ECHO("---->		qry_values[2] = %s\n",qry_values[2]);
			}*/
			else if(!tc_strcmp(attr_names[i],"jci6_CostType"))
			{
				tc_strcpy(qry_entries[1],entry_names[i]);
				tc_strcpy(qry_values[1],values[i]);
				ECHO("---->		qry_entries[1] = %s\n",qry_entries[1] );
				ECHO("---->		qry_values[1] = %s\n",qry_values[1]);
			}
			
		}
		ITKCALL(QRY_execute(qry_tag,2,qry_entries,qry_values,&result_cnt,&results));
		ECHO("---->		result_cnt = %d\n",result_cnt);

		for( i = 0; i < result_cnt ; i ++)
		{
			tmp_cost_info = results[i];
			ITKCALL(AOM_ask_value_string( tmp_cost_info , "jci6_CPT" , &tmp_cpt_value));
			ITKCALL(AOM_ask_value_string( tmp_cost_info , "jci6_Unit" , &tmp_unit_value));
			ECHO("--->			I = %d\n",i);
			ECHO("--->		jci6_CPT = %s\n",tmp_cpt_value);
			ECHO("--->		jci6_Unit = %s\n",tmp_unit_value);
			if((!tc_strcmp(tmp_cpt_value ,cpt_value))&&(!tc_strcmp(tmp_unit_value , unit_value)))
			{
				AOM_ask_value_double( tmp_cost_info,JCI6_JAN,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_FEB,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_MAR,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_APR,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_MAY,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_JUN,&tmp_double_value);
			    tmp_sum+=tmp_double_value;

			    AOM_ask_value_double( tmp_cost_info,JCI6_JUL,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_AUG,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_SEP,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_OCT,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_NOV,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			    AOM_ask_value_double( tmp_cost_info,JCI6_DEC,&tmp_double_value);
			    tmp_sum+=tmp_double_value;
			}
			
			
		}

		ECHO("----->				free result !\n");
		if(results)
		{
			MEM_free(results);
			results = NULL;
		}
		for(i = 0; i < 2; i ++)
		{
			MEM_free(qry_entries[i]);
			MEM_free(qry_values[i]);
		}
		if(qry_entries)
		{
			MEM_free(qry_entries);
			qry_entries = NULL;
		}
		if(qry_values)
		{
			MEM_free(qry_values);
			qry_values = NULL;
		}
		if(attr_names)
		{
			MEM_free(attr_names);
			attr_names = NULL;
		}
		if(entry_names)
		{
			MEM_free(entry_names);
			entry_names = NULL;
		}
		if(logical_ops)
		{
			MEM_free(logical_ops);
			logical_ops = NULL;
		}
		if(math_ops)
		{
			MEM_free(math_ops);
			math_ops = NULL;
		}
		if(values)
		{
			MEM_free(values);
			values = NULL;
		}
		if(lov_tags)
		{
			MEM_free(lov_tags);
			lov_tags = NULL;
		}
		if(attr_types)
		{
			MEM_free(attr_types);
			attr_types = NULL;
		}
	}else{
		ECHO("--->	系统中不存在查询：%s\n",qry_name);
	}

	*value+=tmp_sum ;

	
	ECHO("--------------------calculation_pr_total ended  %s------------\n",getNowtime2().c_str());
	return ifail;
}

/**
计算费用信息  
tag_t * secondary_objects <I>        costInfos 
int count <I>						  count of costInfos 
char * unit <I>				          value of unit
char * costType <I>			          value of costType
char * costPhaseType <I>			  value of costPhaseType
double * value <O>                    value
*/

int calculation(tag_t * secondary_objects,int count,char * unit,char * costType,char * costPhaseType,double * value)
{	
    double reValue=0.00;
    int i=0;
    char costPhaseTypeStr[512]="";

    //Add by zhanggl 2013-11-14
    int cost_type_cnt = 0;
    char **cost_types = NULL;
    if( costType && tc_strcmp(costType,"") != 0 )
    {
        ITKCALL( EPM__parse_string( costType, ",", &cost_type_cnt, &cost_types) );
    }
    //END
    for(i=0;i<count;i++)
    {
        date_t released_date;
        char * unitTemp					=NULL,
            * costTypeTemp				=NULL,
            * costPhaseTypeTemp		=NULL;

        double valueTemp				=0.00;
        bool  flag						=	0;

        ITKCALL(AOM_ask_value_string(secondary_objects[i],"jci6_Unit",&unitTemp));
        ITKCALL(AOM_ask_value_string(secondary_objects[i],"jci6_CPT",&costPhaseTypeTemp));
        ITKCALL(AOM_ask_value_string(secondary_objects[i],"jci6_CostType",&costTypeTemp));
        //ITKCALL(AOM_ask_value_date(secondary_objects[i],"date_released",&released_date));

        if(tc_strcmp(unitTemp,unit)==0 )
        {	
            bool cptFlag=0;
            if(tc_strstr(costPhaseType,","))
            {
                 char * cpt_str =NULL;
                 tc_strcpy(costPhaseTypeStr,costPhaseType);
                 cpt_str= tc_strtok(costPhaseTypeStr,",");
                 while(cpt_str)
                 {
                    if(tc_strcmp(cpt_str,costPhaseTypeTemp)==0)
                    {
                        cptFlag=1;
                        break;
                    }
                    cpt_str= tc_strtok(NULL,",");

                  }
            }
            else
            {
                if(tc_strcmp(costPhaseType,costPhaseTypeTemp)==0)
                {
                    cptFlag=1 ;
                }
            }

            if(cptFlag==1)  //if(tc_strstr(costPhaseType,costPhaseTypeTemp))
            {
                //Remove by zhanggl 2013-11-14
                /*if(  tc_strcmp(costType,"")==0 || tc_strstr(costType,costTypeTemp)  )
                {
                    flag=1;
                }*/
                //END
                //Add by zhanggl 2013-11-14
                if(  tc_strcmp(costType,"") == 0 )
                {
                    flag=1;
                }
                else if( cost_types )
                {
                    for(int x_cnt = 0; x_cnt < cost_type_cnt; x_cnt++ )
                    {
                        if( tc_strcmp( costTypeTemp, cost_types[x_cnt] ) == 0)
                        {
                            flag = 1;
                            break;
                        }
                    }
                }
                //END
            }
        }	

        if(flag)
        {	
            double valueTemp=0.0;
            if(tc_strcmp(costPhaseTypeTemp,"Actual")==0 || tc_strcmp(costPhaseTypeTemp,"Budget")==0 || tc_strcmp(costPhaseTypeTemp,"Actual_PR")==0)
            {
                //if(released_date.year==0)
                //		continue;
            }

            AOM_ask_value_double( secondary_objects[i],JCI6_JAN,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_FEB,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_MAR,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_APR,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_MAY,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_JUN,&valueTemp);
            reValue+=valueTemp;

            AOM_ask_value_double( secondary_objects[i],JCI6_JUL,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_AUG,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_SEP,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_OCT,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_NOV,&valueTemp);
            reValue+=valueTemp;
            AOM_ask_value_double( secondary_objects[i],JCI6_DEC,&valueTemp);
            reValue+=valueTemp;

        }

    }	
    *value+=reValue;

    if( cost_types )
    {
        MEM_free( cost_types );
        cost_types = NULL;
    }
    return 0;
}


/**
	2014-6-4 mengyawei modify
计算费用信息  
tag_t * secondary_objects <I>        costInfos 
int count <I>						  count of costInfos 
map< string , map< string , string > > <I>		 map<attr_name , map< attr_value , attr_value > >
double * value <O>                    value
*/

int calculation_attr_map(tag_t * secondary_objects,int count,map< string , map< string , string > > attr_map,double * value)
{	
    double reValue=0.00;
    tag_t sec_object_type = NULLTAG;
	char sec_object_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

	char tmp_attr_name[TCTYPE_class_name_size_c+1] = {'\0'};
	char * tmp_attr_value = NULL;
	
	//for
	int i = 0;
	tag_t tmp_cost_info = NULLTAG;

	logical attr_value_not_in_map = FALSE;
	
	map< string , map< string , string > >::iterator attr_map_ite;

	string datetime;

	
	ECHO("方法calculation_attr_map-----开始 %s\n" , getNowtime2().c_str() );

	ECHO("---->		secondary_objects[%d] .object_type = %s\n" , i , sec_object_class_name );
	
	for( i = 0 ; i < count; i ++)
	{
		attr_value_not_in_map = FALSE;
		tmp_cost_info = secondary_objects[i] ;
		ITKCALL (TCTYPE_ask_object_type( tmp_cost_info , &sec_object_type ));
		ITKCALL (TCTYPE_ask_class_name( sec_object_type , sec_object_class_name));
		//ECHO("---->		secondary_objects[%d] .object_type = %s\n" , i , sec_object_class_name );
		if(!tc_strcmp( sec_object_class_name , CostInfoType ))
		{
			//ECHO("--->		cost info -------------\n");
			for( attr_map_ite = attr_map.begin(); attr_map_ite != attr_map.end(); attr_map_ite ++)
			{
				tc_strcpy( tmp_attr_name , attr_map_ite->first.c_str());
				//ECHO("--->		tmp_attr_name = %s\n" , tmp_attr_name );
				map< string , string > value_map = attr_map_ite->second;
				//ECHO("--->	attr_map_ite->second.size() = %d",attr_map_ite->second.size());
				map< string , string >::iterator value_map_ite;
				/*
				for( value_map_ite = attr_map_ite->second.begin(); value_map_ite != attr_map_ite->second.end(); value_map_ite ++)
				{
					ECHO("value_map_ite->first.c_str() = %s , value_map_ite->second.c_str()=%s " , value_map_ite->first.c_str() , value_map_ite->second.c_str() );
				}*/
				//value_map.find();

				ITKCALL (AOM_ask_value_string( tmp_cost_info , tmp_attr_name , &tmp_attr_value ));
				ECHO("---->	JCI6_CostInfo[%d]  属性%s的值为%s \n" , i , tmp_attr_name , tmp_attr_value);

				value_map_ite = value_map.find(tmp_attr_value);
				if(value_map_ite == value_map.end())
				{
					ECHO("---->	不在首选项集合里，不做处理！\n");
					attr_value_not_in_map = TRUE;
					if(tmp_attr_value)
					{
						MEM_free(tmp_attr_value);
						tmp_attr_value = NULL;
					}
					break;
				}
				//else{}
				if(tmp_attr_value)
				{
					MEM_free(tmp_attr_value);
					tmp_attr_value = NULL;
				}
			}
			if(!attr_value_not_in_map)
			{
				double valueTemp=0.0;
            
				AOM_ask_value_double( tmp_cost_info,JCI6_JAN,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_FEB,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_MAR,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_APR,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_MAY,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_JUN,&valueTemp);
				reValue+=valueTemp;

				AOM_ask_value_double( tmp_cost_info,JCI6_JUL,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_AUG,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_SEP,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_OCT,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_NOV,&valueTemp);
				reValue+=valueTemp;
				AOM_ask_value_double( tmp_cost_info,JCI6_DEC,&valueTemp);
				reValue+=valueTemp;

				//ECHO("--->	valueTemp = %lf    ---->		reValue = %lf \n" , valueTemp , reValue );
			}
		}
		
	}

    *value+=reValue;


	
	ECHO("方法calculation_attr_map-----结束 %s\n" , getNowtime2().c_str() );

    return 0;
}

/**
	2014-6-4 mengyawei modify
计算费用信息  
tag_t * secondary_objects <I>        costInfos 
int count <I>						  count of costInfos 
map< string , map< string , string > > <I>		 map<attr_name , map< attr_value , attr_value > >
double * value <O>                    value
*/

int calculation_go_task(tag_t * secondary_objects,int count, tag_t go_task , map< string , map< string , string > > attr_map,double * value)
{	
    double reValue=0.00;
    tag_t sec_object_type = NULLTAG;
	char sec_object_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

	char tmp_attr_name[TCTYPE_class_name_size_c+1] = {'\0'};
	char * tmp_attr_value = NULL;

	const char * costinfo_month_attr[12] = { JCI6_JAN , JCI6_FEB , JCI6_MAR , JCI6_APR , JCI6_MAY , JCI6_JUN , JCI6_JUL , JCI6_AUG , JCI6_SEP , JCI6_OCT , JCI6_NOV , JCI6_DEC };
	
	//for
	int i = 0;
	int j = 0;
	tag_t tmp_cost_info = NULLTAG;

	logical attr_value_not_in_map = FALSE;
	
	map< string , map< string , string > >::iterator attr_map_ite;
	
	//costinfo year
	int cost_info_year = 0;

	
	//go_task
	tag_t tccalendar = NULLTAG;
	date_t go_start_date;
	date_t go_month_end_date;

	char month_start_date[32] = {'\0'};
	char month_end_date[32] = {'\0'};
	char month_cur_date[32] = {'\0'};

	int cur_month = 0;

	int month_no_work_day = 0;
	int month_work_day = 0;
	int cur_no_work_day = 0;
	int cur_work_day = 0;

	char ** nonWorkingDates = NULL;

	string datetime;


	
	ECHO("%s--calculation_go_task-----开始 \n" , getNowtime2().c_str() );

							
	ITKCALL( AOM_ask_value_date( go_task , "start_date" , &go_start_date ));
	get_month_last_date( go_start_date , &go_month_end_date );
	cur_month = go_start_date.month;
	sprintf( month_start_date , "%4d-%2d-%2d 00:00:00" , go_start_date.year , go_start_date.month , 1 );
	sprintf( month_cur_date , "%4d-%2d-%2d 00:00:00" , go_start_date.year , go_start_date.month , go_start_date.day );
	sprintf( month_end_date , "%4d-%2d-%2d 00:00:00" , go_month_end_date.year , go_month_end_date.month , go_month_end_date.day );

	//ECHO("---->		month_start_date = %s\n" , month_start_date );
	//ECHO("---->		month_cur_date = %s\n" , month_cur_date );
	//ECHO("---->		month_end_date = %s\n" , month_end_date );
	//ITKCALL( TCCALENDAR_get_schedule_tccalendar( go_task , &tccalendar ) );//tccalendar

	
	
	ECHO("%s--调用日历API-----开始 \n" , getNowtime2().c_str() );

	ITKCALL( TCCALENDAR_get_base_tccalendar( &tccalendar ) );
	ITKCALL( TCCALENDAR_get_resource_non_working_dates( tccalendar , month_start_date , month_end_date , &month_no_work_day , &nonWorkingDates ) );
	
	
	ECHO("%s--调用日历API-----结束 \n" , getNowtime2().c_str() );
	
	//ECHO("---->		month_no_work_day = %d \n" , month_no_work_day );
	month_work_day = go_month_end_date.day - month_no_work_day;
	//ECHO("---->		%d月份的工作日为：%d\n" , go_month_end_date.month+1 , month_work_day );
	if(nonWorkingDates)
	{
		MEM_free(nonWorkingDates);
		nonWorkingDates = NULL;
	}


	
	ECHO("%s--TCCALENDAR_get_resource_non_working_datesAPI-----开始 \n" , getNowtime2().c_str() );

	ITKCALL( TCCALENDAR_get_resource_non_working_dates( tccalendar , month_cur_date , month_end_date , &cur_no_work_day , &nonWorkingDates ) );
	
	
	ECHO("%s--TCCALENDAR_get_resource_non_working_datesAPI-----结束 \n" , getNowtime2().c_str() );
	
	
	//ECHO("---->		cur_no_work_day = %d\n" , cur_no_work_day );
	cur_work_day = go_month_end_date.day - go_start_date.day - cur_no_work_day;
	//ECHO("---->		%d月份GO门后的工作日为：%d\n" , go_month_end_date.month + 1 , cur_work_day );
	if(nonWorkingDates)
	{
		MEM_free(nonWorkingDates);
		nonWorkingDates = NULL;
	}


	for( i = 0 ; i < count; i ++)
	{
		attr_value_not_in_map = FALSE;
		tmp_cost_info = secondary_objects[i] ;
		ITKCALL (TCTYPE_ask_object_type( tmp_cost_info , &sec_object_type ));
		ITKCALL (TCTYPE_ask_class_name( sec_object_type , sec_object_class_name));
		//ECHO("---->		secondary_objects[%d] .object_type = %s\n" , i , sec_object_class_name );
		if(!tc_strcmp( sec_object_class_name , CostInfoType ))
		{
			//ECHO("--->		cost info -------------\n");
			for( attr_map_ite = attr_map.begin(); attr_map_ite != attr_map.end(); attr_map_ite ++)
			{
				tc_strcpy( tmp_attr_name , attr_map_ite->first.c_str());
				//ECHO("--->		tmp_attr_name = %s\n" , tmp_attr_name );
				map< string , string > value_map = attr_map_ite->second;
				//ECHO("--->	attr_map_ite->second.size() = %d",attr_map_ite->second.size());
				map< string , string >::iterator value_map_ite;
				/*
				for( value_map_ite = attr_map_ite->second.begin(); value_map_ite != attr_map_ite->second.end(); value_map_ite ++)
				{
					ECHO("value_map_ite->first.c_str() = %s , value_map_ite->second.c_str()=%s " , value_map_ite->first.c_str() , value_map_ite->second.c_str() );
				}*/
				//value_map.find();

				ITKCALL (AOM_ask_value_string( tmp_cost_info , tmp_attr_name , &tmp_attr_value ));
				ECHO("---->	JCI6_CostInfo[%d]  属性%s的值为%s \n" , i , tmp_attr_name , tmp_attr_value);

				value_map_ite = value_map.find(tmp_attr_value);
				if(value_map_ite == value_map.end())
				{
					ECHO("---->	不在首选项集合里，不做处理！\n");
					attr_value_not_in_map = TRUE;
					if(tmp_attr_value)
					{
						MEM_free(tmp_attr_value);
						tmp_attr_value = NULL;
					}
					break;
				}
				//else{}
				if(tmp_attr_value)
				{
					MEM_free(tmp_attr_value);
					tmp_attr_value = NULL;
				}
			}
			if(!attr_value_not_in_map)
			{
				double valueTemp=0.0;

				//JCI6_YEAR
				ITKCALL( AOM_ask_value_int( tmp_cost_info , JCI6_YEAR , &cost_info_year ));

				//ECHO("---->		costInfo . jci6_Year = %d\n" , cost_info_year );

				if(cost_info_year == go_start_date.year )
				{
					AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[cur_month] , &valueTemp );
					//ECHO("--->			costinfo_month_attr[%d] = %s\n" , cur_month , costinfo_month_attr[cur_month] );
					//ECHO("11111---->			valueTemp = %lf\n" , valueTemp );
					if(!((abs(valueTemp)<1e-15)&&(month_work_day==0)&&(cur_work_day==0)))
					{
						reValue+= valueTemp/month_work_day*cur_work_day;
					}
					//ECHO("--->	reValue = %lf \n" , reValue );


					for(j = cur_month +1 ; j < 12 ; j++)
					{

						AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[j] , &valueTemp );
						//ECHO("---->		costinfo_month_attr[%d] = %s \n" , j , costinfo_month_attr[j]);
						//ECHO("---->		valueTemp = %lf \n" , valueTemp );
						reValue+=valueTemp;

					}
				}else if(cost_info_year > go_start_date.year){
					for(j = 0 ; j < 12 ; j++)
					{

						AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[j] , &valueTemp );
						//ECHO("---->		costinfo_month_attr[%d] = %s \n" , j , costinfo_month_attr[j]);
						//ECHO("---->		valueTemp = %lf \n" , valueTemp );
						reValue+=valueTemp;
					}
				}

				//ECHO("----->		reValue = %lf \n"  , reValue );
			}
		}
	}

	//ECHO("--->	aaaaaaaa			reValue = %lf\n" , reValue );

	
	ECHO("%s--calculation_go_task-----结束 \n" , getNowtime2().c_str() );


    *value+=reValue;

    return 0;
}

/**
	2016-4-12 mengyawei modify
计算费用信息  
tag_t * secondary_objects <I>        costInfos 
int count <I>						  count of costInfos 
map< string , map< string , string > > <I>		 map<attr_name , map< attr_value , attr_value > >
double * value <O>                    value
*/

int calculation_go_task_resident_engineer(tag_t * secondary_objects,int count, tag_t go_task , map< string , map< string , string > > attr_map,double * value)
{	

    double reValue=0.00;
    tag_t sec_object_type = NULLTAG;
	char sec_object_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

	char tmp_attr_name[TCTYPE_class_name_size_c+1] = {'\0'};
	char * tmp_attr_value = NULL;

	const char * costinfo_month_attr[12] = { JCI6_JAN , JCI6_FEB , JCI6_MAR , JCI6_APR , JCI6_MAY , JCI6_JUN , JCI6_JUL , JCI6_AUG , JCI6_SEP , JCI6_OCT , JCI6_NOV , JCI6_DEC };
	
	//for
	int i = 0;
	int j = 0;
	tag_t tmp_cost_info = NULLTAG;

	logical attr_value_not_in_map = FALSE;
	
	map< string , map< string , string > >::iterator attr_map_ite;
	
	//costinfo year
	int cost_info_year = 0;

	
	char datatime[32]={'\0'};
	//go_task
	tag_t tccalendar = NULLTAG;
	date_t go_start_date;
	date_t go_month_end_date;

	char month_start_date[32] = {'\0'};
	char month_end_date[32] = {'\0'};
	char month_cur_date[32] = {'\0'};

	int cur_month = 0;

	int month_no_work_day = 0;
	int month_work_day = 0;
	int cur_no_work_day = 0;
	int cur_work_day = 0;

	char ** nonWorkingDates = NULL;

	char * task_type = NULL;
	string datetime;


	
	tr_ECHO("%s calculation_go_task_resident_engineer方法--开始  \n",getNowtime2().c_str());

							
	ITKCALL( AOM_ask_value_date( go_task , "start_date" , &go_start_date ));
	get_month_last_date( go_start_date , &go_month_end_date );
	cur_month = go_start_date.month;
	sprintf( month_start_date , "%4d-%2d-%2d 00:00:00" , go_start_date.year , go_start_date.month , 1 );
	sprintf( month_cur_date , "%4d-%2d-%2d 00:00:00" , go_start_date.year , go_start_date.month , go_start_date.day );
	sprintf( month_end_date , "%4d-%2d-%2d 00:00:00" , go_month_end_date.year , go_month_end_date.month , go_month_end_date.day );

	//ECHO("---->		month_start_date = %s\n" , month_start_date );
	//ECHO("---->		month_cur_date = %s\n" , month_cur_date );
	//ECHO("---->		month_end_date = %s\n" , month_end_date );
	//ITKCALL( TCCALENDAR_get_schedule_tccalendar( go_task , &tccalendar ) );//tccalendar
	
	
	tr_ECHO("%s TCCALENDAR_get_base_tccalendar方法--开始  \n",getNowtime2().c_str());
	
	ITKCALL( TCCALENDAR_get_base_tccalendar( &tccalendar ) );

	
	tr_ECHO("%s TCCALENDAR_get_base_tccalendar方法--结束  \n",getNowtime2().c_str());



	
	tr_ECHO("%s TCCALENDAR_get_resource_non_working_dates方法--开始  \n",getNowtime2().c_str());

	ITKCALL( TCCALENDAR_get_resource_non_working_dates( tccalendar , month_start_date , month_end_date , &month_no_work_day , &nonWorkingDates ) );
	

	tr_ECHO("%s TCCALENDAR_get_resource_non_working_dates方法--结束  \n",getNowtime2().c_str());
	
	//ECHO("---->		month_no_work_day = %d \n" , month_no_work_day );
	month_work_day = go_month_end_date.day - month_no_work_day;
	//ECHO("---->		%d月份的工作日为：%d\n" , go_month_end_date.month+1 , month_work_day );
	if(nonWorkingDates)
	{
		MEM_free(nonWorkingDates);
		nonWorkingDates = NULL;
	}

	
	ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----开始 \n" , getNowtime2().c_str() );

	ITKCALL( TCCALENDAR_get_resource_non_working_dates( tccalendar , month_cur_date , month_end_date , &cur_no_work_day , &nonWorkingDates ) );
	
	
	ECHO("%s--TCCALENDAR_get_resource_non_working_dates-----结束 \n" , getNowtime2().c_str() );
	
	
	//ECHO("---->		cur_no_work_day = %d\n" , cur_no_work_day );
	cur_work_day = go_month_end_date.day - go_start_date.day - cur_no_work_day;
	//ECHO("---->		%d月份GO门后的工作日为：%d\n" , go_month_end_date.month + 1 , cur_work_day );
	if(nonWorkingDates)
	{
		MEM_free(nonWorkingDates);
		nonWorkingDates = NULL;
	}


	for( i = 0 ; i < count; i ++)
	{
		attr_value_not_in_map = FALSE;
		tmp_cost_info = secondary_objects[i] ;
		ITKCALL (TCTYPE_ask_object_type( tmp_cost_info , &sec_object_type ));
		ITKCALL (TCTYPE_ask_class_name( sec_object_type , sec_object_class_name));
		//ECHO("---->		secondary_objects[%d] .object_type = %s\n" , i , sec_object_class_name );
		if(!tc_strcmp( sec_object_class_name , CostInfoType ))
		{
			//if task_type == task_type 26
			//如果costinfo是驻地工程师的，则不做统计计算
			ITKCALL( AOM_UIF_ask_value( tmp_cost_info , "jci6_TaskType" , &task_type ) );
			ECHO("--->		task_type = %s\n" , task_type );
			if( !tc_strcmp( task_type , "Resident Engineer" ) )
			{
				if( task_type )
				{
					MEM_free( task_type );
					task_type = NULL;
				}
				continue;
			}
			if( task_type )
			{
				MEM_free( task_type );
				task_type = NULL;
			}
			//continue;
			//ECHO("--->		cost info -------------\n");
			for( attr_map_ite = attr_map.begin(); attr_map_ite != attr_map.end(); attr_map_ite ++)
			{
				tc_strcpy( tmp_attr_name , attr_map_ite->first.c_str());
				//ECHO("--->		tmp_attr_name = %s\n" , tmp_attr_name );
				map< string , string > value_map = attr_map_ite->second;
				//ECHO("--->	attr_map_ite->second.size() = %d",attr_map_ite->second.size());
				map< string , string >::iterator value_map_ite;
				/*
				for( value_map_ite = attr_map_ite->second.begin(); value_map_ite != attr_map_ite->second.end(); value_map_ite ++)
				{
					ECHO("value_map_ite->first.c_str() = %s , value_map_ite->second.c_str()=%s " , value_map_ite->first.c_str() , value_map_ite->second.c_str() );
				}*/
				//value_map.find();

				ITKCALL (AOM_ask_value_string( tmp_cost_info , tmp_attr_name , &tmp_attr_value ));
				ECHO("---->	JCI6_CostInfo[%d]  属性%s的值为%s \n" , i , tmp_attr_name , tmp_attr_value);

				value_map_ite = value_map.find(tmp_attr_value);
				if(value_map_ite == value_map.end())
				{
					ECHO("---->	不在首选项集合里，不做处理！\n");
					attr_value_not_in_map = TRUE;
					if(tmp_attr_value)
					{
						MEM_free(tmp_attr_value);
						tmp_attr_value = NULL;
					}
					break;
				}
				//else{}
				if(tmp_attr_value)
				{
					MEM_free(tmp_attr_value);
					tmp_attr_value = NULL;
				}
			}
			if(!attr_value_not_in_map)
			{
				double valueTemp=0.0;

				//JCI6_YEAR
				ITKCALL( AOM_ask_value_int( tmp_cost_info , JCI6_YEAR , &cost_info_year ));

				//ECHO("---->		costInfo . jci6_Year = %d\n" , cost_info_year );

				if(cost_info_year == go_start_date.year )
				{
					AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[cur_month] , &valueTemp );
					//ECHO("--->			costinfo_month_attr[%d] = %s\n" , cur_month , costinfo_month_attr[cur_month] );
					//ECHO("11111---->			valueTemp = %lf\n" , valueTemp );
					if(!((abs(valueTemp)<1e-15)&&(month_work_day==0)&&(cur_work_day==0)))
					{
						reValue+= valueTemp/month_work_day*cur_work_day;
					}
					//ECHO("--->	reValue = %lf \n" , reValue );


					for(j = cur_month +1 ; j < 12 ; j++)
					{

						AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[j] , &valueTemp );
						//ECHO("---->		costinfo_month_attr[%d] = %s \n" , j , costinfo_month_attr[j]);
						//ECHO("---->		valueTemp = %lf \n" , valueTemp );
						reValue+=valueTemp;

					}
				}else if(cost_info_year > go_start_date.year){
					for(j = 0 ; j < 12 ; j++)
					{

						AOM_ask_value_double( tmp_cost_info , costinfo_month_attr[j] , &valueTemp );
						//ECHO("---->		costinfo_month_attr[%d] = %s \n" , j , costinfo_month_attr[j]);
						//ECHO("---->		valueTemp = %lf \n" , valueTemp );
						reValue+=valueTemp;
					}
				}

				//ECHO("----->		reValue = %lf \n"  , reValue );
			}
		}
	}

	//ECHO("--->	aaaaaaaa			reValue = %lf\n" , reValue );



	tr_ECHO("%s calculation_go_task_resident_engineer方法--结束  \n",getNowtime2().c_str());

    *value+=reValue;

    return 0;
}

//2014-6-6 mengyawei added started

//获取当前给定schedule_task的子schedule_task的子中的go_schedule_task

tag_t get_go_schedule_task( tag_t schedule_task )
{
	tag_t go_task = NULLTAG;

	//for
	int i = 0;
	int j = 0;

	int first_child_cnt = 0;
	tag_t * first_childs = NULL;
	tag_t tmp_first_child = NULLTAG;
	tag_t tmp_first_object_type = NULLTAG;
	char tmp_first_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

	int second_child_cnt = 0;
	tag_t * second_childs = NULL;
	tag_t tmp_second_child = NULLTAG;
	tag_t tmp_second_object_type = NULLTAG;
	char tmp_second_class_name[TCTYPE_class_name_size_c+1] = {'\0'};

	char * tmp_task_type = NULL;

	ITKCALL( AOM_ask_value_tags( schedule_task , ScheduleChildList , &first_child_cnt , &first_childs ));
	for( i = 0; i < first_child_cnt ; i ++)
	{
		tmp_first_child = first_childs[i];
		ITKCALL( TCTYPE_ask_object_type( tmp_first_child , &tmp_first_object_type ) );
		ITKCALL( TCTYPE_ask_class_name( tmp_first_object_type , tmp_first_class_name ));
		ECHO("---->		first_childs[%d].object_type = %s\n" , i , tmp_first_class_name );
		if(!tc_strcmp( tmp_first_class_name , ScheduleTaskType ))
		{
			ITKCALL( AOM_ask_value_tags( tmp_first_child , ScheduleChildList , &second_child_cnt , &second_childs ));
			for( j = 0; j < second_child_cnt ; j ++)
			{
				tmp_second_child = second_childs[j];
				ITKCALL( TCTYPE_ask_object_type( tmp_second_child , &tmp_second_object_type ) );
				ITKCALL( TCTYPE_ask_class_name( tmp_second_object_type , tmp_second_class_name ));
				ECHO("--------->		second_childs[%d].object_type = %s\n" , j , tmp_second_class_name);
				
				if(!tc_strcmp( tmp_second_class_name , ScheduleTaskType ))
				{
					ITKCALL( AOM_ask_value_string( tmp_second_child , "jci6_TaskType" , &tmp_task_type ));
					ECHO("==>		tmp_task_type = %s\n" , tmp_task_type );
					if(!tc_strcmp( tmp_task_type , "gatetype03" ))
					{
						ECHO("找到 GO 门！\n");
						go_task = tmp_second_child;
						if(tmp_task_type)
						{
							MEM_free( tmp_task_type );
							tmp_task_type = NULL;
						}
						break;
					}
					if(tmp_task_type)
					{
						MEM_free( tmp_task_type );
						tmp_task_type = NULL;
					}
				}
			
			}
			if(second_childs)
			{
				MEM_free(second_childs);
				second_childs = NULL;
			}
			if(go_task!=NULLTAG)
				break;
		}
	}

	if(first_childs)
	{
		MEM_free(first_childs);
		first_childs = NULL;
	}

	return go_task;
}


//获取给定日期所属当前月份的最后一天
//Returns the last one days of the month
void get_month_last_date(date_t month_cur_date , date_t * monthLastDate)
{
	int day_table[2][12]=
		{{31,29,31,30,31,30,31,31,30,31,30,31},
		{31,28,31,30,31,30,31,31,30,31,30,31}};
	int endday = 0;
	
	if( IsLeapYear(month_cur_date.year) )
	{
		endday = day_table[0][month_cur_date.month];
	}else
	{
		endday = day_table[1][month_cur_date.month];
	}

	monthLastDate->year = month_cur_date.year;
	monthLastDate->month = month_cur_date.month;
	monthLastDate->day = endday;
	monthLastDate->hour = month_cur_date.hour;
	monthLastDate->minute = month_cur_date.minute;
	monthLastDate->second = month_cur_date.second;

}


//tag_t program_info(I)	//programInfo
//double * value(O)	//return value


tag_t get_program_info_go_schdule_task( tag_t program_info )
{
	//TC_Project
	int proj_cnt = 0;
	tag_t * projs = NULL;
	//relation
	//ProgramPreferredItem
	tag_t prefered_relation = NULLTAG;
	int sec_cnt = 0;
	tag_t * secs = NULL;
	tag_t tmp_schedule = NULLTAG;
	//tctype
	tag_t tmp_sec_object_type = NULLTAG;
	char tmp_sec_class_name[TCTYPE_class_name_size_c+1] = {'\0'};
	//scheduleTask
	int task_cnt = 0;
	tag_t * tasks = NULL;
	tag_t scheduleTask = NULLTAG;

	tag_t go_task = NULLTAG;
	
	//for
	int i = 0;

	ITKCALL( AOM_ask_value_tags( program_info , ProjectList , &proj_cnt , &projs ));
	if(proj_cnt>0)
	{
		ITKCALL( GRM_find_relation_type( ProgramPreferredItem , &prefered_relation ));
		ITKCALL( GRM_list_secondary_objects_only( projs[0] , prefered_relation , &sec_cnt , &secs));
		for( i = 0; i < sec_cnt ; i ++)
		{
			tmp_schedule = secs[i];
			ITKCALL( TCTYPE_ask_object_type ( tmp_schedule , &tmp_sec_object_type ));
			ITKCALL( TCTYPE_ask_class_name ( tmp_sec_object_type , tmp_sec_class_name ));
			ECHO("--->	secs[%d].object_type = %s\n" , i , tmp_sec_class_name );
			if(!tc_strcmp( tmp_sec_class_name , ScheduleType ))
			{
				//modify by wuwei ---sch_summary_task 
				ITKCALL( AOM_ask_value_tags( tmp_schedule , "fnd0SummaryTask" , &task_cnt , &tasks ));
				if(task_cnt > 0)
				{
					scheduleTask = tasks[0];
					ECHO("----->		找到对应的ScheduleTask\n");
					go_task = get_go_schedule_task( scheduleTask );
				}
				if(tasks)
				{
					MEM_free(tasks);
					tasks = NULL;
				}
			}
		}
		if(secs)
		{
			MEM_free(secs);
			secs = NULL;
		}
	}else{
		ECHO("---->	未找到TC_Project , 请查看当前对象是否发送项目！\n");
	}

	if(projs)
	{
		MEM_free(projs);
		projs = NULL;
	}

	

	return go_task;
}

//2014-6-6 mengyawei added ended


//SP-EXT-FUN-06.“项目信息”上和门相关的信息
int getGateTypeInfoValue(METHOD_message_t* msg, va_list args)
{
    int ifail					= ITK_ok,
        preCount				= 0,
        count					= 0,
        j						= 0,
        i						= 0,
        projectNum             = 0;

    tag_t item_tag				= NULLTAG,
        item_rev_tag			= NULLTAG,
        prop_tag				= NULLTAG,
        *schedule_tags			= NULL,
        *primary_objects		=NULL,
        *project_lists         = NULL;

    const char*     preference_gt_name="YFJC_Gate_Types_Mapping";
    char * propName				=NULL,
        propNameBefore[20]		="",
        propNameBack[30]		="",
        **preValue				=NULL,
        gageType[20]			="";
        
	
	ECHO("getGateTypeInfoValue===========start  \n");

    item_tag = msg->object_tag;
    date_t * prop_value_date	= NULL;
    char ** prop_value_str		= NULL;
    PROP_value_type_t     valtype   ;
    char	*  valtype_n ;

    prop_tag = va_arg(args,tag_t );
    ask_opt_debug();

    ITKCALL(PROP_ask_value_type(prop_tag, &valtype,&valtype_n));

    if(valtype==PROP_date){
        prop_value_date=va_arg( args, date_t * ); 
    }else{
        prop_value_str=va_arg( args, char ** ); 
    }

    ITKCALL(PROP_ask_name(prop_tag,&propName));
    sscanf( propName, "jci6_%[^_]_%s", propNameBefore,propNameBack );
    ECHO("propNameBefore ==%s propNameBack==%s\n",propNameBefore,propNameBack);

    ITKCALL(ifail=PREF_ask_char_values(preference_gt_name,&preCount,&preValue));
    if(ifail)
    {
        EMH_store_initial_error_s1( EMH_severity_user_error, ERROR_PREFERENCE_NOT_FOUND,preference_gt_name);
        ifail =ERROR_PREFERENCE_NOT_FOUND;
        goto GOCON;
    }

    for(i=0;i<preCount;i++)
    {	char * str1		=  NULL,
    *str2		=  NULL,
    strtemp[50]	=	"";
    tc_strcpy(strtemp,preValue[i]);
    str1 = tc_strtok(strtemp,"=");
    str2 = tc_strtok(NULL,"=");
    if(tc_strcmp(str1,propNameBefore)==0){
        tc_strcpy(gageType,str2);
        break;
    }
    }
    ECHO("ww gageType==%s \n",gageType);

	if(gageType==NULL){
		tc_strcpy(gageType,"");
	}

    ITKCALL(AOM_ask_value_tags(item_tag,"project_list",&projectNum,&project_lists));
    ECHO("projectNum=====%d \n",projectNum);
    if( projectNum )
    {
        int preferred_ItemNum = 0;
        tag_t *preferred_Items = NULL;
        tag_t scheduleTag = NULL;
        ITKCALL(AOM_ask_value_tags(project_lists[0],"TC_Program_Preferred_Items",&preferred_ItemNum,&preferred_Items));
        ECHO("preferred_ItemNum=====%d \n",preferred_ItemNum);
        for(int k=0;k<preferred_ItemNum;k++)
        {
            char object_type[WSO_name_size_c+1] = "";
            ITKCALL(WSOM_ask_object_type(preferred_Items[k],object_type));
            if(tc_strcmp(object_type,"Schedule")==0)
            {
				char *item_id=NULL;
                scheduleTag = preferred_Items[k];
                ITKCALL(AOM_ask_value_string(scheduleTag,"item_id",&item_id));
                ECHO("litem_id=====%s\n",item_id);
                searchScheduleTask(item_id,gageType,&count,&schedule_tags);
				SAFE_SM_FREE(item_id);
                break;
            }
        }
        SAFE_SM_FREE( preferred_Items );
    }
    SAFE_SM_FREE( project_lists );

    ECHO("schedule task count=====%d \n",count);
    if(tc_strcmp(gageType,"")==0)  //就是 over-all-status
    {
        int //start			=0,
            //notComplete		=0,
            not_start       = 0,    // add by zhanggl 记录未开始的门个数
            completed       = 0;    // add by zhanggl 记录完成的门的个数

        if(tc_strstr(propNameBack,"date")){
            return 0 ;
        }
        for(i=0;i<count;i++)
        {
            int status		=0;
            bool  isGate	=0;
            char * taskType =NULL;
			char    *obj_type2=NULL;   

			ITKCALL(WSOM_ask_object_type2(schedule_tags[i],&obj_type2));
			 ECHO("schedule_tags[i] obj_type2=====%s \n",obj_type2);
			 SAFE_SM_FREE(obj_type2);

            ITKCALL(AOM_ask_value_string(schedule_tags[i],"jci6_TaskType",&taskType));

            for(j=0;j<preCount;j++)
            {	
                if(tc_strstr(preValue[j],taskType)){
                    isGate=1;
                    break;
                }
            }
            if(!isGate)             //不是门对象 
                continue;

            ITKCALL( AOM_ask_value_int( schedule_tags[i], "status", &status ) );
            if(status==5){
                //ITKCALL( PROP_set_value_string( prop_tag,"5"));
                //此处错误,如果上一次status = 1,那么start=1，这次循环status=5，那么跳出循环体，执行标志1部分，这里的赋值是不起作用的。
                *prop_value_str=MEM_string_copy("5");
                goto BREAK5;
            }
            if( status == 0 )
            {
                not_start++;
            }
            else if( status == 3 )
            {
                completed++;
            }
            //Remove by zhanggl
            //if(status!=0) //start
            //{  
            //	start=1;
            //}
            //if(status!=3) //not complete
            //{  
            //	notComplete=1;
            //}
        }
        //Add by zhanggl
        if( completed == count )
        {
            *prop_value_str=MEM_string_copy("3");
        }
        else if( not_start == count )
        {
            *prop_value_str=MEM_string_copy("0");
        }
        else
        {
            *prop_value_str=MEM_string_copy("1");
        }
        //标志1
        //Remove by zhanggl
        //if(start==0)
        //{	
        //	//ITKCALL( PROP_set_value_string( prop_tag,"0") );
        //	*prop_value_str=MEM_string_copy("0");
        //}else if(notComplete==0)
        //{
        //	//ITKCALL( PROP_set_value_string( prop_tag,"3") );	
        //	*prop_value_str=MEM_string_copy("3");
        //}else{
        //	//ITKCALL( PROP_set_value_string( prop_tag,"1") );
        //	*prop_value_str=MEM_string_copy("1");
        //}

    }else{
        if(count){

			 ECHO("new propNameBack=====%s\n",propNameBack);

			 char    *obj_type2=NULL;   

			ITKCALL(WSOM_ask_object_type2(schedule_tags[0],&obj_type2));
			 ECHO("schedule_tags[0] obj_type2=====%s \n",obj_type2);
			 SAFE_SM_FREE(obj_type2);

            if(tc_strstr(propNameBack,"date")!=NULL){
                date_t finishi_date	=	NULLDATE;
                ITKCALL(AOM_ask_value_date(schedule_tags[0],"finish_date",&finishi_date));
				 ECHO("==AOM_ask_value_date===\n");
                //ITKCALL( PROP_set_value_date( prop_tag,finishi_date) );
                *prop_value_date=finishi_date;
            }else{
                int status=0;
				char statusStr[20]={'\0'};

				
				 //modify by wuwei
				ITKCALL(AOM_ask_value_int(schedule_tags[0],"status",&status));

			    ECHO("status=====%d\n",status);
                sprintf( statusStr, "%d",status);
				 ECHO("11 statusStr=====%s\n",statusStr);

                *prop_value_str=MEM_string_copy(statusStr);
            }	
        }
    }
BREAK5:
    if(schedule_tags){
        //SAFE_SM_FREE(schedule_tags);
		 ECHO(" =====SAFE_SM_FREE(schedule_tags)==========  \n");
	}
GOCON: 
    if(preValue)
    {
        SAFE_SM_FREE(preValue);
        ECHO(" =====SAFE_SM_FREE(preValue)==========  \n");
    }

    ECHO("WW123 getGateTypeInfoValue===========end  \n");
    return ifail;

}

/**
根据项目id找到时间表任务 
char * project_id <I>   project id
char * gateType <I>		gate type
int * count <O>        result count
tag_t * count <O>        result 
*/

int searchScheduleTask(char *project_id,char * gateType ,int  * count,tag_t ** obj_tags){
    tag_t query_tag	  = NULLTAG,
        user        = NULLTAG,
        *qresult	  = NULL ;
    char **qkey			=NULL,
        **qvalue		=NULL,
        **entries		=NULL,
        **values		=NULL;
    int  i  		=0,
        icount		=0,
        qcount		=0,
        num_found		=0;

    if(gateType==NULL||tc_strcmp(gateType,"")==0)
    {
        qcount=1;
    }
    else
    {
        qcount=2;
    }

    qkey= (char **) MEM_alloc(qcount* sizeof(char*));
    qvalue=(char **) MEM_alloc(qcount* sizeof(char*));

    ITKCALL(QRY_find("YFJC_SearchScheduleTask",&query_tag));	

    if(query_tag != 0)
    {
        ITKCALL(QRY_find_user_entries(query_tag,&icount,&entries,&values));
        for(i=0;i<qcount;i++)
        {
            qkey[i] = (char *)MEM_alloc(tc_strlen(entries[i]) + 1);
            qvalue[i] = (char *)MEM_alloc(500);
            tc_strcpy(qkey[i],entries[i]);
        }

		 tc_strcpy(qkey[0],"Project ID");

        tc_strcpy(qvalue[0],project_id);
       
		
		if(qcount==2)
        {
			tc_strcpy(qkey[1],"Gate/Task Type");
            tc_strcpy(qvalue[1],gateType);
        }

        ITKCALL(QRY_execute(query_tag,qcount,qkey,qvalue,&num_found,&qresult));

		 ECHO("YFJC_SearchScheduleTask查询result-->%d  \n",num_found);
        for(i=0;i<qcount;i++)
        {
            if(qkey[i]!=NULL)
            {
                MEM_free(qkey[i]);
                qkey[i]=NULL;
            }
            if(qvalue[i]!=NULL)
            {
                MEM_free(qvalue[i]);
                qvalue[i]=NULL;
            }
        }
    }
    else
    {
        ECHO("YFJC_SearchScheduleTask查询构建器未找到  \n");
    }
    *count=num_found;
    *obj_tags=qresult;

	SAFE_SM_FREE(qresult);

    if(entries!=NULL)
    {
        MEM_free(entries);
        entries=NULL;
    }
    if(values!=NULL)
    {
        MEM_free(values);
        values=NULL;
    }
    if(qkey!=NULL)
    {
        MEM_free(qkey);
        qkey=NULL;
    }
    if(qvalue!=NULL)
    {
        MEM_free(qvalue);
        qvalue=NULL;
    }
    return ITK_ok;
}



//SP-EXT-FUN-07.“项目信息”版本上的预算汇总信息
int	getRevBudgetStatValue(METHOD_message_t* msg, va_list args)
{	
    double value=0.00;
    int ifail							= ITK_ok,
        preCount						= 0,
        count							= 0,
        i								= 0;

    tag_t item_rev_tag					= NULLTAG,
        *primary_objects				= NULLTAG,
        relation_type					= NULLTAG,
        *secondary_objects			= NULLTAG,
        prop_tag						= NULLTAG;

	const char	*   preference_cpt_name ="YFJC_Cost_Phase_Types_Mapping";
    const char	*   preference_ct_name="YFJC_Cost_Types_Mapping";
    char * propName					=NULL,
        propNameBefore[20]			="",
        propNameBack[30]			="",
        **preValue					=NULL,
        costPhaseType[20]			=JCI6_Budget,
        costType[200]				=""	,
        unit[20]					=""	;

	//mengyawei added started
	map< string , map< string , string > > attr_map;//map< attr_name , map< attr_value , attr_value > >
	char tmp_prop_name[64] = {'\0'};
	char tmp_str_buf[1024] = {'\0'};
	char attr_name_values[1024] = {'\0'};
	char attr_name_value[512] = {'\0'};
	char attr_name[256] = {'\0'};
	char tmp_att_value[256] = {'\0'};
	char attr_value[512] = {'\0'};
	int attr_cnt = 0;
	char ** attrs = NULL;
	int value_cnt = 0;
	char ** values = NULL;
	//for
	int j = 0;
	int k = 0;

	//cpt
	string cpt_key ;
	map< string , string > cpt_value_map;

	tag_t program_info = NULLTAG;
	//go_task
	tag_t go_task = NULLTAG;
	
	//mengyawei added ended


    double * prop_value =NULL;
    long double returnValue=0.00;
    prop_tag = va_arg(args,tag_t );
    prop_value=va_arg(args, double*);
    ask_opt_debug();

    item_rev_tag = msg->object_tag;
    /* JCI6TransToCostInfoImpl infoImpl;
    JCI6TransToCostInfo *info = &infoImpl;
    info->jci6TransToCostInfo( item_rev_tag );*/
	ECHO("getRevBudgetStatValue===========start \n");
    ITKCALL(PROP_ask_name(prop_tag,&propName));
    sscanf( propName, "jci6_%[^_]_%s", propNameBefore,propNameBack ); // propNameBefore=budget,propNameBack==labor_hc
    ECHO("propNameBefore ==%s propNameBack==%s\n",propNameBefore,propNameBack);

	ITKCALL(ifail=PREF_ask_char_values(preference_cpt_name,&preCount,&preValue));
    if(ifail)
    {	
        EMH_store_initial_error_s1( EMH_severity_user_error, ERROR_PREFERENCE_NOT_FOUND,preference_cpt_name);
        ifail =ERROR_PREFERENCE_NOT_FOUND;
        goto GOCON;
    }
	//mengyawei modify
	cpt_key.assign("jci6_CPT");
    for(i=0;i<preCount;i++)
    {	
		sscanf(preValue[i],"%[^=]=%[^=]",attr_name,attr_value);
		//ECHO("---->		attr_name = %s  attr_value = %s" , attr_name , attr_value );
        if(tc_strcmp(attr_name,propNameBefore)==0)
        {
            ITKCALL( EPM__parse_string( attr_value, ",", &value_cnt , &values) );

			for(i = 0; i < value_cnt ; i++)
			{
				tc_strcpy( tmp_att_value , values[i] );
				//ECHO("---->		values[%d] = %s     11\n",i , tmp_att_value);
				string tmp_value;
				tmp_value.assign( tmp_att_value );
				cpt_value_map.insert(pair<string , string>( tmp_value , tmp_value ));
			}
			attr_map.insert(pair< string , map< string , string > >( cpt_key , cpt_value_map ));
			if(values)
			{
				MEM_free(values);
				values = NULL;
			}
            break;
        }
    }
	if(preValue)
	{
		MEM_free(preValue);
		preValue = NULL;
	}
	//mengyawei modify


    ITKCALL(ifail=PREF_ask_char_values(preference_ct_name,&preCount,&preValue));
    if(ifail)
    {
        EMH_store_initial_error_s1( EMH_severity_user_error, ERROR_PREFERENCE_NOT_FOUND,preference_ct_name);
        ifail =ERROR_PREFERENCE_NOT_FOUND;
        goto GOCON;
    }
	
	//mengyawei modify started
	for( i = 0 ; i < preCount ; i ++)
	{
		//ECHO("--->	preValue[%d]=%s\n" , i , preValue[i] );
		sscanf( preValue[i] , "%[^=]" , tmp_prop_name);
		//ECHO("--->		tmp_prop_name = %s\n" , tmp_prop_name );
		if(!tc_strcmp( tmp_prop_name , propNameBack))
		{
			
			tc_strcpy( attr_name_values , (tc_strstr( preValue[i] , "=" ) +1 ) );
			//ECHO("--->	attr_name_values = %s\n" , attr_name_values );
			
			
			ITKCALL( EPM__parse_string( attr_name_values, "{", &attr_cnt , &attrs) );
			for( j = 0; j < attr_cnt ; j ++)
			{
				//ECHO("--->	attrs[%d] = %s\n" , j , attrs[j] );
				if(tc_strlen(attrs[j])==0)
					continue;
				
				sscanf(attrs[j] , "%[^=]=%[^}]",attr_name,attr_value);
				//ECHO("--->		attr_name = %s attr_value = %s\n" , attr_name , attr_value );
			
				string att_key;
				att_key.assign(attr_name);
				map< string , string > attr_value_map;
				
				ITKCALL( EPM__parse_string( attr_value, ",", &value_cnt , &values) );
				for( k = 0; k < value_cnt ; k ++)
				{
					tc_strcpy(tmp_att_value , values[k]);
					//ECHO("--->	values[%d] = %s\n" , k , tmp_att_value );
					string att_value;
					att_value.assign(tmp_att_value);
					//ECHO("--->	att_value.c_str() = %s",att_value.c_str());
					attr_value_map.insert(pair< string , string >( att_value , att_value ));
				}
				//ECHO("---->		attr_value_map.size() = %d \n" , attr_value_map.size());
				attr_map.insert(pair<string , map< string , string > >( att_key , attr_value_map ));
				if(values)
				{
					MEM_free(values);
					values = NULL;
				}
				
			}
			if(attrs)
			{
				MEM_free(attrs);
				attrs = NULL;
			}
			break;
		}
		

	}
	//mengyawei modify ended
    if(preValue)
    {
        MEM_free(preValue);
        preValue=NULL;
    }
    
	ECHO("---->		propName[tc_strlen(propName)-1] = %c \n" , propName[tc_strlen(propName)-1]);
	ITKCALL(GRM_find_relation_type(IMAN_EXTERNAL_OBJECT_LINK,&relation_type));//规格关系
	//ITKCALL(GRM_find_relation_type("IMAN_specification",&relation_type));//本机测试
	ITKCALL(GRM_list_secondary_objects_only(item_rev_tag,relation_type,&count,&secondary_objects));

	 if(propName[tc_strlen(propName)-1] == '2')//如果当前属性以2结尾则去计算go门
	 {
		 ITKCALL(ITEM_ask_item_of_rev( item_rev_tag , &program_info ));
		 go_task = NULLTAG;
		 go_task = get_program_info_go_schdule_task( program_info );
		 if(go_task != NULLTAG)
		 {
			 calculation_go_task( secondary_objects , count , go_task , attr_map , &value );
		 }else{
			 ECHO("5555---->	未找到 GO 门！\n");
		 }
	 }
	else if(propName[tc_strlen(propName)-1] == '3')
	{
		ITKCALL(ITEM_ask_item_of_rev( item_rev_tag , &program_info ));
		 go_task = NULLTAG;
		 go_task = get_program_info_go_schdule_task( program_info );
		 if(go_task != NULLTAG)
		 {
			 calculation_go_task_resident_engineer( secondary_objects , count , go_task , attr_map , &value );
		 }else{
			 ECHO("6666---->	未找到 GO 门！\n");
		 }
	}
	else{//正常的取costInfo的属性求和

		
		calculation_attr_map(secondary_objects,count,attr_map,&value);
		
	 }
	 if(secondary_objects)
	{
		MEM_free(secondary_objects);
		secondary_objects = NULL;
	}
	
	
    //ITKCALL( PROP_set_value_double( prop_tag, value ));

	  ECHO("ww getRevBudgetStatValue::value-->%lf \n",value);

    soa_DataPrecision(value,&returnValue);
    *prop_value = returnValue;

GOCON: 

	//modify by wuwei
   // if(preValue)
  //  {
   //     MEM_free(preValue);
   //     preValue=NULL;
  //  } 

	if(propName)
	{
		MEM_free(propName);
		propName = NULL;
	}
    ECHO("lala getRevBudgetStatValue===========end \n");
    return ifail;
}


