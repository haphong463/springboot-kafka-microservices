package net.javaguides.api_gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.javaguides.api_gateway.util.JwtUtil;
import net.javaguides.common_lib.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class RoleBasedAccessFilter extends AbstractGatewayFilterFactory<RoleBasedAccessFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(RoleBasedAccessFilter.class);
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;  // Inject ObjectMapper here

    public RoleBasedAccessFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Kiểm tra xem phương thức HTTP có nằm trong danh sách được bảo vệ không
            // Trích xuất token từ cookie
            String token = extractTokenFromCookies(request);

            if (token == null) {
                return onError(exchange, "Missing or invalid token", HttpStatus.UNAUTHORIZED);
            }

            try {
                // Xác thực token
                jwtUtil.validateToken(token);

                // Trích xuất vai trò từ token
                List<String> roles = jwtUtil.extractRoles(token);
                List<String> permissions = jwtUtil.extractPermissions(token);
                // Kiểm tra vai trò của người dùng
                boolean hasRole = roles.stream().anyMatch(config.getRequiredRoles()::contains);
                // Kiểm tra vai trò của người dùng
                boolean hasPermission = permissions.stream().anyMatch(config.getRequiredPermissions()::contains);
                if (!hasRole) {
                    return onError(exchange, "Forbidden access", HttpStatus.FORBIDDEN);
                }

                if(config.getRequiredPermissions().size() == 0){
                    return chain.filter(exchange);
                }

                if(!hasPermission){
                    return onError(exchange, "You don't have permission to do this.", HttpStatus.UNAUTHORIZED);
                }

            } catch (Exception e) {
                log.error(e.getMessage());
                return onError(exchange, "Unauthorized access", HttpStatus.UNAUTHORIZED);
            }


            return chain.filter(exchange);
        };
    }

    private String extractTokenFromCookies(ServerHttpRequest request) {
        return request.getCookies().getFirst("token") != null ?
                request.getCookies().getFirst("token").getValue() : null; // Lấy giá trị của cookie "token"
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponse<String> apiResponse = new ApiResponse<>(errorMessage, httpStatus.value());
        try {
            byte[] bytes = objectMapper.writeValueAsString(apiResponse).getBytes(StandardCharsets.UTF_8);
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                    .bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    public static class Config {
        private List<String> requiredPermissions;
        private List<String> requiredRoles;
        private List<String> methods;

        public List<String> getMethods() {
            return methods;
        }

        public void setMethods(List<String> methods) {
            this.methods = methods;
        }

        public List<String> getRequiredPermissions() {
            return requiredPermissions != null ? requiredPermissions : Collections.emptyList();
        }

        public void setRequiredPermissions(List<String> requiredPermissions) {
            this.requiredPermissions = requiredPermissions;
        }

        public List<String> getRequiredRoles() {
            return requiredRoles;
        }

        public void setRequiredRoles(List<String> requiredRoles) {
            this.requiredRoles = requiredRoles;
        }
    }
}
