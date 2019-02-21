package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLLineBuilder;

import java.util.Arrays;

/**
 * sql美化器
 */
class SQLBeautifier {

    /**
     * 换行关键字
     */
    private static final char[][] addNewLineKeyWords = {
            "FROM".toCharArray(),
            "LEFT JOIN".toCharArray(),
            "JOIN".toCharArray(),
            "RIGHT JOIN".toCharArray(),
            "FROM".toCharArray(),
            "ON".toCharArray(),
            "AND".toCharArray(),
            "WHERE".toCharArray(),
            "ORDER BY".toCharArray(),
            "LIMIT".toCharArray(),
            "GROUP BY".toCharArray(),
            "HAVING".toCharArray()
    };

    // 单词/词组容器数组
    char[] wordCharArray = new char[10];
    // 游标
    int cursor=0;


    /**
     * 美化
     *
     * @param sql
     * @return
     */
    public String beautify(String sql) {
        //去除两头空格
        sql=sql.trim();
        char[] sqlCharArr=sql.toCharArray();
        //游标处于引号内标识
        boolean inQuote=false;
        SQLLineBuilder currentLine=new SQLLineBuilder();
        //逐字符遍历
        for(char c:sqlCharArr) {
            if (c == '\'') {
                inQuote = !inQuote;
                currentLine.append(c);
                continue;
            }
            if (inQuote) {
                //引号内的字符不特别处理
                //直接进StringBuilder
                currentLine.append(c);
                continue;
            }

            //逐字纳入容器
            //遇到空格时检查单词是否处于换行关键字数组 或疑似处于换行关键字数组并进一步检查是否处于换行关键字数组
            //若是,更换新的SQLLineBuilder
            //若否,将单词/词组纳入旧的SQLLineBuilder
            //清空容器


            //result.append(c);
        }

        //return result.toString();
        return null;
    }

    /**
     * 清空单词/词组数组
     */
    private void cleanWordCharArray(){
        cursor=0;
    }

    /**
     * 扩展单词/词组数组
     * @return
     */
    private char[] expandWordCharArray(){
        return Arrays.copyOf(wordCharArray,wordCharArray.length*2);
    }

    public static void main(String[] args) {
        SQLBeautifier sqlBeautifier=new SQLBeautifier();
//        String s="select * from table";
//        String s1=sqlBeautifier.beautify(s);
//        System.out.println(s1);

        sqlBeautifier.wordCharArray[0]='a';
        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));
        System.out.println(sqlBeautifier.wordCharArray.length);
        System.out.println();
        char[] newArr=sqlBeautifier.expandWordCharArray();
        System.out.println(Arrays.toString(newArr));
        System.out.println(newArr.length);
    }
}
