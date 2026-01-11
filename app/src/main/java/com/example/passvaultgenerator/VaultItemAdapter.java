package com.example.passvaultgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VaultItemAdapter extends RecyclerView.Adapter<VaultItemAdapter.ViewHolder> {

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    private List<VaultItem> vaultItems;
    private List<VaultItem> fullList;
    private final OnItemDeleteListener deleteListener;

    public VaultItemAdapter(List<VaultItem> vaultItems, OnItemDeleteListener deleteListener) {
        this.vaultItems = vaultItems;
        this.fullList = new ArrayList<>(vaultItems);
        this.deleteListener = deleteListener;
    }

    public void updateList(List<VaultItem> newList) {
        this.vaultItems = newList;
        this.fullList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        List<VaultItem> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(fullList);
        } else {
            for (VaultItem item : fullList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getUsername().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        this.vaultItems = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View vaultItemView = inflater.inflate(R.layout.item_vault, parent, false);
        return new ViewHolder(vaultItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaultItem vaultItem = vaultItems.get(position);

        holder.nameTextView.setText(vaultItem.getName());
        holder.usernameTextView.setText(vaultItem.getUsername());

        long currentTime = System.currentTimeMillis();
        if (currentTime - vaultItem.getLastChangedTimestamp() > 60000) {
            holder.statusTextView.setVisibility(View.VISIBLE);
            holder.statusTextView.setText("SECURITY RISK: STALE PASSWORD");
            holder.warningIcon.setVisibility(View.VISIBLE);
        } else {
            holder.statusTextView.setVisibility(View.GONE);
            holder.warningIcon.setVisibility(View.GONE);
        }

        holder.copyPasswordButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", vaultItem.getPassword());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                // Find original position in the full list if filtered
                int originalPos = fullList.indexOf(vaultItem);
                deleteListener.onItemDelete(originalPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vaultItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView usernameTextView;
        public TextView statusTextView;
        public ImageView warningIcon;
        public ImageButton copyPasswordButton;
        public ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
            warningIcon = itemView.findViewById(R.id.warning_icon);
            copyPasswordButton = itemView.findViewById(R.id.copy_password_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
