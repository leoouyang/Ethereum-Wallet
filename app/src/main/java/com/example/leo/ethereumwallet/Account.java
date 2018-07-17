package com.example.leo.ethereumwallet;

public class Account {
    //    private String privateKey;
    private String keystore;
    private String address;
    private String username;
    private double ethereum = 0.0;
    private double selfCoin = 0.0;

//    public String getPrivateKey() {
//        return privateKey;
//    }

//    public void setPrivateKey(String privateKey) {
//        this.privateKey = privateKey;
//    }

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

    public double getEthereum() {
        return ethereum;
    }

    public void setEthereum(double ethereum) {
        this.ethereum = ethereum;
    }

    public double getSelfCoin() {
        return selfCoin;
    }

    public void setSelfCoin(double selfCoin) {
        this.selfCoin = selfCoin;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Address: ")
                .append(getAddress())
//                .append("Private Key: ")
//                .append(getPrivateKey())
                .append("Name: ")
                .append(getUsername())
                .append("Keystore: ")
                .append(getKeystore());
        return builder.toString();
    }
}
