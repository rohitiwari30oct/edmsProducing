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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for filesAndFolders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filesAndFolders">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="filesList" type="{http://edms.com/documentModule}ArrayOfFiles"/>
 *         &lt;element name="foldersList" type="{http://edms.com/documentModule}ArrayOfFolders"/>
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filesAndFolders", propOrder = {
    "filesList",
    "foldersList",
    "success"
})
public class FilesAndFolders {

    @XmlElement(required = true)
    protected ArrayOfFiles filesList;
    @XmlElement(required = true)
    protected ArrayOfFolders foldersList;
    protected boolean success;

    /**
     * Gets the value of the filesList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFiles }
     *     
     */
    public ArrayOfFiles getFilesList() {
        return filesList;
    }

    /**
     * Sets the value of the filesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFiles }
     *     
     */
    public void setFilesList(ArrayOfFiles value) {
        this.filesList = value;
    }

    /**
     * Gets the value of the foldersList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFolders }
     *     
     */
    public ArrayOfFolders getFoldersList() {
        return foldersList;
    }

    /**
     * Sets the value of the foldersList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFolders }
     *     
     */
    public void setFoldersList(ArrayOfFolders value) {
        this.foldersList = value;
    }

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

}
