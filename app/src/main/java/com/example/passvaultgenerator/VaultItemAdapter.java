package com.example.passvaultgenerator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VaultItemAdapter extends RecyclerView.Adapter<VaultItemAdapter.ViewHolder> {

    private final List<VaultItem> vaultItems;

    public VaultItemAdapter(List<VaultItem> vaultItems) {
        this.vaultItems = vaultItems;
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

        holder.copyPasswordButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", vaultItem.getPassword());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return vaultItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView usernameTextView;
        public Button copyPasswordButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            usernameTextView = itemView.findViewById(R.id.username_text_view);
            copyPasswordButton = itemView.findViewById(R.id.copy_password_button);
        }
    }
}
