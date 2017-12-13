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
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Created by virjar on 2017/12/13.
 */
@Slf4j
public class ModelExtractor {
    private Class<? extends AbstractAutoProcessModel> aClass;
    private FetchTaskProcessor fetchTaskProcessor;
    private ModelSelector rootSelector;
    private AnnotationProcessorFactory annotationProcessorFactory;


    public ModelExtractor(Class<? extends AbstractAutoProcessModel> aClass, AnnotationProcessorFactory annotationProcessorFactory) {
        this.aClass = aClass;
        this.annotationProcessorFactory = annotationProcessorFactory;
    }

    public void init() {
        judgeRender(annotationProcessorFactory);
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

    private void judgeRender(AnnotationProcessorFactory annotationProcessorFactory) {
        fetchTaskProcessor = new FetchTaskProcessor(annotationProcessorFactory);
        Field[] fields = aClass.getFields();
        for (Field field : fields) {
            FetchTaskBean fetchTaskBean = matchFetchTask(field);
            if (fetchTaskBean != null) {
                fetchTaskProcessor.registerTask(fetchTaskBean);
            }
        }
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


    public void process(Seed seed, String content, CrawlResult crawlResult, AbstractAutoProcessModel model) {
        String url = null;
        try {
            new URI(seed.getData());
            url = seed.getData();
        } catch (Exception e) {
            //ignore
        }
        AbstractSelectable<String> baseSelectable = AbstractSelectable.createModel(url, content);
        AbstractSelectable root = rootSelector.select(baseSelectable);
        Object selectModel = root.createOrGetModel();
        if (root == baseSelectable || !(selectModel instanceof Collection)) {
            //证明模型只有一个,不是多行数据的模型
            model.setRawText(content);
            model.setOriginSelectable(baseSelectable);
            model.setSeed(seed);
            if (!model.hasGrabSuccess()) {
                seed.retry();
                return;
            }
            List<Seed> newSeeds = fetchTaskProcessor.injectField(model, root);
            model.afterAutoFetch();
            newSeeds.addAll(model.newSeeds());

            crawlResult.addSeeds(newSeeds);
            crawlResult.addResult(JSON.toJSONString(model));
            return;
        }
        List<AbstractSelectable> list = root.toMultiSelectable();
        for (AbstractSelectable abstractSelectable : list) {
            AbstractAutoProcessModel t = ObjectFactory.newInstance(aClass);
            t.setRawText(abstractSelectable.getRawText());
            t.setOriginSelectable(abstractSelectable);
            t.setSeed(seed);
            if (!t.hasGrabSuccess()) {
                seed.retry();
                return;
            }
            List<Seed> newSeeds = fetchTaskProcessor.injectField(t, root);
            t.afterAutoFetch();
            newSeeds.addAll(t.newSeeds());

            crawlResult.addSeeds(newSeeds);
            crawlResult.addResult(JSON.toJSONString(t));
        }
    }
}
