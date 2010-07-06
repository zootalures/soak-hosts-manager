package edu.bath.soak;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import edu.bath.soak.cmd.CommandDispatcherRegistry;
import edu.bath.soak.cmd.CommandExpander;
import edu.bath.soak.cmd.CommandProcessor;
import edu.bath.soak.cmd.OrderedValidator;
import edu.bath.soak.mgr.AddressManagerAdvisor;
import edu.bath.soak.model.HostDataSource;
import edu.bath.soak.net.AdviceBasedAddressSpaceManager;
import edu.bath.soak.net.cmd.HookableValidator;
import edu.bath.soak.net.model.NetDAO;
import edu.bath.soak.propertyeditors.SoakPropertyEditorRegistrar;
import edu.bath.soak.query.ExtensibleSearchTarget;
import edu.bath.soak.query.SearchExpander;
import edu.bath.soak.xml.SoakXMLIDResolver;
import edu.bath.soak.xml.SoakXMLManager;

/**
 * 
 * Utility bean which automatically registers beans into various contact points
 * in the main System
 * 
 * 
 * Developers must set a plugin name and version
 * 
 * @author cspocc
 * 
 */
public class SoakCorePlugin extends SoakPlugin implements InitializingBean {
	Map<HookableValidator, OrderedValidator> subValidators = new HashMap<HookableValidator, OrderedValidator>();
	List<CommandExpander> commandExpanders = new ArrayList<CommandExpander>();
	List<CommandProcessor> commandProcessors = new ArrayList<CommandProcessor>();
	Map<SearchExpander, ExtensibleSearchTarget> searchExpanders = new HashMap<SearchExpander, ExtensibleSearchTarget>();

	Map<Class, PropertyEditor> customPropertyEditors = new HashMap<Class, PropertyEditor>();
	List<AddressManagerAdvisor> addressManagerAdvisors = new ArrayList<AddressManagerAdvisor>();
	List<SoakXMLIDResolver> xmlIdResolvers = new ArrayList<SoakXMLIDResolver>();
	List<String> xmlMappedPackages = new ArrayList<String>();
	CommandDispatcherRegistry commandDispatcherRegistry;
	AdviceBasedAddressSpaceManager addressSpaceManager;
	SoakXMLManager xmlManager;
	SoakPropertyEditorRegistrar propertyEditorRegistrar;
	NetDAO hostsDAO;
	DataSourceRegistry dataSourceRegistry;
	List<HostDataSource> hostDataSources = new ArrayList<HostDataSource>();

	public void afterPropertiesSet() throws Exception {
		log.info("Rigging up core plugin: " + pluginName + " : " + pluginVersion);

		for (Entry<HookableValidator, OrderedValidator> valentry : subValidators
				.entrySet()) {
			valentry.getKey().registerSubValidator(valentry.getValue());
		}

		for (CommandExpander commandExpander : commandExpanders) {
			commandDispatcherRegistry.registerExpander(commandExpander);
		}
		for (CommandProcessor commandProcessor : commandProcessors) {
			commandDispatcherRegistry.registerDispatcher(commandProcessor);
		}

		for (AddressManagerAdvisor advisor : addressManagerAdvisors)
			addressSpaceManager.registerAdvisor(advisor);

		for (SoakXMLIDResolver xmlIdResolver : xmlIdResolvers) {
			hostsDAO.registerXmlIDResolver(xmlIdResolver);

		}

		for (String xmlPackage : xmlMappedPackages) {
			xmlManager.registerSearchContext(xmlPackage);
		}
		for (Entry<Class, PropertyEditor> pe : customPropertyEditors.entrySet()) {
			propertyEditorRegistrar.registerCustomEditor(pe.getKey(), pe
					.getValue());
		}

		for (Entry<SearchExpander, ExtensibleSearchTarget> searchExtension : searchExpanders
				.entrySet()) {
			searchExtension.getValue().registerSearchExpander(
					searchExtension.getKey());

		}
		if (dataSourceRegistry != null)
			for (HostDataSource hds : hostDataSources) {
				dataSourceRegistry.registerDataSource(hds);
			}
		log.debug("Finished Rigging up plugin:" + pluginName + " : "
				+ pluginVersion);
	}

	public Map<HookableValidator, OrderedValidator> getSubValidators() {
		return subValidators;
	}

	/**
	 * A set of validator plugs, given a validatopr, the right hand side will be
	 * registered as a subvalidator of that validator
	 * 
	 * @param subValidators
	 */
	public void setSubValidators(
			Map<HookableValidator, OrderedValidator> subValidators) {
		this.subValidators = subValidators;
	}

