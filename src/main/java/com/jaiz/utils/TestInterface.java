package com.jaiz.utils;

import net.paoding.rose.jade.annotation.SQL;

public interface TestInterface {

    // TODO 记得调试完毕后删除

    String sql="select * from table";

    @SQL(sql)
    String testDao();
}
