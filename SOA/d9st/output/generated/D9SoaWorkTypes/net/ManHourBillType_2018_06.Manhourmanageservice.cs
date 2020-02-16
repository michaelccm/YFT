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
  public partial class ManHourBillType 
  {

         private string BillTypeInternalField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="billTypeInternal")]
     public string BillTypeInternal
     { 
        get { return this.BillTypeInternalField;}
        set { this.BillTypeInternalField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getBillTypeInternal()
     { 
       return this.BillTypeInternalField;
     }
     public void setBillTypeInternal(string val)
     { 
       this.BillTypeInternalField = val;
     }


     private string BillRateNameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="billRateName")]
     public string BillRateName
     { 
        get { return this.BillRateNameField;}
        set { this.BillRateNameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getBillRateName()
     { 
       return this.BillRateNameField;
     }
     public void setBillRateName(string val)
     { 
       this.BillRateNameField = val;
     }


     private string MyRefBRField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="myRefBR")]
     public string MyRefBR
     { 
        get { return this.MyRefBRField;}
        set { this.MyRefBRField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMyRefBR()
     { 
       return this.MyRefBRField;
     }
     public void setMyRefBR(string val)
     { 
       this.MyRefBRField = val;
     }



    
    


  } // type
} // ns
            





