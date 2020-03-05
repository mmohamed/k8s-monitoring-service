package dev.medinvention.service.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dev.medinvention.service.model.AuthToken;
import dev.medinvention.service.model.LoginUser;
import dev.medinvention.service.security.JwtTokenUtil;

@RestController
@RequestMapping("/token")
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public ResponseEntity<AuthToken> login(@RequestBody LoginUser loginUser) throws AuthenticationException {

		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		final String token = jwtTokenUtil.generateToken(loginUser.getUsername());
		
		return ResponseEntity.ok(new AuthToken(token, loginUser.getUsername()));
	}
	
	@RequestMapping(value = "/revoke", method = RequestMethod.POST)
	public ResponseEntity<AuthToken> logout(@RequestBody AuthToken token) throws AuthenticationException {
		
		if(!jwtTokenUtil.revokeToken(token.getToken(), token.getUsername())) {
			return ResponseEntity.status(401).body(token);
		}
		
		return ResponseEntity.ok(token);
	}
}
