//package com.uploadservers3.s3.config;
//
//import com.uploadservers3.s3.config.annotation.StorageProps;
//import io.minio.MinioClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class MinioConfig {
//    @Bean
//    public MinioClient minioClient(StorageProps p) {
//        return MinioClient.builder()
//                .endpoint(p.getEndpoint())
//                .credentials(p.getAccessKey(), p.getSecretKey())
//                .build();
//    }
//}
//
