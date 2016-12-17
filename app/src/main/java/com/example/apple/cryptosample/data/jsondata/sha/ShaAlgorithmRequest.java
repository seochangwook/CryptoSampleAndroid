package com.example.apple.cryptosample.data.jsondata.sha;

public class ShaAlgorithmRequest {
    private ShaAlgorithmRequestData data;
    private String is_success;

    public ShaAlgorithmRequestData getData() {
        return this.data;
    }

    public void setData(ShaAlgorithmRequestData data) {
        this.data = data;
    }

    public String getIs_success() {
        return this.is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
