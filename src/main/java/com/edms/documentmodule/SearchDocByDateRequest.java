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
 *         &lt;element name="searchParamValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folderPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="searchParam" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "searchParamValue",
    "folderPath",
    "searchParam",
    "userid",
    "password"
})
@XmlRootElement(name = "searchDocByDateRequest")
public class SearchDocByDateRequest {

    @XmlElement(required = true)
    protected String searchParamValue;
    @XmlElement(required = true)
    protected String folderPath;
    @XmlElement(required = true)
    protected String searchParam;
    @XmlElement(required = true)
    protected String userid;
    @XmlElement(required = true)
    protected String password;

    /**
     * Gets the value of the searchParamValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchParamValue() {
        return searchParamValue;
    }

    /**
     * Sets the value of the searchParamValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchParamValue(String value) {
        this.searchParamValue = value;
    }

    /**
     * Gets the value of the folderPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderPath() {
        return folderPath;
    }

    /**
     * Sets the value of the folderPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderPath(String value) {
        this.folderPath = value;
    }

    /**
     * Gets the value of the searchParam property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSearchParam() {
        return searchParam;
    }

    /**
     * Sets the value of the searchParam property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSearchParam(String value) {
        this.searchParam = value;
    }

    /**
     * Gets the value of the userid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserid() {
        return userid;
    }

    /**
     * Sets the value of the userid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserid(String value) {
        this.userid = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

}
