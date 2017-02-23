package com.waiwang1113.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.entity.UserRole;
import com.waiwang1113.application.security.repository.UserRepository;
import com.waiwang1113.application.security.repository.UserRoleRepository;


//This class is used to add dummy user and roles for verifying
@Component
public class TestSetup {
	@Autowired
	UserRepository userRepo;
	
	@Autowired
	UserRoleRepository roleRepo;
	
    @EventListener({ContextRefreshedEvent.class})
    void contextRefreshedEvent() {
    	User user=new User();
		user.setEmail("user");
		user.setFirstName("Wei");
		user.setLastName("Wang");
		user.setPassword("password");
		user=userRepo.save(user);
		UserRole role=new UserRole();
		role.setUserId(user.getId());
		role.setRole("ROLE_USER");
		roleRepo.save(role);
    }
}