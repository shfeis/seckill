package com.hspedu.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * 这是解析器、拦截器等的配置类，如果没有该配置类那么解析器等就不会自动执行
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private UserArgumentResolver userArgumentResolver;
    @Resource
    private AccessLimitInterceptor accessLimitInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    //将解析器添加到HandlerMethodArgumentResolver列表中，只有这样解析器才能工作
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    //将拦截器添加到HandlerMethodArgumentResolver列表中，只有这样拦截器才能工作
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLimitInterceptor);
    }
}
