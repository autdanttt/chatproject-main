package com.forcy.chatapp.security.jwt;

import com.forcy.chatapp.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

@Component
public class JwtUtility {
    private final Logger logger = LoggerFactory.getLogger(JwtUtility.class);
    private static final String SECRET_KEY_ALGORIHM = "HmacSHA512";
    @Value("${app.security.jwt.issuer}")
    private String issuerName;
    @Value("${app.security.jwt.secret}")
    private String secretKey;
    @Value("${app.security.jwt.access-token.expiration}")
    private int accessTokenExpiration;


    public String generateAccessToken(User user){
        if(user== null || user.getId() == null || user.getRoles().isEmpty()){
            throw new IllegalArgumentException("user object is null or its fields have null values");
        }
        long expirationTimeMillis = System.currentTimeMillis() + accessTokenExpiration * 60000L;
        logger.info("System time miliis: " +System.currentTimeMillis() );
        System.out.println("System time miliis: " +System.currentTimeMillis());
        String subject = String.format("%s,%s", user.getId(), user.getUsername());
        Date now = new Date();
        logger.info("Date now is {}", now);
        Date expiration = new Date(expirationTimeMillis);
        logger.info("Expiration Date is {}", expiration);
        return Jwts.builder()
                .subject(subject)
                .issuer(issuerName)
                .issuedAt(now)
                .expiration(expiration)
                .claim("roles", user.getRoles().toString())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), Jwts.SIG.HS512)
                .compact();
    }
    public Claims validateAccessToken(String token) throws JwtValidationException {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), SECRET_KEY_ALGORIHM);
            return Jwts.parser()
                    .verifyWith(keySpec)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (ExpiredJwtException ex){
            Date now = new Date();
            System.out.println("Now: " + now);
            System.out.println("Token Expiration: " + ex.getClaims().getExpiration());
            throw new JwtValidationException("Access token expired", ex);
        }catch (IllegalArgumentException ex){
            throw new JwtValidationException("Access token illegal", ex);
        }catch (MalformedJwtException ex){
            throw new JwtValidationException("Access token is not well formed", ex);
        }catch (UnsupportedJwtException ex){
            throw new JwtValidationException("Access token is not supported", ex);
        }
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public void setAccessTokenExpiration(int accessTokenExpiration) {
        this.accessTokenExpiration = accessTokenExpiration;
    }
}
