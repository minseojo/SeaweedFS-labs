package reader;

import seaweedfs.client.FilerClient;
import seaweedfs.client.SeaweedInputStream;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadFilerGrpc {
    private static final int BUF = 1 << 20; // 1MB

    public void run() throws Exception {
        // gRPC 포트로 접속 (weed 실행 시 -filer.grpcPort=18888 로 켜져 있어야 함)
        FilerClient filerClient = new FilerClient("localhost", 18888);
        try {
            String filerPath = "/buckets/upload-test/songpa.zip"; // /buckets 접두사 필수
            try (InputStream in = new BufferedInputStream(new SeaweedInputStream(filerClient, filerPath), BUF)) {
                readZipStream(in);
            }
        } finally {
            try {
                filerClient.shutdown();
            } catch (Throwable ignore) {

            }
        }
//        System.out.println("Filer gRPC read time: " + (System.currentTimeMillis() - t0) + " ms");
    }

    private static void readZipStream(InputStream is) throws Exception {
        try (ZipInputStream zin = new ZipInputStream(is)) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
//                System.out.println(ze.getName());
            }
        }
    }
}
