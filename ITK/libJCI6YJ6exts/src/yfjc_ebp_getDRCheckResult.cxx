/*==========================================================================================================
                Copyright (C) 2013 Siemens PLM Software Corp. All rights reserved
                   Unpublished - All Rights Reserved
============================================================================================================
File description:

    Filename: yfjc_getDRCheckResult.cxx
    Module  : Extension

============================================================================================================
DATE           Name             Description of Change
12-Mar-2013    zhanggl          creation
$HISTORY$
11-Apr-2014    zhanggl          new
============================================================================================================*/

#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif
logical is_string_all_digit(char* str, int str_len) 
{     
	int ic       = 0,
		counter  = 0,
		pointCnt = 0;

	logical returnValue = FALSE;
	
	for( ic = 0; ic < str_len; ++ic, ++str )
	{
		if (*str < '0' || *str > '9' )
		{
			if( *str == '.' )
				pointCnt++;
			else
				counter++;
		}
	}
	ECHO(" pointCnt = %d\n", pointCnt);
	ECHO(" counter = %d\n", counter);

	if( !counter && pointCnt < 2)
		returnValue = TRUE;

	return returnValue; 
} 
int JCI6getDRCheckResult( METHOD_message_t *msg, va_list  args ) 
{
	int ifail             = ITK_ok;

	double jci6_MaxValue      = 0.0,
           jci6_MinValue      = 0.0,
           acutalValue_double = 0.0;

	char *jci6_Type          = NULL,
		 *jci6_Operation     = NULL,
		 *jci6_AcutalValue   = NULL,
		 *jci6_ValidValue    = NULL;
	//==============================add on 2013-5-24 by liwh
	char *jci6_MaxValueStr	 = NULL,
		 *jci6_MinValueStr	 = NULL;
	//===============================

	tag_t    prop_tag     = va_arg( args, tag_t );
    logical  *prop_value  = va_arg( args, logical* );

	tag_t designReq_tag    = NULLTAG;

	ask_opt_debug();

    METHOD_PROP_MESSAGE_OBJECT( msg, designReq_tag ); 

	ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_TYPE, &jci6_Type ) );

	ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_OPERATION, &jci6_Operation ) );

	if( !jci6_Operation || !jci6_Type )
	{
		goto FOUT;
	}
	ECHO(" jci6_Type = %s\n", jci6_Type);
	ECHO(" jci6_Operation = %s\n", jci6_Operation);

	ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_ACUTALVALUE, &jci6_AcutalValue ) );

	if( jci6_AcutalValue )
	{
		ECHO(" jci6_AcutalValue = %s\n", jci6_AcutalValue);
		//Range
		ECHO(" jci6_Type=%s,SCOPE=%s\n", jci6_Type,SCOPE);
		if( tc_strcmp( jci6_Type, SCOPE ) == 0 )
		{
			ECHO("Range\n");
			//Double
			if( is_string_all_digit( jci6_AcutalValue, tc_strlen(jci6_AcutalValue) ) )
			{
				ECHO("is_string_all_digit\n");
				acutalValue_double = atof( jci6_AcutalValue );
				//所有double型改为string,以下相同
				//ITKCALL( AOM_ask_value_double( designReq_tag, JCI6_MAXVALUE, &jci6_MaxValue ) );
				ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_MAXVALUE, &jci6_MaxValueStr ) );
				jci6_MaxValue = atof(jci6_MaxValueStr);
				//ITKCALL( AOM_ask_value_double( designReq_tag, JCI6_MINVALUE, &jci6_MinValue ) );
				ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_MINVALUE, &jci6_MinValueStr ) );
				jci6_MinValue = atof(jci6_MinValueStr);
				//<X<
				if( tc_strcmp( jci6_Operation, MIDDLE ) == 0 )
				{
					if( acutalValue_double > jci6_MinValue && acutalValue_double < jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				//<=X<=
				else if( tc_strcmp( jci6_Operation, MIDDLEEQU ) == 0 )
				{
					if( acutalValue_double >= jci6_MinValue && acutalValue_double <= jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				//<=X<
				else if( tc_strcmp( jci6_Operation, "<=X<" ) == 0 )
				{
					if( acutalValue_double >= jci6_MinValue && acutalValue_double < jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				//<X<=
				else if( tc_strcmp( jci6_Operation, "<X<=" ) == 0 )
				{
					if( acutalValue_double > jci6_MinValue && acutalValue_double <= jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				// >
				else if( tc_strcmp( jci6_Operation, ">" ) == 0 )
				{
					if( acutalValue_double > jci6_MinValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				// >=
				else if( tc_strcmp( jci6_Operation, ">=" ) == 0 )
				{
					if( acutalValue_double >= jci6_MinValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				// <
				else if( tc_strcmp( jci6_Operation, "<" ) == 0 )
				{
					if( acutalValue_double < jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				// <=
				else if( tc_strcmp( jci6_Operation, "<=" ) == 0 )
				{
					if( acutalValue_double <= jci6_MaxValue )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
			}
			//String
			else
			{
				int newStrLen  = 0,
					chCnt      = 0,
					step       = 0;

				char *newStr          = NULL,
					 **rangeArrlist   = NULL,
					 *pointer         = NULL; 

				logical isRange = FALSE;

				ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_VALIDVALUE, &jci6_ValidValue ) );
				ECHO(" jci6_ValidValue = %s\n", jci6_ValidValue);

				if(!jci6_ValidValue)
					goto FOUT;

				ITKCALL( STRNG_replace_str( jci6_ValidValue, "；", ";", &newStr ) );
				newStrLen = tc_strlen( newStr );

				for(int ix = 0; ix < newStrLen; ix++ )
				{
					if( newStr[ix] == ';' )
					{
						chCnt++;
					}
				}
				ECHO("chCnt = %d\n", chCnt);
				chCnt = chCnt + 1;

				rangeArrlist = (char**)MEM_alloc( chCnt * sizeof(char*) );
				pointer = tc_strtok( newStr, ";");
				while( pointer )
				{
					rangeArrlist[step] = (char*)MEM_alloc( (tc_strlen(pointer)+1) * sizeof(char) );
					tc_strcpy( rangeArrlist[step], pointer ); 
					pointer = tc_strtok( NULL, ";");
					step++;
				}
				MEM_free( newStr );
				newStr = NULL;

				for(int iy = 0 ; iy < chCnt; iy++ )
				{
					if( tc_strcmp( rangeArrlist[iy], jci6_AcutalValue ) == 0 )
					{
						isRange = TRUE;
						break;
					}
				}
				
				// =
				if( tc_strcmp( jci6_Operation, EQUAL ) == 0 )
				{
					if( isRange )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}
				// !=
				else if( tc_strcmp( jci6_Operation, NOTEQUAL ) == 0 )
				{
					if( !isRange )
					{
						ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
						*prop_value = TRUE;
					}
					else
					{
						ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
						*prop_value = FALSE;
					}
				}

				// Free rangeArrlist
				for(int iy = 0 ; iy < chCnt; iy++ )
				{
					MEM_free( rangeArrlist[iy] );
					rangeArrlist[iy] = NULL;
				}
				MEM_free( rangeArrlist );
				rangeArrlist = NULL;
			}
		}
		//NoRange
		else
		{
			ECHO("NoRange\n");
			//=====add by liwh on 2013-5-24
			ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_VALIDVALUE, &jci6_ValidValue ) );
			ECHO(" jci6_ValidValue = %s\n", jci6_ValidValue);

			if(strcmp(jci6_ValidValue,"") == 0)
				goto FOUT;
			
			// =
			if( tc_strcmp( jci6_Operation, EQUAL ) == 0 )
			{
				ECHO("EQUAL\n");
				ECHO("jci6_ValidValue=%s,jci6_AcutalValue=%s\n",jci6_ValidValue,jci6_AcutalValue);
				if( tc_strcmp( jci6_ValidValue, jci6_AcutalValue ) == 0 )
				{
					ECHO("TRUE\n");
					ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
					*prop_value = TRUE;
				}
				else
				{
					ECHO("FALSE\n");
					ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
					*prop_value = FALSE;
				}
			}
			// !=
			else if( tc_strcmp( jci6_Operation, NOTEQUAL ) == 0 )
			{
				ECHO("NOTEQUAL\n");
				ECHO("jci6_ValidValue=%s,jci6_AcutalValue=%s\n",jci6_ValidValue,jci6_AcutalValue);
				if( tc_strcmp( jci6_ValidValue, jci6_AcutalValue ) != 0 )
				{
					ECHO("TRUE\n");
					ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
					*prop_value = TRUE;
				}
				else
				{
					ECHO("FALSE\n");
					ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
					*prop_value = FALSE;
				}
			}
			//if( jci6_ValidValue != NULL )
			//	MEM_free(jci6_ValidValue);
		}
	}

FOUT:
	if( jci6_Type != NULL )
	{
		MEM_free( jci6_Type );
		jci6_Type = NULL;
	}

	if( jci6_Operation != NULL )
	{
		MEM_free( jci6_Operation );
		jci6_Operation = NULL;
	}

	if( jci6_AcutalValue != NULL )
	{
		MEM_free( jci6_AcutalValue );
		jci6_AcutalValue = NULL;
	}

	if( jci6_ValidValue != NULL )
	{
		MEM_free( jci6_ValidValue );
		jci6_ValidValue = NULL;
	}

	return ifail;
}
//int JCI6getDRCheckResult_old( METHOD_message_t *msg, va_list  args ) 
//{
//	int ifail             = ITK_ok;
//
//    double jci6_MaxValue      = 0.0,
//           jci6_MinValue      = 0.0,
//           acutalValue_double = 0.0;
//
//    char *jci6_Type          = NULL,
//         *jci6_AcutalValue   = NULL,
//         *jci6_ValidValue    = NULL;
//
//    tag_t designReq_tag    = NULLTAG;
//
//    tag_t    prop_tag     = va_arg( args, tag_t );
//    logical  *prop_value  = va_arg( args, logical* );
//
//    ask_opt_debug();
//
//    METHOD_PROP_MESSAGE_OBJECT( msg, designReq_tag ); 
//
//    ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_ACUTALVALUE, &jci6_AcutalValue ) );
//
//    if( jci6_AcutalValue )
//    {
//        ECHO(" jci6_AcutalValue = %s\n", jci6_AcutalValue);
//        acutalValue_double = atol( jci6_AcutalValue );
//    }
//
//    ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_TYPE, &jci6_Type ) );
//
//    if( jci6_Type && tc_strcmp( jci6_Type, SCOPE ) == 0)
//    {
//        ITKCALL( AOM_ask_value_double( designReq_tag, JCI6_MAXVALUE, &jci6_MaxValue ) );
//
//        ITKCALL( AOM_ask_value_double( designReq_tag, JCI6_MINVALUE, &jci6_MinValue ) );
//
//        ECHO(" jci6_MaxValue = %lf\n", jci6_MaxValue);
//        ECHO(" jci6_MinValue = %lf\n", jci6_MinValue);
//        ECHO(" acutalValue_double = %lf\n", acutalValue_double);
//
//        if( acutalValue_double >= jci6_MinValue && acutalValue_double <= jci6_MaxValue )
//        {
//            ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
//            *prop_value = TRUE;
//        }
//        else
//        {
//            ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
//            *prop_value = FALSE;
//        }
//    }
//    else
//    {
//        ITKCALL( AOM_ask_value_string( designReq_tag, JCI6_VALIDVALUE, &jci6_ValidValue ) );
//        ECHO(" jci6_ValidValue = %s\n", jci6_ValidValue);
//
//        if( !jci6_ValidValue && !jci6_AcutalValue )
//        {
//            ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
//            *prop_value = TRUE;
//        }
//        else if( !jci6_ValidValue || !jci6_AcutalValue )
//        {
//            ITKCALL( PROP_assign_logical( prop_tag, FALSE ));
//            *prop_value = FALSE;
//        }
//        else if( tc_strcmp( jci6_AcutalValue, jci6_ValidValue ) == 0 )
//        {
//            ITKCALL( PROP_assign_logical( prop_tag, TRUE ));
//            *prop_value = TRUE;
//        }
//        
//        MEM_free( jci6_ValidValue );
//    }
//    MEM_free( jci6_AcutalValue );
//    MEM_free( jci6_Type );
//
//    //printf(" *prop_value = %d\n", *prop_value);
//    return ifail;
//}

#ifdef __cplusplus
}
#endif