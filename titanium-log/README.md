/**
 * 日志模块，提供统一的日志处理和分布式追踪功能
 *
 * 功能包括：
 * 1. 集成Logback日志框架
 * 2. 支持多种分布式追踪系统（Zipkin、Skywalking等）
 * 3. 自动在日志中添加TraceId
 * 4. 提供TraceId获取工具类
 * 
 * 使用方式：
 * 1. 在项目中引入此模块依赖
 * 2. 可选择引入具体的追踪实现模块：
 *    - Zipkin: implementation project(':titanium-tracing-zipkin')
 *    - Skywalking: implementation project(':titanium-tracing-skywalking')
 * 3. 无需额外配置，自动装配
 * 4. 可通过application.yml调整日志相关配置
 * 
 * 日志输出效果：
 * 当引入追踪模块后，日志将包含TraceId信息，例如：
 * ```
 * 10:30:15.123 [http-nio-8080-exec-1] INFO  [2e3a1b4c5d6e7f8a] com.example.service.UserService - User login successful
 * 10:30:15.456 [http-nio-8080-exec-2] ERROR [2e3a1b4c5d6e7f8a] com.example.service.OrderService - Order creation failed
 * ```
 * 其中 [2e3a1b4c5d6e7f8a] 就是TraceId，可用于追踪整个请求链路。
 */