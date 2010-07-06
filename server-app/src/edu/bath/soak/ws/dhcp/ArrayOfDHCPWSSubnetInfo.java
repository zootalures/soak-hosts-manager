
package edu.bath.soak.ws.dhcp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfDHCPWSSubnetInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDHCPWSSubnetInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DHCPWSSubnetInfo" type="{http://www.bath.edu/soak/ws/dhcp/0.3}DHCPWSSubnetInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDHCPWSSubnetInfo", propOrder = {
    "dhcpwsSubnetInfo"
})
public class ArrayOfDHCPWSSubnetInfo {

    @XmlElement(name = "DHCPWSSubnetInfo", nillable = true)
    protected List<DHCPWSSubnetInfo> dhcpwsSubnetInfo;

    /**
     * Gets the value of the dhcpwsSubnetInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dhcpwsSubnetInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDHCPWSSubnetInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DHCPWSSubnetInfo }
     * 
     * 
     */
    public List<DHCPWSSubnetInfo> getDHCPWSSubnetInfo() {
        if (dhcpwsSubnetInfo == null) {
            dhcpwsSubnetInfo = new ArrayList<DHCPWSSubnetInfo>();
        }
        return this.dhcpwsSubnetInfo;
    }

}
