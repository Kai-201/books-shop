package com.bookshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

/**
 * ES 配置 —— 用 RestTemplate 调 ES REST API。
 *
 * <p>不依赖 elasticsearch-java 客户端，避免 ES 9.x / HttpClient 5 的版本冲突。
 * ES 的 REST API 就是 HTTP + JSON，RestTemplate 够用。</p>
 */
@Configuration
public class ElasticsearchConfig {

    public static final String ES_URL = "https://localhost:9200";

    @Bean
    public RestTemplate esRestTemplate() {
        RestTemplate template = new RestTemplate();
        // 跳过 SSL 证书验证（仅本地开发）
        template.setRequestFactory(new SslUtils.TrustAllRequestFactory());
        // 添加 Basic Auth 拦截器
        String auth = "elastic:2qYaJiM24TB+W1zH3*Rg";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        template.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return execution.execute(request, body);
        });
        return template;
    }
}
