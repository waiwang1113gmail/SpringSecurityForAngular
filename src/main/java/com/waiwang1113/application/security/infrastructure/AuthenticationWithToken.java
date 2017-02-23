package com.waiwang1113.application.security.infrastructure;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class AuthenticationWithToken extends PreAuthenticatedAuthenticationToken{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8458157519709065307L;
	
	public AuthenticationWithToken(Object aPrincipal, Object aCredentials) {
		super(aPrincipal, aCredentials); 
	}
	public AuthenticationWithToken(Object aPrincipal, Object aCredentials, Collection<? extends GrantedAuthority> anAuthorities) {
        super(aPrincipal, aCredentials, anAuthorities);
    }
	public void setToken(String token){
		setDetails(token);
	}
	public String getToken(){
		return (String)getDetails();
	}
	
}
