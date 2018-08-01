package com.example.leo.ethereumwallet;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PriceFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "PriceFragment";
    private float prev_eth2usd;
    private float prev_eth2cny;
    private IntentFilter intentFilter;
    private PriceRefreshReceiver priceRefreshReceiver;

    private SwipeRefreshLayout refreshLayout;
    private TextView eth2usdTextView;
    private TextView eth2cnyTextView;
    private ImageView ethStatusImage;

    public PriceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prev_eth2cny = Utility.getEth2cny();
        prev_eth2usd = Utility.getEth2usd();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);

        refreshLayout = view.findViewById(R.id.price_swipe_refresh);
        eth2usdTextView = view.findViewById(R.id.price_eth_usd);
        eth2cnyTextView = view.findViewById(R.id.price_eth_cny);
        ethStatusImage = view.findViewById(R.id.price_eth_status_image);

        refreshLayout.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Utility.REFRESH_PRICE_SIGNAL);
        priceRefreshReceiver = new PriceRefreshReceiver();
        this.getActivity().registerReceiver(priceRefreshReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        this.getActivity().unregisterReceiver(priceRefreshReceiver);
    }

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
////        if (!hidden){
////            Log.d(TAG, "onHiddenChanged: show");
//////            refreshDisplay();
////        }
//    }

    private void refreshDisplay() {
        eth2cnyTextView.setText(String.valueOf(Utility.getEth2cny()));
        eth2usdTextView.setText(String.valueOf(Utility.getEth2usd()));
        if (Utility.getEth2usd() > prev_eth2usd) {
            ethStatusImage.setImageResource(R.drawable.ic_arrow_up);
        } else if (Utility.getEth2usd() < prev_eth2usd) {
            ethStatusImage.setImageResource(R.drawable.ic_arrow_down);
        } else {
            ethStatusImage.setImageResource(R.drawable.ic_equal);
        }
        prev_eth2usd = Utility.getEth2usd();
        prev_eth2cny = Utility.getEth2cny();
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Utility.getEtherExchangeRate(PriceFragment.this.getActivity());
            }
        }).start();
//        new AsyncTask<Void,Void,Void>(){
//            @Override
//            protected Void doInBackground(Void... voids) {
//                Utility.getEtherExchangeRate();
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                refreshDisplay();
//                refreshLayout.setRefreshing(false);
//            }
//        }.execute();
    }

    class PriceRefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDisplay();
            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
        }
    }
}
