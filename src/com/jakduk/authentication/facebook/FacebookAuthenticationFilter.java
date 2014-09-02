package com.jakduk.authentication.facebook;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.http.AccessTokenRequiredException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.jakduk.common.CommonConst;

/**
 * @author <a href="mailto:phjang1983@daum.net">Jang,Pyohwan</a>
 * @company  : http://jakduk.com
 * @date     : 2014. 7. 27.
 * @desc     :
 */
public class FacebookAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private Logger logger = Logger.getLogger(this.getClass());

	public FacebookAuthenticationFilter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	public FacebookAuthenticationFilter() {
		super("/facebook_login");
	}

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken("facebook", null);
		SecurityContextHolder.getContext().setAuthentication(authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		Integer addInfoStatus = ((FacebookUserDetails)authResult.getPrincipal()).getAddInfoStatus();
		
		if (addInfoStatus.equals(CommonConst.OAUTH_ADDITIONAL_INFO_STATUS_BLANK)) {
			response.sendRedirect(request.getContextPath() + "/oauth/write");
		}
		
		super.successfulAuthentication(request, response, chain, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException failed)
					throws IOException, ServletException {
		if (failed instanceof AccessTokenRequiredException
				|| failed instanceof AccessTokenRequiredException) {
			// Need to force a redirect via the OAuth client filter, so rethrow
			// here
			throw failed;
		} else {
			// If the exception is not a Spring Security exception this will
			// result in a default error page
			super.unsuccessfulAuthentication(request, response, failed);
		}
	} 

}