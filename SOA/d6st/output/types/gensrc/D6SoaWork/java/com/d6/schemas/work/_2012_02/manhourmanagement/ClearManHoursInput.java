//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.06.22 at 01:50:20 PM EDT 
//


package com.d6.schemas.work._2012_02.manhourmanagement;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="theUser" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlRootElement(name = "ClearManHoursInput")
public class ClearManHoursInput
    implements Serializable
{

    @XmlAttribute(required = true)
    protected String theUser;
    @XmlAttribute(required = true)
    protected String theYear;
    @XmlAttribute(required = true)
    protected String theMonth;

    /**
     * Gets the value of the theUser property.
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
     * Sets the value of the theUser property.
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
     * Gets the value of the theYear property.
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
     * Sets the value of the theYear property.
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
     * Gets the value of the theMonth property.
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
     * Sets the value of the theMonth property.
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
