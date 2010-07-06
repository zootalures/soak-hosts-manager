package edu.bath.soak.web.bulk;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.webflow.action.MultiAction;
import org.springframework.webflow.core.collection.MutableAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import edu.bath.soak.DataSourceRegistry;
import edu.bath.soak.net.CSVParser;
import edu.bath.soak.net.bulk.BulkCreateEditHostsManagerImpl;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.net.model.HostName;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.propertyeditors.HostNameEditor;
import edu.bath.soak.propertyeditors.SoakPropertyEditorRegistrar;
import edu.bath.soak.security.SecurityHelper;
import edu.bath.soak.web.bulk.BulkCreateChooseTypeCmd.BulkCreateType;

/*******************************************************************************
 * Controller for bulk actions on hosts
 * 
 * 
 * @author cspocc
 * 
 ******************************************************************************/
public class BulkCreateHostsFlowController extends MultiAction {
	NetDAO hostsDAO;
	String commandAttribute = "command";
	BulkCreateEditHostsManagerImpl bulkCreateHostsManager;
	CSVParser hostsCSVParser;
	SecurityHelper securityHelper;
	SoakPropertyEditorRegistrar propertyEditorRegistrar;
	DataSourceRegistry dataSourceRegistry;

	public BulkCreateHostsFlowController() {
	}

	HostNameEditor hostNameEditor;

	public Event setExistingHostDetailsFromCSVFile(RequestContext context)
			throws Exception {
		BulkCreateFromCsvCmd csvCmd = (BulkCreateFromCsvCmd) context
				.getFlowScope().get("csvCmd");
		Assert.notNull(csvCmd);

		final HashSet<Host> gotHosts = new HashSet();
		List<Host> hosts = hostsCSVParser.extractBeanDataUsingMapper(
				Host.class, new CSVParser.ObjectMapper<Host>() {
					public Host getObject(String[] csvLine) {
						String name = csvLine[0];
						if (!StringUtils.hasText(name))
							throw new RuntimeException("host has no name");
						HostName hostName;
						hostNameEditor.setAsText(name);
						hostName = (HostName) hostNameEditor.getValue();
						if (hostName == null) {
							throw new IllegalArgumentException(
									"Could not parse name " + hostName);
						}
						Host h = hostsDAO.findHost(hostName);
						if (null == h) {
							throw new IllegalArgumentException(
									"No host found with name " + hostName);
						}
						h = hostsDAO.getHostForEditing(h.getId());
						if (gotHosts.contains(h)) {
							throw new IllegalArgumentException(
									"Duplicate host found " + hostName);
						}
						gotHosts.add(h);
						return h;
					}
				}, new ByteArrayInputStream(csvCmd.getUploadData()));

		context.getFlowScope().put("selectedHosts", hosts);
		return success();
	}

	public Event fillHostDetailsFromCSVFile(RequestContext context)
			throws Exception {
		BulkCreateFromCsvCmd csvCmd = (BulkCreateFromCsvCmd) context
				.getFlowScope().get("csvCmd");
		Assert.notNull(csvCmd);

		List<Host> gotHosts = hostsCSVParser.extractBeanData(Host.class,
				new ByteArrayInputStream(csvCmd.getUploadData()), csvCmd
						.getDefaultHostData());
		ArrayList<Host> permittedHosts = new ArrayList<Host>();
		for (Host h : gotHosts) {
			if (h.getHostName() != null
					&& StringUtils.hasText(h.getHostName().getName())) {
				permittedHosts.add(h);
			}
		}
		context.getFlowScope().put("selectedHosts", permittedHosts);
		return success();
	}

	public Event setUpHostDefaultsCommand(RequestContext context) {
		MutableAttributeMap model = context.getRequestScope();
		model.put("hostClasses", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses()));
		if (securityHelper.isAdmin()) {
			model.put("orgUnits", hostsDAO.getOrgUnits());
		} else {
			model.put("orgUnits", securityHelper
					.getAllowedOrgUnitsForCurrentUser());
		}

		return success();
	}

	public Event setUpCreateHostsCommand(RequestContext context) {
		MutableAttributeMap model = context.getRequestScope();
		model.put("hostClasses", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getHostClasses()));
		model.put("nameDomains", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getNameDomains()));
		model.put("dataSources", dataSourceRegistry.getDataSources());

		if (securityHelper.isAdmin()) {
			model.put("orgUnits", hostsDAO.getOrgUnits());
		} else {
			model.put("orgUnits", securityHelper
					.getAllowedOrgUnitsForCurrentUser());
		}
		model.put("subnets", securityHelper
				.filterAllowedEntitiesForUser(hostsDAO.getSubnets()));

		return success();
	}

	public Event getType(RequestContext context) {
		BulkCreateChooseTypeCmd cmd = (BulkCreateChooseTypeCmd) context
				.getFlowScope().get("chooseType");
		if (cmd.getType().equals(BulkCreateType.CSV_UPLOAD)) {
			return result("upload");
		} else {
			return result("range");
		}
	}

	public Event fillHostDetailsFromRange(RequestContext context) {
		BulkCreateFromRangeCmd rangeCmd = (BulkCreateFromRangeCmd) context
				.getFlowScope().get("rangeCmd");
		Assert.notNull(rangeCmd);
		Assert.isTrue(rangeCmd.getRange1().isSet());

		LinkedList<Host> hosts = new LinkedList<Host>();

		int val1 = 0;
		int val2 = 0;
		for (val1 = rangeCmd.getRange1().getMin(); val1 <= rangeCmd.getRange1()
				.getMax(); val1++) {

			for (val2 = (rangeCmd.getRange2().isSet() ? rangeCmd.getRange2()
					.getMin() : 0); val2 <= (rangeCmd.getRange2().isSet() ? rangeCmd
					.getRange2().getMax()
					: 0); val2++) {
				Host host = new Host();
				BeanWrapper bw = new BeanWrapperImpl(host);
				propertyEditorRegistrar.registerCustomEditors(bw);
				rangeCmd.getDefaultHostData().applyDefaults(host);

				bw.setPropertyValue("hostName", rangeCmd.formatHostName(val1,
						val2));
				if (StringUtils.hasText(rangeCmd.getIpAddressTemplate())) {
					String ipVal = rangeCmd.formatIp(val1, val2);
					bw.setPropertyValue("ipAddress", ipVal);
				}

				hosts.add(host);
			}
		}
		context.getFlowScope().put("selectedHosts", hosts);
		return success();
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	public String getCommandAttribute() {
		return commandAttribute;
	}

	public void setCommandAttribute(String commandAttribute) {
		this.commandAttribute = commandAttribute;
	}

	@Required
	public void setBulkCreateHostsManager(
			BulkCreateEditHostsManagerImpl bulkCreateHostsManger) {
		this.bulkCreateHostsManager = bulkCreateHostsManger;
	}

	@Required
	public void setHostsCSVParser(CSVParser cSVParser) {
		this.hostsCSVParser = cSVParser;
	}

	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	@Required
	public void setPropertyEditorRegistrar(
			SoakPropertyEditorRegistrar propertyEditorRegistrar) {
		this.propertyEditorRegistrar = propertyEditorRegistrar;
	}

	@Required
	public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
		this.dataSourceRegistry = dataSourceRegistry;
	}

	@Required
	public void setHostNameEditor(HostNameEditor hostNameEditor) {
		this.hostNameEditor = hostNameEditor;
	}

}
