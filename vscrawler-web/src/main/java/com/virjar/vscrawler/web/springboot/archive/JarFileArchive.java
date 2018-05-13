package com.virjar.vscrawler.web.springboot.archive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import com.virjar.vscrawler.web.springboot.data.RandomAccessData;
import com.virjar.vscrawler.web.springboot.jar.JarFile;

public class JarFileArchive implements Archive {
   private static final String UNPACK_MARKER = "UNPACK:";
   private static final int BUFFER_SIZE = 32768;
   private final JarFile jarFile;
   private URL url;
   private File tempUnpackFolder;

   public JarFileArchive(File file) throws IOException {
      this(file, (URL)null);
   }

   public JarFileArchive(File file, URL url) throws IOException {
      this(new JarFile(file));
      this.url = url;
   }

   public JarFileArchive(JarFile jarFile) {
      this.jarFile = jarFile;
   }

   public URL getUrl() throws MalformedURLException {
      return this.url != null ? this.url : this.jarFile.getUrl();
   }

   public Manifest getManifest() throws IOException {
      return this.jarFile.getManifest();
   }

   public List getNestedArchives(Archive.EntryFilter filter) throws IOException {
      List nestedArchives = new ArrayList();
      Iterator var3 = this.iterator();

      while(var3.hasNext()) {
         Archive.Entry entry = (Archive.Entry)var3.next();
         if (filter.matches(entry)) {
            nestedArchives.add(this.getNestedArchive(entry));
         }
      }

      return Collections.unmodifiableList(nestedArchives);
   }

   public Iterator iterator() {
      return new JarFileArchive.EntryIterator(this.jarFile.entries());
   }

   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
      JarEntry jarEntry = ((JarFileArchive.JarFileEntry)entry).getJarEntry();
      if (jarEntry.getComment().startsWith("UNPACK:")) {
         return this.getUnpackedNestedArchive(jarEntry);
      } else {
         try {
            JarFile jarFile = this.jarFile.getNestedJarFile((ZipEntry)jarEntry);
            return new JarFileArchive(jarFile);
         } catch (Exception var4) {
            throw new IllegalStateException("Failed to get nested archive for entry " + entry.getName(), var4);
         }
      }
   }

   private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
      String name = jarEntry.getName();
      if (name.lastIndexOf("/") != -1) {
         name = name.substring(name.lastIndexOf("/") + 1);
      }

      File file = new File(this.getTempUnpackFolder(), name);
      if (!file.exists() || file.length() != jarEntry.getSize()) {
         this.unpack(jarEntry, file);
      }

      return new JarFileArchive(file, file.toURI().toURL());
   }

   private File getTempUnpackFolder() {
      if (this.tempUnpackFolder == null) {
         File tempFolder = new File(System.getProperty("java.io.tmpdir"));
         this.tempUnpackFolder = this.createUnpackFolder(tempFolder);
      }

      return this.tempUnpackFolder;
   }

   private File createUnpackFolder(File parent) {
      int var2 = 0;

      File unpackFolder;
      do {
         if (var2++ >= 1000) {
            throw new IllegalStateException("Failed to create unpack folder in directory '" + parent + "'");
         }

         String fileName = (new File(this.jarFile.getName())).getName();
         unpackFolder = new File(parent, fileName + "-spring-boot-libs-" + UUID.randomUUID());
      } while(!unpackFolder.mkdirs());

      return unpackFolder;
   }

   private void unpack(JarEntry entry, File file) throws IOException {
      InputStream inputStream = this.jarFile.getInputStream((ZipEntry)entry, RandomAccessData.ResourceAccess.ONCE);

      try {
         FileOutputStream outputStream = new FileOutputStream(file);

         try {
            byte[] buffer = new byte['耀'];
            boolean var6 = true;

            int bytesRead;
            while((bytesRead = inputStream.read(buffer)) != -1) {
               outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
         } finally {
            outputStream.close();
         }
      } finally {
         inputStream.close();
      }
   }

   public String toString() {
      try {
         return this.getUrl().toString();
      } catch (Exception var2) {
         return "jar archive";
      }
   }

   private static class JarFileEntry implements Archive.Entry {
      private final JarEntry jarEntry;

      JarFileEntry(JarEntry jarEntry) {
         this.jarEntry = jarEntry;
      }

      public JarEntry getJarEntry() {
         return this.jarEntry;
      }

      public boolean isDirectory() {
         return this.jarEntry.isDirectory();
      }

      public String getName() {
         return this.jarEntry.getName();
      }
   }

   private static class EntryIterator implements Iterator {
      private final Enumeration enumeration;

      EntryIterator(Enumeration enumeration) {
         this.enumeration = enumeration;
      }

      public boolean hasNext() {
         return this.enumeration.hasMoreElements();
      }

      public Archive.Entry next() {
         return new JarFileArchive.JarFileEntry((JarEntry)this.enumeration.nextElement());
      }

      public void remove() {
         throw new UnsupportedOperationException("remove");
      }
   }
}
