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
  public partial class MheTestInput 
  {

         private string UserNameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="userName")]
     public string UserName
     { 
        get { return this.UserNameField;}
        set { this.UserNameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getUserName()
     { 
       return this.UserNameField;
     }
     public void setUserName(string val)
     { 
       this.UserNameField = val;
     }



    
    


  } // type
} // ns
            





