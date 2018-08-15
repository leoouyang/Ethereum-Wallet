package com.example.leo.ethereumwallet.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.adapter.AssetSideAccountAdapter;
import com.example.leo.ethereumwallet.util.Utility;
import com.example.leo.ethereumwallet.activity.CreateWalletActivity;
import com.example.leo.ethereumwallet.activity.MakePaymentActivity;
import com.example.leo.ethereumwallet.activity.ReceivePaymentActivity;
import com.example.leo.ethereumwallet.activity.ScanQRActivity;
import com.example.leo.ethereumwallet.asyncTask.AssetRefreshTask;
import com.example.leo.ethereumwallet.gson.Account;

import java.util.Locale;

import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;

import static android.app.Activity.RESULT_OK;

//import org.ethereum.crypto.ECKey;


public class AssetFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AssetFragment";
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefreshLayout;

//    private Animation hideAnimation;
    private boolean firstTime;
    private Animation showAnimation;
//    private AccountChangeReceiver accountChangeReceiver;
    private IntentFilter intentFilter;

    public Button menuButton;
    private RecyclerView recyclerView;
    private LinearLayout createAccountLayout;
    private LinearLayout scanQRLayout;

    private TextView usernameView;
    private TextView addressView;
    private LinearLayout receiveLayout;
    private TextView realTotalView;

    private RelativeLayout ethLayout;
    private TextView ethView;
    private TextView realETHView;
    private LinearLayout ethButtons;
    private Button ethMakePay;
    private Button ethReceivePay;

    private LinearLayout selfCoinContainer;
    private RelativeLayout selfCoinLayout;
    private TextView selfCoinView;
    private TextView realSelfCoinView;
    private LinearLayout selfCoinButtons;
    private Button selfCoinMakePay;
    private Button selfCoinReceivePay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccountsManager.setCurAccountIndex(0);

//        intentFilter = new IntentFilter();
//        intentFilter.addAction(AccountsManager.ACCOUNTS_CHANGE_SIGNAL);
//        accountChangeReceiver = new AccountChangeReceiver();

        if (!AssetRefreshTask.refreshing) {
            new AssetRefreshTask(this, true).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset, container, false);

        showAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.show);
//        hideAnimation = AnimationUtils.loadAnimation(this.getActivity(),R.anim.hide);

        drawerLayout = view.findViewById(R.id.asset_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        swipeRefreshLayout = view.findViewById(R.id.asset_swipe_refresh);

        menuButton = view.findViewById(R.id.asset_menu);
        menuButton.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.asset_side_nav_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(new AssetSideAccountAdapter(this));

        createAccountLayout = view.findViewById(R.id.asset_side_nav_create_account);
        createAccountLayout.setOnClickListener(this);
        scanQRLayout = view.findViewById(R.id.asset_side_nav_scanQR);
        scanQRLayout.setOnClickListener(this);

        usernameView = view.findViewById(R.id.asset_username);
        addressView = view.findViewById(R.id.asset_address);
        receiveLayout = view.findViewById(R.id.asset_receive_layout);
        receiveLayout.setOnClickListener(this);
        realTotalView = view.findViewById(R.id.asset_total_real);

        ethLayout = view.findViewById(R.id.asset_eth_layout);
        ethLayout.setOnClickListener(this);
        ethView = view.findViewById(R.id.asset_eth_amount);
        realETHView = view.findViewById(R.id.asset_eth_amount_real);
        ethButtons = view.findViewById(R.id.asset_eth_buttons);
        ethButtons.setVisibility(View.GONE);
        ethMakePay = view.findViewById(R.id.asset_eth_make_payment);
        ethMakePay.setOnClickListener(this);
        ethReceivePay = view.findViewById(R.id.asset_eth_receive_payment);
        ethReceivePay.setOnClickListener(this);

        selfCoinContainer = view.findViewById(R.id.asset_selfCoin_container);
        selfCoinLayout = view.findViewById(R.id.asset_selfCoin_layout);
        selfCoinLayout.setOnClickListener(this);
        selfCoinView = view.findViewById(R.id.asset_selfCoin_amount);
        realSelfCoinView = view.findViewById(R.id.asset_selfCoin_amount_real);
        selfCoinButtons = view.findViewById(R.id.asset_selfCoin_buttons);
        selfCoinMakePay = view.findViewById(R.id.asset_selfCoin_make_payment);
        selfCoinMakePay.setOnClickListener(this);
        selfCoinReceivePay = view.findViewById(R.id.asset_selfCoin_receive_payment);
        selfCoinReceivePay.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!AssetRefreshTask.refreshing) {
                    new AssetRefreshTask(AssetFragment.this, true).execute();
                }
            }
        });
        refreshDisplay();
        swipeRefreshLayout.setRefreshing(true);
//        if (AccountsManager.getAccountSize() == 0){
//            AccountsManager.loadAccounts(this.getActivity());
//        }
//        Log.d(TAG, "onCreateView: lifecycleTest" + this.toString());
//        if (AccountsManager.getAccountSize() > 0) {
//            if (!AssetRefreshTask.refreshing) {
//                AccountsManager.setCurAccountIndex(0);
//                refreshDisplay();
//                new AssetRefreshTask(this, true).execute();
//            }
//        } else {
//            Log.d(TAG, "onCreateView: Empty accounts list");
//        }
        return view;
    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getActivity().registerReceiver(accountChangeReceiver, intentFilter);
