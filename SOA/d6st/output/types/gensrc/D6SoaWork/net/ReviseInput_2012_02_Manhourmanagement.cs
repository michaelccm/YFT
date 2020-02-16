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
  public partial class ReviseInput 
  {

         private string YearField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="year")]
     public string Year
     { 
        get { return this.YearField;}
        set { this.YearField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getYear()
     { 
       return this.YearField;
     }
     public void setYear(string val)
     { 
       this.YearField = val;
     }


     private string MonthField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="month")]
     public string Month
     { 
        get { return this.MonthField;}
        set { this.MonthField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMonth()
     { 
       return this.MonthField;
     }
     public void setMonth(string val)
     { 
       this.MonthField = val;
     }


     private string UsernameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="username")]
     public string Username
     { 
        get { return this.UsernameField;}
        set { this.UsernameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getUsername()
     { 
       return this.UsernameField;
     }
     public void setUsername(string val)
     { 
       this.UsernameField = val;
     }


     private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry[] ManHoursField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("manHours")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry[] ManHours
     { 
        get { return this.ManHoursField;}
        set { this.ManHoursField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getManHours()
     { 
         if(this.ManHoursField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.ManHoursField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setManHours(System.Collections.ArrayList val)
     { 
       this.ManHoursField = new D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry[val.Count];
       val.CopyTo(this.ManHoursField);
     }



    
    


  } // type
} // ns
            





