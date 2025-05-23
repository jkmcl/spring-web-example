package jkml.security;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Based on (@link BasicAuthenticationFilter}.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

	private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder
			.getContextHolderStrategy();

	private final AuthenticationManager authenticationManager;

	private final AuthenticationConverter authenticationConverter = new ApiKeyAuthenticationConverter();

	private final SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

	public ApiKeyAuthenticationFilter(AuthenticationManager authenticationManager) {
		Assert.notNull(authenticationManager, "authenticationManager cannot be null");
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		try {
			var authRequest = authenticationConverter.convert(request);
			if (authRequest == null) {
				filterChain.doFilter(request, response);
				return;
			}
			var authResult = authenticationManager.authenticate(authRequest);
			var context = securityContextHolderStrategy.createEmptyContext();
			context.setAuthentication(authResult);
			securityContextHolderStrategy.setContext(context);
			securityContextRepository.saveContext(context, request, response);
		} catch (AuthenticationException ex) {
			logger.debug("Failed to process authentication request", ex);
			securityContextHolderStrategy.clearContext();
		}

		filterChain.doFilter(request, response);
	}

}
