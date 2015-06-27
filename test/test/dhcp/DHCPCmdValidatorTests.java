package test.dhcp;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import test.hosts.SpringSetup;
import edu.bath.soak.dhcp.cmd.DHCPCmd;
import edu.bath.soak.dhcp.cmd.DHCPCmdValidator;
import edu.bath.soak.dhcp.model.DHCPDao;
import edu.bath.soak.dhcp.model.DHCPScope;
import edu.bath.soak.dhcp.model.DHCPServer;
import edu.bath.soak.dhcp.model.StaticDHCPReservation;
import edu.bath.soak.util.MacAddress;
import edu.bath.soak.util.TypeUtils;

public class DHCPCmdValidatorTests extends
		AbstractTransactionalSpringContextTests {

	DHCPCmdValidator dhcpCmdValidator;
	DHCPServer testServer;
	DHCPDao dhcpDao;
	DHCPScope testScope;
	
	

	public DHCPCmdValidatorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DHCP_TEST_LOCS;
	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		// TODO Auto-generated method stub
		super.onSetUpInTransaction();
		testServer = new DHCPServer();
		testServer.setDisplayName("Test DHCP Server");
		dhcpDao.saveDHCPServer(testServer);
		testScope = new DHCPScope();
		testScope.setMinIP(TypeUtils.txtToIP("10.0.0.0"));
		testScope.setMaxIP(TypeUtils.txtToIP("10.255.255.255"));
		testScope.setServer(testServer);
		testScope.setName(" a scope");
		dhcpDao.saveScope(testScope);

	}

	public void testValidateDHCPCmdCreateOK() {
		StaticDHCPReservation staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foo");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);
		DHCPCmd cmd = new DHCPCmd();
		cmd.insertAdd(staticReservation);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);
		assertTrue(!objectErrors.hasErrors());
	}

	public void testValidateDHCPCmdCreateWithDuplicateMacInScope() {

		StaticDHCPReservation staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foobar");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);
		dhcpDao.saveReservation(staticReservation);
		staticReservation = new StaticDHCPReservation();

		staticReservation.setHostName("foobar");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.2"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);

		DHCPCmd cmd = new DHCPCmd();
		cmd.insertAdd(staticReservation);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);
		assertEquals(1, objectErrors.getErrorCount());

	}

	public void testValidateDHCPCmdCreateWithDuplicateIPInScope() {
		testValidateDHCPCmdCreateOK();
		StaticDHCPReservation staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foobar");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);
		dhcpDao.saveReservation(staticReservation);
		staticReservation = new StaticDHCPReservation();

		staticReservation.setHostName("foobar");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:56"));
		staticReservation.setScope(testScope);

		DHCPCmd cmd = new DHCPCmd();
		cmd.insertAdd(staticReservation);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);
		assertEquals(1, objectErrors.getErrorCount());

	}

	public void testValidateDHCPCmdDeleteOK() {
		testValidateDHCPCmdCreateOK();
		StaticDHCPReservation staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foobar");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);
		dhcpDao.saveReservation(staticReservation);

		staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foo");
		staticReservation.setIpAddress(TypeUtils.txtToIP("10.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);

		DHCPCmd cmd = new DHCPCmd();
		cmd.insertDel(staticReservation);

		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);
		assertTrue(!objectErrors.hasErrors());
	}

	public void testValidateDHCPCmdCreateOffScope() {
		StaticDHCPReservation staticReservation = new StaticDHCPReservation();
		staticReservation.setHostName("foo");
		staticReservation.setIpAddress(TypeUtils.txtToIP("11.0.0.1"));
		staticReservation.setMacAddress(MacAddress
				.fromText("00:11:22:33:44:55"));
		staticReservation.setScope(testScope);
		DHCPCmd cmd = new DHCPCmd();
		cmd.insertAdd(staticReservation);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dhcpCmdValidator, cmd, objectErrors);
		assertTrue(objectErrors.hasErrors());
		assertEquals(1, objectErrors.getErrorCount());
	}

	@Required
	public void setDhcpCmdValidator(DHCPCmdValidator dhcpCmdValidator) {
		this.dhcpCmdValidator = dhcpCmdValidator;
	}

	@Required
	public void setDhcpDAO(DHCPDao dhcpDao) {
		this.dhcpDao = dhcpDao;
	}
}
