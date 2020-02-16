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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * 
 *                 ManHourBillType
 *             
 * 
 * <p>ManHourBillType complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ManHourBillType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="myRefBR" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="billTypeInternal" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="billRateName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ManHourBillType")
public class ManHourBillType
    implements Serializable
{

    @XmlAttribute(name = "myRefBR", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myRefBR;
    @XmlAttribute(name = "billTypeInternal", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String billTypeInternal;
    @XmlAttribute(name = "billRateName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String billRateName;

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

    /**
     * 获取billRateName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillRateName() {
        return billRateName;
    }

    /**
     * 设置billRateName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillRateName(String value) {
        this.billRateName = value;
    }

    public boolean isSetBillRateName() {
        return (this.billRateName!= null);
    }

}
