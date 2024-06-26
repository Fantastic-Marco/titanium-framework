package com.titanium.data.mybatis.plus.encrypt.encrytor;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EncryptTypeHandler implements TypeHandler<String> {
    private IEncryptor encryptor;


    public EncryptTypeHandler() {
        this.encryptor = SpringUtil.getBean(IEncryptor.class);
    }



    /**
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, encryptor.encrypt(parameter));
    }

    /**
     * @param rs
     * @param columnName Colunm name, when configuration <code>useColumnLabel</code> is <code>false</code>
     */
    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        return encryptor.decrypt(rs.getString(columnName));
    }

    /**
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        return encryptor.decrypt(rs.getString(columnIndex));
    }

    /**
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return encryptor.decrypt(cs.getString(columnIndex));
    }
}
