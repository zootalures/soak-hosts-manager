
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
 *         &lt;element name="GetClientInfoResult" type="{http://www.bath.edu/soak/ws/dhcp/0.3}DHCPWSClientInfo" minOccurs="0"/>
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
    "getClientInfoResult"
})
@XmlRootElement(name = "GetClientInfoResponse")
public class GetClientInfoResponse {

    @XmlElement(name = "GetClientInfoResult")
    protected DHCPWSClientInfo getClientInfoResult;

    /**
     * Gets the value of the getClientInfoResult property.
     * 
     * @return
     *     possible object is
     *     {@link DHCPWSClientInfo }
     *     
     */
    public DHCPWSClientInfo getGetClientInfoResult() {
        return getClientInfoResult;
    }

    /**
     * Sets the value of the getClientInfoResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link DHCPWSClientInfo }
     *     
     */
    public void setGetClientInfoResult(DHCPWSClientInfo value) {
        this.getClientInfoResult = value;
    }

}
