package reader.manifest;

import java.time.Instant;
import java.util.List;

public record Manifest(
        String txnId,
        String archiveSha256,
        long count,
        List<ManifestFile> files,
        Instant createdAt
) {}

