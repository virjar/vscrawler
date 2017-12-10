package com.virjar.vscrawler.core.net.useragent;

import java.util.Random;

/**
 * Created by tyreke.xu on 21/09/2017.
 */
public enum AppUserAgentEnum {

    Android_Chrome(1, "android chrome ua") {
        @Override
        public String getUA() {
            Random random = new Random();

            StringBuilder builder = new StringBuilder();
            builder.append("Mozilla/5.0 ");
            builder.append("(");
            builder.append(UAConstants.androidDevices.get(random.nextInt(UAConstants.androidDevices.size())));
            builder.append(") AppleWebKit/");
            int mV = random.nextInt(50) + 510;
            int sV = random.nextInt(80) + 5;
            builder.append(mV);
            builder.append(".");
            builder.append(sV);
            builder.append(" (KHTML, like Gecko)");
            builder.append(" Chrome/");
            builder.append(UAConstants.chromeVersions.get(random.nextInt(UAConstants.chromeVersions.size())));
            builder.append(" Mobile Safari/");
            builder.append(mV);
            builder.append(".");
            builder.append(sV);
            return builder.toString();
        }
    },

    IOS(2, "ios ua") {
        @Override
        public String getUA() {
            Random random = new Random();

            StringBuilder builder = new StringBuilder();
            builder.append("Mozilla/5.0 ");
            builder.append("(");
            builder.append(UAConstants.iphoneDevices.get(random.nextInt(UAConstants.iphoneDevices.size())));
            builder.append(") AppleWebKit/");
            int mV = random.nextInt(50) + 510;
            int sV = random.nextInt(80) + 5;
            builder.append(mV);
            builder.append(".");
            builder.append(sV);
            builder.append(" (KHTML, like Gecko)");
            builder.append(" Version/9.0 ");
            builder.append(UAConstants.iphoneDeviceNums.get(random.nextInt(UAConstants.iphoneDeviceNums.size())));
            builder.append(" Safari/");
            builder.append(mV);
            builder.append(".");
            builder.append(sV);
            return builder.toString();
        }
    };

    private int code;
    private String desc;

    public abstract String getUA();

    AppUserAgentEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
