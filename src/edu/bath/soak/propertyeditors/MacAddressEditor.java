/**
 * 
 */
package edu.bath.soak.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

import edu.bath.soak.util.MacAddress;

public class MacAddressEditor extends PropertyEditorSupport {

	public MacAddressEditor(){
		
	}
	@Override
	public String getAsText() {
		Object value = getValue();
		return (value == null ? "" : ((MacAddress)value).toString());
	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.hasText(text)) {
			try {
				setValue(MacAddress.fromText(text));
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid MAC address syntax");
			}
		}else{
			setValue(null);
		}

	}

}