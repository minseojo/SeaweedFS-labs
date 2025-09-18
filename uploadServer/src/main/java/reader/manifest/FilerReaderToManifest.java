package reader.manifest;

import seaweedfs.client.FilerClient;
import seaweedfs.client.SeaweedInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilerReaderToManifest {

    public static void main(String[] args) {
        try {
            String txnId = "2025-09-18-abc123";
            String bucket = "upload-test";
            String objectKey = "songpa.zip";
            String filerPath = "/buckets/" + bucket + "/" + objectKey;

            // gRPC로 읽어 매니페스트 생성
            FilerClient filerClient = new FilerClient("localhost", 18888);
            Manifest manifest = readZipFromSeaweedGrpc(filerClient, filerPath, txnId);

            // JSON 파일로 저장 (로컬)
            Path out = Path.of("manifests", txnId + ".json");
            ManifestJson.writeToFile(manifest, out);
            System.out.println("Saved: " + out.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // gRPC
    public static Manifest readZipFromSeaweedGrpc(FilerClient filerClient, String filerPath, String txnId) throws Exception {
        try (SeaweedInputStream sis = new SeaweedInputStream(filerClient, filerPath)) {
            return readZipStream(sis, txnId);
        }
    }

    // HTTP (선택)
    public static Manifest readZipFromSeaweedHttp(String httpUrl, String txnId) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
        conn.setRequestMethod("GET");
        try (InputStream in = conn.getInputStream()) {
            return readZipStream(in, txnId);
        } finally {
            conn.disconnect();
        }
    }

    // 공통: ZIP 원본 SHA-256(아카이브) + 엔트리 SHA-1
    private static Manifest readZipStream(InputStream is, String txnId) throws Exception {
        // 1) 입력 스트림을 임시 ZIP 파일로 저장하면서 동시에 SHA-256 계산 (ZIP 원본 바이트 기준)
        TempWithSha256 t = saveToTempAndSha256(is);
        String archiveSha256 = t.sha256;

        List<ManifestFile> files = new ArrayList<>();

        // 2) 임시 ZIP 파일을 열어 엔트리별 SHA-1/size 계산 (압축 해제 데이터 기준)
        try (ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(t.file)))) {
            ZipEntry ze;
            byte[] buf = new byte[8192];

            while ((ze = zin.getNextEntry()) != null) {
                if (ze.isDirectory()) continue;

                MessageDigest fileSha1 = MessageDigest.getInstance("SHA-1");
                long size = 0;
                int n;
                while ((n = zin.read(buf)) != -1) {
                    fileSha1.update(buf, 0, n);
                    size += n;
                }
                String sha1 = toHex(fileSha1.digest());
                files.add(new ManifestFile(ze.getName(), size, sha1));
            }
        } finally {
            // 임시 파일 정리
            if (t.file != null && t.file.exists()) t.file.delete();
        }

        // 3) Manifest 생성 (주의: Manifest DTO 필드명이 archiveSha256 이어야 함)
        return new Manifest(txnId, archiveSha256, files.size(), files, Instant.now());
    }

    // 입력 스트림을 임시 파일에 저장하면서 SHA-256 동시 계산
    private static TempWithSha256 saveToTempAndSha256(InputStream in) throws Exception {
        File tmp = File.createTempFile("manifest-", ".zip");
        tmp.deleteOnExit();

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buf = new byte[8192];
        int n;

        try (in; OutputStream out = new BufferedOutputStream(new FileOutputStream(tmp))) {
            while ((n = in.read(buf)) != -1) {
                md.update(buf, 0, n); // ZIP 원본 바이트 누적
                out.write(buf, 0, n); // 파일로 저장
            }
        }
        String sha256 = toHex(md.digest());
        return new TempWithSha256(tmp, sha256);
    }

    private record TempWithSha256(File file, String sha256) {}

    private static String toHex(byte[] bytes) {
        try (Formatter f = new Formatter()) {
            for (byte b : bytes) f.format("%02x", b);
            return f.toString();
        }
    }
}
