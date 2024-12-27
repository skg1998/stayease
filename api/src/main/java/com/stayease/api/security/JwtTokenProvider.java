package com.stayease.api.security;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stayease.api.entity.User;
import com.stayease.api.exceptions.NotFoundException;
import com.stayease.api.services.UserService;

import static com.stayease.api.constants.Contants.TOKEN_HEADER;
import static com.stayease.api.constants.Contants.TOKEN_TYPE;

@Component
@Slf4j
public class JwtTokenProvider {
    private final UserService userService;

    private final String appSecret;

    @Getter
    private final Long tokenExpiresIn;

    @Getter
    private Long refreshTokenExpiresIn;

    private final Long rememberMeTokenExpiresIn;

    private final HttpServletRequest httpServletRequest;

    public JwtTokenProvider(
        @Value("${app.secret}") final String appSecret,
        @Value("${app.jwt.token.expires-in}") final Long tokenExpiresIn,
        @Value("${app.jwt.refresh-token.expires-in}") final Long refreshTokenExpiresIn,
        @Value("${app.jwt.remember-me.expires-in}") final Long rememberMeTokenExpiresIn,
        final UserService userService,
        final HttpServletRequest httpServletRequest
    ) {
        this.userService = userService;
        this.appSecret = appSecret;
        this.tokenExpiresIn = tokenExpiresIn;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
        this.rememberMeTokenExpiresIn = rememberMeTokenExpiresIn;
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * Generate token by user ID.
     *
     * @param id      String
     * @param expires Long
     * @return String
     */
    public String generateTokenByUserId(final Long id, final Long expires) {
    	
        String token = Jwts.builder()
            .setSubject(id.toString())
            .setIssuedAt(new Date())
            .setExpiration(getExpireDate(expires))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
        log.trace("Token is added to the local cache for userID: {}, ttl: {}", id, expires);

        return token;
    }

    /**
     * Generate JWT token by user ID.
     *
     * @param id String
     * @return String
     */
    public String generateJwt(final Long id) {
        return generateTokenByUserId(id, tokenExpiresIn);
    }

    /**
     * Generate refresh token by user ID.
     *
     * @param id String
     * @return String
     */
    public String generateRefresh(final Long id) {
        return generateTokenByUserId(id, refreshTokenExpiresIn);
    }

    /**
     * Get JwtUserDetails from authentication.
     *
     * @param authentication Authentication
     * @return JwtUserDetails
     */
    public JwtUserDetails getPrincipal(final Authentication authentication) {
        return userService.getPrincipal(authentication);
    }

    /**
     * Get user ID from token.
     *
     * @param token String
     * @return String
     */
    public Long getUserIdFromToken(final String token) {
    	log.info("##### Token: {}", token);
        Claims claims = parseToken(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Get user from token.
     *
     * @param token String
     * @return User
     */
    public User getUserFromToken(final String token) {
        try {
            return userService.findById(getUserIdFromToken(token));
        } catch (NotFoundException e) {
            return null;
        }
    }

    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    public boolean validateToken(final String token) {
        return validateToken(token, true);
    }

    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    public boolean validateToken(final String token, final boolean isHttp) {
        parseToken(token);
        return !isTokenExpired(token);
    }

    /**
     * Validate token.
     *
     * @param token              String
     * @param httpServletRequest HttpServletRequest
     * @return boolean
     */
    public boolean validateToken(final String token, final HttpServletRequest httpServletRequest) {
        try {
            boolean isTokenValid = validateToken(token);
            if (!isTokenValid) {
                log.error("[JWT] Token could not found in local cache");
                httpServletRequest.setAttribute("notfound", "Token is not found in cache");
            }
            return isTokenValid;
        } catch (UnsupportedJwtException e) {
            log.error("[JWT] Unsupported JWT token!");
            httpServletRequest.setAttribute("unsupported", "Unsupported JWT token!");
        } catch (MalformedJwtException e) {
            log.error("[JWT] Invalid JWT token!");
            httpServletRequest.setAttribute("invalid", "Invalid JWT token!");
        } catch (ExpiredJwtException e) {
            log.error("[JWT] Expired JWT token!");
            httpServletRequest.setAttribute("expired", "Expired JWT token!");
        } catch (IllegalArgumentException e) {
            log.error("[JWT] Jwt claims string is empty");
            httpServletRequest.setAttribute("illegal", "JWT claims string is empty.");
        }

        return false;
    }

    /**
     * Extract jwt from bearer string.
     *
     * @param bearer String
     * @return String value of bearer token or null
     */
    public String extractJwtFromBearerString(final String bearer) {
        if (StringUtils.hasText(bearer) && bearer.startsWith(String.format("%s ", TOKEN_TYPE))) {
            return bearer.substring(TOKEN_TYPE.length() + 1);
        }

        return null;
    }

    /**
     * Extract jwt from request.
     *
     * @param request HttpServletRequest object to get Authorization header
     * @return String value of bearer token or null
     */
    public String extractJwtFromRequest(final HttpServletRequest request) {
        return extractJwtFromBearerString(request.getHeader(TOKEN_HEADER));
    }

    /**
     * Parsing token.
     *
     * @param token String jwt token to parse
     * @return Jws object
     */
    private Jws<Claims> parseToken(final String token) {
    	log.info("##### Token: {}", token);
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
    }

    /**
     * Check token is expired or not.
     *
     * @param token String jwt token to get expiration date
     * @return True or False
     */
    private boolean isTokenExpired(final String token) {
        return parseToken(token).getBody().getExpiration().before(new Date());
    }

    /**
     * Get expire date.
     *
     * @return Date object
     */
    private Date getExpireDate(final Long expires) {
        return new Date(new Date().getTime() + expires);
    }

    /**
     * Get signing key.
     *
     * @return Key
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(appSecret.getBytes());
    }
}