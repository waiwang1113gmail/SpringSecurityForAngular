package com.waiwang1113.application.security.infrastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.entity.UserRole;
import com.waiwang1113.application.security.repository.UserRepository;
import com.waiwang1113.application.security.repository.UserRoleRepository;

@Service
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider{
	Logger LOG = LoggerFactory.getLogger(UsernamePasswordAuthenticationProvider.class);
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private UserRoleRepository userRoleRepo;
	@Autowired
	private TokenService tokenRepository;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		@SuppressWarnings("unchecked")
		Optional<String> username = (Optional<String>) authentication.getPrincipal();
		@SuppressWarnings("unchecked")
		Optional<String> password = (Optional<String>) authentication.getCredentials();
		LOG.debug("Authenticate user: {}",username);
		if(!username.isPresent() || !password.isPresent() ){ 
			throw new BadCredentialsException("Invalid username.");
		}
		User user = userRepo.findByEmailAndPassword(username.get(),password.get());
		if(user == null){
			throw new BadCredentialsException("Authentication failed");
		}
		List<UserRole> userRoles = userRoleRepo.findByUserId(user.getId()); 
		List<GrantedAuthority> grantedAuthority = new ArrayList<>();
		userRoles.forEach(u -> grantedAuthority.add(new SimpleGrantedAuthority(u.getRole())));
		AuthenticationWithToken resultOfAuthentication = new AuthenticationWithToken(user,null,grantedAuthority);
		String token = tokenRepository.generateNewToken();
		resultOfAuthentication.setToken(token);
		tokenRepository.store(token, resultOfAuthentication);
		return resultOfAuthentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.equals(authentication);
	} 
}
