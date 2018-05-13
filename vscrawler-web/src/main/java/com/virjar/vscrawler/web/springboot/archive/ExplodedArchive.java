package com.virjar.vscrawler.web.springboot.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.jar.Manifest;

public class ExplodedArchive implements Archive {
    private static final Set<String> SKIPPED_NAMES = new HashSet<>(Arrays.asList(".", ".."));
    private final File root;
    private final boolean recursive;
    private File manifestFile;
    private Manifest manifest;

    public ExplodedArchive(File root) {
        this(root, true);
    }

    public ExplodedArchive(File root, boolean recursive) {
        if (root.exists() && root.isDirectory()) {
            this.root = root;
            this.recursive = recursive;
            this.manifestFile = this.getManifestFile(root);
        } else {
            throw new IllegalArgumentException("Invalid source folder " + root);
        }
    }

    private File getManifestFile(File root) {
        File metaInf = new File(root, "META-INF");
        return new File(metaInf, "MANIFEST.MF");
    }

    public URL getUrl() throws MalformedURLException {
        return this.root.toURI().toURL();
    }

    public Manifest getManifest() throws IOException {
        if (this.manifest == null && this.manifestFile.exists()) {

            try (FileInputStream inputStream = new FileInputStream(this.manifestFile)) {
                this.manifest = new Manifest(inputStream);
            }
        }

        return this.manifest;
    }

    public List<Archive> getNestedArchives(Archive.EntryFilter filter) throws IOException {
        List<Archive> nestedArchives = new ArrayList<>();

        for (Object o : this) {
            Entry entry = (Entry) o;
            if (filter.matches(entry)) {
                nestedArchives.add(this.getNestedArchive(entry));
            }
        }

        return Collections.unmodifiableList(nestedArchives);
    }

    public Iterator<Archive.Entry> iterator() {
        return new ExplodedArchive.FileEntryIterator(this.root, this.recursive);
    }

    private Archive getNestedArchive(Archive.Entry entry) throws IOException {
        File file = ((ExplodedArchive.FileEntry) entry).getFile();
        return file.isDirectory() ? new ExplodedArchive(file) : new JarFileArchive(file);
    }

    public String toString() {
        try {
            return this.getUrl().toString();
        } catch (Exception var2) {
            return "exploded archive";
        }
    }

    private static class FileEntry implements Archive.Entry {
        private final String name;
        private final File file;

        FileEntry(String name, File file) {
            this.name = name;
            this.file = file;
        }

        public File getFile() {
            return this.file;
        }

        public boolean isDirectory() {
            return this.file.isDirectory();
        }

        public String getName() {
            return this.name;
        }
    }

    private static class FileEntryIterator implements Iterator<Archive.Entry> {
        private final Comparator entryComparator = new ExplodedArchive.FileEntryIterator.EntryComparator();
        private final File root;
        private final boolean recursive;
        private final Deque<Iterator<File>> stack = new LinkedList<>();
        private File current;

        FileEntryIterator(File root, boolean recursive) {
            this.root = root;
            this.recursive = recursive;
            this.stack.add(this.listFiles(root));
            this.current = this.poll();
        }

        public boolean hasNext() {
            return this.current != null;
        }

        public Archive.Entry next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            } else {
                File file = this.current;
                if (file.isDirectory() && (this.recursive || file.getParentFile().equals(this.root))) {
                    this.stack.addFirst(this.listFiles(file));
                }

                this.current = this.poll();
                String name = file.toURI().getPath().substring(this.root.toURI().getPath().length());
                return new ExplodedArchive.FileEntry(name, file);
            }
        }

        private Iterator<File> listFiles(File file) {
            File[] files = file.listFiles();
            if (files == null) {
                List<File> objects = Collections.emptyList();
                return objects.iterator();
            } else {
                Arrays.sort(files, this.entryComparator);
                return Arrays.asList(files).iterator();
            }
        }

        private File poll() {
            label17:
            while (true) {
                if (!this.stack.isEmpty()) {
                    File file;
                    do {
                        if (!((Iterator) this.stack.peek()).hasNext()) {
                            this.stack.poll();
                            continue label17;
                        }

                        file = (File) ((Iterator) this.stack.peek()).next();
                    } while (ExplodedArchive.SKIPPED_NAMES.contains(file.getName()));

                    return file;
                }

                return null;
            }
        }

        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        private static class EntryComparator implements Comparator<File> {
            private EntryComparator() {
            }

            @Override
            public int compare(File o1, File o2) {
                return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
            }

        }
    }
}
