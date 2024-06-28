# titanium-data-mybatis-plus

* Mybatis-Plus: 版本 3.5.7
* p6spy-spring-boot-starter: 1.9.1
* HikariCP: 5.1.0

当前组件致力于提供Mybatis-Plus集成功能，方便引用该组件的业务服务可以短平快地使用以下功能

- <a href="#auto-fill">字段填充器</a>
- <a href="#logic-delete">逻辑删除支持</a>
- <a href="#tenant">租户功能配置化集成</a>
- <a href="#encrypt"> 数据加密持久化 </a>
- <a href="#optimistic-locker">乐观锁</a>
- <a href="#batch-insert">自定义批量插入</a>
- 数据权限支持
- 分页支持
- <a href="#block-attack">防止全表删除/更新</a>
- <a href="#log-print">SQL打印与分析</a>
- 代码生成器
- <a href="#connection-pool">连接池默认配置</a>
- <a href="#id-generator">自定义Id生成器</a>

## 最佳实践

### <p id="auto-fill">字段填充器</p>

字段填充器，可以自动填充一些字段，比如创建时间、更新时间,是否删除等。  
**填充范围：**

- 插入
    - 创建时间
    - 创建人Id
    - 创建人名称
    - 是否删除
- 更新
    - 更新时间
    - 更新人Id
    - 更新人名称

**遵循以下规则**

1. Entity类继承自`NamedBaseEntity` 或者 `BaseEntity`

```java
public class User extends NamedBaseEntity {

}
```

2. 往 `UserContextHolder` 提供用户信息，如果你的业务服务集成了 `titanium-web-starter` 组件，那么可以自动注入  
   如果没有集成 `titanium-web-starter` 组件，那么需要手动注入，例如自己实现一个拦截器，在拦截器中注入用户信息

### <p id="logic-delete">开启逻辑删除</p>

建议配置到统一的配置文件中，不要在应用配置

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除字段名
      logic-delete-value: 1 # 逻辑已删除值
      logic-not-delete-value: 0 # 逻辑未删除值
```

### <p id="tenant">租户功能配置化集成</p>

租户功能，可以自动过滤掉租户相关的数据。  
当前的租户拦截器已经配合`titanium-web-starter` 组件，可以自动注入租户信息。  
如果业务服务没有集成`titanium-web-starter` 组件，那么需要手动注入租户信息。  
**使用方式**

1. 配置租户拦截器，在`application.yml`中添加以下配置

```yaml
titanium:
  data:
    mybatis-plus:
      tenant:
        enabled: true # 是否开启租户功能，默认关闭
        tables: # 需要过滤租户信息的表，支持多表
          - table1
          - table2
        column: tenant_id # 租户字段名，默认tenant_id 
```

2. 自定义路径拦截器,配置默认的拦截器类 `TenantHandlerInterceptor`

```java

@Bean
TitaniumWebCustomizer webCustomizer() {
    return customizer -> {
        customizer.setWebInterceptor(TitaniumWebProperties.tenantInterceptor.builder()
                .enable(true)
                .interceptor(TenantHandlerInterceptor.class)
                .includePatterns(List.of("/**"))
                .excludePatterns(List.of("/test/**"))
                .order(Integer.MAX_VALUE)
                .build());
    };
}
```

### <p id="encrypt"> 数据加密持久化</p>

有时候，有些业务信息具有敏感性，需要加密存储到数据库中。  
本组件主要提供了加密持久化到数据库（默认使用`AES`加解密。），并且查询的时候自动解密的功能。  
对于被加密的字段，需要事先预留好足够的长度，否则会报错。

1. 开启加密持久化功能，需要配置以下信息

```yaml
# 加密持久化配置
titanium:
  data:
    mybatis-plus:
      encrypt:
        enabled: true # 是否开启加密持久化功能，默认关闭
        key: 1234567890 # 加密密钥，默认1234567890
        algorithm: AES # 加密算法，默认AES
