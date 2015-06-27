package edu.bath.soak;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import edu.bath.soak.model.HostDataSource;
import edu.bath.soak.util.OrderedComparator;

/**
 * Holder for the list of host datasources
 * 
 * these are currently used by bulk create to optionally fill data from existing
 * sources for hosts
 * 
 * @see HostDataSource
 * @author cspocc
 * 
 */
public class DataSourceRegistry {

	Set<HostDataSource> dataSources = new TreeSet<HostDataSource>(
			new OrderedComparator());

	public Collection<HostDataSource> getDataSources() {
		return dataSources;
	}

	public HostDataSource getDataSource(String id) {
		for (HostDataSource ds : dataSources) {
			if (ds.getId().equals(id)) {
				return ds;
			}
		}
		return null;
	}

	public void setDataSources(Set<HostDataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public void registerDataSource(HostDataSource dataSource) {
		dataSources.add(dataSource);
	}
}
