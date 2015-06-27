package edu.bath.soak.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.sun.xml.bind.IDResolver;

/**
 * Handles the interface between XML and DAO objects
 * 
 * @author cspocc
 * 
 */
public interface SoakXMLManager {
	public void registerSearchContext(String str);

	public <T> T unmarshall(Class<T> clazz, IDResolver resolver, InputStream is);

	public <T extends Object> List<T> unmarshallAll(Class<T> clazz,
			IDResolver resolver, List<InputStream> ises);

	public void marshall(Object o, OutputStream os);
}
