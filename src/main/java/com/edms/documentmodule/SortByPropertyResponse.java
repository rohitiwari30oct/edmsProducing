//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.23 at 05:45:15 PM IST 
//


package com.edms.documentmodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="sortByPropertyRes" type="{http://edms.com/documentModule}sortByPropertyRes"/>
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
    "sortByPropertyRes"
})
@XmlRootElement(name = "sortByPropertyResponse")
public class SortByPropertyResponse {

    @XmlElement(required = true)
    protected SortByPropertyRes sortByPropertyRes;

    /**
     * Gets the value of the sortByPropertyRes property.
     * 
     * @return
     *     possible object is
     *     {@link SortByPropertyRes }
     *     
     */
    public SortByPropertyRes getSortByPropertyRes() {
        return sortByPropertyRes;
    }

    /**
     * Sets the value of the sortByPropertyRes property.
     * 
     * @param value
     *     allowed object is
     *     {@link SortByPropertyRes }
     *     
     */
    public void setSortByPropertyRes(SortByPropertyRes value) {
        this.sortByPropertyRes = value;
    }

}
