package com.virjar.vscrawler.web.springboot;

import com.virjar.vscrawler.web.springboot.archive.Archive;

public class WarLauncher extends ExecutableArchiveLauncher {
    private static final String WEB_INF = "WEB-INF/";
    private static final String WEB_INF_CLASSES = "WEB-INF/classes/";
    private static final String WEB_INF_LIB = "WEB-INF/lib/";
    private static final String WEB_INF_LIB_PROVIDED = "WEB-INF/lib-provided/";

    private WarLauncher() {
    }

    protected WarLauncher(Archive archive) {
        super(archive);
    }

    public boolean isNestedArchive(Archive.Entry entry) {
        if (entry.isDirectory()) {
            return entry.getName().equals("WEB-INF/classes/");
        } else {
            return entry.getName().startsWith("WEB-INF/lib/") || entry.getName().startsWith("WEB-INF/lib-provided/");
        }
    }

    public static void main(String[] args) throws Exception {
        (new WarLauncher()).launch(args);
    }
}
