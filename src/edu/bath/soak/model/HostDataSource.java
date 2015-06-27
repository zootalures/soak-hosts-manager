package edu.bath.soak.model;

import org.springframework.core.Ordered;

import edu.bath.soak.net.model.Host;

/**
 * A simple interface for beans which can be used to fill host data from
 * specific sources
 * 
 * @author cspocc
 * 
 */
public interface HostDataSource extends Ordered {
	
	String getId();
	String getSourceDescription();

	public void fillInfoForHost(Host h, boolean overwrite);
}
