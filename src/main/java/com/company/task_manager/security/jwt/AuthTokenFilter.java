package com.company.task_manager.security.jwt;

import com.company.task_manager.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // --- START DEBUGGING ---
        System.out.println("\n====== AuthTokenFilter is running for request: " + request.getRequestURI() + " ======");
        String jwt = parseJwt(request);
        System.out.println("Parsed JWT from request: " + jwt);
        // --- END DEBUGGING ---

        try {
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // --- MORE DEBUGGING ---
                System.out.println("JWT validation successful.");
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                System.out.println("Username extracted from token: " + username);
                // --- END DEBUGGING ---

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                // --- MORE DEBUGGING ---
                System.out.println("User '" + username + "' has been authenticated and set in SecurityContext.");
                // --- END DEBUGGING ---
            } else {
                // --- MORE DEBUGGING ---
                System.out.println("JWT is either null or invalid.");
                // --- END DEBUGGING ---
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
        System.out.println("====== AuthTokenFilter finished processing. ======\n");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        // --- DEBUGGING ---
        System.out.println("Authorization Header found: " + headerAuth);
        // --- DEBUGGING ---

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}