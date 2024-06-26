package com.titanium.data.mybatis.plus.encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.titanium.data.mybatis.plus.encrypt.annotation.Encrypt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public interface Encrypted {

    @JsonIgnore
    default List<Field> getEncryptFields() {
        List<Field> list = new ArrayList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Encrypt.class)) {
                list.add(field);
            }
        }
        return list;
    }
}
