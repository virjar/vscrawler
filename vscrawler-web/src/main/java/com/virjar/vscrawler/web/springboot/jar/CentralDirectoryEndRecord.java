package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

class CentralDirectoryEndRecord {
   private static final int MINIMUM_SIZE = 22;
   private static final int MAXIMUM_COMMENT_LENGTH = 65535;
   private static final int MAXIMUM_SIZE = 65557;
   private static final int SIGNATURE = 101010256;
   private static final int COMMENT_LENGTH_OFFSET = 20;
   private static final int READ_BLOCK_SIZE = 256;
   private byte[] block;
   private int offset;
   private int size;

   CentralDirectoryEndRecord(RandomAccessData data) throws IOException {
      this.block = this.createBlockFromEndOfData(data, 256);
      this.size = 22;

      for(this.offset = this.block.length - this.size; !this.isValid(); this.offset = this.block.length - this.size) {
         ++this.size;
         if (this.size > this.block.length) {
            if (this.size >= 65557 || (long)this.size > data.getSize()) {
               throw new IOException("Unable to find ZIP central directory records after reading " + this.size + " bytes");
            }

            this.block = this.createBlockFromEndOfData(data, this.size + 256);
         }
      }

   }

   private byte[] createBlockFromEndOfData(RandomAccessData data, int size) throws IOException {
      int length = (int)Math.min(data.getSize(), (long)size);
      return Bytes.get(data.getSubsection(data.getSize() - (long)length, (long)length));
   }

   private boolean isValid() {
      if (this.block.length >= 22 && Bytes.littleEndianValue(this.block, this.offset + 0, 4) == 101010256L) {
         long commentLength = Bytes.littleEndianValue(this.block, this.offset + 20, 2);
         return (long)this.size == 22L + commentLength;
      } else {
         return false;
      }
   }

   public long getStartOfArchive(RandomAccessData data) {
      long length = Bytes.littleEndianValue(this.block, this.offset + 12, 4);
      long specifiedOffset = Bytes.littleEndianValue(this.block, this.offset + 16, 4);
      long actualOffset = data.getSize() - (long)this.size - length;
      return actualOffset - specifiedOffset;
   }

   public RandomAccessData getCentralDirectory(RandomAccessData data) {
      long offset = Bytes.littleEndianValue(this.block, this.offset + 16, 4);
      long length = Bytes.littleEndianValue(this.block, this.offset + 12, 4);
      return data.getSubsection(offset, length);
   }

   public int getNumberOfRecords() {
      long numberOfRecords = Bytes.littleEndianValue(this.block, this.offset + 10, 2);
      if (numberOfRecords == 65535L) {
         throw new IllegalStateException("Zip64 archives are not supported");
      } else {
         return (int)numberOfRecords;
      }
   }
}
