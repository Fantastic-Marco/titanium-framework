package com.titanium.data.mybatis.plus.log;

import com.baomidou.mybatisplus.extension.p6spy.StdoutLogger;
import com.p6spy.engine.logging.Category;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TitaniumSqlStdoutLogger extends StdoutLogger {

    @Override
    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
        super.logSQL(connectionId, now, elapsed, category, prepared, sql, url);
    }

    @Override
    public void logText(String text) {
        log.info(text);
    }
}
