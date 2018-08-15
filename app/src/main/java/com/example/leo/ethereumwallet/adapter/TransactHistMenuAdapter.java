package com.example.leo.ethereumwallet.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.activity.TransactionHistoryActivity;
import com.example.leo.ethereumwallet.gson.Account;
import com.example.leo.ethereumwallet.util.AccountsManager;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactHistMenuAdapter extends RecyclerView.Adapter {
    TransactionHistoryActivity activity;
    int prevIndex;

    public TransactHistMenuAdapter(TransactionHistoryActivity activity) {
        super();
        this.activity = activity;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View accountView;
        CircleImageView profilePicture;
        TextView username;
        public ViewHolder(View view) {
            super(view);
            accountView = view;
            profilePicture = view.findViewById(R.id.asset_side_nav_image);
            username = view.findViewById(R.id.asset_side_nav_username);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_side_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.accountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int curIndex = holder.getAdapterPosition();
                String prevAddress = AccountsManager.getCurAccount().getAddress();
                AccountsManager.setCurAccountIndex(curIndex);
                String curAddress = AccountsManager.getCurAccount().getAddress();
                notifyItemChanged(prevIndex);
                prevIndex = curIndex;
                notifyItemChanged(curIndex);
                if (!prevAddress.equals(curAddress)) {
                    activity.drawerLayout.closeDrawer(Gravity.RIGHT);
                    activity.changeAccount(curIndex);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Account account = AccountsManager.getAccountAtIndex(position);
        ((TransactHistMenuAdapter.ViewHolder) holder).profilePicture.setImageResource(R.drawable.js_components_images_walletavatar);
        ((TransactHistMenuAdapter.ViewHolder) holder).username.setText(account.getUsername());
        if (position == AccountsManager.getCurAccountIndex()) {
            prevIndex = position;
            ((TransactHistMenuAdapter.ViewHolder) holder).accountView.setBackgroundColor(Color.parseColor("#d4d4d4"));
        } else {
            ((TransactHistMenuAdapter.ViewHolder) holder).accountView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }


    @Override
    public int getItemCount() {
        return AccountsManager.getAccountSize();
    }
}
