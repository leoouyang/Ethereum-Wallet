package com.example.leo.ethereumwallet;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SideAccountAdapter extends RecyclerView.Adapter {
    private static final String TAG = "SideAccountAdapter";
    private AssetFragment assetFragment;
    private int curIndex = 0;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View accountView;
        CircleImageView profilePicture;
        TextView username;

        private ViewHolder(View view){
            super(view);
            accountView = view;
            profilePicture = view.findViewById(R.id.asset_side_nav_image);
            username = view.findViewById(R.id.asset_side_nav_username);
        }
    }

    public SideAccountAdapter(AssetFragment assetFragment){
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
                notifyItemChanged(curIndex);
                curIndex = holder.getAdapterPosition();
                notifyItemChanged(curIndex);
                assetFragment.refreshDisplay(curIndex);
                assetFragment.refreshAccount(curIndex);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: bind position " + position);
        Account account = AccountUtil.accounts.get(position);
        ((ViewHolder) holder).profilePicture.setImageResource(R.drawable.empty_profile_public);
        ((ViewHolder) holder).username.setText(account.getUsername());
        if (position == curIndex){
            ((ViewHolder)holder).accountView.setBackgroundColor(Color.parseColor("#d4d4d4"));
        }else {
            ((ViewHolder)holder).accountView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return AccountUtil.accounts.size();
    }
}