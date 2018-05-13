package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

class JarFileEntries implements CentralDirectoryVisitor, Iterable {
    private static final long LOCAL_FILE_HEADER_SIZE = 30L;
    private static final String SLASH = "/";
    private static final String NO_SUFFIX = "";
    protected static final int ENTRY_CACHE_SIZE = 25;
    private final JarFile jarFile;
    private final JarEntryFilter filter;
    private RandomAccessData centralDirectoryData;
    private int size;
    private int[] hashCodes;
    private int[] centralDirectoryOffsets;
    private int[] positions;
    private final Map<Integer, FileHeader> entriesCache = Collections.synchronizedMap(new LinkedHashMap<Integer, FileHeader>(16, 0.75F, true) {
        protected boolean removeEldestEntry(Entry eldest) {
            return !JarFileEntries.this.jarFile.isSigned() && this.size() >= 25;
        }
    });

    JarFileEntries(JarFile jarFile, JarEntryFilter filter) {
        this.jarFile = jarFile;
        this.filter = filter;
    }

    public void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
        int maxSize = endRecord.getNumberOfRecords();
        this.centralDirectoryData = centralDirectoryData;
        this.hashCodes = new int[maxSize];
        this.centralDirectoryOffsets = new int[maxSize];
        this.positions = new int[maxSize];
    }

    public void visitFileHeader(CentralDirectoryFileHeader fileHeader, int dataOffset) {
        AsciiBytes name = this.applyFilter(fileHeader.getName());
        if (name != null) {
            this.add(name, dataOffset);
        }

    }

    private void add(AsciiBytes name, int dataOffset) {
        this.hashCodes[this.size] = name.hashCode();
        this.centralDirectoryOffsets[this.size] = dataOffset;
        this.positions[this.size] = this.size++;
    }

    public void visitEnd() {
        this.sort(0, this.size - 1);
        int[] positions = this.positions;
        this.positions = new int[positions.length];

        for (int i = 0; i < this.size; this.positions[positions[i]] = i++) {

        }

    }

    private void sort(int left, int right) {
        if (left < right) {
            int pivot = this.hashCodes[left + (right - left) / 2];
            int i = left;
            int j = right;

            while (i <= j) {
                while (this.hashCodes[i] < pivot) {
                    ++i;
                }

                while (this.hashCodes[j] > pivot) {
                    --j;
                }

                if (i <= j) {
                    this.swap(i, j);
                    ++i;
                    --j;
                }
            }

            if (left < j) {
                this.sort(left, j);
            }

            if (right > i) {
                this.sort(i, right);
            }
        }

    }

    private void swap(int i, int j) {
        this.swap(this.hashCodes, i, j);
        this.swap(this.centralDirectoryOffsets, i, j);
        this.swap(this.positions, i, j);
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public Iterator iterator() {
        return new JarFileEntries.EntryIterator();
    }

    boolean containsEntry(String name) {
        return this.getEntry(name, FileHeader.class, true) != null;
    }

    JarEntry getEntry(String name) {
        return (JarEntry) this.getEntry(name, JarEntry.class, true);
    }

    InputStream getInputStream(String name, RandomAccessData.ResourceAccess access) throws IOException {
        FileHeader entry = this.getEntry(name, FileHeader.class, false);
        return this.getInputStream(entry, access);
    }

    InputStream getInputStream(FileHeader entry, RandomAccessData.ResourceAccess access) throws IOException {
        if (entry == null) {
            return null;
        } else {
            InputStream inputStream = this.getEntryData(entry).getInputStream(access);
            if (entry.getMethod() == 8) {
                inputStream = new ZipInflaterInputStream(inputStream, (int) entry.getSize());
            }

            return inputStream;
        }
    }

    RandomAccessData getEntryData(String name) throws IOException {
        FileHeader entry = this.getEntry(name, FileHeader.class, false);
        return entry == null ? null : this.getEntryData(entry);
    }

    private RandomAccessData getEntryData(FileHeader entry) throws IOException {
        RandomAccessData data = this.jarFile.getData();
        byte[] localHeader = Bytes.get(data.getSubsection(entry.getLocalHeaderOffset(), 30L));
        long nameLength = Bytes.littleEndianValue(localHeader, 26, 2);
        long extraLength = Bytes.littleEndianValue(localHeader, 28, 2);
        return data.getSubsection(entry.getLocalHeaderOffset() + 30L + nameLength + extraLength, entry.getCompressedSize());
    }

    private FileHeader getEntry(String name, Class type, boolean cacheEntry) {
        int hashCode = AsciiBytes.hashCode(name);
        FileHeader entry = this.getEntry(hashCode, name, "", type, cacheEntry);
        if (entry == null) {
            hashCode = AsciiBytes.hashCode(hashCode, "/");
            entry = this.getEntry(hashCode, name, "/", type, cacheEntry);
        }

        return entry;
    }

    private FileHeader getEntry(int hashCode, String name, String suffix, Class type, boolean cacheEntry) {
        for (int index = this.getFirstIndex(hashCode); index >= 0 && index < this.size && this.hashCodes[index] == hashCode; ++index) {
            FileHeader entry = this.getEntry(index, type, cacheEntry);
            if (entry.hasName(name, suffix)) {
                return entry;
            }
        }

        return null;
    }

    private FileHeader getEntry(int index, Class type, boolean cacheEntry) {
        try {
            FileHeader cached = this.entriesCache.get(index);
            FileHeader entry = cached != null ? cached : CentralDirectoryFileHeader.fromRandomAccessData(this.centralDirectoryData, this.centralDirectoryOffsets[index], this.filter);
            if (CentralDirectoryFileHeader.class.equals(entry.getClass()) && type.equals(JarEntry.class)) {
                entry = new JarEntry(this.jarFile, (CentralDirectoryFileHeader) entry);
            }

            if (cacheEntry && cached != entry) {
                this.entriesCache.put(index, entry);
            }

            return entry;
        } catch (IOException var6) {
            throw new IllegalStateException(var6);
        }
    }

    private int getFirstIndex(int hashCode) {
        int index = Arrays.binarySearch(this.hashCodes, 0, this.size, hashCode);
        if (index < 0) {
            return -1;
        } else {
            while (index > 0 && this.hashCodes[index - 1] == hashCode) {
                --index;
            }

            return index;
        }
    }

    void clearCache() {
        this.entriesCache.clear();
    }

    private AsciiBytes applyFilter(AsciiBytes name) {
        return this.filter == null ? name : this.filter.apply(name);
    }

    private class EntryIterator implements Iterator {
        private int index;

        private EntryIterator() {
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return this.index < JarFileEntries.this.size;
        }

        @Override
        public JarEntry next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            int entryIndex = JarFileEntries.this.positions[this.index];
            ++this.index;
            return (JarEntry) JarFileEntries.this.getEntry(entryIndex, JarEntry.class, false);

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("unsupport");
        }

    }
}
