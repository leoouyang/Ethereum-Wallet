package com.example.leo.ethereumwallet.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ethereumwallet.R;

import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

public class ReceivePaymentActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "ReceivePaymentActivity";

    private ClipboardManager myClipboard;
    private ClipData myClip;
    private String address;
    private TextView addressText;
    private ImageView returnButton;
    private ImageView shareButton;
    private ImageView qrcode;
    private Button copyAddressButton;

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

        addressText = findViewById(R.id.receive_payment_full_address);
        addressText.setText(address);
        returnButton = findViewById(R.id.toolbar_return);
        returnButton.setOnClickListener(this);
        shareButton = findViewById(R.id.receive_payment_share);
        shareButton.setOnClickListener(this);
        qrcode = findViewById(R.id.receive_payment_qr_code);
        copyAddressButton = findViewById(R.id.receive_payment_copy_address);
        copyAddressButton.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                Point size = new Point();
                getWindowManager().getDefaultDisplay().getSize(size);
                return QRCodeEncoder.syncEncodeQRCode(address, size.x * 7/11, Color.parseColor("#000000"));
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    qrcode.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(ReceivePaymentActivity.this, R.string.generate_qr_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_return:
                finish();
                break;
            case R.id.receive_payment_copy_address:
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                myClip = ClipData.newPlainText("address", address);
                myClipboard.setPrimaryClip(myClip);
                copyAddressButton.setText(R.string.copied);
                copyAddressButton.setClickable(false);
                break;
            case R.id.receive_payment_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "address");
                shareIntent.putExtra(Intent.EXTRA_TEXT, address);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share"));
                break;
            default:
                break;
        }
    }
}
