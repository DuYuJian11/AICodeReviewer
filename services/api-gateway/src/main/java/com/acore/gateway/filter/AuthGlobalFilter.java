package com.acore.gateway.filter;

import com.acore.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * 全局 JWT 鉴权过滤器
 *
 * <p>对白名单路径放行，其余请求校验 JWT 令牌。</p>
 *
 * @author acore
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtProperties jwtProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 白名单路径直接放行
        if (isWhitelist(path)) {
            return chain.filter(exchange);
        }

        // 从请求头获取 Token
        String token = extractToken(exchange.getRequest());
        if (token == null) {
            log.warn("请求缺少 Token: {}", path);
            return unauthorized(exchange, "缺少认证令牌");
        }

        // 校验 Token
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                    jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // 将用户信息传递到下游服务
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Name", String.valueOf(claims.get("username", "")))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (JwtException e) {
            log.warn("Token 校验失败: {}", e.getMessage());
            return unauthorized(exchange, "令牌无效或已过期");
        }
    }

    @Override
    public int getOrder() {
        return -100; // 高优先级
    }

    /**
     * 判断是否为白名单路径
     */
    private boolean isWhitelist(String path) {
        return jwtProperties.getWhitelist().stream()
                .anyMatch(pattern -> {
                    // 支持 Ant 风格通配，简单处理：移除末尾 ** 并进行前缀匹配
                    String prefix = pattern.replace("/**", "");
                    return path.startsWith(prefix);
                });
    }

    /**
     * 从请求头提取 Token
     */
    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 返回 401 未授权
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"code\":401,\"message\":\"%s\"}", message);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8))));
    }
}