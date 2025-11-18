package com.ultimate.wellme.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ultimate.wellme.models.User;
import com.ultimate.wellme.services.AppService;
import com.ultimate.wellme.services.DoctorService;
import com.ultimate.wellme.services.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtService jwtService;

    @Autowired
    ApplicationContext context;

    @Autowired
    private DoctorService doctorService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/signup") || path.equals("/signUpDoctor");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

                System.out.println("Inside JwtFilter");
                // String authHeader = request.getHeader("Authorization");
                String token=null, userName_id=null;

                if(request.getCookies() != null) {
                    for(Cookie cookie : request.getCookies()) {
                        if("jwt".equals(cookie.getName())) {
                            token = cookie.getValue();
                        }
                    }
                }

                System.out.println("Token in JwtFilter is: " + token);

                if(token != null) {

                    try {
                        userName_id = jwtService.extractUserName(token);
                    } catch (ExpiredJwtException e) {
                        System.out.println("JWT token has expired, proceeding without authentication");

                        // Clear the expired cookie
                        Cookie expiredCookie = new Cookie("jwt", null);
                        expiredCookie.setMaxAge(0);
                        expiredCookie.setPath("/");
                        response.addCookie(expiredCookie);
                    }

                } else {
                    System.out.println("token is null in JwtFilter...");
                }


                System.out.println("userName_id in jwtFilter is: " + userName_id);

                if(userName_id != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // Long userId = Long.parseLong(userName_id);
                    // Optional<User> optionalUser = doctorService.getDoctorById(userId);
                    // System.out.println(optionalUser.get().getEmail());
                    UserDetails userDetails = context.getBean(AppService.class).loadUserByUsername(userName_id);

                    System.out.println("UserDetails loaded successfully ✅");

                    try {
                        if(jwtService.validateToken(token, userDetails)) {
                            
                            System.out.println("Token validation successful");

                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    
                            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));   
                            /*
                             * it adds metadata about the request environment to the authentication object,
                             * making the entire security process more robust and auditable
                             * 
                             * It extracts and attaches details like the remote IP address of the client
                             * 
                             * 
                             * Real-World Use Case: Failed Login Attempt Tracking
                             */
    
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        } 
                    } catch (ExpiredJwtException e) {
                        System.out.println("Token validation failed-expired " + e.getMessage());
                    }
                }

                filterChain.doFilter(request, response);
    }
    
}
