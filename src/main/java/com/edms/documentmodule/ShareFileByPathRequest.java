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
 *         &lt;element name="FilePath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="users" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="groups" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="grouppermissions" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userpermissions" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "filePath",
    "userid",
    "password",
    "users",
    "groups",
    "grouppermissions",
    "userpermissions"
})
@XmlRootElement(name = "shareFileByPathRequest")
public class ShareFileByPathRequest {

    @XmlElement(name = "FilePath", required = true)
    protected String filePath;
    @XmlElement(required = true)
    protected String userid;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String users;
    @XmlElement(required = true)
    protected String groups;
    @XmlElement(required = true)
    protected String grouppermissions;
    @XmlElement(required = true)
    protected String userpermissions;

    /**
     * Gets the value of the filePath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the value of the filePath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilePath(String value) {
        this.filePath = value;
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

    /**
     * Gets the value of the users property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsers() {
        return users;
    }

    /**
     * Sets the value of the users property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsers(String value) {
        this.users = value;
    }

    /**
     * Gets the value of the groups property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroups() {
        return groups;
    }

    /**
     * Sets the value of the groups property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroups(String value) {
        this.groups = value;
    }

    /**
     * Gets the value of the grouppermissions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrouppermissions() {
        return grouppermissions;
    }

    /**
     * Sets the value of the grouppermissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrouppermissions(String value) {
        this.grouppermissions = value;
    }

    /**
     * Gets the value of the userpermissions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserpermissions() {
        return userpermissions;
    }

    /**
     * Sets the value of the userpermissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserpermissions(String value) {
        this.userpermissions = value;
    }

}
