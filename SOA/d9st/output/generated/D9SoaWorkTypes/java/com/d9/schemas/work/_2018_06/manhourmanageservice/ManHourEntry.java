//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.01 时间 03:11:38 PM CST 
//


package com.d9.schemas.work._2018_06.manhourmanageservice;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * 
 *                 ManHourEntry
 *             
 * 
 * <p>ManHourEntry complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ManHourEntry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="myUserName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myDay" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myDayOfWeek" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myHoliday" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="workRequired" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myPrjName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myPrjId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myTaskType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myTaskTypeDval" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="billType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myRefBR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="workingHours" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myRefTE" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tseStatus" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myRefMHE" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="row" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="col" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="billTypeInternal" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManHourEntry")
public class ManHourEntry
    implements Serializable
{

    @XmlAttribute(name = "myUserName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myUserName;
    @XmlAttribute(name = "myYear", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myYear;
    @XmlAttribute(name = "myMonth", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myMonth;
    @XmlAttribute(name = "myDay", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myDay;
    @XmlAttribute(name = "myDayOfWeek", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myDayOfWeek;
    @XmlAttribute(name = "myHoliday", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myHoliday;
    @XmlAttribute(name = "workRequired", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String workRequired;
    @XmlAttribute(name = "myPrjName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myPrjName;
    @XmlAttribute(name = "myPrjId", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myPrjId;
    @XmlAttribute(name = "myTaskType", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myTaskType;
    @XmlAttribute(name = "myTaskTypeDval", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myTaskTypeDval;
    @XmlAttribute(name = "billType", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String billType;
    @XmlAttribute(name = "myRefBR", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myRefBR;
    @XmlAttribute(name = "workingHours", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String workingHours;
    @XmlAttribute(name = "myRefTE", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myRefTE;
    @XmlAttribute(name = "tseStatus", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String tseStatus;
    @XmlAttribute(name = "myRefMHE", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myRefMHE;
    @XmlAttribute(name = "row", required = true)
    protected BigInteger row;
    @XmlAttribute(name = "col", required = true)
    protected BigInteger col;
    @XmlAttribute(name = "billTypeInternal", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String billTypeInternal;

    /**
     * 获取myUserName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyUserName() {
        return myUserName;
    }

    /**
     * 设置myUserName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyUserName(String value) {
        this.myUserName = value;
    }

    public boolean isSetMyUserName() {
        return (this.myUserName!= null);
    }

    /**
     * 获取myYear属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyYear() {
        return myYear;
    }

    /**
     * 设置myYear属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyYear(String value) {
        this.myYear = value;
    }

    public boolean isSetMyYear() {
        return (this.myYear!= null);
    }

    /**
     * 获取myMonth属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyMonth() {
        return myMonth;
    }

    /**
     * 设置myMonth属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyMonth(String value) {
        this.myMonth = value;
    }

    public boolean isSetMyMonth() {
        return (this.myMonth!= null);
    }

    /**
     * 获取myDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyDay() {
        return myDay;
    }

    /**
     * 设置myDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyDay(String value) {
        this.myDay = value;
    }

    public boolean isSetMyDay() {
        return (this.myDay!= null);
    }

    /**
     * 获取myDayOfWeek属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyDayOfWeek() {
        return myDayOfWeek;
    }

    /**
     * 设置myDayOfWeek属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyDayOfWeek(String value) {
        this.myDayOfWeek = value;
    }

    public boolean isSetMyDayOfWeek() {
        return (this.myDayOfWeek!= null);
    }

    /**
     * 获取myHoliday属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyHoliday() {
        return myHoliday;
    }

    /**
     * 设置myHoliday属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyHoliday(String value) {
        this.myHoliday = value;
    }

    public boolean isSetMyHoliday() {
        return (this.myHoliday!= null);
    }

    /**
     * 获取workRequired属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkRequired() {
        return workRequired;
    }

    /**
     * 设置workRequired属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkRequired(String value) {
        this.workRequired = value;
    }

    public boolean isSetWorkRequired() {
        return (this.workRequired!= null);
    }

    /**
     * 获取myPrjName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyPrjName() {
        return myPrjName;
    }

    /**
     * 设置myPrjName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyPrjName(String value) {
        this.myPrjName = value;
    }

    public boolean isSetMyPrjName() {
        return (this.myPrjName!= null);
    }

    /**
     * 获取myPrjId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyPrjId() {
        return myPrjId;
    }

    /**
     * 设置myPrjId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyPrjId(String value) {
        this.myPrjId = value;
    }

    public boolean isSetMyPrjId() {
        return (this.myPrjId!= null);
    }

    /**
     * 获取myTaskType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyTaskType() {
        return myTaskType;
    }

    /**
     * 设置myTaskType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyTaskType(String value) {
        this.myTaskType = value;
    }

    public boolean isSetMyTaskType() {
        return (this.myTaskType!= null);
    }

    /**
     * 获取myTaskTypeDval属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyTaskTypeDval() {
        return myTaskTypeDval;
    }

    /**
     * 设置myTaskTypeDval属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyTaskTypeDval(String value) {
        this.myTaskTypeDval = value;
    }

    public boolean isSetMyTaskTypeDval() {
        return (this.myTaskTypeDval!= null);
    }

    /**
     * 获取billType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillType() {
        return billType;
    }

    /**
     * 设置billType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillType(String value) {
        this.billType = value;
    }

    public boolean isSetBillType() {
        return (this.billType!= null);
    }

    /**
     * 获取myRefBR属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyRefBR() {
        return myRefBR;
    }

    /**
     * 设置myRefBR属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyRefBR(String value) {
        this.myRefBR = value;
    }

    public boolean isSetMyRefBR() {
        return (this.myRefBR!= null);
    }

    /**
     * 获取workingHours属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkingHours() {
        return workingHours;
    }

    /**
     * 设置workingHours属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkingHours(String value) {
        this.workingHours = value;
    }

    public boolean isSetWorkingHours() {
        return (this.workingHours!= null);
    }

    /**
     * 获取myRefTE属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyRefTE() {
        return myRefTE;
    }

    /**
     * 设置myRefTE属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyRefTE(String value) {
        this.myRefTE = value;
    }

    public boolean isSetMyRefTE() {
        return (this.myRefTE!= null);
    }

    /**
     * 获取tseStatus属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTseStatus() {
        return tseStatus;
    }

    /**
     * 设置tseStatus属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTseStatus(String value) {
        this.tseStatus = value;
    }

    public boolean isSetTseStatus() {
        return (this.tseStatus!= null);
    }

    /**
     * 获取myRefMHE属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyRefMHE() {
        return myRefMHE;
    }

    /**
     * 设置myRefMHE属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyRefMHE(String value) {
        this.myRefMHE = value;
    }

    public boolean isSetMyRefMHE() {
        return (this.myRefMHE!= null);
    }

    /**
     * 获取row属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRow() {
        return row;
    }

    /**
     * 设置row属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRow(BigInteger value) {
        this.row = value;
    }

    public boolean isSetRow() {
        return (this.row!= null);
    }

    /**
     * 获取col属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCol() {
        return col;
    }

    /**
     * 设置col属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCol(BigInteger value) {
        this.col = value;
    }

    public boolean isSetCol() {
        return (this.col!= null);
    }

    /**
     * 获取billTypeInternal属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillTypeInternal() {
        return billTypeInternal;
    }

    /**
     * 设置billTypeInternal属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillTypeInternal(String value) {
        this.billTypeInternal = value;
    }

    public boolean isSetBillTypeInternal() {
        return (this.billTypeInternal!= null);
    }

}
