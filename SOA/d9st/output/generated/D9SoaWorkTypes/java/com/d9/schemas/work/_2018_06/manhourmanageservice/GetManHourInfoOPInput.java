//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.8-b130911.1802 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2019.11.01 ʱ�� 03:11:38 PM CST 
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
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
     * ��ȡtheUserName���Ե�ֵ��
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
     * ����theUserName���Ե�ֵ��
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
     * ��ȡtheYear���Ե�ֵ��
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
     * ����theYear���Ե�ֵ��
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
     * ��ȡtheMonth���Ե�ֵ��
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
     * ����theMonth���Ե�ֵ��
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
