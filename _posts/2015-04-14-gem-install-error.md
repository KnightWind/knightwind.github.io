---
layout: post
title: "gem install ECONNRESET 问题"
keywords: ["gem", "ruby"]
description: "gem install ECONNRESET 问题解决方法"
category: "ruby"
tags: ["gem", "ruby"]
---
{% include JB/setup %}

在执行gem install的时候出现如下错误：
ERROR:  While executing gem ... (Gem::RemoteFetcher::FetchError)
    Errno::ECONNRESET: Connection reset by peer - SSL_connect (https://api.rubygems.org/quick/Marshal.4.8/jekyll-2.5.3.gemspec.rz)

那么有可能是ruby sources 被墙了，so  你需要设置你的sources为国内资源比如淘宝。
执行以下操作：

```
$ gem sources --remove https://rubygems.org/
$ gem sources -a https://ruby.taobao.org/
$ gem sources -l
*** CURRENT SOURCES ***

https://ruby.taobao.org
# 请确保只有 ruby.taobao.org
$ gem install rails
```
