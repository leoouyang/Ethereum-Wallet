package com.example.leo.ethereumwallet;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {
    private static final String INFURA_ADDRESS = "https://ropsten.infura.io/";
    private static final String INFURA_TOKEN = "LaSYLIXhaLNU6l2t5zXZ";
    private static final BigDecimal ETHER2WEI = new BigDecimal(Math.pow(10, 18));
    private static final String TAG = "Utility";
    private static final Web3j web3 = Web3jFactory.build(new HttpService(INFURA_ADDRESS + INFURA_TOKEN));
    //todo change node to our own node
    protected static double eth2usd = 0;
    protected static double eth2cny = 0;

    public static double getEtherBalance(String address) {

        try {
            Log.d(TAG, "getEtherBalance: " + address);
            EthGetBalance ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger weiBalance = ethGetBalance.getBalance();
            BigDecimal etherBalance = new BigDecimal(weiBalance).divide(ETHER2WEI, RoundingMode.HALF_UP);
            Log.d(TAG, "getEtherBalance: " + etherBalance.doubleValue());
            return etherBalance.doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1.0;
    }

    public static void getEtherExchangeRate() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD,CNY")
                    .build();
            Response response = client.newCall(request).execute();
            String reseponseData = response.body().string();
            JSONObject jsonObject = new JSONObject(reseponseData);
            eth2usd = jsonObject.getDouble("USD");
            eth2cny = jsonObject.getDouble("CNY");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getWalletdirectory(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/keystore";
    }

    public static String createKeyStore(Context context, String password, String privateKey) {
        try {
            String fileName = Utility.getWalletdirectory(context);
            File outDir = new File(fileName);
            if (!outDir.exists()) {
                outDir.mkdir();
            }
            String createdfileName;
            if (privateKey == null) {
                createdfileName = WalletUtils.generateFullNewWalletFile(password, outDir);
            } else {
                ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
                createdfileName = WalletUtils.generateWalletFile(password, keyPair, outDir, true);
            }
            return createdfileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
