package edu.bath.soak.imprt.cmd;



/*******************************************************************************
 * System import command Directly imports system data into the database.
 * 
 * @author cspocc
 * 
 */

public class BulkSystemImportCmd extends BulkCommand {
	XMLImportData data ;
	public XMLImportData getData() {
		return data;
	}

	public void setData(XMLImportData data) {
		this.data = data;
	}

	
}