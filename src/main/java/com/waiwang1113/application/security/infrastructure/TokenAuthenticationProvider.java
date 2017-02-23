package com.waiwang1113.application.security.infrastructure;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service; 

@Service
public class TokenAuthenticationProvider implements AuthenticationProvider{
	Logger LOG = LoggerFactory.getLogger(TokenAuthenticationProvider.class);
	@Autowired
	private TokenService tokenRepository;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		@SuppressWarnings("unchecked")
		Optional<String> token = (Optional<String>)authentication.getPrincipal();
		if(!token.isPresent() || token.get().isEmpty()){
			LOG.trace("Invalid token: {}", token);
			throw new BadCredentialsException("Invalid token");
		} 
		if(!tokenRepository.contains(token.get())){
			LOG.warn("Invalid token or token expired {}",token);
			throw new BadCredentialsException("Invalid token or token expired.");
		}
		return tokenRepository.retrieve(token.get());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(PreAuthenticatedAuthenticationToken.class);
	}

}
