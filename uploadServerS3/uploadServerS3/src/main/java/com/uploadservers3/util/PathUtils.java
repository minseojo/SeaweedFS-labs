package com.uploadservers3.util;

public final class PathUtils {
    private PathUtils(){}

    /** 디렉터리 traversal 및 절대 경로 차단 */
    public static String normalizeZipPath(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("empty entry name");
        if (name.startsWith("/") || name.startsWith("\\"))
            throw new IllegalArgumentException("absolute path not allowed: " + name);
        String lowered = name.replace('\\', '/');
        if (lowered.contains("../") || lowered.contains("..\\"))
            throw new IllegalArgumentException("path traversal detected: " + name);
        // 필요 시 추가 정규화
        return lowered;
    }
}
