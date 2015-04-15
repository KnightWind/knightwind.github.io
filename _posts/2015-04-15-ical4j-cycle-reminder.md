---
layout: post
title: "ical4j日历操作"
keywords: ["ical4j", "修改","取消","日历"]
description: "ical4j的一些操作"
category: "technology"
tags: ["ical4j", "约会"]
---
{% include JB/setup %}

因业务需要，需要设置周期提醒的日历头以及发送约会修改和约会的取消，网上找了很久没有找到相应资料。
研究一段ical4j api还有通过抓取outlook生成的日历头发现可以这样解决：

首先需要了解一些ical4j的基本操作用例,可以参考以下链接：
http://www.ibm.com/developerworks/cn/java/j-lo-ical4j/ 

## 1. 循环提醒，如果需要实现按周或者按每个月的第几天 或者每个月的第几周的第几天则需要构建 recur 具体代码如下： 

```java
public static Recur getRecurByCycle(ConfCycle cycle){ 
  if(cycle!=null){ 
   Recur recur = new Recur(); 
   //周期信息 
   String cycleInfo = cycle.getCycleValue(); 
   String infos[] = cycleInfo.split(";"); 
   
   int index = 0 ; 
   List<Integer> indexs = new ArrayList<Integer>(); 
   int seq = 0; 
   if(infos.length == 1){ 
     String indexstr[] = infos[0].split(","); 
     for (int i = 0; i < indexstr.length; i++) { 
      indexs.add(Integer.parseInt(indexstr[i])); 
     } 
     index = indexs.get(0); 
    
   }else if(infos.length == 2){ 
    seq = Integer.parseInt(infos[0]); 
    index = Integer.parseInt(infos[1]); 
   } 
   
   //周期的类型 
   int cycleType = cycle.getCycleType().intValue(); 
   switch (cycleType) { 
//按间隔多少天循环 
   case 1: 
    recur.setFrequency(Recur.DAILY); 
    recur.setInterval(index); 
    break; 
//按每一周的周几  可以是每一周的一天或者多天 
   case 2: 
    recur.setFrequency(Recur.WEEKLY); 
    for (Integer weekday:indexs) { 
     switch (weekday) { 
     case 1: 
      recur.getDayList().add(WeekDay.SU); 
      break; 
     case 2: 
      recur.getDayList().add(WeekDay.MO); 
      break; 
     case 3: 
      recur.getDayList().add(WeekDay.TU); 
      break; 
     case 4: 
      recur.getDayList().add(WeekDay.WE); 
      break; 
     case 5: 
      recur.getDayList().add(WeekDay.TH); 
      break; 
     case 6: 
      recur.getDayList().add(WeekDay.FR); 
      break; 
     case 7: 
      recur.getDayList().add(WeekDay.SA); 
      break; 
     default: 
      throw new RuntimeException("un support week index!!!"); 
     } 
    } 
    break; 
//按月循环 可以按每个月的第几天 
   case 3: 
    recur.setFrequency(Recur.MONTHLY); 
//按每月的第几周的周几 
    if(seq != 0){ 
     switch (index) { 
     case 1: 
      recur.getDayList().add(WeekDay.SU); 
      break; 
     case 2: 
      recur.getDayList().add(WeekDay.MO); 
      break; 
     case 3: 
      recur.getDayList().add(WeekDay.TU); 
      break; 
     case 4: 
      recur.getDayList().add(WeekDay.WE); 
      break; 
     case 5: 
      recur.getDayList().add(WeekDay.TH); 
      break; 
     case 6: 
      recur.getDayList().add(WeekDay.FR); 
      break; 
     case 7: 
      recur.getDayList().add(WeekDay.SA); 
      break; 
     default: 
      throw new RuntimeException("un support week index!!!"); 
     } 
     recur.getSetPosList().add(seq); 

//按每月的第几天 
    }else{ 
     recur.getMonthDayList().add(index); 
    } 
    break; 
   default: 
    throw new RuntimeException("un know cycle type! "); 
     
   } 
   boolean unlimited = cycle.getInfiniteFlag().intValue() == 0 ?false:true; 
   //设置结束周期 
   if (!unlimited) { 
    int count = cycle.getRepeatCount(); 
    if(count>0){ 
     recur.setCount(count); 
    }else{ 
     recur.setUntil(new net.fortuna.ical4j.model.Date(cycle.getEndDate())); 
    } 
   } 
   return recur; 
  } 
  return null; 
} 
```

2.ical4j 发送日历修改，取消。
通过截取分析对比邀请约会，修改约会，取消约会的日历头

邀请约会部分日历头结构：


