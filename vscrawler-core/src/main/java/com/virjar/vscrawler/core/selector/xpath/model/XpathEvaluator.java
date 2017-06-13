package com.virjar.vscrawler.core.selector.xpath.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.function.axis.AxisFunction;
import com.virjar.vscrawler.core.selector.xpath.exception.FinalTypeNotSameException;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/9.
 */
public abstract class XpathEvaluator {
    public abstract List<JXNode> evaluate(List<JXNode> elements);

    public List<JXNode> evaluate(Elements elements) {
        return evaluate(Lists.transform(elements, new Function<Element, JXNode>() {
            @Override
            public JXNode apply(Element input) {
                return JXNode.e(input);
            }
        }));
    }

    public List<JXNode> evaluate(Element element) {
        return evaluate(new Elements(element));
    }

    public List<String> evaluateToString(List<JXNode> jxNodes) {
        return transformToString(evaluate(jxNodes));
    }

    public List<String> evaluateToString(Element element) {
        return transformToString(evaluate(element));
    }

    public List<Element> evaluateToElement(List<JXNode> jxNodes) {
        return transformToElement(evaluate(jxNodes));
    }

    public List<Element> evaluateToElement(Element element) {
        return transformToElement(evaluate(element));
    }

    public static List<Element> transformToElement(List<JXNode> jxNodes) {
        return Lists.newLinkedList(Iterables.transform(Iterables.filter(jxNodes, new Predicate<JXNode>() {
            @Override
            public boolean apply(JXNode input) {
                return input.getElement() != null;
            }
        }), new Function<JXNode, Element>() {
            @Override
            public Element apply(JXNode input) {
                return input.getElement();
            }
        }));
    }

    public static List<String> transformToString(List<JXNode> jxNodes) {
        return Lists.newLinkedList(Iterables.transform(Iterables.filter(jxNodes, new Predicate<JXNode>() {
            @Override
            public boolean apply(JXNode input) {
                return input.isText();
            }
        }), new Function<JXNode, String>() {
            @Override
            public String apply(JXNode input) {
                return input.getTextVal();
            }
        }));
    }

    /**
     * 系统会checktype
     *
     * @return type
     */
    public abstract JXNode.NodeType judeNodeType() throws FinalTypeNotSameException;

    public XpathEvaluator wrap(XpathEvaluator newRule) {
        throw new UnsupportedOperationException();
    }

    public static class AnanyseStartEvaluator extends XpathEvaluator {

        @Override
        public List<JXNode> evaluate(List<JXNode> elements) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JXNode.NodeType judeNodeType() throws FinalTypeNotSameException {
            throw new UnsupportedOperationException();
        }

        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            return newRule;
        }
    }

    public static class ChainEvaluator extends XpathEvaluator {
        private LinkedList<XpathNode> xpathNodeList = Lists.newLinkedList();

        public ChainEvaluator(LinkedList<XpathNode> xpathNodeList) {
            this.xpathNodeList = xpathNodeList;
        }

        private List<JXNode> handleNode(List<JXNode> input, final XpathNode xpathNode) {

            // 目前只支持对element元素进行抽取,如果中途抽取到了文本,则会断节
            List<Element> elements = Lists
                    .transform(Lists.newLinkedList(Iterables.filter(input, new Predicate<JXNode>() {
                        @Override
                        public boolean apply(JXNode input) {
                            return !input.isText();
                        }
                    })), new Function<JXNode, Element>() {
                        @Override
                        public Element apply(JXNode input) {
                            return input.getElement();
                        }
                    });

            List<Element> contextElements;

            // 轴
            AxisFunction axis = xpathNode.getAxis();
            if (axis != null) {
                contextElements = Lists.newLinkedList();
                for (Element element : elements) {
                    Elements call = axis.call(element, xpathNode.getAxisParams());
                    if (call != null) {
                        contextElements.addAll(call);
                    }
                }
            } else {
                contextElements = elements;
            }

            // 调用抽取函数
            List<JXNode> jxNodes = xpathNode.getSelectFunction().call(xpathNode.getScopeEm(),
                    new Elements(contextElements), xpathNode.getSelectParams());

            // 谓语过滤
            if (xpathNode.getPredicate() == null) {
                return jxNodes;
            }

            // 谓语只支持对元素过滤,非元素节点直接被过滤
            return Lists.newLinkedList(Iterables.filter(jxNodes, new Predicate<JXNode>() {
                @Override
                public boolean apply(JXNode input) {
                    return xpathNode.getPredicate().isValid(input.getElement());
                }
            }));
        }

        @Override
        public List<JXNode> evaluate(List<JXNode> elements) {
            for (XpathNode xpathNode : xpathNodeList) {// 对xpath语法树上面每个节点进行抽取
                elements = handleNode(elements, xpathNode);
            }
            return elements;
        }

        @Override
        public JXNode.NodeType judeNodeType() throws FinalTypeNotSameException {
            // Predicate predicate = xpathNodeList.getLast().getPredicate();
            // return null;// TODO 优化谓语结构后实现
            // return xpathNodeList.getLast().getTagName()
            return null;
        }
    }

    public static class OrEvaluator extends XpathEvaluator {
        private List<XpathEvaluator> subEvaluators = Lists.newLinkedList();

        public OrEvaluator() {
        }

        @Override
        public List<JXNode> evaluate(List<JXNode> elements) {
            Iterator<XpathEvaluator> iterator = subEvaluators.iterator();
            List<JXNode> evaluate = iterator.next().evaluate(elements);
            while (iterator.hasNext()) {
                evaluate.addAll(iterator.next().evaluate(elements));
            }
            return evaluate;
        }

        @Override
        public JXNode.NodeType judeNodeType() throws FinalTypeNotSameException {
            XpathUtil.checkSameResultType(subEvaluators);
            return subEvaluators.iterator().next().judeNodeType();
        }

        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            subEvaluators.add(newRule);
            return this;
        }
    }

    public static class AndEvaluator extends XpathEvaluator {

        private List<XpathEvaluator> subEvaluators = Lists.newLinkedList();

        public AndEvaluator() {
        }

        @Override
        public List<JXNode> evaluate(List<JXNode> elements) {
            Iterator<XpathEvaluator> iterator = subEvaluators.iterator();
            List<JXNode> evaluate = iterator.next().evaluate(elements);
            while (iterator.hasNext()) {
                evaluate.retainAll(iterator.next().evaluate(elements));
            }
            return evaluate;
        }

        @Override
        public JXNode.NodeType judeNodeType() throws FinalTypeNotSameException {
            XpathUtil.checkSameResultType(subEvaluators);
            return subEvaluators.iterator().next().judeNodeType();
        }

        @Override
        public XpathEvaluator wrap(XpathEvaluator newRule) {
            subEvaluators.add(newRule);
            return this;
        }
    }
}
