package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLLineBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            "ON".toCharArray(),
            "AND".toCharArray(),
            "WHERE".toCharArray(),
            "ORDER BY".toCharArray(),
            "LIMIT".toCharArray(),
            "GROUP BY".toCharArray(),
            "HAVING".toCharArray()
    };

    /**
     * select字符数组,处理子查询
     */
    private static final char[] selectCharArr="SELECT".toCharArray();

    // 单词/词组容器数组
    char[] wordCharArray = new char[10];
    // 单词容器游标,sql字符数组游标
    int cursorForWordCharArr, cursorForSqlCharArr = 0;


    /**
     * 美化
     *
     * @param sql
     * @return
     */
    public String beautify(String sql) {
        //去除两头空格
        sql = sql.trim();
        char[] sqlCharArr = sql.toCharArray();
        //游标处于引号内标识
        boolean inQuote = false;
        List<SQLLineBuilder> lines=new ArrayList<>();
        SQLLineBuilder currentLine = new SQLLineBuilder();
        //逐字符遍历
        while (cursorForSqlCharArr<sqlCharArr.length) {
            if (sqlCharArr[cursorForSqlCharArr] == '\'') {
                inQuote = !inQuote;
                readCharIntoContainer(sqlCharArr[cursorForSqlCharArr]);
                continue;
            }
            if (inQuote) {
                //引号内的字符不特别处理
                //直接进StringBuilder
                readCharIntoContainer(sqlCharArr[cursorForSqlCharArr]);
                continue;
            }
            boolean continueFlag=true;
            while(continueFlag){
                //读取单词至容器
                readWordIntoContainer(sqlCharArr);
                //遇到空格时检查单词是否处于换行关键字数组 或疑似处于换行关键字数组并进一步检查是否处于换行关键字数组
                NextLineStatusEnum status = checkNeedNewLine();
                //单词尾部添加空格
                readCharIntoContainer(' ');
                switch (status){
                    case NEED:
                        //若是,将旧的SQLLineBuilder加入list,将单词/词组纳入新的SQLLineBuilder,清空容器并跳出循环
                        lines.add(currentLine);
                        currentLine=new SQLLineBuilder();
                        currentLine.appendChars(wordCharArray,cursorForWordCharArr);
                        cleanWordCharArray();
                        continueFlag=false;
                        break;
                    case SUSPECT:
                        //若疑似,重复当前循环
                        break;
                    case NO:
                        //若否,将单词/词组纳入旧的SQLLineBuilder,清空容器并跳出循环
                        currentLine.appendChars(wordCharArray,cursorForWordCharArr);
                        cleanWordCharArray();
                        continueFlag=false;
                        break;
                }
            }
        }
        lines.add(currentLine);
        return SQLLineBuilder.listToString(lines);
    }

    /**
     * 读取单个字符至容器
     * @param c
     */
    private void readCharIntoContainer(char c) {
        if(cursorForWordCharArr==wordCharArray.length){
            expandWordCharArray();
        }
        wordCharArray[cursorForWordCharArr]=c;
        cursorForWordCharArr++;
        cursorForSqlCharArr++;
    }

    /**
     * 判断是否需要换行
     *
     * @return
     */
    private NextLineStatusEnum checkNeedNewLine() {
        //单词在关键字数组中得到完全匹配->NEED
        //单词在关键字数组中疑似匹配->SUSPECT
        for (char[] carr : addNewLineKeyWords) {
            boolean diff = false;
            for (int i = 0; i < cursorForWordCharArr && i < carr.length; i++) {
                if (!sameCharIgnoreCase(carr[i], wordCharArray[i])) {
                    //发现不同的字符
                    diff = true;
                    break;
                }
            }
            if (!diff) {
                //未发现不同的字符,但是carr未读至词尾,且下一个字符是空格,疑似相等
                if (cursorForWordCharArr < carr.length && carr[cursorForWordCharArr] == ' ') {
                    //疑似
                    return NextLineStatusEnum.SUSPECT;
                }
                //单词匹配且已经读至carr词尾
                if (cursorForWordCharArr == carr.length) {
                    return NextLineStatusEnum.NEED;
                }
            }
        }
        //->NO
        return NextLineStatusEnum.NO;
    }

    /**
     * 忽略大小写判断两个字符是否相等
     *
     * @param c
     * @param c1
     * @return
     */
    private boolean sameCharIgnoreCase(char c, char c1) {
        return c == c1 || c == c1 + 32 || c == c1 - 32;
    }

    /**
     * 读取单词至容器
     *
     * @param sqlCharArr
     */
    private void readWordIntoContainer(char[] sqlCharArr) {
        while (cursorForSqlCharArr < sqlCharArr.length) {
            if (sqlCharArr[cursorForSqlCharArr] == ' ') {
                //读到空格跳出循环
                break;
            }
            //判断是否需要扩展容器
            if (cursorForWordCharArr == wordCharArray.length) {
                expandWordCharArray();
            }
            wordCharArray[cursorForWordCharArr] = sqlCharArr[cursorForSqlCharArr];
            cursorForWordCharArr++;
            cursorForSqlCharArr++;
        }
    }

    /**
     * 清空单词/词组数组
     */
    private void cleanWordCharArray() {
        cursorForWordCharArr = 0;
    }

    /**
     * 扩展单词/词组数组
     *
     * @return
     */
    private void expandWordCharArray() {
        wordCharArray = Arrays.copyOf(wordCharArray, wordCharArray.length + wordCharArray.length / 2);
    }

    public static void main(String[] args) {
        SQLBeautifier sqlBeautifier = new SQLBeautifier();
        String s = "select * from (select c1,c2 from table3 t3) t1 left join table2 t2 on t1.id=t2.tId where id=0 and name='text' order by t1.id limit 10,20";
        String s1=sqlBeautifier.beautify(s);
        System.out.println(s1);

//        测试容器扩展
//        sqlBeautifier.wordCharArray[0]='a';
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));
//        System.out.println(sqlBeautifier.wordCharArray.length);
//        System.out.println();
//        char[] newArr=sqlBeautifier.expandWordCharArray();
//        System.out.println(Arrays.toString(newArr));
//        System.out.println(newArr.length);

//        测试读取一个单词
//        char[] sqlArr=s.toCharArray();
//        sqlBeautifier.readWordIntoContainer(sqlArr);
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));

//        测试判断字符相等
//        System.out.println(sqlBeautifier.sameCharIgnoreCase('A','1'));

//        测试换行检查
//        String word = "LEFT";
//        char[] sqlArr = word.toCharArray();
//        sqlBeautifier.readWordIntoContainer(sqlArr);
//        System.out.println(sqlBeautifier.checkNeedNewLine());

//        测试读取单个字符进入容器
//        sqlBeautifier.readWordIntoContainer("selecteeee".toCharArray());
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));
//        sqlBeautifier.readCharIntoContainer(' ');
//        sqlBeautifier.readCharIntoContainer('a');
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));

    }
}
