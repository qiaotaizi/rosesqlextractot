package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * Unit test for simple ExtractUtil.
 */
public class AppTest 
{

    @Test
    public void workTest() throws NoSuchMethodException, SQLNotFoundException {
        String sql=ExtractUtil.work(TestDAO.class,"testSelect");
        System.out.println(sql);
    }

    @Test
    public void scanTest(){
        new SQLScanner().scan("com.jaiz.utils","DAO");
        new SQLScanner().defaultScan();
    }

    @Test
    public void scan2Test(){
        new FunctionalSQLScanner().scan("com.jaiz.utils",
                "DAO",
                new Predicate[]{
                        (Predicate<Method>) Filters::methodHasSQLAnnotation},
                new Predicate[]{
                        (Predicate<String>)sql->sql.toLowerCase().contains("BOSS_HELP".toLowerCase())
                });
    }
}
