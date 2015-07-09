---
layout: post
title: "dubbo分布式服务架构应用"
keywords: ["dubbo", "zookeeper","java","server",分布式"]
description: "基于dubbo的分布式服务架构"
category: "technology"
tags: ["dubbo", "zookeeper"]
---
{% include JB/setup %}

公司最近要做一款智能终端服务平台，基于架构上的考虑，dubbo能很好的满足我们的需求。这两天研究了下基于dubbo zoomkeeper的分布式服务架构搭建，基本掌握的dubbo的分布式服务搭建部署等。总结归纳如下：


 1. 首先需要对dubbo有基本的了解。


 2.注册中心基于zookeeper，首先下载安装zookeeper

 3.解压zookeeper后复制一份/conf/zoo_sample.cfg配置，命名为zoo.cfg。具体配置项详解可咨询zookeeper官网文档


4.准备发布服务


4.1 准备提供服务的接口， 具体操作是：


a) 新建一个项目 
b) 新建一个接口，真的只需要定义接口哟 如：

```java
      package com.wcb.dubbo.test;

      public interface SayHello {
        
        //
        public String sayHello(String name);
      }

```

c) 将这个项目打成jar包，提供给服务的provider 和 consumer 如果是maven project，则直接在dependency中添加该接口项目




4.2 准备编写service provider 具体操作是：

新建一个项目，导入准备提供服务接口定义项目依赖。

建一个类，实现要提供的服务接口。

```java

          package com.wcb.dubbotest.impl;

          import com.wcb.dubbo.test.SayHello;

          public class MySayHelloImpl implements SayHello {

            public String sayHello(String name) {
              // TODO Auto-generated method stub
              return "Hello ! "+name;
            }

          }
```

在Spring配置文件中注册服务: applicationContext.xml 文件内容如下：

```xml

          <?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans" xmlns:util="http://www.springframework.org/schema/util" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
          xmlns:context="http://www.springframework.org/schema/context" xmlns:jaxws="http://cxf.apache.org/jaxws" xmlns:jaxrs="http://cxf.apache.org/jaxrs" xmlns:jee="http://www.springframework.org/schema/jee"
          xmlns:tx="http://www.springframework.org/schema/tx"
          xmlns:dubbo="http://code.alibabatech.com/schema/dubbo" 
          xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.1.xsd 
           http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd 
           http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd
           http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd
           http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.1.xsd
           http://code.alibabatech.com/schema/dubbo
           http://code.alibabatech.com/schema/dubbo/dubbo.xsd
           ">
         
         <!-- Application name -->  
         <dubbo:application name="say-hello-app" />  
          
         <!-- registry address, used for service to register itself -->  
         <dubbo:registry address="zookeeper://127.0.0.1:2181" />   
         
         
         <dubbo:protocol name="dubbo" port="20990" />
          
         <dubbo:service interface="com.wcb.dubbo.test.SayHello"  
                ref="helloService" executes="10" />
          
            <!-- Default Protocol -->  
            <!--  
            <dubbo:protocol server="netty" />  
            -->  
          
            <!-- designate implementation -->  
         <bean id="helloService" class="com.wcb.dubbotest.impl.MySayHelloImpl" />
         
        </beans>
```

将项目部署到tomcat，启动tomcat即可启动服务。

也可以直接在main方法中启动，我的另一个服务将在main方法中启动和管理

定义服务接口IProcessData接口如下：


```java

          package com.wcb.duboo;

          public interface IProcessData {
            
            public String processData(String data);
          }
```


为了方便起见服务接口定义，provider consumer 全部包括在工程该工程


创建服务接口实现类IProcessDataImpl：


```java

          public class IProcessDataImpl implements IProcessData {

            public String processData(String data) {
              // TODO Auto-generated method stub
          //    return null;
              try{
                
                Thread.sleep(1000l);
              }catch(Exception e){
                
                e.printStackTrace();
              }
              
              return "Flished process:"+data;
            }
            
          }
```



在spring的配置中注册服务：


```xml

        <?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans.xsd  
        http://code.alibabatech.com/schema/dubbo  
        http://code.alibabatech.com/schema/dubbo/dubbo.xsd  
        ">  
  
    <!-- Application name -->  
    <dubbo:application name="hello-world-app" />  
  
    <!-- registry address, used for service to register itself -->  
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />  
          
    <!-- Service interface   Concurrent Control  -->  
    <dubbo:service interface="com.wcb.duboo.IProcessData"  
        ref="demoService" executes="10" />  
  
    <!-- Default Protocol -->  
    <!--  
    <dubbo:protocol server="netty" />  
    -->  
  
    <!-- designate implementation -->  
    <bean id="demoService" class="com.wcb.duboo.IProcessDataImpl" />  
  
</beans> 
```

可以在main方法中启动服务：


```java

    public class MyTest {
  
    public static void main(String [] args) throws IOException{
      
      
      System.out.println("Hello dubbo!");
      
      
      ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(    
                  new String[]{"applicationProvider.xml"});    
          context.start();  
      
          System.out.println("Press any key to exit.");    
          System.in.read();  
    }

  }

```


为了模拟分布式多服务实例，再重新启动一个该服务接口的服务实例，由于默认端口20880在本机已被占用，需要修改dubbo监听端口,在配置中修改：

