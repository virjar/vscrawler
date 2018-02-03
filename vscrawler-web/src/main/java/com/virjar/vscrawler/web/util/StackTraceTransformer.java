package com.virjar.vscrawler.web.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Created by virjar on 2018/2/3.<br>
 * 将异常堆栈转化为字符串,用于前端渲染展示
 */
public class StackTraceTransformer {
    public static String getStackTrack(Throwable throwable) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream));
        throwable.printStackTrace(printWriter);
        return byteArrayOutputStream.toString();
    }
}
