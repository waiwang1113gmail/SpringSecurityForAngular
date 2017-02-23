package com.waiwang1113.application.security.infrastructure;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

@Service
public class TokenService {
	private static final String CACHE_NAME = "AuthenticationCache";
	private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);
	private static final Cache authenticationTokenCache = CacheManager.getInstance().getCache(CACHE_NAME);
	
	public static final int HALF_AN_HOUR_IN_MILI = 30 *60 * 1000;
	
	@Scheduled(fixedRate = HALF_AN_HOUR_IN_MILI)
	public void EvictExpiredTokens(){
		LOG.info("Evicting expired tokens");
		authenticationTokenCache.evictExpiredElements();
	}
	
	public String generateNewToken(){
		return UUID.randomUUID().toString();
	}
	public void store(String token, Authentication authentication) {
		authenticationTokenCache.put(new Element(token, authentication));
    }
	public boolean contains(String token){
		return authenticationTokenCache.get(token)!=null;
	}
	public Authentication retrieve(String token) {
		return (Authentication) authenticationTokenCache.get(token).getObjectValue();
	}
	
}
