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
     * ��ȡtheUser���Ե�ֵ��
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
     * ����theUser���Ե�ֵ��
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
     * ��ȡtheAction���Ե�ֵ��
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
     * ����theAction���Ե�ֵ��
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
     * ��ȡthePara���Ե�ֵ��
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
     * ����thePara���Ե�ֵ��
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
