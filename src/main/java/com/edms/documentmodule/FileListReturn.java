//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.04.28 at 06:01:22 PM IST 
//


package com.edms.documentmodule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FileListReturn complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FileListReturn">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="FileListResult" type="{http://edms.com/documentModule}ArrayOfFiles" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FileListReturn", propOrder = {
    "success",
    "fileListResult"
})
public class FileListReturn {

    @XmlElement(name = "Success")
    protected boolean success;
    @XmlElement(name = "FileListResult")
    protected ArrayOfFiles fileListResult;

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the fileListResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFiles }
     *     
     */
    public ArrayOfFiles getFileListResult() {
        return fileListResult;
    }

    /**
     * Sets the value of the fileListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFiles }
     *     
     */
    public void setFileListResult(ArrayOfFiles value) {
        this.fileListResult = value;
    }

}
