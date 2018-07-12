package com.example.leo.ethereumwallet;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FrameLayout mainView;
    private BottomNavigationView navigationView;

    private boolean secondBack = false;
    private AssetFragment assetFragment = new AssetFragment();
    private PriceFragment priceFragment  = new PriceFragment();
    private MeFragment meFragment = new MeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainView = findViewById(R.id.main_view);
        navigationView = findViewById(R.id.navigator);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()){
                    case R.id.nav_assets:
                        transaction.hide(priceFragment);
                        transaction.hide(meFragment);
                        transaction.show(assetFragment);
                        transaction.commit();
                        break;
                    case R.id.nav_price:
                        transaction.show(priceFragment);
                        transaction.hide(meFragment);
                        transaction.hide(assetFragment);
                        transaction.commit();
                        break;
                    case R.id.nav_account:
                        transaction.hide(priceFragment);
                        transaction.show(meFragment);
                        transaction.hide(assetFragment);
                        transaction.commit();
                        break;
                    default:
                        Log.d(TAG, "onNavigationItemSelected: Change fragment shouldn't encounter default case");
                        transaction.commit();
                        return false;
                }
                return true;
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
//        Account account = new Account();
//        account.setAddress("0x8E647387434cac6cAC3DCFc5170f946e10DA57B0");
//        account.setPrivateKey("47cdb88b969767933348d2550f0516a601151158c300d34b1cb6902d5471d873");
//        account.setUsername("ouyang");
//        AccountUtil.accounts.add(account);
//        Account account2 = new Account();
//        account2.setAddress("0x9b3aFF61a18C117257Ce1f6CAF6Ad69eE7A0189B");
//        account2.setPrivateKey("91457d7f2c42885b1d23e171ae20424e7916e21e252c98c27d0b957701f435e6");
//        account2.setUsername("awepofnawefo");
//        AccountUtil.accounts.add(account2);
//        AccountUtil.saveAccounts(this);
        AccountUtil.loadAccounts(this);
        initFragment();
    }

    private void initFragment(){
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_view, meFragment);
        transaction.add(R.id.main_view, priceFragment);
        transaction.add(R.id.main_view, assetFragment);
        transaction.commit();
    }

    public void onBackPressed() {
        if (secondBack) {
            super.onBackPressed();
        }else {
            this.secondBack = true;
            Toast.makeText(this, R.string.back_button_reminder, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    secondBack = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onDestroy() {
//        Web3jUtil.web3.shutdown();
        AccountUtil.saveAccounts(this);
        super.onDestroy();
    }
}
