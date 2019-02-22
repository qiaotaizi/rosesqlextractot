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
```
mvn clean install -Dmaven.test.skip=true
```
在pom中添加依赖如下:  
```
<dependency>
  <groupId>com.jaiz.utils</groupId>
  <artifactId>rosesqlextractot</artifactId>
  <version>1.0.2-SNAPSHOT</version>
</dependency>
```
在你的项目的任意java类中  
加入如下代码并运行
```
public static void main(String[] args) throws NoSuchMethodException, com.jaiz.utils.exceptions.SQLNotFoundException {
    String sql=com.jaiz.utils.ExtractUtil.work(BossSuccessAuctionDAO.class,"selectMaxIdDealCarDetail");
    System.out.println(sql);
}
```
注意:调试完毕后从pom中删除依赖
