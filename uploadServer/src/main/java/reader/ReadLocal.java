package reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ReadLocal {
    private static final int BUF = 1 << 20; // 1MB

    public void run() throws Exception {
        String localPath = "C:/songpa.zip"; // 변경 가능

        long t0 = System.currentTimeMillis();
        try (InputStream in = new BufferedInputStream(new FileInputStream(localPath), BUF)) {
            readZipStream(in);
        }
//        System.out.println("Local read time: " + (System.currentTimeMillis() - t0) + " ms");
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
