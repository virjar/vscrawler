package com.virjar.vscrawler.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by virjar on 2018/1/18.<br>
 */
@Slf4j
class Version {
    private static boolean printBanner = false;
    private static String version = "0.2.x";
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
        InputStream pomProperties = Version.class.getResourceAsStream("/META-INF/maven/com/virjar/vscrawler-core/pom.properties");
        if (pomProperties == null) {
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(pomProperties);
            version = properties.getProperty("version", "0.2.x");
            groupId = properties.getProperty("groupId", "com.virjar");
            artifactId = properties.getProperty("artifactId", "vscrawler-core");
        } catch (IOException e) {
            log.error("error when load pom.properties", e);
        } finally {
            IOUtils.closeQuietly(pomProperties);
        }
    }
}
