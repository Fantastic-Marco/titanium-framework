# Titanium-Data-Redis

## 简介
Titanium-Data-Redis 是基于 SpringBoot 整合 Redis 的一个框架，主要实现 Redis 的使用。
### 特性
- 集成 spring-data-redis
- 集成 redisson
- 实现 Mybatis 二级缓存
- 实现分布式锁


## 快速开始
### Mybatis 二级缓存实现
1. 添加依赖
```groovy
implementation 'com.github.titanium-framework:titanium-data-redis'
```
2. 添加 reids 配置
```yaml
#配置redis
  spring:
    redis:
      host: 127.0.0.1
      port: 6379
      database: 0
```
3. 程序入口添加注解
```java
@SpringBootApplication
@EnableCaching
public class TitaniumDataRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TitaniumDataRedisApplication.class, args);
    }

}
```
4.Mapper 添加注解
```java
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface WeChatUserListMapper extends BaseMapper<WeChatUser> {
    
}
```


### 分布式锁
