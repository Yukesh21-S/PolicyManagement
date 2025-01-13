package com.PolicyManagement.config;

import com.PolicyManagement.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomerService customerService;

    public SecurityConfiguration(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    // Bean for password encoding
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // Bean for authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customerService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    // Bean for security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Public access to registration, static resources, and login pages
                        .requestMatchers("/registration/**", "/js/**", "/css/**", "/img/**", "/home").permitAll()
                        // Role-based access control for customer and admin endpoints
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Shared login processing URL
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecretKey")  // Key for token generation (secure and unique)
                        .tokenValiditySeconds(1209600)  // 2 weeks (time in seconds)
                        .userDetailsService(customerService)  // Specify the UserDetailsService
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)  // Invalidate session
                        .clearAuthentication(true)  // Clear authentication
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Logout URL
                        .logoutSuccessUrl("/login?logout")  // Redirect after logout
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler();
    }

    private static class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"));
            logger.info("Is admin: " + isAdmin);
            logger.info("Is admin: " + authentication.getAuthorities());

            if (isAdmin) {
                setDefaultTargetUrl("/customer/home");
            } else {
                setDefaultTargetUrl("/admin/admin-home");
            }
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
