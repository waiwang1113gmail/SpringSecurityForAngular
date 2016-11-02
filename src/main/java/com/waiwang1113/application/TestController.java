package com.waiwang1113.application;

import java.security.Principal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Add rest endpoints to verify if security works correctly 
 * @author wanwe17
 *
 */
@RestController
public class TestController {
	@RequestMapping("/admin")
	public String getAdmin(Principal user) {
		return user.getName();
	}
	
	@RequestMapping("/user")
	public String getUser(Principal user) {
		return user.getName();
	}
}
