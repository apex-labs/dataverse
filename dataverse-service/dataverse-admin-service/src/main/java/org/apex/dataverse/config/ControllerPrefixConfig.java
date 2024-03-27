//package org.apex.dataverse.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cloud.context.config.annotation.RefreshScope;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//@RefreshScope
//public class ControllerPrefixConfig implements WebMvcConfigurer {
//
//    @Value("${global.prefix}")
//    private String prefix;
//
//    @Override
//    public void configurePathMatch(PathMatchConfigurer configurer) {
//        configurer.addPathPrefix(prefix, c -> c.isAnnotationPresent(RestController.class));
//    }
//
//
//}
