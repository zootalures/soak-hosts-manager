package edu.bath.soak.web.tags;

import org.springframework.beans.factory.annotation.Required;

/**
 * Holder class for tag configuration. This bean must be named "soakTagConfig"
 * and must be presented to the request context.
 * 
 * @author cspocc
 * 
 */
public class SoakTagConfig {
	String helpBase;
	String defaultHostNameSuffix;

	public String getHelpBase() {
		return helpBase;
	}

	@Required
	public void setHelpBase(String helpBase) {
		this.helpBase = helpBase;
	}

	public String getDefaultHostNameSuffix() {
		return defaultHostNameSuffix;
	}

	@Required
	public void setDefaultHostNameSuffix(String defaultHostSuffix) {
		this.defaultHostNameSuffix = defaultHostSuffix;
	}
}
