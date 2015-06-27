package edu.bath.soak.web.tags;

import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * Tag for trimming a given host name suffix from host names, this is used to
 * print more conveniently named host names.
 * 
 * The suffix can either be specified in the "suffix" attribute of the tag,
 * otherwise it will be pulled out of the Config object in the request context.
 * 
 * @author cspocc
 * 
 */
public class TrimNameSuffix extends RequestContextAwareTag {

	String value;
	String suffix = null;

	public TrimNameSuffix() {

	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setValue(String value) {

		this.value = value;
	}

	String getSuffix() {
		if (suffix != null)
			return suffix;
		SoakTagConfig config = (SoakTagConfig) getRequestContext()
				.getWebApplicationContext().getBean("soakTagConfig");
		Assert.notNull(config);
		return config.getDefaultHostNameSuffix();
	}

	@Override
	public int doStartTagInternal() {
		String trimSuffix = getSuffix();
		try {
			if (value.endsWith(trimSuffix)) {

				pageContext.getOut().print(
						value.substring(0, (value.length() - trimSuffix
								.length())));
			} else {
				pageContext.getOut().print(value);

			}
			return SKIP_BODY;
		} catch (Exception e) {
			throw new Error(e);
		}
	}

}
