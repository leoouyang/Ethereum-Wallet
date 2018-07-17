package com.example.leo.ethereumwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ReceivePaymentActivity extends AppCompatActivity {

    private String address;

    public static void actionStart(Context context, String address) {
        Intent intent = new Intent(context, ReceivePaymentActivity.class);
        intent.putExtra("address", address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
    }
}
