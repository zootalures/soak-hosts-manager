/**
 * Core command functionality 
 * 
 * 
 */
@XmlJavaTypeAdapters(value = {
		@XmlJavaTypeAdapter(type = Inet4Address.class, value = Inet4AddressAdapter.class),
		@XmlJavaTypeAdapter(type = MacAddress.class, value = MacAddressAdapter.class) })
@TypeDefs( {
		@TypeDef(name = "inet4type", typeClass = Inet4AddressUserType.class),
		@TypeDef(name = "mactype", typeClass = MacAddressUserType.class) })
package edu.bath.soak.cmd;

import java.net.Inet4Address;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.propertyeditors.*;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

