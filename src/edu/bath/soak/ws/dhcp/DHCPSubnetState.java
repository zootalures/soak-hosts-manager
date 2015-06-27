
package edu.bath.soak.ws.dhcp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DHCPSubnetState.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="DHCPSubnetState">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DhcpSubnetEnabled"/>
 *     &lt;enumeration value="DhcpSubnetDisabled"/>
 *     &lt;enumeration value="DhcpSubnetEnabledSwitched"/>
 *     &lt;enumeration value="DhcpSubnetDisabledSwitched"/>
 *     &lt;enumeration value="DhcpSubnetInvalidState"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DHCPSubnetState")
@XmlEnum
public enum DHCPSubnetState {

    @XmlEnumValue("DhcpSubnetEnabled")
    DHCP_SUBNET_ENABLED("DhcpSubnetEnabled"),
    @XmlEnumValue("DhcpSubnetDisabled")
    DHCP_SUBNET_DISABLED("DhcpSubnetDisabled"),
    @XmlEnumValue("DhcpSubnetEnabledSwitched")
    DHCP_SUBNET_ENABLED_SWITCHED("DhcpSubnetEnabledSwitched"),
    @XmlEnumValue("DhcpSubnetDisabledSwitched")
    DHCP_SUBNET_DISABLED_SWITCHED("DhcpSubnetDisabledSwitched"),
    @XmlEnumValue("DhcpSubnetInvalidState")
    DHCP_SUBNET_INVALID_STATE("DhcpSubnetInvalidState");
    private final String value;

    DHCPSubnetState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DHCPSubnetState fromValue(String v) {
        for (DHCPSubnetState c: DHCPSubnetState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
