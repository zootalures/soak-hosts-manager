package edu.bath.soak.security;

import org.springframework.security.GrantedAuthority;

import edu.bath.soak.model.OrgUnit;

/*******************************************************************************
 * Acegi authoritiy which indicates that the user has authority to adminster a
 * given OU
 * 
 * @author cspocc
 * 
 */
public class OuAdminAuthority implements GrantedAuthority {
	OrgUnit ou;

	public OuAdminAuthority(OrgUnit ou) {
		this.ou = ou;
	}

	public OrgUnit getOu() {
		return ou;
	}

	public String getAuthority() {
		return "SOAK_OU_ADMIN_" + ou.getId();
	}
	public String toString(){
		return getAuthority();
	}
	public int compareTo(Object o) {
		if(!(o instanceof GrantedAuthority)){
			return 0;
		}
		return getAuthority().compareTo(((GrantedAuthority)o).getAuthority());
	}
}
