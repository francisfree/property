package com.castle.property.service;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Map;

@Service
public class HtmlToPdfTemplateService {

    private final TemplateEngine templateEngine;

    public HtmlToPdfTemplateService() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCheckExistence(true);

        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
    }

    public String render(String template, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(template, context);
    }
}