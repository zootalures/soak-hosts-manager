/**
 * 
 */
package edu.bath.soak.security;

import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;

/**
 * Holder class for details about the current user
 * 
 * @see LDAPUserDetailsService for how this structure is populated from LDAP
 * @author cspocc
 * 
 */
public class SoakUserDetails implements UserDetails {

	String dn;
	Attributes attributes;
	GrantedAuthority[] authorities;
	Control[] controls;
	String username;
	String friendlyName;
	String email;

	public String getPassword() {
		return "not-set";
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	public GrantedAuthority[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(GrantedAuthority[] authoroities) {
		this.authorities = authoroities;
	}

	public Control[] getControls() {
		return controls;
	}

	public void setControls(Control[] controls) {
		this.controls = controls;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}
}