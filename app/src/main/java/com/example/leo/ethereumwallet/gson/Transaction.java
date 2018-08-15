package com.example.leo.ethereumwallet.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Transaction implements Serializable{
    public static final String REFRESHING = "refreshing";
    public static final String END = "end";

    public enum TransactionType {
            BLOC, ETH, INTERNAL
    }

    private TransactionType transactionType;

    @SerializedName("hash")
    private String transactionID;

    @SerializedName("from")
    private String fromAddress;

    @SerializedName("to")
    private String toAddress;

    private String value;

    private String gasUsed;

    private String gasPrice;

    private String timeStamp;
    //Date date = Date.from(Instant.ofEpochSecond(timeStamp))

    private String blockNumber;

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
}
