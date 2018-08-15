package com.example.leo.ethereumwallet.asyncTask;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.activity.TransactionHistoryActivity;
import com.example.leo.ethereumwallet.gson.Transaction;
import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.util.Utility;

import org.json.JSONException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class GetTransactHistTask extends AsyncTask<Void, Void, List<Transaction>> {
    public enum MODE {MODE_PREPEND, MODE_APPEND, MODE_REPLACE}

    private static final String TAG = "GetTransactHistTask";

    private TransactionHistoryActivity.TransactHistFragment fragment;
    private Transaction.TransactionType transactionType;
    private String address;
    private MODE mode;
    private Exception e = null;
    private int latestBlock = 0;
    private int nextpage = 1;

    public GetTransactHistTask(TransactionHistoryActivity.TransactHistFragment fragment,
                               Transaction.TransactionType transactionType, String address, MODE mode){
        this.fragment = fragment;
        this.transactionType = transactionType;
        this.address = address;
        this.mode = mode;
    }

    @Override
    protected void onPreExecute() {
        switch (mode){
            case MODE_APPEND:
                fragment.loadingMore = true;
                latestBlock = fragment.latestBlock;
                nextpage = fragment.nextPage;
                break;
            case MODE_REPLACE:
                fragment.replacing = true;
                fragment.transactionList.clear();
                fragment.recyclerView.getAdapter().notifyDataSetChanged();
                if (fragment.refreshLayout != null){
                    fragment.refreshLayout.setRefreshing(true);
                }
                break;
            case MODE_PREPEND:
                if (fragment.refreshLayout != null){
                    fragment.refreshLayout.setRefreshing(true);
                }
                latestBlock = fragment.latestBlock;
                nextpage = fragment.nextPage;
                break;
        }

    }

    @Override
    protected List<Transaction> doInBackground(Void... voids) {
        try{
            Log.d(TAG, "doInBackground: " + transactionType.toString());
            List<Transaction> transactionList = Utility.getTransactHist(
                    transactionType, mode, address, nextpage, latestBlock);
            return transactionList;
        }catch (JSONException|IOException e){
            this.e = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Transaction> transactionList) {
        if (address.equals(AccountsManager.getCurAccount().getAddress())){
            if (fragment.refreshLayout != null && fragment.refreshLayout.isRefreshing()) {
                fragment.refreshLayout.setRefreshing(false);
            }
            if (transactionList != null) {
                switch (mode) {
                    case MODE_APPEND:
                        fragment.transactionList.remove(fragment.transactionList.size() - 1);
                        if (transactionList.size() == 0) {
                            Transaction transaction = new Transaction();
                            transaction.setTransactionID(Transaction.END);
                            fragment.transactionList.add(transaction);
                        } else {
                            fragment.transactionList.addAll(transactionList);
                            fragment.nextPage += 1;
                            Transaction transaction = new Transaction();
                            transaction.setTransactionID(Transaction.REFRESHING);
                            fragment.transactionList.add(transaction);
                        }
                        fragment.loadingMore = false;
                        break;
                    case MODE_PREPEND:
                        Collections.reverse(transactionList);
                        for (Transaction t : transactionList) {
                            fragment.transactionList.add(0, t);
                        }
                        Toast.makeText(fragment.getActivity(), R.string.transact_hist_refreshed, Toast.LENGTH_SHORT).show();
                        break;
                    case MODE_REPLACE:
//                        Log.d(TAG, "onPostExecute: "+ transactionType.toString());
                        if (transactionList.size() == 0) {
                            Transaction transaction = new Transaction();
                            transaction.setTransactionID(Transaction.END);
                            fragment.transactionList.add(transaction);
                        } else {
                            fragment.transactionList.addAll(transactionList);
                            fragment.nextPage = 2;
                            Transaction transaction = new Transaction();
                            transaction.setTransactionID(Transaction.REFRESHING);
                            fragment.transactionList.add(transaction);
                            fragment.recyclerView.scrollToPosition(0);
                        }
                        fragment.replacing = false;
                        fragment.loadingMore = false;
                        break;
                }
                if (fragment.transactionList.size() != 0 && !fragment.transactionList.get(0).getTransactionID().equals(Transaction.END)) {
                    fragment.latestBlock = Integer.parseInt(fragment.transactionList.get(0).getBlockNumber());
                } else {
                    fragment.latestBlock = 0;
                }
                fragment.recyclerView.getAdapter().notifyDataSetChanged();
            } else if (e != null) {
                Toast.makeText(fragment.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(fragment.getActivity(), "Unknown Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
