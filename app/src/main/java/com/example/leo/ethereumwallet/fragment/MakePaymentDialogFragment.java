package com.example.leo.ethereumwallet.fragment;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.util.Utility;
import com.example.leo.ethereumwallet.activity.MakePaymentActivity;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.Locale;


public class MakePaymentDialogFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final String TAG = "MakePaymentDialogFragme";

    private MakePaymentActivity.Token token;
    private String address;
    private String gasPrice;
    private int gasLimit;
    private String value;
    private boolean inProgress;

    private EditText passwordText;
    private LinearLayout mainContainer;
    private LinearLayout progressBar;

    public MakePaymentDialogFragment() {
        //d Required empty public constructor
    }

    public static MakePaymentDialogFragment newInstance(MakePaymentActivity.Token token, String address, String gasPrice, int gasLimit, String value) {
        MakePaymentDialogFragment makePaymentDialogFragment = new MakePaymentDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable("token", token);
        args.putString("address", address);
        args.putString("gasPrice", gasPrice);
        args.putInt("gasLimit", gasLimit);
        args.putString("value", value);
        makePaymentDialogFragment.setArguments(args);

        return makePaymentDialogFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        token = (MakePaymentActivity.Token) getArguments().getSerializable("token");
        address = getArguments().getString("address");
        gasPrice = getArguments().getString("gasPrice");
        gasLimit = getArguments().getInt("gasLimit");
        value = getArguments().getString("value");

        inProgress = false;
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_payment_dialog, container, false);

        mainContainer = view.findViewById(R.id.make_payment_dialog_main);
        view.findViewById(R.id.toolbar_return).setOnClickListener(this);
        TextView title = view.findViewById(R.id.toolbar_title);
        title.setText(R.string.payment_detail);

//
//        TextView transactionType = view.findViewById(R.id.make_payment_dialog_token);
//        switch (token){
//            case ETH:
//                transactionType.setText(R.string.make_ETH_payment);
//                break;
//            case BLOC:
//                transactionType.setText(R.string.make_BLOC_payment);
//                break;
//        }
        TextView toAddress = view.findViewById(R.id.make_payment_dialog_toAddress);
        toAddress.setText(address);
        TextView fromAddress = view.findViewById(R.id.make_payment_dialog_fromAddress);
        fromAddress.setText(AccountsManager.getCurAccount().getAddress());
        TextView minerFee = view.findViewById(R.id.make_payment_dialog_minerFee);
        minerFee.setText(String.format(Locale.CHINA, "%f ETH = %s (gas price) * %d (gas limit)", gasLimit * Double.parseDouble(gasPrice) * Math.pow(10, -9), gasPrice, gasLimit));
        TextView valueText = view.findViewById(R.id.make_payment_dialog_value);
        switch (token) {
            case ETH:
                valueText.setText(String.format(Locale.CHINA, "%s ETH", value));
                break;
            case BLOC:
                valueText.setText(String.format(Locale.CHINA, "%s BLOC", value));
                break;
        }
//        valueText.setText(String.format(Locale.CHINA, "%f",value));
        passwordText = view.findViewById(R.id.make_payment_dialog_password);
        view.findViewById(R.id.make_payment_dialog_confirm).setOnClickListener(this);
        progressBar = view.findViewById(R.id.make_payment_dialog_progressBar);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_return:
                if (!inProgress) {
                    dismiss();
                }
                break;
            case R.id.make_payment_dialog_confirm:
                String password = passwordText.getText().toString();
                if (password.length() > 0) {
                    makePayment(password);
                } else {
                    Toast.makeText(this.getActivity(), R.string.empty_password, Toast.LENGTH_SHORT).show();
                }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void makePayment(final String password) {
        new AsyncTask<Void, Void, Integer>() {
            String errorMessage;

            @Override
            protected void onPreExecute() {
                inProgress = true;
                mainContainer.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Integer doInBackground(Void... voids) {
                int status = 2;
                String keystoreLocation = Utility.getWalletdirectory(getActivity()) + "/" + AccountsManager.getCurAccount().getKeystore();
                try {
                    Credentials credentials = WalletUtils.loadCredentials(password, keystoreLocation);
                    Log.d(TAG, "doInBackground: " + token);
                    switch (token) {
                        case ETH:
                            EthSendTransaction ethSendTransaction = Utility.makeETHTransaction(credentials, address, gasPrice, gasLimit, value);
                            if (ethSendTransaction == null) {
                                status = 2;
                            } else if (ethSendTransaction.hasError()) {
                                errorMessage = ethSendTransaction.getError().getMessage();
                                status = 1;
                            } else {
                                status = 0;
                            }
                            break;
                        case BLOC:
                            try {
                                TransactionReceipt receipt = Utility.makeBLOCTransaction(credentials, address, gasPrice, gasLimit, value);
                                status = 0;
                            } catch (RuntimeException e) {
                                errorMessage = e.getMessage().split(":")[1];
                                status = 1;
                            } catch (Exception e) {
                                status = 2;
                            }
                            break;
                        default:
                            status = 0;
                            break;
                    }
                } catch (CipherException e) {
//                    e.printStackTrace();
                    return -1;
                } catch (IOException e) {
                    e.printStackTrace();
                    return -2;
                }
                return status;
            }

            @Override
            protected void onPostExecute(Integer status) {
//                progressBar.setVisibility(View.GONE);
                mainContainer.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                ((MakePaymentActivity) getActivity()).responseHandler(status, errorMessage);
                inProgress = false;
                dismiss();
            }
        }.execute();
    }
}
