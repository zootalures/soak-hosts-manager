package edu.bath.soak.dns.cmd;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.dns.model.DNSDao;
import edu.bath.soak.dns.model.DNSRecord;

public class DNSCmdValidator implements Validator {
	DNSDao dnsDao;

	public boolean supports(Class clazz) {
		return DNSCmd.class.isAssignableFrom(clazz);
	}

	public void validate(Object target, Errors errors) {
		DNSCmd command = (DNSCmd) target;
		Collection<DNSRecord> deletions = command.getDeletions();

		for (DNSChange change : command.getChanges()) {
			DNSRecord chrecord = change.getRecord();
			if (change.isAddition()) {

				if (null == chrecord.getZone()) {
					errors.reject("invalid-input", "You must specify a zone");
				} else {
					if (!chrecord.getHostName().endsWith(
							chrecord.getZone().getDomain())) {
						errors.reject("invalid-input", "the record " + chrecord
								+ " is not suffixed by the  requested zone   "
								+ chrecord.getZone());

					}

				}

				Collection<DNSRecord> clashes = new ArrayList<DNSRecord>();
				DNSRecord existing = dnsDao.findRecord(chrecord.getZone(),
						chrecord.getHostName(), chrecord.getType(), chrecord
								.getTarget());
				if (existing != null) {
					clashes.add(existing);
				}
				if (chrecord.getType().equals("PTR")) {
					clashes.addAll(dnsDao.findRecords(chrecord.getZone(),
							chrecord.getHostName(), "PTR"));
				} else if (chrecord.getType().equals("A")) {
					clashes.addAll(dnsDao.findRecords(chrecord.getZone(),
							chrecord.getHostName(), "CNAME"));

				} else if (chrecord.getType().equals("CNAME")) {
					clashes.addAll(dnsDao.findRecords(chrecord.getZone(),
							chrecord.getHostName(), "A"));

					dnsDao.findRecords(chrecord.getZone(), chrecord
							.getHostName(), "CNAME");
				}

				for (DNSRecord other : clashes) {
					if (!deletions.contains(other)) {
						errors.reject("semantic-error",
								"change clashes with record " + other);
					}
				}
			} else {
				DNSRecord existing = dnsDao.findRecord(chrecord.getZone(),
						chrecord.getHostName(), chrecord.getType(), chrecord
								.getTarget());
				if (existing == null) {
					errors.reject("semantic-error",
							"can't delete non-existant record" + chrecord);
				}
			}
		}

	}

	@Required
	public void setDnsDao(DNSDao dnsDao) {
		this.dnsDao = dnsDao;
	}

}
