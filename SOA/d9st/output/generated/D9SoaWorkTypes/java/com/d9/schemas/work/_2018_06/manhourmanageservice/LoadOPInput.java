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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="manHourFilter" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourEntry"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "manHourFilter"
})
@XmlRootElement(name = "LoadOPInput")
public class LoadOPInput
    implements Serializable
{

    @XmlElement(required = true)
    protected ManHourEntry manHourFilter;

    /**
     * 获取manHourFilter属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ManHourEntry }
     *     
     */
    public ManHourEntry getManHourFilter() {
        return manHourFilter;
    }

    /**
     * 设置manHourFilter属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ManHourEntry }
     *     
     */
    public void setManHourFilter(ManHourEntry value) {
        this.manHourFilter = value;
    }

    public boolean isSetManHourFilter() {
        return (this.manHourFilter!= null);
    }

}
