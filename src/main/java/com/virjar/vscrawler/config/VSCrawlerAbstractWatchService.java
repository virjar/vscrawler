package com.virjar.vscrawler.config;

import java.io.IOException;
import java.nio.file.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.*;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * Created by virjar on 17/5/4.<br/>
 * 看了一下,jdk的实现也是启动了一个线程,主动探测文件夹或者文件的修改时间,内部维护了一个线程。这样的话这里可以优化,节省一个线程的开销
 */
public class VSCrawlerAbstractWatchService implements WatchService {
    @Override
    public void close() throws IOException {

    }

    @Override
    public WatchKey poll() {
        return null;
    }

    @Override
    public WatchKey poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public WatchKey take() throws InterruptedException {
        return null;
    }
    /**
    private final Map<Object, VSCrawlerAbstractWatchService.PollingWatchKey> map = new HashMap();
    private final ScheduledExecutorService scheduledExecutor = Executors
            .newSingleThreadScheduledExecutor(new ThreadFactory() {
                public Thread newThread(Runnable var1) {
                    Thread var2 = new Thread(var1);
                    var2.setDaemon(true);
                    return var2;
                }
            });

    private final LinkedBlockingDeque<WatchKey> pendingKeys = new LinkedBlockingDeque();
    private final WatchKey CLOSE_KEY = new AbstractWatchKey((Path) null, (AbstractWatchService) null) {
        public boolean isValid() {
            return true;
        }

        public void cancel() {
        }
    };
    private volatile boolean closed;
    private final Object closeLock = new Object();

    protected VSCrawlerAbstractWatchService() {
    }

    WatchKey register(Path var1, WatchEvent.Kind<?>[] var2, WatchEvent.Modifier... var3) throws IOException {
        final HashSet var4 = new HashSet(var2.length);
        WatchEvent.Kind[] var5 = var2;
        int var6 = var2.length;

        int var7;
        for (var7 = 0; var7 < var6; ++var7) {
            WatchEvent.Kind var8 = var5[var7];
            if (var8 != StandardWatchEventKinds.ENTRY_CREATE && var8 != StandardWatchEventKinds.ENTRY_MODIFY
                    && var8 != StandardWatchEventKinds.ENTRY_DELETE) {
                if (var8 != StandardWatchEventKinds.OVERFLOW) {
                    if (var8 == null) {
                        throw new NullPointerException("An element in event set is \'null\'");
                    }

                    throw new UnsupportedOperationException(var8.name());
                }
            } else {
                var4.add(var8);
            }
        }

        if (var4.isEmpty()) {
            throw new IllegalArgumentException("No events to register");
        } else {
            final SensitivityWatchEventModifier var11 = SensitivityWatchEventModifier.MEDIUM;
            if (var3.length > 0) {
                WatchEvent.Modifier[] var12 = var3;
                var7 = var3.length;

                for (int var14 = 0; var14 < var7; ++var14) {
                    WatchEvent.Modifier var9 = var12[var14];
                    if (var9 == null) {
                        throw new NullPointerException();
                    }

                    if (!(var9 instanceof SensitivityWatchEventModifier)) {
                        throw new UnsupportedOperationException("Modifier not supported");
                    }

                    var11 = (SensitivityWatchEventModifier) var9;
                }
            }

            if (!this.isOpen()) {
                throw new ClosedWatchServiceException();
            } else {
                try {
                    return (WatchKey) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                        public VSCrawlerAbstractWatchService.PollingWatchKey run() throws IOException {
                            return VSCrawlerAbstractWatchService.this.doPrivilegedRegister(var1, var4, var11);
                        }
                    });
                } catch (PrivilegedActionException var10) {
                    Throwable var13 = var10.getCause();
                    if (var13 != null && var13 instanceof IOException) {
                        throw (IOException) var13;
                    } else {
                        throw new AssertionError(var10);
                    }
                }
            }
        }
    }

    final void enqueueKey(WatchKey var1) {
        this.pendingKeys.offer(var1);
    }

    private void checkOpen() {
        if (this.closed) {
            throw new ClosedWatchServiceException();
        }
    }

    private void checkKey(WatchKey var1) {
        if (var1 == this.CLOSE_KEY) {
            this.enqueueKey(var1);
        }

        this.checkOpen();
    }

    public final WatchKey poll() {
        this.checkOpen();
        WatchKey var1 = (WatchKey) this.pendingKeys.poll();
        this.checkKey(var1);
        return var1;
    }

    public final WatchKey poll(long var1, TimeUnit var3) throws InterruptedException {
        this.checkOpen();
        WatchKey var4 = (WatchKey) this.pendingKeys.poll(var1, var3);
        this.checkKey(var4);
        return var4;
    }

    public final WatchKey take() throws InterruptedException {
        this.checkOpen();
        WatchKey var1 = (WatchKey) this.pendingKeys.take();
        this.checkKey(var1);
        return var1;
    }

    final boolean isOpen() {
        return !this.closed;
    }

    final Object closeLock() {
        return this.closeLock;
    }

    void implClose() throws IOException {
        Map var1 = this.map;
        synchronized (this.map) {
            Iterator var2 = this.map.entrySet().iterator();

            while (true) {
                if (!var2.hasNext()) {
                    this.map.clear();
                    break;
                }

                Map.Entry var3 = (Map.Entry) var2.next();
                VSCrawlerAbstractWatchService.PollingWatchKey var4 = (VSCrawlerAbstractWatchService.PollingWatchKey) var3
                        .getValue();
                var4.disable();
                var4.invalidate();
            }
        }

        AccessController.doPrivileged(new PrivilegedAction() {
            public Void run() {
                VSCrawlerAbstractWatchService.this.scheduledExecutor.shutdown();
                return null;
            }
        });
    }

    public final void close() throws IOException {
        Object var1 = this.closeLock;
        synchronized (this.closeLock) {
            if (!this.closed) {
                this.closed = true;
                this.implClose();
                this.pendingKeys.clear();
                this.pendingKeys.offer(this.CLOSE_KEY);
            }
        }
    }

    private class PollingWatchKey extends AbstractWatchKey {
        private final Object fileKey;
        private Set<? extends WatchEvent.Kind<?>> events;
        private ScheduledFuture<?> poller;
        private volatile boolean valid;
        private int tickCount;
        private Map<Path, VSCrawlerAbstractWatchService.CacheEntry> entries;

        PollingWatchKey(Path var2, VSCrawlerAbstractWatchService var3, Object var4) throws IOException {
            super(var2, var3);
            this.fileKey = var4;
            this.valid = true;
            this.tickCount = 0;
            this.entries = new HashMap();

            try {
                DirectoryStream var5 = Files.newDirectoryStream(var2);
                Throwable var6 = null;

                try {
                    Iterator var7 = var5.iterator();

                    while (var7.hasNext()) {
                        Path var8 = (Path) var7.next();
                        long var9 = Files.getLastModifiedTime(var8, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })
                                .toMillis();
                        this.entries.put(var8.getFileName(),
                                new VSCrawlerAbstractWatchService.CacheEntry(var9, this.tickCount));
                    }
                } catch (Throwable var19) {
                    var6 = var19;
                    throw var19;
                } finally {
                    if (var5 != null) {
                        if (var6 != null) {
                            try {
                                var5.close();
                            } catch (Throwable var18) {
                                var6.addSuppressed(var18);
                            }
                        } else {
                            var5.close();
                        }
                    }

                }

            } catch (DirectoryIteratorException var21) {
                throw var21.getCause();
            }
        }

        Object fileKey() {
            return this.fileKey;
        }

        public boolean isValid() {
            return this.valid;
        }

        void invalidate() {
            this.valid = false;
        }

        void enable(Set<? extends WatchEvent.Kind<?>> var1, long var2) {
            synchronized (this) {
                this.events = var1;
                Runnable var5 = new Runnable() {
                    public void run() {
                        VSCrawlerAbstractWatchService.PollingWatchKey.this.poll();
                    }
                };
                this.poller = VSCrawlerAbstractWatchService.this.scheduledExecutor.scheduleAtFixedRate(var5, var2, var2,
                        TimeUnit.SECONDS);
            }
        }

        void disable() {
            synchronized (this) {
                if (this.poller != null) {
                    this.poller.cancel(false);
                }

            }
        }

        public void cancel() {
            this.valid = false;
            synchronized (VSCrawlerAbstractWatchService.this.map) {
                VSCrawlerAbstractWatchService.this.map.remove(this.fileKey());
            }

            this.disable();
        }

        synchronized void poll() {
            if (this.valid) {
                ++this.tickCount;
                DirectoryStream var1 = null;

                try {
                    var1 = Files.newDirectoryStream(this.watchable());
                } catch (IOException var17) {
                    this.cancel();
                    this.signal();
                    return;
                }

                Iterator var2;
                try {
                    var2 = var1.iterator();

                    while (var2.hasNext()) {
                        Path var3 = (Path) var2.next();
                        long var4 = 0L;

                        try {
                            var4 = Files.getLastModifiedTime(var3, new LinkOption[] { LinkOption.NOFOLLOW_LINKS })
                                    .toMillis();
                        } catch (IOException var18) {
                            continue;
                        }

                        VSCrawlerAbstractWatchService.CacheEntry var6 = (VSCrawlerAbstractWatchService.CacheEntry) this.entries
                                .get(var3.getFileName());
                        if (var6 == null) {
                            this.entries.put(var3.getFileName(),
                                    new VSCrawlerAbstractWatchService.CacheEntry(var4, this.tickCount));
                            if (this.events.contains(StandardWatchEventKinds.ENTRY_CREATE)) {
                                this.signalEvent(StandardWatchEventKinds.ENTRY_CREATE, var3.getFileName());
                            } else if (this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                this.signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, var3.getFileName());
                            }
                        } else {
                            if (var6.lastModified != var4
                                    && this.events.contains(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                this.signalEvent(StandardWatchEventKinds.ENTRY_MODIFY, var3.getFileName());
                            }

                            var6.update(var4, this.tickCount);
                        }
                    }
                } catch (DirectoryIteratorException var19) {
                    ;
                } finally {
                    try {
                        var1.close();
                    } catch (IOException var16) {
                        ;
                    }

                }

                var2 = this.entries.entrySet().iterator();

                while (var2.hasNext()) {
                    Map.Entry var21 = (Map.Entry) var2.next();
                    VSCrawlerAbstractWatchService.CacheEntry var22 = (VSCrawlerAbstractWatchService.CacheEntry) var21
                            .getValue();
                    if (var22.lastTickCount() != this.tickCount) {
                        Path var5 = (Path) var21.getKey();
                        var2.remove();
                        if (this.events.contains(StandardWatchEventKinds.ENTRY_DELETE)) {
                            this.signalEvent(StandardWatchEventKinds.ENTRY_DELETE, var5);
                        }
                    }
                }

            }
        }
    }

    private static class CacheEntry {
        private long lastModified;
        private int lastTickCount;

        CacheEntry(long var1, int var3) {
            this.lastModified = var1;
            this.lastTickCount = var3;
        }

        int lastTickCount() {
            return this.lastTickCount;
        }

        long lastModified() {
            return this.lastModified;
        }

        void update(long var1, int var3) {
            this.lastModified = var1;
            this.lastTickCount = var3;
        }
    }*/
}
