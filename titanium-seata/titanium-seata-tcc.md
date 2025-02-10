# Titanium-Seata-TCC

### 简介
Titanium 框架提供了seata tcc 模式的支持，对于TCC模式产生的悬挂，空回滚等问题提供了统一的解决方案。
Titanium-Seata 提供了MySQL,Redis,以及内存的持久化支持。用户可以通过配置选择不同的持久化方式用于存储TCC事务信息。
此外，Titanium-Seata还提供了三阶段用户信息的自动包装，用户只需要关注业务逻辑的实现即可。

### 快速上手

#### 1.添加依赖
```groovy
api "com.github.yejinhui:titanium-seata-tcc:${titaniumSeataVersion}"
```

#### 2.配置文件
需要在 application.yml 配置文件中添加seata的配置，如下：
```yaml
titanium:
  seata:
    #TCC事务过程数据持久化方案选择，可选值：mysql、redis、memory
    holder-type: memory
```

#### 3.集成TCC 父类 AbstractTccAction ，实现业务逻辑
无需关注TM和RM的实现，只需要关注业务逻辑的实现即可。
并且自动包装用户信息在各阶段的传递。
```java
package com.titanium.user.profile.service.tcc;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.json.Json;
import com.titanium.seata.action.AbstractTccAction;
import com.titanium.seata.context.BusinessParamContext;
import com.titanium.seata.holder.TccResourceHolder;
import com.titanium.user.profile.common.user.req.UserCreateReq;
import com.titanium.user.profile.common.user.resp.UserDetailResp;
import com.titanium.user.profile.service.assembler.UserAssembler;
import com.titanium.user.profile.service.entity.User;
import com.titanium.user.profile.service.repository.UserRepository;
import com.titanium.user.profile.service.tcc.context.UserCreateContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserCreateAction extends AbstractTccAction {
    @Autowired
    private UserRepository userRepository;


    public UserCreateAction(TccResourceHolder holder) {
        super(holder);
    }

    @Override
    public boolean doPrepare(BusinessActionContext context, BusinessParamContext businessParamContext) {
        UserCreateContext paramContext = (UserCreateContext) businessParamContext;
        UserCreateReq req = paramContext.getReq();
        User user = userRepository.getByMobile(req.getMobile());
        context.addActionContext("req", Json.serialize(req));
        return ObjectUtil.isNull(user);
    }

    @Override
    public boolean doCommit(BusinessActionContext context) {
        UserContext userContext = getUserContext(context);
        UserContextHolder.set(userContext);
        UserCreateReq req = Json.deserialize((String) context.getActionContext("req"), UserCreateReq.class);
        User user = UserAssembler.toEntity(req);
        boolean saved = userRepository.save(user);
        Assert.isTrue(saved, "创建用户失败");
        UserDetailResp detailResp = UserAssembler.toDetailResp(user);
        Assert.isTrue(ObjectUtil.isNotNull(detailResp));
        Assert.notNull(detailResp.getUserId());
        return true;
    }

    @Override
    public boolean doRollback(BusinessActionContext context) {
        UserCreateReq req = Json.deserialize((String) context.getActionContext("req"), UserCreateReq.class);
        boolean deleted = userRepository.deleteByMobile(req.getMobile());
        return deleted;
    }
}
```



