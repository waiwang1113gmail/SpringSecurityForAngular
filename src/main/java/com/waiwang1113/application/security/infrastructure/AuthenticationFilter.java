package com.waiwang1113.application.security.infrastructure;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import com.waiwang1113.application.security.SecurityConstants;

/**
 * Custom Filter for handing user name and token authentication
 * @author Weige
 *
 */
public class AuthenticationFilter extends GenericFilterBean{
	private Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		Optional<String> username = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_USERNAME_HEADER));
		Optional<String> password = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_PASSWORD_HEADER));
		Optional<String> token = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_TOKEN_HEADER));
		
		String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
		
		try{
			if(postToAuthenticate(httpRequest,resourcePath)){
				LOG.debug("Trying to authenticate {} by username and password",username,resourcePath);
				processUsernamePasswordAuthentication(httpResponse,username,password);
				return;
			}
			if(token.isPresent()){
				LOG.debug("Trying to authenticate user {} by token {}",username,token);
				processTokenAuthentication(token);
			}
			LOG.debug("AuthenticationFilter is passing request down the filter chain.");
			addSessionContextToLogging();
			chain.doFilter(request, response);
		}catch(Exception e){
			// TODO Catch exceptions
		}finally{
			// TODO Remove session logging context 
		}
	}
	private void addSessionContextToLogging() {
		// TODO Auto-generated method stub
		
	}
	private void processTokenAuthentication(Optional<String> token) {
		// TODO Auto-generated method stub
		
	}
	private void processUsernamePasswordAuthentication(HttpServletResponse httpResponse, Optional<String> username,
			Optional<String> password) {
		Authentication resultOfAuthentication = tryToAuthenticateWithUsernamePassword(username,password);
		SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
		httpResponse.setStatus(HttpServletResponse.SC_OK);
		
		
	}
	private Authentication tryToAuthenticateWithUsernamePassword(Optional<String> username,
			Optional<String> password) {
		// TODO Auto-generated method stub
		return null;
	}
	private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
		// TODO Auto-generated method stub
		return false;
	}

}
