package org.example.trainingapp.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.trainingapp.jwt.JwtTokenUtil;
import org.example.trainingapp.jwt.TokenBlacklistUtil;
import org.example.trainingapp.service.impl.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.example.trainingapp.constant.Constant.BEARER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;


@Service
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistUtil tokenBlacklistUtil;
    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class.getName());
    // check token being Base64URL (A-Z, a-z, 0-9, -, _, .)
    private static final Pattern JWT_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+$");


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromJwt(request);
            if (token != null) {

                if (!isValidJwtFormat(token)) {
                    log.warn("Rejected malformed JWT: {}", token);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT");
                    return;
                }

                log.debug("Token found");

                if (tokenBlacklistUtil.isTokenBlacklisted(token)) {                 //  if token is in the black list
                    log.warn("Token is blacklisted: {}... (len={}), user logged out",
                            token.substring(0, Math.min(10, token.length())), token.length());     // first 10 symbols of token
                    throw new SecurityException("User is logged out");
                }

                jwtTokenUtil.validateAndParseToken(token);          //  if no exception thrown go further
                String username = jwtTokenUtil.getUsernameFromToken(token);
                log.debug("Token is valid for user: {}", username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Authentication set for user: {}", username);
            }
        } catch (JWTVerificationException | SecurityException e) {
            log.warn("Authentication failed: {}", e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT: " + e.getMessage());
            return;
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth processing error");
            return;
        }
        filterChain.doFilter(request, response);
    }


    private boolean isValidJwtFormat(String token) {
        return JWT_PATTERN.matcher(token).matches();
    }


    public String getTokenFromJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(BEARER)) {
            return headerAuth.substring(7);
        }
        return null;
    }
}

