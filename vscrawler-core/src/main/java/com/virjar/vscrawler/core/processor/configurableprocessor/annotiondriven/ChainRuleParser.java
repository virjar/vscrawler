package com.virjar.vscrawler.core.processor.configurableprocessor.annotiondriven;


import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.virjar.sipsoup.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.combine.AbstractSelectable;
import com.virjar.vscrawler.core.selector.string.FunctionParser;
import com.virjar.vscrawler.core.selector.string.StingEvaluator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by virjar on 2017/12/10.<br/>
 * 支持链式规则 $css{} $xpath{} $regex{} $jsonpath{} $stringrule{}
 */
public class ChainRuleParser {
    public static ModelSelector parse(String rule) {
        final TokenQueue tokenQueue = new TokenQueue(rule);
        tokenQueue.consumeWhitespace();
        final List<ModelSelector> modelSelectors = Lists.newLinkedList();
        while (!tokenQueue.isEmpty()) {
            if (tokenQueue.peek() != '$') {
                modelSelectors.add(new ModelSelector() {
                    @Override
                    public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                        return abstractSelectable.xpath(tokenQueue.remainder());
                    }
                });
                break;
            }
            tokenQueue.advance();
            tokenQueue.consumeWhitespace();
            String ruleName = tokenQueue.consumeIdentify();
            String ruleValue = tokenQueue.chompBalanced('{', '}');
            ModelSelector modelSelector = create(ruleName, ruleValue);
            Preconditions.checkNotNull(modelSelector);
            modelSelectors.add(modelSelector);
            tokenQueue.consumeWhitespace();
        }

        return new ModelSelector() {
            @Override
            public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                AbstractSelectable ret = abstractSelectable;
                for (ModelSelector modelSelector : modelSelectors) {
                    ret = modelSelector.select(ret);
                }
                return ret;
            }
        };

    }

    public static ModelSelector create(String ruleName, final String ruleValue) {
        if (StringUtils.equalsIgnoreCase(ruleName, "css")) {
            return new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable.css(ruleValue);
                }
            };
        }
        if (StringUtils.equalsIgnoreCase(ruleName, "xpath")) {
            return new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable.xpath(ruleValue);
                }
            };
        }
        if (StringUtils.equalsIgnoreCase(ruleName, "jsonpath")) {
            return new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable.jsonPath(ruleValue);
                }
            };
        }
        if (StringUtils.equalsIgnoreCase(ruleName, "stringrule")) {
            final StingEvaluator stingEvaluator = new StingEvaluator(new FunctionParser(ruleValue).parse());
            return new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable.stringRule(stingEvaluator);
                }
            };
        }

        if (StringUtils.equalsIgnoreCase(ruleName, "regex")) {
            String regex = ruleValue;
            int group = 0;
            Matcher matcher = Pattern.compile("(.+)\\s*,\\s*(\\d+)").matcher(ruleValue);
            if (matcher.matches()) {
                regex = matcher.group(1);
                group = NumberUtils.toInt(matcher.group(2));
            }
            final String finalRegex = regex;
            final int finalGroup = group;
            return new ModelSelector() {
                @Override
                public AbstractSelectable select(AbstractSelectable abstractSelectable) {
                    return abstractSelectable.regex(finalRegex, finalGroup);
                }
            };
        }
        return null;
    }
}
