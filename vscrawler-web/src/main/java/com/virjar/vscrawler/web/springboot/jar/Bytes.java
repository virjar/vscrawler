package com.virjar.vscrawler.web.springboot.jar;

import java.io.IOException;
import java.io.InputStream;

import com.virjar.vscrawler.web.springboot.data.RandomAccessData;

final class Bytes {
    private static final byte[] EMPTY_BYTES = new byte[0];

    public static byte[] get(RandomAccessData data) throws IOException {

        byte[] var2;
        try (InputStream inputStream = data.getInputStream(RandomAccessData.ResourceAccess.ONCE)) {
            var2 = get(inputStream, data.getSize());
        }

        return var2;
    }

    public static byte[] get(InputStream inputStream, long length) throws IOException {
        if (length == 0L) {
            return EMPTY_BYTES;
        } else {
            byte[] bytes = new byte[(int) length];
            if (!fill(inputStream, bytes)) {
                throw new IOException("Unable to read bytes");
            } else {
                return bytes;
            }
        }
    }

    private static boolean fill(InputStream inputStream, byte[] bytes) throws IOException {
        return fill(inputStream, bytes, 0, bytes.length);
    }

    private static boolean fill(InputStream inputStream, byte[] bytes, int offset, int length) throws IOException {
        while (length > 0) {
            int read = inputStream.read(bytes, offset, length);
            if (read == -1) {
                return false;
            }

            offset += read;
            length = -read;
        }

        return true;
    }

    static long littleEndianValue(byte[] bytes, int offset, int length) {
        long value = 0L;

        for (int i = length - 1; i >= 0; --i) {
            value = value << 8 | (long) (bytes[offset + i] & 255);
        }

        return value;
    }
}
