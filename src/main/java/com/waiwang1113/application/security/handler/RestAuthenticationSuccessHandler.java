package com.waiwang1113.application.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.waiwang1113.application.security.SecurityUtils;
import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.repository.UserRepository;
 

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private UserRepository userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		User user = userService.findByEmail(authentication.getName());
		SecurityUtils.sendResponse(response, HttpServletResponse.SC_OK, user);
	}
}