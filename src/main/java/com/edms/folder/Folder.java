//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.01.13 at 05:19:41 PM IST 
//


package com.edms.folder;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Folder complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Folder">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="folderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="parentFolder" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createdBy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="folderPath" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="keywords" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="notes" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modificationDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="no_of_folders" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="no_of_documents" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recycle" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="folderVersionsHistory" type="{http://edms.com/Folder}folderVersionDetail" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user_read" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user_write" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user_delete" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user_security" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="group_read" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="group_write" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="group_delete" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="group_security" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Folder", propOrder = {
    "folderName",
    "parentFolder",
    "createdBy",
    "folderPath",
    "keywords",
    "notes",
    "creationDate",
    "modificationDate",
    "noOfFolders",
    "noOfDocuments",
    "recycle",
    "folderVersionsHistory",
    "userRead",
    "userWrite",
    "userDelete",
    "userSecurity",
    "groupRead",
    "groupWrite",
    "groupDelete",
    "groupSecurity"
})
public class Folder {

    @XmlElement(required = true)
    protected String folderName;
    @XmlElement(required = true)
    protected String parentFolder;
    @XmlElement(required = true)
    protected String createdBy;
    @XmlElement(required = true)
    protected String folderPath;
    protected List<String> keywords;
    @XmlElement(required = true)
    protected String notes;
    @XmlElement(required = true)
    protected String creationDate;
    @XmlElement(required = true)
    protected String modificationDate;
    @XmlElement(name = "no_of_folders", required = true)
    protected String noOfFolders;
    @XmlElement(name = "no_of_documents", required = true)
    protected String noOfDocuments;
    protected boolean recycle;
    protected List<FolderVersionDetail> folderVersionsHistory;
    @XmlElement(name = "user_read")
    protected List<String> userRead;
    @XmlElement(name = "user_write")
    protected List<String> userWrite;
    @XmlElement(name = "user_delete")
    protected List<String> userDelete;
    @XmlElement(name = "user_security")
    protected List<String> userSecurity;
    @XmlElement(name = "group_read")
    protected List<String> groupRead;
    @XmlElement(name = "group_write")
    protected List<String> groupWrite;
    @XmlElement(name = "group_delete")
    protected List<String> groupDelete;
    @XmlElement(name = "group_security")
    protected List<String> groupSecurity;

    /**
     * Gets the value of the folderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolderName() {
        return folderName;
    }

    /**
     * Sets the value of the folderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolderName(String value) {
        this.folderName = value;
    }

    /**
     * Gets the value of the parentFolder property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentFolder() {
        return parentFolder;
    }

    /**
     * Sets the value of the parentFolder property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentFolder(String value) {
        this.parentFolder = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
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
     * Gets the value of the keywords property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the keywords property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKeywords().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<String>();
        }
        return this.keywords;
    }

    /**
     * Gets the value of the notes property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the value of the notes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotes(String value) {
        this.notes = value;
    }

    /**
     * Gets the value of the creationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the value of the creationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreationDate(String value) {
        this.creationDate = value;
    }

    /**
     * Gets the value of the modificationDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModificationDate() {
        return modificationDate;
    }

    /**
     * Sets the value of the modificationDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModificationDate(String value) {
        this.modificationDate = value;
    }

    /**
     * Gets the value of the noOfFolders property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfFolders() {
        return noOfFolders;
    }

    /**
     * Sets the value of the noOfFolders property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfFolders(String value) {
        this.noOfFolders = value;
    }

    /**
     * Gets the value of the noOfDocuments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNoOfDocuments() {
        return noOfDocuments;
    }

    /**
     * Sets the value of the noOfDocuments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNoOfDocuments(String value) {
        this.noOfDocuments = value;
    }

    /**
     * Gets the value of the recycle property.
     * 
     */
    public boolean isRecycle() {
        return recycle;
    }

    /**
     * Sets the value of the recycle property.
     * 
     */
    public void setRecycle(boolean value) {
        this.recycle = value;
    }

    /**
     * Gets the value of the folderVersionsHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the folderVersionsHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFolderVersionsHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FolderVersionDetail }
     * 
     * 
     */
    public List<FolderVersionDetail> getFolderVersionsHistory() {
        if (folderVersionsHistory == null) {
            folderVersionsHistory = new ArrayList<FolderVersionDetail>();
        }
        return this.folderVersionsHistory;
    }

    /**
     * Gets the value of the userRead property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userRead property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserRead().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUserRead() {
        if (userRead == null) {
            userRead = new ArrayList<String>();
        }
        return this.userRead;
    }

    /**
     * Gets the value of the userWrite property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userWrite property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserWrite().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUserWrite() {
        if (userWrite == null) {
            userWrite = new ArrayList<String>();
        }
        return this.userWrite;
    }

    /**
     * Gets the value of the userDelete property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userDelete property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserDelete().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUserDelete() {
        if (userDelete == null) {
            userDelete = new ArrayList<String>();
        }
        return this.userDelete;
    }

    /**
     * Gets the value of the userSecurity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userSecurity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserSecurity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUserSecurity() {
        if (userSecurity == null) {
            userSecurity = new ArrayList<String>();
        }
        return this.userSecurity;
    }

    /**
     * Gets the value of the groupRead property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupRead property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupRead().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupRead() {
        if (groupRead == null) {
            groupRead = new ArrayList<String>();
        }
        return this.groupRead;
    }

    /**
     * Gets the value of the groupWrite property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupWrite property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupWrite().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupWrite() {
        if (groupWrite == null) {
            groupWrite = new ArrayList<String>();
        }
        return this.groupWrite;
    }

    /**
     * Gets the value of the groupDelete property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupDelete property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupDelete().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupDelete() {
        if (groupDelete == null) {
            groupDelete = new ArrayList<String>();
        }
        return this.groupDelete;
    }

    /**
     * Gets the value of the groupSecurity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupSecurity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupSecurity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupSecurity() {
        if (groupSecurity == null) {
            groupSecurity = new ArrayList<String>();
        }
        return this.groupSecurity;
    }

}
