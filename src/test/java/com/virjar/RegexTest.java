package com.virjar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by virjar on 17/4/17.
 */
public class RegexTest {
    private static final Pattern qinxiIDPattern = Pattern.compile("www\\.qixin\\.com/company/(([^/])+)");

    public static void main(String[] args) {
        String url = "http://www.qixin.com/company/6a91e8db-0838-4f1d-b6f2-fed1bb687941";

        Matcher matcher = qinxiIDPattern.matcher(url);
        if (matcher.find()) {
            System.out.println(matcher.group(1));
        }
    }
}
