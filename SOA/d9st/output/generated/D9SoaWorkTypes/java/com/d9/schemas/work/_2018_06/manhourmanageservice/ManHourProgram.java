//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.8-b130911.1802 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2019.11.01 ʱ�� 03:11:38 PM CST 
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
 * <p>ManHourProgram complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
     * ��ȡprjId���Ե�ֵ��
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
     * ����prjId���Ե�ֵ��
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
     * ��ȡprjName���Ե�ֵ��
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
     * ����prjName���Ե�ֵ��
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
     * ��ȡtskType���Ե�ֵ��
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
     * ����tskType���Ե�ֵ��
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
     * ��ȡtskTypeDval���Ե�ֵ��
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
     * ����tskTypeDval���Ե�ֵ��
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
     * ��ȡstkTag���Ե�ֵ��
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
     * ����stkTag���Ե�ֵ��
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
     * ��ȡstartDay���Ե�ֵ��
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
     * ����startDay���Ե�ֵ��
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
     * ��ȡendDay���Ե�ֵ��
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
     * ����endDay���Ե�ֵ��
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
     * ��ȡprjStartYear���Ե�ֵ��
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
     * ����prjStartYear���Ե�ֵ��
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
     * ��ȡprjStartMonth���Ե�ֵ��
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
     * ����prjStartMonth���Ե�ֵ��
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
     * ��ȡprjStartDay���Ե�ֵ��
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
     * ����prjStartDay���Ե�ֵ��
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
     * ��ȡprjEndYear���Ե�ֵ��
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
     * ����prjEndYear���Ե�ֵ��
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
     * ��ȡprjEndMonth���Ե�ֵ��
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
     * ����prjEndMonth���Ե�ֵ��
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
     * ��ȡprjEndDay���Ե�ֵ��
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
     * ����prjEndDay���Ե�ֵ��
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
     * ��ȡschTag���Ե�ֵ��
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
     * ����schTag���Ե�ֵ��
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
