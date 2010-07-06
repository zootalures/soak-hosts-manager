package edu.bath.soak.propertyeditors;

import java.net.InetAddress;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import edu.bath.soak.util.TypeUtils;

public class Inet4AddressAdapter extends XmlAdapter<String, InetAddress> {
	@Override
	public String marshal(InetAddress arg0) throws Exception {
		return arg0.getHostAddress();
	}

	@Override
	public InetAddress unmarshal(String arg0) throws Exception {
		return TypeUtils.txtToIP(arg0);
	}

}
