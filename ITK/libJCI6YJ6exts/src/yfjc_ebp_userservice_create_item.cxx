/*===================================================================================================
                    Copyright(c) 2011 Siemens PLM Software Corp. All rights reserved.
                             Unpublished - All rights reserved
====================================================================================================
File description:
    
    Filename : JC6_userservice_create_item.cxx

    This file describes user service function : 

	辅助方法，创建item，将创建结果返回java

====================================================================================================
Date               Name                  Description of Change
11-Nov-2012       Ray Li        creation

$HISTORY$
==================================================================================================*/
#pragma warning (disable: 4996) 
#pragma warning (disable: 4819) 

/**
* @headerfile			tcua 头文件
*/
//#include <ict_userservice.h>
//#include <item.h>
//#include <aom.h>
//#include <aom_prop.h>
//#include <prop_errors.h>
//#include <workspaceobject.h>
//#include <preferences.h>
//#include <imantype.h>
//#include <grm.h>
//#include <grmtype.h>
//#include <am.h>
//#include <bom.h>
//#include <envelope.h>
//#include <cfm.h>
//#include <folder.h>
//#include <sa.h>
//#include <user.h>
/**
* @headerfile			standard c & cpp header files
*/
//#include <string>
//#include <vector>
//#include <map>
//#include <fstream>
//#include <iostream>
/**
* @headerfile			user's header files
*/
#include "yfjc_ebp_head.h"

#ifndef WIN32 
  #define stricmp strcasecmp 
  #define strnicmp strncasecmp 
#endif

#define debug 1

using namespace std;
/*************************************************************************************************
* jc6_create_item(void *retValType)
*
* Description:
*    This userservice will return send result
*
* Syntax:
*         
*     
*
* Placement:
*    no request
*
**************************************************************************************************/

int jc6_create_item( void *retValType )
{
	int ifail = ITK_ok, rcode =0, count = 0, i = 0, j =0;
	char *arg = NULL, *errMsg = NULL, rev_id[ITEM_id_size_c+1]="",*returnValue = NULL,
		 itemid[ITEM_id_size_c+1]="", item_name[ITEM_name_size_c+1]="",
		item_type[TCTYPE_name_size_c+1]="";
	tag_t  rev = NULLTAG, item = NULLTAG;
	logical has_error = false;
	vector<string> arg_vec;
	//if (debug)
	{
		WriteLog("=========================================================\n");
		WriteLog("jc6_create_item 开始执行\n");
		WriteLog("=========================================================\n");
	}

	//CreateLogFile("jc6_create_item");

	//接收JAVA传递数据,数据字符

	ITKCALL( ifail = USERARG_get_string_argument( &arg ));
	if( debug )
		WriteLog("arg=%s",arg);
	//Split(arg,';', arg_vec);

    char *tokenPtr=tc_strtok(arg,";");
    while(tokenPtr!=NULL)
    {
        arg_vec.push_back(tokenPtr);
        tokenPtr=tc_strtok(NULL,";");
    }

	if( debug )
		WriteLog("arg_vec.size()=%d",arg_vec.size());

	returnValue = ( char *)MEM_alloc(512 * sizeof( char ));
	if( debug )
		WriteLog("create item:%s name:%s type:%s",arg_vec[0].c_str(), arg_vec[1].c_str(), arg_vec[2].c_str());
	//查找是否已存在
	ITKCALL( ITEM_find_item( arg_vec[0].c_str(), &item ) );
	if( item != NULLTAG)
	{
		if( debug )
			WriteLog("item %s already exist, continue...",arg_vec[0].c_str());
		tc_strcpy( returnValue, "exist" );
		*((char**) retValType) = returnValue;
		return ITK_ok;
	}
	//依据参数创建
	ITKCALL( ifail = ITEM_create_item(arg_vec[0].c_str(),arg_vec[1].c_str(), arg_vec[2].c_str(), NULL, &item, &rev));
	if( ifail != ITK_ok)
	{
		EMH_store_error_s1( EMH_severity_user_error, ERROR_CREATE_ITEM_FAILED, "");
		if( debug )
			WriteLog("create item failed");
		tc_strcpy( returnValue, "failed" );
		*((char**) retValType) = returnValue;
		return ITK_ok;
	}
	//保存
	if ( item != NULLTAG)
	{
		ITKCALL( AOM_save(rev) );
		ITKCALL( AOM_unlock( rev ) );
		ITKCALL( AOM_save(item) );
		ITKCALL( AOM_unlock(item) );
		if( debug )
			WriteLog("create item sucess");
		tc_strcpy( returnValue, "success" );
	}

	
	DOFREE(arg);
	//CloseLog();
	//if (debug)
	{
		printf("=========================================================\n");
		printf("jc6_create_items 执行完成\n");
		printf("=========================================================\n");
	}

	*((char**) retValType) = returnValue;
	
	return ifail;
}


