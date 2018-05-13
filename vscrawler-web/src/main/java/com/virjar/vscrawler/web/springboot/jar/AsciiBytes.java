package com.virjar.vscrawler.web.springboot.jar;

import java.nio.charset.Charset;

final class AsciiBytes {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final byte[] bytes;
    private final int offset;
    private final int length;
    private String string;
    private int hash;

    AsciiBytes(String string) {
        this(string.getBytes(UTF_8));
        this.string = string;
    }

    private AsciiBytes(byte[] bytes) {
        this(bytes, 0, bytes.length);
    }

    AsciiBytes(byte[] bytes, int offset, int length) {
        if (offset >= 0 && length >= 0 && offset + length <= bytes.length) {
            this.bytes = bytes;
            this.offset = offset;
            this.length = length;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int length() {
        return this.length;
    }

    boolean startsWith(AsciiBytes prefix) {
        if (this == prefix) {
            return true;
        } else if (prefix.length > this.length) {
            return false;
        } else {
            for (int i = 0; i < prefix.length; ++i) {
                if (this.bytes[i + this.offset] != prefix.bytes[i + prefix.offset]) {
                    return false;
                }
            }

            return true;
        }
    }

    boolean endsWith(AsciiBytes postfix) {
        if (this == postfix) {
            return true;
        } else if (postfix.length > this.length) {
            return false;
        } else {
            for (int i = 0; i < postfix.length; ++i) {
                if (this.bytes[this.offset + (this.length - 1) - i] != postfix.bytes[postfix.offset + (postfix.length - 1) - i]) {
                    return false;
                }
            }

            return true;
        }
    }

    public AsciiBytes substring(int beginIndex) {
        return this.substring(beginIndex, this.length);
    }

    public AsciiBytes substring(int beginIndex, int endIndex) {
        int length = endIndex - beginIndex;
        if (this.offset + length > this.bytes.length) {
            throw new IndexOutOfBoundsException();
        } else {
            return new AsciiBytes(this.bytes, this.offset + beginIndex, length);
        }
    }

    public AsciiBytes append(String string) {
        return string != null && !string.isEmpty() ? this.append(string.getBytes(UTF_8)) : this;
    }

    public AsciiBytes append(AsciiBytes asciiBytes) {
        return asciiBytes != null && asciiBytes.length() != 0 ? this.append(asciiBytes.bytes) : this;
    }

    private AsciiBytes append(byte[] bytes) {
        if (bytes != null && bytes.length != 0) {
            byte[] combined = new byte[this.length + bytes.length];
            System.arraycopy(this.bytes, this.offset, combined, 0, this.length);
            System.arraycopy(bytes, 0, combined, this.length, bytes.length);
            return new AsciiBytes(combined);
        } else {
            return this;
        }
    }

    public String toString() {
        if (this.string == null) {
            this.string = new String(this.bytes, this.offset, this.length, UTF_8);
        }

        return this.string;
    }

    public int hashCode() {
        int hash = this.hash;
        if (hash == 0 && this.bytes.length > 0) {
            for (int i = this.offset; i < this.offset + this.length; ++i) {
                int b = this.bytes[i];
                if (b < 0) {
                    b &= 127;
                    int excess = 128;
                    byte limit;
                    if (b < 96) {
                        limit = 1;
                        excess = excess + 4096;
                    } else if (b < 112) {
                        limit = 2;
                        excess = excess + 401408;
                    } else {
                        limit = 3;
                        excess = excess + 29892608;
                    }

                    for (int j = 0; j < limit; ++j) {
                        int var10000 = b << 6;
                        ++i;
                        b = var10000 + (this.bytes[i] & 255);
                    }

                    b -= excess;
                }

                if (b <= 65535) {
                    hash = 31 * hash + b;
                } else {
                    hash = 31 * hash + (b >> 10) + 'ퟀ';
                    hash = 31 * hash + (b & 1023) + '\udc00';
                }
            }

            this.hash = hash;
        }

        return hash;
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this == obj) {
            return true;
        } else {
            if (obj.getClass().equals(AsciiBytes.class)) {
                AsciiBytes other = (AsciiBytes) obj;
                if (this.length == other.length) {
                    for (int i = 0; i < this.length; ++i) {
                        if (this.bytes[this.offset + i] != other.bytes[other.offset + i]) {
                            return false;
                        }
                    }

                    return true;
                }
            }

            return false;
        }
    }

    static String toString(byte[] bytes) {
        return new String(bytes, UTF_8);
    }

    static int hashCode(String string) {
        return string.hashCode();
    }

    static int hashCode(int hash, String string) {
        for (int i = 0; i < string.length(); ++i) {
            hash = 31 * hash + string.charAt(i);
        }

        return hash;
    }
}
