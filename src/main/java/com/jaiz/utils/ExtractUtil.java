package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;

/**
 *
 *
 */
public class ExtractUtil
{
    public static <T> String work(Class<T> daoClass,String methodName) throws NoSuchMethodException, SQLNotFoundException {
        String result= new SQLBeautifier().beautify(new SQLExtractor<>(daoClass,methodName).extract());
        ClipboardUtil.copyToClipBoard(result);
        return result;
    }

}
