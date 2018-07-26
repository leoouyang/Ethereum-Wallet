package com.example.leo.ethereumwallet;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakePaymentDialogFragment extends BottomSheetDialogFragment {


    public MakePaymentDialogFragment() {
        //d Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_payment_dialog, container, false);
        return view;
    }

}
