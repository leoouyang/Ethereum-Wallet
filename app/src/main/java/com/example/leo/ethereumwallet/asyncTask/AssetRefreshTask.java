package com.example.leo.ethereumwallet.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.fragment.AssetFragment;
import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.util.Utility;
import com.example.leo.ethereumwallet.gson.Account;

import java.math.BigDecimal;

public class AssetRefreshTask extends AsyncTask<Void, Void, BigDecimal[]> {
    private static final String TAG = "AssetRefreshTask";
    public static boolean refreshing = false;

    private AssetFragment assetFragment;
    private String address;
    private Account curAccount;
    private boolean refreshExchange;

    public AssetRefreshTask(AssetFragment assetFragment, boolean refreshExchange) {
        AssetRefreshTask.refreshing = true;
        Log.d(TAG, "AssetRefreshTask: Using refresh task");
        this.assetFragment = assetFragment;
        curAccount = AccountsManager.getCurAccount();
        this.refreshExchange = refreshExchange;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (assetFragment.drawerLayout != null){
            assetFragment.drawerLayout.closeDrawer(Gravity.LEFT);
        }
        if (assetFragment.swipeRefreshLayout != null){
            assetFragment.swipeRefreshLayout.setRefreshing(true);
        }
        address = curAccount.getAddress();
    }

    @Override
    protected BigDecimal[] doInBackground(Void... voids) {

        if (refreshExchange){
            try {
                Utility.getEtherExchangeRate(assetFragment.getActivity());
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(assetFragment.getActivity(), R.string.exchange_refresh_failed, Toast.LENGTH_SHORT).show();
            }
        }
        BigDecimal ethAmount = Utility.getEtherBalance(address);
        BigDecimal blocAmount = Utility.getBlocBalance(address);

        return new BigDecimal[]{ethAmount, blocAmount};
    }


    @Override
    protected void onPostExecute(BigDecimal[] balances) {
        BigDecimal ethAmount = balances[0];
        BigDecimal blocAmount = balances[1];

        boolean refresh = false;
        if (ethAmount != null) {
            curAccount.setEthereum(ethAmount);
            refresh = true;
        }
        if (ethAmount != null) {
            curAccount.setSelfCoin(blocAmount);
            refresh = true;
        }
        if (refresh) {
            assetFragment.refreshDisplay();
        } else {
            if (assetFragment.swipeRefreshLayout != null){
                assetFragment.swipeRefreshLayout.setRefreshing(false);
            }
            assetFragment.menuButton.setClickable(true);
            Toast.makeText(assetFragment.getActivity(), R.string.balance_refresh_failed, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "refreshAccount: Failed");
        }

        AssetRefreshTask.refreshing = false;
    }
}
