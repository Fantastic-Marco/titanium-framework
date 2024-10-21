# Titanium-Feign

## 简介

Titanium-Feign 是基于 OpenFeign 实现的微服务调用客户端。

## 功能列表

- 统一日志
- 用户信息透传

## 用户信息透传

用户信息透传，需要使用 `TitaniumFeignInterceptor` 拦截器。  
引入当前模块默认是开启的，需要关闭，请在配置文件中添加如下配置

```yaml
titanium:
  feign:
    enable: false
```

