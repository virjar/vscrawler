# 抓取平台api接口定义

## 1.爬虫操作相关

### 1.1 在线添加一个爬虫
api: ``/crawler/uploadJar``  
method:``post``  
param:``file``  
response:  
desc:``上传一个文件,文件参数名字为file``  

### 1.2 启动一个爬虫
api: ``/crawler/startCrawler``  
method:``post|get``  
param:``crawlerName``  
response:  
desc:``crawlerName是爬虫的id``  

### 1.3 停止一个爬虫
api: ``/crawler/stopCrawler``  
method:``post|get``  
param:``crawlerName``  
response:  
desc:``crawlerName是爬虫的id``  

### 1.3  查看所有爬虫状态
api: ``/crawler/crawlerStatus``  
method:``post|get``  
param:``none``  
response:
```
{
	"status": 0,
	"message": "success",
	"data": [{
		"crawlerName": "giteeProjectCrawler",
		"jarPath": "/Users/virjar/git/vscrawler/vscrawler-web/target/vscrawler-web/WEB-INF/vscrawler_hot_jar/vscrawler-web-jar-sample-0.3.0-SNAPSHOT.jar",
		"reloadAble": true,
		"status": "初始化",
		"activeThreadNumber": 0,
		"activeSessionNumber": 0,
		"totalSeed": 0,
		"finishedSeed": 0
	}]
}
```
desc:``crawlerName是爬虫的id``

## 2.资源操作接口
### 2.1 重新加载账号数据
api: ``/resource/reloadAccount``  
method:``post|get``  
param:``crawlerName``  
response:  
desc:``crawlerName是爬虫的id``  

### 2.2 重新加载资源数据
api: ``/resource/reloadAccount``  
method:``post|get``  
param:``crawlerName&resourceName``  
response:  
desc:``crawlerName是爬虫的id;resourceName是该爬虫所关联的资源名字``  


### 2.3 查看资源状态
api: ``/resource/resourceStatus``  
method:``post|get``  
param:``crawlerName&resourceName``  
response:
```
{
	"status": 0,
	"message": "success",
	"data": {
		"pollingQueue": [{
			"tag": "tag",
			"key": "",
			"data": "the data",
			"score": 0.5343,
			"status": 0,
			"validTimeStamp": 149004546546
		}],
		"leaveQueue": [],
		"forbiddenQueue": []
	}
}
```  
desc:``crawlerName是爬虫的id;resourceName是该爬虫所关联的资源名字``  

