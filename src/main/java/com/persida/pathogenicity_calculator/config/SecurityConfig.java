package com.persida.pathogenicity_calculator.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;

@Configuration
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

    @Value("${disableCSRF}")
    private Boolean disableCSRF;

    @Value("${disableCORS}")
    private Boolean disableCORS;

    @Value("${disableFrameOptions}")
    private Boolean disableFrameOptions;

    @Value("${disableHttpStrictTransportSecurity}")
    private Boolean disableHttpStrictTransportSecurity;

    @Value("${disableXssProtection}")
    private Boolean disableXssProtection;

    @Value("${setUseHTTPOnly}")
    private Boolean setUseHTTPOnly;

    @Value("${setSecureCookie}")
    private Boolean setSecureCookie;

    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(customAuthenticationProvider);
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

            //do noe use the /pcacl in the URL definition, this is handled internally
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET,"/api/srvc").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/tokenRequest").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/classifications/variant/*").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/classifications").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/classification/*").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/diseases/*").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/modesOfInheritance").permitAll()
                    .antMatchers(HttpMethod.GET,"/api/specifications").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/classification/create").permitAll()
                    .antMatchers(HttpMethod.PUT,"/api/classification/update").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/classification/delete").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/classification/addEvidence").permitAll()
                    .antMatchers(HttpMethod.POST,"/api/classification/removeEvidence").permitAll()
                    .antMatchers(loginPage+"*").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic();

            if(disableCSRF) {
                http.csrf().disable();
            }
            if(disableCORS){
                http.cors().disable();
            }
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

    // Used by spring security if CORS is enabled
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

    @Bean
    public ServletContextInitializer servletContextInitializer() {return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
                sessionCookieConfig.setHttpOnly(setUseHTTPOnly);
                sessionCookieConfig.setSecure(setSecureCookie);
            }
        };
    }
}
