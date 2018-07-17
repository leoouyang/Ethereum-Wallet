package com.example.leo.ethereumwallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CreateWalletActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CreateWalletActivity";

    private EditText walletName;
    private EditText password;
    private EditText passwordDoublecheck;
    private Button createWalletButton;
    private ImageView backButton;
    private TextView create2ImportButton;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CreateWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        walletName = findViewById(R.id.create_wallet_name);
        password = findViewById(R.id.create_password);
        passwordDoublecheck = findViewById(R.id.create_password_doublecheck);
        createWalletButton = findViewById(R.id.create_wallet_button);
        create2ImportButton = findViewById(R.id.create_to_import_button);
        backButton = findViewById(R.id.create_wallet_return);

        backButton.setOnClickListener(this);
        createWalletButton.setOnClickListener(this);
        create2ImportButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_wallet_button:
                String walletNameInput = walletName.getText().toString();
                String passwordInput = password.getText().toString();
                String passwordInput2 = passwordDoublecheck.getText().toString();

                if (password.length() == 0) {
                    Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
                } else if (!passwordInput.equals(passwordInput2)) {
                    Toast.makeText(this, R.string.password_inconsistent, Toast.LENGTH_SHORT).show();
                } else if (walletNameInput.length() > 12 || walletNameInput.length() < 1) {
                    Toast.makeText(this, R.string.wallet_name_too_long, Toast.LENGTH_SHORT).show();
                } else if (AccountManager.checkNameExisted(walletNameInput)) {
                    Toast.makeText(this, R.string.wallet_name_existed, Toast.LENGTH_SHORT).show();
                } else {
                    new CreateWalletTask(this, walletNameInput, passwordInput).execute((String) null);
//                    new AsyncTask<Void, Void, String>(){
//                        Activity activity;
//                        AlertDialog dialog;
//                        {
//                            activity = CreateWalletActivity.this;
//                        }
//
//                        @Override
//                        protected void onPreExecute() {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                            builder.setMessage("生成中...");
//                            builder.setCancelable(false);
//                            dialog = builder.create();
//                            dialog.show();
//                        }
//
//                        @Override
//                        protected String doInBackground(Void... voids) {
//                            return Utility.createKeyStore(activity, passwordInput, null);
//                        }
//
//                        @Override
//                        protected void onPostExecute(String createdFileName) {
//                            if (createdFileName != null){
//                                try{
//                                    String filepath = Utility.getWalletdirectory(activity) + "/" + createdFileName;
//                                    Credentials credentials = WalletUtils.loadCredentials(passwordInput, filepath);
//                                    Account newAccount = new Account();
//                                    newAccount.setAddress(credentials.getAddress());
//                                    newAccount.setUsername(walletNameInput);
//                                    newAccount.setKeystore(createdFileName);
////                                    newAccount.setPrivateKey(credentials.getEcKeyPair().getPrivateKey().toString(16));
//                                    AccountManager.accounts.add(newAccount);
//                                    AccountManager.saveAccounts(activity);
//                                    dialog.dismiss();
//                                    Log.d(TAG, "onClick: " + newAccount.toString());
//                                    activity.finish();
//                                }catch (Exception e){
//                                    dialog.dismiss();
//                                    e.printStackTrace();
//                                }
//                            }else{
//                                dialog.dismiss();
//                                Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
//                            }
//                            Log.d(TAG, createdFileName);
//                        }
//                    }.execute();
                }
                break;
            case R.id.create_wallet_return:
                this.finish();
                break;
            case R.id.create_to_import_button:
                ImportWalletActivity.actionStart(this);
                break;
            default:
                break;
        }
    }
}
