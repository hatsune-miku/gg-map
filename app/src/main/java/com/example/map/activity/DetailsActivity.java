package com.example.map.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.map.R;
import com.example.map.adapter.CommentAdapter;
import com.example.map.databinding.ActivityDetailsBinding;
import com.example.map.entity.Packets;
import com.example.map.fragment.CommentPreviewFragment;
import com.example.map.fragment.DetailsPreferencesFragment;
import com.example.map.helper.AccountHelper;
import com.example.map.helper.ActivityHelper;
import com.example.map.mapper.ServiceImageMapper;
import com.example.map.util.AddressUtil;
import com.example.map.model.Address;
import com.example.map.util.NetworkUtil;
import com.example.map.util.PermissionUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailsActivity extends AppCompatActivity implements CommentAdapter.ICommentOperationDelegate {
    private static final String TAG = "DetailsActivity";
    private ActivityDetailsBinding binding;
    private Address address;
    private AccountHelper accountHelper;
    private CommentAdapter commentAdapter;
    private ActivityHelper activityHelper;
    private boolean loading = false;
    private Packets.CommentInformation targetComment;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get address from intent.
        address = AddressUtil.extractFromIntent(getIntent());

        // Helpers.
        activityHelper = new ActivityHelper(this);
        accountHelper = new AccountHelper(this);

        // Adapter.
        commentAdapter = new CommentAdapter(this, this);

        bind();

        // Prepare the fragment.
        var fragmentCompat = new DetailsPreferencesFragment(address);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragmentContainer, fragmentCompat)
            .commit();

        refreshComments();
        setupOnImagePickedListener();

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        activityHelper.setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));
    }

    private void bind() {
        // Prepare the image.
        binding.addressImageView.setImageResource(
            ServiceImageMapper.getImageResourceId(address));

        binding.addressImageView.setScaleType(
            ImageView.ScaleType.FIT_XY);

        binding.topAppBar.setTitle(address.getName());
        binding.topAppBar.setNavigationOnClickListener(v -> finish());

        // Send the comment.
        binding.textInputLayoutComment.setEndIconOnClickListener(v -> onSendComment());

        // Prepare recycler view.
        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.commentRecyclerView.setAdapter(commentAdapter);
    }

    private void setNoComments(boolean noComments) {
        binding.textViewNoComment.setVisibility(noComments
            ? View.VISIBLE
            : View.GONE);
        binding.commentRecyclerView.setVisibility(noComments
            ? View.GONE
            : View.VISIBLE);
    }

    private void refreshComments() {
        Optional<String> tokenOpt = accountHelper.getToken();
        if (!tokenOpt.isPresent()) {
            LoginActivity.show(this);
            return;
        }
        String token = tokenOpt.get();

        executor.execute(() -> {
            try {
                var result = NetworkUtil.getCommentList(address.getSiteIdentifier(), token).get();
                if (result.isSuccess()) {
                    var comments = result.getComments();
                    runOnUiThread(() -> {
                        setNoComments(comments.isEmpty());
                        commentAdapter.updateComments(comments);
                    });
                }
            }
            catch (Exception e) {
                Log.e(TAG, "bind: ", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "拉取评论失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void performSendComment(String token) {
        if (loading) {
            return;
        }
        loading = true;

        String content = Objects.requireNonNull(binding.editTextComment.getText()).toString();
        String author = accountHelper.getLoggingInUsername().orElse("User");

        if (content.isBlank()) {
            loading = false;
            return;
        }

        var fragment = new CommentPreviewFragment(content, author);
        var view = fragment.prepareView(getLayoutInflater());

        var builder = new MaterialAlertDialogBuilder(this)
            .setTitle("预览评论")
            .setView(view)
            .setCancelable(false)
            .setNegativeButton("取消", (d, w) -> loading = false)
            .setPositiveButton("发布", (d, w) -> executor.execute(() -> {
                try {
                    var result = NetworkUtil.postComment(
                        Packets.PostCommentPacket.builder()
                            .comment(content)
                            .siteIdentifier(address.getSiteIdentifier())
                            .rating(fragment.getRating())
                            .build(),
                        token
                    ).get();
                    if (result.isSuccess()) {
                        refreshComments();
                    }
                    runOnUiThread(() -> {
                        binding.editTextComment.setText("");
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                catch (Exception e) {
                    Log.e(TAG, "bind: ", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "发送评论失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
                finally {
                    loading = false;
                }
            }));
        builder.create().show();
    }

    private void onSendComment() {
        Optional<String> tokenOpt = accountHelper.getToken();
        if (!tokenOpt.isPresent()) {
            LoginActivity.show(this);
            return;
        }
        String token = tokenOpt.get();
        performSendComment(token);
    }

    private void setupOnImagePickedListener() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null || uri.getPath() == null || targetComment == null) {
                    return;
                }

                var fileOpt = PermissionUtil.fileFromUri(DetailsActivity.this, uri);
                if (!fileOpt.isPresent()) {
                    Toast.makeText(this, "无法获取图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                appendFileToComment(targetComment, fileOpt.get());
            });
    }

    private void appendFileToComment(Packets.CommentInformation comment, File file) {
        Optional<String> tokenOpt = accountHelper.getToken();
        if (!tokenOpt.isPresent()) {
            LoginActivity.show(this);
            return;
        }
        String token = tokenOpt.get();
        activityHelper.loadingDialogOpen("正在上传图片...");

        executor.execute(() -> {
            try {
                var result = NetworkUtil.postMediaToExistingComment(comment.getSiteIdentifier(), file, token).get();
                if (result.isSuccess()) {
                    refreshComments();
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
            catch (Exception e) {
                Log.e(TAG, "bind: ", e);
                runOnUiThread(() -> {
                    Toast.makeText(this, "上传图片失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                runOnUiThread(() -> activityHelper.loadingDialogClose());
            }
        });
    }

    @Override
    public void onAppendImagesToComment(Packets.CommentInformation comment) {
        targetComment = comment;
        pickMedia.launch(new PickVisualMediaRequest.Builder().build());
    }

    @Override
    public void onDeleteComment(Packets.CommentInformation comment) {
        activityHelper.showAlertDialog("确认要删除这条评论吗？", "删除评论", "删除", () -> {
            Optional<String> tokenOpt = accountHelper.getToken();
            if (!tokenOpt.isPresent()) {
                LoginActivity.show(this);
                return;
            }
            String token = tokenOpt.get();

            executor.execute(() -> {
                try {
                    var result = NetworkUtil.deleteComment(comment.getSiteIdentifier(), token).get();
                    if (result.isSuccess()) {
                        refreshComments();
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
                catch (Exception e) {
                    Log.e(TAG, "bind: ", e);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "删除评论失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        }, "取消", null);
    }
}