	public List<CommandExpander> getCommandExpanders() {
		return commandExpanders;
	}

	/**
	 * A list of command expanders, on load, these will be injected into the
	 * {@link CommandDispatcherRegistry} to expand commands for this plugin
	 * 
	 * @param commandExpanders
	 */
	public void setCommandExpanders(List<CommandExpander> commandExpanders) {
		this.commandExpanders = commandExpanders;
	}

	public List<CommandProcessor> getCommandProcessors() {
		return commandProcessors;
	}

	/**
	 * A list of {@link CommandProcessor}, on load, these will be injected into
	 * the {@link CommandDispatcherRegistry} to implements the commands defined
	 * by this plugin A plugin should only register processors for commands
	 * which are defined by the plugin.
	 * 
	 * @param commandExpanders
	 */

	public void setCommandProcessors(List<CommandProcessor> commandProcessors) {
		this.commandProcessors = commandProcessors;
	}

	public Map<Class, PropertyEditor> getCustomPropertyEditors() {
		return customPropertyEditors;
	}

	/**
	 * A set of {@link PropertyEditor}/ {@link Class} pairs,
	 * 
	 * Each of the specified property editors will be registered for the
	 * specified class, allowing the relevant type to be bound and unbound by
	 * the user interface.
	 * 
	 * @param customPropertyEditors
	 */
	public void setCustomPropertyEditors(
			Map<Class, PropertyEditor> customPropertyEditors) {
		this.customPropertyEditors = customPropertyEditors;
	}

	public List<AddressManagerAdvisor> getAddressManagerAdvisors() {
		return addressManagerAdvisors;
	}

	/**
	 * A listof {@link AddressManagerAdvisor} objects to plug into the default
	 * {@link AdviceBasedAddressSpaceManager}.
	 * 
	 * Each can be used to affect how IP addresses are allcoated when the are
	 * being chosen.
	 * 
	 * @param addressManagerAdvisors
	 */
	public void setAddressManagerAdvisors(
			List<AddressManagerAdvisor> addressManagerAdvisors) {
		this.addressManagerAdvisors = addressManagerAdvisors;
	}

	/**
	 * A list of {@link SoakXMLIDResolver} objects which allow ID-referenced XML
	 * entitites to be resolved when objects are serialized to XML and back
	 * 
	 * @param xmlIdResolvers
	 */
	public void setXmlIdResolvers(List<SoakXMLIDResolver> xmlIdResolvers) {
		this.xmlIdResolvers = xmlIdResolvers;
	}

	/**
	 * A list of mapped packages which should be searched for JAXB XML metadata
	 * and ObjectFactory classes.
	 * 
	 * @param xmlMappedPackages
	 */
	public void setXmlMappedPackages(List<String> xmlMappedPackages) {
		this.xmlMappedPackages = xmlMappedPackages;
	}

	@Required
	public void setCommandDispatcherRegistry(
			CommandDispatcherRegistry commandDispatcherRegistry) {
		this.commandDispatcherRegistry = commandDispatcherRegistry;
	}

	@Required
	public void setAddressSpaceManager(
			AdviceBasedAddressSpaceManager addressSpaceManager) {
		this.addressSpaceManager = addressSpaceManager;
	}

	@Required
	public void setHostsDAO(NetDAO hostsDAO) {
		this.hostsDAO = hostsDAO;
	}

	@Required
	public void setPropertyEditorRegistrar(
			SoakPropertyEditorRegistrar propertyEditorRegistrar) {
		this.propertyEditorRegistrar = propertyEditorRegistrar;
	}

	@Required
	public void setXmlManager(SoakXMLManager xmlManager) {
		this.xmlManager = xmlManager;
	}

	public Map<SearchExpander, ExtensibleSearchTarget> getSearchExpanders() {
		return searchExpanders;
	}

	public void setSearchExpanders(
			Map<SearchExpander, ExtensibleSearchTarget> searchExpanders) {
		this.searchExpanders = searchExpanders;
	}

	public void setDataSourceRegistry(DataSourceRegistry dataSourceRegistry) {
		this.dataSourceRegistry = dataSourceRegistry;
	}

	public List<HostDataSource> getHostDataSources() {
		return hostDataSources;
	}

	public void setHostDataSources(List<HostDataSource> hostDataSources) {
		this.hostDataSources = hostDataSources;
	}
}
