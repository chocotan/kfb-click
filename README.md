kfb-click
=========

苍雪的自动点广告获取kfb的工具

---------

###使用方法###
windows:
请先安装git和mavenmac
git: http://git-scm.com/
maven: http://maven.apache.org/
mac和linux:
自行解决(应该比windows简单多了)

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
