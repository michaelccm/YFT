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
  public partial class ClearManHoursOPInput 
  {

         private string TheParaField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="thePara")]
     public string ThePara
     { 
        get { return this.TheParaField;}
        set { this.TheParaField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getThePara()
     { 
       return this.TheParaField;
     }
     public void setThePara(string val)
     { 
       this.TheParaField = val;
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


     private string TheActionField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="theAction")]
     public string TheAction
     { 
        get { return this.TheActionField;}
        set { this.TheActionField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getTheAction()
     { 
       return this.TheActionField;
     }
     public void setTheAction(string val)
     { 
       this.TheActionField = val;
     }



    
    


  } // type
} // ns
            





