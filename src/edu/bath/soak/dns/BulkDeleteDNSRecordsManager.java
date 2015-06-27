package edu.bath.soak.dns;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import edu.bath.soak.cmd.BaseCompositeCommand;
import edu.bath.soak.cmd.CmdException;
import edu.bath.soak.cmd.CmdValidationException;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.UICommand;
import edu.bath.soak.dns.cmd.BulkDeleteDNSRecordsCmd;
import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.cmd.DNSCmdValidator;
import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.net.model.NetDAO;

public class BulkDeleteDNSRecordsManager implements CommandExpander {

	public boolean canExpand(Class clazz) {
		return BulkDeleteDNSRecordsCmd.class.isAssignableFrom(clazz);
	}

	Logger log = Logger.getLogger(BulkDeleteDNSRecordsManager.class);
	NetDAO hostsDAO;
	DNSDao dnsDAO;
	DNSCmdValidator dnsCmdValidator;
	DNSHostsInterceptor dnsHostsInterceptor;

	public void expandCmd(UICommand cmdIn, BaseCompositeCommand result)
			throws CmdException {
		BulkDeleteDNSRecordsCmd command = (BulkDeleteDNSRecordsCmd) cmdIn;
		DNSCmd outCmd = new DNSCmd();

		for (DNSRecord rec : command.getToDelete()) {
			outCmd.insertDelete(rec);
		}
		Errors objectErrors = new BeanPropertyBindingResult(outCmd, "cmd");
		ValidationUtils.invokeValidator(dnsCmdValidator, outCmd, objectErrors);
		if (objectErrors.hasErrors())
			throw new CmdValidationException(objectErrors);
		result.appendCommand(outCmd);

	}



	public void setupCommand(UICommand cmd) {
		// TODO Auto-generated method stub

	}

	public int getOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Required
	public void setDnsCmdValidator(DNSCmdValidator dnsCmdValidator) {
		this.dnsCmdValidator = dnsCmdValidator;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setDnsHostsInterceptor(DNSHostsInterceptor dnsHostsInterceptor) {
		this.dnsHostsInterceptor = dnsHostsInterceptor;
	}

	@Required
	public void setDnsDAO(DNSDao dnsDAO) {
		this.dnsDAO = dnsDAO;
	}

}