//    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        recyclerView.getAdapter().notifyDataSetChanged();
        if (!AssetRefreshTask.refreshing) {
            refreshDisplay();
            new AssetRefreshTask(this, false).execute();
        }
    }

    @Override
    public void onDestroy() {
//        getActivity().unregisterReceiver(accountChangeReceiver);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(this.getActivity(), ScanQRActivity.class);
                startActivityForResult(intent, 1);
            }else{
                Toast.makeText(this.getActivity(), R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    MakePaymentActivity.actionStart(this.getActivity(), MakePaymentActivity.Token.ETH, data.getStringExtra("receiver_address"));
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (!swipeRefreshLayout.isRefreshing()) {

            switch (view.getId()) {
                case R.id.asset_menu:
                    drawerLayout.openDrawer(Gravity.LEFT);
                    break;
                case R.id.asset_receive_layout:
                    ReceivePaymentActivity.actionStart(AssetFragment.this.getActivity(),
                            AccountsManager.getCurAccount().getAddress());
                    break;
                case R.id.asset_side_nav_create_account:
                    CreateWalletActivity.actionStart(this.getActivity());
                    break;
                case R.id.asset_eth_layout:
                    if (ethButtons.getVisibility() == View.VISIBLE) {
                        Animation hideAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.hide);
                        float ethButtonsHeight = ethButtons.getHeight();
                        Animation coordinateHideAnimation = new TranslateAnimation(0, 0, ethButtonsHeight, 0);
                        coordinateHideAnimation.setDuration(200);
                        ethButtons.setVisibility(View.GONE);
                        selfCoinContainer.startAnimation(coordinateHideAnimation);
                        ethButtons.startAnimation(hideAnimation);
                    } else {
                        ethButtons.setVisibility(View.VISIBLE);
                        float ethButtonsHeight = ethButtons.getHeight();
                        if (ethButtonsHeight == 0){
//                            float scale = this.getActivity().getResources().getDisplayMetrics().density;
//                            ethButtonsHeight = 48 * scale + 0.5f;
                            ethButtonsHeight = BGAQRCodeUtil.dp2px(this.getActivity(), 48);
                        }
                        Animation coordinateShowAnimation = new TranslateAnimation(0, 0, ethButtonsHeight * -1, 0);
                        coordinateShowAnimation.setDuration(200);
                        selfCoinContainer.startAnimation(coordinateShowAnimation);
                        ethButtons.startAnimation(showAnimation);
                    }
                    break;
                case R.id.asset_selfCoin_layout:
                    if (selfCoinButtons.getVisibility() == View.VISIBLE) {
                        Animation hideAnimation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.hide);
                        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                selfCoinButtons.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        selfCoinButtons.startAnimation(hideAnimation);
                    } else {
                        selfCoinButtons.setVisibility(View.VISIBLE);
                        selfCoinButtons.startAnimation(showAnimation);
                    }
                    break;
                case R.id.asset_eth_make_payment:
                    MakePaymentActivity.actionStart(this.getActivity(), MakePaymentActivity.Token.ETH);
                    break;
                case R.id.asset_eth_receive_payment:
                    ReceivePaymentActivity.actionStart(AssetFragment.this.getActivity(),
                            AccountsManager.getCurAccount().getAddress());
                    break;
                case R.id.asset_selfCoin_make_payment:
                    MakePaymentActivity.actionStart(this.getActivity(), MakePaymentActivity.Token.BLOC);
                    break;
                case R.id.asset_selfCoin_receive_payment:
                    ReceivePaymentActivity.actionStart(AssetFragment.this.getActivity(),
                            AccountsManager.getCurAccount().getAddress());
                    break;
                case R.id.asset_side_nav_scanQR:
                    if (ContextCompat.checkSelfPermission(this.getActivity(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
                    }else{
                        Intent intent = new Intent(this.getActivity(), ScanQRActivity.class);
                        startActivityForResult(intent, 1);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void refreshDisplay() {
        Log.d(TAG, "refreshDisplay: " + this.toString());
        if (swipeRefreshLayout != null) {
            Account curAccount = AccountsManager.getCurAccount();
            usernameView.setText(curAccount.getUsername());
            String address = curAccount.getAddress();
            if (address.length() == 42) {
                String address_brief = address.substring(0, 10) + "..." + address.substring(32, 42);
                addressView.setText(address_brief);
            } else {
                Toast.makeText(this.getActivity(), "The format of address is incorrect", Toast.LENGTH_SHORT).show();
            }

            double realETH = curAccount.getEthereum().doubleValue() * Utility.getEth2cny();
            double realselfCoin = curAccount.getSelfCoin().doubleValue() * 0.05;

            realTotalView.setText(String.format(Locale.CHINA, "%.2f", realETH + realselfCoin));

            ethView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getEthereum().doubleValue()));
            realETHView.setText(String.format(Locale.CHINA, "%.2f", realETH));
            selfCoinView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getSelfCoin()));
            realSelfCoinView.setText(String.format(Locale.CHINA, "%.2f", realselfCoin));

            swipeRefreshLayout.setRefreshing(false);
        }
    }

//    public class AccountChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (recyclerView != null){
//                recyclerView.getAdapter().notifyDataSetChanged();
//            }
//        }
//    }
}
