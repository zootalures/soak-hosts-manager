package edu.bath.soak.web.bulk;

import java.io.Serializable;

public class BulkCreateChooseTypeCmd implements Serializable {
	public enum BulkCreateType {
		CSV_UPLOAD, RANGE
	}

	BulkCreateType type = BulkCreateType.CSV_UPLOAD;

	public BulkCreateChooseTypeCmd() {

	}

	public BulkCreateType getType() {
		return type;
	}

	public void setType(BulkCreateType type) {
		this.type = type;
	}

}
