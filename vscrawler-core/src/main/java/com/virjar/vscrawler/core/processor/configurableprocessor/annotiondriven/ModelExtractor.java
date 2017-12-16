package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;

import com.alibaba.fastjson.JSON;
import com.virjar.sipsoup.util.ObjectFactory;
import com.virjar.vscrawler.core.processor.CrawlResult;
import com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven.annotation.*;
import com.virjar.vscrawler.core.seed.Seed;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

/**
 * Created by virjar on 2017/12/13.<br/>
 * 数据抽取器,能够对一个class抽取model下面的各种属性
 *
 * @author virjar
 * @since 0.2.1
 */
@Slf4j
class ModelExtractor {
    private Class<? extends AbstractAutoProcessModel> aClass;
    private FetchTaskProcessor fetchTaskProcessor;
    private ModelSelector rootSelector;
    private AnnotationProcessorBuilder annotationProcessorBuilder;


    ModelExtractor(Class<? extends AbstractAutoProcessModel> aClass, AnnotationProcessorBuilder annotationProcessorBuilder) {
        this.aClass = aClass;
        this.annotationProcessorBuilder = annotationProcessorBuilder;
    }

    public void init() {
        judgeRender(annotationProcessorBuilder);
        judgeRootSelector();
    }


    private void judgeRootSelector() {
        FetchChain fetchChain = aClass.getAnnotation(FetchChain.class);
        if (fetchChain == null || StringUtils.isBlank(fetchChain.value())) {
            rootSelector = new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable;
                }
            };
            return;
        }
        rootSelector = ChainRuleParser.parse(fetchChain.value());
    }

    private void judgeRender(AnnotationProcessorBuilder annotationProcessorBuilder) {
        fetchTaskProcessor = new FetchTaskProcessor(annotationProcessorBuilder);

        Class clazz = aClass;
        do {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                FetchTaskBean fetchTaskBean = matchFetchTask(field);
                if (fetchTaskBean != null) {
                    if (!Modifier.isPublic(field.getModifiers())) {
                        field.setAccessible(true);
                    }
                    fetchTaskProcessor.registerTask(fetchTaskBean);
                }
            }
        } while ((clazz = clazz.getSuperclass()) != null);
    }

    private FetchTaskBean matchFetchTask(Field field) {

        boolean newSeed = field.getAnnotation(NewSeed.class) != null;

        final JSONPath jsonPath = field.getAnnotation(JSONPath.class);
        if (jsonPath != null) {
            if (StringUtils.isBlank(jsonPath.value())) {
                log.warn("jsonPath annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.create("jsonpath", jsonPath.value()), newSeed);
            }
        }

        final CSS css = field.getAnnotation(CSS.class);
        if (css != null) {
            if (StringUtils.isBlank(css.value())) {
                log.warn("css annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.create("css", css.value()), newSeed);
            }
        }

        final Xpath xpath = field.getAnnotation(Xpath.class);
        if (xpath != null) {
            if (StringUtils.isBlank(xpath.value())) {
                log.warn("xpath annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.create("xpath", xpath.value()), newSeed);
            }
        }
        final Regex regex = field.getAnnotation(Regex.class);
        if (regex != null) {
            if (StringUtils.isBlank(regex.value())) {
                log.warn("regex annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.create("regex", regex.value() + "," + regex.value()), newSeed);
            }
        }

        StringRule stringRule = field.getAnnotation(StringRule.class);
        if (stringRule != null) {
            if (StringUtils.isBlank(stringRule.value())) {
                log.warn("stringRule annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.create("stringrule", stringRule.value()), newSeed);
            }
        }

        FetchChain fetchChain = field.getAnnotation(FetchChain.class);
        if (fetchChain != null) {
            if (StringUtils.isBlank(fetchChain.value())) {
                log.warn("fetchChain annotation is empty for class :{} for field:{}", aClass.getName(), field.getName());
            } else {
                return new FetchTaskBean(field, ChainRuleParser.parse(fetchChain.value()), newSeed);
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public void process(Seed seed, String content, CrawlResult crawlResult, AbstractAutoProcessModel model, AbstractSelectable abstractSelectable, boolean save) {
        String url = model.getBaseUrl();
        if (StringUtils.isBlank(url)) {
            try {
                new URI(seed.getData());
                url = seed.getData();
                model.setBaseUrl(url);
            } catch (Exception e) {
                //ignore
            }
        }
        AbstractSelectable baseSelectable = abstractSelectable;
        if (baseSelectable == null) {
            baseSelectable = AbstractSelectable.createModel(url, content);
        }

        Iterator<AbstractSelectable> iterator = rootSelector.select(baseSelectable).toMultiSelectable().iterator();
        if (!iterator.hasNext()) {
            return;
        }
        boolean hasRetry = false;
        //支持单一网页抽取多个模型
        while (true) {
            AbstractSelectable next = iterator.next();
            model.setRawText(content);
            model.setOriginSelectable(next);
            model.setSeed(seed);
            model.setBaseUrl(url);
            if (!model.hasGrabSuccess()) {
                if (!hasRetry) {
                    seed.retry();
                    hasRetry = true;
                }
                continue;
            }
            model.beforeAutoFetch();
            List<Seed> newSeeds = fetchTaskProcessor.injectField(model, next, crawlResult, false);
            model.afterAutoFetch();
            newSeeds.addAll(model.newSeeds());

            crawlResult.addSeeds(newSeeds);
            if (save) {
                String resultString = JSON.toJSONString(model);
                if (!"{}".equals(resultString)) {//fastjson,如果数据为空,可以不序列化
                    crawlResult.addResult(resultString);
                }
            }
            if (iterator.hasNext()) {
                model = ObjectFactory.newInstance(aClass);
            } else {
                break;
            }
        }
    }
}
