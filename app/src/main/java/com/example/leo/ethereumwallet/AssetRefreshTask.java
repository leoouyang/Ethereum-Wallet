package com.example.leo.ethereumwallet;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class AssetRefreshTask extends AsyncTask<Void, Void, Double> {
    private static final String TAG = "AssetRefreshTask";

    private AssetFragment assetFragment;
    private String address;
    private Account curAccount;

    public AssetRefreshTask(AssetFragment assetFragment) {
        Log.d(TAG, "AssetRefreshTask: Using refresh task");
        this.assetFragment = assetFragment;
        curAccount = AccountManager.getCurrentAccount();
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
        Utility.getEtherExchangeRate(assetFragment.getActivity());
        double ethAmount = Utility.getEtherBalance(address);

        return ethAmount;
    }


    @Override
    protected void onPostExecute(Double ethAmount) {
        if (ethAmount >= 0) {
            curAccount.setEthereum(ethAmount);
            assetFragment.refreshDisplay();

        } else {
            assetFragment.swipeRefreshLayout.setRefreshing(false);
            assetFragment.menuButton.setClickable(true);
            Toast.makeText(assetFragment.getActivity(), "Failed to Refresh", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "refreshAccount: Failed");
        }
    }
}