# rosesqlextractot
rose框架DAO层sql代码抽取器

## 功能:
快速抽取@SQL注解中的sql语句,方便对sql进行分析和检查  
可以有效减少sql调试时的重启次数

## 使用方法:
克隆源代码
```
git clone git@github.com:qiaotaizi/rosesqlextractot.git
```
使用maven编译至本地仓库
在pom中添加依赖如下:  
```
<dependency>
  <groupId>com.jaiz.utils</groupId>
  <artifactId>rosesqlextractot</artifactId>
  <version>1.0.0-SNAPSHOT</version>
</dependency>
```
在你的项目的任意java类中  
使用main方法运行如下代码
```
ExtractUtil.work();
```
注意:调试完毕后从pom中删除依赖
