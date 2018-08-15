package com.example.leo.ethereumwallet.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.activity.TransactionHistoryActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment implements View.OnClickListener {

    private LinearLayout manageWalletButton;
    private LinearLayout transactHistButton;

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        manageWalletButton = view.findViewById(R.id.me_manage_wallet);
        manageWalletButton.setOnClickListener(this);
        transactHistButton = view.findViewById(R.id.me_transaction_history);
        transactHistButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.me_manage_wallet:
                break;
            case R.id.me_transaction_history:
                TransactionHistoryActivity.actionStart(this.getActivity());
                break;
        }
    }
}
