package com.example.apple.cryptosample.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apple.cryptosample.R;
import com.example.apple.cryptosample.data.viewdata.AlgorithmData;
import com.example.apple.cryptosample.view.AlgorithmListViewHolder;

/**
 * Created by apple on 2016. 12. 16..
 */
public class AlgorithmListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //데이터 클래스 정의//
    AlgorithmData algorithmData;

    //자원 클래스 정의//
    Context context;

    //생성자를 이용하여서 자원과 데이터 클래스의 초기화//
    public AlgorithmListAdapter(Context context) {
        this.context = context;

        algorithmData = new AlgorithmData();
    }

    public void set_AlgorithmData(AlgorithmData algorithmData) {
        if (this.algorithmData != algorithmData) {
            this.algorithmData = algorithmData;

            notifyDataSetChanged(); //UI데이터 갱신//
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //리스트에 나타낼 뷰를 생성//
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.algorithm_view_layout, parent, false);

        AlgorithmListViewHolder holder = new AlgorithmListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (algorithmData.getAlgorithmDataList().size() > 0) {
            if (position < algorithmData.getAlgorithmDataList().size()) {
                //뷰홀더의 자원을 초기화//
                final AlgorithmListViewHolder algorithmListViewHolder = (AlgorithmListViewHolder) holder;

                //데이터 클래스, 자원을 할당//
                algorithmListViewHolder.set_Algorithm_info(algorithmData.getAlgorithmDataList().get(position), context);

                Log.d("json data_bind", algorithmData.getAlgorithmDataList().get(position).getAlgorithm_name());

                return;
            }

            position -= algorithmData.getAlgorithmDataList().size();
        }

        throw new IllegalArgumentException("invalid position");
    }

    @Override
    public int getItemCount() {
        if (algorithmData == null) {
            return 0;
        }

        //현재 리스트에 등록된 개수만큼 반환//
        return algorithmData.getAlgorithmDataList().size();
    }
}
