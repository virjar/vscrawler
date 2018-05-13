package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

class JarEntry extends java.util.jar.JarEntry implements FileHeader {
   private Certificate[] certificates;
   private CodeSigner[] codeSigners;
   private final JarFile jarFile;
   private long localHeaderOffset;

   JarEntry(JarFile jarFile, CentralDirectoryFileHeader header) {
      super(header.getName().toString());
      this.jarFile = jarFile;
      this.localHeaderOffset = header.getLocalHeaderOffset();
      this.setCompressedSize(header.getCompressedSize());
      this.setMethod(header.getMethod());
      this.setCrc(header.getCrc());
      this.setSize(header.getSize());
      this.setExtra(header.getExtra());
      this.setComment(header.getComment().toString());
      this.setSize(header.getSize());
      this.setTime(header.getTime());
   }

   public boolean hasName(String name, String suffix) {
      return this.getName().length() == name.length() + suffix.length() && this.getName().startsWith(name) && this.getName().endsWith(suffix);
   }

   URL getUrl() throws MalformedURLException {
      return new URL(this.jarFile.getUrl(), this.getName());
   }

   public Attributes getAttributes() throws IOException {
      Manifest manifest = this.jarFile.getManifest();
      return manifest == null ? null : manifest.getAttributes(this.getName());
   }

   public Certificate[] getCertificates() {
      if (this.jarFile.isSigned() && this.certificates == null) {
         this.jarFile.setupEntryCertificates(this);
      }

      return this.certificates;
   }

   public CodeSigner[] getCodeSigners() {
      if (this.jarFile.isSigned() && this.codeSigners == null) {
         this.jarFile.setupEntryCertificates(this);
      }

      return this.codeSigners;
   }

   void setCertificates(java.util.jar.JarEntry entry) {
      this.certificates = entry.getCertificates();
      this.codeSigners = entry.getCodeSigners();
   }

   public long getLocalHeaderOffset() {
      return this.localHeaderOffset;
   }
}
