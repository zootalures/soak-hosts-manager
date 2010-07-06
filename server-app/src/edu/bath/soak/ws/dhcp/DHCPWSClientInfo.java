
package edu.bath.soak.ws.dhcp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DHCPWSClientInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DHCPWSClientInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Comment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FetchedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="HostName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LeaseTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="MacAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="NetMask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DHCPWSClientInfo", propOrder = {
    "comment",
    "fetchedOn",
    "hostName",
    "ipAddress",
    "leaseTime",
    "macAddress",
    "netMask"
})
public class DHCPWSClientInfo {

    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "FetchedOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fetchedOn;
    @XmlElement(name = "HostName")
    protected String hostName;
    @XmlElement(name = "IPAddress")
    protected String ipAddress;
    @XmlElement(name = "LeaseTime", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar leaseTime;
    @XmlElement(name = "MacAddress")
    protected String macAddress;
    @XmlElement(name = "NetMask")
    protected String netMask;

    /**
     * Gets the value of the comment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the fetchedOn property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFetchedOn() {
        return fetchedOn;
    }

    /**
     * Sets the value of the fetchedOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFetchedOn(XMLGregorianCalendar value) {
        this.fetchedOn = value;
    }

    /**
     * Gets the value of the hostName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Sets the value of the hostName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostName(String value) {
        this.hostName = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPAddress(String value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the leaseTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLeaseTime() {
        return leaseTime;
    }

    /**
     * Sets the value of the leaseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLeaseTime(XMLGregorianCalendar value) {
        this.leaseTime = value;
    }

    /**
     * Gets the value of the macAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Sets the value of the macAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMacAddress(String value) {
        this.macAddress = value;
    }

    /**
     * Gets the value of the netMask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetMask() {
        return netMask;
    }

    /**
     * Sets the value of the netMask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetMask(String value) {
        this.netMask = value;
    }

}
