package com.virjar.vscrawler;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;

/**
 * Created by virjar on 17/5/4.<br/>
 * context,存放上下文信息,多组件数据共享。这个在0.2.x上面实现,他的实现代表多站点爬虫开始实现
 * 
 * @author virjar
 * @since 0.0.1
 */
public class VSCrawlerContext {

    public static void main(String[] args) {
        FileSystem aDefault = FileSystems.getDefault();
        System.out.println(aDefault);
    }
}
