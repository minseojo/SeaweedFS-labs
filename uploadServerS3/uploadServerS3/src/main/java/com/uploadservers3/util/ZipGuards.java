package com.uploadservers3.util;

import java.util.concurrent.atomic.AtomicLong;

public final class ZipGuards {
    private ZipGuards(){}

    /** Zip Bomb 방어 한도 (필요시 yml로 빼세요) */
    public static final long MAX_ENTRIES = 1_000_000L;      // 엔트리 개수 상한
    public static final long MAX_UNCOMPRESSED_TOTAL = 2L * 1024 * 1024 * 1024; // 2GB
    public static final long MAX_RATIO = 100L;              // 압축비 상한(해제:압축)

    public static void checkEntryCount(long count) {
        if (count > MAX_ENTRIES) throw new IllegalStateException("too many entries: " + count);
    }

    public static void checkTotals(AtomicLong totalUncomp, AtomicLong totalComp) {
        if (totalUncomp.get() > MAX_UNCOMPRESSED_TOTAL)
            throw new IllegalStateException("too large uncompressed total: " + totalUncomp.get());
        long comp = Math.max(1, totalComp.get());
        long ratio = totalUncomp.get() / comp;
        if (ratio > MAX_RATIO)
            throw new IllegalStateException("suspicious compression ratio: " + ratio + "x");
    }
}
