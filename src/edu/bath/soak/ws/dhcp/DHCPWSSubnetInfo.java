
package edu.bath.soak.ws.dhcp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for DHCPWSSubnetInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DHCPWSSubnetInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FetchedOn" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="SubnetBase" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubnetComment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubnetMask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubnetName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SubnetState" type="{http://www.bath.edu/soak/ws/dhcp/0.3}DHCPSubnetState"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DHCPWSSubnetInfo", propOrder = {
    "fetchedOn",
    "subnetBase",
    "subnetComment",
    "subnetMask",
    "subnetName",
    "subnetState"
})
public class DHCPWSSubnetInfo {

    @XmlElement(name = "FetchedOn", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar fetchedOn;
    @XmlElement(name = "SubnetBase")
    protected String subnetBase;
    @XmlElement(name = "SubnetComment")
    protected String subnetComment;
    @XmlElement(name = "SubnetMask")
    protected String subnetMask;
    @XmlElement(name = "SubnetName")
    protected String subnetName;
    @XmlElement(name = "SubnetState", required = true)
    protected DHCPSubnetState subnetState;

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
     * Gets the value of the subnetBase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetBase() {
        return subnetBase;
    }

    /**
     * Sets the value of the subnetBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetBase(String value) {
        this.subnetBase = value;
    }

    /**
     * Gets the value of the subnetComment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetComment() {
        return subnetComment;
    }

    /**
     * Sets the value of the subnetComment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetComment(String value) {
        this.subnetComment = value;
    }

    /**
     * Gets the value of the subnetMask property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetMask() {
        return subnetMask;
    }

    /**
     * Sets the value of the subnetMask property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetMask(String value) {
        this.subnetMask = value;
    }

    /**
     * Gets the value of the subnetName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubnetName() {
        return subnetName;
    }

    /**
     * Sets the value of the subnetName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubnetName(String value) {
        this.subnetName = value;
    }

    /**
     * Gets the value of the subnetState property.
     * 
     * @return
     *     possible object is
     *     {@link DHCPSubnetState }
     *     
     */
    public DHCPSubnetState getSubnetState() {
        return subnetState;
    }

    /**
     * Sets the value of the subnetState property.
     * 
     * @param value
     *     allowed object is
     *     {@link DHCPSubnetState }
     *     
     */
    public void setSubnetState(DHCPSubnetState value) {
        this.subnetState = value;
    }

}
