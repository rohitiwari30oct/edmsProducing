//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.04 at 10:57:37 AM IST 
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
 *         &lt;element name="GetSharedFiles" type="{http://edms.com/documentModule}FileListReturn" minOccurs="0"/>
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
    "getSharedFiles"
})
@XmlRootElement(name = "getSharedFilesWithOutStreamResponse")
public class GetSharedFilesWithOutStreamResponse {

    @XmlElement(name = "GetSharedFiles")
    protected FileListReturn getSharedFiles;

    /**
     * Gets the value of the getSharedFiles property.
     * 
     * @return
     *     possible object is
     *     {@link FileListReturn }
     *     
     */
    public FileListReturn getGetSharedFiles() {
        return getSharedFiles;
    }

    /**
     * Sets the value of the getSharedFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileListReturn }
     *     
     */
    public void setGetSharedFiles(FileListReturn value) {
        this.getSharedFiles = value;
    }

}
