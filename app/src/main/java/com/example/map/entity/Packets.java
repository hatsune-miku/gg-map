package com.example.map.entity;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class Packets {

    @Builder
    @Getter
    public static class LoginPacket {
        String username;
        String password;
    }

    @Builder
    @Getter
    public static class LoginResponse {
        boolean success;
        String message;
        String token;
    }

    @Builder
    @Getter
    public static class RegisterPacket {
        String username;
        String password;
    }

    @Builder
    @Getter
    public static class RegisterResponse {
        boolean success;
        String message;
        String token;
    }

    @Builder
    @Getter
    public static class GetUserInformationResponse {
        boolean success;
        String message;
        String username;
    }

    @Builder
    @Getter
    public static class PostCommentPacket {
        String siteIdentifier;
        String comment;
        int rating;
    }

    @Builder
    public static class PostCommentResponse {
        @Getter
        boolean success;
        String message;

        public String getMessage() {
            if (message == null || message.isEmpty()) {
                return "服务器错误";
            }
            return message;
        }
    }

    @Builder
    @Getter
    public static class PostMediaToExistingCommentResponse {
        boolean success;
        String message;
    }

    @Builder
    @Getter
    public static class DeleteCommentResponse {
        boolean success;
        String message;
    }

    @Builder
    @Getter
    public static class GetCommentListPacket {
        String siteIdentifier;
    }

    @Builder
    @Getter
    public static class GetCommentListResponse {
        boolean success;
        String message;
        List<CommentInformation> comments;
    }

    @Getter
    @Builder
    public static class CommentInformation {
        String siteIdentifier;
        String comment;
        String username;
        int rating;
        List<String> imageUrls;
    }
}
