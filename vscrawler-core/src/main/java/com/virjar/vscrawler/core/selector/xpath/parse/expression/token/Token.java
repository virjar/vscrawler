package com.virjar.vscrawler.core.selector.xpath.parse.expression.token;

/**
 * Created by virjar on 17/6/12.
 */
public class Token {

    public static String OPERATOR = "OPERATOR";
    public static String CONSTANT = "CONSTANT";
    public static String NUMBER = "NUMBER";
    public static String EXPRESSION = "EXPRESSION";
    public static String ATTRIBUTE_ACTION = "ATTRIBUTE_ACTION";
    public static String XPATH = "XPATH";
    public static String FUNCTION = "FUNCTION";
    public static String BOOLEAN = "BOOLEAN";

    @Deprecated
    public enum TokenType {
        OPERATOR, CONSTANT, NUMBER, EXPRESSION, ATTRIBUTE_ACTION, XPATH, FUNCTION, BOOLEAN
    }
}
