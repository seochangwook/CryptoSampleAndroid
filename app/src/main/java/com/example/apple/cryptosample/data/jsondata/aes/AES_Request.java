package com.example.apple.cryptosample.data.jsondata.aes;

public class AES_Request {
    private AES_RequestData data;
    private String is_success;

    public AES_RequestData getData() {
        return this.data;
    }

    public void setData(AES_RequestData data) {
        this.data = data;
    }

    public String getIs_success() {
        return this.is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
