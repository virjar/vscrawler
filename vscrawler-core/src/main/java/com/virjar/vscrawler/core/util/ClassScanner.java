package com.virjar.vscrawler.core.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

/**
 * 类扫描器
 *
 * @author virjar
 * @since 0.0.1
 */
@Slf4j
public class ClassScanner {

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

    public static <T> List<Class<? extends T>> scan(Class<T> pclazz) {
        SubClassVisitor<T> subClassVisitor = new SubClassVisitor<T>(false, pclazz);
        scan(subClassVisitor);
        return subClassVisitor.getSubClass();
    }

    public static <T> void scan(ClassVisitor<T> subClassVisitor) {
        Collection<String> emptBasePackage = Lists.newArrayList();
        scan(subClassVisitor, emptBasePackage);
    }

    public static <T> void scan(ClassVisitor<T> subClassVisitor, Collection<String> basePackages) {

        List<File> jarFiles = allJar();
        if (jarFiles.size() == 0) {
            URL location = ClassScanner.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null) {
                jarFiles.add(new File(location.getPath()));
            }
        }
        for (File f : jarFiles) {
            scan(f, subClassVisitor, basePackages);
        }
    }

    private static List<File> allJar() {
        Set<String> jars = findJars(ClassScanner.class.getClassLoader());
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

    public static class AnnotationClassVisitor implements ClassScanner.ClassVisitor {
        private Class annotationClazz;
        private Set<Class> classSet = Sets.newHashSet();

        public AnnotationClassVisitor(Class annotationClazz) {
            this.annotationClazz = annotationClazz;
        }

        @Override
        public void visit(Class clazz) {
            try {
                if (clazz.getAnnotation(annotationClazz) != null) {
                    classSet.add(clazz);
                }
            } catch (Throwable e) {
                // do nothing 可能有classNotFoundException
            }
        }

        public Set<Class> getClassSet() {
            return classSet;
        }
    }

    public static class AnnotationMethodVisitor implements ClassScanner.ClassVisitor {
        private Class annotationClazz;
        private Set<Method> methodSet = Sets.newHashSet();

        public AnnotationMethodVisitor(Class annotationClazz) {
            this.annotationClazz = annotationClazz;
        }

        @Override
        public void visit(Class clazz) {
            try {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(annotationClazz) != null) {
                        methodSet.add(method);
                    }
                }
            } catch (Throwable e) {
                // do nothing 可能有classNotFoundException
            }
        }

        public Set<Method> getMethodSet() {
            return methodSet;
        }
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

    public static <T> void scan(File f, ClassVisitor<T> classVisitor, Collection<String> basePackages) {

        if (f.isDirectory()) {
            List<File> classFileList = new ArrayList<File>();
            scanClass(classFileList, f.getPath());
            for (File file : classFileList) {

                int start = f.getPath().length();
                int end = file.toString().length() - 6; // 6 == ".class".length();

                String classFile = file.toString().substring(start + 1, end);
                String className = classFile.replace(File.separator, ".");
                visitClass(className, basePackages, classVisitor);
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
                    visitClass(className, basePackages, classVisitor);
                }
            }
        } catch (IOException e1) {
        } finally {
            IOUtils.closeQuietly(jarFile);
        }

    }

    private static <T> void visitClass(String className, Collection<String> basePackages,
                                       ClassVisitor<T> classVisitor) {
        if (basePackages.size() == 0) {
            Class<T> clazz = classForName(className);
            if (clazz != null) {
                classVisitor.visit(clazz);
            }
        } else {
            boolean needVisit = false;
            for (String basePackage : basePackages) {
                if (className.startsWith(basePackage)) {
                    needVisit = true;
                    break;
                }
            }
            if (needVisit) {
                Class<T> clazz = classForName(className);
                if (clazz != null) {
                    classVisitor.visit(clazz);
                }
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
