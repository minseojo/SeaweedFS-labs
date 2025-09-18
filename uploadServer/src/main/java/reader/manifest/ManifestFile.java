package reader.manifest;

public record ManifestFile(
        String path,
        long size,
        String sha1
) {}