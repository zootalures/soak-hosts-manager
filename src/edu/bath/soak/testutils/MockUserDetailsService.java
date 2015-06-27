package edu.bath.soak.testutils;

import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

public class MockUserDetailsService implements UserDetailsService {

	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

}
