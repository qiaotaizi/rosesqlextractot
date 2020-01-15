package com.jaiz.utils;

import com.jaiz.utils.annotations.ScanTarget;
import net.paoding.rose.jade.annotation.SQL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 过滤函数类
 */
public class Filters {

    /**
     * 方法被@SQL注解标记
     * @param method
     * @return
     */
    public static boolean methodHasSQLAnnotation(Method method){
        return methodHasAnnotation(method,SQL.class);
    }

    /**
     * 方法被@ScanTarget注解标记
     * @param method
     * @return
     */
    public static boolean methodHasScanTargetAnnotation(Method method){
        return methodHasAnnotation(method,ScanTarget.class);
    }

    private static <T extends Annotation> boolean methodHasAnnotation(Method method, Class<T> annotationClass){
        return Objects.nonNull(method.getAnnotation(annotationClass));
    }
}
