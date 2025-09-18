package com.uploadservers3.storage;

import com.uploadservers3.manifest.Manifest;
import com.uploadservers3.manifest.ManifestFile;
import com.uploadservers3.manifest.ManifestRepository;
import com.uploadservers3.util.HashUtils;
import com.uploadservers3.util.PathUtils;
import com.uploadservers3.util.ZipGuards;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Service
public class UploadService {

    private final ManifestRepository repo;
    private final StorageClient storage;
    private final String bucket;

    public UploadService(ManifestRepository repo,
                         StorageClient storage,
                         @Value("${storage.bucket:upload-test}") String bucket) {
        this.repo = repo;
        this.storage = storage;
        this.bucket = bucket;
    }

    public Map<String, Object> init(Manifest m) {
        if (m == null || m.txnId() == null || m.txnId().isBlank())
            throw new IllegalArgumentException("txnId required");
        if (m.archiveSha256() == null || m.archiveSha256().isBlank())
            throw new IllegalArgumentException("archiveSha256 required");
        repo.save(m);
        return Map.of("ok", true, "txnId", m.txnId());
    }

    public Map<String, Object> stream(String txnId, InputStream body) throws Exception {
        Manifest manifest = Optional.ofNullable(repo.findByTxnId(txnId))
                .orElseThrow(() -> new IllegalArgumentException("unknown txnId: " + txnId));

        // 1) 스트림을 임시파일에 저장하면서(1회 I/O) — 여기서는 단순 저장 후 파일로 SHA 계산(명확성↑)
        File tmp = saveToTemp(body);

        // 2) 아카이브 SHA-256 (ZIP 원본 바이트 그대로)
        String actualArchiveSha256 = HashUtils.sha256OfFile(tmp);
        if (!actualArchiveSha256.equalsIgnoreCase(manifest.archiveSha256())) {
            tmp.delete();
            throw new IllegalStateException(
                    "archive sha256 mismatch: expected=" + manifest.archiveSha256() + ", actual=" + actualArchiveSha256);
        }

        // 3) ZipFile(중앙 디렉터리) 기반 검증
        Map<String, ManifestFile> expect = new HashMap<>();
        for (ManifestFile f : manifest.files()) expect.put(PathUtils.normalizeZipPath(f.path()), f);

        Set<String> seen = new HashSet<>();
        AtomicLong totalUncomp = new AtomicLong(0);
        AtomicLong totalComp = new AtomicLong(0);
        long entryCount = 0;

        try (ZipFile zf = new ZipFile(tmp)) {
            var entries = zf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry ze = entries.nextElement();
                if (ze.isDirectory()) continue;

                entryCount++;
                ZipGuards.checkEntryCount(entryCount);

                String path = PathUtils.normalizeZipPath(ze.getName());
                ManifestFile mf = expect.get(path);
                if (mf == null) throw new IllegalStateException("unexpected entry: " + path);

                long declaredSize = ze.getSize();           // -1일 수 있음
                long declaredComp = ze.getCompressedSize(); // -1일 수 있음
                if (declaredSize >= 0) totalUncomp.addAndGet(declaredSize);
                if (declaredComp >= 0) totalComp.addAndGet(declaredComp);
                ZipGuards.checkTotals(totalUncomp, totalComp);

                // 빠른 1차 필터: CRC32 (실제 바이트 기준 계산)
                CRC32 crc = new CRC32();
                long actualSize = 0;
                try (InputStream in = zf.getInputStream(ze)) {
                    byte[] buf = new byte[8192]; int n;
                    while ((n = in.read(buf)) != -1) {
                        crc.update(buf, 0, n);
                        actualSize += n;
                    }
                }
                long actualCrc = crc.getValue();
                long expectedCrc = ze.getCrc(); // 중앙 디렉터리 내 CRC
                if (expectedCrc != -1 && actualCrc != expectedCrc)
                    throw new IllegalStateException("crc mismatch for " + path);

                // 사이즈 검증 (manifest와 실제 해제 사이즈 비교)
                if (mf.size() != actualSize)
                    throw new IllegalStateException("size mismatch for " + path + " expected=" + mf.size() + " actual=" + actualSize);

                // 중요 파일만 SHA-1 (manifest.sha1이 있을 때만 강제)
                if (mf.sha1() != null && !mf.sha1().isBlank()) {
                    try (InputStream in = zf.getInputStream(ze)) {
                        String sha1 = HashUtils.sha1OfStream(in);
                        if (!mf.sha1().equalsIgnoreCase(sha1))
                            throw new IllegalStateException("sha1 mismatch for " + path);
                    }
                }

                seen.add(path);
            }
        }

        // 개수 검증
        if (seen.size() != expect.size())
            throw new IllegalStateException("entry count mismatch: expected=" + expect.size() + " actual=" + seen.size());
        if (manifest.count() != expect.size())
            throw new IllegalStateException("manifest count mismatch: manifest.count=" + manifest.count() + " files.size=" + expect.size());

        // 4) 업로드(콘텐츠 주소화 권장: objectKey = {archiveSha256}.zip)
        String objectKey = txnId + ".zip"; // 또는 manifest.archiveSha256()+".zip"
        storage.putObject(bucket, objectKey, tmp);
        tmp.delete();

        return Map.of("ok", true, "status", "PROMOTED", "txnId", txnId, "objectKey", objectKey, "bucket", bucket);
    }

    private File saveToTemp(InputStream in) throws Exception {
        File tmp = File.createTempFile("upload-", ".zip");
        tmp.deleteOnExit();
        try (in; OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp))) {
            in.transferTo(out);
        }
        return tmp;
    }
}
