package com.virjar.vscrawler.web.api;

import org.springframework.web.context.WebApplicationContext;

/**
 * Created by virjar on 2018/2/3.<br>
 */
public interface SpringContextAware {
    void init4SpringContext(WebApplicationContext webApplicationContext);
}
