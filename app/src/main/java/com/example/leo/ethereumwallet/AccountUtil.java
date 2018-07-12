package com.example.leo.ethereumwallet;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.spongycastle.jce.exception.ExtIOException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class AccountUtil {
    private static final String TAG = "AccountUtil";
    static List<Account> accounts = new ArrayList<Account>();
    private static final String fileName = "accounts";


    static void loadAccounts(Context context){
        FileInputStream in = null;
        BufferedReader reader = null;
        try{
            in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            String accountsJson = reader.readLine();
            if (accountsJson != null){
                accounts = new Gson().fromJson(accountsJson, new TypeToken<List<Account>>(){}.getType());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (reader != null){
                    reader.close();
                }
                if (in != null){
                    in.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static void saveAccounts(Context context){
        Log.d(TAG, "saveAccounts: "+ accounts.toString());
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            out = context.openFileOutput(fileName, context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            String accountsJson = new Gson().toJson(accounts, new TypeToken<List<Account>>(){}.getType());
            writer.write(accountsJson);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if (writer != null){
                    writer.close();
                }
                if (out != null){
                    out.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
