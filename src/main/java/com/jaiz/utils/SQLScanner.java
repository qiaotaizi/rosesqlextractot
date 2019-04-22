package com.jaiz.utils;

import com.jaiz.utils.annotations.ScanTarget;
import com.jaiz.utils.exceptions.SQLNotFoundException;
import net.paoding.rose.jade.annotation.DAO;
import net.paoding.rose.jade.annotation.SQL;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SQLScanner {

    private ClassLoader classLoader;

    private List<SQLInfo> sqlList=new ArrayList<>();

    private SQLExtractor extractor=new SQLExtractor();

    private String suffix;

    /**
     * 默认包名为com.ttpai,方便调用
     */
    public void defaultScan(){
        scan("com.ttpai","DAO");
    }

    /**
     * 扫描包名下含有ScanTarget注解的方法,并抽取sql
     */
    public void scan(String packName,String suffix){
        this.suffix=suffix;
        this.classLoader=SQLScanner.class.getClassLoader();
        //file,jar执行不同的策略 这里不处理jar包\
        findClassesInPackAndExtractSQL(packName);

        copyAllSQLToClipboard();

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

    private void findClassesInPackAndExtractSQL(String packName){
        URL url=classLoader.getResource(packName.replace(".","/"));
        //略过jar包
        if(!url.getProtocol().equals("file")){
            return;
        }
        URI uri=null;
        try {
            uri=url.toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        File f=new File(uri);
        f.listFiles(file->{
            if(file.isDirectory()){
                findClassesInPackAndExtractSQL(packName+"."+file.getName());
            }
            if(file.getName().endsWith(".class")){
                String className=packName+"."+file.getName().replace(".class","");
                if(!className.endsWith(suffix)){
                    return false;
                }
                Class<?> clazz=null;
                try {
                    clazz=classLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
                DAO daoAnnotation=clazz.getAnnotation(DAO.class);
                if(daoAnnotation==null){
                    //排除非DAO类
                    return false;
                }
                //找到类中包含ScanTarget注解且包含SQL注解的方法
                Method[] ms=clazz.getDeclaredMethods();
                for(Method m:ms){
                    ScanTarget st=m.getAnnotation(ScanTarget.class);
                    if(st==null){
                        continue;
                    }
                    SQL s=m.getAnnotation(SQL.class);
                    if(s==null){
                        continue;
                    }
                    try {
                        String unBeautifiedSql=extractor.extract(m);
                        String beautifiedSql=new SQLBeautifier().beautify(unBeautifiedSql);
                        SQLInfo sqlInfo=new SQLInfo();
                        sqlInfo.setClassName(clazz.getName());
                        sqlInfo.setMethodName(m.getName());
                        sqlInfo.setSql(beautifiedSql);
                        sqlList.add(sqlInfo);
                    } catch (SQLNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
            return false;
        });
    }
}
