/**
 * 
 */
package edu.bath.soak.web.host;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.Ordered;

public class HostViewTab implements Ordered {
	String tabTitle;
	String tabName;
	List<Object> renderBeans = new ArrayList<Object>();
	int order = 0;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTabTitle() {
		return tabTitle;
	}

	public void setTabTitle(String tabTitle) {
		this.tabTitle = tabTitle;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public List<Object> getRenderBeans() {
		return renderBeans;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tabName == null) ? 0 : tabName.hashCode());
		result = prime * result
				+ ((tabTitle == null) ? 0 : tabTitle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HostViewTab other = (HostViewTab) obj;
		if (tabName == null) {
			if (other.tabName != null)
				return false;
		} else if (!tabName.equals(other.tabName))
			return false;
		if (tabTitle == null) {
			if (other.tabTitle != null)
				return false;
		} else if (!tabTitle.equals(other.tabTitle))
			return false;
		return true;
	}

	public void setRenderBeans(List<Object> renderBeans) {
		this.renderBeans = renderBeans;
	}
	
	public void addViewComponent(Object component){
		renderBeans.add(component);
	}
}