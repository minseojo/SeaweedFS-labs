package com.uploadservers3.manifest;

import java.util.List;

public record Manifest(
        String txnId,
        String archiveSha256,       // ZIP 전체 SHA-256
        int    count,
        List<ManifestFile> files,    // path, size, sha256
        String createdAt
) {}

