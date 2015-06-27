package edu.bath.soak.imprt.cmd;

import javax.xml.bind.annotation.XmlRegistry;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostAlias;

@XmlRegistry
public class ObjectFactory {
	public ObjectFactory() {
	}
	public Host createHost(){
		return new Host();
		
	}
	public HostAlias createHostAlias(){
		return new HostAlias();
	}

	
}
