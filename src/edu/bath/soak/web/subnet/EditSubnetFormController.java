package edu.bath.soak.web.subnet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.mvc.SimpleFormController;

import edu.bath.soak.net.model.HostClass;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;
import edu.bath.soak.net.model.Subnet.HostClassState;
import edu.bath.soak.util.TypeUtils;

/**
 * form which handles creation/update of Subnets
 * 
 * @author cspocc
 * 
 */
public class EditSubnetFormController extends SimpleFormController {
	NetDAO hostsDao;

	public EditSubnetFormController() {
		setFormView("subnet/edit");
		setCommandName("subnetCmd");
		setBindOnNewForm(true);
	}

	Subnet getSubnetFromCommand(EditSubnetCommand cmd) {
		Assert.notNull(cmd);
		Subnet s;
		if (cmd.getId() != null) {
			s = hostsDao.getSubnet(cmd.getId());
		} else {
			s = new Subnet();
		}
		s.setMinIP(cmd.getBaseAddress());
		s.setMaxIP(TypeUtils.getCIDRMaxAddress(cmd.getBaseAddress(), cmd
				.getNumBits()));
		s.setComments(cmd.getComments());
		s.setDescription(cmd.getDescription());
		s.setName(cmd.getName());
		s.setGateway(cmd.getGateway());
		s.setId(cmd.getId());
		s.setVlan(cmd.getVlan());
		s.setNetworkClass(cmd.getNetworkClass());

		for (HostClass hc : hostsDao.getHostClasses()) {
			HostClassState state = cmd.getHostClassPermissions()
					.get(hc.getId());
			Assert.notNull(state);
			if (state != null)
				if (state.equals(HostClassState.ALLOWED)) {
					s.getSubnetAllowedHostClasses().add(hc);

				} else if (state.equals(HostClassState.DENIED)) {
					s.getSubnetDeniedHostClasses().add(hc);

				} else {
					s.getSubnetAllowedHostClasses().remove(hc);
					s.getSubnetDeniedHostClasses().remove(hc);
				}
		}

		return s;
	}

	public EditSubnetCommand getCommandFromSubnet(Subnet s) {
		Assert.notNull(s);
		EditSubnetCommand cmd = new EditSubnetCommand();
		cmd.setBaseAddress(s.getMinIP());
		cmd.setNumBits(s.getMaskBits());
		cmd.setComments(s.getComments());
		cmd.setDescription(s.getDescription());
		cmd.setGateway(s.getGateway());
		cmd.setNetworkClass(s.getNetworkClass());
		cmd.setId(s.getId());
		cmd.setName(s.getName());
		cmd.setNoScan(s.isNoScan());
		cmd.setVlan(s.getVlan());

		for (HostClass hc : hostsDao.getHostClasses()) {
			if (s.getSubnetAllowedHostClasses().contains(hc)) {
				cmd.getHostClassPermissions().put(hc.getId(),
						HostClassState.ALLOWED);
			} else if (s.getSubnetDeniedHostClasses().contains(hc)) {
				cmd.getHostClassPermissions().put(hc.getId(),
						HostClassState.DENIED);

			} else {
				cmd.getHostClassPermissions().put(hc.getId(),
						HostClassState.DEFAULT);

			}
		}

		return cmd;
	}

	@Override
	protected void doSubmitAction(Object command) throws Exception {
		Subnet s = getSubnetFromCommand((EditSubnetCommand) command);
		hostsDao.saveSubnet(s);
	}

	@Required
	public void setHostsDAO(NetDAO hostsDao) {
		this.hostsDao = hostsDao;
	}

	@Override
	protected void onBind(HttpServletRequest request, Object command)
			throws Exception {
		super.onBind(request, command);

	}

	@Override
	protected Object formBackingObject(HttpServletRequest request)
			throws Exception {
		String id = request.getParameter("id");
		EditSubnetCommand cmd;
		if (StringUtils.hasText(id)) {
			cmd = getCommandFromSubnet(hostsDao.getSubnet(Long.parseLong(id)));
		} else {
			cmd = new EditSubnetCommand();
			for (HostClass hc : hostsDao.getHostClasses()) {
				cmd.getHostClassPermissions().put(hc.getId(),
						HostClassState.DEFAULT);
			}

		}
		return cmd;

	}

	@Override
	protected Map referenceData(HttpServletRequest request, Object cmd,
			Errors err) throws Exception {
		EditSubnetCommand ecmd = (EditSubnetCommand) cmd;
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("vlans", hostsDao.getAllVlans());
		model.put("networkClasses", hostsDao.getNetworkClasses());
		model.put("hostClasses", hostsDao.getHostClasses());
		Integer[] subnetValues = new Integer[] { 8, 12, 16, 17, 18, 19, 20, 21,
				22, 23, 24, 25, 26, 27, 28, 29, 30 };
		model.put("subnetValues", subnetValues);
		model.put("orgUnits", hostsDao.getOrgUnits());
		if (null == ecmd.getId()) {
			request.setAttribute("isNew", Boolean.TRUE);
		} else {
			request.setAttribute("isNew", Boolean.FALSE);
		}
		;
		return model;
	}
}
