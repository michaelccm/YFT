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
  public partial class SaveInput 
  {

         private string TheMonthField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="theMonth")]
     public string TheMonth
     { 
        get { return this.TheMonthField;}
        set { this.TheMonthField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getTheMonth()
     { 
       return this.TheMonthField;
     }
     public void setTheMonth(string val)
     { 
       this.TheMonthField = val;
     }


     private string TheUserNameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="theUserName")]
     public string TheUserName
     { 
        get { return this.TheUserNameField;}
        set { this.TheUserNameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getTheUserName()
     { 
       return this.TheUserNameField;
     }
     public void setTheUserName(string val)
     { 
       this.TheUserNameField = val;
     }


     private string TheYearField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="theYear")]
     public string TheYear
     { 
        get { return this.TheYearField;}
        set { this.TheYearField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getTheYear()
     { 
       return this.TheYearField;
     }
     public void setTheYear(string val)
     { 
       this.TheYearField = val;
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


     private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[] TheProgramsField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("thePrograms")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[] ThePrograms
     { 
        get { return this.TheProgramsField;}
        set { this.TheProgramsField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getThePrograms()
     { 
         if(this.TheProgramsField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.TheProgramsField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setThePrograms(System.Collections.ArrayList val)
     { 
       this.TheProgramsField = new D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[val.Count];
       val.CopyTo(this.TheProgramsField);
     }



    
    


  } // type
} // ns
            





