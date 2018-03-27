package com.virjar.vscrawler.core.util;

import java.util.ArrayList;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Created by virjar on 2018/3/27.<br>
 * quick search if a package is a subPackage for packageNameList<br>
 * 
 * <pre>
 * PackageSearchNode root = new PackageSearchNode();
 * root.addToTree("android");
 * root.addToTree("java.lang");
 * root.addToTree("com.alibaba");
 * root.addToTree("com.alipay");
 * root.addToTree("com.baidu");
 * root.addToTree("com.tencent");
 * root.addToTree("com.google");
 * root.addToTree("com.networkbench");
 * root.addToTree("com.sina.weibo");
 * root.addToTree("com.taobao");
 * root.addToTree("com.tendcloud");
 * root.addToTree("com.umeng.message");
 * root.addToTree("org.android");
 * root.addToTree("org.aspectj");
 * root.addToTree("org.java_websocket");
 * </pre>
 * 
 * then
 * 
 * <pre>
 *     root.isSubPackage("com.alibaba.fastjson.JSONObject"); return true
 *     root.isSubPackage("com.163"); return false
 *     root.isSubPackage("com"); return false
 * </pre>
 * 
 * @since 0.3.0
 */
public class PackageSearchNode {
    private static final Splitter dotSplitter = Splitter.on(".").omitEmptyStrings();

    private Map<String, PackageSearchNode> children = Maps.newHashMap();

    public void addToTree(ArrayList<String> packageSplitItems, int index) {
        if (index > packageSplitItems.size() - 1) {
            return;
        }
        String node = packageSplitItems.get(index);
        PackageSearchNode packageSearchNode = children.get(node);
        if (packageSearchNode == null) {
            packageSearchNode = new PackageSearchNode();
            children.put(node, packageSearchNode);
        }
        packageSearchNode.addToTree(packageSplitItems, index + 1);
    }

    public void addToTree(String packageName) {
        addToTree(Lists.newArrayList(dotSplitter.split(packageName)), 0);
    }

    public boolean isSubPackage(String packageName) {
        return isSubPackage(Lists.newArrayList(dotSplitter.split(packageName)), 0);
    }

    public boolean isSubPackage(ArrayList<String> packageSplitItems, int index) {
        if (children.size() == 0) {
            return true;
        }
        if (index > packageSplitItems.size() - 1) {
            return false;
        }

        String node = packageSplitItems.get(index);
        return children.containsKey(node) && children.get(node).isSubPackage(packageSplitItems, index + 1);
    }
}
