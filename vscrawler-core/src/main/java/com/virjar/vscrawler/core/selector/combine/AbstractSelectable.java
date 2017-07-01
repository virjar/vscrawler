package com.virjar.vscrawler.core.selector.combine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.Lists;
import com.virjar.sipsoup.model.SIPNode;
import com.virjar.sipsoup.model.XpathEvaluator;
import com.virjar.sipsoup.parse.TokenQueue;
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

    public AbstractSelectable jsonPath(JSONPath jsonPath) {
        // 转化为json
        JsonNode fromNode = covert(JsonNode.class);
        Object result = jsonPath.eval(fromNode.createOrGetModel());

        // jsonPath的抽取结果类型不定,需要做转化适配
        if (result == null) {
            return new RawNode(getBaseUrl(), "");
        }

        // 抽取结果还是json,仍然使用json的模型
        if (result instanceof JSON) {
            JSON jsonResult = (JSON) result;
            JsonNode jsonNode = new JsonNode(getBaseUrl(), null);
            jsonNode.setModel(jsonResult);
            return jsonNode;
        }

        // 抽取结果是列表
        if (result instanceof List) {
            List listResult = (List) result;
            if (listResult.size() == 0) {
                return new RawNode(getBaseUrl(), "");
            }

            Object first = listResult.get(0);
            if (first instanceof String) {
                StringNode stringNode = new StringNode(getBaseUrl(), null);
                List<String> data = Lists.newArrayListWithExpectedSize(listResult.size());
                for (Object aListResult : listResult) {
                    data.add(aListResult.toString());
                }
                stringNode.setModel(data);
                return stringNode;
            }
        }
        log.warn("can not convert json eval result:" + result);
        return new RawNode(getBaseUrl(), null);
    }

    public AbstractSelectable jsonPath(String jsonPathStr) {
        // FastJson 内部会缓存1024个规则,所以本身应该也会有缓存,对于JsonPath的规则缓存问题,可以先不用考虑了
        return jsonPath(JSONPath.compile(jsonPathStr));
    }

    // 当你确定抽取结果就是字符串的时候,具体试表达式而定,如果随意调用此方法,可能引发异常
    public StringNode toStringNode() {
        return (StringNode) this;
    }

    public StringNode stringRule(StingEvaluator stingEvaluator) {
        StringNode from = covert(StringNode.class);
        List<String> evaluate = stingEvaluator.evaluate(from.createOrGetModel());
        StringNode newNode = new StringNode(getBaseUrl(), null);
        newNode.setModel(evaluate);
        return newNode;
    }

    public StringNode stringRule(String stringFunction) {
        return stringRule(new StingEvaluator(new FunctionParser(new TokenQueue(stringFunction)).parse()));
    }

    public StringNode regex(String regex, int group) {
        return stringRule("regex(" + StringUtils.wrap(regex, "\"") + "," + group + ")");
    }

    public static AbstractSelectable createModel(String baseUrl, String rawText) {
        return new RawNode(baseUrl, rawText);
    }
}
