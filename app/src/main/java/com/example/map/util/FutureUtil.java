package com.example.map.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class FutureUtil {
    public static <R> Future<R> failed(Exception e) {
        CompletableFuture<R> future = new CompletableFuture<>();
        future.completeExceptionally(e);
        return future;
    }
}
