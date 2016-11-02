package com.waiwang1113.application.security.service;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.entity.UserRole;
import com.waiwang1113.application.security.repository.UserRepository;
import com.waiwang1113.application.security.repository.UserRoleRepository;

 
/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private UserRoleRepository userRolesRepo;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.info("Authenticating {}", login);
        User user = userRepo.findByEmail(login);
        if (user == null) {
            throw new UsernameNotFoundException("User " + login + " was not found in the database");
        } 
        //else if (!user.getEnabled()) {
       //     throw new UserNotEnabledException("User " + login + " was not enabled");
        //}
        
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (UserRole authority : userRolesRepo.findByUserId(user.getId())) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getRole());
            grantedAuthorities.add(grantedAuthority);
        }

        return new org.springframework.security.core.userdetails.User(login, user.getPassword(),
                grantedAuthorities);
    }
}
