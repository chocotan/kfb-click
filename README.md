kfb-click
=========

苍雪的自动点广告获取kfb的工具

---------

###使用方法###
需要java7

####windows:####

> 安装[git](http://git-scm.com/)和[maven](http://maven.apache.org/)

> 在git bash中执行下面的git clone命令

> 在cmd中cd到kfb-click目录执行下面的mvn命令

或者

> 安装maven

> 下载[这个](https://github.com/chocotan/kfb-click/archive/master.zip)

> 解压后cd进此目录

> 执行下面的mvn命令

或者

> 下载[这个](https://github.com/chocotan/kfb-click/archive/master.zip)

> 解压后cd进此目录里的target目录

> 执行```java -jar io.loli.kf-jar-with-dependencies.jar username password```

####mac和linux####
> 安装完git和maven后, 直接运行下面的命令
> 也可以用windows的后两种方法

####编译####
```
git clone git@github.com:chocotan/kfb-click.git
cd kfb-click
mvn package
```

####运行####

```
mvn exec:java -Dexec.mainClass="io.loli.kf.AdAutoClick" -Dexec.args="username password"
```
