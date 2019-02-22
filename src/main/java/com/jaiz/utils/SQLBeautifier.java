package com.jaiz.utils;

import com.jaiz.utils.exceptions.SQLLineBuilder;

import java.util.*;

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
            "HAVING".toCharArray(),
            "CASE".toCharArray(),
            "WHEN".toCharArray(),
            "THEN".toCharArray(),
            "ELSE".toCharArray(),
            "END".toCharArray()
    };

    /**
     * select字符数组,处理子查询
     */
    private static final char[] selectCharArr = "SELECT".toCharArray();

    // 单词/词组容器数组
    char[] wordCharArray = new char[10];
    // 单词容器游标,sql字符数组游标
    int cursorForWordCharArr, cursorForSqlCharArr = 0;

    //select出现时的左括号深度栈
    LinkedList<Integer> bracketDepthStack = new LinkedList<>();
    //左括号深度
    int bracketDepth = 0;
    //select深度,也是添加tab的数量
    int selectDepth = 0;
    //
    //boolean stackTopRemoved = false;

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
        List<SQLLineBuilder> lines = new ArrayList<>();
        SQLLineBuilder currentLine = new SQLLineBuilder(selectDepth);

        //逐字符遍历
        while (cursorForSqlCharArr < sqlCharArr.length) {
            if (sqlCharArr[cursorForSqlCharArr] == '\'') {
                inQuote = !inQuote;
                currentLine.append(sqlCharArr[cursorForSqlCharArr]);
                cursorForSqlCharArr++;
                continue;
            }
            if (inQuote) {
                //引号内的字符不特别处理
                //直接进StringBuilder
                currentLine.append(sqlCharArr[cursorForSqlCharArr]);
                cursorForSqlCharArr++;
                continue;
            }
            //读到左右括号时,控制深度
            if (sqlCharArr[cursorForSqlCharArr] == '(') {
                bracketDepth++;
                currentLine.append(sqlCharArr[cursorForSqlCharArr]);
                cursorForSqlCharArr++;
                continue;
            }
            if (sqlCharArr[cursorForSqlCharArr] == ')') {
                beforeBracketDepthSelfMinus();
                bracketDepth--;
                currentLine.append(sqlCharArr[cursorForSqlCharArr]);
                cursorForSqlCharArr++;
                continue;
            }
            boolean continueFlag = true;
            while (continueFlag) {
                //读取单词至容器,并取得使单词断开的字符(可能是' ','(',')')
                char wordBreaker = readWordIntoContainer(sqlCharArr);
                //遇到空格时检查单词是否处于换行关键字数组 或疑似处于换行关键字数组并进一步检查是否处于换行关键字数组
                NextLineStatusEnum status = checkNeedNewLine();
                readCharIntoContainer(wordBreaker);
                switch (status) {
                    case SELECT:
                        //特殊处理SELECT
                        if (bracketDepth > 0) {
                            //需要换行,仿照case NEED
                            bracketDepthStack.addFirst(bracketDepth);
                            lines.add(currentLine);
                            currentLine = new SQLLineBuilder(bracketDepth);
                            currentLine.appendChars(wordCharArray, cursorForWordCharArr);
                            cleanWordCharArray();
                            continueFlag = false;
                        } else {
                            //不需要换行,仿照case NO
                            currentLine.appendChars(wordCharArray, cursorForWordCharArr);
                            cleanWordCharArray();
                            continueFlag = false;
                        }
                        break;
                    case NEED:
                        //若是,将旧的SQLLineBuilder加入list,将单词/词组纳入新的SQLLineBuilder,清空容器并跳出循环
                        lines.add(currentLine);
                        currentLine = new SQLLineBuilder(bracketDepth);
                        currentLine.appendChars(wordCharArray, cursorForWordCharArr);
                        cleanWordCharArray();
                        continueFlag = false;
                        break;
                    case SUSPECT:
                        //若疑似,重复当前循环
                        break;
                    case NO:
                        //若否,将单词/词组纳入旧的SQLLineBuilder,清空容器并跳出循环
                        currentLine.appendChars(wordCharArray, cursorForWordCharArr);
                        cleanWordCharArray();
                        continueFlag = false;
                        break;
                }
            }
        }
        lines.add(currentLine);
        return SQLLineBuilder.listToString(lines);
    }

    /**
     * 左括号深度自减之前,检查深度是否与栈顶的值相等
     * 若栈是空的,什么也不做
     * 若相等select深度减1,栈顶移走一个元素,但是select深度最小为0
     */
    private void beforeBracketDepthSelfMinus() {
        if (bracketDepthStack.size() == 0) {
            return;
        }
        if (bracketDepth == bracketDepthStack.getFirst().intValue()) {
            bracketDepthStack.removeFirst();
            selectDepth--;
            //stackTopRemoved = true;
        }
        if (selectDepth < 0) {
            selectDepth = 0;
        }
    }

    /**
     * 读取单个字符至容器
     *
     * @param c
     */
    private void readCharIntoContainer(char c) {
        if (cursorForWordCharArr == wordCharArray.length) {
            expandWordCharArray();
        }
        wordCharArray[cursorForWordCharArr] = c;
        cursorForWordCharArr++;
        cursorForSqlCharArr++;
    }

    /**
     * 判断是否需要换行
     *
     * @return
     */
    private NextLineStatusEnum checkNeedNewLine() {
        //先检查select
        if (isSelect(wordCharArray)) {
            return NextLineStatusEnum.SELECT;
        }
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
     * 判断数组内容是否时SELECT
     *
     * @param wordCharArray
     * @return
     */
    private boolean isSelect(char[] wordCharArray) {
        if (cursorForWordCharArr != selectCharArr.length) {
            return false;
        }
        for (int i = 0; i < selectCharArr.length; i++) {
            if (!sameCharIgnoreCase(selectCharArr[i], wordCharArray[i])) {
                return false;
            }
        }
        return true;
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
     * @return 返回单词之后的一个字符
     */
    private char readWordIntoContainer(char[] sqlCharArr) {
        while (cursorForSqlCharArr < sqlCharArr.length) {
            if (sqlCharArr[cursorForSqlCharArr] == ' ') {
                //读到空格跳出循环,并返回单词之后的字符
                return sqlCharArr[cursorForSqlCharArr];
            }
            //读到括号跳出循环,并返回单词之后的字符,控制括号深度
            if (sqlCharArr[cursorForSqlCharArr] == '(') {
                bracketDepth++;
                return sqlCharArr[cursorForSqlCharArr];
            }
            if (sqlCharArr[cursorForSqlCharArr] == ')') {
                beforeBracketDepthSelfMinus();
                bracketDepth--;
                return sqlCharArr[cursorForSqlCharArr];
            }
            //判断是否需要扩展容器
            if (cursorForWordCharArr == wordCharArray.length) {
                expandWordCharArray();
            }
            wordCharArray[cursorForWordCharArr] = sqlCharArr[cursorForSqlCharArr];
            cursorForWordCharArr++;
            cursorForSqlCharArr++;
        }
        return ' ';
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
        String s1 = sqlBeautifier.beautify(s);
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
//        String word = "SELECT";
//        char[] sqlArr = word.toCharArray();
//        sqlBeautifier.readWordIntoContainer(sqlArr);
//        System.out.println(sqlBeautifier.checkNeedNewLine());

//        测试读取单个字符进入容器
//        sqlBeautifier.readWordIntoContainer("selecteeee".toCharArray());
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));
//        sqlBeautifier.readCharIntoContainer(' ');
//        sqlBeautifier.readCharIntoContainer('a');
//        System.out.println(Arrays.toString(sqlBeautifier.wordCharArray));

//        测试链表
//        sqlBeautifier.bracketDepthStack.addFirst(sqlBeautifier.bracketDepth);
//        sqlBeautifier.bracketDepth++;
//        sqlBeautifier.bracketDepthStack.addFirst(sqlBeautifier.bracketDepth);
//        sqlBeautifier.bracketDepth++;
//        sqlBeautifier.bracketDepthStack.addFirst(sqlBeautifier.bracketDepth);
//        sqlBeautifier.bracketDepth++;
//        sqlBeautifier.bracketDepthStack.addFirst(sqlBeautifier.bracketDepth);
//        sqlBeautifier.bracketDepth++;
//        sqlBeautifier.bracketDepthStack.addFirst(sqlBeautifier.bracketDepth);
//        sqlBeautifier.bracketDepth--;
//        sqlBeautifier.bracketDepthStack.removeFirst();
//        System.out.println(sqlBeautifier.bracketDepthStack);
    }
}
