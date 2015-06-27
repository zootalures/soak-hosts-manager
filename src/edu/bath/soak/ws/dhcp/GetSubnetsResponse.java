
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
 *         &lt;element name="GetSubnetsResult" type="{http://www.bath.edu/soak/ws/dhcp/0.3}ArrayOfDHCPWSSubnetInfo" minOccurs="0"/>
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
    "getSubnetsResult"
})
@XmlRootElement(name = "GetSubnetsResponse")
public class GetSubnetsResponse {

    @XmlElement(name = "GetSubnetsResult")
    protected ArrayOfDHCPWSSubnetInfo getSubnetsResult;

    /**
     * Gets the value of the getSubnetsResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDHCPWSSubnetInfo }
     *     
     */
    public ArrayOfDHCPWSSubnetInfo getGetSubnetsResult() {
        return getSubnetsResult;
    }

    /**
     * Sets the value of the getSubnetsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDHCPWSSubnetInfo }
     *     
     */
    public void setGetSubnetsResult(ArrayOfDHCPWSSubnetInfo value) {
        this.getSubnetsResult = value;
    }

}
