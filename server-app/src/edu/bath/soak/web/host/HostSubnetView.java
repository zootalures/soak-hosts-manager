package edu.bath.soak.web.host;

import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.web.BeanView;

@BeanView("beanview/host/HostSubnetView")
public class HostSubnetView {
	Subnet subnet;
	Host host;

	public HostSubnetView(Host host, Subnet subnet) {
		this.host = host;
		this.subnet = subnet;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public Subnet getSubnet() {
		return subnet;
	}

	public void setSubnet(Subnet subnet) {
		this.subnet = subnet;
	}

}
