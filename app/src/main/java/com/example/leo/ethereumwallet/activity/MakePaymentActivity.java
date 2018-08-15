package com.example.leo.ethereumwallet.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ethereumwallet.util.AccountsManager;
import com.example.leo.ethereumwallet.fragment.MakePaymentDialogFragment;
import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.util.Utility;
import com.example.leo.ethereumwallet.gson.Account;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MakePaymentActivity extends AppCompatActivity implements View.OnClickListener {
    public enum Token {
        ETH, BLOC
    }

    private static final String TAG = "MakePaymentActivity";
    private Account senderAccount;
    private boolean gasPriceRefreshing;
    private Token token;
    private BigDecimal accountBalance;

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

    public static void actionStart(Context context, Token token) {
        Intent intent = new Intent(context, MakePaymentActivity.class);
        intent.putExtra("token", token);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, Token token, String address) {
        Intent intent = new Intent(context, MakePaymentActivity.class);
        intent.putExtra("token", token);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        senderAccount = AccountsManager.getCurAccount();
        gasPriceRefreshing = false;
        Intent intent = getIntent();
        this.token = (Token) intent.getSerializableExtra("token");

        TextView title = findViewById(R.id.toolbar_title);
        ImageView backButton = findViewById(R.id.toolbar_return);
        backButton.setOnClickListener(this);
        ImageView scanQR = findViewById(R.id.make_payment_scanQR);
        scanQR.setOnClickListener(this);

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

        String address = intent.getStringExtra("address");
        if (address != null) {
            receiverAddressInput.setText(intent.getStringExtra("address"));
        }

        switch (token) {
            case ETH:
                title.setText(R.string.make_ETH_payment);
                accountBalance = senderAccount.getEthereum();
                accountBalanceTextView.setText(String.format(Locale.CHINA, "%.4f", accountBalance));
                gasLimitInput.setText("22000");
                break;
            case BLOC:
                accountBalance = senderAccount.getSelfCoin();
                accountBalanceTextView.setText(String.format(Locale.CHINA, "%.4f", accountBalance));
                title.setText(R.string.make_BLOC_payment);
                gasLimitInput.setText("200000");
                break;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.parseColor("#30000000"));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.make_payment_refresh_gas_price:
                if (!gasPriceRefreshing) {
                    gasPriceRefreshing = true;
                    gasPriceTable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("https://ethgasstation.info/json/ethgasAPI.json")
                                        .build();
                                Response response = client.newCall(request).execute();
                                final String responseData = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObject = new JSONObject(responseData);
                                            lowGasPrice.setText(String.format(Locale.CHINA, "%.2f Gwei", jsonObject.getDouble("safeLow") / 10));
                                            mediumGasPrice.setText(String.format(Locale.CHINA, "%.2f Gwei", jsonObject.getDouble("average") / 10));
                                            highGasPrice.setText(String.format(Locale.CHINA, "%.2f Gwei", jsonObject.getDouble("fast") / 10));
                                            progressBar.setVisibility(View.GONE);
                                            gasPriceTable.setVisibility(View.VISIBLE);
                                            gasPriceRefreshing = false;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(MakePaymentActivity.this, "Fail to parse JSON", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
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
                String address = receiverAddressInput.getText().toString();
                String gasPriceString = gasPriceInput.getText().toString();
                String gasLimitString = gasLimitInput.getText().toString();
                String valueString = amountInput.getText().toString();

                if (address.length() == 0 || gasPriceString.length() == 0 ||
                        gasLimitString.length() == 0 || valueString.length() == 0) {
                    Toast.makeText(this, R.string.empty_field_exists, Toast.LENGTH_SHORT).show();
                    return;
                }else if(gasPriceString.matches("\\.") || valueString.matches("\\.")){
                    Toast.makeText(this, R.string.only_dot_invalid_input, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (gasPriceString.matches("\\.[0-9]+")){
                    gasPriceString = "0"+gasPriceString;
                }
                if (valueString.matches("\\.[0-9]+")){
                    valueString = "0"+valueString;
                }

                BigDecimal gasPrice = new BigDecimal(gasPriceString);
                int gasLimit = Integer.parseInt(gasLimitString);
                BigDecimal value =new BigDecimal(valueString);

                Log.d(TAG, "onClick: gas in ether: " + Math.pow(10, -9) * gasLimit * gasPrice.doubleValue());

                address = Utility.formatAddress(address);
                Log.d(TAG, String.format("onClick: %f", value.add(gasPrice.multiply(new BigDecimal(Math.pow(10, -9) * gasLimit)))));
                if (address == null) {
                    Toast.makeText(this, R.string.address_incorrect_format, Toast.LENGTH_SHORT).show();
                } else if (address.equals(senderAccount.getAddress())) {
                    Toast.makeText(this, R.string.receiver_cannot_be_self, Toast.LENGTH_SHORT).show();
                } else if ( value.add(gasPrice.multiply(new BigDecimal(Math.pow(10, -9) * gasLimit))).compareTo(accountBalance) > 0) {
//                   value  + Math.pow(10, -9) * gasLimit * gasPrice  > accountBalance
                    Toast.makeText(this, R.string.insufficient_fund, Toast.LENGTH_SHORT).show();
                } else {
                    MakePaymentDialogFragment makePaymentDialogFragment = MakePaymentDialogFragment.newInstance(token, address, gasPriceString, gasLimit, valueString);
                    makePaymentDialogFragment.show(getSupportFragmentManager(), "make payment bottom sheet dialog");
                }
//                makePayment();
                break;
            case R.id.toolbar_return:
                finish();
                break;
            case R.id.make_payment_scanQR:
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                }else{
                    Intent intent = new Intent(this, ScanQRActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            default:
                break;
        }
    }

    public void responseHandler(int status, String errorMessage) {
        switch (status) {
            case -2:
                Toast.makeText(MakePaymentActivity.this, "Can't load keystore file", Toast.LENGTH_LONG).show();
                break;
            case -1:
                Toast.makeText(MakePaymentActivity.this, R.string.wrong_password, Toast.LENGTH_LONG).show();
                break;
            case 0:
                Toast.makeText(MakePaymentActivity.this, R.string.transaction_request_success, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case 1:
                Toast.makeText(MakePaymentActivity.this, "请求参数错误：" + errorMessage, Toast.LENGTH_LONG).show();
                break;
            case 2:
                Toast.makeText(MakePaymentActivity.this, R.string.unknown_error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        if (requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(this, ScanQRActivity.class);
                startActivityForResult(intent, 1);
            }else{
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK){
                    receiverAddressInput.setText(data.getStringExtra("receiver_address"));
                }
                break;
        }
    }
}
