//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.04.08 at 12:06:39 PM IST 
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
 *         &lt;element name="GetSharedFolders" type="{http://edms.com/documentModule}FolderListReturn" minOccurs="0"/>
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
    "getSharedFolders"
})
@XmlRootElement(name = "getSharedFoldersResponse")
public class GetSharedFoldersResponse {

    @XmlElement(name = "GetSharedFolders")
    protected FolderListReturn getSharedFolders;

    /**
     * Gets the value of the getSharedFolders property.
     * 
     * @return
     *     possible object is
     *     {@link FolderListReturn }
     *     
     */
    public FolderListReturn getGetSharedFolders() {
        return getSharedFolders;
    }

    /**
     * Sets the value of the getSharedFolders property.
     * 
     * @param value
     *     allowed object is
     *     {@link FolderListReturn }
     *     
     */
    public void setGetSharedFolders(FolderListReturn value) {
        this.getSharedFolders = value;
    }

}
