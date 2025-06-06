package com.example;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<HelloFilter> helloFilterRegistrationBean() {
        FilterRegistrationBean<HelloFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new HelloFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<WorldFilter> worldFilterRegistrationBean() {

        FilterRegistrationBean<WorldFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new WorldFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2);
        return registrationBean;

    }
}
