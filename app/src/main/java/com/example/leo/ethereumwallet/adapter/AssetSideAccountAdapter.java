package com.example.leo.ethereumwallet.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.asyncTask.AssetRefreshTask;
import com.example.leo.ethereumwallet.fragment.AssetFragment;
import com.example.leo.ethereumwallet.gson.Account;

import de.hdodenhof.circleimageview.CircleImageView;

public class AssetSideAccountAdapter extends RecyclerView.Adapter {
    private static final String TAG = "AssetSideAccountAdapter";
    private AssetFragment assetFragment;
    private int prevIndex = 0;

    public AssetSideAccountAdapter(AssetFragment assetFragment) {
        this.assetFragment = assetFragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_side_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.accountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AssetRefreshTask.refreshing) {
                    int curIndex = holder.getAdapterPosition();
                    AccountsManager.setCurAccountIndex(curIndex);
                    notifyItemChanged(prevIndex);
                    prevIndex = curIndex;
                    notifyItemChanged(curIndex);
                    assetFragment.refreshDisplay();
                    new AssetRefreshTask(assetFragment, false).execute();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: bind position " + position);
        Account account = AccountsManager.getAccountAtIndex(position);
        ((ViewHolder) holder).profilePicture.setImageResource(R.drawable.js_components_images_walletavatar);
        ((ViewHolder) holder).username.setText(account.getUsername());
        if (position == AccountsManager.getCurAccountIndex()) {
            prevIndex = position;
            ((ViewHolder) holder).accountView.setBackgroundColor(Color.parseColor("#d4d4d4"));
        } else {
            ((ViewHolder) holder).accountView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }


    @Override
    public int getItemCount() {
        return AccountsManager.getAccountSize();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View accountView;
        CircleImageView profilePicture;
        TextView username;

        private ViewHolder(View view) {
            super(view);
            accountView = view;
            profilePicture = view.findViewById(R.id.asset_side_nav_image);
            username = view.findViewById(R.id.asset_side_nav_username);
        }
    }
}
