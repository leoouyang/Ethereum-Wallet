package com.example.leo.ethereumwallet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MakePaymentActivity extends AppCompatActivity implements View.OnClickListener{
    public enum Token {
        ETH, BLOC
    }

    private static final String TAG = "MakePaymentActivity";
    private Account senderAccount;
    private boolean gasPriceRefreshing;
    private Token token;
    private double accountBalance;

    private EditText receiverAddressInput;
    private EditText amountInput;
    private TextView accountBalanceTextView;
    private EditText gasPriceInput;
    private Button refreshGasPrice;
    private EditText gasLimitInput;
    private Button makePaymentButton;

    private ProgressBar progressBar;
    private TableLayout gasPriceTable;
    private TextView lowGasPrice;
    private TextView mediumGasPrice;
    private TextView highGasPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        senderAccount = AccountsManager.getCurrentAccount();
        gasPriceRefreshing = false;
        Intent intent = getIntent();
        this.token = (Token)intent.getSerializableExtra("token");

        TextView title = findViewById(R.id.make_payment_title);
        ImageView backButton = findViewById(R.id.make_payment_return);
        backButton.setOnClickListener(this);

        receiverAddressInput = findViewById(R.id.make_payment_address);
        amountInput = findViewById(R.id.make_payment_amount);
        accountBalanceTextView = findViewById(R.id.make_payment_sender_balance);
        gasPriceInput = findViewById(R.id.make_payment_gas_price);
        refreshGasPrice = findViewById(R.id.make_payment_refresh_gas_price);
        refreshGasPrice.setOnClickListener(this);
        gasLimitInput = findViewById(R.id.make_payment_gas_limit);
        makePaymentButton = findViewById(R.id.make_payment_button);
        makePaymentButton.setOnClickListener(this);

        progressBar = findViewById(R.id.make_payment_progressBar);
        gasPriceTable = findViewById(R.id.make_payment_gas_price_table);
        lowGasPrice = findViewById(R.id.make_payment_low_gas_price);
        mediumGasPrice = findViewById(R.id.make_payment_medium_gas_price);
        highGasPrice = findViewById(R.id.make_payment_high_gas_price);


        switch (token){
            case ETH:
                title.setText(R.string.make_ETH_payment);
                accountBalance = senderAccount.getEthereum();
                accountBalanceTextView.setText(String.format("%.4f", accountBalance));
                gasLimitInput.setText("21000");
                break;
            case BLOC:
                accountBalance = senderAccount.getSelfCoin();
                accountBalanceTextView.setText(String.format("%.4f", accountBalance));
                title.setText(R.string.make_BLOC_payment);
                gasLimitInput.setText("200000");
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.make_payment_refresh_gas_price:
                if (!gasPriceRefreshing){
                    gasPriceRefreshing = true;
                    gasPriceTable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("https://ethgasstation.info/json/ethgasAPI.json")
                                        .build();
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            JSONObject jsonObject = new JSONObject(responseData);
                                            lowGasPrice.setText(String.format(Locale.CHINA,"%.2f Gwei", jsonObject.getDouble("safeLow")/10));
                                            mediumGasPrice.setText(String.format(Locale.CHINA,"%.2f Gwei", jsonObject.getDouble("average")/10));
                                            highGasPrice.setText(String.format(Locale.CHINA,"%.2f Gwei", jsonObject.getDouble("fast")/10));
                                            progressBar.setVisibility(View.GONE);
                                            gasPriceTable.setVisibility(View.VISIBLE);
                                            gasPriceRefreshing = false;
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                            Toast.makeText(MakePaymentActivity.this, "Fail to parse JSON", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }catch (Exception e){
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(MakePaymentActivity.this, "Failed to request gas price", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }).start();
                }
                break;
            case R.id.make_payment_button:
                MakePaymentDialogFragment makePaymentDialogFragment = new MakePaymentDialogFragment();
                makePaymentDialogFragment.show(getSupportFragmentManager(), "make payment bottom sheet dialog");
                Dialog dialog = makePaymentDialogFragment.getDialog();
                dialog.findViewById(R.id.make_payment_dialog_return).setOnClickListener(this);
//                makePayment();
                break;
            case R.id.make_payment_return:
                finish();
            case R.id.make_payment_dialog_return:
                Toast.makeText(this,"wieofn", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private void makePayment(){
        gasPriceTable.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final String address = receiverAddressInput.getText().toString();
        final String gasPriceString = gasPriceInput.getText().toString();
        final String gasLimitString = gasLimitInput.getText().toString();
        final String valueString = amountInput.getText().toString();

        if (address.length() == 0 || gasPriceString.length() == 0 ||
                gasLimitString.length() == 0 || valueString.length() == 0){
            Toast.makeText(this, R.string.empty_field_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        final double gasPrice = Double.parseDouble(gasPriceInput.getText().toString());
        final int gasLimit = Integer.parseInt(gasLimitInput.getText().toString());
        final double value = Double.parseDouble(amountInput.getText().toString());
        Log.d(TAG, "onClick: gas in ether: " + Math.pow(10, -9) * gasLimit * gasPrice);
        if (!Utility.checkAddressFormat(address)){
            Toast.makeText(this, R.string.address_incorrect_format, Toast.LENGTH_SHORT).show();
        }else if(address.equals(senderAccount.getAddress())){
            Toast.makeText(this, R.string.receiver_cannot_be_self, Toast.LENGTH_SHORT).show();
        }else if(value > accountBalance + Math.pow(10, -9) * gasLimit * gasPrice){
            Toast.makeText(this, R.string.insufficient_fund, Toast.LENGTH_SHORT).show();
        }else{
            new AsyncTask<Void, Void, Integer>() {
                boolean hasError = false;
                String errorMessage;

                @Override
                protected Integer doInBackground(Void... voids) {
                    int status = 2;
                    String keystoreLocation = Utility.getWalletdirectory(MakePaymentActivity.this) + "/" + senderAccount.getKeystore();
                    try {
                        Credentials credentials = WalletUtils.loadCredentials("ouyang.823", keystoreLocation);
                        Log.d(TAG, "doInBackground: " + token);
                        switch (token) {
                            case ETH:
                                EthSendTransaction ethSendTransaction = Utility.makeETHTransaction(credentials, address, gasPrice, gasLimit, value);
                                if (ethSendTransaction == null) {
                                    status = 2;
                                } else if (ethSendTransaction.hasError()) {
                                    hasError = true;
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
                                }catch (Exception e){
                                    status = 2;
                                }
                                break;
                            default:
                                status = 0;
                                break;
                        }
                    } catch (CipherException e) {
                        e.printStackTrace();
                        return -1;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return -2;
                    }
                    return status;
                }

                @Override
                protected void onPostExecute(Integer status) {
                    progressBar.setVisibility(View.GONE);
                    switch (status) {
                        case -2:
                            Toast.makeText(MakePaymentActivity.this, "Can't load keystore file", Toast.LENGTH_SHORT).show();
                            break;
                        case -1:
                            Toast.makeText(MakePaymentActivity.this, "pass wrong", Toast.LENGTH_SHORT).show();
                            break;
                        case 0:
                            Toast.makeText(MakePaymentActivity.this, "success", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(MakePaymentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MakePaymentActivity.this, "Fail to send request", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }.execute();
        }
    }
    public static void actionStart(Context context, Token token){
        Intent intent = new Intent(context, MakePaymentActivity.class);
        intent.putExtra("token", token);
        context.startActivity(intent);
    }
}
