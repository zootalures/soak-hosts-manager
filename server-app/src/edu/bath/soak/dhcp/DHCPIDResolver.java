package edu.bath.soak.dhcp;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;

import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.xml.SoakXMLIDResolver;

/**
 * Resolves XML identities for DHCP-related xml seriliazations
 * 
 * 
 * @author cspocc
 * 
 */
public class DHCPIDResolver extends SoakXMLIDResolver {
	DHCPDao dhcpDAO;

	public DHCPIDResolver() {
		super(new Class[] { DHCPScope.class });
	}

	@Override
	public void bind(String arg0, Object arg1) throws SAXException {
		// TODO Auto-generated method stub

	}

	@Override
	public Callable<?> resolve(final String id, Class clazz)
			throws SAXException {
		Assert.isTrue(supportsResolving(clazz));
		Assert.notNull(id);

		return new Callable<DHCPScope>() {
			public DHCPScope call() throws Exception {
				return dhcpDAO.getScope(Long.parseLong(id));
			}
		};

	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDAO) {
		this.dhcpDAO = dhcpDAO;
	}

}
