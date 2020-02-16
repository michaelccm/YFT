/*=====================================================================================================================
Copyright(c) 2005 Siemens PLM Software Corp. All rights reserved.
Unpublished - All rights reserved
=======================================================================================================================
File description:

Filename: yfjc_ebp_ask_DesignReq_ValidValue.cxx
Module  : Custom Hook

This file contains functions custom librarires

=======================================================================================================================
Date               Name              Description of Change
10-May-2013        wuw               creation
$HISTORY$
=====================================================================================================================*/

#include "yfjc_ebp_head.h"


/**************************************************************************
*
*  JCI6_ask_DesignReq_ValidValue
*
*
***************************************************************************/
int JCI6_ask_DesignReq_ValidValue( METHOD_message_t *msg, va_list args )
{
	int	ifail = ITK_ok,
		i=0,
		j=0,
		m=0;
	tag_t   DRT_tag = NULLTAG,
		tag = NULLTAG,
		rev_tag = NULLTAG,
		DR_tag = NULLTAG;
	tag_t     relation_type = NULLTAG; 
	tag_t*    secondary_objects = NULL;/*free*/
	int       count = 0;
	tag_t*    program_tags = NULL;/*free*/
	int       num = 0;
	char*   object_type = NULL; /*free*/
	char*   value_str = NULL; /*free*/
	char**  LOV_str = NULL;/*free*/
	char*   prop_n = NULL; /*free*/
	char*  point = NULL;
	char    tempValue[256] = {'\0'};
	
	vector<string> ans1, ans2;
	int p = 0, q = 0;
	double d_value = 0;

	tag_t   prop_tag= va_arg( args,tag_t );
	char**  prop_value_string = va_arg( args,char** );
	*prop_value_string = NULL;

	METHOD_PROP_MESSAGE_OBJECT(msg,DR_tag);

	//printf("register JCI6_ask_DesignReq_ValidValue!!!!\n\n\n");
	//printf("DR_tag is %d\n",DR_tag);

	ITKCALL(PROP_ask_name(prop_tag,&prop_n));
	//printf("prop_name=====%s\n\n",prop_n);
	ITKCALL(GRM_find_relation_type("JCI6_SMTET",&relation_type));
	//printf("relation_type is %d\n",relation_type);
	ITKCALL(GRM_list_secondary_objects_only(DR_tag,relation_type,&count,&secondary_objects));
	//printf("JCI6_SMTET下的关系的数量为：%d\n\n",count);

	if(count > 0)
	{
		DRT_tag=secondary_objects[0];
		MEM_free(secondary_objects);
		secondary_objects = NULL;
		count = 0;	
	}
	else
	{
		MEM_free(prop_n);
		return ifail;
	}

	ITKCALL( AOM_ask_value_string( DRT_tag, prop_n, &value_str ));
	//printf("%s === %s\n\n",prop_n,value_str);
	MEM_free(prop_n);
	if(value_str == NULL)
	{
		MEM_free(value_str);
		return ifail;
	}
	else if(tc_strstr(value_str, "#") == NULL)
	{
		*prop_value_string = MEM_string_copy(value_str);
		printf("值是固定的为%s\n\n",value_str);
		MEM_free(value_str);
		return ifail;
	}

	ITKCALL( AOM_ask_value_tag_at( DR_tag,"project_list",0, &tag ));
	ITKCALL( GRM_find_relation_type("TC_Program_Preferred_Items",&relation_type));
	ITKCALL( GRM_list_secondary_objects_only(tag,relation_type,&num,&program_tags));
	if(num == 0)
	{
		*prop_value_string = MEM_string_copy(value_str);
		printf("Error: TC_Program_Preferred_Items下的关系的数量为：%d\n\n",num);
		MEM_free(value_str);
		return ifail;
	}
	for(i=0;i<num;i++)
	{
		ITKCALL( AOM_ask_value_string(program_tags[i],"object_type",&object_type));
		if(tc_strcmp(object_type,"JCI6_ProgramInfo") == 0)
		{
			ITKCALL( ITEM_ask_latest_rev(program_tags[i],&rev_tag) );
			ITKCALL(AOM_ask_value_strings(rev_tag,"jci6_SMTE_Variables",&count,&LOV_str));

			if(count <= 0)
			{
				*prop_value_string = MEM_string_copy(value_str);
				printf("Error: jci6_SMTE_Variables的lov值得count为：%d\n\n",count);
				MEM_free(value_str);
				MEM_free(object_type);
				MEM_free(program_tags);
				program_tags = NULL;
				num = 0;
				return ifail;
			}
		}
		MEM_free(object_type);
	}
	MEM_free(program_tags);
	program_tags = NULL;
	num = 0;

	//增加+-运算符功能
	printf("value_str=%s\n",value_str);
	sprintf(tempValue, "%s", value_str);
	MEM_free(value_str);	
	Split(tempValue, '+', ans1);
	for(p = 0; p < (int) ans1.size(); p ++)
	{
		printf("ans1[%d].c_str()=%s\n",p,ans1[p].c_str());
		unsigned found = ans1[p].find("-");  
		if (found!=string::npos)
		{
			Split(ans1[p], '-', ans2);
			for( q = 0; q < (int) ans2.size(); q++)
			{
				printf("ans2[%d].c_str()=%s\n",q, ans2[q].c_str());
				if( ans2[q].c_str()[0] == '#')
				{
					for(j=0;j<count;j++)
					{
						if(tc_strstr(LOV_str[j], ans2[q].c_str()) != NULL)
						{
							//得到LOV对应的属性名
							point = LOV_str[j] + tc_strlen(ans2[q].c_str()) + 1;
							if(tc_strlen(point) == 0)
							    continue;
						    //printf( "==号后 is  %s\n\n", point);
							if( q == 0)
								d_value = d_value + atof(point);
							else
								d_value = d_value - atof(point);
						    //printf( "d_value  %d\n\n", d_value );
							break;
						}
					}
					//break;
				}
				else
				{
					if( q == 0)
						d_value = d_value + atof(ans2[q].c_str());
					else
						d_value = d_value - atof(ans2[q].c_str());
				}

			}
		}
		else
		{
			if( ans1[p].c_str()[0] == '#')
			{
				for(j=0;j<count;j++)
				{
					if(tc_strstr(LOV_str[j], ans1[p].c_str()) != NULL)
					{
						//得到LOV对应的属性名		
						point = LOV_str[j] + tc_strlen(ans1[p].c_str()) + 1;						
						if(tc_strlen(point) == 0)
						    continue;
						//printf( "2==号后 is  %s\n\n", point);
						d_value = d_value + atof(point);
						break;
					}
				}
				//break;

			}
			else
			{
				d_value = d_value + atof(ans1[p].c_str());
			}
		}
	}
	printf("set *prop_value_string value:%d",(int)d_value);
	sprintf(tempValue,"%d",(int)d_value);
	*prop_value_string = MEM_string_copy(tempValue);

	if(count > 0)
	{
		for(i=0; i<count; i++)
		{
			if(LOV_str[i] != NULL)
				MEM_free(LOV_str[i]);
		}
		MEM_free(LOV_str);
	}
	count = 0;
	LOV_str = NULL;
	return ifail;
}
