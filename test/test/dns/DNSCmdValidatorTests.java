package test.dns;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import test.hosts.SpringSetup;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSCmdValidator;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;
import edu.bath.soak.dns.model.ForwardZone;
import edu.bath.soak.util.TypeUtils;

public class DNSCmdValidatorTests extends
		AbstractTransactionalSpringContextTests {

	DNSCmdValidator dnsCmdValidator;
	DNSDao dnsDao;
	DNSZone testZone;

	public DNSCmdValidatorTests() {
		setAutowireMode(AUTOWIRE_BY_NAME);
	}

	@Override
	protected String[] getConfigLocations() {
		return SpringSetup.DNS_TEST_LOCS;
	}

	@Override
	protected void onSetUpInTransaction() throws Exception {
		// TODO Auto-generated method stub
		super.onSetUpInTransaction();
		testZone = new ForwardZone();
		testZone.setDescription("foo");
		testZone.setDisplayName("test zone");
		testZone.setDomain(".test.");
		testZone.setServerIP(TypeUtils.txtToIP("127.0.0.1"));

		dnsDao.saveZone(testZone);

	}

	public void testValidateDNSCmdCreateOK() {
		DNSCmd cmd = new DNSCmd();
		DNSRecord rec = new DNSRecord();
		rec.setHostName("test" + testZone.getDomain());
		rec.setType("A");
		rec.setTtl(3600L);
		rec.setTarget("10.0.0.1");
		rec.setZone(testZone);

		cmd.insertAdd(rec);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, cmd, objectErrors);
		assertFalse(objectErrors.hasErrors());
	}

	public void testValidateDNSCmdCreateBadZone() {
		DNSCmd cmd = new DNSCmd();
		DNSRecord rec = new DNSRecord();
		rec.setHostName("test.foo.bar.not.in.this.zone.");
		rec.setType("A");
		rec.setTtl(3600L);
		rec.setTarget("10.0.0.1");
		rec.setZone(testZone);

		cmd.insertAdd(rec);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, cmd, objectErrors);
		assertTrue(objectErrors.hasErrors());
	}

	public void testValidateDNSCmdDeleteOK() {
		DNSCmd cmd = new DNSCmd();
		DNSRecord rec = new DNSRecord();
		rec.setHostName("test" + testZone.getDomain());
		rec.setType("A");
		rec.setTtl(3600L);
		rec.setTarget("10.0.0.1");
		rec.setZone(testZone);

		dnsDao.saveRecord(rec);
		cmd.insertDelete(rec);
		Errors objectErrors = new BeanPropertyBindingResult(cmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, cmd, objectErrors);
		assertFalse(objectErrors.hasErrors());
	}

	@Required
	public void setDnsCmdValidator(DNSCmdValidator dnsCmdValidator) {
		this.dnsCmdValidator = dnsCmdValidator;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDao) {
		this.dnsDao = dnsDao;
	}
}
