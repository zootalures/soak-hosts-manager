
package edu.bath.soak.ws.dhcp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDHCPWSClientInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDHCPWSClientInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DHCPWSClientInfo" type="{http://www.bath.edu/soak/ws/dhcp/0.3}DHCPWSClientInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDHCPWSClientInfo", propOrder = {
    "dhcpwsClientInfo"
})
public class ArrayOfDHCPWSClientInfo {

    @XmlElement(name = "DHCPWSClientInfo", nillable = true)
    protected List<DHCPWSClientInfo> dhcpwsClientInfo;

    /**
     * Gets the value of the dhcpwsClientInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dhcpwsClientInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDHCPWSClientInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DHCPWSClientInfo }
     * 
     * 
     */
    public List<DHCPWSClientInfo> getDHCPWSClientInfo() {
        if (dhcpwsClientInfo == null) {
            dhcpwsClientInfo = new ArrayList<DHCPWSClientInfo>();
        }
        return this.dhcpwsClientInfo;
    }

}
