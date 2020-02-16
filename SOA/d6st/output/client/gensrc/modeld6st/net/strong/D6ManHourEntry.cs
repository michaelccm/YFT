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
     * Generated class to represent the TcType/TcClass D6ManHourEntry
     */

    public class D6ManHourEntry : WorkspaceObject
    {
        public D6ManHourEntry( Teamcenter.Soa.Client.Model.SoaType type, string uid ) :  base( type, uid )
        {
        }

        public string D6BillType
        {
            get 
            { 
                return GetProperty("d6BillType").StringValue; 
            }
        }

        public string D6Holiday
        {
            get 
            { 
                return GetProperty("d6Holiday").StringValue; 
            }
        }

        public string D6UserName
        {
            get 
            { 
                return GetProperty("d6UserName").StringValue; 
            }
        }

        public string D6WorkingHours
        {
            get 
            { 
                return GetProperty("d6WorkingHours").StringValue; 
            }
        }

        public string D6WorkRequired
        {
            get 
            { 
                return GetProperty("d6WorkRequired").StringValue; 
            }
        }

        public string D6DayOfWeek
        {
            get 
            { 
                return GetProperty("d6DayOfWeek").StringValue; 
            }
        }

        public string D6Month
        {
            get 
            { 
                return GetProperty("d6Month").StringValue; 
            }
        }

        public string D6Year
        {
            get 
            { 
                return GetProperty("d6Year").StringValue; 
            }
        }

        public string D6Day
        {
            get 
            { 
                return GetProperty("d6Day").StringValue; 
            }
        }

        public string D6RefTE
        {
            get 
            { 
                return GetProperty("d6RefTE").StringValue; 
            }
        }

        public string D6PrjName
        {
            get 
            { 
                return GetProperty("d6PrjName").StringValue; 
            }
        }

        public string D6PrjId
        {
            get 
            { 
                return GetProperty("d6PrjId").StringValue; 
            }
        }

        public string D6TaskType
        {
            get 
            { 
                return GetProperty("d6TaskType").StringValue; 
            }
        }

        public string D6RefBR
        {
            get 
            { 
                return GetProperty("d6RefBR").StringValue; 
            }
        }


    }
}