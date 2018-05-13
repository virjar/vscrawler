package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

class CentralDirectoryParser {
    private static final int CENTRAL_DIRECTORY_HEADER_BASE_SIZE = 46;
    private final List<CentralDirectoryVisitor> visitors = new ArrayList<>();

    CentralDirectoryVisitor addVisitor(CentralDirectoryVisitor visitor) {
        this.visitors.add(visitor);
        return visitor;
    }

    RandomAccessData parse(RandomAccessData data, boolean skipPrefixBytes) throws IOException {
        CentralDirectoryEndRecord endRecord = new CentralDirectoryEndRecord(data);
        if (skipPrefixBytes) {
            data = this.getArchiveData(endRecord, data);
        }

        RandomAccessData centralDirectoryData = endRecord.getCentralDirectory(data);
        this.visitStart(endRecord, centralDirectoryData);
        this.parseEntries(endRecord, centralDirectoryData);
        this.visitEnd();
        return data;
    }

    private void parseEntries(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) throws IOException {
        byte[] bytes = Bytes.get(centralDirectoryData);
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        int dataOffset = 0;

        for (int i = 0; i < endRecord.getNumberOfRecords(); ++i) {
            fileHeader.load(bytes, dataOffset, null, 0, null);
            this.visitFileHeader(dataOffset, fileHeader);
            dataOffset += CENTRAL_DIRECTORY_HEADER_BASE_SIZE + fileHeader.getName().length() + fileHeader.getComment().length() + fileHeader.getExtra().length;
        }

    }

    private RandomAccessData getArchiveData(CentralDirectoryEndRecord endRecord, RandomAccessData data) {
        long offset = endRecord.getStartOfArchive(data);
        return offset == 0L ? data : data.getSubsection(offset, data.getSize() - offset);
    }

    private void visitStart(CentralDirectoryEndRecord endRecord, RandomAccessData centralDirectoryData) {
        for (CentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitStart(endRecord, centralDirectoryData);
        }

    }

    private void visitFileHeader(int dataOffset, CentralDirectoryFileHeader fileHeader) {
        for (CentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitFileHeader(fileHeader, dataOffset);
        }

    }

    private void visitEnd() {
        for (CentralDirectoryVisitor visitor : this.visitors) {
            visitor.visitEnd();
        }

    }
}
