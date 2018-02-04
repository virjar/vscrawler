package com.virjar.vscrawler.web.crawlerloader;

import com.google.common.collect.Sets;
import com.virjar.vscrawler.core.VSCrawler;
import com.virjar.vscrawler.core.util.ReflectUtil;
import com.virjar.vscrawler.web.api.CrawlerBuilder;
import com.virjar.vscrawler.web.api.SpringContextAware;
import com.virjar.vscrawler.web.model.CrawlerBean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

/**
 * Created by virjar on 2018/2/3.<br>
 * 热加载爬虫代码
 */
@Slf4j
public class VSCrawlerClassLoader extends URLClassLoader {
    @Getter
    private File jarFile;

    public VSCrawlerClassLoader(File jarFile, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{jarFile.toURI().toURL()}, parent);
        this.jarFile = jarFile;
    }

    /**
     * @param crawlerEntryName 爬虫入口类,应该是com.virjar.vscrawler.web.crawler.CrawlerBuilder的实现类
     * @return 由入口类构造的一个爬虫对象
     * @see CrawlerBuilder
     */
    public CrawlerBean loadCrawler(String crawlerEntryName, WebApplicationContext webApplicationContext) throws InstantiationException, IllegalAccessException {
        // check
        try {
            CrawlerBuilder crawlerBuilder = (CrawlerBuilder) loadClass(crawlerEntryName).newInstance();
            if (crawlerBuilder instanceof SpringContextAware) {
                SpringContextAware springContextAware = (SpringContextAware) crawlerBuilder;
                springContextAware.init4SpringContext(webApplicationContext);
            }
            //for spring bean auto injection
            injectDependency(crawlerBuilder, true, webApplicationContext);

            VSCrawler vsCrawler = crawlerBuilder.build();
            return new CrawlerBean(vsCrawler, true, this);
        } catch (ClassNotFoundException e) {
            //this exception will not happen
        }
        return null;
    }

    private Set<Object> extensionBean = Sets.newHashSet();


    private void registerBean(Object o) {
        extensionBean.add(o);
    }

    private void injectField(Object bean, boolean registerSelf, WebApplicationContext webApplicationContext, Field field) {
        // 确认是否有值,没有则尝试注入
        Object fieldValue = ReflectUtil.getFieldValue(bean, field.getName());
        if (fieldValue != null) {
            injectDependency(fieldValue, false, webApplicationContext);
            return;
        }

        // 匹配对象
        Class<?> declaringClass = field.getType();
        //test find bean in  spring context
        fieldValue = webApplicationContext.getBean(declaringClass);
        if (fieldValue == null) {
            //test find bean in extension
            for (Object candidateBean : extensionBean) {
                if (declaringClass.isAssignableFrom(candidateBean.getClass())) {
                    fieldValue = candidateBean;
                    break;
                }
            }
        }
        if (fieldValue != null) {
            ReflectUtil.setFieldValue(bean, field.getName(), fieldValue);
        }
        fieldValue = ReflectUtil.getFieldValue(bean, field.getName());
        if (fieldValue == null) {// 如果还是为空,那么创建一个,然后注入
            try {
                fieldValue = declaringClass.newInstance();
                injectDependency(fieldValue, registerSelf, webApplicationContext);// 循环注入
                // extensionBean.add(fieldValue);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("bean:{} 注入依赖:{} 失败", bean, field.getName(), e);
                }
            }
        }
        if (fieldValue != null) {
            ReflectUtil.setFieldValue(bean, field.getName(), fieldValue);
        }
    }

    /**
     * 对某个对象实现自动注入
     *
     * @param bean 本处理对象
     */
    private void injectDependency(Object bean, boolean registerSelf, WebApplicationContext webApplicationContext) {
        if (registerSelf) {
            registerBean(bean);
        }
        Class clazz = bean.getClass();
        while (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getAnnotation(Resource.class) != null || field.getAnnotation(Autowired.class) != null) {
                    injectField(bean, registerSelf, webApplicationContext, field);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }
}
