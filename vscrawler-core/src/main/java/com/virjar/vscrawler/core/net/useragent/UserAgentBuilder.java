package com.virjar.vscrawler.core.net.useragent;

import java.util.Random;

/**
 * Created by virjar on 17/3/30.
 */
public class UserAgentBuilder {
    public static UserAgentBuilder create() {
        return new UserAgentBuilder();
    }


    public static String randomUserAgent() {

        Random random = new Random();

        String userAgent;

        int webBrowser = random.nextInt(2);
        switch (webBrowser) {
            // Chrome
            case 0:
                userAgent = buildChrome();
                break;
            // Firfox
            case 1:
                userAgent = buildFirefox();
                break;
            default:
                userAgent = buildChrome();
        }
        return userAgent;
    }

    public static String randomAppUserAgent() {
        Random random = new Random();
        int browserType = random.nextInt(AppUserAgentEnum.values().length);
        return AppUserAgentEnum.values()[browserType].getUA();
    }

    /**
     * Chrome UA
     */
    private static String buildChrome() {

        // Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95
        // Safari/537.36
        Random random = new Random();

        StringBuilder builder = new StringBuilder();
        builder.append("Mozilla/");
        builder.append(random.nextInt(3) + 3);
        builder.append(" (X");
        builder.append(random.nextInt(10) + 5);
        builder.append(" Windows) AppleWebKit/");
        int mV = random.nextInt(50) + 510;
        int sV = random.nextInt(80) + 5;
        builder.append(mV);
        builder.append(".");
        builder.append(sV);
        builder.append(" (KHTML, like Gecko)");
        builder.append(" Chrome/");
        builder.append(UAConstants.chromeVersions.get(random.nextInt(UAConstants.chromeVersions.size())));

        builder.append(" Safari/");
        builder.append(mV);
        builder.append(".");
        builder.append(sV);
        return builder.toString();
    }

    /**
     * Firfox UA
     */
    private static String buildFirefox() {

        Random random = new Random();

        StringBuilder builder = new StringBuilder();
        builder.append("Mozilla/");
        builder.append(random.nextInt(3) + 3);
        builder.append(" (X");
        builder.append(random.nextInt(10) + 5);
        builder.append(" Window NT ");
        builder.append(random.nextInt(10));
        builder.append(".");
        builder.append(random.nextInt(10));
        builder.append(" rv:");

        int mV = random.nextInt(30) + 15;
        int sV = random.nextInt(20) + 10;
        int lV = random.nextInt(10);

        builder.append(mV);
        builder.append(".");
        builder.append(sV);
        builder.append(".");
        builder.append(lV);
        builder.append(") Gecko/20100101 Firefox/");
        builder.append(mV);
        builder.append(".");
        builder.append(sV);
        builder.append(".");
        builder.append(lV);

        builder.append(" Firefox/");
        builder.append(UAConstants.firefoxVersions.get(random.nextInt(UAConstants.firefoxVersions.size())));

        return builder.toString();
    }

}
