package edu.bath.soak.web.tags;

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
public class DisplayChange extends RequestContextAwareTag {

	Object before;
	Object after;

	public DisplayChange() {

	}

	@Override
	public int doStartTagInternal() {
		try {

			if (before != null && after != null && before.equals(after)
					|| before == null && after == null) {
				pageContext.getOut().print("<span class='changeBox nochange'>");
				if (before != null) {
					pageContext.getOut().print(before.toString());
				}
				pageContext.getOut().print("</span>");

			} else {
				pageContext.getOut().print("<span class='changeBox changed'>");
				if (after != null) {
					pageContext.getOut().print(
							"<span class='changeAfter'>" + after.toString()
									+ "</span>");
				}

				if (before != null) {
					pageContext.getOut().print(
							"<span class='changeBefore'>" + before.toString()
									+ "</span>");
				}
				pageContext.getOut().print("</span>");

			}

			return SKIP_BODY;
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public void setBefore(Object before) {
		this.before = before;
	}

	public void setAfter(Object after) {
		this.after = after;
	}

}
