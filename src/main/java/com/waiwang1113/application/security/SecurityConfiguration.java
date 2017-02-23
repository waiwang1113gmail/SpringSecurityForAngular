package com.waiwang1113.application.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.waiwang1113.application.response.ResponseHelper;
import com.waiwang1113.application.security.infrastructure.AuthenticationFilter;
import com.waiwang1113.application.security.infrastructure.TokenAuthenticationProvider;
import com.waiwang1113.application.security.infrastructure.UsernamePasswordAuthenticationProvider;
 

/**
 * Configuration class for web security.
 * @author wanwe17
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);
	@Autowired 
	TokenAuthenticationProvider tokenAuthenticationProvider;
	
	@Autowired 
	UsernamePasswordAuthenticationProvider usernamePasswordAuthenticationProvider;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.anonymous().disable();
		http.exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint()).accessDeniedHandler(unauthroizedEntryPoint2());
		
		http.addFilterBefore(new AuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
		
		//Comment out in case we need backend admin
		//.addFilterBefore(/*new ManagementEngpointAuthenticationFilter(authenticationManager())*/null, BasicAuthenticationFilter.class)
	}
	private AccessDeniedHandler unauthroizedEntryPoint2() {
		return (request, response, authException) ->{
			LOG.trace("Unauthorized access.",authException);
			ResponseHelper.sendError(response, HttpServletResponse.SC_FORBIDDEN,
			   "Authorization failed");
		};
	}
	private AuthenticationEntryPoint unauthorizedEntryPoint(){
		return (request, response, authException) -> 
			{
				LOG.trace("Unauthorized access.",authException);
				ResponseHelper.sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
				   "Authentication failed");
				
			};
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder authBuilder){
		authBuilder.authenticationProvider(usernamePasswordAuthenticationProvider)
			.authenticationProvider(tokenAuthenticationProvider);
	}

}
