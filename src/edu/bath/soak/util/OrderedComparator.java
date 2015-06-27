package edu.bath.soak.util;

import java.util.Comparator;

import org.springframework.core.Ordered;

public class OrderedComparator implements Comparator<Ordered> {

	public int compare(Ordered arg0, Ordered  arg1) {
		if(arg0.getOrder() == arg1.getOrder() && arg0.hashCode()!=arg1.hashCode())
			return ((Integer)arg0.hashCode()).compareTo(arg1.hashCode());
		return(((Integer)arg0.getOrder()).compareTo(arg1.getOrder()));
	}

}
