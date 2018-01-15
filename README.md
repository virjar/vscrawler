# 看文档,不吹逼,不被批
[http://vscrawler.virjar.com/](http://vscrawler.virjar.com/)
[http://vscrawler.scumall.com/](http://vscrawler.scumall.com/)

## maven坐标
```
<dependency>
      <groupId>com.virjar</groupId>
      <artifactId>vscrawler-core</artifactId>
      <version>0.2.4</version>
</dependency>
```



## QQ群：

569543649(VS系列统一交流群，目前包含DungProxy，VSCrawler，SipSoup三个项目)

### history

1. 0.0.x vs基础结构确定
2. 0.1.x 对数据抽取进行完整设计,包括xpath、链式抽取、字符串函数集、表格数据定位
3. 0.2.0 支持运行多个爬虫实例(context隔离)
4. 0.2.1 支持注解爬虫
5. 0.2.2 完整设计爬虫资源队列管理,处理资源的分发、封禁、解禁、多机器分发隔离,用来处理账号、设备号、token等可复用资源
6. 0.2.3-4 同步抓取接口,关于超时时间的各种优化(同步抓取系统需要实时性),自此vscrawler在离线抓取和在线抓取上面都得到了工程性的验证
