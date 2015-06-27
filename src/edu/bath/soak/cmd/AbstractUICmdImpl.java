package edu.bath.soak.cmd;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.dns.cmd.BulkDeleteDNSRecordsCmd;
import edu.bath.soak.dns.cmd.CleanUpUnusedDNSRecordsCmd;

/*******************************************************************************
 * Default implementation of {@link UICommand}
 * 
 * @author cspocc
 * 
 */
@XmlSeeAlso( { BulkDeleteDNSRecordsCmd.class, CleanUpUnusedDNSRecordsCmd.class })
public abstract class AbstractUICmdImpl implements UICommand, Serializable {
	Map<Object, Object> optionData = new HashMap<Object, Object>();
	List<Object> commandOptions = new ArrayList<Object>();
	String changeComments;

	@XmlTransient
	public Map<Object, RenderableCommandOption> getRenderableOptions() {
		HashMap<Object, RenderableCommandOption> option = new HashMap<Object, RenderableCommandOption>();
		for (Entry<Object, Object> o : getOptionData().entrySet()) {
			if (o.getValue() instanceof RenderableCommandOption) {
				option.put(o.getKey(), (RenderableCommandOption) o.getValue());
			}
		}
		return new TreeMap<Object, RenderableCommandOption>(option);
	}

	public boolean isHasRenderableOptions() {
		return getRenderableOptions().size() > 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AbstractUICmdImpl other = (AbstractUICmdImpl) obj;
		if (changeComments == null) {
			if (other.changeComments != null)
				return false;
		} else if (!changeComments.equals(other.changeComments))
			return false;
		if (optionData == null) {
			if (other.optionData != null)
				return false;
		} else if (!optionData.equals(other.optionData))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((changeComments == null) ? 0 : changeComments.hashCode());
		result = prime * result
				+ ((optionData == null) ? 0 : optionData.hashCode());
		return result;
	}

	public Map<Object, Object> getOptionData() {
		return optionData;
	}

	public void setOptionData(Map<Object, Object> commandFlags) {
		this.optionData = commandFlags;
	}

	public String getChangeComments() {
		return changeComments;
	}

	public void setChangeComments(String changeComments) {
		this.changeComments = changeComments;
	}

}
