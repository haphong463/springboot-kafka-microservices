package net.javaguides.order_service.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add the JWT token from cookie to the request headers
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String cookie = request.getHeader("Cookie");
            if (cookie != null) {
                requestTemplate.header("Cookie", cookie);
            }
        };
    }
}
