package com.example.leo.ethereumwallet.gson;

import java.math.BigDecimal;

public class Account {
    //    private String privateKey;
    private String keystore;
    private String address;
    private String username;
    private BigDecimal ethereum = new BigDecimal(0);
    private BigDecimal selfCoin = new BigDecimal(0);

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getEthereum() {
        return ethereum;
    }

    public void setEthereum(BigDecimal ethereum) {
        this.ethereum = ethereum;
    }

    public BigDecimal getSelfCoin() {
        return selfCoin;
    }

    public void setSelfCoin(BigDecimal selfCoin) {
        this.selfCoin = selfCoin;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Address: ")
                .append(getAddress())
                .append("Name: ")
                .append(getUsername())
                .append("Keystore: ")
                .append(getKeystore());
        return builder.toString();
    }
}
