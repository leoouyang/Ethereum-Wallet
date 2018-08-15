package com.example.leo.ethereumwallet.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.leo.ethereumwallet.asyncTask.GetTransactHistTask;
import com.example.leo.ethereumwallet.gson.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utility {
    public static final String REFRESH_PRICE_SIGNAL = "com.example.leo.ethereumwallet.PRICE_REFRESHED";

    private static final String INFURA_ADDRESS = "https://ropsten.infura.io/";
    private static final String INFURA_TOKEN = "LaSYLIXhaLNU6l2t5zXZ";
    private static final String NODE = "http://192.168.31.246:8545";
    private static final String BLOC_ADDRESS = "0x097544cCc24766afF1BF3a78a219C8e1E304Be14";
    private static final String ETHERSCAN_API = "https://api-ropsten.etherscan.io/api";
    private static final String ETHERSCAN_TOKEN = "IVVWBJY8SD1T96DX2M8G5V5XH8UCI3GVCA";
    private static final String ETHERSCAN_DEFAULT = "?module=account&sort=desc";
    private static final String ETHERSCAN_PER_PAGE = "10";
    private static final String TAG = "Utility";
    private static final Web3j web3 = Web3jFactory.build(new HttpService(NODE));
//    private static final Web3j web3 = Web3jFactory.build(new HttpService(INFURA_ADDRESS + INFURA_TOKEN));

    private static volatile float eth2usd = 0;
    private static volatile float eth2cny = 0;

    public static float getEth2usd() {
        return eth2usd;
    }

    public static float getEth2cny() {
        return eth2cny;
    }


    public static void loadExchangeRates(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        eth2usd = pref.getFloat("eth2usd", 0);
        eth2cny = pref.getFloat("eth2cny", 0);
    }

    public static void saveExchangeRates(Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat("eth2usd", eth2usd);
        editor.putFloat("eth2cny", eth2cny);
        editor.apply();
    }

    public static BigDecimal getEtherBalance(String address) {
        try {
            Log.d(TAG, "getEtherBalance: " + address);
            EthGetBalance ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger weiBalance = ethGetBalance.getBalance();
            BigDecimal etherBalance = Convert.fromWei(new BigDecimal(weiBalance), Convert.Unit.ETHER);
//            BigDecimal etherBalance = new BigDecimal(weiBalance).divide(ETHER2WEI, RoundingMode.HALF_UP);
            Log.d(TAG, "getEtherBalance: " + etherBalance.doubleValue());
            return etherBalance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getEtherExchangeRate(Context context) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=USD,CNY")
                    .build();
            Response response = client.newCall(request).execute();
            String reseponseData = response.body().string();
            JSONObject jsonObject = new JSONObject(reseponseData);
            eth2usd = (float) jsonObject.getDouble("USD");
            eth2cny = (float) jsonObject.getDouble("CNY");

            Log.d(TAG, "getEtherExchangeRate: CNY: " + eth2cny);
            Log.d(TAG, "getEtherExchangeRate: USD: " + eth2usd);

            Intent intent = new Intent(REFRESH_PRICE_SIGNAL);
            intent.putExtra("status", true);
            context.sendBroadcast(intent);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Intent intent = new Intent(REFRESH_PRICE_SIGNAL);
            intent.putExtra("status", false);
            context.sendBroadcast(intent);
        }
    }

    public static BigDecimal getBlocBalance(String address) {
        TransactionManager transactionManager = new ReadonlyTransactionManager(web3, address);
        BlockcloudToken blockcloudToken = BlockcloudToken.load(BLOC_ADDRESS, web3, transactionManager, BigInteger.valueOf(0), BigInteger.valueOf(0));
        try {
            BigInteger result = blockcloudToken.balanceOf(AccountsManager.getCurAccount().getAddress()).send();
            BigDecimal blocBalance = Convert.fromWei(new BigDecimal(result), Convert.Unit.ETHER);
            Log.d(TAG, "getBlocBalance: " + blocBalance.doubleValue());
            return blocBalance;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getWalletdirectory(Context context) {
        return context.getFilesDir().getAbsolutePath() + "/keystore";
    }

    public static EthSendTransaction makeETHTransaction(Credentials credentials, String receiverAddress, String gasPrice, int gasLimit, String value) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            Log.d(TAG, "makeETHTransaction: nonce is " + nonce);
            BigInteger gasPriceWei = Convert.toWei(new BigDecimal(gasPrice), Convert.Unit.GWEI).toBigInteger();
            BigInteger valueWei = Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER).toBigInteger();
            BigInteger gasLimitBig = BigInteger.valueOf(gasLimit);
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPriceWei, gasLimitBig, receiverAddress, valueWei);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
            return ethSendTransaction;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static TransactionReceipt makeBLOCTransaction(Credentials credentials, String address, String gasPrice, int gasLimit, String value) throws RuntimeException {
        BigInteger gasPriceWei = Convert.toWei(new BigDecimal(gasPrice), Convert.Unit.GWEI).toBigInteger();
        BigInteger valueWei = Convert.toWei(new BigDecimal(value), Convert.Unit.ETHER).toBigInteger();
        BigInteger gasLimitBig = BigInteger.valueOf(gasLimit);
        BlockcloudToken blockcloudToken = BlockcloudToken.load(BLOC_ADDRESS, web3, credentials, gasPriceWei, gasLimitBig);
        try {
            TransactionReceipt result = blockcloudToken.transfer(address, valueWei).send();
            return result;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                createdfileName = WalletUtils.generateLightNewWalletFile(password, outDir);
            } else {
                ECKeyPair keyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
                createdfileName = WalletUtils.generateWalletFile(password, keyPair, outDir, false);
            }
            return createdfileName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatAddress(String address) {
        Pattern pattern1 = Pattern.compile("0x[0-9A-Fa-f]{40}");
        Matcher matcher1 = pattern1.matcher(address);
        if (matcher1.matches()){
            return address;
        }else{
            Pattern pattern2 = Pattern.compile("[0-9A-Fa-f]{40}");
            Matcher matcher2 = pattern2.matcher(address);
            if (matcher2.matches()){
                return "0x" + address;
            }else{
                return null;
            }
        }
    }

    public static List<Transaction> getTransactHist(Transaction.TransactionType transactionType,
            GetTransactHistTask.MODE mode, String address, int page, int latestBlock)
            throws JSONException, IOException{
        String url = ETHERSCAN_API + ETHERSCAN_DEFAULT + "&address=" + address + "&apikey=" + ETHERSCAN_TOKEN;

        switch (transactionType){
            case BLOC:
                url += "&action=tokentx&contractaddress=" + BLOC_ADDRESS;
                break;
            case ETH:
                url += "&action=txlist";
                break;
            case INTERNAL:
                url += "&action=txlistinternal";
                break;
        }

        switch (mode){
            case MODE_APPEND:
                url += "&page=" + page + "&offset=" + ETHERSCAN_PER_PAGE + "&endblock=" + latestBlock;
                break;
            case MODE_PREPEND:
                url += "&startblock=" + (latestBlock + 1);
                break;
            case MODE_REPLACE:
                url += "&page=" + page + "&offset=" + ETHERSCAN_PER_PAGE;
                break;
        }
        Log.d(TAG, "getTransactHist: " + url);

        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String reseponseData = response.body().string();
            JSONObject jsonObject = new JSONObject(reseponseData);
            List<Transaction> transacts = new Gson().fromJson(jsonObject.getString("result"),
                    new TypeToken<List<Transaction>>() {}.getType());

            for (Transaction t:transacts){
                t.setTransactionType(transactionType);
            }
//            Log.d(TAG, "getETHTransactHist: " + transacts.size());
//            Transaction t = transacts.get(0);
//            Log.d(TAG, "getETHTransactHist: " + t.getTimeStamp() + "\n" + t.getFromAddress() + "\n" + t.getToAddress() + "\n" + t.getBlockNumber() + "\n" + t.getTransactionID() + "\n" + t.getValue());
            return transacts;
        }catch (JSONException e){
            Log.d(TAG, "getETHTransactHist: JSON parse error");
            e.printStackTrace();
            throw new JSONException("JSON parse error");
        }catch (IOException e){
            Log.d(TAG, "getETHTransactHist: Network Error");
            e.printStackTrace();
            throw new IOException("Network error");
        }
    }
}
