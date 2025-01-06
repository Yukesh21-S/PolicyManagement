package com.PolicyManagement.config;

import com.PolicyManagement.service.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
        http
                .authorizeHttpRequests(authz -> authz
                        // Public access to registration, static resources, and admin login
                        .requestMatchers("/registration**", "/js/**", "/css/**", "/img/**", "/admin/login**").permitAll()
                        // Role-based access control for customer and admin endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        // Custom login pages for customers and admins
                        .loginPage("/login")  // Default login page
                        .loginProcessingUrl("/login")  // Customer login endpoint
                        .defaultSuccessUrl("/home", true)  // Redirect for customers

                        // Admin login
                        .loginPage("/admin/login")  // Admin login page
                        .loginProcessingUrl("/admin/login")  // Admin login endpoint
                        .defaultSuccessUrl("/admin/admin-home", true)  // Redirect for admins

                        .permitAll()
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)  // Invalidate session
                        .clearAuthentication(true)  // Clear authentication
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Logout URL
                        .logoutSuccessUrl("/login?logout")// Redirect after logout
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());  // Disable CSRF for simplicity (adjust as needed)

        return http.build();
    }
}
