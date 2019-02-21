package com.jaiz.utils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        String sql=new SQLExtractor().extract();
        String sqlBeautified=new SQLBeautifier().beautify(sql);
        System.out.println("=="+sqlBeautified+"==");
    }
}
