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
  public partial class MheTestOutput 
  {

         private string OutField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="out")]
     public string Out
     { 
        get { return this.OutField;}
        set { this.OutField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getOut()
     { 
       return this.OutField;
     }
     public void setOut(string val)
     { 
       this.OutField = val;
     }



    
    


  } // type
} // ns
            





