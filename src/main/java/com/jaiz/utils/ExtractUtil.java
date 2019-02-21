package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;

/**
 * Hello world!
 *
 */
public class ExtractUtil
{

    public static <T> String work(Class<T> daoClass,String methodName) throws NoSuchMethodException, SQLNotFoundException {
        return new SQLBeautifier().beautify(new SQLExtractor<T>(daoClass,methodName).extract());
    }

    public static void main( String[] args ) throws NoSuchMethodException, SQLNotFoundException {

        Class<TestInterface> testClass=TestInterface.class;
        String methodName="testDao";
        String sql=new SQLExtractor(testClass,methodName).extract();
        System.out.println(sql);
        String sqlBeautified=new SQLBeautifier().beautify(sql);
        System.out.println("=="+sqlBeautified+"==");
    }
}
