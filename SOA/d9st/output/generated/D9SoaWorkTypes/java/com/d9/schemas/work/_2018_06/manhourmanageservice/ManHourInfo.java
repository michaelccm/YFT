//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.8-b130911.1802 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2019.11.01 ʱ�� 03:11:38 PM CST 
//


package com.d9.schemas.work._2018_06.manhourmanageservice;

import java.io.Serializable;
import java.math.BigInteger;
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
 * 
 *                 ManHourInfo
 *             
 * 
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="theMonthTmp" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourMonthTmp" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="theProgramSet" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourProgram" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="theBillTypeSet" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourBillType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="myUserName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myPosition" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myYear" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="myMonth" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="totalDays" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="isHourlyBasedUser" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isManHourEditable" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "theMonthTmp",
    "theProgramSet",
    "theBillTypeSet"
})
@XmlRootElement(name = "ManHourInfo")
public class ManHourInfo
    implements Serializable
{

    protected List<ManHourMonthTmp> theMonthTmp;
    protected List<ManHourProgram> theProgramSet;
    protected List<ManHourBillType> theBillTypeSet;
    @XmlAttribute(name = "myUserName", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myUserName;
    @XmlAttribute(name = "myPosition", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myPosition;
    @XmlAttribute(name = "myYear", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myYear;
    @XmlAttribute(name = "myMonth", required = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    protected String myMonth;
    @XmlAttribute(name = "totalDays", required = true)
    protected BigInteger totalDays;
    @XmlAttribute(name = "isHourlyBasedUser", required = true)
    protected boolean isHourlyBasedUser;
    @XmlAttribute(name = "isManHourEditable", required = true)
    protected boolean isManHourEditable;

    /**
     * Gets the value of the theMonthTmp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theMonthTmp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheMonthTmp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourMonthTmp }
     * 
     * 
     */
    public List<ManHourMonthTmp> getTheMonthTmp() {
        if (theMonthTmp == null) {
            theMonthTmp = new ArrayList<ManHourMonthTmp>();
        }
        return this.theMonthTmp;
    }

    public boolean isSetTheMonthTmp() {
        return ((this.theMonthTmp!= null)&&(!this.theMonthTmp.isEmpty()));
    }

    public void unsetTheMonthTmp() {
        this.theMonthTmp = null;
    }

    /**
     * Gets the value of the theProgramSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theProgramSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheProgramSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourProgram }
     * 
     * 
     */
    public List<ManHourProgram> getTheProgramSet() {
        if (theProgramSet == null) {
            theProgramSet = new ArrayList<ManHourProgram>();
        }
        return this.theProgramSet;
    }

    public boolean isSetTheProgramSet() {
        return ((this.theProgramSet!= null)&&(!this.theProgramSet.isEmpty()));
    }

    public void unsetTheProgramSet() {
        this.theProgramSet = null;
    }

    /**
     * Gets the value of the theBillTypeSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the theBillTypeSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTheBillTypeSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourBillType }
     * 
     * 
     */
    public List<ManHourBillType> getTheBillTypeSet() {
        if (theBillTypeSet == null) {
            theBillTypeSet = new ArrayList<ManHourBillType>();
        }
        return this.theBillTypeSet;
    }

    public boolean isSetTheBillTypeSet() {
        return ((this.theBillTypeSet!= null)&&(!this.theBillTypeSet.isEmpty()));
    }

    public void unsetTheBillTypeSet() {
        this.theBillTypeSet = null;
    }

    /**
     * ��ȡmyUserName���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyUserName() {
        return myUserName;
    }

    /**
     * ����myUserName���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyUserName(String value) {
        this.myUserName = value;
    }

    public boolean isSetMyUserName() {
        return (this.myUserName!= null);
    }

    /**
     * ��ȡmyPosition���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyPosition() {
        return myPosition;
    }

    /**
     * ����myPosition���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyPosition(String value) {
        this.myPosition = value;
    }

    public boolean isSetMyPosition() {
        return (this.myPosition!= null);
    }

    /**
     * ��ȡmyYear���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyYear() {
        return myYear;
    }

    /**
     * ����myYear���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyYear(String value) {
        this.myYear = value;
    }

    public boolean isSetMyYear() {
        return (this.myYear!= null);
    }

    /**
     * ��ȡmyMonth���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMyMonth() {
        return myMonth;
    }

    /**
     * ����myMonth���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMyMonth(String value) {
        this.myMonth = value;
    }

    public boolean isSetMyMonth() {
        return (this.myMonth!= null);
    }

    /**
     * ��ȡtotalDays���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalDays() {
        return totalDays;
    }

    /**
     * ����totalDays���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalDays(BigInteger value) {
        this.totalDays = value;
    }

    public boolean isSetTotalDays() {
        return (this.totalDays!= null);
    }

    /**
     * ��ȡisHourlyBasedUser���Ե�ֵ��
     * 
     */
    public boolean isIsHourlyBasedUser() {
        return isHourlyBasedUser;
    }

    /**
     * ����isHourlyBasedUser���Ե�ֵ��
     * 
     */
    public void setIsHourlyBasedUser(boolean value) {
        this.isHourlyBasedUser = value;
    }

    public boolean isSetIsHourlyBasedUser() {
        return true;
    }

    /**
     * ��ȡisManHourEditable���Ե�ֵ��
     * 
     */
    public boolean isIsManHourEditable() {
        return isManHourEditable;
    }

    /**
     * ����isManHourEditable���Ե�ֵ��
     * 
     */
    public void setIsManHourEditable(boolean value) {
        this.isManHourEditable = value;
    }

    public boolean isSetIsManHourEditable() {
        return true;
    }

}
