package edu.bath.soak.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import au.com.bytecode.opencsv.CSVReader;
import edu.bath.soak.net.model.Host;
import edu.bath.soak.propertyeditors.SoakPropertyEditorRegistrar;

/**
 * CSV parser for converting CSV files into lists of bound beans
 * 
 * @author cspocc
 * 
 */
public class CSVParser {
	String[] fields;
	SoakPropertyEditorRegistrar propertyEditorRegistrar;
	Class beanClass;
	int minEntries = 0;

	public static interface ObjectMapper<Type> {
		public Type getObject(String csvLine[]);
	}

	public static class CSVParseException extends RuntimeException {
		int lineNo;

		public CSVParseException(int lineNo, String message) {
			super(message);
			this.lineNo = lineNo;
		}

		public CSVParseException(int lineNo, String message, Throwable t) {
			super(message, t);
			this.lineNo = lineNo;
		}

		public int getLineNo() {
			return lineNo;
		}

		public void setLineNo(int lineNo) {
			this.lineNo = lineNo;
		}
	};

	public interface ErrorListener {
		public void error(CSVParseException e);
	}

	/***************************************************************************
	 * Extract host data (see below) throws all exceptions as CSVParseExceptions
	 * 
	 * @param <Type>
	 *            the type of objects to create
	 * @param clazz
	 *            the class of object to extract data into
	 * @param is
	 * @param prototype
	 * @return
	 * @throws IOException
	 */
	public <Type extends Object> List<Type> extractBeanData(Class<Type> clazz,
			InputStream is, Object prototype) throws IOException {

		return extractBeanData(clazz, is, prototype, new ErrorListener() {
			public void error(CSVParseException e) {
				throw e;
			}
		});
	}

	boolean fillLineData(Object beanValue, String[] line, int lineNo,
			ErrorListener err, BeanWrapper defaults) {
		if ((minEntries > 0 && line.length < minEntries)
				|| (minEntries == 0 && line.length != fields.length)
				|| line.length > fields.length) {
			err.error(new CSVParseException(lineNo, "Expecting "
					+ (minEntries > 0 ? (" between " + minEntries + " and "
							+ fields.length + " ") : fields.length)
					+ " columns but got " + line.length));
			return false;
		}

		BeanWrapper wrap = new BeanWrapperImpl(beanValue);
		propertyEditorRegistrar.registerCustomEditors(wrap);
		for (int i = 0; i < fields.length; i++) {
			String field = fields[i];
			String valueStr = i < line.length?line[i]:null;
			Object value = null;
			if (!StringUtils.hasText(valueStr)) {
				// If the property isnt' set in the CSV, try using the
				// default value
				Object defaultValue;
				try {
					defaultValue = defaults.getPropertyValue(field);
					if (defaultValue != null) {
						value = defaultValue;
					}
				} catch (BeansException e) {
				}
			} else {
				value = valueStr;
			}
			try {
				wrap.setPropertyValue(field, value);
			} catch (Exception e) {
				err.error(new CSVParseException(lineNo, "Can't set field "
						+ field + " to value " + value + ":" + e.getMessage(),
						e));
				return false;
			}
		}
		return true;
	}

