package com.uploadservers3.util;

import java.io.*;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class HashUtils {
    private HashUtils() {}

    public static String sha256OfFile(File f) throws Exception {
        return digestOfFile(f, "SHA-256");
    }

    public static String sha1OfStream(InputStream in) throws Exception {
        return digestOfStream(in, "SHA-1");
    }

    public static String digestOfFile(File f, String algo) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algo);
        try (InputStream is = new BufferedInputStream(new FileInputStream(f))) {
            byte[] buf = new byte[8192]; int n;
            while ((n = is.read(buf)) != -1) md.update(buf, 0, n);
        }
        return HexFormat.of().formatHex(md.digest());
    }

    public static String digestOfStream(InputStream in, String algo) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algo);
        byte[] buf = new byte[8192]; int n;
        while ((n = in.read(buf)) != -1) md.update(buf, 0, n);
        return HexFormat.of().formatHex(md.digest());
    }
}
