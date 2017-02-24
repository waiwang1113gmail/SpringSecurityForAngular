package com.waiwang1113.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.entity.UserRole;
import com.waiwang1113.application.security.repository.UserRepository;
import com.waiwang1113.application.security.repository.UserRoleRepository;

/**
 * Main configuration class for application wide configurstion
 * @author wanwe17
 *
 */
@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {
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
