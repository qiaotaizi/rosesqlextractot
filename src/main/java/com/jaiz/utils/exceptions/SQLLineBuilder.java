package com.jaiz.utils.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录sql的一行
 */
public class SQLLineBuilder {

    private StringBuilder content=new StringBuilder();

    /**
     * 尾部添加
     * @param c
     */
    public void append(char c) {
        content.append(c);
    }

    /**
     * 首部添加
     * @param c
     */
    public void appendLeft(char c){
        content.insert(0,c);
    }

    @Override
    public String toString() {
        return this.content.toString();
    }

    public static void main(String[] args) {

        List<SQLLineBuilder> list=new ArrayList<>();
        SQLLineBuilder cur=new SQLLineBuilder();
        cur.append('a');
        list.add(cur);
        cur=new SQLLineBuilder();
        cur.append('b');
        list.add(cur);
        for(SQLLineBuilder b:list){
            System.out.println(b);
        }
    }
}
