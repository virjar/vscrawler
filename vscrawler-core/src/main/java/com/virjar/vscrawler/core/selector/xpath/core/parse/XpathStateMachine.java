package com.virjar.vscrawler.core.selector.xpath.core.parse;

import com.virjar.vscrawler.core.selector.xpath.model.Predicate;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;
import com.virjar.vscrawler.core.selector.xpath.util.EmMap;

import lombok.Getter;

/**
 * Created by virjar on 17/6/9.
 */
public class XpathStateMachine {
    @Getter
    private BuilderState state = BuilderState.SCOPE;
    private TokenQueue tokenQueue;
    private XpathEvaluator evaluator = new XpathEvaluator.AnanyseStartEvalutor();

    private static char xpathFlag = '`';// 所有被反引号标记的,都认为是一个xpath表达式

    public XpathStateMachine(TokenQueue tokenQueue) {
    }

    public enum BuilderState {

        // 解析起始
        SCOPE {
            @Override
            public void parse(XpathStateMachine stateMachine) {
                stateMachine.tokenQueue.consumeWhitespace();// 消除空白字符
                if (stateMachine.tokenQueue.matchesAny('`', '(')) {
                    // 获取一个子串,处理递归,转义,引号问题

                    String subXpath;
                    if (stateMachine.tokenQueue.matchesAny('`')) {
                        subXpath = stateMachine.tokenQueue.chompBalanced('`', '`');
                    } else {
                        subXpath = stateMachine.tokenQueue.chompBalanced('(', ')');
                    }
                    // subXpath = TokenQueue.unescape(subXpath);
                    // TODO 考虑是否抹掉转义
                    XpathEvaluator subTree = new XpathParser(subXpath).parse();
                    stateMachine.evaluator = stateMachine.evaluator.wrap(subTree);
                    return;
                }

                if (stateMachine.tokenQueue.matchesAny("and", "AND", "&")) {
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

                if (stateMachine.tokenQueue.matchesAny("or", "OR", "|")) {
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

            }
        },
        AXIS {
            @Override
            public void parse(XpathStateMachine stateMachine) {

                stateMachine.state = TAG;
            }
        },
        TAG {
            @Override
            public void parse(XpathStateMachine stateMachine) {

            }
        },
        PREDICATE {
            @Override
            public void parse(XpathStateMachine stateMachine) {

            }
        },
        END {
            @Override
            public void parse(XpathStateMachine stateMachine) {
            }
        };
        public void parse(XpathStateMachine stateMachine) {
        }
    }

    /**
     * 根据谓语字符串初步生成谓语体
     *
     * @param pre 谓语文本
     * @return 谓语对象
     */
    public Predicate genPredicate(String pre) {
        StringBuilder op = new StringBuilder();
        StringBuilder left = new StringBuilder();
        StringBuilder right = new StringBuilder();
        Predicate predicate = new Predicate();
        char[] preArray = pre.toCharArray();
        int index = preArray.length - 1;
        int argDeep = 0;
        int opFlag = 0;
        if (pre.matches(".+(\\+|=|-|>|<|>=|<=|^=|\\*=|$=|~=|!=|!~)\\s*'.+'")) {
            while (index >= 0) {
                char tmp = preArray[index];
                if (tmp == '\'') {
                    argDeep += 1;
                }
                if (argDeep == 1 && tmp != '\'') {
                    right.insert(0, tmp);
                } else if (argDeep == 2 && EmMap.getInstance().commOpChar.contains(tmp)) {
                    op.insert(0, tmp);
                    opFlag = 1;
                } else if (argDeep >= 2 && opFlag > 0) {
                    argDeep++;// 取完操作符后剩下的都属于left
                    left.insert(0, tmp);
                }
                index -= 1;
            }
        } else if (pre.matches(".+(\\+|=|-|>|<|>=|<=|^=|\\*=|$=|~=|!=|!~)[^']+")) {
            while (index >= 0) {
                char tmp = preArray[index];
                if (opFlag == 0 && EmMap.getInstance().commOpChar.contains(tmp)) {
                    op.insert(0, tmp);
                } else {
                    if (op.length() > 0) {
                        left.insert(0, tmp);
                        opFlag = 1;
                    } else {
                        right.insert(0, tmp);
                    }
                }
                index -= 1;
            }
        }

        predicate.setOpEm(EmMap.getInstance().opEmMap.get(op.toString()));
        predicate.setLeft(left.toString().trim());
        predicate.setRight(right.toString().trim());
        predicate.setValue(pre);
        return predicate;
    }
}
