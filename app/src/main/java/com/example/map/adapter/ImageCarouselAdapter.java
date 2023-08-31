package com.example.map.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.map.R;
import com.example.map.databinding.ViewCarouselImageBinding;
import com.example.map.util.DeviceUtil;
import com.example.map.util.NetworkUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ImageCarouselAdapter extends RecyclerView.Adapter<ImageCarouselAdapter.ViewHolder> {
    private static final String TAG = "ImageCarouselAdapter";

    private List<String> imageUrls;
    private Activity context;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         var binding = ViewCarouselImageBinding.inflate(
             LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageCarouselAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Apply placeholder.
        var imagePlaceholder = getDrawable(R.drawable.image_placeholder);
        var binding = holder.getBinding();
        binding.carouselImageView.setImageDrawable(imagePlaceholder);

        // ImageView#setImageURI() is only for local files.
        // Download image.
        Picasso.get()
            .load(NetworkUtil.BASE_URL + "/download/" + imageUrls.get(position))
            .resize(0, DeviceUtil.dpiToPixels(120))
            .into(binding.carouselImageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    private Drawable getDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(context.getResources(), id, context.getTheme());
    }

    @Getter
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewCarouselImageBinding binding;
        public ViewHolder(ViewCarouselImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
