package com.example.leo.ethereumwallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class CreateWalletTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "CreateWalletTask";

    private final WeakReference<Activity> activity;
    private AlertDialog dialog;
    private String walletNameInput;
    private String passwordInput;

    public CreateWalletTask(Activity activity, String walletNameInput, String passwordInput) {
        this.activity = new WeakReference<>(activity);
        this.walletNameInput = walletNameInput;
        this.passwordInput = passwordInput;
    }

    @Override
    protected void onPreExecute() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.get());
        builder.setMessage("生成钱包中...");
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: test");
        String address = Utility.createKeyStore(activity.get(), passwordInput, strings[0]);
        Log.d(TAG, "doInBackground: test");
        return address;
    }

    @Override
    protected void onPostExecute(String createdFileName) {
        if (createdFileName != null) {
            try {
//                String filepath = Utility.getWalletdirectory(activity.get()) + "/" + createdFileName;
//                Credentials credentials = WalletUtils.loadCredentials(passwordInput, filepath);
                Log.d(TAG, createdFileName);
                Log.d(TAG, "onPostExecute: " + createdFileName.split("--").length);
                String[] tokens = createdFileName.split("--");
                String address = "0x" + tokens[tokens.length - 1].split("\\.")[0];
                Log.d(TAG, "onPostExecute: " + address);

                if (!AccountManager.checkAddressExisted(address)) {
                    Account newAccount = new Account();
                    newAccount.setAddress(address);
                    newAccount.setUsername(walletNameInput);
                    newAccount.setKeystore(createdFileName);
                    Log.d(TAG, "onPostExecute: " + newAccount.toString());
//                    newAccount.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
                    AccountManager.appendAccount(newAccount);
                    AccountManager.setCurAccountIndex(AccountManager.getAccountSize() - 1);
                    AccountManager.saveAccounts(activity.get());
                    dialog.dismiss();
                    activity.get().finish();
                } else {
                    dialog.dismiss();
                    Toast.makeText(activity.get(), R.string.wallet_existed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();
            }
        } else {
            dialog.dismiss();
            Toast.makeText(activity.get(), "Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
