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
 *         &lt;element name="searchedFolders" type="{http://edms.com/documentModule}filesAndFolders"/>
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
    "searchedFolders"
})
@XmlRootElement(name = "searchDocByLikeResponse")
public class SearchDocByLikeResponse {

    @XmlElement(required = true)
    protected FilesAndFolders searchedFolders;

    /**
     * Gets the value of the searchedFolders property.
     * 
     * @return
     *     possible object is
     *     {@link FilesAndFolders }
     *     
     */
    public FilesAndFolders getSearchedFolders() {
        return searchedFolders;
    }

    /**
     * Sets the value of the searchedFolders property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilesAndFolders }
     *     
     */
    public void setSearchedFolders(FilesAndFolders value) {
        this.searchedFolders = value;
    }

}
