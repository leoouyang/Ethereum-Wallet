package com.example.leo.ethereumwallet;

import android.util.Log;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class Web3jUtil {
    private static final String INFURA_ADDRESS = "https://ropsten.infura.io/";
    private static final String INFURA_TOKEN = "LaSYLIXhaLNU6l2t5zXZ";
    private static final BigDecimal ETHER2WEI = new BigDecimal(Math.pow(10,18));
    private static final String TAG = "Web3jUtil";
    private static final Web3j web3 = Web3jFactory.build(new HttpService(INFURA_ADDRESS + INFURA_TOKEN));

    public static double getEtherBalance(String address){

        try{
            Log.d(TAG, "getEtherBalance: " + address);
            EthGetBalance ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger weiBalance = ethGetBalance.getBalance();
            BigDecimal etherBalance = new BigDecimal(weiBalance).divide(ETHER2WEI, RoundingMode.HALF_UP);
            Log.d(TAG, "getEtherBalance: "+ etherBalance.doubleValue());
            return etherBalance.doubleValue();
        }catch (Exception e){
            e.printStackTrace();
        }
        return -1.0;
    }

}
