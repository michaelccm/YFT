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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
     * ��ȡmanHourFilter���Ե�ֵ��
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
     * ����manHourFilter���Ե�ֵ��
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
