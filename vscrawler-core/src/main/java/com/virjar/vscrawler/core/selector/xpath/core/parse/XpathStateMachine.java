package com.virjar.vscrawler.core.selector.xpath.core.parse;

import java.util.LinkedList;
import java.util.List;
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

import lombok.Getter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathStateMachine {
    private static Map<String, XpathNode.ScopeEm> scopeEmMap = Maps.newHashMap();
    static {
        scopeEmMap.put("/", XpathNode.ScopeEm.INCHILREN);
        scopeEmMap.put("//", XpathNode.ScopeEm.RECURSIVE);
        scopeEmMap.put("./", XpathNode.ScopeEm.CUR);
        scopeEmMap.put(".//", XpathNode.ScopeEm.CURREC);
    }

    // 注意顺序,这顺序不能乱
    private static List<String> scopeList = Lists.newArrayList("//", "/", ".//", "/");

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
                    if (stateMachine.tokenQueue.matches("&")) {
                        stateMachine.tokenQueue.consume("&");
                    } else {
                        stateMachine.tokenQueue.advance("and".length());
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (!(tempEvaluator instanceof XpathEvaluator.AndEvaluator)) {
                        XpathEvaluator newEvaluator = new XpathEvaluator.AndEvaluator();
                        stateMachine.evaluator = tempEvaluator.wrap(newEvaluator);
                    }
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain.getXpathNodeList()));
                    stateMachine.xpathChain = new XpathChain();
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("or", "|")) {
                    if (stateMachine.tokenQueue.matches("|")) {
                        stateMachine.tokenQueue.consume("|");
                    } else {
                        stateMachine.tokenQueue.advance("or".length());
                    }
                    XpathEvaluator tempEvaluator = stateMachine.evaluator;
                    if (!(tempEvaluator instanceof XpathEvaluator.OrEvaluator)) {
                        XpathEvaluator newEvaluator = new XpathEvaluator.OrEvaluator();
                        stateMachine.evaluator = tempEvaluator.wrap(newEvaluator);
                    }
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain.getXpathNodeList()));
                    stateMachine.xpathChain = new XpathChain();
                    return;
                }

                for (String scope : scopeList) {
                    if (stateMachine.tokenQueue.matches(scope)) {
                        stateMachine.tokenQueue.consume(scope);
                        XpathNode xpathNode = new XpathNode();
                        xpathNode.setScopeEm(scopeEmMap.get(scope));
                        stateMachine.xpathChain.getXpathNodeList().add(xpathNode);
                        stateMachine.state = AXIS;
                        return;
                    }
                }

                throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                        "can not recognize token:" + stateMachine.tokenQueue.remainder());
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
                stateMachine.tokenQueue.consume("::");
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
                    if (!paramTokenQueue.isEmpty() && paramTokenQueue.peek() == ',') {
                        paramTokenQueue.advance();
                        paramTokenQueue.consumeWhitespace();
                    }
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
                        if (StringUtils.isEmpty(param)) {
                            continue;
                        }
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
                    stateMachine.tokenQueue.advance();
                    stateMachine.tokenQueue.consumeWhitespace();
                    stateMachine.xpathChain.getXpathNodeList().getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction("tag"));
                    stateMachine.xpathChain.getXpathNodeList().getLast().setSelectParams(Lists.newArrayList("*"));
                    stateMachine.state = PREDICATE;
                    return;
                }

                if (stateMachine.tokenQueue.matchesFunction()) {// 遇到主干抽取函数,后面不能有谓语
                    String function = stateMachine.tokenQueue.consumeFunction();
                    TokenQueue functionTokenQueue = new TokenQueue(function);
                    String functionName = functionTokenQueue.consumeTo("(");
                    LinkedList<String> params = Lists.newLinkedList();
                    TokenQueue paramTokenQueue = new TokenQueue(functionTokenQueue.chompBalanced('(', ')'));
                    while ((paramTokenQueue.consumeWhitespace() && !paramTokenQueue.consumeWhitespace())
                            || !paramTokenQueue.isEmpty()) {
                        String param;
                        if (paramTokenQueue.matches("\"")) {
                            param = paramTokenQueue.chompBalanced('\"', '\"');
                            if (paramTokenQueue.peek() == ',') {
                                paramTokenQueue.advance();
                            }
                        } else if (paramTokenQueue.matches("\'")) {
                            param = paramTokenQueue.chompBalanced('\'', '\'');
                            if (paramTokenQueue.peek() == ',') {
                                paramTokenQueue.advance();
                            }
                        } else {
                            param = paramTokenQueue.consumeTo(",");
                        }
                        params.add(TokenQueue.unescape(param));
                    }
                    stateMachine.xpathChain.getXpathNodeList().getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction(functionName));
                    stateMachine.xpathChain.getXpathNodeList().getLast().setSelectParams(params);
                    stateMachine.state = PREDICATE;// TODO 后面是否支持谓语,如果支持的话,谓语数据结构需要修改
                    return;
                }
                if (stateMachine.tokenQueue.matches("@")) {// 遇到属性抽取动作
                    stateMachine.tokenQueue.advance();
                    stateMachine.tokenQueue.consumeWhitespace();
                    stateMachine.xpathChain.getXpathNodeList().getLast()
                            .setSelectFunction(FunctionEnv.getSelectFunction("@"));
                    if (stateMachine.tokenQueue.peek() == '*') {
                        stateMachine.tokenQueue.advance();
                        stateMachine.xpathChain.getXpathNodeList().getLast().setSelectParams(Lists.newArrayList("*"));
                    } else {
                        String attributeKey = stateMachine.tokenQueue.consumeAttributeKey();
                        stateMachine.xpathChain.getXpathNodeList().getLast()
                                .setSelectParams(Lists.newArrayList(attributeKey));
                    }
                    stateMachine.state = PREDICATE;
                    stateMachine.tokenQueue.consumeWhitespace();
                    return;
                }

                String tagName = stateMachine.tokenQueue.consumeTagName();
                if (StringUtils.isEmpty(tagName)) {
                    throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                            "can not recognize token,expected start with a tagName,actually is:"
                                    + stateMachine.tokenQueue.remainder());
                }
                stateMachine.xpathChain.getXpathNodeList().getLast()
                        .setSelectFunction(FunctionEnv.getSelectFunction("tag"));
                stateMachine.xpathChain.getXpathNodeList().getLast().setSelectParams(Lists.newArrayList(tagName));
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
                stateMachine.tokenQueue.consumeWhitespace();
                // if (!stateMachine.tokenQueue.isEmpty() && !stateMachine.tokenQueue.matches("/")) {
                // throw new XpathSyntaxErrorException(stateMachine.tokenQueue.nowPosition(),
                // "illegal predicate token :\"" + stateMachine.tokenQueue.remainder() + "\"");
                // }
                if (stateMachine.tokenQueue.isEmpty()) {
                    stateMachine.state = END;
                    stateMachine.evaluator = stateMachine.evaluator
                            .wrap(new XpathEvaluator.ChainEvaluator(stateMachine.xpathChain.getXpathNodeList()));
                    stateMachine.xpathChain = new XpathChain();
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
