package edu.bath.soak.web.admin.namedomain;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.NameDomain;
import edu.bath.soak.net.model.NetDAO;

/**
 * form which handles creation/update of Host Class details
 * 
 * @author cspocc
 * 
 */
public class EditNameDomainFormController extends SimpleFormController {

	NetDAO hostsDao;

	@Override
	protected void doSubmitAction(Object command) throws Exception {

		hostsDao.saveNameDomain(((EditNameDomainCommand) command)
				.getNameDomain());
	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		EditNameDomainCommand cmd = new EditNameDomainCommand();

		cmd.setCreation(true);
		cmd.setNameDomain(new NameDomain());

		return cmd;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

}
