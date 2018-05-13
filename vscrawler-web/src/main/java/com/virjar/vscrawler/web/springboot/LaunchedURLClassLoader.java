package com.virjar.vscrawler.web.springboot;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Enumeration;
import java.util.jar.JarFile;

import com.virjar.vscrawler.web.springboot.jar.Handler;
import org.springframework.lang.UsesJava7;

class LaunchedURLClassLoader extends URLClassLoader {
    LaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public URL findResource(String name) {
        Handler.setUseFastConnectionExceptions(true);

        URL var2;
        try {
            var2 = super.findResource(name);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var2;
    }

    public Enumeration<URL> findResources(String name) throws IOException {
        Handler.setUseFastConnectionExceptions(true);

        Enumeration<URL> var2;
        try {
            var2 = super.findResources(name);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var2;
    }

    protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Handler.setUseFastConnectionExceptions(true);

        Class var3;
        try {
            try {
                this.definePackageIfNecessary(name);
            } catch (IllegalArgumentException var7) {
                if (this.getPackage(name) == null) {
                    throw new AssertionError("Package " + name + " has already been defined but it could not be found");
                }
            }

            var3 = super.loadClass(name, resolve);
        } finally {
            Handler.setUseFastConnectionExceptions(false);
        }

        return var3;
    }

    private void definePackageIfNecessary(String className) {
        int lastDot = className.lastIndexOf(46);
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (this.getPackage(packageName) == null) {
                try {
                    this.definePackage(className, packageName);
                } catch (IllegalArgumentException var5) {
                    if (this.getPackage(packageName) == null) {
                        throw new AssertionError("Package " + packageName + " has already been defined but it could not be found");
                    }
                }
            }
        }

    }

    private void definePackage(final String className, final String packageName) {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction() {
                public Object run() throws ClassNotFoundException {
                    String packageEntryName = packageName.replace('.', '/') + "/";
                    String classEntryName = className.replace('.', '/') + ".class";

                    for (URL url : LaunchedURLClassLoader.this.getURLs()) {
                        try {
                            URLConnection connection = url.openConnection();
                            if (connection instanceof JarURLConnection) {
                                JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                                if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null && jarFile.getManifest() != null) {
                                    LaunchedURLClassLoader.this.definePackage(packageName, jarFile.getManifest(), url);
                                    return null;
                                }
                            }
                        } catch (IOException ignored) {

                        }
                    }

                    return null;
                }
            }, AccessController.getContext());
        } catch (PrivilegedActionException ignored) {

        }

    }

    public void clearCache() {
        URL[] var1 = this.getURLs();

        for (URL url : var1) {
            try {
                URLConnection connection = url.openConnection();
                if (connection instanceof JarURLConnection) {
                    this.clearCache(connection);
                }
            } catch (IOException ignored) {

            }
        }

    }

    private void clearCache(URLConnection connection) throws IOException {
        Object jarFile = ((JarURLConnection) connection).getJarFile();
        if (jarFile instanceof com.virjar.vscrawler.web.springboot.jar.JarFile) {
            ((com.virjar.vscrawler.web.springboot.jar.JarFile) jarFile).clearCache();
        }

    }

    @UsesJava7
    private static void performParallelCapableRegistration() {
        try {
            ClassLoader.registerAsParallelCapable();
        } catch (NoSuchMethodError ignored) {

        }

    }

    static {
        performParallelCapableRegistration();
    }
}
