package com.waiwang1113.application.security.infrastructure;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.assertj.core.util.Strings;
import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import com.waiwang1113.application.RestfulApiController;
import com.waiwang1113.application.response.ResponseHelper;
import com.waiwang1113.application.response.TokenResponse;
import com.waiwang1113.application.security.SecurityConstants;

/**
 * Custom Filter for handing user name and token authentication
 * @author Weige
 *
 */
public class AuthenticationFilter extends GenericFilterBean{
	private static final String TOKEN_SESSION_KEY = "token";

	private static final String USER_SESSION_KEY = "user";

	private Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	private AuthenticationManager authenticationManager;
	public AuthenticationFilter(AuthenticationManager authenticationManager){
		this.authenticationManager=authenticationManager;
	}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		Optional<String> username = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_USERNAME_HEADER));
		Optional<String> password = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_PASSWORD_HEADER));
		Optional<String> token = Optional.ofNullable(httpRequest.getHeader(SecurityConstants.X_AUTH_TOKEN_HEADER));
		
		String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
		LOG.debug("Requested resource: {}" ,resourcePath);
		try{
			if(postToAuthenticate(httpRequest,resourcePath)){
				LOG.debug("Trying to authenticate {} by username and password",username);
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
		} catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            SecurityContextHolder.clearContext();
            logger.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException authenticationException) {
            SecurityContextHolder.clearContext();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        }finally{
			MDC.remove(TOKEN_SESSION_KEY);
			MDC.remove(USER_SESSION_KEY);
		}
	}
	private void addSessionContextToLogging() {
		Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
		String tokenValue = "EMPTY";
		if(currentAuthentication !=null && !Strings.isNullOrEmpty(currentAuthentication.getDetails().toString())){
			MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("SHA-1");
			tokenValue = encoder.encodePassword(currentAuthentication.getDetails().toString(),"random test "/*UNKNOW*/);
		}
		MDC.put(TOKEN_SESSION_KEY, tokenValue);
		String userValue = "EMPTY";
		if(currentAuthentication !=null && !Strings.isNullOrEmpty(currentAuthentication.getPrincipal().toString())){
			userValue = currentAuthentication.getPrincipal().toString();
		}
		MDC.put(USER_SESSION_KEY, userValue);
		
	}
	private void processTokenAuthentication(Optional<String> token) {
		PreAuthenticatedAuthenticationToken requestAuthentication =new PreAuthenticatedAuthenticationToken(token,null);
		Authentication resultAuthentication = tryToAuthenticate(requestAuthentication);
		SecurityContextHolder.getContext().setAuthentication(resultAuthentication);
		
	}
	private void processUsernamePasswordAuthentication(HttpServletResponse httpResponse, Optional<String> username,
			Optional<String> password) throws IOException {
		Authentication resultOfAuthentication = tryToAuthenticateWithUsernamePassword(username,password);
		SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
		httpResponse.setStatus(HttpServletResponse.SC_OK);
		TokenResponse tokenResponse = new TokenResponse(resultOfAuthentication.getDetails().toString());
		ResponseHelper.sendResponse(httpResponse, HttpServletResponse.SC_OK, tokenResponse);
		
	}
	private Authentication tryToAuthenticateWithUsernamePassword(Optional<String> username,
			Optional<String> password) {
		UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(username,password);
		
		return tryToAuthenticate(requestAuthentication);
	}
	private Authentication tryToAuthenticate(Authentication requestAuthentication) {
		Authentication responseAuthentication = this.authenticationManager.authenticate(requestAuthentication);
		if(responseAuthentication == null || !responseAuthentication.isAuthenticated()){
			throw new InternalAuthenticationServiceException("Unable to authenticate yser for provided credentials");
		}
		LOG.debug("User successfully authenticated");
		return responseAuthentication;
	}
	/**
	 * Verify if the request type is post and target endpoint is authentication endpoint
	 * @param httpRequest
	 * @param resourcePath
	 * @return
	 */
	private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
		return httpRequest.getMethod().equalsIgnoreCase("POST") && RestfulApiController.AUTHENTICATE_URL.equals(resourcePath);
	}

}
