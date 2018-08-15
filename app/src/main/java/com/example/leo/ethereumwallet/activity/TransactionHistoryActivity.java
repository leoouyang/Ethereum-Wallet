package com.example.leo.ethereumwallet.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.adapter.TransactHistAdapter;
import com.example.leo.ethereumwallet.adapter.TransactHistMenuAdapter;
import com.example.leo.ethereumwallet.asyncTask.GetTransactHistTask;
import com.example.leo.ethereumwallet.gson.Transaction;
import com.example.leo.ethereumwallet.util.AccountsManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "TransactionHistoryActiv";
    private static final Transaction.TransactionType[] transactionTypes = {
            Transaction.TransactionType.ETH,
            Transaction.TransactionType.BLOC,
            Transaction.TransactionType.INTERNAL};

    private TransactHistPagerAdapter transactHistPagerAdapter;

    public DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private ImageView returnButton;
    private TextView title;
    private ImageView menuButton;
    private ViewPager viewPager;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, TransactionHistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        drawerLayout = findViewById(R.id.transact_hist_drawerLayout);
        recyclerView = findViewById(R.id.transact_hist_menu_accounts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new TransactHistMenuAdapter(this));

        returnButton = findViewById(R.id.toolbar_return);
        returnButton.setOnClickListener(this);
        title = findViewById(R.id.toolbar_title);
        title.setText(R.string.transaction_history);
        menuButton = findViewById(R.id.transact_hist_menu_button);
        menuButton.setOnClickListener(this);

        transactHistPagerAdapter = new TransactHistPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.transact_hist_pager);
        viewPager.setAdapter(transactHistPagerAdapter);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_return:
                finish();
                break;
            case R.id.transact_hist_menu_button:
                drawerLayout.openDrawer(Gravity.RIGHT);
                break;
            default:
                break;
        }
    }

    public void changeAccount(int index){
        AccountsManager.setCurAccountIndex(index);
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment f: fragments){
            if (f instanceof TransactHistFragment){
                ((TransactHistFragment)f).changeAddress();
            }
        }
    }

    public static class TransactHistPagerAdapter extends FragmentPagerAdapter{

        public TransactHistPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: position" + position);
            return TransactHistFragment.newInstance(transactionTypes[position]);
        }

        @Override
        public int getCount() {
            return transactionTypes.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return transactionTypes[position].toString();
        }
    }



    public static class TransactHistFragment extends Fragment{
        public volatile List<Transaction> transactionList;
        public volatile int latestBlock;
        public volatile int nextPage;

        public SwipeRefreshLayout refreshLayout;
        Transaction.TransactionType transactionType;
        public RecyclerView recyclerView;

        private boolean firstTime;
        public boolean replacing = false;
        public boolean loadingMore = false;

        private String curAddress;

        public static TransactHistFragment newInstance(Transaction.TransactionType transactionType) {

            Bundle args = new Bundle();
            args.putSerializable("transactionType", transactionType);

            TransactHistFragment fragment = new TransactHistFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            transactionType = (Transaction.TransactionType) args.getSerializable("transactionType");
            transactionList = new ArrayList<>();

//            firstTime = true;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Log.d(TAG, "onCreateView: view created " + transactionType.toString());
            View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);
            recyclerView = view.findViewById(R.id.transact_hist_recycler_view);

            refreshLayout = view.findViewById(R.id.transact_hist_swipe_refresh);

            final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
            recyclerView.setLayoutManager(layoutManager);
            TransactHistAdapter adapter = new TransactHistAdapter(transactionList);
            recyclerView.setAdapter(adapter);

            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!replacing){
                        new GetTransactHistTask(TransactHistFragment.this, transactionType, AccountsManager.
                                getCurAccount().getAddress(),GetTransactHistTask.MODE.MODE_PREPEND).execute();
                    }

                }
            });

            if (!AccountsManager.getCurAccount().getAddress().equals(curAddress)){
                curAddress = AccountsManager.getCurAccount().getAddress();
                latestBlock = 0;
                nextPage = 1;
                new GetTransactHistTask(this, transactionType, AccountsManager.
                        getCurAccount().getAddress(),GetTransactHistTask.MODE.MODE_REPLACE).execute();
            }

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int total = layoutManager.getItemCount();
                    int curLast = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (curLast >= total - 2){
                        if (!loadingMore && !replacing){
                            if (!transactionList.get(transactionList.size()-1).getTransactionID().equals(Transaction.END)) {
                                recyclerView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        new GetTransactHistTask(TransactHistFragment.this, transactionType,
                                                AccountsManager.getCurAccount().getAddress(), GetTransactHistTask.MODE.MODE_APPEND).execute();

                                    }
                                });
                            }
                        }
                    }

                }
            });

//            if (firstTime){
//                refreshLayout.setRefreshing(true);
//                firstTime = false;
//            }
            return view;
        }

        public void changeAddress(){
            curAddress = AccountsManager.getCurAccount().getAddress();
            new GetTransactHistTask(this, transactionType, AccountsManager.
                    getCurAccount().getAddress(),GetTransactHistTask.MODE.MODE_REPLACE).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
