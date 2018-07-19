package com.example.leo.ethereumwallet;

import android.content.Context;
import android.util.Log;

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

public class AccountManager {
    private static final String TAG = "AccountManager";
    private static final String accountFilename = "accounts";
    private static List<Account> accounts = new ArrayList<>();
    private static int curAccountIndex = -1;

    public static int getCurAccountIndex() {
        return curAccountIndex;
    }

    public static void setCurAccountIndex(int index) {
        if (index < 0 || index >= accounts.size()){
            index = 0;
        }
        curAccountIndex = index;
    }

    public static Account getAccountAtIndex(int position){
        return accounts.get(position);
    }

    public static boolean checkNameExisted(String name){
        for (Account account : accounts) {
            if (account.getUsername().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkAddressExisted(String address){
        for (Account account : accounts) {
            if (account.getAddress().equals(address)) {
                return true;
            }
        }
        return false;
    }

    public static int getAccountSize(){
        return accounts.size();
    }

    public static Account getCurrentAccount(){
        return accounts.get(curAccountIndex);
    }

    public static void appendAccount(Account account){
        accounts.add(account);
    }

    static void loadAccounts(Context context) {
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

    static void saveAccounts(Context context) {
        Log.d(TAG, "saveAccounts: " + accounts.toString());
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = context.openFileOutput(accountFilename, context.MODE_PRIVATE);
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
