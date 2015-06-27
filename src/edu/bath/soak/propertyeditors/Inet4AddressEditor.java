/**
 * 
 */
package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.net.Inet4Address;

import org.springframework.util.StringUtils;

import edu.bath.soak.util.TypeUtils;

public class Inet4AddressEditor extends PropertyEditorSupport {

	public Inet4AddressEditor(){
		
	}
	@Override
	public String getAsText() {
		Object value = getValue();
		return (value == null ? "" : ((Inet4Address) value).getHostAddress());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				setValue(TypeUtils.txtToIP(text));
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid IP address syntax");
			}
		}else{
			setValue(null);
		}

	}

}