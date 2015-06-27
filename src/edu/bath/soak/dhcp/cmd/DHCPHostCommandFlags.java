package edu.bath.soak.dhcp.cmd;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import edu.bath.soak.cmd.RenderableCommandOption;
import edu.bath.soak.web.BeanView;
import edu.bath.soak.web.BeanViews;

/**
 * Command Flag object which is used to augment settings for host commands
 * 
 * @author cspocc
 * 
 */

@BeanViews( {
		@BeanView(value = "beanview/dhcp/DHCPHostCommandFlags"),
		@BeanView(value = "beanview/dhcp/DHCPHostCommandFlags-form", view = "form") })
@XmlRootElement
public class DHCPHostCommandFlags implements Serializable,
		RenderableCommandOption {
	public static final String DHCP_FLAGS_KEY = "DHCP_FLAGS";

	public int getOrder() {
		return 0;
	}

	boolean refreshDHCP = false;

	public boolean isHasOptionsSet() {
		return refreshDHCP;
	}

	public boolean isRefreshDHCP() {
		return refreshDHCP;
	}

	public void setRefreshDHCP(boolean refreshDHCP) {
		this.refreshDHCP = refreshDHCP;
	}
}
