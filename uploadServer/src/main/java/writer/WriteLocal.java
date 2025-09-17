package writer;

import java.nio.file.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class WriteLocal {
    // 필요에 맞게 경로만 바꾸세요
    private final Path src = Paths.get("C:/songpa.zip");
    private final Path dst = Paths.get("D:/tmp/songpa_copy.zip");

    public void run() throws Exception {
        Files.createDirectories(dst.getParent());
        long t0 = System.currentTimeMillis();
        Files.copy(src, dst, REPLACE_EXISTING);
        long ms = System.currentTimeMillis() - t0;
        long size = Files.size(dst);
        System.out.printf("Local write: %d ms (%.1f MiB/s)%n",
                ms, (size/1024.0/1024.0) / (ms/1000.0));
    }
}
