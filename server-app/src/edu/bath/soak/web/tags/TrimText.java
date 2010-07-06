package edu.bath.soak.web.tags;

import javax.servlet.jsp.tagext.TagSupport;

/**
 * Very simple tag which trims any trailing characters beyond a certain length
 * off a string
 * 
 * @author cspocc
 * 
 */
public class TrimText extends TagSupport {

	String value;
	int maxLength;
	String trailer = "...";
	boolean stripNewLines = true;

	public TrimText() {

	}

	@Override
	public int doStartTag() {
		try {

			String value = this.value;
			if (stripNewLines) {
				value = value.replaceAll("\\n", " ");
			}
			if (value.length() > maxLength) {
				value = value.substring(0, maxLength) + trailer;
			}

			pageContext.getOut().print(value);

			return SKIP_BODY;
		} catch (Exception e) {
			throw new Error(e);
		}
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public boolean isStripNewLines() {
		return stripNewLines;
	}

	public void setStripNewLines(boolean stripNewLines) {
		this.stripNewLines = stripNewLines;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTrailer() {
		return trailer;
	}

	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

}
