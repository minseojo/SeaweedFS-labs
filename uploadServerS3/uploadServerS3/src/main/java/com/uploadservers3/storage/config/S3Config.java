//package com.uploadservers3.storage.config;
//
//import com.uploadservers3.storage.config.annotation.StorageProps;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.S3Configuration;
//
//import java.net.URI;
//
//@Configuration
//class S3Config {
//
//    @Bean
//    S3Client s3Client(StorageProps p) {
//        return S3Client.builder()
//                .endpointOverride(URI.create(p.getEndpoint()))
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(p.getAccessKey(), p.getSecretKey())))
//                .region(Region.of(p.getRegion()))
//                .serviceConfiguration(S3Configuration.builder()
//                        .pathStyleAccessEnabled(true) // MinIO/Seaweed 호환
//                        .build())
//                .build();
//    }
//}
