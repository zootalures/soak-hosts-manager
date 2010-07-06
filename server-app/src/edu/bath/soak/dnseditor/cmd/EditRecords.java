package edu.bath.soak.dnseditor.cmd;

import java.io.Serializable;

import org.springframework.util.Assert;

import edu.bath.soak.dns.cmd.DNSCmd;
import edu.bath.soak.dns.model.DNSRecord;

public class EditRecords extends UIDNSChange implements Serializable{

	
	DNSRecord oldRecord;
	DNSRecord newRecord;

	@Override
	public void fillCmd(DNSCmd cmd) {
		Assert.notNull(oldRecord);
		Assert.notNull(newRecord);
		cmd.insertDelete(oldRecord);
		cmd.insertAdd(newRecord);

	}

	@Override
	public boolean matchesRecord(DNSRecord rec) {
		// TODO Auto-generated method stub
		return rec.equalsIncludingTtl(oldRecord);
	}

	public DNSRecord getOldRecord() {
		return oldRecord;
	}

	public void setOldRecord(DNSRecord oldRecord) {
		this.oldRecord = oldRecord;
	}

	public DNSRecord getNewRecord() {
		return newRecord;
	}

	public void setNewRecord(DNSRecord newRecord) {
		this.newRecord = newRecord;
	}

}

