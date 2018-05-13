package com.virjar.vscrawler.web.springboot;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

import com.virjar.vscrawler.web.springboot.archive.Archive;

abstract class ExecutableArchiveLauncher extends Launcher {
    private final Archive archive;

    ExecutableArchiveLauncher() {
        try {
            this.archive = this.createArchive();
        } catch (Exception var2) {
            throw new IllegalStateException(var2);
        }
    }

    ExecutableArchiveLauncher(Archive archive) {
        this.archive = archive;
    }

    protected final Archive getArchive() {
        return this.archive;
    }

    protected String getMainClass() throws Exception {
        Manifest manifest = this.archive.getManifest();
        String mainClass = null;
        if (manifest != null) {
            mainClass = manifest.getMainAttributes().getValue("Start-Class");
        }

        if (mainClass == null) {
            throw new IllegalStateException("No 'Start-Class' manifest entry specified in " + this);
        } else {
            return mainClass;
        }
    }

    protected List<Archive> getClassPathArchives() throws Exception {
        List<Archive> archives = new ArrayList<>(this.archive.getNestedArchives(new Archive.EntryFilter() {
            public boolean matches(Archive.Entry entry) {
                return ExecutableArchiveLauncher.this.isNestedArchive(entry);
            }
        }));
        this.postProcessClassPathArchives(archives);
        return archives;
    }

    protected abstract boolean isNestedArchive(Archive.Entry var1);

    private void postProcessClassPathArchives(List archives) throws Exception {
    }
}
