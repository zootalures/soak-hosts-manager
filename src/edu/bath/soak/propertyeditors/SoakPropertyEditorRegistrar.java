package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditor;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import edu.bath.soak.net.model.NetDAO;

/**
 * Helper delegate which registers the standard type-based property editors for
 * internal soak editors
 * 
 * @author cspocc
 * 
 */
public class SoakPropertyEditorRegistrar implements PropertyEditorRegistrar {

	NetDAO hostsDAO;
	Map<Class, PropertyEditor> customEditors = new HashMap<Class, PropertyEditor>();

	public void registerCustomEditors(PropertyEditorRegistry registry) {
		registry.registerCustomEditor(byte[].class,
				new ByteArrayMultipartFileEditor());
		registry.registerCustomEditor(java.util.Date.class, new CustomDateEditor(
				DateFormat.getDateInstance(DateFormat.SHORT, Locale.UK), true));

		// registry.registerCustomEditor(Inet4Address.class,
		// new Inet4AddressEditor());
		//
		// registry.registerCustomEditor(MacAddress.class, new
		// MacAddressEditor());
		//
		// VlanEditor vle = new VlanEditor();
		// vle.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(Vlan.class, vle);
		// NetworkClassEditor nce = new NetworkClassEditor();
		// nce.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(NetworkClass.class, nce);
		// HostClassEditor hce = new HostClassEditor();
		// hce.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(HostClass.class, hce);
		// SubnetEditor sne = new SubnetEditor();
		// sne.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(Subnet.class, sne);
		// NameDomainEditor nde = new NameDomainEditor();
		// nde.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(NameDomain.class, nde);
		// OrgUnitEditor oue = new OrgUnitEditor();
		// oue.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(OrgUnit.class, oue);
		// HostEditor he = new HostEditor();
		// he.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(Host.class, he);
		//
		// HostNameEditor hne = new HostNameEditor();
		// hne.setHostsDAO(hostsDAO);
		// registry.registerCustomEditor(HostName.class, hne);

		for (Entry<Class, PropertyEditor> entry : customEditors.entrySet()) {
			registry.registerCustomEditor(entry.getKey(), entry.getValue());
		}
	}

	public void setCustomEditors(Map<Class, PropertyEditor> editors) {
		for (Entry<Class, PropertyEditor> entry : editors.entrySet()) {
			customEditors.put(entry.getKey(), entry.getValue());
		}
	}

	public void registerCustomEditor(Class clazz, PropertyEditor pe) {
		customEditors.put(clazz, pe);
	}

	public NetDAO getHostsDAO() {
		return hostsDAO;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}
}