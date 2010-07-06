package edu.bath.soak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;
import org.springframework.util.StringUtils;

/**
 * This behaves a bit like springs {@link AnnotationSessionFactoryBean} however
 * it searches for text files containing lists of annotated classes in a list of
 * plugin locations.
 * 
 * This is a bit of a hack to overcome a limitation of the
 * AnnotationsSessionFactoryBean which creates the SessionFactory at the
 * earliest possible moment, preventing plugins from registering additional
 * annotated classes.
 * 
 * @author cspocc
 * 
 */
public class SoakAnnotationSessionFactoryBean extends
		AnnotationSessionFactoryBean {

	String[] plugins;

	@Override
	protected void postProcessAnnotationConfiguration(
			AnnotationConfiguration config) throws HibernateException {
		List<Class> classes = new LinkedList<Class>();
		logger.debug("Starting session factory initialization");
		for (String plugin : plugins) {

			plugin = plugin.trim();
			String annotated = plugin.replaceAll("\\.", "/") + "/"
					+ "annotated-classes.soak";
			logger.debug("looking for annotated classes in classpath:"
					+ annotated);
			int lineNo = 0;

			try {
				ClassPathResource cpr = new ClassPathResource(annotated);

				InputStream is = cpr.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while (StringUtils.hasText((line = br.readLine()))) {
					lineNo++;
					line = line.trim();
					logger.debug("Trying to load  class " + line + " in "
							+ plugin);
					Class clazz = Class.forName(line);
					classes.add(clazz);
					logger.debug("Added class " + line + " in " + plugin);

				}
			} catch (IOException e) {
				logger.debug("resource " + annotated
						+ " was not found, skipping");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Could not resolve class in plugin "
						+ plugin + ":lineNo");
			}
		}

		for (Class clazz : classes) {
			config.addAnnotatedClass(clazz);
		}
	}

	public void setPluginsString(String pluginlist) {
		this.plugins = pluginlist.split("\\s");
	}

	public void setPlugins(String plugins[]) {
		this.plugins = plugins;
	}

}
