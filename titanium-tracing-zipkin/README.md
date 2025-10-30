/**
 * Zipkin分布式追踪实现模块
 *
 * 功能包括：
 * 1. 集成Zipkin分布式追踪
 * 2. 自动与[titanium-tracing](file:///C:/Users/85370/Documents/idea-project/titanium-framework/titanium-tracing/src/main/java/com/titanium/tracing/config/TracingConfiguration.java)基础模块集成
 * 3. 自动与日志模块集成，在日志中添加TraceId
 * 
 * 使用方式：
 * 1. 在项目中引入此模块依赖
 * 2. 无需额外配置，自动装配
 * 3. 可通过application.properties调整zipkin相关配置
 */