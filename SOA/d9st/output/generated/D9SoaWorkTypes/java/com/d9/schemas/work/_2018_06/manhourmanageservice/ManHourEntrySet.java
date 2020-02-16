//
// ���ļ����� JavaTM Architecture for XML Binding (JAXB) ����ʵ�� v2.2.8-b130911.1802 ���ɵ�
// ����� <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// �����±���Դģʽʱ, �Դ��ļ��������޸Ķ�����ʧ��
// ����ʱ��: 2019.11.01 ʱ�� 03:11:38 PM CST 
//


package com.d9.schemas.work._2018_06.manhourmanageservice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 ManHourEntrySet
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
 *         &lt;element name="mheSet" type="{http://d9.com/Schemas/Work/2018-06/ManHourManageService}ManHourEntry" maxOccurs="unbounded" minOccurs="0"/>
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
    "mheSet"
})
@XmlRootElement(name = "ManHourEntrySet")
public class ManHourEntrySet
    implements Serializable
{

    protected List<ManHourEntry> mheSet;

    /**
     * Gets the value of the mheSet property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mheSet property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMheSet().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManHourEntry }
     * 
     * 
     */
    public List<ManHourEntry> getMheSet() {
        if (mheSet == null) {
            mheSet = new ArrayList<ManHourEntry>();
        }
        return this.mheSet;
    }

    public boolean isSetMheSet() {
        return ((this.mheSet!= null)&&(!this.mheSet.isEmpty()));
    }

    public void unsetMheSet() {
        this.mheSet = null;
    }

}
