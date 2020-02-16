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
 *                 ManHourProgram
 *             
 * 
 * <p>ManHourProgram complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ManHourProgram">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="prjId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="prjName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tskType" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="tskTypeDval" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="stkTag" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="startDay" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="endDay" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjStartYear" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjStartMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjStartDay" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjEndYear" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjEndMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="prjEndDay" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="schTag" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManHourProgram")
public class ManHourProgram
    implements Serializable
{

    @XmlAttribute(name = "prjId", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String prjId;
    @XmlAttribute(name = "prjName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String prjName;
    @XmlAttribute(name = "tskType", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String tskType;
    @XmlAttribute(name = "tskTypeDval", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String tskTypeDval;
    @XmlAttribute(name = "stkTag", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String stkTag;
    @XmlAttribute(name = "startDay", required = true)
    protected BigInteger startDay;
    @XmlAttribute(name = "endDay", required = true)
    protected BigInteger endDay;
    @XmlAttribute(name = "prjStartYear", required = true)
    protected BigInteger prjStartYear;
    @XmlAttribute(name = "prjStartMonth", required = true)
    protected BigInteger prjStartMonth;
    @XmlAttribute(name = "prjStartDay", required = true)
    protected BigInteger prjStartDay;
    @XmlAttribute(name = "prjEndYear", required = true)
    protected BigInteger prjEndYear;
    @XmlAttribute(name = "prjEndMonth", required = true)
    protected BigInteger prjEndMonth;
    @XmlAttribute(name = "prjEndDay", required = true)
    protected BigInteger prjEndDay;
    @XmlAttribute(name = "schTag", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String schTag;

    /**
     * 获取prjId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrjId() {
        return prjId;
    }

    /**
     * 设置prjId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrjId(String value) {
        this.prjId = value;
    }

    public boolean isSetPrjId() {
        return (this.prjId!= null);
    }

    /**
     * 获取prjName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrjName() {
        return prjName;
    }

    /**
     * 设置prjName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrjName(String value) {
        this.prjName = value;
    }

    public boolean isSetPrjName() {
        return (this.prjName!= null);
    }

    /**
     * 获取tskType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTskType() {
        return tskType;
    }

    /**
     * 设置tskType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTskType(String value) {
        this.tskType = value;
    }

    public boolean isSetTskType() {
        return (this.tskType!= null);
    }

    /**
     * 获取tskTypeDval属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTskTypeDval() {
        return tskTypeDval;
    }

    /**
     * 设置tskTypeDval属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTskTypeDval(String value) {
        this.tskTypeDval = value;
    }

    public boolean isSetTskTypeDval() {
        return (this.tskTypeDval!= null);
    }

    /**
     * 获取stkTag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStkTag() {
        return stkTag;
    }

    /**
     * 设置stkTag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStkTag(String value) {
        this.stkTag = value;
    }

    public boolean isSetStkTag() {
        return (this.stkTag!= null);
    }

    /**
     * 获取startDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getStartDay() {
        return startDay;
    }

    /**
     * 设置startDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setStartDay(BigInteger value) {
        this.startDay = value;
    }

    public boolean isSetStartDay() {
        return (this.startDay!= null);
    }

    /**
     * 获取endDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getEndDay() {
        return endDay;
    }

    /**
     * 设置endDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setEndDay(BigInteger value) {
        this.endDay = value;
    }

    public boolean isSetEndDay() {
        return (this.endDay!= null);
    }

    /**
     * 获取prjStartYear属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjStartYear() {
        return prjStartYear;
    }

    /**
     * 设置prjStartYear属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjStartYear(BigInteger value) {
        this.prjStartYear = value;
    }

    public boolean isSetPrjStartYear() {
        return (this.prjStartYear!= null);
    }

    /**
     * 获取prjStartMonth属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjStartMonth() {
        return prjStartMonth;
    }

    /**
     * 设置prjStartMonth属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjStartMonth(BigInteger value) {
        this.prjStartMonth = value;
    }

    public boolean isSetPrjStartMonth() {
        return (this.prjStartMonth!= null);
    }

    /**
     * 获取prjStartDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjStartDay() {
        return prjStartDay;
    }

    /**
     * 设置prjStartDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjStartDay(BigInteger value) {
        this.prjStartDay = value;
    }

    public boolean isSetPrjStartDay() {
        return (this.prjStartDay!= null);
    }

    /**
     * 获取prjEndYear属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjEndYear() {
        return prjEndYear;
    }

    /**
     * 设置prjEndYear属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjEndYear(BigInteger value) {
        this.prjEndYear = value;
    }

    public boolean isSetPrjEndYear() {
        return (this.prjEndYear!= null);
    }

    /**
     * 获取prjEndMonth属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjEndMonth() {
        return prjEndMonth;
    }

    /**
     * 设置prjEndMonth属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjEndMonth(BigInteger value) {
        this.prjEndMonth = value;
    }

    public boolean isSetPrjEndMonth() {
        return (this.prjEndMonth!= null);
    }

    /**
     * 获取prjEndDay属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPrjEndDay() {
        return prjEndDay;
    }

    /**
     * 设置prjEndDay属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPrjEndDay(BigInteger value) {
        this.prjEndDay = value;
    }

    public boolean isSetPrjEndDay() {
        return (this.prjEndDay!= null);
    }

    /**
     * 获取schTag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchTag() {
        return schTag;
    }

    /**
     * 设置schTag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchTag(String value) {
        this.schTag = value;
    }

    public boolean isSetSchTag() {
        return (this.schTag!= null);
    }

}
