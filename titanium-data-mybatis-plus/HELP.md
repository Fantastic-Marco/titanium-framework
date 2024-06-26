# titanium-data-mybatis-plus

* Mybatis-Plus: 版本 3.5.7


当前组件致力于提供Mybatis-Plus集成功能，方便引用该组件的业务服务可以短平快地使用以下功能
- 字段填充器：自动填充创建信息，更新信息等
- 逻辑删除支持
- 租户功能配置化集成
- 数据加密持久化
- 乐观锁
- 自定义批量插入
- 数据权限支持
- 分页支持
- 防止全表删除/更新
- SQL打印与分析 
- 代码生成器
- 连接池默认配置

## 配置
### 开启逻辑删除
建议配置到统一的配置文件中，不要在应用配置
```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted # 全局逻辑删除字段名
      logic-delete-value: 1 # 逻辑已删除值
      logic-not-delete-value: 0 # 逻辑未删除值
```

### 数据加密持久化
有时候，有些业务信息具有敏感性，需要加密存储到数据库中。
当前组件提供了默认的加密器，使用`AES`加解密。

开启加密持久化功能，需要配置以下信息
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

