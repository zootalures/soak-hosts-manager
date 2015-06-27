package edu.bath.soak;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

/**
 * Base class for plugin objects
 * 
 * @author cspocc
 * 
 */
public class SoakPlugin {

	protected String pluginName;
	protected String pluginVersion;
	String pluginUrl;
	String pluginDescription;
	String pluginConfigUrl;
	protected Logger log = Logger.getLogger(this.getClass());

	public SoakPlugin() {
		super();
	}

	/**
	 * @see #getPluginName()
	 * @return
	 */
	public String getPluginName() {
		return pluginName;
	}

	/**
	 * The name of the plugin
	 * 
	 */
	@Required
	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	/**
	 * 
	 * @see #getPluginVersion()
	 */
	public String getPluginVersion() {
		return pluginVersion;
	}

	/**
	 * 
	 * Sets the plugin version
	 * 
	 * @param pluginVersion
	 */
	@Required
	public void setPluginVersion(String pluginVersion) {
		this.pluginVersion = pluginVersion;
	}

	/**
	 * @see #setPluginUrl(String)
	 * @return
	 */
	public String getPluginUrl() {
		return pluginUrl;
	}

	/**
	 * Sets a URL associated with the plugin
	 * 
	 * @param pluginUrl
	 */
	public void setPluginUrl(String pluginUrl) {
		this.pluginUrl = pluginUrl;
	}

	/***************************************************************************
	 * A local (i.e. relative to the webapp root) URL for the configuration of
	 * this plugin
	 * 
	 * @return
	 */
	public String getPluginConfigUrl() {
		return pluginConfigUrl;
	}

	public void setPluginConfigUrl(String pluginConfig) {
		this.pluginConfigUrl = pluginConfig;
	}

	public String getPluginDescription() {
		return pluginDescription;
	}

	public void setPluginDescription(String pluginDescription) {
		this.pluginDescription = pluginDescription;
	}

}