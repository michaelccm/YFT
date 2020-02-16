#include <tccore/aom_prop.h>
#include <property/prop.h>
#include <tccore/aom.h>
#include <tccore/method.h>
#include <time.h>
#include "yfjc_ebp_head.h"

extern "C" int POM_AM__set_application_bypass(logical bypass);


#define JCI6_SOP                         "jci6_SOP"
#define JCI6_SOPFY                       "jci6_SOPFY"


int bmf_autoUpdateSOPFisYear(METHOD_message_t* msg, va_list args)
{
	printf("**********************bmf_autoUpdateSOPFisYear start****************************\n");
	tag_t programinfo_rev_tag = NULLTAG;
	tag_t     prop_tag   = va_arg( args, tag_t );
    	date_t    prop_value = va_arg( args, date_t );
	int sopFY = 0;

	//得到该属性所属的对象
    	METHOD_PROP_MESSAGE_OBJECT( msg, programinfo_rev_tag );
		
		// tyl  2015/01/13 项目关闭冻结信息
		logical jci6_ActiveStatus = TRUE;
		ITKCALL(AOM_ask_value_logical(programinfo_rev_tag, "jci6_ActiveStatus", &jci6_ActiveStatus));
		if(!jci6_ActiveStatus)
		{
		return 0;
		}
		// tyl  2015/01/13 项目关闭冻结信息
		
	 sopFY = SOPFYIsYear(prop_value);
	if( programinfo_rev_tag != NULLTAG )
	{
		POM_AM__set_application_bypass(TRUE);
		//ITKCALL(AOM_lock(programinfo_rev_tag));
		//设置jci6_SOPFY属性
		ITKCALL(AOM_set_value_int(programinfo_rev_tag,JCI6_SOPFY,sopFY));
		//ITKCALL(AOM_save(programinfo_rev_tag));
		//ITKCALL(AOM_unlock(programinfo_rev_tag));
		POM_AM__set_application_bypass(FALSE);	
	}
	printf("**********************bmf_autoUpdateSOPFisYear end****************************\n");
	return 0;
}


