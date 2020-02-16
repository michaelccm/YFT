/***************************************************************************************
*
*  JCI6_ask_timesheetentry_hour
*
*  This method is to convert minutes to hours for "minutes" property of TimeSheetEntry.
*
***************************************************************************************/
#include "yfjc_ebp_head.h"

#ifdef __cplusplus
extern "C" {
#endif
int  JCI6_ask_timesheetentry_hour( METHOD_message_t *  message, va_list  args )
{
    int ifail = ITK_ok;
    tag_t objTag = null_tag;
    int minutes = 0;
    float hours = 0;
    char tempValue[32] = {'\0'};
    tag_t   prop_tag = va_arg( args, tag_t );
    char**  value = va_arg( args, char** );

    METHOD_PROP_MESSAGE_OBJECT(message, objTag);
    *value = NULL;
    ITKCALL(ifail = AOM_ask_value_int(objTag, "minutes", &minutes));
    hours = minutes/60.0;
    sprintf(tempValue, "%.1f", hours);

    *value = (char*)MEM_alloc ( tc_strlen ( tempValue ) + 1 );
    tc_strcpy ( *value, tempValue );

    return ifail;
}
#ifdef __cplusplus
}
#endif
