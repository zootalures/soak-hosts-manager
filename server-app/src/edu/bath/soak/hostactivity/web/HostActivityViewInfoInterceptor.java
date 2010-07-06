package edu.bath.soak.hostactivity.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.Assert;

import edu.bath.soak.hostactivity.model.HostActivityDAO;
import edu.bath.soak.web.host.HostView;
import edu.bath.soak.web.host.HostViewTab;
import edu.bath.soak.web.host.ShowHostInfoInterceptor;

/*******************************************************************************
 * View interceptor which adds host history informatio not he NETWORK_INFO tab
 * 
 * @author cspocc
 * 
 */
public class HostActivityViewInfoInterceptor implements ShowHostInfoInterceptor {
	HostActivityDAO hostActivityDAO;

	public void elaborateView(HostView view, HttpServletRequest request) {
		HostViewTab tab = view.getTabByName(HostView.NETWORK_INFO);
		Assert.notNull(tab);
		HostNetworkActivitySection section = new HostNetworkActivitySection(
				view);

		section.setLastIpSeen(hostActivityDAO.getMacIpInfoForIP(view.getHost()
				.getIpAddress()));
		section.setHistoryForIp(hostActivityDAO.getMacHistoryForIp(view
				.getHost().getIpAddress(), 10));

		if (null != section.getLastIpSeen()) {
			section.setHistoryForMac(hostActivityDAO.getMacHistoryForMac(
					section.getLastIpSeen().getMacAddress(), 10));
		}

		tab.getRenderBeans().add(section);
	}

	public int getOrder() {
		return Integer.MAX_VALUE / 2;
	}

	public void setHostActivityDAO(HostActivityDAO hostActivityDAO) {
		this.hostActivityDAO = hostActivityDAO;
	}

}
