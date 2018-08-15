package com.example.leo.ethereumwallet.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.leo.ethereumwallet.gson.Account;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class AccountsManager {

    public static String ACCOUNTS_CHANGE_SIGNAL = "com.example.leo.ethereumwallet.PRICE_REFRESHED";

    private static final String TAG = "AccountsManager";
    private static final String accountFilename = "accounts";
    private static List<Account> accounts = new ArrayList<>();
    private static int curAccountIndex = -1;

    public static int getCurAccountIndex() {
        return curAccountIndex;
    }

    public static void setCurAccountIndex(int index) {
        if (index < 0 || index >= accounts.size()) {
            index = 0;
        }
        curAccountIndex = index;
    }

    public static Account getAccountAtIndex(int position) {
        return accounts.get(position);
    }

    public static boolean checkNameExisted(String name) {
        for (Account account : accounts) {
            if (account.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkAddressExisted(String address) {
        for (Account account : accounts) {
            if (account.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public static int getAccountSize() {
        return accounts.size();
    }

    public static Account getCurAccount() {
        return accounts.get(curAccountIndex);
    }

    public static void appendAccount(Account account) {
        accounts.add(account);
//        context.sendBroadcast(new Intent(ACCOUNTS_CHANGE_SIGNAL));
    }

    public static void deleteAccount(Account account) {
        accounts.remove(account);
        if (curAccountIndex >= accounts.size()){
            curAccountIndex = accounts.size()-1;
        }
//        context.sendBroadcast(new Intent(ACCOUNTS_CHANGE_SIGNAL));
    }

    public static void loadAccounts(Context context) {
        FileInputStream in = null;
        BufferedReader reader = null;
        try {
            in = context.openFileInput(accountFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            String accountsJson = reader.readLine();
            if (accountsJson != null) {
                accounts.clear();
                accounts = new Gson().fromJson(accountsJson, new TypeToken<List<Account>>() {
                }.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveAccounts(Context context) {
        Log.d(TAG, "saveAccounts: " + accounts.toString());
        if (accounts.size() > 0) {
            FileOutputStream out = null;
            BufferedWriter writer = null;
            try {
                out = context.openFileOutput(accountFilename, Context.MODE_PRIVATE);
                writer = new BufferedWriter(new OutputStreamWriter(out));
                String accountsJson = new Gson().toJson(accounts, new TypeToken<List<Account>>() {
                }.getType());
                writer.write(accountsJson);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
