package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Value("${login.enabled}")
    private Boolean loginEnabled;

    @Value("${navigation.startPage}")
    private String startPage;

    @Value("${navigation.indexPage}")
    private String indexPage;

    @Value("${navigation.adminPage}")
    private String adminPage;

    @Value("${navigation.loginPage}")
    private String loginPage;

    @Value("${disableFrameOptions}")
    private Boolean  disableFrameOptions;

    @Value("${disableHttpStrictTransportSecurity}")
    private Boolean  disableHttpStrictTransportSecurity;

    @Value("${disableXssProtection}")
    private Boolean  disableXssProtection;

    @Bean
    public UserDetailsService userDetailsService() { return new UserDetailsServiceImpl(); }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    Note that Spring security requires the column names must be: username, password, enabled and role.
    Both users are enabled, the passwords are encoded in BCrypt format.
    */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /* Secure the endpoints with HTTP Basic authentication
    The configure(HttpSecurity) method defines which URL paths should be secured and which should not.
    */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (loginEnabled == true) {

            http.formLogin()
                    .loginPage(loginPage)
                    .defaultSuccessUrl(indexPage, true);

            http.logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl(startPage)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID");

            http.sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                    .maximumSessions(2)
                    .expiredUrl(loginPage);

            http.authorizeRequests()
                    .antMatchers(HttpMethod.POST,"/rest/userTokens/authenticateAndProvideToken").permitAll()
                    .antMatchers("/login*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();

            http.addFilterAfter(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

            http.cors();
            http.csrf().disable();

            if(disableFrameOptions){
                http.headers().frameOptions().disable();
            }
            if(disableHttpStrictTransportSecurity){
                http.headers().httpStrictTransportSecurity().disable();
            }
            if(disableXssProtection){
                http.headers().xssProtection().disable();
            }
        } else {
            http.csrf().disable().authorizeRequests().anyRequest().anonymous().and().httpBasic().disable();
        }
    }

    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    //handles users to try to authenticate the second time but did not terminate the previous session
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Override @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
