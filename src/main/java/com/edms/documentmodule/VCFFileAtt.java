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
 * <p>Java class for VCFFileAtt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="VCFFileAtt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ContactEmail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactPhoto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactDept" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ContactFileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "VCFFileAtt", propOrder = {
    "contactEmail",
    "contactPhoto",
    "contactName",
    "contactPhone",
    "contactAddress",
    "contactDept",
    "contactFileName"
})
public class VCFFileAtt {

    @XmlElement(name = "ContactEmail", required = true)
    protected String contactEmail;
    @XmlElement(name = "ContactPhoto", required = true)
    protected String contactPhoto;
    @XmlElement(name = "ContactName", required = true)
    protected String contactName;
    @XmlElement(name = "ContactPhone", required = true)
    protected String contactPhone;
    @XmlElement(name = "ContactAddress", required = true)
    protected String contactAddress;
    @XmlElement(name = "ContactDept", required = true)
    protected String contactDept;
    @XmlElement(name = "ContactFileName", required = true)
    protected String contactFileName;

    /**
     * Gets the value of the contactEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the value of the contactEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactEmail(String value) {
        this.contactEmail = value;
    }

    /**
     * Gets the value of the contactPhoto property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPhoto() {
        return contactPhoto;
    }

    /**
     * Sets the value of the contactPhoto property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPhoto(String value) {
        this.contactPhoto = value;
    }

    /**
     * Gets the value of the contactName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Sets the value of the contactName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactName(String value) {
        this.contactName = value;
    }

    /**
     * Gets the value of the contactPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactPhone() {
        return contactPhone;
    }

    /**
     * Sets the value of the contactPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactPhone(String value) {
        this.contactPhone = value;
    }

    /**
     * Gets the value of the contactAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactAddress() {
        return contactAddress;
    }

    /**
     * Sets the value of the contactAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactAddress(String value) {
        this.contactAddress = value;
    }

    /**
     * Gets the value of the contactDept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactDept() {
        return contactDept;
    }

    /**
     * Sets the value of the contactDept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactDept(String value) {
        this.contactDept = value;
    }

    /**
     * Gets the value of the contactFileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactFileName() {
        return contactFileName;
    }

    /**
     * Sets the value of the contactFileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactFileName(String value) {
        this.contactFileName = value;
    }

}
