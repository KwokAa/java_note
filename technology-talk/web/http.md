## HTTP 协议

---
### 资料

* [HTTP/2到底是什么？](https://mp.weixin.qq.com/s/MNbjdnSeoeSlTwuXokaXMQ)
* [彻底弄懂 HTTP 缓存机制及原理](https://mp.weixin.qq.com/s/qnBExJ0sjVmhk8UTxAPK1Q)
* [Http中Content-Type的详解](https://blog.csdn.net/danielzhou888/article/details/72861097)


---
HTTP协议是指计算机通信网络中两台计算机之间进行通信所必须共同遵守的规定或规则，超文本传输协议(HTTP)是一种通信协议，它允许将超文本标记语言(HTML)文档从Web服务器传送到客户端的浏览器。




---
### Web组件化


###数据结构

![image](img/2.png)


### Request相关（协议参数）

* 	URL：

URL(Uniform Resource Locator) 地址用于描述一个网络上的资源,  基本格式如下

```
schema://host[:port#]/path/.../[?query-string][#anchor]
scheme               指定低层使用的协议(例如：http, https, ftp)
host                   HTTP服务器的IP地址或者域名
port#                 HTTP服务器的默认端口是80，这种情况下端口号可以省略。如果使用了别的端口，必须指明，例如 http://www.cnblogs.com:8080/
path                   访问资源的路径
query-string       发送给http服务器的数据
anchor-             锚
URL 的一个例子
http://www.mywebsite.com/sj/test/test.aspx?name=sviergn&x=true#stuff
Schema:                 http
host:                   www.mywebsite.com
path:                   /sj/test/test.aspx
Query String:           name=sviergn&x=true
Anchor:                 stuff
```


*	Accept

作用： 浏览器端可以接受的媒体类型,

```
例如：  Accept: text/html  代表浏览器可以接受服务器回发的类型为 text/html  也就是我们常说的html文档,

如果服务器无法返回text/html类型的数据,服务器应该返回一个406错误(non acceptable)

通配符 * 代表任意类型

例如  Accept: */*  代表浏览器可以处理所有类型,(一般浏览器发给服务器都是发这个)
```

*	Referer

作用： 提供了Request的上下文信息的服务器，告诉服务器我是从哪个链接过来的，比如从我主页上链接到一个朋友那里，他的服务器就能够从HTTP Referer中统计出每天有多少用户点击我主页上的链接访问他的网站。

```
例如: Referer: http://translate.google.cn/?hl=zh-cn&tab=wT
```

*	Accept-Language

作用： 浏览器申明自己接收的语言。

语言跟字符集的区别：中文是语言，中文有多种字符集，比如big5，gb2312，gbk等等；

```
例如： Accept-Language: en-us
```

*	Content-Type

作用：

```
例如：Content-Type: application/x-www-form-urlencoded
```

*	Accept-Encoding：

作用： 浏览器申明自己接收的编码方法，通常指定压缩方法，是否支持压缩，支持什么压缩方法（gzip，deflate），（注意：这不是只字符编码）;

```
例如： Accept-Encoding: gzip, deflate
```

*	User-Agent

作用：告诉HTTP服务器， 客户端使用的操作系统和浏览器的名称和版本.
我们上网登陆论坛的时候，往往会看到一些欢迎信息，其中列出了你的操作系统的名称和版本，你所使用的浏览器的名称和版本，这往往让很多人感到很神奇，实际上，服务器应用程序就是从User-Agent这个请求报头域中获取到这些信息User-Agent请求报头域允许客户端将它的操作系统、浏览器和其它属性告诉服务器。

```
例如： User-Agent: Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C; InfoPath.2; .NET4.0E)
```

*	Connection

```
例如：　Connection: keep-alive   当一个网页打开完成后，客户端和服务器之间用于传输HTTP数据的TCP连接不会关闭，如果客户端再次访问这个服务器上的网页，会继续使用这一条已经建立的连接

例如：  Connection: close  代表一个Request完成后，客户端和服务器之间用于传输HTTP数据的TCP连接会关闭， 当客户端再次发送Request，需要重新建立TCP连接。
```

*	Content-Length

作用：发送给HTTP服务器数据的长度

```
例如： Content-Length: 38
```

*	Pragma

作用： 防止页面被缓存， 在HTTP/1.1版本中，它和Cache-Control:no-cache作用一模一样


*	Cookie:

作用： 最重要的header, 将cookie的值发送给HTTP 服务器

*	Accept-Charset

作用：浏览器申明自己接收的字符集，如gb2312，utf-8（通常我们说Charset包括了相应的字符编码方案）；

*	If-Modified-Since

作用： 把浏览器端缓存页面的最后修改时间发送到服务器去，服务器会把这个时间与服务器上实际文件的最后修改时间进行对比。如果时间一致，那么返回304，客户端就直接使用本地缓存文件。如果时间不一致，就会返回200和新的文件内容。客户端接到之后，会丢弃旧文件，把新文件缓存起来，并显示在浏览器中.

*	If-None-Match

作用: If-None-Match和ETag一起工作，工作原理是在HTTP Response中添加ETag信息。 当用户再次请求该资源时，将在HTTP Request 中加入If-None-Match信息(ETag的值)。如果服务器验证资源的ETag没有改变（该资源没有更新），将返回一个304状态告诉客户端使用本地缓存文件。否则将返回200状态和新的资源和Etag。使用这样的机制将提高网站的性能。

---

### Response相关（协议参数）

*	Cache-Control

作用: 这个是非常重要的规则。 这个用来指定Response-Request遵循的缓存机制。各个指令含义如下

```
Cache-Control:Public   响应被缓存，并且在多用户间共享
Cache-Control:Private     响应只能作为私有缓存，不能在用户之间共享
Cache-Control:no-cache  提醒浏览器要从服务器提取文档进行验证
Cache-Control:no-store	 绝对禁止缓存（用于机密，敏感文件）
Cache-Control: max-age=60	60秒之后缓存过期（相对时间）
```

*	Content-Type

作用：WEB服务器告诉浏览器自己响应的对象的类型和字符集,

```
Content-Type: text/html; charset=utf-8
Content-Type:text/html;charset=GB2312
Content-Type: image/jpeg
```

*	Expires

作用: 浏览器会在指定过期时间内使用本地缓存

```
Expires: Tue, 08 Feb 2022 11:35:14 GMT
```


*	Last-Modified:

作用： 表示资源最后修改时间

```
例如: Last-Modified: Wed, 21 Dec 2011 09:09:10 GMT
```

* Etag

主要为了解决Last-Modified无法解决的一些问题，它比Last_Modified更加精确的知道文件是否被修改过。如果一个文件修改非常频繁，比如1秒内修改了10次，If-Modified-Since只能检查到秒级别的修改。

*	Server:

作用：指明HTTP服务器的软件信息

```
例如:Server: Microsoft-IIS/7.5
```

*	Connection

```
例如：　Connection: keep-alive   当一个网页打开完成后，客户端和服务器之间用于传输HTTP数据的TCP连接不会关闭，如果客户端再次访问这个服务器上的网页，会继续使用这一条已经建立的连接

例如：  Connection: close  代表一个Request完成后，客户端和服务器之间用于传输HTTP数据的TCP连接会关闭， 当客户端再次发送Request，需要重新建立TCP连接。
```

*	Content-Length

指明实体正文的长度，以字节方式存储的十进制数字来表示。在数据下行的过程中，Content-Length的方式要预先在服务器中缓存所有数据，然后所有数据再一股脑儿地发给客户端。

```
例如: Content-Length: 19847
```

###压缩过程




```