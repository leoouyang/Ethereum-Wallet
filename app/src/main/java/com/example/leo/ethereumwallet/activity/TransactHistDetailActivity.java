package com.example.leo.ethereumwallet.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.gson.Transaction;
import com.example.leo.ethereumwallet.util.AccountsManager;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactHistDetailActivity extends AppCompatActivity {
    private Transaction curTransaction;

    private ImageView returnButton;
    private CircleImageView icon;
    private TextView amount;
    private TextView unit;
    private TextView sender;
    private ImageView senderSelf;
    private TextView receiver;
    private ImageView receiverSelf;
    private TextView minerFee;
    private TextView transactionID;
    private TextView blockNum;
    private TextView transactTime;


    public static void actionStart(Context context, Transaction transaction){
        Intent intent = new Intent(context, TransactHistDetailActivity.class);
        intent.putExtra("transaction", transaction);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transact_hist_detail);

        Intent intent = getIntent();
        curTransaction = (Transaction) intent.getSerializableExtra("transaction");

        BigDecimal gasPrice = Convert.fromWei(curTransaction.getGasPrice(), Convert.Unit.ETHER);
        BigDecimal gasUsed = new BigDecimal(curTransaction.getGasUsed());
        BigDecimal totalFee = gasPrice.multiply(gasUsed);

        returnButton = findViewById(R.id.toolbar_return);
        icon = findViewById(R.id.transact_hist_detail_icon);
        amount = findViewById(R.id.transact_hist_detail_amount);
        unit = findViewById(R.id.transact_hist_detail_unit);
        sender = findViewById(R.id.transact_hist_detail_sender);
        senderSelf = findViewById(R.id.transact_hist_detail_sender_self);
        receiver = findViewById(R.id.transact_hist_detail_receiver);
        receiverSelf = findViewById(R.id.transact_hist_detail_receiver_self);
        minerFee = findViewById(R.id.transact_hist_detail_minerFee);
        transactionID = findViewById(R.id.transact_hist_detail_transactionID);
        blockNum = findViewById(R.id.transact_hist_detail_blockNum);
        transactTime = findViewById(R.id.transact_hist_detail_transaction_time);

        if (AccountsManager.getCurAccount().getAddress().equals(curTransaction.getFromAddress())){
            senderSelf.setVisibility(View.VISIBLE);
        }else{
            receiverSelf.setVisibility(View.VISIBLE);
        }
        switch (curTransaction.getTransactionType()){
            case BLOC:
                icon.setImageResource(R.drawable.ic_bloc);
                unit.setText(R.string.selfcoin);
                break;
            case ETH:
                icon.setImageResource(R.drawable.js_images_asset_eth);
                unit.setText(R.string.eth);
                break;
            case INTERNAL:
                icon.setImageResource(R.drawable.js_images_asset_eth);
                unit.setText(R.string.eth);
                break;
        }

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransactHistDetailActivity.this.finish();
            }
        });

        amount.setText(String.format(Locale.CHINA, "%.4f", Convert.fromWei(curTransaction.getValue(), Convert.Unit.ETHER)));
        sender.setText(curTransaction.getFromAddress());
        receiver.setText(curTransaction.getToAddress());
        minerFee.setText(String.format(Locale.CHINA, "%f", totalFee));
        transactionID.setText(curTransaction.getTransactionID());
        blockNum.setText(curTransaction.getBlockNumber());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        transactTime.setText(simpleDateFormat.format(new Date(Long.parseLong(curTransaction.getTimeStamp() + "000"))));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
