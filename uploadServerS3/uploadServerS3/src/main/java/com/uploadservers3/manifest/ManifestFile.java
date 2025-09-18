package com.uploadservers3.manifest;

public record ManifestFile(
        String path,   // gzip 내부 경로 (e.g. models/001.glb)
        long   size,
        String sha1     // 각 엔트리 SHA-1
) {}