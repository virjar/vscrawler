package com.virjar.vscrawler.core.util;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * Created by virjar on 17/5/15.
 */
public class PathResolver {
    private static final Pattern urlPattern = Pattern.compile("https?://([^/]+)(.*)");
    private static final Pattern fileNamePattern = Pattern.compile("https?://([^/]+).*/(.+)");
    private static final Splitter dotSplitter = Splitter.on(".").omitEmptyStrings().trimResults();
    private static final Joiner filePathJoiner = Joiner.on("/").skipNulls();
    private static final Splitter urlSeparatorSplitter = Splitter.on("/").omitEmptyStrings().trimResults();

    public static String resolveAbsolutePath(String pathName) {
        // file protocol
        if (StringUtils.startsWithIgnoreCase(pathName, "file:")) {
            return pathName.substring("file:".length());
        }

        // think as normal file
        File tempFile = new File(pathName);
        if (tempFile.exists()) {
            return tempFile.getAbsolutePath();
        }

        if (StringUtils.startsWithIgnoreCase(pathName, "classpath:")) {
            pathName = pathName.substring("classpath:".length());
            // as classpath
            URL url = PathResolver.class.getResource(pathName);
            if (url != null) {
                return new File(url.getFile()).getAbsolutePath();
            } else {
                URL classPathDirectoryRoot = PathResolver.class.getResource("/");
                if (classPathDirectoryRoot == null) {
                    return pathName;
                }
                return new File(new File(classPathDirectoryRoot.getFile()).getAbsoluteFile(), pathName)
                        .getAbsolutePath();
            }

        }

        // as classpath
        URL url = PathResolver.class.getResource(pathName);
        if (url != null) {
            return new File(url.getFile()).getAbsolutePath();
        }
        return pathName;
    }

    /**
     * calculate a file path to save http url resource, base rule is "basePath +revert domain+ path"
     * 
     * @param basePath 起始路径
     * @param url url ,http://sss/path/resource.html
     * @return path
     */
    public static String commonDownloadPath(String basePath, String url) {
        if (basePath.startsWith("~")) {
            basePath = new File(System.getProperty("user.home"), basePath.substring(1)).getAbsolutePath();
        }
        Matcher matcher = urlPattern.matcher(url);
        if (!matcher.find()) {
            return new File(basePath, url).getAbsolutePath();
        }
        String domain = matcher.group(1);
        String resource = matcher.group(2);

        List<String> reverse = Lists.newLinkedList(Lists.reverse(dotSplitter.splitToList(domain)));
        reverse.addAll(urlSeparatorSplitter.splitToList(resource));
        String filePath = filePathJoiner.join(reverse);
        File targetFile = new File(basePath, filePath);
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                return new File(basePath, url).getAbsolutePath();
            }
        }
        return targetFile.getAbsolutePath();
        // return absolutePath;
    }

    public static String resourceName(String basePath, String url) {
        if (basePath.startsWith("~")) {
            basePath = new File(System.getProperty("user.home"), basePath.substring(1)).getAbsolutePath();
        }

        Matcher matcher = fileNamePattern.matcher(url);
        if (!matcher.find()) {
            return new File(basePath, UUID.randomUUID().toString()).getAbsolutePath();
        }
        String domain = matcher.group(1);
        String resource = matcher.group(2);

        List<String> reverse = Lists.newLinkedList(Lists.reverse(dotSplitter.splitToList(domain)));
        reverse.add(resource);
        String filePath = filePathJoiner.join(reverse);
        File targetFile = new File(basePath, filePath);
        File parentFile = targetFile.getParentFile();
        if (!parentFile.exists()) {
            if (!parentFile.mkdirs()) {
                return new File(basePath, url).getAbsolutePath();
            }
        }
        return targetFile.getAbsolutePath();
    }

}
