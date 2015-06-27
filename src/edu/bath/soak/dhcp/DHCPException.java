package edu.bath.soak.dhcp;

public class DHCPException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public DHCPException(String msg){
		super(msg);
	}
	public DHCPException(Exception e){
		super(e);
	}
}
