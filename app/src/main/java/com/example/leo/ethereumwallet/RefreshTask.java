package com.example.leo.ethereumwallet;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class RefreshTask extends AsyncTask<Void, Void, Double> {
    private static final String TAG = "RefreshTask";
    
    private int accountIndex;
    private AssetFragment assetFragment;
    private String address;
    private Account curAccount;

    public RefreshTask(int accountIndex, AssetFragment assetFragment){
        Log.d(TAG, "RefreshTask: Using refresh task");
        this.accountIndex = accountIndex;
        this.assetFragment = assetFragment;
        curAccount = AccountUtil.accounts.get(accountIndex);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        assetFragment.drawerLayout.closeDrawer(Gravity.LEFT);
        assetFragment.swipeRefreshLayout.setRefreshing(true);
        address = curAccount.getAddress();
    }

    @Override
    protected Double doInBackground(Void... voids) {
        Double ethAmount =  Web3jUtil.getEtherBalance(address);
        return ethAmount;
    }

    @Override
    protected void onPostExecute(Double ethAmount) {
        if (ethAmount >= 0) {
            curAccount.setEthereum(ethAmount);
            assetFragment.refreshDisplay(accountIndex);
        } else {
            Toast.makeText(assetFragment.getActivity(), "Failed to Refresh", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "refreshAccount: Failed");
        }
    }
}
