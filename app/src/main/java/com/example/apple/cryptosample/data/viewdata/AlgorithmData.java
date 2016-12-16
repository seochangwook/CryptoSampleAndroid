package com.example.apple.cryptosample.data.viewdata;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2016. 12. 16..
 */
public class AlgorithmData {
    public List<AlgorithmData> algorithmDataList = new ArrayList<>(); //여러 사람의 정보를 담기위한 배열선언//
    public String algorithm_name;

    public List<AlgorithmData> getAlgorithmDataList() {
        return algorithmDataList;
    }

    public void setAlgorithmDataList(List<AlgorithmData> algorithmDataList) {
        this.algorithmDataList = algorithmDataList;
    }

    public String getAlgorithm_name() {
        return algorithm_name;
    }

    public void setAlgorithm_name(String algorithm_name) {
        this.algorithm_name = algorithm_name;
    }
}
