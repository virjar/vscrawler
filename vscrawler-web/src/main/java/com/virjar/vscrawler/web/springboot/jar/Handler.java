package com.virjar.vscrawler.web.springboot.jar;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler extends URLStreamHandler {
    private static final String JAR_PROTOCOL = "jar:";
    private static final String FILE_PROTOCOL = "file:";
    private static final String SEPARATOR = "!/";
    private static final String[] FALLBACK_HANDLERS = new String[]{"sun.net.www.protocol.jar.Handler"};
    private static final Method OPEN_CONNECTION_METHOD;
    private static SoftReference<Map<File, JarFile>> rootFileCache;
    private final JarFile jarFile;
    private URLStreamHandler fallbackHandler;

    public Handler() {
        this((JarFile) null);
    }

    Handler(JarFile jarFile) {
        this.jarFile = jarFile;
    }

    protected URLConnection openConnection(URL url) throws IOException {
        if (this.jarFile != null) {
            return JarURLConnection.get(url, this.jarFile);
        } else {
            try {
                return JarURLConnection.get(url, this.getRootJarFileFromUrl(url));
            } catch (Exception var3) {
                return this.openFallbackConnection(url, var3);
            }
        }
    }

    private URLConnection openFallbackConnection(URL url, Exception reason) throws IOException {
        try {
            return this.openConnection(this.getFallbackHandler(), url);
        } catch (Exception var4) {
            if (reason instanceof IOException) {
                this.log(false, "Unable to open fallback handler", var4);
                throw (IOException) reason;
            } else {
                this.log(true, "Unable to open fallback handler", var4);
                if (reason instanceof RuntimeException) {
                    throw (RuntimeException) reason;
                } else {
                    throw new IllegalStateException(reason);
                }
            }
        }
    }

    private void log(boolean warning, String message, Exception cause) {
        try {
            Logger.getLogger(this.getClass().getName()).log(warning ? Level.WARNING : Level.FINEST, message, cause);
        } catch (Exception var5) {
            if (warning) {
                System.err.println("WARNING: " + message);
            }
        }

    }

    private URLStreamHandler getFallbackHandler() {
        if (this.fallbackHandler != null) {
            return this.fallbackHandler;
        } else {
            String[] var1 = FALLBACK_HANDLERS;
            int var2 = var1.length;
            int var3 = 0;

            while (var3 < var2) {
                String handlerClassName = var1[var3];

                try {
                    Class handlerClass = Class.forName(handlerClassName);
                    this.fallbackHandler = (URLStreamHandler) handlerClass.newInstance();
                    return this.fallbackHandler;
                } catch (Exception var6) {
                    ++var3;
                }
            }

            throw new IllegalStateException("Unable to find fallback handler");
        }
    }

    private URLConnection openConnection(URLStreamHandler handler, URL url) throws Exception {
        if (OPEN_CONNECTION_METHOD == null) {
            throw new IllegalStateException("Unable to invoke fallback open connection method");
        } else {
            OPEN_CONNECTION_METHOD.setAccessible(true);
            return (URLConnection) OPEN_CONNECTION_METHOD.invoke(handler, url);
        }
    }

    protected void parseURL(URL context, String spec, int start, int limit) {
        if (spec.toLowerCase().startsWith("jar:")) {
            this.setFile(context, this.getFileFromSpec(spec.substring(start, limit)));
        } else {
            this.setFile(context, this.getFileFromContext(context, spec.substring(start, limit)));
        }

    }

    private String getFileFromSpec(String spec) {
        int separatorIndex = spec.lastIndexOf("!/");
        if (separatorIndex == -1) {
            throw new IllegalArgumentException("No !/ in spec '" + spec + "'");
        } else {
            try {
                new URL(spec.substring(0, separatorIndex));
                return spec;
            } catch (MalformedURLException var4) {
                throw new IllegalArgumentException("Invalid spec URL '" + spec + "'", var4);
            }
        }
    }

    private String getFileFromContext(URL context, String spec) {
        String file = context.getFile();
        if (spec.startsWith("/")) {
            return this.trimToJarRoot(file) + "!/" + spec.substring(1);
        } else if (file.endsWith("/")) {
            return file + spec;
        } else {
            int lastSlashIndex = file.lastIndexOf(47);
            if (lastSlashIndex == -1) {
                throw new IllegalArgumentException("No / found in context URL's file '" + file + "'");
            } else {
                return file.substring(0, lastSlashIndex + 1) + spec;
            }
        }
    }

    private String trimToJarRoot(String file) {
        int lastSeparatorIndex = file.lastIndexOf("!/");
        if (lastSeparatorIndex == -1) {
            throw new IllegalArgumentException("No !/ found in context URL's file '" + file + "'");
        } else {
            return file.substring(0, lastSeparatorIndex);
        }
    }

    private void setFile(URL context, String file) {
        this.setURL(context, "jar:", null, -1, null, null, file, null, null);
    }

    protected int hashCode(URL u) {
        return this.hashCode(u.getProtocol(), u.getFile());
    }

    private int hashCode(String protocol, String file) {
        int result = protocol == null ? 0 : protocol.hashCode();
        int separatorIndex = file.indexOf("!/");
        if (separatorIndex == -1) {
            return result + file.hashCode();
        } else {
            String source = file.substring(0, separatorIndex);
            String entry = this.canonicalize(file.substring(separatorIndex + 2));

            try {
                result += (new URL(source)).hashCode();
            } catch (MalformedURLException var8) {
                result += source.hashCode();
            }

            result += entry.hashCode();
            return result;
        }
    }

    protected boolean sameFile(URL u1, URL u2) {
        if (u1.getProtocol().equals("jar") && u2.getProtocol().equals("jar")) {
            int separator1 = u1.getFile().indexOf("!/");
            int separator2 = u2.getFile().indexOf("!/");
            if (separator1 != -1 && separator2 != -1) {
                String nested1 = u1.getFile().substring(separator1 + "!/".length());
                String nested2 = u2.getFile().substring(separator2 + "!/".length());
                String root1;
                String root2;
                if (!nested1.equals(nested2)) {
                    root1 = this.canonicalize(nested1);
                    root2 = this.canonicalize(nested2);
                    if (!root1.equals(root2)) {
                        return false;
                    }
                }

                root1 = u1.getFile().substring(0, separator1);
                root2 = u2.getFile().substring(0, separator2);

                try {
                    return super.sameFile(new URL(root1), new URL(root2));
                } catch (MalformedURLException var10) {
                    return super.sameFile(u1, u2);
                }
            } else {
                return super.sameFile(u1, u2);
            }
        } else {
            return false;
        }
    }

    private String canonicalize(String path) {
        return path.replace("!/", "/");
    }

    private JarFile getRootJarFileFromUrl(URL url) throws IOException {
        String spec = url.getFile();
        int separatorIndex = spec.indexOf("!/");
        if (separatorIndex == -1) {
            throw new MalformedURLException("Jar URL does not contain !/ separator");
        } else {
            String name = spec.substring(0, separatorIndex);
            return this.getRootJarFile(name);
        }
    }

    private JarFile getRootJarFile(String name) throws IOException {
        try {
            if (!name.startsWith("file:")) {
                throw new IllegalStateException("Not a file URL");
            } else {
                String path = name.substring("file:".length());
                File file = new File(URLDecoder.decode(path, "UTF-8"));
                Map cache = (Map) rootFileCache.get();
                JarFile result = cache == null ? null : (JarFile) cache.get(file);
                if (result == null) {
                    result = new JarFile(file);
                    addToRootFileCache(file, result);
                }

                return result;
            }
        } catch (Exception var6) {
            throw new IOException("Unable to open root Jar file '" + name + "'", var6);
        }
    }

    private static void addToRootFileCache(File sourceFile, JarFile jarFile) {
        Map<File, JarFile> cache = rootFileCache.get();
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            rootFileCache = new SoftReference<>(cache);
        }

        cache.put(sourceFile, jarFile);
    }

    public static void setUseFastConnectionExceptions(boolean useFastConnectionExceptions) {
        JarURLConnection.setUseFastExceptions(useFastConnectionExceptions);
    }

    static {
        Method method = null;

        try {
            method = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class);
        } catch (Exception var2) {

        }

        OPEN_CONNECTION_METHOD = method;
        rootFileCache = new SoftReference<>(null);
    }
}
