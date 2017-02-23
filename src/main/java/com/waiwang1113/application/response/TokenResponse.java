package com.waiwang1113.application.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
	@JsonProperty
	private String token;
	
	public TokenResponse() {
    }
	public TokenResponse(String token) {
        this.token = token;
    }
}
