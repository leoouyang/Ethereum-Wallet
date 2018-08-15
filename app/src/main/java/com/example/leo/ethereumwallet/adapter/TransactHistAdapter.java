package com.example.leo.ethereumwallet.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.activity.TransactHistDetailActivity;
import com.example.leo.ethereumwallet.gson.Transaction;
import com.example.leo.ethereumwallet.util.AccountsManager;

import org.web3j.utils.Convert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactHistAdapter extends RecyclerView.Adapter {

    private List<Transaction> transactionList;
//    private String selfAddress;

    static class ViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout itemView;
        CircleImageView icon;
        TextView address;
        TextView date;
        TextView sign;
        TextView amount;
        TextView unit;

        RelativeLayout specialView;
        ProgressBar specialViewProgress;
        TextView specialViewText;

        public ViewHolder(View view){
            super(view);
            itemView = view.findViewById(R.id.transact_hist_item_view);
            icon = view.findViewById(R.id.transact_hist_item_icon);
            address = view.findViewById(R.id.transact_hist_item_address);
            date = view.findViewById(R.id.transact_hist_item_date);
            sign = view.findViewById(R.id.transact_hist_item_sign);
            amount = view.findViewById(R.id.transact_hist_item_amount);
            unit = view.findViewById(R.id.transact_hist_item_unit);

            specialView = view.findViewById(R.id.transact_hist_item_special);
            specialViewProgress = view.findViewById(R.id.transact_hist_item_special_progressBar);
            specialViewText = view.findViewById(R.id.transact_hist_item_special_text);
        }
    }

    public TransactHistAdapter(List<Transaction> transactionList){
        this.transactionList = transactionList;
//        this.selfAddress = selfAddress;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Transaction transaction = transactionList.get(holder.getAdapterPosition());
                TransactHistDetailActivity.actionStart(parent.getContext(), transaction);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);
        if (transaction.getTransactionID().equals(Transaction.END)){
            ((ViewHolder)holder).itemView.setVisibility(View.GONE);
            ((ViewHolder)holder).specialView.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).specialViewProgress.setVisibility(View.GONE);
            ((ViewHolder)holder).specialViewText.setText(R.string.no_more_transact_hist);
        }else if (transaction.getTransactionID().equals(Transaction.REFRESHING)){
            ((ViewHolder)holder).itemView.setVisibility(View.GONE);
            ((ViewHolder)holder).specialView.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).specialViewProgress.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).specialViewText.setText(R.string.loading);
        }else {
            ((ViewHolder)holder).itemView.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).specialView.setVisibility(View.GONE);
            ((ViewHolder)holder).specialViewProgress.setVisibility(View.GONE);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            ((ViewHolder) holder).date.setText(simpleDateFormat.format(new Date(Long.parseLong(transaction.getTimeStamp() + "000"))));

            if (transaction.getToAddress().toLowerCase().equals(AccountsManager.getCurAccount().getAddress())) {
                ((ViewHolder) holder).sign.setText(R.string.plus);
                String address = transaction.getFromAddress().substring(0, 7) + "..." + transaction.getFromAddress().substring(35, 42);
                ((ViewHolder) holder).address.setText(address);
            } else {
                ((ViewHolder) holder).sign.setText(R.string.minus);
                String address = transaction.getToAddress().substring(0, 7) + "..." + transaction.getToAddress().substring(35, 42);
                ((ViewHolder) holder).address.setText(address);
            }

            if (transaction.getTransactionType() == Transaction.TransactionType.BLOC) {
                ((ViewHolder) holder).icon.setImageResource(R.drawable.ic_bloc);
                ((ViewHolder) holder).unit.setText(R.string.selfcoin);
                ((ViewHolder) holder).amount.setText(String.format(Locale.CHINA, "%.2f", Convert.fromWei(transaction.getValue(), Convert.Unit.ETHER)));
            } else {
                ((ViewHolder) holder).icon.setImageResource(R.drawable.js_images_asset_eth);
                ((ViewHolder) holder).unit.setText(R.string.eth);
                ((ViewHolder) holder).amount.setText(String.format(Locale.CHINA, "%.4f", Convert.fromWei(transaction.getValue(), Convert.Unit.ETHER)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }


}
