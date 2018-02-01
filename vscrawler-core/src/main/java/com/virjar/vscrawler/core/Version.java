package com.virjar.vscrawler.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by virjar on 2018/1/18.<br>
 */
@Slf4j
class Version {
    private static boolean printBanner = false;
    private static String version = "unknown";
    private static String groupId = "com.virjar";
    private static String artifactId = "vscrawler-core";

    public static String getVersion() {
        return version;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static String getArtifactId() {
        return artifactId;
    }

    public synchronized static boolean needPrintBanner() {
        if (printBanner) {
            return false;
        }
        printBanner = true;
        return true;
    }

    static {
        judgeVersion();
    }

    private static void judgeVersion() {
        if (!loadWithClassLoader()) {
            loadWithPomPath();
        }
    }

    private static boolean loadWithClassLoader() {
        CodeSource codeSource = Version.class.getProtectionDomain().getCodeSource();
        if (codeSource == null) {
            return false;
        }
        //find pom.properties
        URL location = codeSource.getLocation();
        JarFile jarFile = null;
        try {
            if (location.toString().startsWith("file:")) {
                File file = new File(location.getPath());
                if (!file.exists()) {
                    return false;
                } else if (file.isDirectory()) {
                    return loadWithDirector(file);
                }
                jarFile = new JarFile(new File(location.getPath()));
            } else {
                Object content = location.getContent();
                if (content instanceof JarFile) {
                    jarFile = (JarFile) content;
                }
            }
            if (jarFile == null) {
                return false;
            }
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith("pom.properties")) {
                    InputStream jarFileInputStream = jarFile.getInputStream(jarEntry);
                    try {
                        loadMavenProperties(jarFileInputStream);
                    } finally {
                        IOUtils.closeQuietly(jarFileInputStream);
                    }
                    return true;
                }
            }
        } catch (IOException e) {
            log.error("error when open file pom.properties", e);
        } finally {
            IOUtils.closeQuietly(jarFile);
        }
        return false;
    }

    private static boolean loadWithDirector(File root) {
        File pomPropertiesFile = findPomPropertiesFile(root);
        if (pomPropertiesFile == null) {
            return false;
        }

        InputStream pomPropertiesStream = null;
        try {
            pomPropertiesStream = new FileInputStream(pomPropertiesFile);
            loadMavenProperties(pomPropertiesStream);
            return true;
        } catch (IOException e) {
            log.error("error when open file pom.properties", e);
        } finally {
            IOUtils.closeQuietly(pomPropertiesStream);
        }
        return false;
    }


    private static File findPomPropertiesFile(File dir) {
        File files[] = dir.listFiles();
        if (null == files || files.length == 0)
            return null;
        for (File file : files) {
            if (file.isDirectory()) {
                return findPomPropertiesFile(file);
            } else if (file.getName().endsWith("pom.properties")) {
                return file;
            }
        }
        return null;
    }

    private static void loadWithPomPath() {
        InputStream pomProperties = Version.class.getResourceAsStream("/META-INF/maven/com/virjar/vscrawler-core/pom.properties");
        if (pomProperties == null) {
            return;
        }
        try {
            loadMavenProperties(pomProperties);
        } catch (IOException ioe) {
            log.error("error when open file pom.properties", ioe);
        } finally {
            IOUtils.closeQuietly(pomProperties);
        }
    }

    private static void loadMavenProperties(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);
        version = properties.getProperty("version", "unknown");
        groupId = properties.getProperty("groupId", "com.virjar");
        artifactId = properties.getProperty("artifactId", "vscrawler-core");
    }

}
