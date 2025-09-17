package writer;

import seaweedfs.client.FilerClient;
import seaweedfs.client.SeaweedOutputStream;

import java.io.*;
import java.nio.file.*;

public class WriteFilerGrpc {
    private static final int BUF = 1 << 20; // 1MB
    private final Path src = Paths.get("C:/songpa.zip");
    // gRPC 기준 경로 (Filer 가상경로! 윈도우 경로(X) 아님)
    private final String filerPath = "/buckets/upload-test/songpa2.zip";

    public void run() throws Exception {
        byte[] buf = new byte[BUF];
        long size = Files.size(src);

        // gRPC 포트로 연결 (weed: -filer.grpcPort=18888)
        FilerClient client = new FilerClient("localhost", 18888);
        try {
            // 부모 디렉터리 보장
            String parent = Path.of(filerPath).getParent().toString();
//            client.mkdirs(parent, 0755);

            long t0 = System.currentTimeMillis();
            try (InputStream in  = new BufferedInputStream(Files.newInputStream(src), BUF);
                 OutputStream out = new SeaweedOutputStream(client, filerPath)) {
                for (int n; (n = in.read(buf)) > 0; ) out.write(buf, 0, n);
                out.flush();
            }
            long ms = System.currentTimeMillis() - t0;
            System.out.printf("Filer gRPC write: %d ms (%.1f MiB/s)%n",
                    ms, (size/1024.0/1024.0) / (ms/1000.0));
        } finally {
            // 리소스 누수 경고 방지
            try { client.shutdown(); } catch (Throwable ignore) {}
        }
    }
}
