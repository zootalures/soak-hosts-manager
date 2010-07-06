/**
 * The soak hosts manager.
 *  
 * Most of the functionality is split up by component, 
 *  
 * 
 *  
 */
@XmlJavaTypeAdapters(value = {
		@XmlJavaTypeAdapter(type = Inet4Address.class, value = Inet4AddressAdapter.class),
		@XmlJavaTypeAdapter(type = MacAddress.class, value = MacAddressAdapter.class) })
package edu.bath.soak.imprt.cmd;







import java.net.Inet4Address;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.propertyeditors.*;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

