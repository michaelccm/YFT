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
 *                 ManHourMonthTmp
 *             
 * 
 * <p>ManHourMonthTmp complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ManHourMonthTmp">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="dayOfMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="dayOfWeek" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isWeekend" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isHoliday" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="holidayName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isWorkRequired" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManHourMonthTmp")
public class ManHourMonthTmp
    implements Serializable
{

    @XmlAttribute(name = "dayOfMonth", required = true)
    protected BigInteger dayOfMonth;
    @XmlAttribute(name = "dayOfWeek", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String dayOfWeek;
    @XmlAttribute(name = "isWeekend", required = true)
    protected boolean isWeekend;
    @XmlAttribute(name = "isHoliday", required = true)
    protected boolean isHoliday;
    @XmlAttribute(name = "holidayName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String holidayName;
    @XmlAttribute(name = "isWorkRequired", required = true)
    protected boolean isWorkRequired;

    /**
     * ��ȡdayOfMonth���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * ����dayOfMonth���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDayOfMonth(BigInteger value) {
        this.dayOfMonth = value;
    }

    public boolean isSetDayOfMonth() {
        return (this.dayOfMonth!= null);
    }

    /**
     * ��ȡdayOfWeek���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * ����dayOfWeek���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDayOfWeek(String value) {
        this.dayOfWeek = value;
    }

    public boolean isSetDayOfWeek() {
        return (this.dayOfWeek!= null);
    }

    /**
     * ��ȡisWeekend���Ե�ֵ��
     * 
     */
    public boolean isIsWeekend() {
        return isWeekend;
    }

    /**
     * ����isWeekend���Ե�ֵ��
     * 
     */
    public void setIsWeekend(boolean value) {
        this.isWeekend = value;
    }

    public boolean isSetIsWeekend() {
        return true;
    }

    /**
     * ��ȡisHoliday���Ե�ֵ��
     * 
     */
    public boolean isIsHoliday() {
        return isHoliday;
    }

    /**
     * ����isHoliday���Ե�ֵ��
     * 
     */
    public void setIsHoliday(boolean value) {
        this.isHoliday = value;
    }

    public boolean isSetIsHoliday() {
        return true;
    }

    /**
     * ��ȡholidayName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHolidayName() {
        return holidayName;
    }

    /**
     * ����holidayName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHolidayName(String value) {
        this.holidayName = value;
    }

    public boolean isSetHolidayName() {
        return (this.holidayName!= null);
    }

    /**
     * ��ȡisWorkRequired���Ե�ֵ��
     * 
     */
    public boolean isIsWorkRequired() {
        return isWorkRequired;
    }

    /**
     * ����isWorkRequired���Ե�ֵ��
     * 
     */
    public void setIsWorkRequired(boolean value) {
        this.isWorkRequired = value;
    }

    public boolean isSetIsWorkRequired() {
        return true;
    }

}
