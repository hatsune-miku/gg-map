package com.example.map.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.map.databinding.ViewBottomSheetBinding;
import com.example.map.databinding.ViewBottomSheetItemBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ModalSingleSelectionBottomSheet extends BottomSheetDialogFragment {
    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    private ViewBottomSheetBinding binding;
    private final List<String> items;
    private final OnItemClickListener onItemClickListener;

    public ModalSingleSelectionBottomSheet(List<String> items, OnItemClickListener onItemClickListener) {
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ViewBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.itemsRecyclerView.setAdapter(new StringListSingleSelectionAdapter(items, position -> {
            dismiss();
            onItemClickListener.onItemClicked(position);
        }));
    }

    @AllArgsConstructor
    private static class StringListSingleSelectionAdapter extends RecyclerView.Adapter<StringListSingleSelectionAdapter.StringViewHolder> {
        private final List<String> items;
        private final OnItemClickListener onItemClickListener;

        @NonNull
        @Override
        public StringViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            var binding = ViewBottomSheetItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new StringViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull StringViewHolder holder, int position) {
            holder.getBinding().textView.setText(items.get(position));
            holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClicked(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }


        @Getter
        private static class StringViewHolder extends RecyclerView.ViewHolder {
            ViewBottomSheetItemBinding binding;

            public StringViewHolder(@NonNull ViewBottomSheetItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
