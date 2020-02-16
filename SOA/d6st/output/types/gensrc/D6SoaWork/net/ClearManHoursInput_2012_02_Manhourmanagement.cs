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
  public partial class ClearManHoursInput 
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


     private string TheUserField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="theUser")]
     public string TheUser
     { 
        get { return this.TheUserField;}
        set { this.TheUserField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getTheUser()
     { 
       return this.TheUserField;
     }
     public void setTheUser(string val)
     { 
       this.TheUserField = val;
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



    
    


  } // type
} // ns
            





