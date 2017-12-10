package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.google.common.base.Preconditions;
import com.virjar.sipsoup.util.ObjectFactory;
import com.virjar.vscrawler.core.net.session.CrawlerSession;
import com.virjar.vscrawler.core.processor.BindRouteProcessor;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.*;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import com.virjar.vscrawler.core.selector.combine.Selector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.regex.Pattern;

/**
 * Created by virjar on 2017/12/10.<br/>基于注解的处理器
 */
@Slf4j
public class AnnotationProcessor<T> implements BindRouteProcessor {
    private Class<T> aClass;
    private MatchStrategy matchStrategy;
    private Downloader<T> downloader;
    private Render render;

    public AnnotationProcessor(Class<T> aClass) {
        this.aClass = aClass;
        judgeMatchStrategy();
        judgeDownloader();
        judgeRender();
    }

    private void judgeRender() {
        render = new Render();
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            FetchTask fetchTask = matchFetchTask(field);
            if (fetchTask != null) {
                render.regiesterTask(fetchTask);
            }
        }
    }

    private FetchTask matchFetchTask(Field field) {
        final JSONPath jsonPath = field.getAnnotation(JSONPath.class);
        if (jsonPath != null) {
            if (StringUtils.isBlank(jsonPath.value())) {
                log.warn("jsonPath annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.create("jsonpath", jsonPath.value()));
            }
        }

        final CSS css = field.getAnnotation(CSS.class);
        if (css != null) {
            if (StringUtils.isBlank(css.value())) {
                log.warn("css annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.create("css", css.value()));
            }
        }

        final Xpath xpath = field.getAnnotation(Xpath.class);
        if (xpath != null) {
            if (StringUtils.isBlank(xpath.value())) {
                log.warn("xpath annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.create("xpath", xpath.value()));
            }
        }
        final Regex regex = field.getAnnotation(Regex.class);
        if (regex != null) {
            if (StringUtils.isBlank(regex.value())) {
                log.warn("regex annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.create("regex", regex.value() + "," + regex.value()));
            }
        }

        StringRule stringRule = field.getAnnotation(StringRule.class);
        if (stringRule != null) {
            if (StringUtils.isBlank(stringRule.value())) {
                log.warn("stringRule annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.create("stringrule", stringRule.value()));
            }
        }

        FetchChain fetchChain = field.getAnnotation(FetchChain.class);
        if (fetchChain != null) {
            if (StringUtils.isBlank(fetchChain.value())) {
                log.warn("fetchChain annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTask(field, ChainRuleParser.parse(fetchChain.value()));
            }
        }
        return null;
    }


    private void judgeDownloader() {
        Method[] methods = aClass.getMethods();
        for (final Method method : methods) {
            if (method.getAnnotation(DownLoadMethod.class) == null) {
                continue;
            }
            Preconditions.checkArgument(String.class.isAssignableFrom(method.getReturnType()));
            Preconditions.checkArgument(method.getParameterTypes().length >= 2);
            Preconditions.checkArgument(method.getParameterTypes()[0].isAssignableFrom(Seed.class));
            Preconditions.checkArgument(method.getParameterTypes()[1].isAssignableFrom(CrawlerSession.class));

            downloader = new Downloader<T>() {
                @Override
                public String download(Seed seed, T model, CrawlerSession crawlerSession) {
                    try {
                        return (String) method.invoke(model, seed, crawlerSession);
                    } catch (Exception e) {
                        throw new RuntimeException("invoke download method :" + method.toGenericString() + " failed", e);
                    }
                }
            };
            return;
        }

        downloader = new Downloader<T>() {
            @Override
            public String download(Seed seed, T model, CrawlerSession crawlerSession) {
                return crawlerSession.getCrawlerHttpClient().get(seed.getData());
            }
        };
    }

    private void judgeMatchStrategy() {
        AutoProcessor autoProcessor = aClass.getAnnotation(AutoProcessor.class);
        Preconditions.checkNotNull(autoProcessor);
        String seedPattern = autoProcessor.seedPattern();
        if (StringUtils.isNotBlank(seedPattern)) {
            final Pattern pattern = Pattern.compile(seedPattern);
            matchStrategy = new MatchStrategy() {
                @Override
                public boolean matchSeed(Seed seed) {
                    return pattern.matcher(seed.getData()).matches();
                }
            };
            return;
        }

        Method[] methods = aClass.getMethods();
        for (final Method method : methods) {
            if (method.getAnnotation(MatchSeed.class) == null) {
                continue;
            }
            Preconditions.checkArgument(Boolean.class.isAssignableFrom(method.getReturnType()));
            Preconditions.checkArgument(Modifier.isStatic(method.getModifiers()));
            matchStrategy = new MatchStrategy() {
                @Override
                public boolean matchSeed(Seed seed) {
                    try {
                        return (Boolean) method.invoke(null, seed);
                    } catch (Exception e) {
                        throw new IllegalStateException("can not jude seed match method", e);
                    }
                }
            };
            return;
        }
        throw new IllegalStateException("auto processor must has seedPattern or matchSeed method");
    }

    @Override
    public void process(Seed seed, CrawlerSession crawlerSession, CrawlResult crawlResult) {
        //创建模型对象
        T model = ObjectFactory.newInstance(aClass);
        String content = downloader.download(seed, model, crawlerSession);
        String url = null;
        try {
            new URI(seed.getData());
            url = seed.getData();
        } catch (Exception e) {
            //ignore
        }
        AbstractSelectable baseSelectable = Selector.rawText(url, content);

    }

    @Override
    public boolean matchSeed(Seed seed) {
        return matchStrategy.matchSeed(seed);
    }

    private interface MatchStrategy {
        boolean matchSeed(Seed seed);
    }

    private interface Downloader<T> {
        String download(Seed seed, T model, CrawlerSession crawlerSession);
    }
}
