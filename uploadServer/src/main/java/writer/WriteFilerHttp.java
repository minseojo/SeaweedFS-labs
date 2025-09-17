package writer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;

public class WriteFilerHttp {
    private static final int BUF = 1 << 20; // 1MB
    private final Path src = Paths.get("C:/songpa.zip");
    // 버킷에 쓸 경로 (HTTP 기준)
    private final String filerPath = "/buckets/upload-test/songpa2.zip";
    private final String httpUrl   = "http://localhost:8888" + filerPath;

    public void run() throws Exception {
        long size = Files.size(src);
        byte[] buf = new byte[BUF];

        long t0 = System.currentTimeMillis();
        HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/zip");
        // 큰 파일은 청크 모드 권장(고정 길이 모드 int 한계 회피)
        conn.setChunkedStreamingMode(BUF);

        try (OutputStream out = new BufferedOutputStream(conn.getOutputStream(), BUF);
             InputStream in = new BufferedInputStream(Files.newInputStream(src), BUF)) {
            for (int n; (n = in.read(buf)) > 0; ) out.write(buf, 0, n);
            out.flush();
        }
        int code = conn.getResponseCode();
        conn.disconnect();

        long ms = System.currentTimeMillis() - t0;
        System.out.printf("Filer HTTP write: %d ms (%.1f MiB/s), HTTP %d%n",
                ms, (size/1024.0/1024.0) / (ms/1000.0), code);
    }
}
