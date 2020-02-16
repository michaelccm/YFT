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
  public partial class ManHourEntrySet 
  {

         private D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[] MheSetField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("mheSet")]
     public D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[] MheSet
     { 
        get { return this.MheSetField;}
        set { this.MheSetField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getMheSet()
     { 
         if(this.MheSetField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.MheSetField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setMheSet(System.Collections.ArrayList val)
     { 
       this.MheSetField = new D9.Schemas.Work._2018_06.Manhourmanageservice.ManHourEntry[val.Count];
       val.CopyTo(this.MheSetField);
     }



    
    


  } // type
} // ns
            





