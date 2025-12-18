package com.example.passvaultgenerator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View vaultItemView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(vaultItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaultItem vaultItem = vaultItems.get(position);
        holder.text1.setText(vaultItem.getName());
        holder.text2.setText(vaultItem.getUsername());
    }

    @Override
    public int getItemCount() {
        return vaultItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text1;
        public TextView text2;

        public ViewHolder(View itemView) {
            super(itemView);
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