```xml

      <?xml version="1.0" encoding="UTF-8"?>  
<beans xmlns="http://www.springframework.org/schema/beans"  
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"  
    xsi:schemaLocation="http://www.springframework.org/schema/beans  
        http://www.springframework.org/schema/beans/spring-beans.xsd  
        http://code.alibabatech.com/schema/dubbo  
        http://code.alibabatech.com/schema/dubbo/dubbo.xsd  
        ">  
  
    <!-- Application name -->  
    <dubbo:application name="hello-world-app" />  
  
    <!-- registry address, used for service to register itself -->  
    <dubbo:registry address="zookeeper://127.0.0.1:2181" />  
  
    <!-- expose this service through dubbo protocol, through port 20880 -->  
    
    <!-- 修改端口号为20660 -->
    <dubbo:protocol name="dubbo" port="20660" />
      
          
    <!-- Service interface   Concurrent Control  -->  
    <dubbo:service interface="com.wcb.duboo.IProcessData"  
        ref="demoService" executes="10" />  
  
    <!-- Default Protocol -->  
    <!--  
    <dubbo:protocol server="netty" />  
    -->  
  
    <!-- designate implementation -->  
    <bean id="demoService" class="com.wcb.duboo.IProcessDataImpl" />  
  
</beans> 
```


启动服务实例：



```java

  public class MyTest4bak {
  
  public static void main(String [] args) throws IOException{
    
    
    System.out.println("Hello dubbo!");
    
    
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(    
                new String[]{"applicationProvider4bak.xml"});    
        context.start();  
    
        System.out.println("Press any key to exit.");    
        System.in.read();  
  }

}

```

5.消费者调用服务例子：

上面定义了两个服务，在编写消费者代码时需要加入服务端接口定义工程的依赖，然后在配置文件中向注册中心申请服务接口：


```xml

      <?xml version="1.0" encoding="UTF-8"?>  
  <beans xmlns="http://www.springframework.org/schema/beans"  
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"  
      xsi:schemaLocation="http://www.springframework.org/schema/beans  
          http://www.springframework.org/schema/beans/spring-beans.xsd  
          http://code.alibabatech.com/schema/dubbo  
          http://code.alibabatech.com/schema/dubbo/dubbo.xsd  
          ">  
    
      <!-- consumer application name -->  
      <dubbo:application name="consumer-of-sayHello-app" />  
    
      <!-- registry address, used for consumer to discover services -->  
      <dubbo:registry address="zookeeper://127.0.0.1:2181" />  
      <dubbo:consumer timeout="5000"/>  
      <!-- which service to consume? -->  
      <dubbo:reference id="helloService" interface="com.wcb.dubbo.test.SayHello" /> 
      
      <dubbo:reference id="procService" interface="com.wcb.duboo.IProcessData" /> 
  </beans> 

  
  ```

执行服务调用：



```java

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wcb.dubbo.test.SayHello;

public class ComsumeThd implements Runnable {

  public void run() {
    // TODO Auto-generated method stub
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(    
                new String[]{"applicationConsumer.xml"});    
        context.start();    
    
//        IProcessData demoService = (IProcessData) context.getBean("demoService"); // get    
        SayHello helloService = (SayHello) context.getBean("helloService"); // get service provider   
        // proxy    
        String hello = helloService.sayHello("martin Wang"); // do invoke!    
        
        // service    
        IProcessData processService = (IProcessData) context.getBean("procService"); // get    
    
    String prodata = processService.processData("martin"); // do invoke!    
    
        System.out.println(Thread.currentThread().getName() + " "+hello  +"   process: "+prodata);    
  }
  
  
  public static void main(String []args){
    
    new Thread(new ComsumeThd()).start();
  }

}

```

  运行结果如下：

  Thread-0 Hello ! martin Wang   process: Flished process:martin


  成功调用了在web项目dubboProvider中定义的sayHello接口，和在普通java项目中定义的processData接口。 其中processData运行有两个实例，就是两个服务provider，停掉任何一个该接口都能正常被调用。


  为更加直观的管理服务，可以下载安装dubbo admin,下载后解压至tomcat webapps/ROOT/ 目录下 修改dubbo.properties 的dubbo.registry.address到zookeeper地址：
  dubbo.registry.address=zookeeper://127.0.0.1:2181
  启动tomcat，如正常启动访问 localhost:port/ 会弹出如下窗口：


<img src="{{ IMAGE_PATH }}/dubbo/login.png" width="520px;">

输入用户名 root 密码 root 可登陆到dubbo admin管理界面

  

<img src="{{ IMAGE_PATH }}/dubbo/main.png" width="520px;">


点击服务治理 > 服务可以查看到发布的服务状态


<img src="{{ IMAGE_PATH }}/dubbo/services.png" width="520px;">

点击服务名称可以查看该服务的详细状态以及服务的provider，可以看到在同一机器上不同的端口部署了两个provider:



<img src="{{ IMAGE_PATH }}/dubbo/sps.png" width="520px;">

点击应用，可以查看当前的应用状态：


<img src="{{ IMAGE_PATH }}/dubbo/apps.png" width="520px;">

源码下载：
hello-provider:  http://pan.baidu.com/s/1mg7WwCK

pubinterface: http://pan.baidu.com/s/1gdrl8Nx

test:  http://pan.baidu.com/s/1kTpb6mj

github地址：git@github.com:KnightWind/dubboTest.git











