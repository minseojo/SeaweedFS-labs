package reader.manifest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.file.Files;
import java.nio.file.Path;

public final class ManifestJson {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)   // pretty-print
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static void writeToFile(Manifest manifest, Path outPath) throws Exception {
        // 상위 디렉터리 없으면 생성
        if (outPath.getParent() != null) Files.createDirectories(outPath.getParent());
        MAPPER.writeValue(outPath.toFile(), manifest);
    }

    public static String toJsonString(Manifest manifest) throws Exception {
        return MAPPER.writeValueAsString(manifest);
    }
}
