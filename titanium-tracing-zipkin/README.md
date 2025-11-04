# Zipkin分布式追踪实现模块

## 功能包括：
1. 集成Zipkin分布式追踪
2. 自动与[titanium-tracing](file:///C:/Users/85370/Documents/idea-project/titanium-framework/titanium-tracing/src/main/java/com/titanium/tracing/config/TracingConfiguration.java)基础模块集成
3. 自动与日志模块集成，在日志中添加TraceId

## 使用方式：
1. 在项目中引入此模块依赖
   ```gradle
   implementation project(':titanium-tracing-zipkin')
   ```
2. 在 application.yml 中配置 Zipkin 服务器地址：
   ```yaml
   spring:
     zipkin:
       base-url: http://localhost:9411  # Zipkin 服务器地址
       enabled: true
     sleuth:
       sampler:
         probability: 1.0  # 采样率，1.0 表示 100% 采样
   ```
3. 启动 Zipkin 服务器（可使用 Docker）：
   ```bash
   docker run -d -p 9411:9411 openzipkin/zipkin
   ```
4. 启动应用并发起请求，即可在 Zipkin UI (http://localhost:9411) 中查看追踪信息

## 日志输出效果：
连接 Zipkin 后，应用的日志将包含 TraceId 信息，例如：
```
10:30:15.123 [http-nio-8080-exec-1] INFO  [2e3a1b4c5d6e7f8a] com.example.service.UserService - User login successful
10:30:15.456 [http-nio-8080-exec-2] ERROR [2e3a1b4c5d6e7f8a] com.example.service.OrderService - Order creation failed
```
其中 [2e3a1b4c5d6e7f8a] 就是 TraceId，可用于在 Zipkin 中追踪整个请求链路。