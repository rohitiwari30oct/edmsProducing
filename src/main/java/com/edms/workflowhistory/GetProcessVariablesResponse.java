//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.09 at 04:33:37 PM IST 
//


package com.edms.workflowhistory;

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
 *         &lt;element name="histDetVrblInstUpdateEntityReturn" type="{http://edms.com/WorkflowHistory}HistDetVrblInstUpdateEntityReturn"/>
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
    "histDetVrblInstUpdateEntityReturn"
})
@XmlRootElement(name = "getProcessVariablesResponse")
public class GetProcessVariablesResponse {

    @XmlElement(required = true)
    protected HistDetVrblInstUpdateEntityReturn histDetVrblInstUpdateEntityReturn;

    /**
     * Gets the value of the histDetVrblInstUpdateEntityReturn property.
     * 
     * @return
     *     possible object is
     *     {@link HistDetVrblInstUpdateEntityReturn }
     *     
     */
    public HistDetVrblInstUpdateEntityReturn getHistDetVrblInstUpdateEntityReturn() {
        return histDetVrblInstUpdateEntityReturn;
    }

    /**
     * Sets the value of the histDetVrblInstUpdateEntityReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link HistDetVrblInstUpdateEntityReturn }
     *     
     */
    public void setHistDetVrblInstUpdateEntityReturn(HistDetVrblInstUpdateEntityReturn value) {
        this.histDetVrblInstUpdateEntityReturn = value;
    }

}
