package edu.bath.soak.dhcp;

import java.net.Inet4Address;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;

import com.sun.xml.ws.client.BindingProviderProperties;

import edu.bath.soak.dhcp.model.DHCPReservation;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.dhcp.model.WSDHCPServer;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;
import edu.bath.soak.ws.dhcp.ArrayOfDHCPWSClientInfo;
import edu.bath.soak.ws.dhcp.DHCPWSClientInfo;
import edu.bath.soak.ws.dhcp.DHCPWSSubnetInfo;
import edu.bath.soak.ws.dhcp.DHCPWSvc;
import edu.bath.soak.ws.dhcp.DHCPWSvcSoap;

/*******************************************************************************
 * Web services based endpoint,
 * 
 * In order to use this end point you must deploy a web service agent on the
 * remote DHCP server (or another attached machine)
 * 
 * 
 * @author cspocc
 * 
 */
public class WSStaticDHCPServiceEndpoint extends DBBackedDHCPServiceEndpoint
		implements SyncedServiceEndPoint {

	public static final QName WS_NAME = new QName(
			"http://www.bath.edu/soak/ws/dhcp/0.3", "DHCPWSvc");
	Logger log = Logger.getLogger(this.getClass());
	public static URL wsdlLoc;
	static {
		try {
			wsdlLoc = (new ClassPathResource("wsdl/DHCPWsvc.wsdl")).getURL();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public DHCPWSvcSoap getDHCPWSvcSoap(final WSDHCPServer server) {

		// ignore SSL certificate verification
		HostnameVerifier verifier = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
		DHCPWSvc dhcpWsvc = new DHCPWSvc(wsdlLoc, WS_NAME);

		DHCPWSvcSoap soap = (DHCPWSvcSoap) dhcpWsvc.getPort(DHCPWSvcSoap.class);
		((BindingProvider) soap).getRequestContext().put(
				BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				server.getAgentUrl().toString());
		((BindingProvider) soap).getRequestContext().put(
				BindingProvider.USERNAME_PROPERTY, server.getUserName());
		((BindingProvider) soap).getRequestContext().put(
				BindingProvider.PASSWORD_PROPERTY, server.getPassword());

		((BindingProvider) soap).getRequestContext().put(
				BindingProviderProperties.HOSTNAME_VERIFIER, verifier);

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// We don't do server validation here.
			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					// TODO Auto-generated method stub

				}

				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			sslContext.init(null, new TrustManager[] { tm }, null);
			((BindingProvider) soap).getRequestContext().put(
					BindingProviderProperties.SSL_SOCKET_FACTORY,
					sslContext.getSocketFactory());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return soap;
	}

	public void createReservation(DHCPServer server, DHCPReservation res)
			throws DHCPException {
		try {
			Assert.notNull(server);
			Assert.isTrue(server instanceof WSDHCPServer);
			Assert.notNull(res);
			Assert.isInstanceOf(StaticDHCPReservation.class, res);
			Assert.notNull(res.getScope());
			Assert.notNull(res.getMacAddress());

			StaticDHCPReservation sres = (StaticDHCPReservation) res;
			Assert.notNull(sres.getIpAddress());

			WSDHCPServer theserver = (WSDHCPServer) server;
			getDHCPWSvcSoap(theserver).createReservation(
					theserver.getServerIP().getHostAddress(),
					sres.getScope().getMinIP().getHostAddress(),
					sres.getIpAddress().getHostAddress(),
					res.getMacAddress().toString(), sres.getHostName(),
					sres.getComment());
			super.createReservation(server, res);
		} catch (Exception e) {
			throw new DHCPException(e);
		}

	}

	public void deleteReservation(DHCPServer server, DHCPReservation res)
			throws DHCPException {
		Assert.notNull(server);
		Assert.isTrue(server instanceof WSDHCPServer);
		Assert.notNull(res);
		Assert.isInstanceOf(StaticDHCPReservation.class, res);

		StaticDHCPReservation sres = (StaticDHCPReservation) res;
		try {
			WSDHCPServer theserver = (WSDHCPServer) server;
			getDHCPWSvcSoap(theserver).deleteReservation(
					theserver.getServerIP().getHostAddress(),
					sres.getScope().getMinIP().getHostAddress(),
					sres.getIpAddress().getHostAddress(),
					sres.getMacAddress().toString());
			super.deleteReservation(server, res);
		} catch (Exception e) {
			throw new DHCPException(e);
		}

	}

	public static StaticDHCPReservation DHCPWSClientInfo2StaticDHCPReservation(
			DHCPScope scope, DHCPWSClientInfo clinfo) {
		StaticDHCPReservation res = new StaticDHCPReservation();
		res.setHostName(clinfo.getHostName());
		res.setIpAddress(TypeUtils.txtToIP(clinfo.getIPAddress()));
		res.setMacAddress(MacAddress.fromText(clinfo.getMacAddress()));
		res.setComment(clinfo.getComment());
		res.setScope(scope);
		return res;
	}

	public List<StaticDHCPReservation> getRemoteClientsInScope(
			DHCPServer server, DHCPScope scope) throws DHCPException {
		Assert.notNull(server);
		Assert.notNull(scope);
		Assert.isTrue(server instanceof WSDHCPServer);

		try {
			List<StaticDHCPReservation> ress = new ArrayList<StaticDHCPReservation>();

			WSDHCPServer theserver = (WSDHCPServer) server;
			for (DHCPWSClientInfo clinfo : getDHCPWSvcSoap(theserver)
					.getSubnetClients(theserver.getServerIP().getHostAddress(),
							scope.getMinIP().getHostAddress())
					.getDHCPWSClientInfo()) {

				ress.add(DHCPWSClientInfo2StaticDHCPReservation(scope, clinfo));
			}
			return ress;

		} catch (Exception e) {
			throw new DHCPException(e);
		}

	}

	public List<DHCPWSSubnetInfo> getRemoteScopes(DHCPServer server)
			throws DHCPException {
		Assert.notNull(server);
		try {

			WSDHCPServer theserver = (WSDHCPServer) server;
			List<DHCPWSSubnetInfo> infos = getDHCPWSvcSoap(theserver)
					.getSubnets(theserver.getServerIP().getHostAddress())
					.getDHCPWSSubnetInfo();
			return infos;

		} catch (Exception e) {
			throw new DHCPException(e);
		}
	}

	public static DHCPScope DHCPWSSubnetInfo2DHCPRange(DHCPServer server,
			DHCPWSSubnetInfo sinfo, DHCPScope range) {
		if (range == null)
			range = new DHCPScope();

		Inet4Address minIP = TypeUtils.txtToIP(sinfo.getSubnetBase());
		range.setMinIP(minIP);
		range.setMaxIP(TypeUtils.getCIDRMaxAddress(minIP, TypeUtils
				.numNetmaskBits(TypeUtils.txtToIP(sinfo.getSubnetMask()))));
		range.setComment(sinfo.getSubnetComment());
		range.setName(sinfo.getSubnetName());
		range.setServer(server);
		return range;
	}

	public DHCPWSClientInfo getRemoteClientInfo(DHCPServer server,
			Inet4Address address) throws DHCPException {
		try {

			WSDHCPServer theserver = (WSDHCPServer) server;
			DHCPWSClientInfo cinfo = getDHCPWSvcSoap(theserver).getClientInfo(
					theserver.getServerIP().getHostAddress(),
					address.getHostAddress());
			return cinfo;

		} catch (Exception e) {
			throw new DHCPException(e);
		}
	}

	public boolean supportsServer(Class<DHCPServer> serverClass) {
		return WSDHCPServer.class.isAssignableFrom(serverClass);
	}

	public void syncScopesInfo(DHCPServer server) throws DHCPException {
		List<DHCPScope> existingRanges = getDhcpDAO().getDHCPScopes(server);

		List<DHCPWSSubnetInfo> remoteRanges = getRemoteScopes(server);
		log.trace("Got " + remoteRanges.size() + " remote ranges");

		for (DHCPWSSubnetInfo remoteRange : remoteRanges) {

			DHCPScope convRange = DHCPWSSubnetInfo2DHCPRange(server,
					remoteRange, null);

			DHCPScope newRange;

			int idx = existingRanges.indexOf(convRange);

			if (-1 != idx) {
				newRange = DHCPWSSubnetInfo2DHCPRange(server, remoteRange,
						existingRanges.get(idx));
			} else {
				newRange = convRange;

			}

			dhcpDAO.saveScope(newRange);
		}
		((WSDHCPServer) server).setLastSubnetsFetched(new Date());
		dhcpDAO.saveDHCPServer(server);

	}

	/**
	 * Updates all clients on a given scope
	 * 
	 * Writes scope changes back to the database, and
	 * 
	 * @param server
	 *            the server to update, this should be a {@link WSDHCPServer}
	 * @param scope
	 *            the scope to update
	 */
	public DHCPScopeClientsUpdateInfo syncScopeClients(DHCPServer server,
			DHCPScope scope) {
		DHCPScopeClientsUpdateInfo info = new DHCPScopeClientsUpdateInfo();
		info.setServer(server);
		info.setScope(scope);
		List<StaticDHCPReservation> existingReservations = new ArrayList<StaticDHCPReservation>();

		existingReservations.addAll(dhcpDAO.getReservationsInScope(scope));

		WSDHCPServer theserver = (WSDHCPServer) server;

		ArrayOfDHCPWSClientInfo serverWsClientInfos = getDHCPWSvcSoap(theserver)
				.getSubnetClients(theserver.getServerIP().getHostAddress(),
						scope.getMinIP().getHostAddress());

		if (null == serverWsClientInfos) {
			throw new DHCPException("Unable to fetch clients from scope "
					+ scope.getMinIP().getHostAddress());
		}

		Set<StaticDHCPReservation> serverClientInfos = new HashSet<StaticDHCPReservation>();
		Set<StaticDHCPReservation> toAdd = new HashSet<StaticDHCPReservation>();
		for (DHCPWSClientInfo wsc : serverWsClientInfos.getDHCPWSClientInfo()) {

			StaticDHCPReservation serverRes = DHCPWSClientInfo2StaticDHCPReservation(
					scope, wsc);
			serverClientInfos.add(serverRes);
			if (!existingReservations.contains(serverRes)) {
				serverRes.setUpdated(new Date());
				toAdd.add(serverRes);
			}
		}

		Set<StaticDHCPReservation> toDelete = new HashSet<StaticDHCPReservation>();
		for (StaticDHCPReservation oldrs : existingReservations) {
			if (!serverClientInfos.contains(oldrs)) {
				toDelete.add(oldrs);
			}
		}

		for (StaticDHCPReservation addRes : toAdd) {
			dhcpDAO.saveReservation(addRes);
		}
		for (StaticDHCPReservation delRes : toDelete) {
			dhcpDAO.deleteReservation(delRes.getId());
		}

		scope.setFetchedOn(new Date());
		dhcpDAO.saveScope(scope);

		info.setNumAdded(toAdd.size());
		info.setNumDeleted(toDelete.size());

		return info;

	}

	public List<DHCPScopeClientsUpdateInfo> syncClientsOnServer(
			DHCPServer server) {
		Assert.isInstanceOf(WSDHCPServer.class, server);

		log.info("Updating DHCP Clients on server " + server.getDisplayName());
		ArrayList<DHCPScopeClientsUpdateInfo> updates = new ArrayList<DHCPScopeClientsUpdateInfo>();

		syncScopesInfo((WSDHCPServer) server);

		for (DHCPScope scope : dhcpDAO.getDHCPScopes(server)) {
			log.debug("Updating DHCP Clients on  scope "
					+ server.getDisplayName() + " : "
					+ scope.getMinIP().getHostAddress());
			DHCPScopeClientsUpdateInfo upd = syncScopeClients(server, scope);
			log.debug("Got " + upd.getNumAdded() + " additions "
					+ upd.getNumDeleted() + "deletions");
			updates.add(upd);

		}

		return updates;
	}

	public int getOrder() {
		return HIGHEST_PRECEDENCE;
	}

	public boolean supportsServer(DHCPServer server) {
		return WSDHCPServer.class.equals(server.getClass());
	}
}
