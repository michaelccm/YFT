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

using System.Xml.Serialization;



namespace D6.Schemas.Work._2012_02.Manhourmanagement 
{


[System.CodeDom.Compiler.GeneratedCodeAttribute("xsd2csharp", "1.0")]
[System.SerializableAttribute()]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
[System.Xml.Serialization.XmlRootAttribute(Namespace="http://d6.com/Schemas/Work/2012-02/ManHourManagement", IsNullable=false)]
[System.Xml.Serialization.XmlTypeAttribute(AnonymousType=true)]
  public partial class ManHourMonthTmp 
  {

         private string DayOfWeekField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="dayOfWeek")]
     public string DayOfWeek
     { 
        get { return this.DayOfWeekField;}
        set { this.DayOfWeekField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getDayOfWeek()
     { 
       return this.DayOfWeekField;
     }
     public void setDayOfWeek(string val)
     { 
       this.DayOfWeekField = val;
     }


     private string HolidayNameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="holidayName")]
     public string HolidayName
     { 
        get { return this.HolidayNameField;}
        set { this.HolidayNameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getHolidayName()
     { 
       return this.HolidayNameField;
     }
     public void setHolidayName(string val)
     { 
       this.HolidayNameField = val;
     }


     private bool IsHolidayField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="isHoliday")]
     public bool IsHoliday
     { 
        get { return this.IsHolidayField;}
        set { this.IsHolidayField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public bool getIsHoliday()
     { 
       return this.IsHolidayField;
     }
     public void setIsHoliday(bool val)
     { 
       this.IsHolidayField = val;
     }


     private int DayOfMonthField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="dayOfMonth")]
     public int DayOfMonth
     { 
        get { return this.DayOfMonthField;}
        set { this.DayOfMonthField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public int getDayOfMonth()
     { 
       return this.DayOfMonthField;
     }
     public void setDayOfMonth(int val)
     { 
       this.DayOfMonthField = val;
     }


     private bool IsWorkRequiredField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="isWorkRequired")]
     public bool IsWorkRequired
     { 
        get { return this.IsWorkRequiredField;}
        set { this.IsWorkRequiredField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public bool getIsWorkRequired()
     { 
       return this.IsWorkRequiredField;
     }
     public void setIsWorkRequired(bool val)
     { 
       this.IsWorkRequiredField = val;
     }


     private bool IsWeekendField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="isWeekend")]
     public bool IsWeekend
     { 
        get { return this.IsWeekendField;}
        set { this.IsWeekendField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public bool getIsWeekend()
     { 
       return this.IsWeekendField;
     }
     public void setIsWeekend(bool val)
     { 
       this.IsWeekendField = val;
     }



    
    


  } // type
} // ns
            





