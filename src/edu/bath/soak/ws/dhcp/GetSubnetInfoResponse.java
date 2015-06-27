
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
 *         &lt;element name="GetSubnetInfoResult" type="{http://www.bath.edu/soak/ws/dhcp/0.3}DHCPWSSubnetInfo" minOccurs="0"/>
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
    "getSubnetInfoResult"
})
@XmlRootElement(name = "GetSubnetInfoResponse")
public class GetSubnetInfoResponse {

    @XmlElement(name = "GetSubnetInfoResult")
    protected DHCPWSSubnetInfo getSubnetInfoResult;

    /**
     * Gets the value of the getSubnetInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link DHCPWSSubnetInfo }
     *     
     */
    public DHCPWSSubnetInfo getGetSubnetInfoResult() {
        return getSubnetInfoResult;
    }

    /**
     * Sets the value of the getSubnetInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link DHCPWSSubnetInfo }
     *     
     */
    public void setGetSubnetInfoResult(DHCPWSSubnetInfo value) {
        this.getSubnetInfoResult = value;
    }

}
