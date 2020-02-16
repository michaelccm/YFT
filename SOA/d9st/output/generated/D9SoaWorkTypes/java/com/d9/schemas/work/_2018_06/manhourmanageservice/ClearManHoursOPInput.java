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
 *       &lt;attribute name="theUser" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="theAction" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="thePara" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "ClearManHoursOPInput")
public class ClearManHoursOPInput
    implements Serializable
{

    @XmlAttribute(name = "theUser", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String theUser;
    @XmlAttribute(name = "theAction", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String theAction;
    @XmlAttribute(name = "thePara", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String thePara;

    /**
     * 获取theUser属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheUser() {
        return theUser;
    }

    /**
     * 设置theUser属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheUser(String value) {
        this.theUser = value;
    }

    public boolean isSetTheUser() {
        return (this.theUser!= null);
    }

    /**
     * 获取theAction属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheAction() {
        return theAction;
    }

    /**
     * 设置theAction属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheAction(String value) {
        this.theAction = value;
    }

    public boolean isSetTheAction() {
        return (this.theAction!= null);
    }

    /**
     * 获取thePara属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getThePara() {
        return thePara;
    }

    /**
     * 设置thePara属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setThePara(String value) {
        this.thePara = value;
    }

    public boolean isSetThePara() {
        return (this.thePara!= null);
    }

}
