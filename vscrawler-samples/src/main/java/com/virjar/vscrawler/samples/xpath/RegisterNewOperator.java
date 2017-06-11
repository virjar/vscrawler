package com.virjar.vscrawler.samples.xpath;

import org.jsoup.nodes.Element;

import com.virjar.vscrawler.core.selector.xpath.core.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.OperatorEnv;
import com.virjar.vscrawler.core.selector.xpath.core.parse.expression.node.AlgorithmUnit;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

/**
 * Created by virjar on 17/6/11. 定义一个运算符
 */
public class RegisterNewOperator {
    public static void main(String[] args) {
        // xpath本身相等判断是单等号 "=",有些语言里面是使用双等号判断相等 "=="。
        // 这里演示如何使用双等号实现相等判断

        System.out.println(isLegal("//div[@name='myname' && 'true']   | /css('.cssExpression')::div"));

        System.out.println(isLegal("//div[@name=='myname' && 'true']"));
        // 注册==,使用=的优先级,使用DoubleEqualOperator运算器,注册后,测试应该就能通过
        OperatorEnv.addOperator("==", OperatorEnv.judgePriority("="), DoubleEqualOperator.class);

        System.out.println(isLegal("//div[@name=='myname' && 'true']"));
    }

    public static boolean isLegal(String xpath) {
        try {
            XpathEvaluator compile = XpathParser.compile(xpath);
            return true;
        } catch (XpathSyntaxErrorException e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    public static class DoubleEqualOperator extends AlgorithmUnit {

        @Override
        public Object calc(Element element) {
            Object rightValue = right.calc(element);
            Object leftValue = left.calc(element);
            // 如果两个null相互判断,则判定为true

            if (leftValue == null && rightValue == null) {
                return true;
            }
            if (leftValue == null || rightValue == null) {
                return false;
            }

            return leftValue.equals(rightValue);
        }

        @Override
        public Class judeResultType() {
            return Boolean.class;
        }
    }
}
