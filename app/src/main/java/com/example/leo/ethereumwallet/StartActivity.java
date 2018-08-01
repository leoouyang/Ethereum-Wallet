package com.example.leo.ethereumwallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private boolean firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AccountsManager.loadAccounts(this);
//
//        firstTime = true;
//        if(AccountsManager.getAccountSize() > 0){
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            finish();
//        }else{
//            Intent i = new Intent(this, CreateWalletActivity.class);
//            startActivity(i);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (AccountsManager.getAccountSize() > 0) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, R.string.wallet_needed, Toast.LENGTH_SHORT).show();
            CreateWalletActivity.actionStart(this);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
