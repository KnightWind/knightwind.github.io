---
layout: post
title: "HMatser启动问题"
keywords: ["hadoop", "hbase","HMaster"]
description: "Hmaster启动后自动关闭问题处理方法"
category: "technology"
tags: ["Hbase", "hadoop"]
---
{% include JB/setup %}


今天在启动Hbase时发现一个问题：每次启动后Hmaster几秒钟后自动就关闭了，在日志文件中发现了这样的异常：

org.apache.hadoop.hbase.TableExistsException: hbase:namespace
  at org.apache.hadoop.hbase.master.handler.CreateTableHandler.prepare(CreateTableHandler.java:132)
  at org.apache.hadoop.hbase.master.TableNamespaceManager.createNamespaceTable(TableNamespaceManager.java:232)
  at org.apache.hadoop.hbase.master.TableNamespaceManager.start(TableNamespaceManager.java:86)
  at org.apache.hadoop.hbase.master.HMaster.initNamespace(HMaster.java:1165)
  at org.apache.hadoop.hbase.master.HMaster.finishInitialization(HMaster.java:1009)
  at org.apache.hadoop.hbase.master.HMaster.run(HMaster.java:678)
  at java.lang.Thread.run(Thread.java:745)


在网上查找一番之后发现有介绍这种方法处理：

1.切换到zookeeper的bin目录；

2.执行$sh zkCli.sh

输入‘ls /’

4.输入‘rmr /hbase’

5.退出

重启hbase即可。

但我的Hbase使用的是Hbase自带的zookeeper,故上述方法不适用。

如使用Hbase自带zookeeper出现该问题处理办法是：

1.查看你的hbase-site.xml文件，找到hbase.zookeeper.property.dataDir的配置目录


2.删除该目录下所有文件


3.重启Hbase




另外一个问题：Hadoop HDFS在电脑重启后启动不起来，必须执行hadoop namenode -format才能启动，在网上找到这篇博客可以解决该问题，亲测可用


http://blog.csdn.net/chengfei112233/article/details/7252812

