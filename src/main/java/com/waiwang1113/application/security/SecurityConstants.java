package com.waiwang1113.application.security;

public class SecurityConstants {
	public static final String REMEMBER_ME_KEY = "rememberme_key";
	// Token is valid for one month
	public static final int TOKEN_VALIDITY_DAYS = 31;

	public static final int TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * TOKEN_VALIDITY_DAYS;

	public static final int DEFAULT_SERIES_LENGTH = 16;

	public static final int DEFAULT_TOKEN_LENGTH = 16;
}
