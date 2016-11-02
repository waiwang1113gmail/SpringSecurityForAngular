package com.waiwang1113.application.security;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waiwang1113.application.security.entity.CookieToken;
import com.waiwang1113.application.security.entity.User;
import com.waiwang1113.application.security.repository.CookieTokenRepository;
import com.waiwang1113.application.security.repository.UserRepository;
import static com.waiwang1113.application.security.SecurityConstants.*;

/**
 * Custom implementation of Spring Security's RememberMeServices.
 * <p/>
 * Persistent tokens are used by Spring Security to automatically log in users.
 * <p/>
 * This is a specific implementation of Spring Security's remember-me
 * authentication, but it is much more powerful than the standard
 * implementations:
 * <ul>
 * <li>It allows a user to see the list of his currently opened sessions, and
 * invalidate them</li>
 * <li>It stores more information, such as the IP address and the user agent,
 * for audit purposes
 * <li>
 * <li>When a user logs out, only his current session is invalidated, and not
 * all of his sessions</li>
 * </ul>
 * <p/>
 */
@Service
public class RememberMeServices extends AbstractRememberMeServices {

	private SecureRandom random;

	@Autowired
	private CookieTokenRepository tokenRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	public RememberMeServices(Environment env, UserDetailsService userDetailsService) {
		super(REMEMBER_ME_KEY, userDetailsService);
		super.setParameter("rememberme");
		random = new SecureRandom();
	}

	@Override
	@Transactional
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
			HttpServletResponse response) {

		CookieToken token = getPersistentToken(cookieTokens);
		String login = token.getUserLogin();

		// Token also matches, so login is valid. Update the token value,
		// keeping the *same* series number.
		// log.debug("Refreshing persistent login token for user '{}', series
		// '{}'", login, token.getSeries());
		token.setDate(new Date());
		token.setValue(generateTokenData());
		token.setIpAddress(request.getRemoteAddr());
		token.setUserAgent(request.getHeader("User-Agent"));
		try {
			tokenRepo.save(token);
			addCookie(token, request, response);
		} catch (DataAccessException e) {
			// log.error("Failed to update token: ", e);
			throw new RememberMeAuthenticationException("Autologin failed due to data access problem", e);
		}
		return getUserDetailsService().loadUserByUsername(login);
	}

	@Override
	protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication) {
		String login = successfulAuthentication.getName();

		// log.debug("Creating new persistent login for user {}", login);
		User user = userRepo.findByEmail(login);
		CookieToken token = new CookieToken();
		token.setSeries(generateSeriesData());
		token.setUserLogin(user.getEmail());
		token.setValue(generateTokenData());
		token.setDate(new Date());
		token.setUserAgent(request.getHeader("User-Agent"));
		try {
			tokenRepo.save(token);
			addCookie(token, request, response);
		} catch (DataAccessException e) {
			// log.error("Failed to save persistent token ", e);
		}
	}

	/**
	 * When logout occurs, only invalidate the current token, and not all user
	 * sessions.
	 * <p/>
	 * The standard Spring Security implementations are too basic: they
	 * invalidate all tokens for the current user, so when he logs out from one
	 * browser, all his other sessions are destroyed.
	 */
	@Override
	@Transactional
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String rememberMeCookie = extractRememberMeCookie(request);
		if (rememberMeCookie != null && rememberMeCookie.length() != 0) {
			try {
				String[] cookieTokens = decodeCookie(rememberMeCookie);
				CookieToken token = getPersistentToken(cookieTokens);
				tokenRepo.delete(token.getSeries());
			} catch (InvalidCookieException ice) {
				// log.info("Invalid cookie, no persistent token could be
				// deleted");
			} catch (RememberMeAuthenticationException rmae) {
				// log.debug("No persistent token found, so no token could be
				// deleted");
			}
		}
		super.logout(request, response, authentication);
	}

	/**
	 * Validate the token and return it.
	 */
	private CookieToken getPersistentToken(String[] cookieTokens) {
		if (cookieTokens.length != 2) {
			throw new InvalidCookieException("Cookie token did not contain " + 2 + " tokens, but contained '"
					+ Arrays.asList(cookieTokens) + "'");
		}

		final String presentedSeries = cookieTokens[0];
		final String presentedToken = cookieTokens[1];

		CookieToken token = null;
		try {
			token = tokenRepo.findOne(presentedSeries);
		} catch (DataAccessException e) {
			// log.error("Error to access database", e );
		}

		if (token == null) {
			// No series match, so we can't authenticate using this cookie
			throw new RememberMeAuthenticationException("No persistent token found for series id: " + presentedSeries);
		}

		// We have a match for this user/series combination
		// log.info("presentedToken={} / tokenValue={}", presentedToken,
		// token.getValue());
		if (!presentedToken.equals(token.getValue())) {
			// Token doesn't match series value. Delete this session and throw
			// an exception.
			tokenRepo.delete(token.getSeries());
			throw new CookieTheftException(
					"Invalid remember-me token (Series/token) mismatch. Implies previous cookie theft attack.");
		}

		if (DateUtils.addDays(token.getDate(), TOKEN_VALIDITY_DAYS).before(new Date())) {
			tokenRepo.delete(token.getSeries());
			throw new RememberMeAuthenticationException("Remember-me login has expired");
		}
		return token;
	}

	private String generateSeriesData() {
		byte[] newSeries = new byte[DEFAULT_SERIES_LENGTH];
		random.nextBytes(newSeries);
		return new String(Base64.encode(newSeries));
	}

	private String generateTokenData() {
		byte[] newToken = new byte[DEFAULT_TOKEN_LENGTH];
		random.nextBytes(newToken);
		return new String(Base64.encode(newToken));
	}

	private void addCookie(CookieToken token, HttpServletRequest request, HttpServletResponse response) {
		setCookie(new String[] { token.getSeries(), token.getValue() }, TOKEN_VALIDITY_SECONDS, request, response);
	}
}