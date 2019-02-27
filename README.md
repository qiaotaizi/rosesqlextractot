# rosesqlextractot
rose框架DAO层sql代码抽取器

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
```
mvn clean install -Dmaven.test.skip=true
```
在pom中添加依赖如下:  
```xml
<dependency>
  <groupId>com.jaiz.utils</groupId>
  <artifactId>rosesqlextractot</artifactId>
  <version>1.0.3-SNAPSHOT</version>
</dependency>
```
在你的项目的任意java类中  
加入如下代码并运行  
sql输出自选  
运行完毕可以直接从剪贴板中访问sql  
粘贴在Navicat等数据库可视化工具中查看
```java
public static void main(String[] args) throws NoSuchMethodException, com.jaiz.utils.exceptions.SQLNotFoundException {
    String sql=com.jaiz.utils.ExtractUtil.work(${daoClass},${methodName});
    //System.out.println(sql);
}
```
注意:调试完毕后从pom中删除依赖
