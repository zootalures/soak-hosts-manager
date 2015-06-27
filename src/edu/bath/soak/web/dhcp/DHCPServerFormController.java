package edu.bath.soak.web.dhcp;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.WSDHCPServer;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class DHCPServerFormController extends SimpleFormController {

	
	DHCPDao dhcpDao;
	
	

	
	@Override
	protected void doSubmitAction(Object command) throws Exception {
		dhcpDao.saveDHCPServer((DHCPServer)command);
	}

	
	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		if (StringUtils.hasText(id)) {
			return dhcpDao.getDHCPServer(Long.parseLong(id));
		} else {
			return new WSDHCPServer();
		}

	}


	public void setDhcpDao(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}


}
