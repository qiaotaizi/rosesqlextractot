package com.jaiz.utils.exceptions;

import java.util.List;

/**
 * 记录sql的一行
 */
public class SQLLineBuilder {

    private StringBuilder content;

    public SQLLineBuilder(int tabCount){
        this.content=new StringBuilder();
        for(int i=0;i<tabCount;i++){
            content.append('\t');
        }
    }

    public static String listToString(List<SQLLineBuilder> lines) {
        StringBuilder sb=new StringBuilder();
        for(SQLLineBuilder sql:lines){
            sb.append(sql.toString()).append('\n');
        }
        return sb.toString();
    }

    /**
     * 尾部添加
     * @param c
     */
    public void append(char c) {
        content.append(c);
    }

    public void appendChars(char[] cs,int limit){
        content.append(cs,0,limit);
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

//    public static void main(String[] args) {
//
//        SQLLineBuilder builder=new SQLLineBuilder();
//        char[] cs="select".toCharArray();
//        builder.appendChars(cs);
//        System.out.println(builder);
//    }
}
