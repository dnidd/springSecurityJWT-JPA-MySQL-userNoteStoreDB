package com.snidd.note_store.config;

import com.snidd.note_store.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This filer is telling the UsernamePasswordAuthenticationFilter
 * that the request is already authenticated.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    /**
     *
     * @param request HttpServletRequest request.
     * @param response HttpServletResponse response.
     * @param filterChain filterChain
     * @throws ServletException e.
     * @throws IOException e.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //  If user is authenticated & valid check if request is
        //  within expiration time - token should not be expired.

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // if no "Authorization" Header -> do next filter.
            filterChain.doFilter(request, response);
            return;
        }

        // Fetch token from bearer in request Header.
        final String jwtToken = authHeader.substring(7);


        // Fetch subject (user) from payload in token.
        final String username = jwtService.extractUsername(jwtToken);

        // Get authentication object (security context) from SecurityContextHolder.
        // Get current user's authentication information:
        //  *  getPrincipal(): who the user is, for example a UserDetails object.
        //  *  getCredentials()	user's credentials, for example password or token.
        //  *  getAuthorities()	user's rolls & authorities.
        //  *  isAuthenticated() if the user is considered authenticated.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If token has username && user NOT authenticated -> authenticate user:
        if(username != null && authentication == null) {
            // Authenticate user

            // Fetch user from DB:
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Check if fetched userDetails matches user details in token.
            // If token is valid -> generate UsernamePasswordAuthenticationToken
            // used on the next filter.
            if(jwtService.isTokenValid(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // Essentially setting the Session id:
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                // Set SecurityContextHolder to hold the authenticationToken.
                SecurityContextHolder.getContext()
                        .setAuthentication(authenticationToken);
            }
        }
        // Call the next filer. (if username not found & no authorization -> call next filter.)
        filterChain.doFilter(request, response);
    }

}
