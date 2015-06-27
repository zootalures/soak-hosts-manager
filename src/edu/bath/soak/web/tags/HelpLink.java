package edu.bath.soak.web.tags;

import javax.servlet.jsp.JspWriter;

import org.springframework.util.Assert;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * Tag for generating a link to related documentation
 * 
 * Pulls a URL base out of the contextual {@link SoakTagConfig} object and
 * creates an HTML link with a given CSS class by appending the given identifier
 * 
 * @author cspocc
 * 
 */
public class HelpLink extends RequestContextAwareTag {
	String path;
	String title;
	String base;
	String cssClass = "helpLink";
	boolean newPage = true;
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	protected int doStartTagInternal() throws Exception {
		SoakTagConfig config = (SoakTagConfig) getRequestContext()
				.getWebApplicationContext().getBean("soakTagConfig");
		Assert.notNull(config);
		JspWriter out = pageContext.getOut();
		String theBase = base;
		if (theBase == null) {
			theBase = config.getHelpBase();
		}
		String newPageString = "";
		if(newPage){
			newPageString=" target='soakHelp' ";
		}
		out
				.append("<a " +newPageString + " class='" + cssClass + "' href='" + theBase + path
						+ "' ");
		if (null != title) {
			out.append("title='" + title + "' ");
		}
		out.append(" > <span> " + title + "</span></a>");
		return SKIP_BODY;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getPath() {
		return path;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public boolean isNewPage() {
		return newPage;
	}

	public void setNewPage(boolean newPage) {
		this.newPage = newPage;
	}
}
