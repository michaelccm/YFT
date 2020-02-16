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
  public partial class ManHourInfo 
  {

         private bool IsHourlyBasedUserField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="isHourlyBasedUser")]
     public bool IsHourlyBasedUser
     { 
        get { return this.IsHourlyBasedUserField;}
        set { this.IsHourlyBasedUserField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public bool getIsHourlyBasedUser()
     { 
       return this.IsHourlyBasedUserField;
     }
     public void setIsHourlyBasedUser(bool val)
     { 
       this.IsHourlyBasedUserField = val;
     }


     private string MyMonthField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="myMonth")]
     public string MyMonth
     { 
        get { return this.MyMonthField;}
        set { this.MyMonthField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMyMonth()
     { 
       return this.MyMonthField;
     }
     public void setMyMonth(string val)
     { 
       this.MyMonthField = val;
     }


     private string MyYearField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="myYear")]
     public string MyYear
     { 
        get { return this.MyYearField;}
        set { this.MyYearField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMyYear()
     { 
       return this.MyYearField;
     }
     public void setMyYear(string val)
     { 
       this.MyYearField = val;
     }


     private string MyUserNameField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="myUserName")]
     public string MyUserName
     { 
        get { return this.MyUserNameField;}
        set { this.MyUserNameField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMyUserName()
     { 
       return this.MyUserNameField;
     }
     public void setMyUserName(string val)
     { 
       this.MyUserNameField = val;
     }


     private bool IsManHourEditableField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="isManHourEditable")]
     public bool IsManHourEditable
     { 
        get { return this.IsManHourEditableField;}
        set { this.IsManHourEditableField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public bool getIsManHourEditable()
     { 
       return this.IsManHourEditableField;
     }
     public void setIsManHourEditable(bool val)
     { 
       this.IsManHourEditableField = val;
     }


     private int TotalDaysField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="totalDays")]
     public int TotalDays
     { 
        get { return this.TotalDaysField;}
        set { this.TotalDaysField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public int getTotalDays()
     { 
       return this.TotalDaysField;
     }
     public void setTotalDays(int val)
     { 
       this.TotalDaysField = val;
     }


     private string MyPositionField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlAttributeAttribute(AttributeName="myPosition")]
     public string MyPosition
     { 
        get { return this.MyPositionField;}
        set { this.MyPositionField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public string getMyPosition()
     { 
       return this.MyPositionField;
     }
     public void setMyPosition(string val)
     { 
       this.MyPositionField = val;
     }


     private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourMonthTmp[] TheMonthTmpField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("theMonthTmp")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourMonthTmp[] TheMonthTmp
     { 
        get { return this.TheMonthTmpField;}
        set { this.TheMonthTmpField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getTheMonthTmp()
     { 
         if(this.TheMonthTmpField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.TheMonthTmpField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setTheMonthTmp(System.Collections.ArrayList val)
     { 
       this.TheMonthTmpField = new D6.Schemas.Work._2012_02.Manhourmanagement.ManHourMonthTmp[val.Count];
       val.CopyTo(this.TheMonthTmpField);
     }


     private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[] TheProgramSetField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("theProgramSet")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[] TheProgramSet
     { 
        get { return this.TheProgramSetField;}
        set { this.TheProgramSetField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getTheProgramSet()
     { 
         if(this.TheProgramSetField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.TheProgramSetField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setTheProgramSet(System.Collections.ArrayList val)
     { 
       this.TheProgramSetField = new D6.Schemas.Work._2012_02.Manhourmanagement.ManHourProgram[val.Count];
       val.CopyTo(this.TheProgramSetField);
     }


     private D6.Schemas.Work._2012_02.Manhourmanagement.ManHourBillType[] TheBillTypeSetField;
     ///<summary>XML Serialization Attributes</summary>
     [System.Xml.Serialization.XmlElementAttribute("theBillTypeSet")]
     public D6.Schemas.Work._2012_02.Manhourmanagement.ManHourBillType[] TheBillTypeSet
     { 
        get { return this.TheBillTypeSetField;}
        set { this.TheBillTypeSetField = value;}
     }

     ///<summary>To support access via generated code.</summary>
     public System.Collections.ArrayList getTheBillTypeSet()
     { 
         if(this.TheBillTypeSetField==null)
         { 
             return new System.Collections.ArrayList();
         }
             return new System.Collections.ArrayList(this.TheBillTypeSetField);
     } 
     ///<summary>Set the vaule of variable </summary> 
     public void setTheBillTypeSet(System.Collections.ArrayList val)
     { 
       this.TheBillTypeSetField = new D6.Schemas.Work._2012_02.Manhourmanagement.ManHourBillType[val.Count];
       val.CopyTo(this.TheBillTypeSetField);
     }



    
    


  } // type
} // ns
            





