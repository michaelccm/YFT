//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.01 时间 03:11:38 PM CST 
//


package com.d9.schemas.work._2018_06.manhourmanageservice;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="theUserName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="theYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="theMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "GetManHourInfoOPInput")
public class GetManHourInfoOPInput
    implements Serializable
{

    @XmlAttribute(name = "theUserName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String theUserName;
    @XmlAttribute(name = "theYear", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String theYear;
    @XmlAttribute(name = "theMonth", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String theMonth;

    /**
     * 获取theUserName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheUserName() {
        return theUserName;
    }

    /**
     * 设置theUserName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheUserName(String value) {
        this.theUserName = value;
    }

    public boolean isSetTheUserName() {
        return (this.theUserName!= null);
    }

    /**
     * 获取theYear属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheYear() {
        return theYear;
    }

    /**
     * 设置theYear属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheYear(String value) {
        this.theYear = value;
    }

    public boolean isSetTheYear() {
        return (this.theYear!= null);
    }

    /**
     * 获取theMonth属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheMonth() {
        return theMonth;
    }

    /**
     * 设置theMonth属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheMonth(String value) {
        this.theMonth = value;
    }

    public boolean isSetTheMonth() {
        return (this.theMonth!= null);
    }

}
