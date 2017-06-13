package com.virjar.vscrawler.core.selector.xpath.parse.expression.node;

import java.util.Map;

import com.virjar.vscrawler.core.selector.xpath.parse.expression.SyntaxNode;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.token.Token;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.virjar.vscrawler.core.selector.xpath.parse.TokenQueue;
import com.virjar.vscrawler.core.selector.xpath.parse.XpathParser;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.ExpressionParser;
import com.virjar.vscrawler.core.selector.xpath.parse.expression.FunctionParser;
import com.virjar.vscrawler.core.selector.xpath.exception.XpathSyntaxErrorException;
import com.virjar.vscrawler.core.selector.xpath.model.JXNode;
import com.virjar.vscrawler.core.selector.xpath.model.XpathEvaluator;

/**
 * Created by virjar on 17/6/11.对于普通的token类型,通过他来构造
 */
@Deprecated
public class TokenNodeFactory {
    private static Map<Token.TokenType, AlgorithmUnitGenerator> allGerator = Maps.newHashMap();

    static {
        registerDefault();
    }

    private static void registerDefault() {
        // 数字
        register(Token.TokenType.NUMBER, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(final ExpressionParser.TokenHolder tokenHolder) {
                return new SyntaxNode() {
                    @Override
                    public Object calc(Element element) {
                        if (StringUtils.contains(tokenHolder.getExpression(), ".")) {
                            return NumberUtils.toDouble(tokenHolder.getExpression());
                        } else {
                            return NumberUtils.toInt(tokenHolder.getExpression());
                        }
                    }

                    @Override
                    public Class judeResultType() {
                        return Double.class;
                    }
                };
            }
        });

        // 取属性动作
        register(Token.TokenType.ATTRIBUTE_ACTION, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(final ExpressionParser.TokenHolder tokenHolder) {
                return new SyntaxNode() {
                    @Override
                    public Object calc(Element element) {
                        return element.attr(tokenHolder.getExpression());
                    }

                    @Override
                    public Class judeResultType() {
                        return String.class;
                    }
                };
            }
        });

        // 字符串常量
        register(Token.TokenType.CONSTANT, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(final ExpressionParser.TokenHolder tokenHolder) {
                return new SyntaxNode() {
                    @Override
                    public Object calc(Element element) {
                        return tokenHolder.getExpression();
                    }

                    @Override
                    public Class judeResultType() {
                        return String.class;
                    }
                };
            }
        });

        // xpath
        register(Token.TokenType.XPATH, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException {
                final XpathEvaluator xpathEvaluator = new XpathParser(tokenHolder.getExpression()).parse();
                return new SyntaxNode() {
                    @Override
                    public Object calc(Element element) {
                        return xpathEvaluator.evaluate(Lists.newArrayList(JXNode.e(element)));
                    }

                    @Override
                    public Class judeResultType() {
                        return String.class;
                    }
                };
            }
        });

        // 子串
        register(Token.TokenType.EXPRESSION, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException {
                return new ExpressionParser(new TokenQueue(tokenHolder.getExpression())).parse();
            }
        });

        // 函数
        register(Token.TokenType.FUNCTION, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException {
                return new FunctionParser(new TokenQueue(tokenHolder.getExpression())).parse();
            }
        });

        // boolean变量
        register(Token.TokenType.BOOLEAN, new AlgorithmUnitGenerator() {
            @Override
            public SyntaxNode gen(final ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException {

                return new SyntaxNode() {
                    @Override
                    public Object calc(Element element) {
                        return BooleanUtils.toBoolean(tokenHolder.getExpression());
                    }

                    @Override
                    public Class judeResultType() {
                        return Boolean.class;
                    }
                };
            }
        });
    }

    public static void register(Token.TokenType tokenType, AlgorithmUnitGenerator gernator) {
        allGerator.put(tokenType, gernator);
    }

    @Deprecated
    public static AlgorithmUnitGenerator hintGenerator(ExpressionParser.TokenHolder tokenType) {
        return allGerator.get(Token.TokenType.valueOf(tokenType.getType()));
    }

    @Deprecated
    public static SyntaxNode hintAndGen(ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException {
        return hintGenerator(tokenHolder).gen(tokenHolder);
    }

    public interface AlgorithmUnitGenerator {
        SyntaxNode gen(ExpressionParser.TokenHolder tokenHolder) throws XpathSyntaxErrorException;
    }
}
