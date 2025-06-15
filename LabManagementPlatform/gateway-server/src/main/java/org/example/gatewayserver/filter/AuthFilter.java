package org.example.gatewayserver.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.core.common.BaseResponse;
import org.example.core.common.ResultUtils;
import org.example.core.domain.SysUser;
import org.example.core.util.EncodingUtils;
import org.example.core.util.JwtUtil;
import org.example.core.util.StringUtils;
import org.example.gatewayserver.properties.IgnoreWhiteProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    @Value("${auth.secret}")
    private String secret;

    @Autowired
    private IgnoreWhiteProperties ignoreWhite;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String url = request.getURI().getPath();
        // 打印请求 URL
        log.info("request url:{}", request.getURI());
        // 跳过不需要验证的路径
        if (StringUtils.matches(url, ignoreWhite.getWhites())) {
            return chain.filter(exchange);
        }
        String token = request.getHeaders().getFirst("Authorization");
        if (StringUtils.isEmpty(token)) {
            return unauthorizedResponse(exchange, "令牌不能为空");
        }

        // 验证令牌
        boolean validToken = JwtUtil.isValidToken(token, secret);
        if (!validToken) {
            return unauthorizedResponse(exchange, "令牌已过期或验证不正确！");
        }
        //解析令牌
        SysUser user = JwtUtil.parseToken(token, secret);
        if (ObjectUtils.isNotEmpty(user)) {
            // 将用户信息添加到请求头
            ServerHttpRequest newRequest = exchange.getRequest().mutate()
                    .header("userId", user.getUserId().toString())
                    .header("roleId", user.getRoleId().toString())
                    .header("account", user.getAccount())
                    .header("name", EncodingUtils.encodeChinese(user.getName()))
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String msg) {
        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        BaseResponse error = ResultUtils.error(401, msg);
        DataBuffer dataBuffer = response.bufferFactory().wrap(JSON.toJSONString(error).getBytes());
        return response.writeWith(Mono.just(dataBuffer));
    }
}
