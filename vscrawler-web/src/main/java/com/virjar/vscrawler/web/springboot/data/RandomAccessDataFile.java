package com.virjar.vscrawler.web.springboot.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

public class RandomAccessDataFile implements RandomAccessData {
    private final File file;
    private final RandomAccessDataFile.FilePool filePool;
    private final long offset;
    private final long length;

    public RandomAccessDataFile(File file) {
        this(file, 4);
    }

    private RandomAccessDataFile(File file, int concurrentReads) {
        if (file == null) {
            throw new IllegalArgumentException("File must not be null");
        } else if (!file.exists()) {
            throw new IllegalArgumentException("File must exist");
        } else {
            this.file = file;
            this.filePool = new RandomAccessDataFile.FilePool(file, concurrentReads);
            this.offset = 0L;
            this.length = file.length();
        }
    }

    private RandomAccessDataFile(File file, RandomAccessDataFile.FilePool pool, long offset, long length) {
        this.file = file;
        this.filePool = pool;
        this.offset = offset;
        this.length = length;
    }

    public File getFile() {
        return this.file;
    }

    public InputStream getInputStream(RandomAccessData.ResourceAccess access) throws IOException {
        return new RandomAccessDataFile.DataInputStream(access);
    }

    public RandomAccessData getSubsection(long offset, long length) {
        if (offset >= 0L && length >= 0L && offset + length <= this.length) {
            return new RandomAccessDataFile(this.file, this.filePool, this.offset + offset, length);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public long getSize() {
        return this.length;
    }

    public void close() throws IOException {
        this.filePool.close();
    }

    private static class FilePool {
        private final File file;
        private final int size;
        private final Semaphore available;
        private final Queue<RandomAccessFile> files;

        FilePool(File file, int size) {
            this.file = file;
            this.size = size;
            this.available = new Semaphore(size);
            this.files = new ConcurrentLinkedQueue<>();
        }

        RandomAccessFile acquire() throws IOException {
            this.available.acquireUninterruptibly();
            RandomAccessFile file = this.files.poll();
            return file != null ? file : new RandomAccessFile(this.file, "r");
        }

        void release(RandomAccessFile file) {
            this.files.add(file);
            this.available.release();
        }

        public void close() throws IOException {
            this.available.acquireUninterruptibly(this.size);

            try {
                for (RandomAccessFile pooledFile = this.files.poll(); pooledFile != null; pooledFile = this.files.poll()) {
                    pooledFile.close();
                }
            } finally {
                this.available.release(this.size);
            }

        }
    }

    private class DataInputStream extends InputStream {
        private RandomAccessFile file;
        private int position;

        DataInputStream(RandomAccessData.ResourceAccess access) throws IOException {
            if (access == RandomAccessData.ResourceAccess.ONCE) {
                this.file = new RandomAccessFile(RandomAccessDataFile.this.file, "r");
                this.file.seek(RandomAccessDataFile.this.offset);
            }

        }

        public int read() throws IOException {
            return this.doRead(null, 0, 1);
        }

        public int read(byte[] b) throws IOException {
            return this.read(b, 0, b == null ? 0 : b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException("Bytes must not be null");
            } else {
                return this.doRead(b, off, len);
            }
        }

        int doRead(byte[] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            } else {
                int cappedLen = this.cap((long) len);
                if (cappedLen <= 0) {
                    return -1;
                } else {
                    RandomAccessFile file = this.file;

                    int rtn;
                    try {
                        if (file == null) {
                            file = RandomAccessDataFile.this.filePool.acquire();
                            file.seek(RandomAccessDataFile.this.offset + (long) this.position);
                        }

                        if (b == null) {
                            rtn = file.read();
                            this.moveOn(rtn == -1 ? 0 : 1);
                            return rtn;
                        }

                        rtn = (int) this.moveOn(file.read(b, off, cappedLen));
                    } finally {
                        if (this.file == null && file != null) {
                            RandomAccessDataFile.this.filePool.release(file);
                        }

                    }

                    return rtn;
                }
            }
        }

        public long skip(long n) throws IOException {
            return n <= 0L ? 0L : this.moveOn(this.cap(n));
        }

        public void close() throws IOException {
            if (this.file != null) {
                this.file.close();
            }

        }

        private int cap(long n) {
            return (int) Math.min(RandomAccessDataFile.this.length - (long) this.position, n);
        }

        private long moveOn(int amount) {
            this.position += amount;
            return (long) amount;
        }
    }
}
