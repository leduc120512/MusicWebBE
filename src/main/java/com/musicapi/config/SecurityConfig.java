package com.musicapi.config;

import com.musicapi.security.CustomUserDetailsService;
import com.musicapi.security.JwtAuthenticationEntryPoint;
import com.musicapi.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("*"));
        cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        cors.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // static
                        .requestMatchers(
                                "/upload/**",
                                "/favicon.ico",
                                "/assets/**", "/static/**", "/resources/**",
                                "/css/**", "/js/**", "/images/**", "/webjars/**"
                        ).permitAll()

                        // public APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test", "/api/health").permitAll()
                        .requestMatchers("/api/songs/public/**").permitAll()

                        // allow public read access to songs, artists, and comments
                        .requestMatchers(HttpMethod.GET, "/api/songs/me", "/api/songs/my").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/songs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/songs/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/songs/by-album/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/artists/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/song/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/comments/*/replies").permitAll()

                        // public song update endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/songs/*").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/songs/*").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/songs/*/lyrics").permitAll()

                        // protect song create, delete and moderation paths
                        .requestMatchers(HttpMethod.POST, "/api/songs/*").hasAnyRole("AUTHOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/songs/*").hasAnyRole("AUTHOR", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/songs/latest", "/api/songs/popular").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/banners", "/api/banners/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/popup-ads/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/popup-ads/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/popup-ads/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/popup-ads/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/popup-ads/**").hasRole("ADMIN")
                        .requestMatchers("/api/favorite-albums/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/albums/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/likes/song/*/count").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/banners").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/banners/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/banners/*").hasRole("ADMIN")

                        // 🎧 genres (👇 THÊM PHẦN NÀY)
                        .requestMatchers(HttpMethod.GET, "/api/genres/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/genres/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/genres/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/genres/**").hasRole("ADMIN")

                        // role-based
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/author/**").hasAnyRole("AUTHOR", "ADMIN")

                        .anyRequest().authenticated()
                )
;

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
