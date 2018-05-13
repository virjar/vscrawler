package com.virjar.vscrawler.web.springboot;

import com.virjar.vscrawler.web.springboot.archive.Archive;

public class JarLauncher extends ExecutableArchiveLauncher {
    static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    private JarLauncher() {
    }

    protected JarLauncher(Archive archive) {
        super(archive);
    }

    protected boolean isNestedArchive(Archive.Entry entry) {
        return entry.isDirectory() ? entry.getName().equals("BOOT-INF/classes/") : entry.getName().startsWith("BOOT-INF/lib/");
    }

    public static void main(String[] args) throws Exception {
        (new JarLauncher()).launch(args);
    }
}
