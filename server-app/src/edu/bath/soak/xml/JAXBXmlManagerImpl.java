package edu.bath.soak.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.springframework.util.Assert;

import com.sun.xml.bind.IDResolver;

import edu.bath.soak.util.TypeUtils;

public class JAXBXmlManagerImpl implements SoakXMLManager {
	List<String> searchContexts = new ArrayList<String>();

	JAXBContext ctx;

	JAXBContext getContext() throws JAXBException {
		if (ctx != null)
			return ctx;

		Assert.notEmpty(searchContexts,
				"You must specify at least one JAXB context to search");
		ctx = JAXBContext.newInstance(TypeUtils
				.joinStrings(searchContexts, ":"));
		return ctx;
	}

	public void registerSearchContext(String str) {
		searchContexts.add(str);

	}

	public void marshall(Object o, OutputStream os) {
		try {
			JAXBContext ctx = getContext();

			Marshaller m = ctx.createMarshaller();
			m.marshal(o, os);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> T unmarshall(Class<T> clazz, IDResolver resolver, InputStream is) {
		// try {
		return unmarshallAll(clazz, resolver, Collections.singletonList(is))
				.get(0);
		// JAXBContext ctx = getContext();
		// Unmarshaller um = ctx.createUnmarshaller();
		// if (resolver != null)
		// um.setProperty(IDResolver.class.getName(), resolver);
		//
		// T val = (T) um.unmarshal(is);
		// return val;
		// } catch (JAXBException e) {
		// throw new RuntimeException(e);
		// }
	}

	public <T extends Object> List<T> unmarshallAll(Class<T> clazz,
			IDResolver resolver, List<InputStream> ises) {
		Assert.notEmpty(ises);
		try {
			JAXBContext ctx = getContext();

			Unmarshaller um = ctx.createUnmarshaller();

			if (resolver != null)
				um.setProperty(IDResolver.class.getName(), resolver);
			List<T> rvs = new ArrayList<T>();
			for (InputStream is : ises) {
				T val = (T) um.unmarshal(is);
				rvs.add(val);
			}
			return rvs;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getSearchContext() {
		return searchContexts;
	}

	public void setSearchContexts(List<String> searchContext) {
		this.searchContexts = searchContext;
	}

}
