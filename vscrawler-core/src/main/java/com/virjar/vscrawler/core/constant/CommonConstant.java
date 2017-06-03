package com.virjar.vscrawler.core.constant;

/**
 * 公用枚举常量
 * Created by mario1oreo on 2017/6/3.
 */
public class CommonConstant {
    public static enum JSONStringType {
        // Json 数组
        JSON_ARRAY("0"),
        // Json 对象
        JSON_OBJECT("1");

        public String getValue() {
            return value;
        }

        private String value;

        private JSONStringType(String string) {
            this.value = string;
        }
    }
}
