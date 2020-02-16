/*
#===========================================================================================
#																			   
#			Copyright (c) 2009 Origin Enterprise Solution LTD.		   
#																			   
#===========================================================================================
# File name: yfjc_delete_lcc_cost.cxx
# File description: 										   	
#===========================================================================================
#	Date			Name		Action	Description of Change					   
#	2017-10-15	    chenk  		Ini		6.1 CUS-FUN-04／圈人表单转costinfo		
#===========================================================================================
*/
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif

	#define ERROR_DELETE_LCC_COST (EMH_USER_error_base + 500)

	extern "C" int POM_AM__set_application_bypass(logical bypass);

	#define CLCCALL(x)                                                                                     \
	{                                                                                                           \
		if ((ifail = (x)) != ITK_ok)                                                                             \
		{                                                                                                       \
			char* err_string = NULL;                                                                            \
			EMH_ask_error_text (ifail, &err_string);                                                            \
			ECHO("*!ERROR!* ifail: %d ERROR MSG: %s.\n", ifail, err_string);                     \
			if (ifail < EMH_USER_error_base)                                                                     \
			{                                                                                                   \
				ECHO("*!ERROR!* Function: %s FILE: %s LINE: %d\n", #x, __FILE__, __LINE__);            \
			}                                                                                                   \
			MEM_free(err_string);                                                                               \
			goto CLEANUP;                                                                                       \
		}                                                                                                       \
	}

	//获得月份对应的属性名称
	char * getProNameByMonth_d(int month)
	{
		if(month == 1)
		{
            return JCI6_JAN;
		}else if(month == 2)
		{
			return JCI6_FEB;
		}else if(month == 3)
		{
			return JCI6_MAR;
		}else if(month == 4)
		{
			return JCI6_APR;
		}else if(month == 5)
		{
			return JCI6_MAY;
		}else if(month == 6)
		{
			return JCI6_JUN;
		}else if(month == 7)
		{
			return JCI6_JUL;
		}else if(month == 8)
		{
			return JCI6_AUG;
		}else if(month == 9)
		{
			return JCI6_SEP;
		}else if(month == 10)
		{
			return JCI6_OCT;
		}else if(month == 11)
		{
			return JCI6_NOV;
		}else if(month == 12)
		{
			return JCI6_DEC;
		}
	}

	/**
	 * 根据工时得到元  jci6_Rate*jci6_Hours*jci6_Modifier
	 * @param hours
	 * @param costNum
	 * @return
	 */
	double getLccYuanByHours_d( double rate , double hours,double modifier )
	{
		double tmp_result = rate*hours*modifier*(-1);
		int tmp_result_first = (int) tmp_result;
		double tmp_result_last = tmp_result-(double)tmp_result_first;
		double finial_result =  (double)tmp_result_first+((double)((int)(tmp_result_last*100000000))/100000000);
		return finial_result;
	}

	/**
	 * 根据工时得到人月
	 * @param hours
	 * @return
	 */
	double getLccManMonthByHours_d( double hours ) 
	{
		return hours*(-1);
	}

	 /**
	 * 根据工时小时
	 * @param hours
	 * @return
	 */
	double getLccHcHours_d( double hours ) 
	{
		double tmp_result = hours*(-1)/170;
		int tmp_result_first = (int) tmp_result;
		double tmp_result_last = tmp_result-(double)tmp_result_first;
		double finial_result =  (double)tmp_result_first+((double)((int)(tmp_result_last*100000000))/100000000);
		return finial_result;
	}

	/**
	 * 设置cost_tag 的月份值
	 * @param cost_tag
	 * @param prop_name
	 * @param costVaule
	 * @param action   
	 * @return
	 */
	int setMonthValueByAtion_d(logical isDebug,tag_t cost_tag,char* prop_name,double costVaule,char* action)
	{
		double jci6_costVaule = 0;
		int ifail = 0;
		char object_name[WSO_name_size_c + 1] = "";

		if( tc_strcmp( "create" , action ) == 0 )
		{
			if(isDebug)
				ECHO("lala setMonthValueByAtion_d action:create CostInfo prop_name:%s  costVaule:%f\n",prop_name,costVaule);
			long double vv1=costVaule;
			CLCCALL(ifail=AOM_refresh(cost_tag,TRUE));	
			CLCCALL(ifail=AOM_set_value_double( cost_tag , prop_name , vv1 ) );
			CLCCALL(ifail=AOM_save(cost_tag));
			CLCCALL(ifail=AOM_refresh(cost_tag,FALSE));	
		}
		else if( tc_strcmp( "add" , action ) == 0 )
		{
			if(isDebug)
			{
				CLCCALL(WSOM_ask_name(cost_tag,object_name));
				ECHO("更新CostInfo:%s属性%s\n",object_name,prop_name);
			}
			CLCCALL(ifail=AOM_ask_value_double( cost_tag , prop_name , &jci6_costVaule ) );
			if(isDebug)
			{
				ECHO("原值为%f,算出来的值为%f\n",costVaule,jci6_costVaule);
			}
			jci6_costVaule = jci6_costVaule + costVaule;
			if(isDebug)
			{
				ECHO("新值为%f\n",jci6_costVaule);
			}
			CLCCALL(ifail=AOM_refresh(cost_tag,TRUE));		
			long double vv1=jci6_costVaule;
			ITKCALL(ifail=AOM_set_value_double(cost_tag ,prop_name ,vv1));
			CLCCALL(ifail=AOM_save(cost_tag));
			CLCCALL(ifail=AOM_refresh(cost_tag,FALSE));	
		}
CLEANUP:
		return ifail;
	}



	//调用查询
	int lcc_delete_cost_query(logical isDebug,tag_t qryTag,char **query_keys,char **query_values,char **query_keys1,char **query_values1,
		char *programId,char *company,char *division_name,char *section_name,char *bill_type,char *year,
		char *month_name,double rate , double hours,double modifier,string &errorMes,logical *flag)
	{
		int num_found = 0,i=0,ifail = 0;
		logical isExist = FALSE;
		tag_t *results = NULL;
		char *unit = NULL;

		
		if(section_name != NULL)
		{
			query_values[0] = (char *) MEM_alloc( (tc_strlen(programId)+ 1) * sizeof(char));
			query_values[1] = (char *) MEM_alloc( (tc_strlen(company)+ 1) * sizeof(char));
			query_values[2] = (char *) MEM_alloc( (tc_strlen(division_name)+ 1) * sizeof(char));
			tc_strcpy(query_values[0],programId);
			tc_strcpy(query_values[1],company);
			tc_strcpy(query_values[2],division_name);
			query_values[3] = (char *) MEM_alloc( (tc_strlen(bill_type)+ 1) * sizeof(char));
			query_values[4] = (char *) MEM_alloc( (tc_strlen("Actual")+ 1) * sizeof(char));
			query_values[5] = (char *) MEM_alloc( (tc_strlen(year)+ 1) * sizeof(char));
			tc_strcpy(query_values[3],bill_type);
			tc_strcpy(query_values[4],"Actual");
			tc_strcpy(query_values[5],year);

		      query_values[6] = (char *) MEM_alloc( (tc_strlen(section_name)+ 1) * sizeof(char));
		      tc_strcpy(query_values[6],section_name);
		      if(isDebug)
			    ECHO("查询值为%s,%s,%s,%s,%s,%s,%s\n",query_values[0],query_values[1],query_values[2],query_values[3],query_values[4],query_values[5],query_values[6]);
		      CLCCALL(ifail=QRY_execute( qryTag , 7 , query_keys , query_values , &num_found , &results ) );
		}else
		{
			query_values1[0] = (char *) MEM_alloc( (tc_strlen(programId)+ 1) * sizeof(char));
			query_values1[1] = (char *) MEM_alloc( (tc_strlen(company)+ 1) * sizeof(char));
			query_values1[2] = (char *) MEM_alloc( (tc_strlen(division_name)+ 1) * sizeof(char));
			tc_strcpy(query_values1[0],programId);
			tc_strcpy(query_values1[1],company);
			tc_strcpy(query_values1[2],division_name);
			query_values1[3] = (char *) MEM_alloc( (tc_strlen(bill_type)+ 1) * sizeof(char));
			query_values1[4] = (char *) MEM_alloc( (tc_strlen("Actual")+ 1) * sizeof(char));
			query_values1[5] = (char *) MEM_alloc( (tc_strlen(year)+ 1) * sizeof(char));
			tc_strcpy(query_values1[3],bill_type);
			tc_strcpy(query_values1[4],"Actual");
			tc_strcpy(query_values1[5],year);
			if(isDebug)
				ECHO("查询值为%s,%s,%s,%s,%s,%s\n",query_values1[0],query_values1[1],query_values1[2],query_values1[3],query_values1[4],query_values1[5]);
		    CLCCALL(ifail=QRY_execute( qryTag , 6 , query_keys1 , query_values1 , &num_found , &results ) );
		}
		
		if(isDebug)
			ECHO("result count is %d\n",num_found);
		if(num_found > 0)
		{
			isExist = TRUE;
		}
		for(i=0;i<num_found;i++)
		{
			//获得costinfo的jci6_Unit
			CLCCALL(ifail=AOM_ask_value_string(results[i],"jci6_Unit",&unit));
			//更新原来的costinfo
			if(!tc_strcmp(unit,"Yuan"))
			{
				ifail=setMonthValueByAtion_d(isDebug,results[i],month_name,getLccYuanByHours_d(rate,hours,modifier),"add");
			}else if(!tc_strcmp(unit,"Hour"))
			{
				ifail=setMonthValueByAtion_d(isDebug,results[i],month_name,getLccManMonthByHours_d(hours),"add");
			}else if(!tc_strcmp(unit,"ManMonth"))
			{
				ifail=setMonthValueByAtion_d(isDebug,results[i],month_name,getLccHcHours_d(hours),"add");
			}

			if(ifail != 0)
			{
				errorMes.assign("更新costinfo失败");
				break;
			}
			DOFREE(unit);
		}
CLEANUP:
		if(section_name != NULL)
		{
			for(i=0;i<7;i++)
			{
			    MEM_free(query_values[i]);
			}
		}else
		{
			for(i=0;i<6;i++)
			{
			    MEM_free(query_values1[i]);
			}
		}
		DOFREE(unit);
		//释放
		DOFREE(results);
		*flag = isExist;
		return ifail;
	}

	//设置costinfo属性
	int setCostInfoProVal_d(logical isDebug,tag_t costInfo,tag_t discipline,tag_t userTag,
		tag_t divisionTag,tag_t sectionTag,char *bill_type,int year,
		char *unit,char *month_name,double rate , double hours,double modifier)
	{
		if(isDebug)
			ECHO("-----------setCostInfoProVal_d %d,%d,%d,%s,%d,%d,%s,%s-------------\n",divisionTag,sectionTag,discipline,bill_type,userTag,year,unit,month_name);
		int ifail = 0;

		CLCCALL(AOM_refresh(costInfo,true));
		//设置jci6_Division,jci6_Section
		if(divisionTag != NULLTAG)
			CLCCALL(ifail=AOM_set_value_tag(costInfo,"jci6_Division",divisionTag));
		if(sectionTag != NULLTAG)
			CLCCALL(ifail=AOM_set_value_tag(costInfo,"jci6_Section",sectionTag));
		if(discipline != NULLTAG)
			//jci6_RateLevel
			CLCCALL(ifail=AOM_set_value_tag(costInfo,"jci6_RateLevel",discipline));
		//jci6_CostType,jci6_CPT
		CLCCALL(ifail=AOM_set_value_string(costInfo,"jci6_CostType",bill_type));
		CLCCALL(ifail=AOM_set_value_string(costInfo,"jci6_CPT","Actual"));
		if(userTag != NULLTAG)
			//jci6_User
			CLCCALL(ifail=AOM_set_value_tag(costInfo,"jci6_User",userTag));
		//jci6_Year
		if(year != 0)
			CLCCALL(ifail=AOM_set_value_int(costInfo,"jci6_Year",year));
		//jci6_Unit,jci6_Jan~jci6_Dec
		CLCCALL(ifail=AOM_set_value_string(costInfo,"jci6_Unit",unit));

		CLCCALL(AOM_save(costInfo));
		CLCCALL(AOM_refresh(costInfo,false));

		if(!tc_strcmp(unit,"Yuan"))
		{
			ifail=setMonthValueByAtion_d(isDebug,costInfo,month_name,getLccYuanByHours_d(rate,hours,modifier),"create");
		}else if(!tc_strcmp(unit,"Hour"))
		{
			ifail=setMonthValueByAtion_d(isDebug,costInfo,month_name,getLccManMonthByHours_d(hours),"create");
		}else if(tc_strstr(unit,"ManMonth")!=NULL)
		{
			ifail=setMonthValueByAtion_d(isDebug,costInfo,month_name,getLccHcHours_d(hours),"create");
		}

CLEANUP:

		CLCCALL(AOM_refresh(costInfo,false));
		return ifail;
	}

	//创建costinfo
	int delete_lcc_costinfos(logical isDebug,tag_t itemType,tag_t discipline,tag_t programInfoTag,
		tag_t linkRelation,char *programId,char *company,tag_t divisionTag,tag_t sectionTag,
		char *bill_type,int year,char *month_name,double rate , double hours,double modifier)
	{
		tag_t costInfo=NULLTAG,createInputTag=NULLTAG,relationTag = NULLTAG,userTag = NULLTAG;
		int i = 0,ifail = 0;
		const char ** teststr = (const char **)MEM_alloc(1*sizeof(char*));
		char unit[8] = "",object_name[512] = "";
		char *division_name = NULL;
		//获得user
		getUserByUserName(company,&userTag);

		for(i = 0;i<3;i++)
		{
			if(i == 0)
			{
				tc_strcpy(unit,"Yuan");
			}else if(i == 1)
			{
				tc_strcpy(unit,"Hour");
			}else
			{
				tc_strcpy(unit,"ManMonth");
			}

			CLCCALL(ifail=TCTYPE_construct_create_input ( itemType, &createInputTag ));
			
			CLCCALL(ifail = AOM_ask_value_string(divisionTag,"name",&division_name));
			sprintf(object_name,"%s_Actual_%s_%s_ExtSupporter_%s",programId,bill_type,division_name,unit);
			DOFREE(division_name);


			if(isDebug)
				ECHO("delete CostInfo object_name-->%s  \n",object_name);
			teststr[0] = object_name;

			//modify by wuwei
			const char ** unitstr = (const char **)MEM_alloc(1*sizeof(char*));
			//char unit[128]="Hour";
			unitstr[0]=unit;
			CLCCALL(ifail=TCTYPE_set_create_display_value( createInputTag, "jci6_Unit", 1, unitstr));

			const char ** ucpt_str = (const char **)MEM_alloc(1*sizeof(char*));
			char CPT[128]="Actual";
			ucpt_str[0]=CPT;

			const char ** year_str = (const char **)MEM_alloc(1*sizeof(char*));
			char YEAR[12]={'\0'};
			sprintf(YEAR,"%d",year);
			year_str[0]=YEAR;
			//modify by wuwei


			
			CLCCALL(ifail=TCTYPE_set_create_display_value( createInputTag, "object_name", 1, teststr));
			CLCCALL(ifail=TCTYPE_set_create_display_value( createInputTag, "object_desc", 1, teststr));	

			
			CLCCALL(ifail=TCTYPE_set_create_display_value( createInputTag, "jci6_CPT", 1, ucpt_str));	
			CLCCALL(ifail=TCTYPE_set_create_display_value( createInputTag, "jci6_Year", 1, year_str));	

			CLCCALL(ifail=TCTYPE_create_object ( createInputTag, &costInfo) );
			CLCCALL(AOM_save ( costInfo ));
			
			DOFREE(year_str);
			DOFREE(ucpt_str);
			DOFREE(unitstr);
			
			
			//设置属性
			ifail=setCostInfoProVal_d(isDebug,costInfo,discipline,userTag,divisionTag,sectionTag,bill_type,year,unit,month_name,rate,hours,modifier);
			
			ECHO("setDEL CostInfoProVal result-->%d  \n",ifail);
			if(ifail != 0)
			{
				break;
			}
			
			//将costinfo以IMAN_external_object_link关系挂在JCI6_ProgramInfo下
			relationTag  = NULLTAG;
			CLCCALL(ifail = GRM_create_relation(programInfoTag,costInfo,linkRelation, 
					NULLTAG, &relationTag));
			CLCCALL(ifail = GRM_save_relation(relationTag));
			if(isDebug)
				ECHO("create_delete_costinfos over.\n");
		}
CLEANUP:
		DOFREE(division_name);
		DOFREE(teststr);
		return ifail;
	}


	//针对每个JCI6_ExtTSE表单
	int delete_lcc_cost(logical isDebug,vector<tag_t> extFormVec,tag_t qryTag,tag_t discipline,string &errorMes)
	{
		int ifail=0,i=0,size = extFormVec.size(),month = 0;
		tag_t programInfoTag  =NULLTAG,divisionTag = NULLTAG,sectionTag = NULLTAG,objectLinkRelation = NULLTAG,
			itemType = NULLTAG;
		char  program_id[ITEM_id_size_c + 1] = "";
		char *company = NULL,*bill_type = NULL,*modifier = NULL,*division_name = NULL, *section_name = NULL;
		date_t date = NULLDATE;
		double rate = 0,hours = 0;
		char **query_keys = NULL,**query_values = NULL,**query_keys1 = NULL,**query_values1 = NULL;
		char year[8] = "";
		string modifierStr;
		double modifier_c = 0;
		char *month_pro = NULL;
		logical isExist = FALSE,isNull = FALSE;
		vector<string> strVec;
		char *company_name = NULL;

		//modify by wuwei
		char *key_programID = TC_text("project_id");
		char *key_company = TC_text("outsource_name");
		char *key_division_name = TC_text("name");
		char *key_section_name = TC_text("Section_name");
		char *key_costtype = TC_text("jci6_CostType");
		char *key_cpt = TC_text("jci6_CPT");
		char *key_year = TC_text("jci6_Year");


		
	
		CLCCALL(ifail=TCTYPE_find_type(JCI6_COSTINFO,JCI6_COSTINFO,&itemType));
		CLCCALL(ifail=GRM_find_relation_type(IMAN_EXTERNAL_OBJECT_LINK,&objectLinkRelation));

		query_keys = (char **) MEM_alloc(7 * sizeof(char*));
		query_values = (char **) MEM_alloc(7 * sizeof(char*));
		query_keys1 = (char **) MEM_alloc(6 * sizeof(char*));
		query_values1 = (char **) MEM_alloc(6 * sizeof(char*));

		
		query_keys[0] = (char *) MEM_alloc( (tc_strlen(key_programID)+ 12) * sizeof(char));
		query_keys[1] = (char *) MEM_alloc( (tc_strlen(key_company)+ 12) * sizeof(char));
		query_keys[2] = (char *) MEM_alloc( (tc_strlen(key_division_name)+ 12) * sizeof(char));
		query_keys[3] = (char *) MEM_alloc( (tc_strlen(key_costtype)+ 12) * sizeof(char));
		query_keys[4] = (char *) MEM_alloc( (tc_strlen(key_cpt)+ 12) * sizeof(char));
		query_keys[5] = (char *) MEM_alloc( (tc_strlen(key_year)+ 12) * sizeof(char));
		query_keys[6] = (char *) MEM_alloc( (tc_strlen(key_section_name)+ 12) * sizeof(char));

		query_keys1[0] = (char *) MEM_alloc( (tc_strlen(key_programID)+ 12) * sizeof(char));
		query_keys1[1] = (char *) MEM_alloc( (tc_strlen(key_company)+ 12) * sizeof(char));
		query_keys1[2] = (char *) MEM_alloc( (tc_strlen(key_division_name)+ 12) * sizeof(char));
		query_keys1[3] = (char *) MEM_alloc( (tc_strlen(key_costtype)+ 12) * sizeof(char));
		query_keys1[4] = (char *) MEM_alloc( (tc_strlen(key_cpt)+ 12) * sizeof(char));
		query_keys1[5] = (char *) MEM_alloc( (tc_strlen(key_year)+ 12) * sizeof(char));


		tc_strcpy(query_keys[0],"Project ID");
		tc_strcpy(query_keys[1],"outsource_name");
		tc_strcpy(query_keys[2],"Name");
		tc_strcpy(query_keys[3],"Cost Type");
		tc_strcpy(query_keys[4],"Cost Phase Type");
		tc_strcpy(query_keys[5],"Year");
		tc_strcpy(query_keys[6],"Section_name");

		tc_strcpy(query_keys1[0],"Project ID");
		tc_strcpy(query_keys1[1],"outsource_name");
		tc_strcpy(query_keys1[2],"Name");
		tc_strcpy(query_keys1[3],"Cost Type");
		tc_strcpy(query_keys1[4],"Cost Phase Type");
		tc_strcpy(query_keys1[5],"Year");
		
		/* modify by wuwei
		tc_strcpy(query_keys[0],key_programID);
		tc_strcpy(query_keys[1],key_company);
		tc_strcpy(query_keys[2],key_division_name);
		tc_strcpy(query_keys[3],key_costtype);
		tc_strcpy(query_keys[4],key_cpt);
		tc_strcpy(query_keys[5],key_year);
		tc_strcpy(query_keys[6],key_section_name);

		tc_strcpy(query_keys1[0],key_programID);
		tc_strcpy(query_keys1[1],key_company);
		tc_strcpy(query_keys1[2],key_division_name);
		tc_strcpy(query_keys1[3],key_costtype);
		tc_strcpy(query_keys1[4],key_cpt);
		tc_strcpy(query_keys1[5],key_year);
		*/

		if(isDebug)
			ECHO("查询条件为%s,%s,%s,%s,%s,%s,%s\n",query_keys[0],query_keys[1],query_keys[2],query_keys[3],query_keys[4],query_keys[5],query_keys[6]);
		
		for(i=0;i<size;i++)
		{
			//获得programinfo的item_id
			ECHO("FOR count %d\n",i);
			CLCCALL(ifail=AOM_is_null_empty(extFormVec[i],"jci6_Date",true,&isNull));
			if(!isNull)
			{
				CLCCALL(ifail=AOM_ask_value_tag(extFormVec[i],"jci6_Program",&programInfoTag));
				CLCCALL(ifail=ITEM_ask_id(programInfoTag,program_id));
				CLCCALL(ifail=AOM_ask_value_string(extFormVec[i],"jci6_Company",&company));
				CLCCALL(ifail=AOM_ask_value_date(extFormVec[i],"jci6_Date" , &date ) );//填报日期
				sprintf(year,"%d",date.year);//年度
				month  = date.month + 1;//月
				//jci6_BillType, jci6_Rate, jci6_Modifier,jci6_Hours
				CLCCALL(ifail=AOM_ask_value_string(extFormVec[i] ,"jci6_BillType" , &bill_type ) );//清单类型
				CLCCALL(ifail=AOM_ask_value_double(extFormVec[i] ,"jci6_Rate",&rate));
				CLCCALL(ifail=AOM_ask_value_string(extFormVec[i],"jci6_Modifier",&modifier));
				modifierStr.assign(modifier);
                CLCCALL(ifail=AOM_ask_value_double(extFormVec[i] ,"jci6_Hours",&hours));
				//2017-10-15
				//hours = hours*(-1);
                //hours = -hours;
				//jci6_Division,jci6_Section
				CLCCALL(ifail=AOM_ask_value_tag(extFormVec[i] ,"jci6_Division",&divisionTag));
				CLCCALL(ifail=AOM_ask_value_string(divisionTag,"full_name",&division_name));
				CLCCALL(ifail=AOM_ask_value_tag(extFormVec[i] ,"jci6_Section",&sectionTag));		
				if(isDebug)
					ECHO("lala  company is %s,modifier is %s  year:%d month:%d\n",company,modifier,date.year,month);
				
				if(tc_strstr(company,"(") != NULL)
				{
					strVec.clear();
					Split(company,'(',strVec);
					company_name =  (char *) MEM_alloc( (strVec[0].length() + 1) * sizeof(char));
					tc_strcpy(company_name,strVec[0].c_str());
				}else
				{
					company_name =  (char *) MEM_alloc( (tc_strlen(company) + 1) * sizeof(char));
					tc_strcpy(company_name,company);
				}
				if(isDebug)
					ECHO("company_name is %s\n",company_name);

				if(sectionTag != NULLTAG)
				{
					CLCCALL(ifail=AOM_ask_value_string(sectionTag,"full_name",&section_name));
				}
				if(!modifierStr.empty())
				{
					modifier_c = atof(modifierStr.substr(1).c_str());
				}
				month_pro = getProNameByMonth_d(month);				
				POM_AM__set_application_bypass(TRUE);
				ifail = lcc_delete_cost_query(isDebug,qryTag,query_keys,query_values,query_keys1,query_values1,program_id,company_name,division_name,section_name,
					bill_type,year,month_pro,rate ,hours,modifier_c,errorMes,&isExist);

				if(ifail == 0 && !isExist)
				{
					//创建 costinfo
					if(delete_lcc_costinfos(isDebug,itemType,discipline,programInfoTag,objectLinkRelation,program_id,company_name,
						divisionTag,sectionTag,bill_type,date.year,month_pro,rate,hours,modifier_c) != 0)
					{
						ifail = -1;
						errorMes.assign("create costinfo FAILED!!");
					}
				}
				POM_AM__set_application_bypass(FALSE);
				DOFREE(company);
				DOFREE(company_name);
				DOFREE(bill_type);
				DOFREE(modifier);
				DOFREE(division_name);
				DOFREE(section_name);
			}else
			{
				ECHO("jci6_Date no value , continue...\n");
			}
			if(ifail != 0)
			{
				break;
			}
		}	
CLEANUP:
		//释放
		for(i=0;i<7;i++)
		{
			DOFREE(query_keys[i]);
			
		}

		for(i=0;i<6;i++){
			  DOFREE(query_keys1[i]);
		}

		DOFREE(query_keys);
		DOFREE(query_values);
		DOFREE(query_keys1);
		DOFREE(query_values1);
		DOFREE(company);
		DOFREE(company_name);
		DOFREE(bill_type);
		DOFREE(modifier);
		DOFREE(division_name);
		DOFREE(section_name);
		return ifail;
	}


	int yfjc_delete_lcc_cost( EPM_action_message_t msg )
	{
		ECHO("***************yfjc_delete_lcc_cost start***************\n");
		int i= 0,ifail = 0,arg_cnt = 0,att_cnt = 0;
		char *tmp_arg = NULL,*para_name = NULL,*para_value = NULL;
		logical isDebug = FALSE,allowOverdue = TRUE,state_has_changed = FALSE;
		tag_t root_task = NULLTAG;
		tag_t *taskAttches = NULL;
		char object_type[WSO_name_size_c+1] = "";

		struct tm *p;
		time_t now;
		int current_year = 0,current_month = 0;
		int year = 0,month = 0,mark = -1;
		vector<tag_t> suppFormVec,extFormVec;

		int extcnt = 0,status_num = 0;
		tag_t query_tag = NULLTAG,discipline = NULLTAG;
		tag_t *release_status = NULL;
		char *uid = NULL;
		string errorMes;

		//解析handler参数
		arg_cnt = TC_number_of_arguments(msg.arguments);
		for(i = 0; i < arg_cnt ; i ++)
		{
			tmp_arg = TC_next_argument(msg.arguments);
			CALL(ITK_ask_argument_named_value( tmp_arg , &para_name , &para_value ));
			if(!tc_strcmp(para_name , "debug"))
			{
				if(!tc_strcasecmp(para_value , "true"))
				{
					isDebug = TRUE;
				}
			}else if(!tc_strcmp(para_name , "allowOverdue"))
			{
				if(!tc_strcasecmp(para_value , "false"))
				{
					allowOverdue = FALSE;
				}
			}
			DOFREE(para_name);
			DOFREE(para_value);
		}


		//获得流程目标中所有的JCI6_Ext2Supp表单
		ITKCALL(EPM_ask_root_task(msg.task, &root_task));
		ITKCALL(EPM_ask_attachments(root_task, EPM_target_attachment, &att_cnt, &taskAttches));
		
		for(i=0;i<att_cnt;i++)
		{
			//获得对象类型
			ITKCALL(WSOM_ask_object_type(taskAttches[i],object_type));
			ECHO("object_type is %s\n",object_type);
			if(!tc_strcmp(object_type,"JCI6_Ext2Supp"))
			{
				if(!allowOverdue)
				{
					if(current_year == 0)
					{
						//获得当前系统年月
						time(&now);
						p = localtime(&now); /* 取得当前时间 */
						current_year = 1900+p->tm_year;
						current_month = p->tm_mon+1;
					}
					//检查JCI6_Ext2Supp表单上的jci6_Year和jci6_Month必须大于等系统当前月
					//否则弹出提示，不允许往下进行
					ITKCALL(AOM_ask_value_int(taskAttches[i],"jci6_Year",&year));
					ITKCALL(AOM_ask_value_int(taskAttches[i],"jci6_Month",&month));
					if(year > current_year && month > current_month)
					{
					
					}else
					{
						ifail = -1;
						//报错
						errorMes.assign("圈人已经延期");
						goto CLEANUP;
					}
				}
				suppFormVec.push_back(taskAttches[i]);
			}else if(!tc_strcmp(object_type,"JCI6_ExtTSE"))//所有JCI6_ExtTSE表单
			{
				//判断是否发布，已发布不做任何处理
				CLCCALL(ifail=AOM_ask_value_tags(taskAttches[i],"release_status_list",&status_num,&release_status));
				DOFREE(release_status);
				if( status_num > 0 )
				{
					if(isDebug)
					{
						CLCCALL(ifail=POM_tag_to_uid(taskAttches[i],&uid));
						ECHO("uid=%s的JCI6_ExtTSE已经发布,不作任何处理\n",uid);
						DOFREE(uid);
					}
				}else
				{
					extFormVec.push_back(taskAttches[i]);
				}
			}
		}

		//获得指定ExtSupporter的Discipline
		CLCCALL(ifail=SA_find_discipline("ExtSupporter",&discipline));
		CLCCALL(ifail=QRY_find( "YFJC_SearchCostInfoByDiscipline_TaskTypeIsNull", &query_tag) );
		CLCCALL(ifail=POM_place_markpoint(&mark));
		ifail = delete_lcc_cost(isDebug,extFormVec,query_tag,discipline,errorMes);
CLEANUP:
		if(ifail != 0)
		{
			ifail = CR_error_in_handler;
			if(errorMes.empty())
			{
				errorMes.assign("执行过程中出现错误,请查看日志...");
			}
			EMH_store_error_s1(EMH_severity_error,ERROR_DELETE_LCC_COST,errorMes.c_str());
			if(mark != -1)
			{
				CLCCALL(POM_roll_to_markpoint(mark,&state_has_changed));
			}
		}
		if(mark != -1)
		{
			POM_forget_markpoint(mark);
		}
		DOFREE(release_status);
		DOFREE(taskAttches);
		ECHO("***************yfjc_delete_lcc_cost end***************\n");
		return ifail;
	}


#ifdef __cplusplus
}
#endif
