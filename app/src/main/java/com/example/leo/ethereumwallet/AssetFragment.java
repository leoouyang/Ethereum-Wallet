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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

//import org.ethereum.crypto.ECKey;


public class AssetFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AssetFragment";

    protected DrawerLayout drawerLayout;
    protected SwipeRefreshLayout swipeRefreshLayout;

    protected Button menuButton;
    private RecyclerView recyclerView;
    private LinearLayout createAccountLayout;

    private TextView usernameView;
    private TextView addressView;
    private LinearLayout receiveLayout;
    private TextView realTotalView;

    private TextView ETHView;
    private TextView realETHView;
    private TextView SelfCoinView;
    private TextView realSelfCoinView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset, container, false);

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

        ETHView = view.findViewById(R.id.asset_eth_amount);
        realETHView = view.findViewById(R.id.asset_eth_amount_real);
        SelfCoinView = view.findViewById(R.id.asset_selfCoin_amount);
        realSelfCoinView = view.findViewById(R.id.asset_selfCoin_amount_real);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                AssetFragment.this.getActivity().getWindow().setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                new RefreshTask(AssetFragment.this).execute();
//                refreshAccount(curAccountIndex);
            }
        });

        if (AccountManager.getAccountSize() > 0) {
            AccountManager.setCurAccountIndex(0);
            refreshDisplay();
            new RefreshTask(this).execute();
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
                            AccountManager.getCurrentAccount().getAddress());
                    break;
                case R.id.asset_side_nav_create_account:
                    CreateWalletActivity.actionStart(this.getActivity());
                    break;
//                try{
//                    String fileName = this.getActivity().getFilesDir().getAbsolutePath() + "/" + AccountManager.accounts.get(curAccountIndex).getUsername();
//                    Log.d(TAG, "onClick: " + fileName);
//                    File outDir = new File(fileName);
//                    if (!outDir.exists()){
//                        outDir.mkdir();
//                    }
//                    ECKeyPair keyPair = ECKeyPair.create(new BigInteger(AccountManager.accounts.get(curAccountIndex).getPrivateKey(),16));
//                    WalletUtils.generateWalletFile("ouyang.823", keyPair, outDir, true);
//
////                    WalletUtils.generateFullNewWalletFile("123456", outDir);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                default:
                    break;
            }
        }
    }

//    protected void refreshAccount(final int i){
//        drawerLayout.closeDrawer(Gravity.LEFT);
//        swipeRefreshLayout.setRefreshing(true);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                try{
////
////                    Thread.sleep(1000);
////                }catch (Exception e){
////                    e.printStackTrace();
////                }
//                Account curAccount = AccountManager.accounts.get(i);
//                double ethAmount = Utility.getEtherBalance(curAccount.getAddress());
//                if (ethAmount >= 0) {
//                    curAccount.setEthereum(ethAmount);
//                    refreshDisplay(i);
//                } else {
//                    AssetFragment.this.getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(AssetFragment.this.getActivity(), "Failed to Refresh", Toast.LENGTH_SHORT).show();
//                            Log.d(TAG, "refreshAccount: Failed");
//                        }
//                    });
//                }
//            }
//        }).start();
//    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    protected void refreshDisplay() {
        Account curAccount = AccountManager.getCurrentAccount();
        usernameView.setText(curAccount.getUsername());
        String address = curAccount.getAddress();
        if (address.length() == 42) {
            String address_brief = address.substring(0, 10) + "..." + address.substring(32, 42);
            addressView.setText(address_brief);
        } else {
            Toast.makeText(this.getActivity(), "The format of address is incorrect", Toast.LENGTH_SHORT).show();
        }

        double realETH = curAccount.getEthereum() * Utility.eth2cny;
        double realselfCoin = curAccount.getSelfCoin() * 0.05;

        realTotalView.setText(String.format(Locale.CHINA, "%.2f", realETH + realselfCoin));

        ETHView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getEthereum()));
        realETHView.setText(String.format(Locale.CHINA, "%.2f", realETH));
        SelfCoinView.setText(String.format(Locale.CHINA, "%.2f", curAccount.getSelfCoin()));
        realSelfCoinView.setText(String.format(Locale.CHINA, "%.2f", realselfCoin));

        swipeRefreshLayout.setRefreshing(false);
    }
}
