package com.waither.config;

import lombok.RequiredArgsConstructor;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    private final SwaggerIndexPageTransformer swaggerIndexPageTransformer;
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/swagger-ui/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
//                .resourceChain(false)
//                .addTransformer(swaggerIndexPageTransformer);
//
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/")
//                .resourceChain(false)
//                .addTransformer(swaggerIndexPageTransformer);
//    }
}
