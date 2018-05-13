package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

final class CentralDirectoryFileHeader implements FileHeader {
    private static final AsciiBytes SLASH = new AsciiBytes("/");
    private static final byte[] NO_EXTRA = new byte[0];
    private static final AsciiBytes NO_COMMENT = new AsciiBytes("");
    private byte[] header;
    private int headerOffset;
    private AsciiBytes name;
    private byte[] extra;
    private AsciiBytes comment;
    private long localHeaderOffset;

    CentralDirectoryFileHeader() {
    }

    private CentralDirectoryFileHeader(byte[] header, int headerOffset, AsciiBytes name, byte[] extra, AsciiBytes comment, long localHeaderOffset) {
        this.header = header;
        this.headerOffset = headerOffset;
        this.name = name;
        this.extra = extra;
        this.comment = comment;
        this.localHeaderOffset = localHeaderOffset;
    }

    void load(byte[] data, int dataOffset, RandomAccessData variableData, int variableOffset, JarEntryFilter filter) throws IOException {
        this.header = data;
        this.headerOffset = dataOffset;
        long nameLength = Bytes.littleEndianValue(data, dataOffset + 28, 2);
        long extraLength = Bytes.littleEndianValue(data, dataOffset + 30, 2);
        long commentLength = Bytes.littleEndianValue(data, dataOffset + 32, 2);
        this.localHeaderOffset = Bytes.littleEndianValue(data, dataOffset + 42, 4);
        dataOffset += 46;
        if (variableData != null) {
            data = Bytes.get(variableData.getSubsection((long) (variableOffset + 46), nameLength + extraLength + commentLength));
            dataOffset = 0;
        }

        this.name = new AsciiBytes(data, dataOffset, (int) nameLength);
        if (filter != null) {
            this.name = filter.apply(this.name);
        }

        this.extra = NO_EXTRA;
        this.comment = NO_COMMENT;
        if (extraLength > 0L) {
            this.extra = new byte[(int) extraLength];
            System.arraycopy(data, (int) ((long) dataOffset + nameLength), this.extra, 0, this.extra.length);
        }

        if (commentLength > 0L) {
            this.comment = new AsciiBytes(data, (int) ((long) dataOffset + nameLength + extraLength), (int) commentLength);
        }

    }

    public AsciiBytes getName() {
        return this.name;
    }

    public boolean hasName(String name, String suffix) {
        return this.name.equals(new AsciiBytes(suffix == null ? name : name + suffix));
    }

    public boolean isDirectory() {
        return this.name.endsWith(SLASH);
    }

    public int getMethod() {
        return (int) Bytes.littleEndianValue(this.header, this.headerOffset + 10, 2);
    }

    long getTime() {
        long date = Bytes.littleEndianValue(this.header, this.headerOffset + 14, 2);
        long time = Bytes.littleEndianValue(this.header, this.headerOffset + 12, 2);
        return this.decodeMsDosFormatDateTime(date, time).getTimeInMillis();
    }

    private Calendar decodeMsDosFormatDateTime(long date, long time) {
        int year = (int) (date >> 9 & 127L) + 1980;
        int month = (int) (date >> 5 & 15L) - 1;
        int day = (int) (date & 31L);
        int hours = (int) (time >> 11 & 31L);
        int minutes = (int) (time >> 5 & 63L);
        int seconds = (int) (time << 1 & 62L);
        return new GregorianCalendar(year, month, day, hours, minutes, seconds);
    }

    long getCrc() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 16, 4);
    }

    public long getCompressedSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 20, 4);
    }

    public long getSize() {
        return Bytes.littleEndianValue(this.header, this.headerOffset + 24, 4);
    }

    byte[] getExtra() {
        return this.extra;
    }

    AsciiBytes getComment() {
        return this.comment;
    }

    public long getLocalHeaderOffset() {
        return this.localHeaderOffset;
    }

    public CentralDirectoryFileHeader clone() {
        byte[] header = new byte[46];
        System.arraycopy(this.header, this.headerOffset, header, 0, header.length);
        return new CentralDirectoryFileHeader(header, 0, this.name, header, this.comment, this.localHeaderOffset);
    }

    static CentralDirectoryFileHeader fromRandomAccessData(RandomAccessData data, int offset, JarEntryFilter filter) throws IOException {
        CentralDirectoryFileHeader fileHeader = new CentralDirectoryFileHeader();
        byte[] bytes = Bytes.get(data.getSubsection((long) offset, 46L));
        fileHeader.load(bytes, 0, data, offset, filter);
        return fileHeader;
    }
}
