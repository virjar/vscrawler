package com.virjar.vscrawler.core.selector.xpath.core.parse;

import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.xpath.core.FunctionEnv;
import com.virjar.vscrawler.core.selector.xpath.core.function.axis.AxisFunction;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.ExpressionParser;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.exception.NoSuchAxisException;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.Predicate;
import com.virjar.vscrawler.core.selector.xpath.model.XpathChain;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;
import com.virjar.vscrawler.core.selector.xpath.model.XpathNode;
import com.virjar.vscrawler.core.selector.xpath.util.ScopeEm;

import lombok.Getter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathStateMachine {
    private static Map<String, ScopeEm> scopeEmMap = Maps.newHashMap();
    static {
        scopeEmMap.put("/", ScopeEm.INCHILREN);
        scopeEmMap.put("//", ScopeEm.RECURSIVE);
        scopeEmMap.put("./", ScopeEm.CUR);
        scopeEmMap.put(".//", ScopeEm.CURREC);
    }
    @Getter
    private BuilderState state = BuilderState.SCOPE;
    private TokenQueue tokenQueue;
    @Getter
    private XpathEvaluator evaluator = new XpathEvaluator.AnanyseStartEvaluator();

    private XpathChain xpathChain = new XpathChain();

    XpathStateMachine(TokenQueue tokenQueue) {
        this.tokenQueue = tokenQueue;
    }

    enum BuilderState {

        // 解析起始
        SCOPE {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();// 消除空白字符
                char xpathFlag = '`';
                if (stateMachine.tokenQueue.matchesAny(xpathFlag, '(')) {
                    // 获取一个子串,处理递归,转义,引号问题

                    String subXpath;
                    if (stateMachine.tokenQueue.matchesAny(xpathFlag)) {
                        subXpath = stateMachine.tokenQueue.chompBalanced(xpathFlag, xpathFlag);
                    } else {
                        subXpath = stateMachine.tokenQueue.chompBalanced('(', ')');
                    }
                    // subXpath = TokenQueue.unescape(subXpath);
                    // TODO 考虑是否抹掉转义
                    XpathEvaluator subTree = new XpathParser(subXpath).parse();
                    stateMachine.evaluator = stateMachine.evaluator.wrap(subTree);
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("and", "&")) {
                    if (stateMachine.tokenQueue.matchesAny("&")) {
                        stateMachine.tokenQueue.consumeTo("&");
                    } else {
                        stateMachine.tokenQueue.consumeToIgnoreCase("and");
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (tempEvaluator instanceof XpathEvaluator.AndEvaluator) {
                        return;
                    }
                    XpathEvaluator newEvaluator = new XpathEvaluator.AndEvaluator();
                    stateMachine.evaluator = newEvaluator.wrap(stateMachine.evaluator);
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("or", "|")) {
                    if (stateMachine.tokenQueue.matchesAny("|")) {
                        stateMachine.tokenQueue.consumeTo("|");
                    } else {
                        stateMachine.tokenQueue.consumeToIgnoreCase("or");
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (tempEvaluator instanceof XpathEvaluator.OrEvaluator) {
                        return;
                    }
                    XpathEvaluator newEvaluator = new XpathEvaluator.OrEvaluator();
                    stateMachine.evaluator = newEvaluator.wrap(stateMachine.evaluator);
                    return;
                }

                XpathNode xpathNode = new XpathNode();
                if (stateMachine.tokenQueue.matchesAny("./", "//", "/", "//")) {
                    String scope = stateMachine.tokenQueue.consumeToAny(".//", "./", "//", "/");
                    xpathNode.setScopeEm(scopeEmMap.get(scope));
                }
                stateMachine.xpathChain.getXpathNodeList().add(xpathNode);
                stateMachine.state = AXIS;

            }
        },
        AXIS {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                // 轴解析
                if (!stateMachine.tokenQueue.hasAxis()) {
                    stateMachine.state = TAG;
                    return;
                }

                String axisFunctionStr = stateMachine.tokenQueue.consumeTo("::");
                TokenQueue functionTokenQueue = new TokenQueue(axisFunctionStr);
                String functionName = functionTokenQueue.consumeIdentify().trim();
                functionTokenQueue.consumeWhitespace();

                AxisFunction axisFunction = FunctionEnv.getAxisFunction(functionName);
                if (axisFunction == null) {
                    throw new NoSuchAxisException(stateMachine.tokenQueue.nowPosition(),
                            "not such axis " + functionName);
                }
                stateMachine.xpathChain.getXpathNodeList().getLast().setAxis(axisFunction);

                if (functionTokenQueue.isEmpty()) {
                    stateMachine.state = TAG;
                    return;
                }

                // 带有参数的轴函数
                if (!functionTokenQueue.matches("(")) {// 必须以括号开头
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "expression is not a function:\"" + axisFunctionStr + "\"");
                }
                String paramList = functionTokenQueue.chompBalanced('(', ')');
                functionTokenQueue.consumeWhitespace();
                if (!functionTokenQueue.isEmpty()) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "expression is not a function: \"" + axisFunctionStr + "\" can not recognize token:"
                                    + functionTokenQueue.remainder());
                }

                // 解析参数列表
                TokenQueue paramTokenQueue = new TokenQueue(paramList);
                LinkedList<String> params = Lists.newLinkedList();
                while (!paramTokenQueue.isEmpty()) {
                    paramTokenQueue.consumeWhitespace();
                    String param;
                    if (paramTokenQueue.matches("\"")) {
                        param = paramTokenQueue.chompBalanced('\"', '\"');
                        if (paramTokenQueue.peek() == ',') {
                            paramTokenQueue.consume();
                        }
                    } else if (paramTokenQueue.matches("\'")) {
                        param = paramTokenQueue.chompBalanced('\'', '\'');
                        if (paramTokenQueue.peek() == ',') {
                            paramTokenQueue.consume();
                        }
                    } else {
                        param = paramTokenQueue.consumeTo(",");
                    }
                    params.add(TokenQueue.unescape(param));
                }
                stateMachine.xpathChain.getXpathNodeList().getLast().setAxisParams(params);
                stateMachine.state = TAG;
            }
        },
        TAG {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();
                if (stateMachine.tokenQueue.peek() == '*') {
                    stateMachine.xpathChain.getXpathNodeList().getLast().setTagName("*");
                    stateMachine.tokenQueue.consume();
                    stateMachine.state = PREDICATE;
                    return;
                }

                String tagName = stateMachine.tokenQueue.consumeTagName();
                if (StringUtils.isEmpty(tagName)) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "can not recognize token,expected start with a tagName,actually is:"
                                    + stateMachine.tokenQueue.remainder());
                }
                stateMachine.xpathChain.getXpathNodeList().getLast().setTagName(tagName);
                stateMachine.state = PREDICATE;
            }
        },
        PREDICATE {
            @Override
            public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
                stateMachine.tokenQueue.consumeWhitespace();

                if (stateMachine.tokenQueue.matches("[")) {
                    // 谓语串
                    String predicate = stateMachine.tokenQueue.chompBalanced('[', ']');
                    SyntaxNode predicateTree = new ExpressionParser(new TokenQueue(predicate)).parse();
                    stateMachine.xpathChain.getXpathNodeList().getLast()
                            .setPredicate(new Predicate(predicate, predicateTree));
                }
                // check
                if (!stateMachine.tokenQueue.isEmpty() || !stateMachine.tokenQueue.matches("/")) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "illegal predicate token :" + stateMachine.tokenQueue.remainder());
                }
                if (stateMachine.tokenQueue.isEmpty()) {
                    stateMachine.state = END;
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain.getXpathNodeList()));
                    stateMachine.xpathChain = null;
                } else {
                    stateMachine.state = SCOPE;
                }
            }
        },
        END {
            @Override
            public void parse(XpathStateMachine stateMachine) {
            }
        };
        public void parse(XpathStateMachine stateMachine) throws XpathSyntaxErrorException {
        }
    }
}