```
VERSION:2.0
METHOD:REQUEST
X-MS-OLK-FORCEINSPECTOROPEN:TRUE
BEGIN:VEVENT
ATTENDEE;CN=wadhis@126.com;RSVP=TRUE:mailto:wadhis@126.com
CLASS:PUBLIC
CREATED:20150415T050818Z
DESCRIPTION:时间: 2015年4月15日星期三 14:00-14:30(UTC+08:00)北京
	，重庆，香港特别行政区，乌鲁木齐。\n地点: office\n\n注
	意: 以上 GMT 时差并不反映夏令时调整。\n\n*~*~*~*~*~*~*~*~*~
	*\n\n\n
DTEND:20150415T063000Z
DTSTAMP:20150415T050818Z
DTSTART:20150415T060000Z
LAST-MODIFIED:20150415T050820Z
LOCATION:office
ORGANIZER;CN=martin_wang:mailto:martin_wang@bizconf.cn
PRIORITY:5
SEQUENCE:0
SUMMARY;LANGUAGE=zh-cn:test
TRANSP:OPAQUE
UID:040000008200E00074C5B7101A82E00800000000A05F7CEF7C77D001000000000000000
	0100000000C67D78A50237D48A85F3490794AADE6
```	


修改约会部分日历头结构：


```
METHOD:REQUEST
X-MS-OLK-FORCEINSPECTOROPEN:TRUE
BEGIN:VEVENT
ATTENDEE;CN=wadhis@126.com;RSVP=TRUE:mailto:wadhis@126.com
CLASS:PUBLIC
CREATED:20150415T050841Z
DESCRIPTION:时间: 2015年4月15日星期三 14:00-15:00(UTC+08:00)北京
	，重庆，香港特别行政区，乌鲁木齐。\n地点: office\n\n注
	意: 以上 GMT 时差并不反映夏令时调整。\n\n*~*~*~*~*~*~*~*~*~
	*\n\n\n
DTEND:20150415T070000Z
DTSTAMP:20150415T050841Z
DTSTART:20150415T060000Z
LAST-MODIFIED:20150415T050841Z
LOCATION:office
ORGANIZER;CN=martin_wang:mailto:martin_wang@bizconf.cn
PRIORITY:5
SEQUENCE:1
SUMMARY;LANGUAGE=zh-cn:test
TRANSP:OPAQUE
UID:040000008200E00074C5B7101A82E00800000000A05F7CEF7C77D001000000000000000
	0100000000C67D78A50237D48A85F3490794AADE6
```


取消约会部分日历头结构：


```
METHOD:CANCEL
X-MS-OLK-FORCEINSPECTOROPEN:TRUE
BEGIN:VEVENT
ATTENDEE;CN=wadhis@126.com;RSVP=TRUE:mailto:wadhis@126.com
CLASS:PUBLIC
CREATED:20150415T050857Z
DESCRIPTION:时间: 2015年4月15日星期三 14:00-15:00(UTC+08:00)北京
	，重庆，香港特别行政区，乌鲁木齐。\n地点: office\n\n注
	意: 以上 GMT 时差并不反映夏令时调整。\n\n*~*~*~*~*~*~*~*~*~
	*\n\n\n
DTEND:20150415T070000Z
DTSTAMP:20150415T050857Z
DTSTART:20150415T060000Z
LAST-MODIFIED:20150415T050857Z
LOCATION:office
ORGANIZER;CN=martin_wang:mailto:martin_wang@bizconf.cn
PRIORITY:1
SEQUENCE:2
SUMMARY;LANGUAGE=zh-cn:已取消: test
TRANSP:TRANSPARENT
UID:040000008200E00074C5B7101A82E00800000000A05F7CEF7C77D001000000000000000
	0100000000C67D78A50237D48A85F3490794AADE6
```

通过对比，不难发现发送更新，其实日历头结构只需要改变DTSTART（开始时间）和DTEND（结束时间）到新的时间点就行
唯一需要注意的是要保持UID一致。
取消约会稍有不同的是：METHOD:CANCEL 取消的时候METHOD属性需要修改为 CANCEL,操作代码如下：

```java
icsCalendar.getProperties().add(Method.CANCEL);
```

同样需要注意的是UID必须要和之前的邀请约会日历头保持一致


3.在Google gmail中如果想让邮件接收者直接添加日历到的日程当中需要如下设置： 

```java	
    ParameterList pls = new ParameterList(); 
    pls.add(new Cn(mailInfo.getToEmail())); 
    pls.add(new Rsvp(true)); 
    Attendee attendee = new Attendee(pls,mailInfo.getToEmail()); 
    attendee.setCalAddress(new URI("mailto:"+mailInfo.getToEmail())); 
    vevent.getProperties().add(attendee);//参会者 
```