	/***************************************************************************
	 * Extract a list of host names from a CSV buffer,
	 * 
	 * @param <Type>
	 *            the type of objects created
	 * 
	 * @param data
	 *            the buffer to extract
	 * 
	 * @param prototype
	 *            a set of defaults to apply to hosts when given properties are
	 *            not set (i.e. empty) in the CSV,
	 * @param type
	 *            the type of objects to create from the CSV files. this type
	 *            should be assignable from the type set in
	 *            {@link #setBeanClass(Class)}
	 * @param is
	 *            the input stream to read data from
	 * 
	 * @param err
	 *            an error listener semantic erros in the input will be returned
	 *            via this interface
	 * 
	 * @return a list of parsed hosts
	 * @throws IOException
	 */
	public <Type extends Object> List<Type> extractBeanData(Class<Type> type,
			InputStream is, Object prototype, ErrorListener err)
			throws IOException {
		Assert.notNull(fields);
		Assert.notNull(beanClass);
		Assert.isAssignable(beanClass, type,
				"Requested type is not assignable from the parser type");
		LinkedList<Type> beans = new LinkedList<Type>();
		CSVReader csvReader = new CSVReader(new InputStreamReader(is));
		String[] line = null;
		int lineNo = 1;

		BeanWrapper defaults = new BeanWrapperImpl(prototype);

		while (null != (line = csvReader.readNext())) {

			Type beanValue;
			try {
				beanValue = type.newInstance();
			} catch (Exception e) {
				throw new RuntimeException();

			}

			if (!fillLineData(beanValue, line, lineNo, err, defaults)) {
				lineNo++;
				continue;
			}
			beans.add(beanValue);
			lineNo++;
		}
		return beans;
	}

	
	public <Type extends Object> List<Type> extractBeanDataUsingMapper(
			Class<Type> type, ObjectMapper<Type> mapper, InputStream is) throws IOException {
		
		return extractBeanDataUsingMapper(type ,mapper, is,new ErrorListener() {
			public void error(CSVParseException e) {
				throw e;
			}
		}); 
	}
	/**
	 * Extracts bean data from A CSV file into exising objects, 
	 * 
	 * 
	 * @param <Type>
	 * @param type
	 * @param objects
	 * @param is
	 * @param prototype
	 * @param err
	 * @throws IOException
	 */
	public <Type extends Object> List<Type> extractBeanDataUsingMapper(
			Class<Type> type, ObjectMapper<Type> mapper, InputStream is,
			ErrorListener err) throws IOException {
		Assert.notNull(fields);
		Assert.notNull(beanClass);
		Assert.isAssignable(beanClass, type,
				"Requested type is not assignable from the parser type");
		CSVReader csvReader = new CSVReader(new InputStreamReader(is));
		String[] line = null;
		int lineNo = 1;
		List<Type > objects = new ArrayList<Type>();

		while (null != (line = csvReader.readNext())) {
			Type object = mapper.getObject(line);
			if (object == null) {
				err.error(new CSVParseException(lineNo,
						"Unable to find object matching line "));
			} else {
				fillLineData(object, line, lineNo, err, new BeanWrapperImpl(
						object));
			}
			objects.add(object);
			lineNo++;
		}
		return objects;
	}

	@Required
	public void setPropertyEditorRegistrar(
			SoakPropertyEditorRegistrar propertyEditorRegistrar) {
		this.propertyEditorRegistrar = propertyEditorRegistrar;
	}

	/**
	 * The class of beans supported by this parser,
	 * 
	 * The parser will check that the requested type for conversion is
	 * asssignable to this type when the objects are parsed.
	 * 
	 * @param beanClass
	 */
	@Required
	public void setBeanClass(Class beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * The fields used to map data for this CSV parser Fields should be bean
	 * property names relative to the base value of the represented bean (where
	 * the bean is called "bean"
	 * 
	 * for example to map
	 * 
	 * <pre>
	 * &quot;adpc-foo1&quot;,138.38.32.1,001122334455
	 * &quot;adpc-foo2&quot;,138.38.32.2,001122334456
	 * &quot;adpc-foo3&quot;,138.38.32.3,001122334457
	 * </pre>
	 * 
	 * To a {@link Host} object you should use.
	 * 
	 * ["hostName","ipAddress","macAddress"]
	 * 
	 * @param fields
	 */
	@Required
	public void setFields(String[] fields) {
		this.fields = fields;
	}

	/**
	 * The minumum number of CSV entries which is acceptable for a
	 * 
	 * @return
	 */
	public int getMinEntries() {
		return minEntries;
	}

	public void setMinEntries(int minEntries) {
		this.minEntries = minEntries;
	}

}
