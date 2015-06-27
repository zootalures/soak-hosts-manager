package edu.bath.soak.dnseditor.cmd;

import java.io.Serializable;

import org.springframework.util.Assert;

import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSRecord;
import edu.bath.soak.dns.model.DNSZone;

public class CreateRange extends UIDNSChange implements Serializable{
	int startNum;
	int endNum;
	DNSZone zone;

	String template;

	@Override
	public void fillCmd(DNSCmd cmd) {
		// TODO Auto-generated method stub
		Assert.isTrue(startNum <= endNum);

		for (int i = startNum; i < endNum; i++) {
			String record;
			record = template.replaceAll("\\$", new Integer(i).toString());
			
			DNSRecord newRecord;
			
		}
	}

	@Override
	public boolean matchesRecord(DNSRecord rec) {
		// TODO Auto-generated method stub
		return false;
	}
}
