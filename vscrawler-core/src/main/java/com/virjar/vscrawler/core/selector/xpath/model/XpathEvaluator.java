package com.virjar.vscrawler.core.selector.xpath.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;
import com.virjar.vscrawler.core.selector.xpath.exception.FinalTypeNotSameException;
import com.virjar.vscrawler.core.selector.xpath.util.XpathUtil;

/**
 * Created by virjar on 17/6/9.
 */
public abstract class XpathEvaluator {
    public abstract List<JXNode> evaluate(List<JXNode> elements);

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

        private List<JXNode> handleNode(List<JXNode> input, XpathNode xpathNode) {
            return null;
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
            Predicate predicate = xpathNodeList.getLast().getPredicate();
            return null;// TODO 优化谓语结构后实现
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
