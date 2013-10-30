kfb-click
=========

苍雪的自动点广告获取kfb的工具

---------

###使用方法###
####windows:####
请先安装[git](http://git-scm.com/)和[maven](http://maven.apache.org/)
在git bash中执行下面的git clone命令, 在cmd中cd到kfb-click目录执行下面的mvn命令

或者

只安装maven, 下载[这个](https://github.com/chocotan/kfb-click/archive/master.zip), 解压后cd进此目录后执行下面的mvn命令
####mac和linux####
安装完git和maven后, 直接运行下面的命令

####先编译####
```
git clone git@github.com:chocotan/kfb-click.git
cd kfb-click
mvn package
```

####运行####

```
mvn exec:java -Dexec.mainClass="io.loli.kf.AdAutoClick" -Dexec.args="username password"
```
