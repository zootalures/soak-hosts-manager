package edu.bath.soak.xml;

import org.springframework.util.Assert;

import com.sun.xml.bind.IDResolver;

public abstract class SoakXMLIDResolver extends IDResolver {
	Class supportedTypes[];

	public SoakXMLIDResolver(Class supportedTypes[]) {
		Assert.notNull(supportedTypes);
		this.supportedTypes = supportedTypes;

	}

	public boolean supportsResolving(Class clazz) {
		for (Class c : supportedTypes)
			if (c.isAssignableFrom(clazz))
				return true;

		return false;
	}

}
