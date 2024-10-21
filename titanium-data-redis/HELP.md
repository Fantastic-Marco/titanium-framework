# Titanium-Data-Redis

## 简介
Titanium-Data-Redis 是基于 SpringBoot 整合 Redis 的一个框架，主要实现 Redis 的使用。
### 特性
- 集成 spring-data-redis
- 集成 redisson
- 实现 Mybatis 二级缓存
- 实现分布式锁

## 准备
在使用该模块之前，需要先做以下2步准备，以确保下面的功能正常使用
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

## 快速开始
### Mybatis 二级缓存实现

1. 程序入口添加注解
```java
@SpringBootApplication
@EnableCaching
public class TitaniumDataRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(TitaniumDataRedisApplication.class, args);
    }

}
```
2.Mapper 添加注解
```java
@CacheNamespace(implementation= MybatisRedisCache.class,eviction=MybatisRedisCache.class)
public interface WeChatUserListMapper extends BaseMapper<WeChatUser> {
    
}
```


### 分布式锁
当业务调用接口或者某些方法的时候，我们希望在方法执行之前，先获取一个锁，当业务执行完毕后，释放锁。
如果通过编码的方式来做的会，会出现很多雷同的逻辑，所以这里使用注解的方式来实现。  
1.这是一个增加全局锁的注解使用方式
```java
@PostMapping("/create")
@DistributionLock(value = "user-sso", spels = "#req.mobile",waitTime = 3,timeUnit = TimeUnit.SECONDS)
public void create(@RequestBody @Validated UserInnerCreateReq req) {
    log.info("create user: {}", Json.serialize(req));
    userService.create(req);
}
```
2.属性解释
- value：锁的名称，如果存在spels表达式的时候，就会变成锁key的前缀，和spels获取的值通过 ：连接起来，如果spels不存在，则直接使用value作为锁key。
- spels：SpEL表达式，支持获取方法参数内的值作为锁key的一部分。
- waitTime：等待时间，当获取锁失败后，会等待一段时间，如果还是获取不到锁，则直接返回。
- timeUnit：等待时间单位


