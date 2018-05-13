package com.virjar.vscrawler.web.springboot.jar;

import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

interface CentralDirectoryVisitor {
   void visitStart(CentralDirectoryEndRecord var1, RandomAccessData var2);

   void visitFileHeader(CentralDirectoryFileHeader var1, int var2);

   void visitEnd();
}
