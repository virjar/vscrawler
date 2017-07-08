package com.virjar.vscrawler.core.selector.string.function;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.virjar.vscrawler.core.selector.string.Strings;
import com.virjar.vscrawler.core.selector.string.syntax.StringContext;
import com.virjar.vscrawler.core.selector.string.syntax.SyntaxNode;

/**
 * Created by virjar on 17/7/8.
 */
public class Regex implements StringFunction {
    @Override
    public Object call(StringContext stringContext, List<SyntaxNode> params) {
        Preconditions.checkArgument(params.size() >= 2, "正则至少包含2个参数");

        Object string = params.get(0).calculate(stringContext);
        if (string == null) {
            return new Strings();
        }
        Object regex = params.get(1).calculate(stringContext);
        if (regex == null) {
            throw new IllegalStateException("regex cannot be null");
        }

        if (!(regex instanceof String)) {
            throw new IllegalStateException("second params must be string ");
        }

        int group = 0;
        if (params.size() >= 3) {
            Object groupObject = params.get(2).calculate(stringContext);
            if (groupObject == null || !(groupObject instanceof Number)) {
                throw new IllegalStateException("group be a number ,now is " + groupObject);
            }
            group = ((Number) groupObject).intValue();
        }

        Pattern pattern = Pattern.compile(regex.toString());
        Strings ret = new Strings();

        if (string instanceof Strings) {
            for (String str : (Strings) string) {
                Matcher matcher = pattern.matcher(str);
                while (matcher.find()) {
                    ret.add(matcher.group(group));
                }
            }
        } else {
            Matcher matcher = pattern.matcher(string.toString());
            while (matcher.find()) {
                ret.add(matcher.group(group));
            }
        }
        return ret;
    }

    @Override
    public String determineFunctionName() {
        return "regex";
    }
}
