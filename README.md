# rosesqlextractor
rose框架DAO层sql代码抽取器
## 更新:
2.0版本  
增加包扫描功能,可以使用@ScanTarget注解标记需要抽取的方法  
程序将批量抽取所需sql并复制到剪贴板  
用法详见下文
## 功能:
快速抽取@SQL注解中的sql语句  
将结果保存至剪贴板  
方便对sql进行分析和检查  
可以有效减少sql调试时的重启次数
## 使用方法:
克隆源代码
```
git clone git@github.com:qiaotaizi/rosesqlextractot.git
```
使用maven编译至本地仓库
```bash
mvn clean install -Dmaven.test.skip=true
```
在pom中添加依赖如下:  
```xml
<dependency>
  <groupId>com.jaiz.utils</groupId>
  <artifactId>rosesqlextractot</artifactId>
  <version>2.0.0</version>
</dependency>
```
在你的项目的任意java类中  
加入如下代码并运行  
```java
public static void main(String[] args) throws NoSuchMethodException, com.jaiz.utils.exceptions.SQLNotFoundException {
    String sql=com.jaiz.utils.ExtractUtil.work(${daoClass},${methodName});
    //System.out.println(sql);
}
```
sql输出自选  


2.0.0版本新增包扫描抽取sql的能力  
在你需要抽取sql的方法上加入@com.jaiz.utils.annotations.ScanTarget注解  
在你的项目的任意java类中  
加入如下代码并运行

```java
public static void main(String[] args){
    new com.jaiz.utils.SQLScanner().defaultScan();
}
```

2.0.1版本新增包含子串sql筛选功能  
在你的项目的任意java类中  
加入如下代码并运行  
最终将筛选出所有含子串的sql

```java
public static void main(String[] args){
        new com.jaiz.utils.FunctionalSQLScanner().defaultScanSubStr("BOSS_HELP_CHECK");
    }
```

运行完毕可以直接从剪贴板中访问sql  
粘贴在Navicat等数据库可视化工具中查看  
注意:调试完毕后删除pom依赖和测试java代码