## 3 抓取测试,(备注,请求内容是json  ``application/json;charset=UTF-8``)
api: ``/grab``  
method:``post``  
param:
```
{
  "crawlerName": "giteeProjectCrawler",
  "crawlerVersion": "",
  "queryPage": "comfort",
  "param": "spider"
}

```
response:
```
{
    "status": 0,
    "message": "success",
    "data": [
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "l-weiwei",
            "projectUrl": "https://gitee.com/l-weiwei/spiderman",
            "authorNickName": "自风/Spiderman",
            "watchers": "1.1k",
            "stargazer": "2.3k",
            "forks": "973",
            "projectDesc": "强力 Java 爬虫，列表分页、详细页分页、ajax、微内核高扩展、配置灵活",
            "meta": [
                "Java",
                "Web爬虫"
            ],
            "timeago": "2年 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "flashsword20",
            "projectUrl": "https://gitee.com/flashsword20/webmagic",
            "authorNickName": "黄亿华/webmagic",
            "watchers": "822",
            "stargazer": "2k",
            "forks": "837",
            "projectDesc": "webmagic 是一个无须配置、便于二次开发的爬虫框架，它提供简单灵活的API，只需少量代码即可实现一个爬虫。",
            "meta": [
                "Java",
                "Web爬虫",
                "Apache-2.0"
            ],
            "timeago": "5个月 前",
            "recommend": false,
            "gvp": true
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "xiyouMc",
            "projectUrl": "https://gitee.com/xiyouMc/pornhubbot",
            "authorNickName": "xiyouMc/PornHubBot",
            "watchers": "460",
            "stargazer": "1.2k",
            "forks": "371",
            "projectDesc": "全球最大成人网站PornHub爬虫 （Scrapy、MongoDB） 一天500w的数据",
            "meta": [
                "Python",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "3个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "xtuhcy",
            "projectUrl": "https://gitee.com/xtuhcy/gecco",
            "authorNickName": "xtuhcy/Gecco",
            "watchers": "386",
            "stargazer": "942",
            "forks": "308",
            "projectDesc": "Gecco 是一款用java语言开发的轻量化的易用的网络爬虫，整合了jsoup、httpclient、fastjson、spring、htmlunit、redission等优秀框架。",
            "meta": [
                "Java",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "6个月 前",
            "recommend": false,
            "gvp": true
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "shentong_012",
            "projectUrl": "https://gitee.com/shentong_012/YayCrawler",
            "authorNickName": "代码神童/YayCrawler",
            "watchers": "429",
            "stargazer": "887",
            "forks": "408",
            "projectDesc": "分布式爬虫系统，简单使用，高级配置。可扩展，减轻开发量，能docker化，适应各种急切需求核心框架：WebMagic, Spring Boot ，MongoDB, ActiveMQ ,Spring + Quartz，Spring Jpa ， Druid，Redis， Ehcache ，SLF4J、Log4j2， Bootstrap + Jquery 等，不详细列举了",
            "meta": [
                "Java",
                "Web爬虫",
                "LGPL-3.0"
            ],
            "timeago": "5个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "l-weiwei",
            "projectUrl": "https://gitee.com/l-weiwei/Spiderman2",
            "authorNickName": "自风/Spiderman2",
            "watchers": "377",
            "stargazer": "814",
            "forks": "302",
            "projectDesc": "二代蜘蛛侠，此版本完全重新开发，比上一代更加强大（性能，易用，架构，分布式，简洁，成熟）",
            "meta": [
                "Java",
                "Web爬虫",
                "Apache-2.0"
            ],
            "timeago": "6个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "mail_osc",
            "projectUrl": "https://gitee.com/mail_osc/templatespider",
            "authorNickName": "鬼画符/templatespider",
            "watchers": "201",
            "stargazer": "661",
            "forks": "112",
            "projectDesc": "扒网站工具，看好哪个网站，指定好URL，自动扒下来做成模版。所见网站，皆可为我所用！",
            "meta": [
                "Java",
                "Web爬虫",
                "Apache-2.0"
            ],
            "timeago": "3天 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "dreamidea",
            "projectUrl": "https://gitee.com/dreamidea/neocrawler",
            "authorNickName": "水熊宝宝/neocrawler",
            "watchers": "125",
            "stargazer": "353",
            "forks": "180",
            "projectDesc": "牛咖-neocrawler nodejs 的爬虫系统。 特点： 支持web界面方式的摘取规则配置（css selector & regex）； 包含无界面的浏览器引擎（phantomjs），支持js产生内容的抓取； 用http代理路由的方式防止抓取并发量过大的情况下被对方屏蔽； nodejs none-block 异步环境下的抓取性能比较高； 中央调度器负责网址的调度（同一时间片内一定数量的抓取任务中根据网站的权重来决定派发任务量； 支持多种抓取实例并存，定制摘取引擎和存储方式。",
            "meta": [
                "NodeJS",
                "Web爬虫",
                "BSD-3-Clause"
            ],
            "timeago": "11个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "zongtui",
            "projectUrl": "https://gitee.com/zongtui/zongtui-webcrawler",
            "authorNickName": "cloudskyme/zongtui-webcrawler",
            "watchers": "208",
            "stargazer": "335",
            "forks": "233",
            "projectDesc": "基于hadoop思维的分布式网络爬虫。",
            "meta": [
                "Java",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "接近2年 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "tigerxue",
            "projectUrl": "https://gitee.com/liinux/ghost-login",
            "authorNickName": "liinux/ghost-login",
            "watchers": "162",
            "stargazer": "293",
            "forks": "117",
            "projectDesc": "专门用来解决爬虫采集相关网站数据时模拟自动登录，验证码自动识别的问题；欢迎加入一起开发完善。",
            "meta": [
                "Java",
                "Web爬虫"
            ],
            "timeago": "16天 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "JIANGWL",
            "projectUrl": "https://gitee.com/JIANGWL/ZhihuSpider",
            "authorNickName": "JIANGWL/ZhihuSpider",
            "watchers": "123",
            "stargazer": "280",
            "forks": "101",
            "projectDesc": "多线程知乎用户爬虫，基于python3",
            "meta": [
                "Python",
                "Web爬虫"
            ],
            "timeago": "4个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "congqian",
            "projectUrl": "https://gitee.com/congqian/DenseSpider",
            "authorNickName": "从前/DenseSpider",
            "watchers": "110",
            "stargazer": "251",
            "forks": "81",
            "projectDesc": "Go语言实现的高性能爬虫，基于go_spider开发。实现了单机并发采集，深度遍历，自定义深度层级等特性。",
            "meta": [
                "Go",
                "Web爬虫",
                "MPL-2.0"
            ],
            "timeago": "接近3年 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "wycm",
            "projectUrl": "https://gitee.com/wycm/zhihu-crawler",
            "authorNickName": "wycm/zhihu-crawler",
            "watchers": "56",
            "stargazer": "246",
            "forks": "95",
            "projectDesc": "zhihu-crawler是一个基于Java的爬虫实战项目，主要功能是抓取知乎用户的基本资料。",
            "meta": [
                "Java",
                "Web爬虫",
                "Apache-2.0"
            ],
            "timeago": "6个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "mktime",
            "projectUrl": "https://gitee.com/mktime/scrapy-douban-group",
            "authorNickName": "mktime/scrapy-douban-group",
            "watchers": "98",
            "stargazer": "195",
            "forks": "73",
            "projectDesc": "通过一个实际的项目，来学习如何使用scrapy爬取网络上的信息。这里以豆瓣小组为例，对组内的图片进行爬取，相关信息保存数据到MongoDB，图片下载到本地。",
            "meta": [
                "Python",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "2个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "zhazhapan_admin",
            "projectUrl": "https://gitee.com/zhazhapan_admin/visual-spider",
            "authorNickName": "FuckTheCode/visual-spider",
            "watchers": "70",
            "stargazer": "184",
            "forks": "27",
            "projectDesc": "用JavaFX开发基于crawler4j的图形化的网络爬虫",
            "meta": [
                "Java",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "24天 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "webcollector",
            "projectUrl": "https://gitee.com/webcollector/WebCollector",
            "authorNickName": "CrawlScript/WebCollector",
            "watchers": "95",
            "stargazer": "181",
            "forks": "139",
            "projectDesc": "A java crawler for infomation collection.",
            "meta": [
                "Java",
                "Web爬虫",
                "GPL-3.0"
            ],
            "timeago": "11个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "ecitlm",
            "projectUrl": "https://gitee.com/ecitlm/splider",
            "authorNickName": "ecitlm/Node-SpliderApi",
            "watchers": "49",
            "stargazer": "177",
            "forks": "50",
            "projectDesc": "基于nodejs 的爬虫 API接口项目,包括前端开发日报、知乎日报、前端top框架排行、妹纸福利、搞笑视频、各类视频新闻资讯 热点详情接口数据",
            "meta": [
                "NodeJS",
                "Web爬虫",
                "GPL-2.0"
            ],
            "timeago": "15天 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "smiledog",
            "projectUrl": "https://gitee.com/smiledog/SuprCrawl",
            "authorNickName": "夏末丶冬初/SuprCrawl",
            "watchers": "92",
            "stargazer": "171",
            "forks": "86",
            "projectDesc": "一个基于Spring+SpringMVC+Mybatis+webmagic+extjs开发的段子图片抓取平台",
            "meta": [
                "Java",
                "Web爬虫",
                "Apache-2.0"
            ],
            "timeago": "2年多 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "bilibala",
            "projectUrl": "https://gitee.com/bilibala/YiSpider",
            "authorNickName": "bilibala/YiSpider",
            "watchers": "56",
            "stargazer": "154",
            "forks": "40",
            "projectDesc": "一款分布式爬虫平台，帮助你更好的管理和开发爬虫。 内置一套爬虫定义规则（模版），可使用模版快速定义爬虫，也可当作框架手动开发爬虫",
            "meta": [
                "Go",
                "Web爬虫"
            ],
            "timeago": "5个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "kidd_yu",
            "projectUrl": "https://gitee.com/kidd_yu/beanbun",
            "authorNickName": "Kiddyu/Beanbun",
            "watchers": "45",
            "stargazer": "143",
            "forks": "38",
            "projectDesc": "Beanbun 是用 PHP 编写的多进程网络爬虫框架，支持分布式，具有良好的开放性、高可扩展性，基于 Workerman。",
            "meta": [
                "PHP",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "6个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "resolvewang",
            "projectUrl": "https://gitee.com/resolvewang/WeiboSpider",
            "authorNickName": "resolvewang/WeiboSpider",
            "watchers": "72",
            "stargazer": "136",
            "forks": "36",
            "projectDesc": "分布式微博爬虫。抓取内容包括微博用户资料、微博信息、评论信息和转发信息。目前专注于微博数据抓取本身，正在快速迭代。如果觉得有帮助，不妨到github上给我点个star，osc上可能不会再继续更新了",
            "meta": [
                "Python",
                "Web爬虫",
                "MIT"
            ],
            "timeago": "8个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "aming0502",
            "projectUrl": "https://gitee.com/aming0502/ebookapp",
            "authorNickName": "aming/ebookapp",
            "watchers": "69",
            "stargazer": "136",
            "forks": "65",
            "projectDesc": "基于bootstrap的php小说采集系统",
            "meta": [
                "PHP",
                "Web爬虫"
            ],
            "timeago": "1年 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "spirit_demon",
            "projectUrl": "https://gitee.com/spirit_demon/CrawlerDemon",
            "authorNickName": "itlabers/CrawlerDemon",
            "watchers": "77",
            "stargazer": "133",
            "forks": "70",
            "projectDesc": "分布式爬虫 Crawler",
            "meta": [
                "Java",
                "Web爬虫",
                "LGPL-3.0"
            ],
            "timeago": "1年 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "Luciferearth",
            "projectUrl": "https://gitee.com/Luciferearth/FaceSpider",
            "authorNickName": "Pulsar-V/FaceSpider",
            "watchers": "53",
            "stargazer": "131",
            "forks": "46",
            "projectDesc": "人脸识别爬虫",
            "meta": [
                "Java",
                "Web爬虫",
                "EPL-1.0"
            ],
            "timeago": "4个月 前",
            "recommend": true,
            "gvp": false
        },
        {
            "baseUrl": "https://gitee.com/explore/starred/spider",
            "avatar": null,
            "authorId": "luosl",
            "projectUrl": "https://gitee.com/luosl/webmagicx",
            "authorNickName": "sameLuo/webmagicx",
            "watchers": "50",
            "stargazer": "130",
            "forks": "29",
            "projectDesc": "webmagicx一款基于webmagic的可配置化的爬虫框架",
            "meta": [
                "Scala",
                "Web爬虫"
            ],
            "timeago": "1个月 前",
            "recommend": true,
            "gvp": false
        }
    ]
}
```