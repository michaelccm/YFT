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
  public partial class GetManHourInfoOPInput 
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



    
    


  } // type
} // ns
            





