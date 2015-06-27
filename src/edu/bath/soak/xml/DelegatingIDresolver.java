package edu.bath.soak.xml;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.sun.xml.bind.IDResolver;

public class DelegatingIDresolver extends IDResolver {
	IDResolver delegate;
	Logger log = Logger.getLogger(DelegatingIDresolver.class);
	Map<String, Object> boundObjects = new HashMap<String, Object>();

	public DelegatingIDresolver(IDResolver delegate) {
		this.delegate = delegate;
	}

	public void setDelegate(IDResolver delegate) {
		this.delegate = delegate;
	}

	public String toCanonicalId(String id, Class clazz) {
		return "#" + id;
	}

	@Override
	public void bind(String id, Object val) throws SAXException {

		String cid = toCanonicalId(id, val.getClass());
	//	log.trace("Binding " + cid + " to " + val);
		boundObjects.put(cid, val);
	}

	@Override
	public Callable<Object> resolve(final String id, final Class clazz)
			throws SAXException {
		final String cid = toCanonicalId(id, clazz);

		return new Callable<Object>() {
			public Object call() throws Exception {

				Object o = boundObjects.get(cid);
	//			log.trace("resolving " + cid);

				if (o == null) {

					o = delegate.resolve(id, clazz).call();
		//			log.trace(cid + " not found locally, delegate was " + o);
					if(o==null)
						log.warn("Unable to find object of type " + clazz.getName() + " with ID " + id);

				} else {
//					log.trace(cid + "got local " + o);

				}
				return o;
			}
		};
	}
}
