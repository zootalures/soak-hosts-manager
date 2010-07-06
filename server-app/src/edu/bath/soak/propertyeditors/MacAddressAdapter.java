package edu.bath.soak.propertyeditors;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.bath.soak.util.MacAddress;

public class MacAddressAdapter extends XmlAdapter<String, MacAddress> {
	
	@Override
	public String marshal(MacAddress arg0) throws Exception {
		return arg0.toString();
	}

	@Override
	public MacAddress unmarshal(String arg0) throws Exception {
		return MacAddress.fromText(arg0);
	}
}
 