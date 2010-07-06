package edu.bath.soak.security;

import java.util.List;

import org.springframework.security.GrantedAuthority;

public interface AuthorityGranter {
		List<GrantedAuthority> grantAuthorities(SoakUserDetails ud);
}
