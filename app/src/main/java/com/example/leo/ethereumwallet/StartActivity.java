package com.example.leo.ethereumwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccountManager.loadAccounts(this);
        if(AccountManager.getAccountSize() > 0){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }else{
            //todo more testing on this ides
            Intent i = new Intent(this, CreateWalletActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(AccountManager.getAccountSize() > 0){
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }else{
            //todo
            Intent i = new Intent(this, CreateWalletActivity.class);
            startActivity(i);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
