//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.10 at 02:52:19 PM IST 
//


package com.edms.workflow;

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
 *         &lt;element name="groupTaskListReturn" type="{http://edms.com/Workflow}GroupTaskListReturn"/>
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
    "groupTaskListReturn"
})
@XmlRootElement(name = "getFetchGroupTaskResponse")
public class GetFetchGroupTaskResponse {

    @XmlElement(required = true)
    protected GroupTaskListReturn groupTaskListReturn;

    /**
     * Gets the value of the groupTaskListReturn property.
     * 
     * @return
     *     possible object is
     *     {@link GroupTaskListReturn }
     *     
     */
    public GroupTaskListReturn getGroupTaskListReturn() {
        return groupTaskListReturn;
    }

    /**
     * Sets the value of the groupTaskListReturn property.
     * 
     * @param value
     *     allowed object is
     *     {@link GroupTaskListReturn }
     *     
     */
    public void setGroupTaskListReturn(GroupTaskListReturn value) {
        this.groupTaskListReturn = value;
    }

}
