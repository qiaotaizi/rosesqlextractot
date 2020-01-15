package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;
import net.paoding.rose.jade.annotation.SQL;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * sql抽离器
 */
class SQLExtractor<T> {

    private Class<T> daoClass;

    private String targetMethodName;

    public SQLExtractor(){

    }

    public SQLExtractor(Class<T> daoClass,String targetMethodName){
        this.daoClass=daoClass;
        this.targetMethodName=targetMethodName;
    }

    public String extract(Method m) throws SQLNotFoundException {
        SQL sqlAnnotation = m.getAnnotation(SQL.class);
        String sqlInAnnotation = sqlAnnotation.value();
        if(StringUtils.isBlank(sqlInAnnotation)){
            throw new SQLNotFoundException(targetMethodName+"方法的@SQL注解中未找到sql语句");
        }
        return sqlInAnnotation;
    }

    /**
     * 抽离方法
     * @return
     */
    public String extract() throws NoSuchMethodException, SQLNotFoundException {
        Method[] ms = daoClass.getDeclaredMethods();
        Method targetMethod = null;
        for (Method m : ms) {
            if (m.getName().equals(targetMethodName)) {
                targetMethod = m;
                break;
            }
        }
        if (targetMethod == null) {
            throw new NoSuchMethodException(targetMethodName + "方法不存在");
        }
        SQL sqlAnnotation = targetMethod.getAnnotation(SQL.class);
        String sqlInAnnotation = sqlAnnotation.value();
        if(sqlAnnotation==null){
           throw new SQLNotFoundException(targetMethodName+"方法的@SQL注解中未找到sql语句");
        }
        return sqlInAnnotation;
    }
}
