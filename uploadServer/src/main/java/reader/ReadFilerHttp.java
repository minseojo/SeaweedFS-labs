package reader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadFilerHttp {
    private static final int BUF = 1 << 20; // 1MB

    public void run() throws Exception {
        // HTTP 포트로 접속 (weed 실행 시 -filer.port=8888 로 켜져 있어야 함)
        String filerPath = "/buckets/upload-test/songpa.zip";
        String httpUrl = "http://localhost:8888" + filerPath;

        HttpURLConnection conn = (HttpURLConnection) new URL(httpUrl).openConnection();
        try {
            conn.setRequestMethod("GET");
            try (InputStream in = new BufferedInputStream(conn.getInputStream(), BUF)) {
                readZipStream(in);
            }
        } finally {
            conn.disconnect();
        }
//        System.out.println("Filer HTTP read time: " + (System.currentTimeMillis() - t0) + " ms");
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
