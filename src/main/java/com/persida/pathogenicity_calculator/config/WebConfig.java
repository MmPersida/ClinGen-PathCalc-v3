package com.persida.pathogenicity_calculator.config;

import com.persida.pathogenicity_calculator.utils.constants.Constants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import springfox.documentation.swagger2.mappers.ModelMapper;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static Logger logger = Logger.getLogger(WebConfig.class);

    private boolean templatesCacheable = true;

    @Value("${navigation.startPage}")
    private String startPage;

    @Value("${navigation.indexPage}")
    private String indexPage;

    @Value("${navigation.loginPage}")
    private String loginPage;

    @Value("${navigation.errorPage}")
    private String errorPage;

    @Value("${navigation.adminPage}")
    private String adminPage;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(loginPage).setViewName(Constants.LOGIN);
        registry.addViewController(startPage).setViewName(Constants.INDEX);
        registry.addViewController(indexPage).setViewName(Constants.INDEX);
        //registry.addViewController(adminPage).setViewName(Constants.ADMIN);
        registry.addViewController(errorPage).setViewName(Constants.ERROR);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    @Description("Thymeleaf view resolver")
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(springTemplateEngine());
        viewResolver.setCharacterEncoding(Constants.UTF8);
        return viewResolver;
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.setTemplateResolver(springTemplateResolver());
        return springTemplateEngine;
    }

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ITemplateResolver springTemplateResolver() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(Constants.UTF8);
        resolver.setCacheable(templatesCacheable);
        return resolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("/", "classpath:/");
    }
}