```

2.在Entity类中添加`@Encrypt`注解，表示该字段需要加密持久化  
例如下列例子中的用户密码字段

```java
public class User extends NamedBaseEntity {
    /**
     * 用户id
     */
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    @Encrypt
    @TableField("password")
    private String password;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("mobile")
    private String mobile;
}
```

### <p id = "optimistic-locker">乐观锁</p>

本组件已经默认开启乐观锁功能  
默认关闭，需要手动开启

**注意事项**

- 支持的数据类型包括：int, Integer, long, Long, Date, Timestamp, LocalDateTime。
- 对于整数类型，newVersion 是 oldVersion + 1。
- newVersion 会自动回写到实体对象中。
- 仅支持 updateById(id) 和 update(entity, wrapper) 方法。在 update(entity, wrapper) 方法中，wrapper 不能复用。

1. 开启乐观锁功能，需要配置以下信息

```yaml
# 乐观锁配置
titanium:
  data:
    mybatis-plus:
      optimistic-locker-enabled: true # 是否开启乐观锁功能，默认关闭
```

2. 在Entity类中添加`@Version`注解，表示该字段为乐观锁版本号

```java
import com.baomidou.mybatisplus.annotation.Version;

public class YourEntity {
    @Version
    private Integer version;
    // 其他字段...
}
```

### <p id = "batch-insert">自定义批量插入</p>

本组件已经默认开启批量插入功能，不需要额外配置,按照下面的规范即可调用 insertBatchSomeColumn 方法使用批量插入  
本批量插入方法和Mybatis-Plus中的saveBatch最终的效果是一样的，但是saveBatch是逐条插入，效率较低，本组件的批量插入方法效率较高  
但是本 insertBatchSomeColumn 方法使用的时候具有局限性

```text
1. 仅支持Mysql数据库
2. 不提供安全的批量插入控制，如果插入的数据量过大，可能会造成数据库插入语句过长，导致插入失败
3. 对于数据库的默认字段支持不友好，会忽略数据库默认值，插入数据时，数据库字段值将为空
```

在代码实现上，需要遵循以下规范

1. Mapper需要 继承`TitaniumMapper<T>`接口

```java

@Mapper
public interface UserMapper extends TitaniumMapper<User> {

}
```

2. Repository 实现类需要继承`TitaniumRepository<M,T>`接口

```java

@Slf4j
@Repository
public class UserRepository extends TitaniumRepository<UserMapper, User> {

}
```

3. 调用insertBatchSomeColumn方法，传入实体类集合即可使用批量插入功能

```java
userRepository.insertBatchSomeColumn(userList);
```

### <p id ="block-attack"> 防止全表删除/更新</p>

本组件提供了防止全表删除/更新的功能，可以防止误操作。  
默认开启，需要时可以手动关闭。

```yaml
titanium:
  data:
    mybatis-plus:
      block-attack-enabled: true
```

### <p id="log-print">SQL打印与分析</p>

本组件提供SQL打印与分析功能，可以方便的查看执行的SQL语句。  
只需要配置一下即可开启SQL打印与分析功能  
默认为关闭状态，需要手动开启，生产环境不建议开启

```yaml
titanium:
  data:
    mybatis-plus:
      sql-log-enabled: true
```

### <p id="connection-pool">连接池默认配置</p>

本组件默认配置了连接池，可以减少数据库连接的开销。  
默认使用HikariDataSource连接池

### <p id="id-generator">自定义Id生成器</p>

内置了雪花算法Id生成器，如果需要自定义Id生成器Bean  
业务系统使用自动生成或者手动生成Id格式保持一致  
默认自动生成雪花算法Id  
手动生成只需要在业务系统中注入`IdGenerator`接口的实现类即可

### <p id="code-generator">代码生成器</p>
代码生成并非改组件提供的功能，当前能为使用者提供的是代码生成器模板，可以基于该模板进行二次开发。  
file:titanium-data-mybatis-plus/src/main/resources/templates/generator.java  
