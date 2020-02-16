//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.01 时间 03:11:38 PM CST 
//


package com.d9.schemas.work._2018_06.manhourmanageservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="manHours" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourEntry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="programs" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourProgram" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="username" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="year" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="month" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "manHours",
    "programs"
})
@XmlRootElement(name = "SaveOPInput")
public class SaveOPInput
    implements Serializable
{

    protected List<ManHourEntry> manHours;
    protected List<ManHourProgram> programs;
    @XmlAttribute(name = "username", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String username;
    @XmlAttribute(name = "year", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String year;
    @XmlAttribute(name = "month", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String month;

    /**
     * Gets the value of the manHours property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the manHours property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManHours().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourEntry }
     * 
     * 
     */
    public List<ManHourEntry> getManHours() {
        if (manHours == null) {
            manHours = new ArrayList<ManHourEntry>();
        }
        return this.manHours;
    }

    public boolean isSetManHours() {
        return ((this.manHours!= null)&&(!this.manHours.isEmpty()));
    }

    public void unsetManHours() {
        this.manHours = null;
    }

    /**
     * Gets the value of the programs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the programs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrograms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourProgram }
     * 
     * 
     */
    public List<ManHourProgram> getPrograms() {
        if (programs == null) {
            programs = new ArrayList<ManHourProgram>();
        }
        return this.programs;
    }

    public boolean isSetPrograms() {
        return ((this.programs!= null)&&(!this.programs.isEmpty()));
    }

    public void unsetPrograms() {
        this.programs = null;
    }

    /**
     * 获取username属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置username属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsername(String value) {
        this.username = value;
    }

    public boolean isSetUsername() {
        return (this.username!= null);
    }

    /**
     * 获取year属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYear() {
        return year;
    }

    /**
     * 设置year属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYear(String value) {
        this.year = value;
    }

    public boolean isSetYear() {
        return (this.year!= null);
    }

    /**
     * 获取month属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMonth() {
        return month;
    }

    /**
     * 设置month属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMonth(String value) {
        this.month = value;
    }

    public boolean isSetMonth() {
        return (this.month!= null);
    }

}
