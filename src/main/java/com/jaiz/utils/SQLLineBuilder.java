package com.jaiz.utils;

import java.util.List;

/**
 * 记录sql的一行
 */
class SQLLineBuilder {

    private StringBuilder content;

    /**
     * 建议长度:70字符
     */
    private int recommendedLength=70;

    public SQLLineBuilder(int tabCount){
        this.content=new StringBuilder();
        for(int i=0;i<tabCount;i++){
            content.append('\t');
            this.recommendedLength-=2;
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
     * 尾部添加一字符
     * @param c
     */
    public void append(char c) {
        content.append(c);
    }

    /**
     * 尾部添加字符数组
     * @param cs
     * @param limit
     */
    public void appendChars(char[] cs,int limit){
        content.append(cs,0,limit);
    }

    @Override
    public String toString() {
        return this.content.toString();
    }

    /**
     * 判断一行内容是否已经过长
     * @return
     */
    public boolean isTooLong() {
        return content.length()>recommendedLength;
    }
}
