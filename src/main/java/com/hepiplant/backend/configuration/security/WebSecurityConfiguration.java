package com.hepiplant.backend.configuration.security;

import com.hepiplant.backend.filter.JwtRequestFilter;
import com.hepiplant.backend.service.impl.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfiguration(UserDetailsServiceImpl userDetailsService, JwtRequestFilter jwtRequestFilter) {
        super();
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/authenticate", "/users").permitAll()
                .anyRequest().authenticated()
//                .mvcMatchers(HttpMethod.GET, "/api/products/{id:^[0-9]*$}", "/index",
//                        "/showGetProduct", "/getProduct", "/productDetails").hasAnyRole("USER", "ADMIN")
//                .mvcMatchers(HttpMethod.GET, "/showCreateProduct", "/createProduct", "/createResponse").hasRole("ADMIN")
//                .mvcMatchers(HttpMethod.POST, "/getProduct").hasAnyRole("USER", "ADMIN")
//                .mvcMatchers(HttpMethod.POST, "/api/products", "/saveProduct").hasRole("ADMIN")
//                .mvcMatchers("/", "/login", "/showReg", "/registerUser").permitAll()
//                .anyRequest().denyAll()
                .and().csrf().disable()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
