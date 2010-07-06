
package edu.bath.soak.ws.dhcp;

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
 *         &lt;element name="GetSubnetClientsResult" type="{http://www.bath.edu/soak/ws/dhcp/0.3}ArrayOfDHCPWSClientInfo" minOccurs="0"/>
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
    "getSubnetClientsResult"
})
@XmlRootElement(name = "GetSubnetClientsResponse")
public class GetSubnetClientsResponse {

    @XmlElement(name = "GetSubnetClientsResult")
    protected ArrayOfDHCPWSClientInfo getSubnetClientsResult;

    /**
     * Gets the value of the getSubnetClientsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDHCPWSClientInfo }
     *     
     */
    public ArrayOfDHCPWSClientInfo getGetSubnetClientsResult() {
        return getSubnetClientsResult;
    }

    /**
     * Sets the value of the getSubnetClientsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDHCPWSClientInfo }
     *     
     */
    public void setGetSubnetClientsResult(ArrayOfDHCPWSClientInfo value) {
        this.getSubnetClientsResult = value;
    }

}
