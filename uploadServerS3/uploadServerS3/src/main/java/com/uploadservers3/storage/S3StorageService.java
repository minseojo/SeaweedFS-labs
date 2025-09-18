//package com.uploadservers3.storage;
//
//import com.uploadservers3.storage.config.annotation.StorageProps;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.*;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class S3StorageService {
//    private final S3Client s3;
//    private final StorageProps props;
//
//    record MpCtx(String uploadId, String key, List<CompletedPart> parts, int partNo){}
//
//    public MpCtx mpInit(String key, String contentType) {
//        var res = s3.createMultipartUpload(CreateMultipartUploadRequest.builder()
//                .bucket(props.getBucket()).key(key).contentType(contentType).build());
//        return new MpCtx(res.uploadId(), key, new ArrayList<>(), 0);
//    }
//
//    public void mpUploadPart(MpCtx ctx, byte[] buf, int len) {
//        int nextNo = ctx.partNo()+1;
//        var up = UploadPartRequest.builder()
//                .bucket(props.getBucket()).key(ctx.key())
//                .uploadId(ctx.uploadId()).partNumber(nextNo).build();
//        var res = s3.uploadPart(up, RequestBody.fromBytes(Arrays.copyOf(buf, len)));
//        ctx.parts().add(CompletedPart.builder().eTag(res.eTag()).partNumber(nextNo).build());
//        // bump part number (record pattern immutability workaround)
//        try {
//            var f = MpCtx.class.getDeclaredField("partNo"); f.setAccessible(true);
//            f.setInt(ctx, nextNo);
//        } catch (Exception ignored) {}
//    }
//
//    public void mpComplete(MpCtx ctx) {
//        s3.completeMultipartUpload(CompleteMultipartUploadRequest.builder()
//                .bucket(props.getBucket()).key(ctx.key()).uploadId(ctx.uploadId())
//                .multipartUpload(CompletedMultipartUpload.builder().parts(ctx.parts()).build())
//                .build());
//    }
//
//    public void mpAbort(MpCtx ctx) {
//        s3.abortMultipartUpload(AbortMultipartUploadRequest.builder()
//                .bucket(props.getBucket()).key(ctx.key()).uploadId(ctx.uploadId()).build());
//    }
//
//    public void copy(String srcKey, String dstKey, Map<String, String> userMeta) {
//        var b = CopyObjectRequest.builder()
//                .bucket(props.getBucket()).copySource(props.getBucket()+"/"+srcKey).key(dstKey);
//        if (userMeta != null && !userMeta.isEmpty()) {
//            b = b.metadataDirective(MetadataDirective.REPLACE).metadata(userMeta);
//        }
//        s3.copyObject(b.build());
//    }
//
//    public void delete(String key) {
//        s3.deleteObject(DeleteObjectRequest.builder().bucket(props.getBucket()).key(key).build());
//    }
//}
