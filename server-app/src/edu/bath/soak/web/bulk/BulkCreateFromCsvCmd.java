package edu.bath.soak.web.bulk;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

import edu.bath.soak.web.DefaultHostData;

public class BulkCreateFromCsvCmd implements Serializable {
	byte[] hostCSVData;
	String hostCSVDataString;
	DefaultHostData defaultHostData = new DefaultHostData();

	public enum UploadType {
		STRING, FILE
	};

	UploadType uploadType = UploadType.FILE;

	public BulkCreateFromCsvCmd() {

	}

	public byte[] getHostCSVData() {
		return hostCSVData;
	}

	public void setHostCSVData(byte[] hostCSVData) {
		this.hostCSVData = hostCSVData;
	}

	public String getHostCSVDataString() {
		return hostCSVDataString;
	}

	public void setHostCSVDataString(String hostCSVDataString) {
		this.hostCSVDataString = hostCSVDataString;
	}

	public UploadType getUploadType() {
		return uploadType;
	}

	public void setUploadType(UploadType uploadType) {
		this.uploadType = uploadType;
	}

	@XmlTransient
	public byte[] getUploadData() {
		if (uploadType == UploadType.FILE) {
			return hostCSVData;
		} else {
			if (hostCSVData != null) {
				return hostCSVDataString.getBytes();
			}
			return null;
		}

	}

	public DefaultHostData getDefaultHostData() {
		return defaultHostData;
	}

	public void setDefaultHostData(DefaultHostData defaultHostData) {
		this.defaultHostData = defaultHostData;
	}

}
