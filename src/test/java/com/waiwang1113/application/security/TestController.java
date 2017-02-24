package com.waiwang1113.application.security;

import java.security.Principal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waiwang1113.application.RestfulApiController;

@RestController
public class TestController extends RestfulApiController{
	public static final String ADM_URL=BASE_URL+"/admin";
	public static final String USER_URL=BASE_URL+"/user";
	@RequestMapping(ADM_URL)
	@Secured("ROLE_ADIM")
	public String getAdmin(Principal user) {
		return user.getName();
	}
	
	@RequestMapping(USER_URL)
	@Secured("ROLE_USER")
	public String getUser(Principal user) {
		return user.getName();
	}
}
