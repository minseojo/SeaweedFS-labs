package reader;

import seaweedfs.client.FilerClient;
import seaweedfs.client.SeaweedInputStream;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FilerReaderAll {

    public static void main(String[] args) {
        try {
            // gRPC 포트(문서 예시: 18888)
            FilerClient filerClient = new FilerClient("localhost", 18888);

            String bucket = "upload-test";
            String objectKey = "songpa.zip";
            String filerPath = "/buckets/" + bucket + "/" + objectKey; // gRPC 경로 (seaweedFS grpc 기본 포트 18888)
            String httpUrl   = "http://localhost:8888" + filerPath;     // HTTP 경로 (seaweedFS http 기본 포트 8888)

            // 1) Local C 드라이브 (의미없음, 그냥 테스트용)
            long t0 = System.currentTimeMillis();
            readZipFromLocal("C:/songpa.zip");
            long localCTime = System.currentTimeMillis() - t0;

            // 2) Local D 드라이브 (의미없음, 그냥 테스트용)
            long t1 = System.currentTimeMillis();
            readZipFromLocal("D:/implicit_tiling/songpa6/tile_content/songpa.zip");
            long localDTime = System.currentTimeMillis() - t1;

            // 3) SeaweedFS gRPC (FilerClient)
            long t2 = System.currentTimeMillis();
            readZipFromSeaweedGrpc(filerClient, filerPath);
            long grpcTime = System.currentTimeMillis() - t2;

            // 4) SeaweedFS HTTP (REST)
            long t3 = System.currentTimeMillis();
            readZipFromSeaweedHttp(httpUrl);
            long httpTime = System.currentTimeMillis() - t3;

            System.out.println("Local C:    " + localCTime + " ms");
            System.out.println("Local D:    " + localDTime + " ms");
            System.out.println("Filer gRPC: " + grpcTime + " ms");
            System.out.println("Filer HTTP: " + httpTime + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Readers ---
    public static void readZipFromLocal(String localZipPath) throws IOException {
        try (FileInputStream fis = new FileInputStream(localZipPath)) {
            readZipStream(fis);
        }
    }

    public static void readZipFromSeaweedGrpc(FilerClient filerClient, String filerPath) throws IOException {
        try (SeaweedInputStream sis = new SeaweedInputStream(filerClient, filerPath)) {
            readZipStream(sis);
        }
    }

    public static void readZipFromSeaweedHttp(String httpUrl) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        try (InputStream in = conn.getInputStream()) {
            readZipStream(in);
        } finally {
            conn.disconnect();
        }
    }

    // --- Common ---
    private static void readZipStream(InputStream is) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(is)) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                // 실제 로직은 (엔트리 읽기/검증 로직 추가)
                System.out.println(ze.getName());
            }
        }
    }
}
