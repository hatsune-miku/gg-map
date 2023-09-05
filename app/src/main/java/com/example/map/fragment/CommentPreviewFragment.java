package com.example.map.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.map.R;
import com.example.map.databinding.ViewCommentReviewBinding;
import com.example.map.entity.Packets;

import lombok.Getter;

public class CommentPreviewFragment extends AppCompatDialogFragment {
    private ViewCommentReviewBinding binding;

    @Getter
    private final String content;

    @Getter
    private final String author;

    @Getter
    private int rating = 0;

    public CommentPreviewFragment(String content, String author) {
        super(R.layout.view_comment_review);
        this.content = content;
        this.author = author;
    }

    @NonNull
    public View prepareView(LayoutInflater inflater) {
        var view = onCreateView(inflater, null, null);
        assert view != null;
        onViewCreated(view, null);
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewCommentReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bind();
    }

    private void bind() {
        binding.textComment.setText(getContent());
        binding.textCommentAuthor.setText(getAuthor());
        binding.ratingBarEditing.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            CommentPreviewFragment.this.rating = (int) rating;
            binding.textViewScoreFriendlyDescription.setText(getRatingDescription((int) rating));
            binding.ratingBar.setRating(rating);
        });
    }

    private static String getRatingDescription(int rating) {
        return switch (rating) {
            case 0 -> "零分差评";
            case 1 -> "1分差评";
            case 2 -> "2分";
            case 3 -> "3分";
            case 4 -> "4分";
            case 5 -> "满分！";
            default -> "点击五角星，进行评分";
        };
    }
}
