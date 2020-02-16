//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.2.8-b130911.1802 生成的
// 请访问 <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.01 时间 03:11:38 PM CST 
//


package org.w3._2001.xmlschema;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, String>
{


    public String unmarshal(String value) {
        return (com.teamcenter.soa.internal.common.utils.SoaStringConverter.parseString(value));
    }

    public String marshal(String value) {
        return (com.teamcenter.soa.internal.common.utils.SoaStringConverter.printString(value));
    }

}
