package com.virjar.vscrawler.util;

import java.io.File;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by virjar on 17/5/15.
 */
public class PathResolver {
    public static String resolveAbsolutePath(String pathName, ClassLoader classLoader) {
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
            URL url = classLoader.getResource(pathName);
            if (url != null) {
                return new File(url.getFile()).getAbsolutePath();
            } else {
                URL classPathDirectoryRoot = classLoader.getResource("/");
                if (classPathDirectoryRoot == null) {
                    return pathName;
                }
                return new File(new File(classPathDirectoryRoot.getFile()).getAbsoluteFile(), pathName)
                        .getAbsolutePath();
            }

        }

        // as classpath
        URL url = classLoader.getResource(pathName);
        if (url != null) {
            return new File(url.getFile()).getAbsolutePath();
        }
        return pathName;
    }

    public static String resolveAbsolutePath(String pathName) {
        return resolveAbsolutePath(pathName, PathResolver.class.getClassLoader());
    }

}
