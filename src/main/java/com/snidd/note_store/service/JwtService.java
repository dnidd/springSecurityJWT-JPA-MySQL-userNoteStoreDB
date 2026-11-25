package com.snidd.note_store.service;

import com.snidd.note_store.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Jwt service handles key generation and token Jwt generation.
 */
@Service
public class JwtService {

    // secret key as env. var.
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Generate JWT token.
     * @param user user.
     * @return JWT token String.
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts
                .builder()
                .claims()
                .add(claims) // the Map, empty atm.
                .subject(user.getUsername())
                .issuer("The creator")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60*10*1000 ))
                .and()
                .signWith(generateKey()) // the secret key
                .compact();
    }

    /**
     * Create JWT secret key object
     * from stored (env. var.) secret key String.
     * @return JJWT secret key object.
     */
    private SecretKey generateKey() {
        // Decode secret key variable into bytes.
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    /**
     * Fetch Claims-object.
     * Based on jwsToken, call getSubject() on Claims.
     * @param jwtToken jwtToken.
     * @return subject/username
     */
    // extractClaims(token, claims -> claims.getSubject());
    // As anonymous function:
    // return extractClaims(token, new Function<Claims, String>() {
    //    @Override
    //    public String apply(Claims claims) {
    //        return claims.getSubject();
    public String extractUsername(String jwtToken) {
        // run and pass a token and a function to fetch the subject from Claims.
        return extractClaims(jwtToken, Claims::getSubject);
    }


    /**
     * Helper method, takes a token and applies
     * a function to fetch the data (username).
     * @param jwtToken jwtToken.
     * @param claimResolver claimResolver function
     * @param <T> generic type.
     * @return claimResolver function
     */
    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimResolver) {
        // Fetch entire Claims object.
        Claims claims = extractClaims(jwtToken);
        // runs Claims::getSubject as param in this method and fetch subject/username.
        return claimResolver.apply(claims);
    }

    /**
     * Fetch/extract Claims object (payload/user data) from JWT token.
     * @param jwtToken jwtToken.
     * @return Claims object with JWT token payload (containing the subject/user).
     */
    private Claims extractClaims(String jwtToken) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }


    /**
     *
     * @param jwtToken jwtToken.
     * @param userDetails userDetails.
     * @return boolean of username && token !expired.
     */
    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        final String username = extractUsername(jwtToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken) );
    }

    /**
     * Check if jwtToken is passed current date.
     * Expiration date should not be before current date.
     * If expiration is before current date, token is expired.
     * @param jwtToken jwtToken.
     * @return boolean of before or after current date.
     */
    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }


    /**
     * Extract expiration time from jwt token.
     * @param jwtToken jwtToken.
     * @return the expiration date.
     */
    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }


}
