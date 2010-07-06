package edu.bath.soak.web.bulk;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import edu.bath.soak.net.CSVParser;
import edu.bath.soak.net.model.Host;

public class BulkCreateFromCsvCmdValidator implements Validator {

	CSVParser hostsCSVParser;

	public boolean supports(Class clazz) {
		return clazz.isAssignableFrom(BulkCreateFromCsvCmd.class);
	}

	public void validate(Object target, final Errors errors) {
		BulkCreateFromCsvCmd command = (BulkCreateFromCsvCmd) target;

		if (null == command.getUploadData()) {
			errors.rejectValue("", "invalid-input",
					"You must enter some CSV data to import");
			return;
		}
		try { 
			List<Host> gotHosts = hostsCSVParser.extractBeanData(Host.class,new ByteArrayInputStream(command
					.getUploadData()), command.getDefaultHostData(),
					new CSVParser.ErrorListener() {
						public void error(CSVParser.CSVParseException e) {
							errors.rejectValue("", "invalid-input",
									"Error on line " + e.getLineNo() + " : "
											+ e.getMessage());
						}
					});
			
			ArrayList<Host> permittedHosts = new ArrayList<Host>();
			for (Host h : gotHosts) {
				if (h.getHostName() != null
						&& StringUtils.hasText(h.getHostName().getName())) {
					permittedHosts.add(h);
				}
			}
			if(permittedHosts.isEmpty()){
				errors.rejectValue("","invalid-input","No hosts were specified in CSV data");
			}
		} catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Required
	public void setHostsCSVParser(CSVParser cSVParser) {
		this.hostsCSVParser = cSVParser;
	}
}
