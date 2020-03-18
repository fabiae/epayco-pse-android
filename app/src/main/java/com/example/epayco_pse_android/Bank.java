package com.example.epayco_pse_android;

public class Bank {

    private String bankCode;
    private String bankName;

    public Bank(String bankCode, String bankName) {
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public Bank() {
        this.bankCode = null;
        this.bankName = null;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
