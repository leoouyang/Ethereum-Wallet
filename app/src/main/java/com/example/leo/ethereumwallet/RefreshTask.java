package com.example.leo.ethereumwallet;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class RefreshTask extends AsyncTask<Void, Void, Double> {
    private static final String TAG = "RefreshTask";

    private AssetFragment assetFragment;
    private String address;
    private Account curAccount;

    public RefreshTask(AssetFragment assetFragment) {
        Log.d(TAG, "RefreshTask: Using refresh task");
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
        Utility.getEtherExchangeRate();
        Log.d(TAG, "doInBackground: CNY: " + Utility.eth2cny);
        Log.d(TAG, "doInBackground: USD: " + Utility.eth2usd);
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
