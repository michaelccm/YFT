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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.w3._2001.xmlschema.Adapter1;


/**
 * 
 *                 ManHourBillType
 *             
 * 
 * <p>ManHourBillType complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
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
     * ��ȡmyRefBR���Ե�ֵ��
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
     * ����myRefBR���Ե�ֵ��
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
     * ��ȡbillTypeInternal���Ե�ֵ��
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
     * ����billTypeInternal���Ե�ֵ��
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
     * ��ȡbillRateName���Ե�ֵ��
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
     * ����billRateName���Ե�ֵ��
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
