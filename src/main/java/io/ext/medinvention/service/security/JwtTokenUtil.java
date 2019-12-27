package io.ext.medinvention.service.security;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 5717029039756858242L;
	private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
	private static final String SIGNING_KEY = "k8smonitoring";

	public String getUsernameFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.getSubject();
	}

	public Date getExpirationDateFromToken(String token) {
		final Claims claims = getAllClaimsFromToken(token);
		return claims.getExpiration();
	}

	public String generateToken(String username) {
		return doGenerateToken(username);
	}

	public Boolean validateToken(String token, String user) {
		final String username = getUsernameFromToken(token);
		return (username.equals(user) && !isTokenExpired(token));
	}

	private String doGenerateToken(String subject) {
		Claims claims = Jwts.claims().setSubject(subject);
		claims.put("scopes", Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));

		return Jwts.builder().setClaims(claims).setIssuer("https://devglan.com")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
				.signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
	}

	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
}
