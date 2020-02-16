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
 *                 ManHourMonthTmp
 *             
 * 
 * <p>ManHourMonthTmp complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
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
     * 获取dayOfMonth属性的值。
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
     * 设置dayOfMonth属性的值。
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
     * 获取dayOfWeek属性的值。
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
     * 设置dayOfWeek属性的值。
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
     * 获取isWeekend属性的值。
     * 
     */
    public boolean isIsWeekend() {
        return isWeekend;
    }

    /**
     * 设置isWeekend属性的值。
     * 
     */
    public void setIsWeekend(boolean value) {
        this.isWeekend = value;
    }

    public boolean isSetIsWeekend() {
        return true;
    }

    /**
     * 获取isHoliday属性的值。
     * 
     */
    public boolean isIsHoliday() {
        return isHoliday;
    }

    /**
     * 设置isHoliday属性的值。
     * 
     */
    public void setIsHoliday(boolean value) {
        this.isHoliday = value;
    }

    public boolean isSetIsHoliday() {
        return true;
    }

    /**
     * 获取holidayName属性的值。
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
     * 设置holidayName属性的值。
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
     * 获取isWorkRequired属性的值。
     * 
     */
    public boolean isIsWorkRequired() {
        return isWorkRequired;
    }

    /**
     * 设置isWorkRequired属性的值。
     * 
     */
    public void setIsWorkRequired(boolean value) {
        this.isWorkRequired = value;
    }

    public boolean isSetIsWorkRequired() {
        return true;
    }

}
