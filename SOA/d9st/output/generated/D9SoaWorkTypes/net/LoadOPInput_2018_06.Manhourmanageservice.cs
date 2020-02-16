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
  public partial class LoadOPInput 
  {

         private D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry ManHourFilterField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("manHourFilter")]
     public D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry ManHourFilter
     { 
        get { return this.ManHourFilterField;}
        set { this.ManHourFilterField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry getManHourFilter()
     { 
       return this.ManHourFilterField;
     }
     public void setManHourFilter(D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry val)
     { 
       this.ManHourFilterField = val;
     }



    
    


  } // type
} // ns
            





