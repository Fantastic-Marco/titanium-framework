/**
 * 日志模块，提供统一的日志处理和分布式追踪功能
 *
 * 功能包括：
 * 1. 集成Logback日志框架
 * 2. 集成Zipkin分布式追踪
 * 3. 自动在日志中添加TraceId
 * 4. 提供TraceId获取工具类
 * 
 * 使用方式：
 * 1. 在项目中引入此模块依赖
 * 2. 无需额外配置，自动装配
 * 3. 可通过application.properties调整zipkin相关配置
 */