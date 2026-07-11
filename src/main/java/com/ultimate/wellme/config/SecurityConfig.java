package com.ultimate.wellme.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/**",
        "/signup",
        "/login",
        "/signUpDoctor", 
        "/features",
        "/contacts",
        "/pricing",
        "/",
        "/home",
        "/index",
        "/forgot-password",
        "/assets/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/api/webhooks/**",
        "/api/payment/**",
        "/error"
    };

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())  // Spring generates a CSRF token and
                                                                                    // stores it in a cookie named
                                                                                    // XSRF-TOKEN. It's not httpOnly
                                                                                    // (unlike your JWT cookie), because
                                                                                    // Angular's JS needs to read it and
                                                                                    // send it back in a header.
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers("/api/v1/auth/login", "/api/v1/auth/signup", "api/v1/auth/signUpDoctor")
        )
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(request -> request.requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup", "api/v1/auth/signUpDoctor").permitAll()
                                                    .requestMatchers("/api/v1/patient/**").hasAnyRole("PATIENT", "ADMIN")
                                                    .requestMatchers("/api/v1/doctor/**").hasAnyRole("DOCTOR", "ADMIN")
                                                    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public AuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();  // to interact with database
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(new BCryptPasswordEncoder(10));
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    // @Bean
    // public UserDetailsService userDetailsService() {
        
    //     UserDetails user = User.withDefaultPasswordEncoder()
    //         .username("user")
    //         .password("user")
    //         .roles("USER")
    //         .build();   

    //     UserDetails admin = User.withDefaultPasswordEncoder()
    //         .username("admin")
    //         .password("admin")
    //         .roles("ADMIN")
    //         .build();

    //     return new InMemoryUserDetailsManager(user, admin); 
    // }
    
}
