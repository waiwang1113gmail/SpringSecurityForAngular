package com.waiwang1113.application.controllers;

import java.security.Principal;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waiwang1113.application.RestfulApiController;

@RestController
public class AuthenticationController extends RestfulApiController {
	@RequestMapping(value = AUTHENTICATE_URL, method = RequestMethod.POST)
	public String authenticate() {
		return "This message shall not be returned to the requester since the request is processed by AuthenticationFilter." ;
	}
	
	@RequestMapping("/user")
	@Secured("ROLE_USER")
	public String getUser(Principal user) {
		return user.getName();
	}
}
