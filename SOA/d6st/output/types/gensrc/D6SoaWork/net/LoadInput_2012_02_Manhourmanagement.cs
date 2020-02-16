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
  public partial class LoadInput 
  {

         private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry FilterField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("filter")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry Filter
     { 
        get { return this.FilterField;}
        set { this.FilterField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry getFilter()
     { 
       return this.FilterField;
     }
     public void setFilter(D6.Schemas.Work._2012_02.Manhourmanagement.ManHourEntry val)
     { 
       this.FilterField = val;
     }



    
    


  } // type
} // ns
            





