package com.jaiz.utils;


import com.jaiz.utils.exceptions.SQLNotFoundException;
import net.paoding.rose.jade.annotation.DAO;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FunctionalSQLScanner {

    private ClassLoader classLoader;

    private List<SQLInfo> sqlList=new ArrayList<>();

    private SQLExtractor<?> extractor=new SQLExtractor<>();

    private String suffix;

    /**
     * 扫描指定包名之下含有指定字符后缀的类中的方法
     * 并使用指定的过滤函数过滤这些方法，抽取sql并打印
     * @param packName 包名
     * @param suffix 类后缀
     * @param methodFilters 作用于方法列表的过滤函数列表
     * @param stringContentFilters 作用于sql字符串的过滤函数列表
     */
    public void scan(String packName, String suffix, Predicate<Method>[] methodFilters,Predicate<String>[] stringContentFilters){
        this.suffix=suffix;
        this.classLoader=SQLScanner.class.getClassLoader();
        //file,jar执行不同的策略 这里不处理jar包\
        findClassesInPackAndExtractSQLWithFilters(packName,methodFilters,stringContentFilters);
        copyAllSQLToClipboard();
    }

    /**
     * 默认扫描指定包名DAO文件中包含某子串的sql
     * @param subStr 目标子串
     */
    public void defaultScanSubStr(String subStr){
        scan("com.ttpai",
                "DAO",
                new Predicate[]{
                        (Predicate<Method>) Filters::methodHasSQLAnnotation},
                new Predicate[]{
                        (Predicate<String>)sql->sql.toLowerCase().contains(subStr.toLowerCase())
                });
    }

    private void copyAllSQLToClipboard() {
        if(sqlList.size()==0){
            System.out.println("扫描未发现目标sql");
            return;
        }
        StringBuilder sb=new StringBuilder();
        for(SQLInfo sqlInfo:sqlList){
            sb.append("-- ").append(sqlInfo.getClassName()).append(".").append(sqlInfo.getMethodName())
                    .append(System.lineSeparator())
                    .append(sqlInfo.getSql())
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        ClipboardUtil.copyToClipBoard(sb.toString());
    }

    private void findClassesInPackAndExtractSQLWithFilters(String packName,Predicate<Method>[] methodFilters,Predicate<String>[] sqlContentFilters){
        URL url=classLoader.getResource(packName.replace(".","/"));
        if (Objects.isNull(url)){
            return;
        }
        //略过jar包
        if(!url.getProtocol().equals("file")){
            return;
        }
        URI uri;
        try {
            uri=url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        //文件过滤
        File f=new File(uri);
        f.listFiles(file->{
            if(file.isDirectory()){
                findClassesInPackAndExtractSQLWithFilters(packName+"."+file.getName(),methodFilters,sqlContentFilters);
            }
            //过滤符合要求的类
            if(file.getName().endsWith(".class")){
                String className=packName+"."+file.getName().replace(".class","");
                if(!className.endsWith(suffix)){
                    return false;
                }
                Class<?> clazz;
                try {
                    clazz=classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                //类必须有@DAO标注
                DAO daoAnnotation=clazz.getAnnotation(DAO.class);
                if(daoAnnotation==null){
                    return false;
                }
                //找到类中包含ScanTarget注解且包含SQL注解的方法
                Stream<Method> methodStream=Arrays.stream(clazz.getDeclaredMethods());
                for (Predicate<Method> methodPredicate:methodFilters){
                    methodStream=methodStream.filter(methodPredicate);
                }
                methodStream.forEach(
                        method -> {
                            //抽取sql并进行过滤
                            String unBeautifiedSql;
                            try {
                                unBeautifiedSql = extractor.extract(method);
                            } catch (SQLNotFoundException e) {
                                e.printStackTrace();
                                return;
                            }
                            for(Predicate<String> sqlContentFilter:sqlContentFilters){
                                if (sqlContentFilter.negate().test(unBeautifiedSql)){
                                    return;
                                }
                            }

                            String beautifiedSql=new SQLBeautifier().beautify(unBeautifiedSql);
                            SQLInfo sqlInfo=new SQLInfo();
                            sqlInfo.setClassName(clazz.getName());
                            sqlInfo.setMethodName(method.getName());
                            sqlInfo.setSql(beautifiedSql);
                            sqlList.add(sqlInfo);
                        }
                );
                return false;
            }
            return false;
        });
    }
}
