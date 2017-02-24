package com.waiwang1113.application.security;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;
import static org.hamcrest.core.IsNot.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.RestAssured;
import com.waiwang1113.application.RestfulApiController;

import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {
	private static final String X_AUTH_USERNAME = "X-Auth-Username";
    private static final String X_AUTH_PASSWORD = "X-Auth-Password";
    private static final String X_AUTH_TOKEN = "X-Auth-Token";
    @LocalServerPort
    private int port;
    
    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost"; 
        RestAssured.port = port; 
    }
    @Test
    public void test_no_header_response() {
        when()
        	.get(TestController.USER_URL)
        .then()
        	.statusCode(HttpStatus.UNAUTHORIZED.value())
        	.body("message", equalTo("Authentication failed"));
    }
    
    @Test
    public void test_authenticate() {
    	given()
    		.header(X_AUTH_USERNAME, "user")
    		.header(X_AUTH_PASSWORD,"password").
        when()
        	.post(RestfulApiController.AUTHENTICATE_URL)
    	.then()
    		.statusCode(HttpStatus.OK.value())
    		.body("token", not(nullValue()));
    }
    @Test
    public void test_authenticate_fail() {
    	given()
    		.header(X_AUTH_USERNAME, "user")
    		.header(X_AUTH_PASSWORD,"password").
        when()
        	.get(RestfulApiController.AUTHENTICATE_URL)
    	.then()
    		.statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
    		.body("token", not(nullValue()));
    }
    
}
