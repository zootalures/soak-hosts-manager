package edu.bath.soak.xml;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.springframework.util.Assert;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

import edu.bath.soak.net.model.UnresolvedEntityException;

public class MultipleEntityIDResolver extends IDResolver {
	Collection<SoakXMLIDResolver> resolvers;

	public MultipleEntityIDResolver(Collection<SoakXMLIDResolver> resolvers) {
		Assert.notNull(resolvers);
		this.resolvers = resolvers;
	}

	@Override
	public void bind(String arg0, Object arg1) throws SAXException {

	}

	@Override
	public Callable resolve(String arg0, Class arg1) throws SAXException {
		for (SoakXMLIDResolver resolver : resolvers) {
			if (resolver.supportsResolving(arg1)) {
				return resolver.resolve(arg0, arg1);
			}
		}
		throw new UnresolvedEntityException("Unable to find entity of class "
				+ arg1  + " with ID  " + arg0 );
	}

}
