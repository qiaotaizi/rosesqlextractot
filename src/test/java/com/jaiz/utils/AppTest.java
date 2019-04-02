package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLNotFoundException;
import org.junit.Test;

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
}
