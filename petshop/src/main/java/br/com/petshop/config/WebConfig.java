package br.com.petshop.config;

import br.com.petshop.security.SessaoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessaoInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/setup", "/logout", "/js/**", "/error");
    }
}
