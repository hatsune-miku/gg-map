package com.example.map.util;

import com.example.map.entity.Packets.*;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.annotation.Nullable;

import lombok.AllArgsConstructor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkUtil {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();
    private static final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);


    public static final String BASE_URL = "http://10.0.0.205:8080";

    public static Future<LoginResponse> login(LoginPacket packet) {
        return new HttpRequest<LoginPacket, LoginResponse>(LoginPacket.class, LoginResponse.class)
            .post("/auth/token", packet, null);
    }

    public static Future<GetUserInformationResponse> getUserInformation(String token) {
        return new HttpRequest<String, GetUserInformationResponse>(String.class, GetUserInformationResponse.class)
            .get("/user", token);
    }

    public static Future<RegisterResponse> register(RegisterPacket packet) {
        return new HttpRequest<RegisterPacket, RegisterResponse>(RegisterPacket.class, RegisterResponse.class)
            .post("/register", packet, null);
    }

    public static Future<PostCommentResponse> postComment(PostCommentPacket packet, String token) {
        return new HttpRequest<PostCommentPacket, PostCommentResponse>(PostCommentPacket.class, PostCommentResponse.class)
            .post("/comment", packet, token);
    }

    public static Future<DeleteCommentResponse> deleteComment(String siteIdentifier, String token) {
        return new HttpRequest<String, DeleteCommentResponse>(String.class, DeleteCommentResponse.class)
            .delete("/comment/" + siteIdentifier, token);
    }

    public static Future<GetCommentListResponse> getCommentList(String siteIdentifier, String token) {
        return new HttpRequest<String, GetCommentListResponse>(String.class, GetCommentListResponse.class)
            .get("/comment/" + siteIdentifier, token);
    }

    public static Future<PostMediaToExistingCommentResponse> postMediaToExistingComment(String siteIdentifier, File file, String token) {
        return new HttpRequest<String, PostMediaToExistingCommentResponse>(String.class, PostMediaToExistingCommentResponse.class)
            .postMultipart("/comment/" + siteIdentifier, file, token);
    }

    @AllArgsConstructor
    public static class HttpRequest<T, R> {
        private final Class<T> packetClass;
        private final Class<R> responseClass;

        public Future<R> request(Request request) {
            Gson gson = new Gson();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() == null) {
                    throw new IOException("response body is null");
                }
                var result = gson.fromJson(response.body().string(), responseClass);
                response.close();
                return CompletableFuture.completedFuture(result);
            }
            catch (Exception e) {
                return FutureUtil.failed(e);
            }
        }

        private void tryAppendToken(Request.Builder builder, @Nullable String token) {
            if (token != null) {
                builder.header("Authorization", "Bearer " + token);
            }
        }

        public Future<R> post(String url, T object, @Nullable String token) {
            Gson gson = new Gson();
            RequestBody body = RequestBody.create(gson.toJson(object, packetClass), JSON);
            Request.Builder builder = new Request.Builder()
                .url(BASE_URL + url)
                .post(body);

            tryAppendToken(builder, token);
            return request(builder.build());
        }

        public Future<R> postMultipart(String url, File file, @Nullable String token) {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));
            MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
            Request.Builder builder = new Request.Builder()
                .url(BASE_URL + url)
                .post(multipartBody);

            tryAppendToken(builder, token);
            return request(builder.build());
        }

        public Future<R> delete(String url, @Nullable String token) {
            Request.Builder builder = new Request.Builder()
                .url(BASE_URL + url)
                .delete();
            tryAppendToken(builder, token);
            return request(builder.build());
        }

        public Future<R> get(String url, @Nullable String token) {
            Request.Builder builder = new Request.Builder()
                .url(BASE_URL + url)
                .get();

            tryAppendToken(builder, token);
            return request(builder.build());
        }
    }
}
