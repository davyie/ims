package com.example;

import jakarta.servlet.*;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class HelloFilter implements Filter {

    @Autowired
    private Logger logger = LoggerFactory.getLogger(HelloFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        logger.info("In Filter request....{}", httpRequest.getRequestURI());
        logger.info("In Filter request....{}", httpRequest.getRemoteAddr());
        filterChain.doFilter(servletRequest, servletResponse);
        logger.info("After doFilter...");
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}