package com.titanium.data.fluent.mybatis;

import cn.org.atool.generator.FileGenerator;
import cn.org.atool.generator.annotation.Table;
import cn.org.atool.generator.annotation.Tables;
import com.google.protobuf.Empty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class TitaniumEntityGenerator {
    private Datasource datasource;

    public TitaniumEntityGenerator(Datasource datasource) {
        this.datasource = datasource;
    }

    public void generate() throws Exception {
        // 引用配置类，build方法允许有多个配置类
        FileGenerator.build(Empty.class);
    }

//    @Tables(
//            // 设置数据库连接信息
//            url = data, username = "root", password = "password",
//            // 设置entity类生成src目录, 相对于 user.dir
//            srcDir = "src/main/java",
//            // 设置entity类的package值
//            basePack = "cn.org.atool.fluent.mybatis.demo3",
//            // 设置dao接口和实现的src目录, 相对于 user.dir
//            daoDir = "src/main/java",
//            // 设置哪些表要生成Entity文件
//            tables = {@Table(value = {"hello_world"})}
//    )
    static class Empty {
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    static class Datasource {
        String url;
        String username;
        String password;
        String driverClassName;
    }
}
