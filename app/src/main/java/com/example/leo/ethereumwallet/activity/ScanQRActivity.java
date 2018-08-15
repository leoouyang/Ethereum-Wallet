package com.example.leo.ethereumwallet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leo.ethereumwallet.R;
import com.example.leo.ethereumwallet.util.Utility;

import cn.bingoogolapple.qrcode.core.BarcodeType;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanQRActivity extends AppCompatActivity implements QRCodeView.Delegate, View.OnClickListener{

    private static final String TAG = "ScanQRActivity";
    private ZXingView zXingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        findViewById(R.id.toolbar_return).setOnClickListener(this);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.scanQR);

        zXingView = findViewById(R.id.zxingview);
        zXingView.setType(BarcodeType.TWO_DIMENSION, null);
        zXingView.setDelegate(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.parseColor("#30000000"));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        zXingView.startCamera();
        zXingView.startSpotAndShowRect();
    }

    @Override
    protected void onPause() {
        zXingView.stopCamera();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        zXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.toolbar_return:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        String address = Utility.formatAddress(result);
        if (address == null){
            Toast.makeText(this, R.string.address_incorrect_format, Toast.LENGTH_SHORT).show();
            zXingView.startSpotAndShowRect();
        }else {
            Intent intent = new Intent();
            intent.putExtra("receiver_address", address);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.d(TAG, "onScanQRCodeOpenCameraError: ");
    }

//    public static void actionStart(Activity activity){
//        Intent intent = new Intent(activity, ScanQRActivity.class);
//        activity.startActivityForResult(intent, 1);
//    }
}
