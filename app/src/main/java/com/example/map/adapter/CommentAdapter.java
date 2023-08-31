package com.example.map.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.map.databinding.ViewCommentBinding;
import com.example.map.entity.Packets;
import com.example.map.helper.AccountHelper;
import com.google.android.material.carousel.CarouselLayoutManager;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    List<Packets.CommentInformation> comments = new ArrayList<>();
    private final AccountHelper accountHelper;
    private final Activity context;
    private final ICommentOperationDelegate delegate;

    public CommentAdapter(Activity context, ICommentOperationDelegate delegate) {
        accountHelper = new AccountHelper(context);
        this.context = context;
        this.delegate = delegate;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateComments(List<Packets.CommentInformation> comments) {
        this.comments.clear();
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ViewCommentBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var comment = comments.get(position);
        var binding = holder.getBinding();

        // Setup basic information.
        binding.textComment.setText(comment.getComment());
        binding.textCommentAuthor.setText("@" + comment.getUsername());

        // Setup images.
        if (comment.getImageUrls().isEmpty()) {
            binding.carouselRecyclerView.setVisibility(View.GONE);
        } else {
            binding.carouselRecyclerView.setLayoutManager(new CarouselLayoutManager());
            binding.carouselRecyclerView.setVisibility(View.VISIBLE);
            binding.carouselRecyclerView.setAdapter(
                new ImageCarouselAdapter(comment.getImageUrls(), context));
        }

        // Setup buttons.
        var loggingInUserNameOpt = accountHelper.getLoggingInUsername();
        if (loggingInUserNameOpt.isPresent() && loggingInUserNameOpt.get().equals(comment.getUsername())) {
            // Author of the comment. Show operations.
            binding.linearLayoutCommentButtons.setVisibility(View.VISIBLE);
            binding.buttonAddImage.setOnClickListener(v -> delegate.onAppendImagesToComment(comment));
            binding.buttonDelete.setOnClickListener(v -> delegate.onDeleteComment(comment));
        } else {
            binding.linearLayoutCommentButtons.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewCommentBinding binding;

        public ViewHolder(ViewCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ICommentOperationDelegate {
        void onAppendImagesToComment(Packets.CommentInformation comment);
        void onDeleteComment(Packets.CommentInformation comment);
    }
}
