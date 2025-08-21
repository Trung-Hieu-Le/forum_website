package com.example.forum_website.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for JWT operations
 */
@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.expiration}")
    private long EXPIRATION;

    /**
     * Generate JWT token for user
     * 
     * @param userId user ID
     * @return JWT token string
     */
    public String generateToken(Long userId) {
        return generateToken(userId, new HashMap<>());
    }

    /**
     * Generate JWT token with additional claims
     * 
     * @param userId user ID
     * @param additionalClaims additional claims to include
     * @return JWT token string
     */
    public String generateToken(Long userId, Map<String, Object> additionalClaims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION);

        return Jwts.builder()
                .claim("id", userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .addClaims(additionalClaims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    /**
     * Validate and parse JWT token
     * 
     * @param token JWT token string
     * @return Claims object if valid, null otherwise
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Token has expired
            return null;
        } catch (UnsupportedJwtException e) {
            // JWT is not supported
            return null;
        } catch (MalformedJwtException e) {
            // JWT is malformed
            return null;
        } catch (SignatureException e) {
            // JWT signature is invalid
            return null;
        } catch (IllegalArgumentException e) {
            // JWT string is empty
            return null;
        }
    }

    /**
     * Extract user ID from token
     * 
     * @param token JWT token string
     * @return user ID if valid, null otherwise
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.get("id", Long.class) : null;
    }

    /**
     * Check if token is expired
     * 
     * @param token JWT token string
     * @return true if expired, false otherwise
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token expiration time
     * 
     * @param token JWT token string
     * @return expiration date if valid, null otherwise
     */
    public Date getTokenExpiration(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get token issued time
     * 
     * @param token JWT token string
     * @return issued date if valid, null otherwise
     */
    public Date getTokenIssuedAt(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getIssuedAt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get configured expiration time in milliseconds
     * 
     * @return expiration time in milliseconds
     */
    public long getExpiration() {
        return EXPIRATION;
    }

    /**
     * Get configured expiration time in seconds
     * 
     * @return expiration time in seconds
     */
    public long getExpirationInSeconds() {
        return EXPIRATION / 1000;
    }

    /**
     * Check if token will expire soon (within specified time)
     * 
     * @param token JWT token string
     * @param timeBeforeExpiry time in milliseconds before expiry to consider "soon"
     * @return true if token expires soon, false otherwise
     */
    public boolean isTokenExpiringSoon(String token, long timeBeforeExpiry) {
        Date expiryDate = getTokenExpiration(token);
        if (expiryDate == null) {
            return false;
        }
        
        long timeUntilExpiry = expiryDate.getTime() - System.currentTimeMillis();
        return timeUntilExpiry <= timeBeforeExpiry;
    }
}
