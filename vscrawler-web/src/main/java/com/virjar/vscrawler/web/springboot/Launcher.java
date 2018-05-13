package com.virjar.vscrawler.web.springboot;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

import com.virjar.vscrawler.web.springboot.archive.Archive;
import com.virjar.vscrawler.web.springboot.archive.ExplodedArchive;
import com.virjar.vscrawler.web.springboot.archive.JarFileArchive;
import com.virjar.vscrawler.web.springboot.jar.JarFile;

public abstract class Launcher {
    protected void launch(String[] args) throws Exception {
        JarFile.registerUrlProtocolHandler();
        ClassLoader classLoader = this.createClassLoader(this.getClassPathArchives());
        this.launch(args, this.getMainClass(), classLoader);
    }

    protected ClassLoader createClassLoader(List<Archive> archives) throws Exception {
        List<URL> urls = new ArrayList<>(archives.size());

        for (Archive archive : archives) {
            urls.add(archive.getUrl());
        }

        return this.createClassLoader(urls.toArray(new URL[urls.size()]));
    }

    private ClassLoader createClassLoader(URL[] urls) throws Exception {
        return new LaunchedURLClassLoader(urls, this.getClass().getClassLoader());
    }

    private void launch(String[] args, String mainClass, ClassLoader classLoader) throws Exception {
        Thread.currentThread().setContextClassLoader(classLoader);
        this.createMainMethodRunner(mainClass, args, classLoader).run();
    }

    private MainMethodRunner createMainMethodRunner(String mainClass, String[] args, ClassLoader classLoader) {
        return new MainMethodRunner(mainClass, args);
    }

    protected abstract String getMainClass() throws Exception;

    protected abstract List getClassPathArchives() throws Exception;

    final Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain = this.getClass().getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = codeSource == null ? null : codeSource.getLocation().toURI();
        String path = location == null ? null : location.getSchemeSpecificPart();
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        } else {
            File root = new File(path);
            if (!root.exists()) {
                throw new IllegalStateException("Unable to determine code source archive from " + root);
            } else {
                return root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root);
            }
        }
    }
}
