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
 *         &lt;element name="GetFilesByParentFile" type="{http://edms.com/documentModule}FileListReturn" minOccurs="0"/>
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
    "getFilesByParentFile"
})
@XmlRootElement(name = "getFileResponse")
public class GetFileResponse {

    @XmlElement(name = "GetFilesByParentFile")
    protected FileListReturn getFilesByParentFile;

    /**
     * Gets the value of the getFilesByParentFile property.
     * 
     * @return
     *     possible object is
     *     {@link FileListReturn }
     *     
     */
    public FileListReturn getGetFilesByParentFile() {
        return getFilesByParentFile;
    }

    /**
     * Sets the value of the getFilesByParentFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link FileListReturn }
     *     
     */
    public void setGetFilesByParentFile(FileListReturn value) {
        this.getFilesByParentFile = value;
    }

}
