package com.example.leo.ethereumwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ImportWalletActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ImportWalletActivity";

    private EditText privateKey;
    private EditText walletName;
    private EditText password;
    private EditText passwordDoublecheck;
    private Button importWalletButton;
    private ImageView backButton;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ImportWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);

        privateKey = findViewById(R.id.import_wallet_private_key);
        walletName = findViewById(R.id.import_wallet_name);
        password = findViewById(R.id.import_password);
        passwordDoublecheck = findViewById(R.id.import_password_doublecheck);
        importWalletButton = findViewById(R.id.import_wallet_button);
        importWalletButton.setOnClickListener(this);

        backButton = findViewById(R.id.toolbar_return);
        backButton.setOnClickListener(this);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(R.string.import_wallet);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.import_wallet_button:
                String privateKeyInput = privateKey.getText().toString();
                String walletNameInput = walletName.getText().toString();
                String passwordInput = password.getText().toString();
                String passwordInput2 = passwordDoublecheck.getText().toString();

                if (password.length() == 0) {
                    Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
                } else if (!passwordInput.equals(passwordInput2)) {
                    Toast.makeText(this, R.string.password_inconsistent, Toast.LENGTH_SHORT).show();
                } else if (walletNameInput.length() > 12 || walletNameInput.length() < 1) {
                    Toast.makeText(this, R.string.wallet_name_too_long, Toast.LENGTH_SHORT).show();
                } else if (AccountsManager.checkNameExisted(walletNameInput)) {
                    Toast.makeText(this, R.string.wallet_name_existed, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "onClick: ");
                    new CreateWalletTask(this, walletNameInput, passwordInput).execute(privateKeyInput);
                }
                break;
            case R.id.toolbar_return:
                this.finish();
                break;
            default:
                break;
        }
    }
}
