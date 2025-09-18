//package com.uploadservers3.storage.config.annotation;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//
//@Setter
//@Getter
//@ConfigurationProperties(prefix = "storage")
//public class StorageProps {
//
//    private String endpoint;        // http://s3-internal-lb:8333
//    private String region;          // ap-northeast-2 등 (임의값 가능)
//    private String accessKey;
//    private String secretKey;
//    private String bucket;          // 기본 버킷명
//    private Integer partSizeMiB;    // 멀티파트 파트 크기(MiB)
//    private Integer concurrentParts;// 동시 파트 업로드 개수(사용시)
//    private String incomingPrefix;  // incoming
//    private String objectsPrefix;   // objects
//    private String quarantinePrefix;// quarantine
//
//}
//
