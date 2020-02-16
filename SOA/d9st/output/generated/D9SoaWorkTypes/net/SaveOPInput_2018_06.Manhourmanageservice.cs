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



namespace D9.Schemas.Work._2018_06.Manhourmanageservice 
{


[System.CodeDom.Compiler.GeneratedCodeAttribute("xsd2csharp", "1.0")]
[System.SerializableAttribute()]
[System.Diagnostics.DebuggerStepThroughAttribute()]
[System.ComponentModel.DesignerCategoryAttribute("code")]
[System.Xml.Serialization.XmlRootAttribute(Namespace="http://d9.com/Schemas/Work/2018-06/ManHourManageService", IsNullable=false)]
[System.Xml.Serialization.XmlTypeAttribute(AnonymousType=true)]
  public partial class SaveOPInput 
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


     private D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[] ManHoursField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("manHours")]
     public D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[] ManHours
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
       this.ManHoursField = new D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[val.Count];
       val.CopyTo(this.ManHoursField);
     }


     private D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourProgram[] ProgramsField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("programs")]
     public D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourProgram[] Programs
     { 
        get { return this.ProgramsField;}
        set { this.ProgramsField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getPrograms()
     { 
         if(this.ProgramsField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.ProgramsField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setPrograms(System.Collections.ArrayList val)
     { 
       this.ProgramsField = new D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourProgram[val.Count];
       val.CopyTo(this.ProgramsField);
     }



    
    


  } // type
} // ns
            





