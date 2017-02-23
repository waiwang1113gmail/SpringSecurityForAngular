package com.waiwang1113.application.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
 

/**
 * Configuration class for web security.
 * @author wanwe17
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true,securedEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.anonymous().disable();
		http.exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());
		
		http.addFilterBefore(/*new AuthenticationFilter(authenticationManager())*/null, BasicAuthenticationFilter.class);
		
		//Comment in case we need backend admin
		//.addFilterBefore(/*new ManagementEngpointAuthenticationFilter(authenticationManager())*/null, BasicAuthenticationFilter.class)
	}
	private AuthenticationEntryPoint unauthorizedEntryPoint(){
		return (request, response, authException) -> SecurityUtils.sendError(response, authException, HttpServletResponse.SC_UNAUTHORIZED,
				   "Authentication failed");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder authBuilder){
		authBuilder.authenticationProvider(usernamePasswordAuthenticationProvider())
			.authenticationProvider(tokenAuthenticationProvider());
	}
	private AuthenticationProvider tokenAuthenticationProvider() {
		// TODO Auto-generated method stub
		return null;
	}
	private AuthenticationProvider usernamePasswordAuthenticationProvider() {
		// TODO Auto-generated method stub
		return null;
	}
}
