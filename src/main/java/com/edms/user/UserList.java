//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.09 at 04:33:37 PM IST 
//


package com.edms.user;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userListResult" type="{http://edms.com/User}ArrayOfUsers" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userList", propOrder = {
    "userListResult"
})
public class UserList {

    protected ArrayOfUsers userListResult;

    /**
     * Gets the value of the userListResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUsers }
     *     
     */
    public ArrayOfUsers getUserListResult() {
        return userListResult;
    }

    /**
     * Sets the value of the userListResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUsers }
     *     
     */
    public void setUserListResult(ArrayOfUsers value) {
        this.userListResult = value;
    }

}
