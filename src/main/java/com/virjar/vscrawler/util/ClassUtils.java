package com.virjar.vscrawler.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassUtils {

    private static Set<String> excludeClassLoader = Sets.newHashSet("sun.misc.Launcher$ExtClassLoader");
    private static Set<String> excludeJarFiles = Sets.newHashSet("charsets.jar", "deploy.jar", "cldrdata.jar",
            "dnsns.jar", "jaccess.jar", "jfxrt.jar", "localedata.jar", "nashorn.jar", "sunec.jar",
            "sunjce_provider.jar", "sunpkcs11.jar", "zipfs.jar", "javaws.jar", "jce.jar", "jfr.jar", "jfxswt.jar",
            "jsse.jar", "management-agent.jar", "plugin.jar", "resources.jar", "rt.jar", "ant-javafx.jar", "dt.jar",
            "javafx-mx.jar", "jconsole.jar", "packager.jar", "sa-jdi.jar", "tools.jar");

    public static void main(String[] args) {
        List<File> files = allJar();

        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }

    }

    public static <T> List<Class<? extends T>> scanSubClass(Class<T> pclazz) {
        SubClassVisitor<T> subClassVisitor = new SubClassVisitor<T>(false, pclazz);
        scanSubClass(pclazz, subClassVisitor);
        return subClassVisitor.getSubClass();
    }

    public static <T> void scanSubClass(Class<T> pclazz, SubClassVisitor<T> subClassVisitor) {
        if (pclazz == null) {
            log.error("scanClass: parent clazz is null");
            return;
        }
        List<File> jarFiles = allJar();
        for (File f : jarFiles) {
            scanSubClass(pclazz, f, subClassVisitor);
        }
    }

    private static List<File> allJar() {
        Set<String> jars = findJars(ClassUtils.class.getClassLoader());
        List<File> ret = new ArrayList<>(jars.size());
        for (String fileName : jars) {
            ret.add(new File(fileName));
        }
        return ret;
    }

    private static boolean isExculed(String jarPath) {
        for (String exculde : excludeJarFiles) {
            if (jarPath.endsWith(exculde)) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> findJars(ClassLoader classLoader) {
        Set<String> ret = new HashSet<>();
        if (classLoader instanceof URLClassLoader && !excludeClassLoader.contains(classLoader.getClass().getName())) {
            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
            URL[] urLs = urlClassLoader.getURLs();
            for (URL url : urLs) {
                String s = url.toString();
                if (s.startsWith("file:") && !isExculed(s)) {// URL对象是抽象的,可能不是本地文件,可能是一个目录,这里就不讨论如何处理了
                    ret.add(url.getPath());
                }
            }
        }
        ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            ret.addAll(findJars(parent));
        }
        return ret;
    }

    public interface ClassVisitor<T> {
        void visit(Class<? extends T> clazz);
    }

    public static class SubClassVisitor<T> implements ClassVisitor {

        private boolean mustCanInstance = false;
        private List<Class<? extends T>> subClass = Lists.newArrayList();
        private Class<T> parentClass;

        public SubClassVisitor(boolean mustCanInstance, Class<T> parentClass) {
            this.mustCanInstance = mustCanInstance;
            this.parentClass = parentClass;
        }

        public List<Class<? extends T>> getSubClass() {
            return subClass;
        }

        @Override
        public void visit(Class clazz) {
            if (clazz != null && parentClass.isAssignableFrom(clazz)) {
                if (mustCanInstance) {
                    if (clazz.isInterface())
                        return;

                    if (Modifier.isAbstract(clazz.getModifiers()))
                        return;
                }
                subClass.add(clazz);
            }
        }

    }

    public static <T> void scanSubClass(Class<T> pclazz, File f, ClassVisitor<T> classVisitor) {
        if (pclazz == null) {
            log.error("scanClass: parent clazz is null");
            return;
        }

        if (f.isDirectory()) {
            List<File> classFileList = new ArrayList<File>();
            scanClass(classFileList, f.getPath());
            for (File file : classFileList) {

                int start = f.getPath().length();
                int end = file.toString().length() - 6; // 6 == ".class".length();

                String classFile = file.toString().substring(start + 1, end);
                Class<T> clazz = classForName(classFile.replace(File.separator, "."));
                classVisitor.visit(clazz);
            }
            return;
        }

        JarFile jarFile = null;

        try {
            jarFile = new JarFile(f);
            Enumeration<JarEntry> entries = jarFile.entries();

            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String entryName = jarEntry.getName();
                if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                    String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                    Class<T> clazz = classForName(className);
                    classVisitor.visit(clazz);
                }
            }

        } catch (IOException e1) {
        } finally {
            if (jarFile != null)
                try {
                    jarFile.close();
                } catch (IOException e) {
                }
        }

    }

    private static Set<String> cannotLoadClassNames = new HashSet<>();

    @SuppressWarnings("unchecked")
    private static <T> Class<T> classForName(String className) {
        if (cannotLoadClassNames.contains(className)) {
            return null;
        }
        Class<T> clazz = null;
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            clazz = (Class<T>) Class.forName(className, false, cl);
        } catch (Throwable e) {
            cannotLoadClassNames.add(className);
            // 取消日志打印,因为失败的东西不少
            // log.error("classForName is error，className:" + className);
        }
        return clazz;
    }

    private static void scanClass(List<File> fileList, String path) {
        File files[] = new File(path).listFiles();
        if (null == files || files.length == 0)
            return;
        for (File file : files) {
            if (file.isDirectory()) {
                scanClass(fileList, file.getAbsolutePath());
            } else if (file.getName().endsWith(".class")) {
                fileList.add(file);
            }
        }
    }

}
