package com.virjar.vscrawler.core.selector.string;

import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.virjar.sipsoup.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.string.tree.FunctionNode;
import com.virjar.vscrawler.core.selector.string.tree.FunctionResult;
import com.virjar.vscrawler.core.selector.string.tree.IntergerResult;
import com.virjar.vscrawler.core.selector.string.tree.StringResult;

/**
 * Created by virjar on 17/7/1.
 */
public class FunctionParser {

    private TokenQueue tokenQueue;

    public FunctionParser(TokenQueue tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    // 将一个函数规则文本编译成模型
    public FunctionNode parse() {

        tokenQueue.consumeWhitespace();
        String functionName = tokenQueue.consumeAttributeKey();
        if (StringUtils.isBlank(functionName)) {
            throw new IllegalStateException("functionName can not be blank for token:" + tokenQueue.getQueue());
        }

        tokenQueue.consumeWhitespace();
        if (tokenQueue.isEmpty() || tokenQueue.peek() != '(') {
            throw new IllegalStateException(
                    "parameter is empty for token:" + tokenQueue.getQueue() + " for function:" + functionName);
        }

        // 参数列表
        String paramString = tokenQueue.chompBalanced('(', ')');
        TokenQueue paramToken = new TokenQueue(paramString);
        LinkedList<FunctionNode> params = Lists.newLinkedList();
        while ((paramToken.consumeWhitespace() || !paramToken.consumeWhitespace()) && !paramToken.isEmpty()) {

            // 字符串参数
            if (tokenQueue.matches("\'")) {
                final String stringParam = TokenQueue.unescape(tokenQueue.chompBalanced('\'', '\''));
                params.add(new StringResult(stringParam).toFunctionNode());
            } else if (tokenQueue.matches("\"")) {
                String stringParam = TokenQueue.unescape(tokenQueue.chompBalanced('\"', '\"'));
                params.add(new StringResult(stringParam).toFunctionNode());
            } // 数字参数
            else if (tokenQueue.matchesDigit()) {
                String digit = tokenQueue.consumeDigit();
                // 当前只支持整数,这里暂时留一个坑(解析结果可能有小数)
                params.add(new IntergerResult(NumberUtils.toInt(digit)).toFunctionNode());
            } // 函数
            else if (tokenQueue.matchesFunction()) {
                String subFunction = tokenQueue.consumeFunction();
                params.add(new FunctionParser(new TokenQueue(subFunction)).parse());
            } else {
                throw new IllegalStateException("can not recognize token:" + tokenQueue.remainder());
            }
            tokenQueue.consumeWhitespace();

            if (!tokenQueue.isEmpty()) {
                if (tokenQueue.peek() != ',') {
                    throw new IllegalStateException("can not parse param list: \"" + tokenQueue.getQueue()
                            + "\"  ,for token " + tokenQueue.remainder());
                }
            }
        }
        return new FunctionResult(functionName, params).toFunctionNode();
    }
}
