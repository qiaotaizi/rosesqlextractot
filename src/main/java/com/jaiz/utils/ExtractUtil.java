package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;

/**
 * Hello world!
 *
 */
public class ExtractUtil
{

    public static <T> String work(Class<T> daoClass,String methodName) throws NoSuchMethodException, SQLNotFoundException {
        return new SQLBeautifier().beautify(new SQLExtractor<>(daoClass,methodName).extract());
    }

    public static void main( String[] args ) throws NoSuchMethodException, SQLNotFoundException {
        String sqlB=work(TestInterface.class,"testDao");

        System.out.println(sqlB);
    }
}
