//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.07.23 at 05:45:15 PM IST 
//


package com.edms.workflow;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StartFormData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StartFormData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="startFormProperties" type="{http://edms.com/Workflow}StartFormProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="formKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StartFormData", propOrder = {
    "startFormProperties",
    "formKey"
})
public class StartFormData {

    @XmlElement(nillable = true)
    protected List<StartFormProperty> startFormProperties;
    @XmlElement(required = true)
    protected String formKey;

    /**
     * Gets the value of the startFormProperties property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the startFormProperties property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStartFormProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StartFormProperty }
     * 
     * 
     */
    public List<StartFormProperty> getStartFormProperties() {
        if (startFormProperties == null) {
            startFormProperties = new ArrayList<StartFormProperty>();
        }
        return this.startFormProperties;
    }

    /**
     * Gets the value of the formKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormKey() {
        return formKey;
    }

    /**
     * Sets the value of the formKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormKey(String value) {
        this.formKey = value;
    }

}
