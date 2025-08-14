package com.castle.property;

import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.webapp.FacesServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.EnumSet;

@SpringBootApplication
public class PropertyApplication {

    final String homePage = "/index.xhtml";
    final String errorPage = "/error/error.html";
    final String error404Page = "/error/error404.jsp";
    final String error400Page = "/error/error400.jsp";
    final String error401Page = "/error/notAuthorized.xhtml";
    final String expiredPage = "/error/viewExpired.xhtml";

//    @Value("${sso.jwks.url}")
//    private String jwkUrl;

    public static void main(String[] args) {
        SpringApplication.run(PropertyApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<FacesServlet> facesServletRegistration() {
        ServletRegistrationBean<FacesServlet> registration = new ServletRegistrationBean<>(new FacesServlet(), "*.xhtml");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    public ServletContextInitializer initializer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.setInitParameter("jakarta.faces.FACELETS_SKIP_COMMENTS", "true");
                servletContext.setInitParameter("com.sun.faces.expressionFactory", "com.sun.el.ExpressionFactoryImpl");
                servletContext.setInitParameter("primefaces.UPLOADER", "native");

                servletContext.setInitParameter("jakarta.faces.AUTOMATIC_EXTENSIONLESS_MAPPING", "true");
                servletContext.setInitParameter("jakarta.faces.PROJECT_STAGE", "Development");
                servletContext.setInitParameter("jakarta.faces.FACELETS_REFRESH_PERIOD", "1");
                servletContext.setInitParameter("jakarta.faces.validate.EMPTY_FIELDS", "true");
                servletContext.setInitParameter("jakarta.faces.INTERPRET_EMPTY_STRING_SUBMITTED_VALUES_AS_NULL", "true");
                servletContext.setInitParameter("primefaces.THEME", "saga");
                servletContext.setInitParameter("primefaces.FONT_AWESOME", "true");
                servletContext.setSessionTimeout(60);
                servletContext.setSessionTrackingModes(EnumSet.of(SessionTrackingMode.COOKIE));

            }
        };
    }


    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return new ErrorPageRegistrar() {
            @Override
            public void registerErrorPages(ErrorPageRegistry registry) {
                registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, error404Page));
                registry.addErrorPages(new ErrorPage(ViewExpiredException.class, expiredPage));
                registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, errorPage));
                registry.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, error400Page));
                registry.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, error401Page));
            }
        };
    }

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        JwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();
//
//        return new JwtDecoder() {
//            @Override
//            public Jwt decode(String token) throws JwtException {
//                return jwtDecoder.decode(token);
//            }
//        };
//    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

}
