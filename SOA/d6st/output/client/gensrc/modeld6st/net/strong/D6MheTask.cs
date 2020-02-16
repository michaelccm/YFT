/* 
 @<COPYRIGHT>@
 ==================================================
 Copyright 2012
 Siemens Product Lifecycle Management Software Inc.
 All Rights Reserved.
 ==================================================
 @<COPYRIGHT>@

 ==================================================

  Auto-generated source from Teamcenter Data Model.
                 DO NOT EDIT

 ==================================================
*/

using System;
using Teamcenter.Soa.Exceptions;

using Teamcenter.Soa.Client.Model.Strong;


namespace Teamcenter.Soa.Client.Model.Strong
{

    /**
     * Generated class to represent the TcType/TcClass D6MheTask
     */

    public class D6MheTask : WorkspaceObject
    {
        public D6MheTask( Teamcenter.Soa.Client.Model.SoaType type, string uid ) :  base( type, uid )
        {
        }

        public string D6TaskType
        {
            get 
            { 
                return GetProperty("d6TaskType").StringValue; 
            }
        }

        public string D6PrjId
        {
            get 
            { 
                return GetProperty("d6PrjId").StringValue; 
            }
        }

        public string D6PrjName
        {
            get 
            { 
                return GetProperty("d6PrjName").StringValue; 
            }
        }

        public string D6UserName
        {
            get 
            { 
                return GetProperty("d6UserName").StringValue; 
            }
        }

        public DateTime D6PrjStartDate
        {
            get 
            { 
                return GetProperty("d6PrjStartDate").DateValue; 
            }
        }

        public DateTime D6PrjEndDate
        {
            get 
            { 
                return GetProperty("d6PrjEndDate").DateValue; 
            }
        }

        public string D6Status
        {
            get 
            { 
                return GetProperty("d6Status").StringValue; 
            }
        }

        public Schedule D6Schedule_tag
        {
            get 
            { 
                return (Schedule)GetProperty("d6Schedule_tag").ModelObjectValue; 
            }
        }


    }
}