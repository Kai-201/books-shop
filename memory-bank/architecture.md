# 系统架构

## 技术栈

| 层 | 技术 |
|----|------|
| 后端框架 | Spring Boot 2.7.18 (Java 8) |
| ORM | MyBatis-Plus 3.5.5 |
| 认证 | Spring Security + JWT (jjwt 0.11.5, HS256) |
| 密码 | BCrypt |
| 缓存 | Redis (Upstash 云服务) + Guava BloomFilter |
| 搜索 | Elasticsearch 9.4.2 (RestTemplate 调 REST API) |
| 消息队列 | RabbitMQ (CloudAMQP) |
| 实时推送 | WebSocket (STOMP + SockJS) |
| 前端 | Vue3 + Element Plus + Pinia + Vite 8 |
| 数据库 | MySQL 8 |

## 中间件连接

```
项目 → Redis:      novel-anteater-121365.upstash.io:6379 (SSL)
项目 → RabbitMQ:   dog-01.lmq.cloudamqp.com:5672
项目 → ES:         localhost:9200 (HTTPS, 自签名证书)
项目 → MySQL:      localhost:3306
```

## Spring Security 过滤器链

```
请求 → SecurityContextPersistenceFilter
    → HeaderWriterFilter
    → CorsFilter (localhost:5173等白名单)
    → LogoutFilter
    → JwtAuthenticationFilter (提取 Bearer token → 解析 → 放入 SecurityContext)
    → ExceptionTranslationFilter (401/403 → JSON 响应)
    → FilterSecurityInterceptor (URL 级别权限)
    → Controller → @PreAuthorize (方法级权限)
```

## 配置类 (8个)

| 配置 | 文件 | 定制的行为 |
|------|------|-----------|
| 安全 | SecurityConfig | JWT 过滤器链 + URL 权限 + CORS + JSON 错误 |
| 缓存 | RedisCacheConfig | JSON 序列化 + 分组随机 TTL |
| 分页+锁 | MybatisPlusConfig | 分页插件 + @Version 乐观锁拦截器 |
| 消息 | RabbitMQConfig | DirectExchange + durable Queue + 绑定 |
| 搜索 | ElasticsearchConfig | RestTemplate Bean (Basic Auth + 跳过 SSL) |
| 实时 | WebSocketConfig | STOMP Broker + SockJS 端点 |
| 文档 | Knife4jConfig | Swagger 扫描 + /doc.html |
| SSL | SslUtils | 开发环境信任所有证书 |
