package com.virjar.vscrawler.core.selector.combine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.XpathEvaluator;
import com.virjar.sipsoup.parse.XpathParser;
import com.virjar.vscrawler.core.selector.combine.convert.Converters;
import com.virjar.vscrawler.core.selector.combine.selectables.JsonNode;
import com.virjar.vscrawler.core.selector.combine.selectables.RawNode;
import com.virjar.vscrawler.core.selector.combine.selectables.StringNode;
import com.virjar.vscrawler.core.selector.combine.selectables.XpathNode;
import com.virjar.vscrawler.core.selector.string.FunctionParser;
import com.virjar.vscrawler.core.selector.string.StingEvaluator;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by virjar on 17/6/30.
 */
@Slf4j
public abstract class AbstractSelectable<M> {

    @Getter
    private String rawText;
    @Getter
    private String baseUrl;

    public AbstractSelectable(String rawText) {
        this(null, rawText);
    }

    public AbstractSelectable(String baseUrl, String rawText) {
        this.baseUrl = baseUrl;
        this.rawText = rawText;
    }

    @Setter
    protected M model;

    public abstract M createOrGetModel();

    protected <T extends AbstractSelectable> T covert(Class<T> mClass) {
        return Converters.findConvert(getClass(), mClass).convert(this);
    }

    public XpathNode xpath(String xpathStr) {
        return xpath(XpathParser.compileNoError(xpathStr));
    }

    /**
     * xpath抽取
     * 
     * @param xpathEvaluator xpath表达式模型
     * @return xpath抽取结果
     */
    public XpathNode xpath(XpathEvaluator xpathEvaluator) {
        List<SIPNode> sipNodes = xpathEvaluator.evaluate(covert(XpathNode.class).createOrGetModel());

        // TODO rawText
        XpathNode xpathNode = new XpathNode(getBaseUrl(), getRawText());
        xpathNode.setModel(sipNodes);
        return xpathNode;
    }

    public XpathNode css(String css) {
        XpathNode xpathNode = new XpathNode(getBaseUrl(), null);
        List<SIPNode> newModels = Lists.newLinkedList();
        for (SIPNode sipNode : covert(XpathNode.class).createOrGetModel()) {
            if (sipNode.isText()) {
                continue;
            }
            for (Element el : sipNode.getElement().select(css)) {
                newModels.add(SIPNode.e(el));
            }
        }
        xpathNode.setModel(newModels);
        return xpathNode;
    }

    public JsonNode jsonPath(final JSONPath jsonPath) {
        // 转化为json
        JsonNode fromNode = covert(JsonNode.class);

        List<JSON> result = Lists.transform(fromNode.createOrGetModel(), new Function<JSON, JSON>() {
            @Override
            public JSON apply(JSON input) {
                return (JSON) jsonPath.eval(input);
            }
        });

        JsonNode jsonNode = new JsonNode(getBaseUrl(), null);
        jsonNode.setModel(result);
        return jsonNode;
    }

    public AbstractSelectable jsonPath(String jsonPathStr) {
        // FastJson 内部会缓存1024个规则,所以本身应该也会有缓存,对于JsonPath的规则缓存问题,可以先不用考虑了
        return jsonPath(JSONPath.compile(jsonPathStr));
    }


    public StringNode stringRule(StingEvaluator stingEvaluator) {
        StringNode from = covert(StringNode.class);
        List<String> evaluate = stingEvaluator.evaluate(from.createOrGetModel(), from.getBaseUrl());
        StringNode newNode = new StringNode(getBaseUrl(), null);
        newNode.setModel(evaluate);
        return newNode;
    }

    public StringNode stringRule(String stringFunction) {
        return stringRule(new StingEvaluator(new FunctionParser(stringFunction).parse()));
    }

    public StringNode regex(String regex, int group) {
        return stringRule("regex(" + StringUtils.wrap(regex, "\"") + "," + group + ")");
    }

    public static AbstractSelectable createModel(String baseUrl, String rawText) {
        return new RawNode(baseUrl, rawText);
    }
}
