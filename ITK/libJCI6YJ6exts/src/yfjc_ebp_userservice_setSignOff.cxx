#include <tcinit/tcinit.h>
#include <tc/tc_macros.h>
#include <ict/ict_userservice.h>
#include <epm/signoff.h>
#include <epm/epm_toolkit_tc_utils.h>
#include "yfjc_ebp_head.h"
int userservice_setSignOff( void *retValType)
{
	printf("*********userservice_setSignOff start************\n");
	int ifail = ITK_ok;
	tag_t member = NULLTAG;
	tag_t task = NULLTAG,root_task = NULLTAG,*root_sub_task=NULLTAG;
	int count = 0,root_sub_cnt=0;
	tag_t *signoffs = NULLTAG;
	////得到java传来的对象
	//ITKCALL(USERARG_get_tag_argument(&task));
	//ITKCALL(USERARG_get_tag_argument(&member));
	//ITKCALL(ifail=EPM_create_adhoc_signoff(task,member,&count,&signoffs));
	//if(ifail == ITK_ok)
	//	ITKCALL(ifail=EPM_set_adhoc_signoff_selection_done(task,TRUE));
	ITKCALL(USERARG_get_tag_argument(&root_task));
	ITKCALL(USERARG_get_tag_argument(&member));
	ITKCALL(EPM_ask_sub_tasks(root_task,&root_sub_cnt,&root_sub_task));
	//得到对象的type
	for(int i=0;i<root_sub_cnt;i++)
	{
		char   root_sub_task_type[WSO_name_size_c+1]="";
		ITKCALL( WSOM_ask_object_type(root_sub_task[i], root_sub_task_type));
		//判断是否为review节点
		printf("root_sub_task_type--->%s\n",root_sub_task_type);
		if(strcmp(root_sub_task_type,"EPMReviewTask") == 0)
		{
			//得子类task
			tag_t *sub_task = NULLTAG;
			int sub_cnt = 0;
			ITKCALL(EPM_ask_sub_tasks(root_sub_task[i],&sub_cnt,&sub_task));
			for(int j=0;j<sub_cnt;j++)
			{
				char   sub_task_type[WSO_name_size_c+1]="";
				ITKCALL( WSOM_ask_object_type(sub_task[j], sub_task_type));
				printf("sub_task_type--->%s\n",sub_task_type);
				if(strcmp(sub_task_type,"EPMSelectSignoffTask") == 0)
				{
					ITKCALL(ifail=EPM_create_adhoc_signoff(sub_task[j],member,&count,&signoffs));
					ITKCALL(ifail=EPM_set_adhoc_signoff_selection_done(sub_task[j],TRUE));
				}
			}
			SAFE_SM_FREE(sub_task);
		}
	}
	SAFE_SM_FREE(root_sub_task);


	*((int *)retValType) = ifail;
	printf("*********userservice_setSignOff end************\n");
	return ifail;
}
