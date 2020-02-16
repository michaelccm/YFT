/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

   Auto-generated source from service interface.
                 DO NOT EDIT

 ==================================================
*/

#include <common/library_indicators.h>

#ifdef EXPORTLIBRARY
#define EXPORTLIBRARY something else
#error ExportLibrary was already defined
#endif

#define EXPORTLIBRARY            libd6soaworktypes

#if !defined(IPLIB)
#   error IPLIB is not defined
#endif

/* Handwritten code should use MANHOURMANAGEMENT_API, not MANHOURMANAGEMENTEXPORT */

#define MANHOURMANAGEMENT_API MANHOURMANAGEMENTEXPORT

/* Support MANHOURMANAGEMENTEXPORT for autogenerated schema/pif code only */

#if IPLIB==libd6soaworktypes
#   if defined(__lint)
#       define MANHOURMANAGEMENTEXPORT       __export(d6soaworktypes)
#       define MANHOURMANAGEMENTGLOBAL       extern __global(d6soaworktypes)
#       define MANHOURMANAGEMENTPRIVATE      extern __private(d6soaworktypes)
#   elif defined(_WIN32)
#       define MANHOURMANAGEMENTEXPORT       __declspec(dllexport)
#       define MANHOURMANAGEMENTGLOBAL       extern __declspec(dllexport)
#       define MANHOURMANAGEMENTPRIVATE      extern
#   else
#       define MANHOURMANAGEMENTEXPORT
#       define MANHOURMANAGEMENTGLOBAL       extern
#       define MANHOURMANAGEMENTPRIVATE      extern
#   endif
#else
#   if defined(__lint)
#       define MANHOURMANAGEMENTEXPORT       __export(d6soaworktypes)
#       define MANHOURMANAGEMENTGLOBAL       extern __global(d6soaworktypes)
#   elif defined(_WIN32) && !defined(WNT_STATIC_LINK)
#       define MANHOURMANAGEMENTEXPORT      __declspec(dllimport)
#       define MANHOURMANAGEMENTGLOBAL       extern __declspec(dllimport)
#   else
#       define MANHOURMANAGEMENTEXPORT
#       define MANHOURMANAGEMENTGLOBAL       extern
#   endif
#endif
