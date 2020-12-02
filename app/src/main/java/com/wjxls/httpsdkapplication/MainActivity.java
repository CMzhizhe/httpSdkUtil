package com.wjxls.httpsdkapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wjxls.httpllibrary.base.HttpResponseHandler;
import com.wjxls.httpllibrary.request.WJHttpRequestUtil;
import com.wjxls.httpllibrary.request.basic.BasicGetRequest;
import com.wjxls.httpllibrary.request.basic.BasicPostRequest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tvContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btGet = this.findViewById(R.id.bt_main_get_reqeust);
        Button btPost = this.findViewById(R.id.bt_main_post_reqeust);
        tvContent = this.findViewById(R.id.tv_main_content);


        WJHttpRequestUtil.getInstance().init(true,"url地址");
        //这里设置某些必要的参数
        WJHttpRequestUtil.getInstance().setOnWjhttpMustParamatersListener(new WJHttpRequestUtil.OnWjhttpMustParamatersListener() {
            @Override
            public ArrayMap<String, String> onWjhttpMustParamaters() {
                ArrayMap<String,String> map = new ArrayMap<>(2);
                map.put("user","张三");
                map.put("phone","110");
                return map;
            }
        });

        btGet.setOnClickListener(this);
        btPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_main_get_reqeust){
            BasicGetRequest basicGetRequest = new BasicGetRequest("index_app", null, new HttpResponseHandler() {
                @Override
                public void onResponse(int statusCode, String response) {
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           tvContent.setText(response);
                       }
                   });
                }
            });
            WJHttpRequestUtil.getInstance().doGetRequest(basicGetRequest);
        }else {
            ArrayMap<String,String> map = new ArrayMap<>();
            map.put("partition_tag","normal_19");
            BasicPostRequest basicPostRequest = new BasicPostRequest("product/partition_thumb", map, new HttpResponseHandler() {
                @Override
                public void onResponse(int statusCode, String response) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvContent.setText(response);
                        }
                    });
                }
            });
            WJHttpRequestUtil.getInstance().dePostReqeust(basicPostRequest);
        }
    }
}