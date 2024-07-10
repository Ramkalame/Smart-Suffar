//package com.rido.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public static PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//    
////    @Bean
////    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http.csrf().disable()
////            .authorizeRequests().requestMatchers("/user/**","/admin/**", "/h2-console/**", "/driver/**", "/locationapi/**", "/contactUs/**", "/api/feedback/**" ,"/history/**", "/api/orders/**" ,"/swagger-ui/**" ,"/subadmin/**").permitAll()
////                .anyRequest().authenticated();
////
////        http.headers().frameOptions().disable();
////        return http.build();
////    }
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .authorizeRequests().requestMatchers("/user/**","/admin/**", "/h2-console/", "/driver/**","/hub/**", "/locationapi/**", "/localhost/**","/images/**").permitAll()
//            .requestMatchers(AUTH_WHITELIST).permitAll()
//                .anyRequest().authenticated();
//
//        http.headers().frameOptions().disable();
//        return http.build();
//    }
//
//
//
//    private static final String[] AUTH_WHITELIST= {
//    		"api/v1/auth/**",
//    		"v3/api-docs/**",
//    		"v3/api-docs yaml",
//    		"swagger-ui/**",
//    		"swagger-ui.html"
//    };
//    
//    
//}
//
//
