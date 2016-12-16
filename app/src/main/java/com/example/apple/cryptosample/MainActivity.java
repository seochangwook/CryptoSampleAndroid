package com.example.apple.cryptosample;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.cryptosample.data.jsondata.AlgorithmDataRequest;
import com.example.apple.cryptosample.data.jsondata.AlgorithmDataRequestAlgorithms;
import com.example.apple.cryptosample.data.jsondata.aes.AES_Request;
import com.example.apple.cryptosample.data.viewdata.AlgorithmData;
import com.example.apple.cryptosample.manager.networkmanager.NetworkManager;
import com.example.apple.cryptosample.securemodule.AESForNodejs;
import com.example.apple.cryptosample.view.LoadMoreView;
import com.example.apple.cryptosample.widget.AlgorithmListAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String password = "CIPHER_KEY"; //서버와의 약속//

    /** "1" - aes256 ,"2" - sha256 **/
    String encrypt_decrypt_case = "";
    String log_data_str = "";

    /**어댑터, 데이터**/
    AlgorithmListAdapter algorithmListAdapter;
    AlgorithmData algorithmData;

    NetworkManager networkManager;

    private FamiliarRefreshRecyclerView algorithm_list;
    private FamiliarRecyclerView recyclerview;
    private ProgressDialog pDialog;

    EditText input_crypto_str_edittext;
    Button send_button;
    TextView log_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        algorithm_list = (FamiliarRefreshRecyclerView)findViewById(R.id.data_info_list);
        input_crypto_str_edittext = (EditText)findViewById(R.id.input_str_edittext);
        send_button = (Button)findViewById(R.id.send_button);
        log_textview = (TextView)findViewById(R.id.log_textview);

        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        /** RecyclerView 정의 **/
        //RefreshRecyclerView 기능설정.//
        algorithm_list.setId(android.R.id.list);
        algorithm_list.setLoadMoreView(new LoadMoreView(MainActivity.this));
        algorithm_list.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
        algorithm_list.setLoadMoreEnabled(true);

        //RecyclerView의 특징을 RefreshRecyclerView에 연결//
        recyclerview = algorithm_list.getFamiliarRecyclerView();
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        /** RecyclerView의 각종 뷰 정의(HeaderView, EmptyView, FooterView) **/
        View algorithmlist_emptyview = LayoutInflater.from(MainActivity.this).inflate(R.layout.algorithmlist_emptyview, null);

        //리사이클뷰에 연결//
        recyclerview.setEmptyView(algorithmlist_emptyview, true);

        /** RecyclerView에 Adapter, 데이터 설정 **/
        algorithmData = new AlgorithmData();
        algorithmListAdapter = new  AlgorithmListAdapter(MainActivity.this);

        recyclerview.setAdapter(algorithmListAdapter);

        //리사이클러뷰 클릭 리스너//
        recyclerview.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                String select_encrpyt_name = algorithmData.getAlgorithmDataList().get(position).getAlgorithm_name();

                if(select_encrpyt_name.equals("aes256"))
                {
                    Toast.makeText(getApplicationContext(), "aes256 알고리즘 선택", Toast.LENGTH_SHORT).show();

                    encrypt_decrypt_case = "1";
                }

                else if(select_encrpyt_name.equals("sha256"))
                {
                    Toast.makeText(getApplicationContext(), "sha256 알고리즘 선택", Toast.LENGTH_SHORT).show();

                    encrypt_decrypt_case = "2";
                }
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_textview.setText("");
                log_data_str = "";

                //선택된 방식으로 암호화 진행//
                if(encrypt_decrypt_case.equals("1")) //AES암호화 진행//
                {
                    String plain_str = input_crypto_str_edittext.getText().toString();

                    Log.d("json Encrypt plain", plain_str);

                    //암호화 후 데이터 전송//
                    String encrpyt_str = Encrpyt_AES(plain_str);

                    Log.d("json Encrpyt str", encrpyt_str);

                    trans_data(encrpyt_str);
                }
            }
        });

        /** RecyclerView Refresh이벤트 처리(일반적으로 위에서 당기기기는 현 정보에서 갱신, 아래에서 로딩은 '더보기'기능) **/
        algorithm_list.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("EVENT :", "새로고침 완료");

                        algorithm_list.pullRefreshComplete();

                        algorithmData.getAlgorithmDataList().clear(); //리스트 정보 초기화//
                        algorithmListAdapter.set_AlgorithmData(algorithmData); //초기화된 정보를 갱신//

                        showpDialog();

                        //get_algorithmdata_dummy(); //더미데이터//
                        get_algorithmdata();
                    }
                }, 1000);
            }
        });

        algorithm_list.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("EVENT :", "새로고침 완료");

                        algorithm_list.loadMoreComplete();

                        algorithmData.getAlgorithmDataList().clear(); //리스트 정보 초기화//

                        algorithmListAdapter.set_AlgorithmData(algorithmData); //초기화된 정보를 갱신//

                        showpDialog();

                        //get_algorithmdata_dummy(); //더미데이터//
                        get_algorithmdata();

                        //맨 아래일 시 '더보기' 작업(더보기 작업 생략 - 더미데이터로는 구현하지 않음)//
                        Toast.makeText(MainActivity.this, "더 이상 정보가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        showpDialog();

        //get_algorithmdata_dummy(); //더미데이터//
        get_algorithmdata();
    }

    public String Encrpyt_AES(String plain_str)
    {
        String encryptedMsg = "";

        //AES-256방식의 암호화//
        try {
            encryptedMsg = AESForNodejs.encrypt(plain_str, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedMsg;
    }

    public String Decrpyt_AES(String encrypt_str)
    {
        String decryptedMsg = "";

        //AES-256방식의 암호화//
        try {
            decryptedMsg = AESForNodejs.decrypt(encrypt_str, password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedMsg;
    }

    public void get_algorithmdata()
    {
        /** 네트워크 설정을 한다. **/
        /** OkHttp 자원 설정 **/
        networkManager = NetworkManager.getInstance();

        /** Client 설정 **/
        OkHttpClient client = networkManager.getClient();

        /** GET방식의 프로토콜 Scheme 정의 **/
        //우선적으로 Url을 만든다.//
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http");
        builder.host(getResources().getString(R.string.server_domain));
        builder.port(3000);
        builder.addPathSegment("algorithm_list");

       /* builder.addQueryParameter("page", "1");
        builder.addQueryParameter("count", "20");*/

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .tag(this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestalgorithmlistcallback);
    }

    private Callback requestalgorithmlistcallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) //접속 실패의 경우.//
        {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (MainActivity.this != null) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String response_data = response.body().string();

            Log.d("json data", response_data);

            if (MainActivity.this != null) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        Gson gson = new Gson();

                        AlgorithmDataRequest algorithmdatarequest = gson.fromJson(response_data, AlgorithmDataRequest.class);

                        setData(algorithmdatarequest.getAlgorithms(), algorithmdatarequest.getAlgorithms().length);
                    }
                });
            }
        }
    };

    public void setData(AlgorithmDataRequestAlgorithms[] algorithm_list, int algorithm_list_size)
    {
        List<AlgorithmDataRequestAlgorithms> list = new ArrayList<>();

        list.addAll(Arrays.asList(algorithm_list));

        for(int i=0; i<algorithm_list_size; i++)
        {
            AlgorithmData new_data = new AlgorithmData();

            new_data.setAlgorithm_name(list.get(i).getName());

            Log.d("json data: ", list.get(i).getName());

            algorithmData.algorithmDataList.add((new_data));
        }

        algorithmListAdapter.set_AlgorithmData(algorithmData);
    }

    public void trans_data(String encrpyt_str)
    {
        //네트워크 설정//
        /** Networok 설정 **/
        networkManager = NetworkManager.getInstance();

        OkHttpClient client = networkManager.getClient();

        /** POST방식의 프로토콜 요청 설정 **/
        /** URL 설정 **/
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http"); //스킴정의(Http / Https)//
        builder.host(getResources().getString(R.string.server_domain)); //host정의.//
        builder.port(3000);
        builder.addPathSegment("cipher_test");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("input_password", encrpyt_str);

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(MainActivity.this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requesciphercallback);
    }

    private Callback requesciphercallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) //접속 실패의 경우.//
        {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (MainActivity.this != null) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String response_data = response.body().string();

            Log.d("json data", response_data);

            if (MainActivity.this != null) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        log_data_str += response_data + "\n";

                        Gson gson = new Gson();

                        AES_Request aes_request = gson.fromJson(response_data, AES_Request.class);

                        //다시 암호화되서 온 값을 복호화한다.//
                        String server_data_encrypt = aes_request.getData().getEncrypt();

                        String decrypt_data = Decrpyt_AES(server_data_encrypt);

                        Log.d("decrypt data", decrypt_data);

                        log_data_str = log_data_str + "decrypt data: " + decrypt_data;

                        log_textview.setText(log_data_str);
                    }
                });
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
