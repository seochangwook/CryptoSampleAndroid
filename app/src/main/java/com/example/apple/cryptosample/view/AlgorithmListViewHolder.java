package com.example.apple.cryptosample.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.apple.cryptosample.R;
import com.example.apple.cryptosample.data.viewdata.AlgorithmData;

/**
 * Created by apple on 2016. 12. 16..
 */
public class AlgorithmListViewHolder extends RecyclerView.ViewHolder{
    public TextView name_textview;

    //데이터 클래스 정의//
    public AlgorithmData algorithmData;

    public AlgorithmListViewHolder(View itemView) {
        super(itemView);

        name_textview = (TextView)itemView.findViewById(R.id.algorithm_name);
    }

    public void set_Algorithm_info(AlgorithmData algorithm_data, Context context) {
        this.algorithmData = algorithm_data; //데이터 정보 등록//

        Log.d("json data view holder", algorithm_data.getAlgorithm_name());

        this.name_textview.setText(algorithm_data.getAlgorithm_name());
    }
}
