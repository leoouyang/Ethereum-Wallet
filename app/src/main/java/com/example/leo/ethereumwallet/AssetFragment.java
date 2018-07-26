package com.example.leo.ethereumwallet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Locale;

//import org.ethereum.crypto.ECKey;


public class AssetFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AssetFragment";

    private Animation showAnimation;
//    private Animation hideAnimation;

    protected DrawerLayout drawerLayout;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected Button menuButton;
    private RecyclerView recyclerView;
    private LinearLayout createAccountLayout;

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

    private boolean firstTime;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstTime = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset, container, false);

        showAnimation = AnimationUtils.loadAnimation(this.getActivity(),R.anim.show);
//        hideAnimation = AnimationUtils.loadAnimation(this.getActivity(),R.anim.hide);

        drawerLayout = view.findViewById(R.id.asset_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        swipeRefreshLayout = view.findViewById(R.id.asset_swipe_refresh);

        menuButton = view.findViewById(R.id.asset_menu);
        menuButton.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.asset_side_nav_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(new SideAccountAdapter(this));
        recyclerView.getAdapter().notifyDataSetChanged();
        createAccountLayout = view.findViewById(R.id.asset_side_nav_create_account);
        createAccountLayout.setOnClickListener(this);

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
//                AssetFragment.this.getActivity().getWindow().setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                new AssetRefreshTask(AssetFragment.this).execute();
//                refreshAccount(curAccountIndex);
            }
        });

        if (AccountsManager.getAccountSize() > 0) {
            AccountsManager.setCurAccountIndex(0);
            refreshDisplay();
            new AssetRefreshTask(this).execute();
        } else {
            Log.d(TAG, "onCreateView: Empty accounts list");
        }
        return view;
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
                            AccountsManager.getCurrentAccount().getAddress());
                    break;
                case R.id.asset_side_nav_create_account:
                    CreateWalletActivity.actionStart(this.getActivity());
                    break;
                case R.id.asset_eth_layout:
                    if (ethButtons.getVisibility() == View.VISIBLE){
                        Animation hideAnimation = AnimationUtils.loadAnimation(this.getActivity(),R.anim.hide);
                        float ethButtonsHeight = ethButtons.getHeight();
                        Animation coordinateHideAnimation = new TranslateAnimation(0,0, ethButtonsHeight, 0);
                        coordinateHideAnimation.setDuration(200);
                        ethButtons.setVisibility(View.GONE);
                        selfCoinContainer.startAnimation(coordinateHideAnimation);
                        ethButtons.startAnimation(hideAnimation);
                    }else {
                        ethButtons.setVisibility(View.VISIBLE);
                        float ethButtonsHeight = ethButtons.getHeight();
                        Animation coordinateShowAnimation = new TranslateAnimation(0,0, ethButtonsHeight*-1, 0);
                        coordinateShowAnimation.setDuration(200);
                        selfCoinContainer.startAnimation(coordinateShowAnimation);
                        ethButtons.startAnimation(showAnimation);
                    }
                    break;
                case R.id.asset_selfCoin_layout:
                    if (selfCoinButtons.getVisibility() == View.VISIBLE){
                        Animation hideAnimation = AnimationUtils.loadAnimation(this.getActivity(),R.anim.hide);
                        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {}

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                selfCoinButtons.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        selfCoinButtons.startAnimation(hideAnimation);
                    }else {
                        selfCoinButtons.setVisibility(View.VISIBLE);
                        selfCoinButtons.startAnimation(showAnimation);
                    }
                    break;
                case R.id.asset_eth_make_payment:
                    MakePaymentActivity.actionStart(this.getActivity(), MakePaymentActivity.Token.ETH);
                    break;
                case R.id.asset_eth_receive_payment:
                    Toast.makeText(this.getActivity(), "receive payment", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.asset_selfCoin_make_payment:
                    MakePaymentActivity.actionStart(this.getActivity(), MakePaymentActivity.Token.BLOC);
                    break;
                case R.id.asset_selfCoin_receive_payment:
                    Toast.makeText(this.getActivity(), "BLOC receive payment", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!firstTime){
            recyclerView.getAdapter().notifyDataSetChanged();
            refreshDisplay();
            new AssetRefreshTask(this).execute();
        }else{
            firstTime = false;
        }
    }

    protected void refreshDisplay() {
        Account curAccount = AccountsManager.getCurrentAccount();
        usernameView.setText(curAccount.getUsername());
        String address = curAccount.getAddress();
        if (address.length() == 42) {
            String address_brief = address.substring(0, 10) + "..." + address.substring(32, 42);
            addressView.setText(address_brief);
        } else {
            Toast.makeText(this.getActivity(), "The format of address is incorrect", Toast.LENGTH_SHORT).show();
        }

        double realETH = curAccount.getEthereum() * Utility.getEth2cny();
        double realselfCoin = curAccount.getSelfCoin() * 0.05;

        realTotalView.setText(String.format(Locale.CHINA, "%.2f", realETH + realselfCoin));

        ethView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getEthereum()));
        realETHView.setText(String.format(Locale.CHINA, "%.2f", realETH));
        selfCoinView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getSelfCoin()));
        realSelfCoinView.setText(String.format(Locale.CHINA, "%.2f", realselfCoin));

        swipeRefreshLayout.setRefreshing(false);
    }
}
