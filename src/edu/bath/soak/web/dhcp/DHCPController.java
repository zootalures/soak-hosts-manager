package edu.bath.soak.web.dhcp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import edu.bath.soak.dhcp.DHCPManager;
import edu.bath.soak.dhcp.DHCPServiceEndPoint;
import edu.bath.soak.dhcp.SyncedServiceEndPoint;
import edu.bath.soak.dhcp.SyncedServiceEndPoint.DHCPScopeClientsUpdateInfo;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.dhcp.model.UpdateableDhcpServer;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.net.model.Subnet;

public class DHCPController extends MultiActionController {
	DHCPDao dhcpDao;
	DHCPManager dhcpMgr;
	NetDAO hostsDAO;

	/**
	 * Displays a list of all subnets
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView listServers(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		List<DHCPServer> servers = dhcpDao.getDHCPServers();
		Map<DHCPServer, List<DHCPScope>> ranges = new HashMap<DHCPServer, List<DHCPScope>>();
		Map<DHCPScope, Subnet> subnets = new HashMap<DHCPScope, Subnet>();

		for (DHCPServer server : servers) {
			List<DHCPScope> scopes = dhcpDao.getDHCPScopes(server);
			ranges.put(server, scopes);
			for (DHCPScope scope : scopes) {
				Subnet subnet = hostsDAO.findSubnetContainingIP(scope
						.getMinIP());
				if (subnet != null)
					subnets.put(scope, subnet);
			}
		}

		model.put("servers", servers);
		model.put("scopes", ranges);
		model.put("subnets", subnets);

		return new ModelAndView("dhcp/serverlist", model);
	}

	public ModelAndView viewScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		long id = Long.parseLong(sid);

		DHCPScope scope = dhcpDao.getScope(id);
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("scope", scope);
		model.put("reservations", dhcpDao.getReservationsInScope(scope));
		return new ModelAndView("dhcp/scopeshow", model);
	}

	public ModelAndView updateServer(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		Assert.notNull(sid);
		long id = Long.parseLong(sid);
		DHCPServer s = dhcpDao.getDHCPServer(id);
		Assert.notNull(s);
		Assert.isInstanceOf(UpdateableDhcpServer.class, s);
		Map<String, Object> model = new HashMap<String, Object>();
		DHCPServiceEndPoint ep = dhcpMgr.getEndpointForServer(s);
		Assert.isInstanceOf(SyncedServiceEndPoint.class, ep);
		List<DHCPScopeClientsUpdateInfo> updates = ((SyncedServiceEndPoint) ep)
				.syncClientsOnServer(s);
		model.put("server", s);
		model.put("updates", updates);
		return new ModelAndView("dhcp/serverUpdated", model);
	}

	public ModelAndView updateSingleScope(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		Assert.notNull(sid);
		long id = Long.parseLong(sid);
		DHCPScope scope = dhcpDao.getScope(id);
		Assert.notNull(scope);

		DHCPServer server = scope.getServer();

		Assert.isInstanceOf(UpdateableDhcpServer.class, server);
		DHCPServiceEndPoint endPoint = dhcpMgr.getEndpointForServer(server);
		Assert.isInstanceOf(SyncedServiceEndPoint.class, endPoint);
		((SyncedServiceEndPoint) endPoint).syncScopeClients(server, scope);

		return new ModelAndView("redirect:/dhcp/viewScope.do?id=" + id);
	}

	public ModelAndView updateScopes(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String sid = request.getParameter("id");
		Assert.notNull(sid);
		long id = Long.parseLong(sid);
		DHCPServer s = dhcpDao.getDHCPServer(id);
		Assert.notNull(s);
		Assert.isInstanceOf(UpdateableDhcpServer.class, s);
		DHCPServiceEndPoint ep = dhcpMgr.getEndpointForServer(s);
		Assert.isInstanceOf(SyncedServiceEndPoint.class, ep);
		((SyncedServiceEndPoint) ep).syncScopesInfo(s);
		return new ModelAndView("redirect:/dhcp/listServers.do");
	}

	public ModelAndView exportDHCPData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		response.setContentType("text/plain");
		for (DHCPServer server : dhcpDao.getDHCPServers()) {
			for (DHCPScope scope : dhcpDao.getDHCPScopes(server)) {
				for (DHCPReservation res : dhcpDao
						.getReservationsInScope(scope)) {
					if (res instanceof StaticDHCPReservation) {
						StaticDHCPReservation sres = (StaticDHCPReservation) res;
						response.getOutputStream().println(
								sres.getIpAddress().getHostAddress() + " "
										+ sres.getMacAddress().toString());
					}
				}
			}
		}

		return null;
	}

	@Required
	public void setDhcpDao(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}

	@Required
	public void setDhcpMgr(DHCPManager dhcpMgr) {
		this.dhcpMgr = dhcpMgr;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

}
