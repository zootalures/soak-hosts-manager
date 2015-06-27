package edu.bath.soak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/*******************************************************************************
 * Very simple bean which pull all plugins out of the application context
 * 
 * @author cspocc
 * 
 */
public class PluginManager implements ApplicationContextAware {
	ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * Returns a list of {@link SoakPlugin} objects which are registered in the
	 * current application context.
	 * 
	 * @return a list of configued plugins
	 */
	public Collection<SoakPlugin> getPlugins() {
		ArrayList<SoakPlugin> plugins = new ArrayList<SoakPlugin>();
		plugins.addAll((Collection<SoakPlugin>) applicationContext
				.getBeansOfType(SoakPlugin.class).values());
		Collections.sort(plugins, new Comparator<SoakPlugin>() {
			public int compare(SoakPlugin o1, SoakPlugin o2) {
				return o1.getPluginName().compareTo(o2.getPluginName());
			}
		});
		return plugins;
	}


}
