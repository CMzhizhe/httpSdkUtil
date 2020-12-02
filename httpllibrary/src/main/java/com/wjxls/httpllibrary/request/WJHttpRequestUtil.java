package com.wjxls.httpllibrary.request;

import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.wjxls.httpllibrary.HttpConstants;
import com.wjxls.httpllibrary.base.BaseConnection;
import com.wjxls.httpllibrary.base.HTTPConnection;
import com.wjxls.httpllibrary.base.HttpBasicRequest;
import com.wjxls.httpllibrary.base.ThreadManager;
import com.wjxls.httpllibrary.request.basic.BasicGetRequest;
import com.wjxls.httpllibrary.request.basic.BasicPostRequest;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wjxls.httpllibrary.HttpConstants.HTTPREQUEST;
import static com.wjxls.httpllibrary.HttpConstants.ISDEBUG;

/**
 * 网络请求分发、执行类
 */
public class WJHttpRequestUtil {
    private String TAG = WJHttpRequestUtil.class.getSimpleName();
    private static volatile WJHttpRequestUtil instance;
    private OnWjhttpMustParamatersListener onWjhttpMustParamatersListener;

    /**
     * @date 创建时间: 2020/12/1
     * @auther gaoxiaoxiong
     * @description 设置必要的参数
     **/
    public void setOnWjhttpMustParamatersListener(OnWjhttpMustParamatersListener onWjhttpMustParamatersListener) {
        this.onWjhttpMustParamatersListener = onWjhttpMustParamatersListener;
    }

    public OnWjhttpMustParamatersListener getOnWjhttpMustParamatersListener() {
        return onWjhttpMustParamatersListener;
    }

    public static WJHttpRequestUtil getInstance() {
        if (instance == null) {
            synchronized (WJHttpRequestUtil.class) {
                if (instance == null) {
                    instance = new WJHttpRequestUtil();
                }
            }
        }
        return instance;
    }

    private WJHttpRequestUtil() {
    }

    public interface OnWjhttpMustParamatersListener{
        ArrayMap<String,String> onWjhttpMustParamaters();
    }

    /**
     * @date 创建时间: 2020/12/1
     * @auther gaoxiaoxiong
     * @description
     **/
    public void init(boolean isDebug, String requestUrl) {
        HttpConstants.ISDEBUG = isDebug;
        HttpConstants.HTTPREQUEST = requestUrl;
    }

    /**
     * @date 创建时间: 2020/11/30
     * @auther gaoxiaoxiong
     * @description get 请求
     **/
    public void doGetRequest(BasicGetRequest basicGetRequest) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                executeRequestInExecutor(basicGetRequest);
            }
        });
    }

    /**
     * @date 创建时间: 2020/11/30
     * @auther gaoxiaoxiong
     * @description post请求
     **/
    public void dePostReqeust(BasicPostRequest basicPostRequest) {
        ThreadManager.getInstance().start(new Runnable() {
            @Override
            public void run() {
                executeRequestInExecutor(basicPostRequest);
            }
        });
    }


    private void executeRequestInExecutor(HttpBasicRequest request) {
        if (ISDEBUG) {
            Log.w(TAG, "=======================================");
            Log.w(TAG, request.getClass().toString());
            Log.w(TAG, HttpConstants.HTTPREQUEST);
            Log.w(TAG, "=======================================");
        }
        boolean isGetRequest = true;
        if (request instanceof BasicPostRequest) {
            isGetRequest = false;
        } else {
            isGetRequest = true;
        }

        String requesUrl = HTTPREQUEST;

        if (TextUtils.isEmpty(requesUrl)){
            return;
        }

        if (isGetRequest) {
            String paramsters = "";

            if (onWjhttpMustParamatersListener!=null && onWjhttpMustParamatersListener.onWjhttpMustParamaters()!=null){
                for (Map.Entry<String, String> entry : onWjhttpMustParamatersListener.onWjhttpMustParamaters().entrySet()) {
                    paramsters = paramsters + entry.getKey() + "=" + entry.getValue() + "&";
                }

                if (!TextUtils.isEmpty(paramsters)) {
                    paramsters = paramsters.substring(0, paramsters.length() - 1);
                }
            }

            if (request.getParamaterArrayMap() != null && request.getParamaterArrayMap().size() > 0) {
                for (Map.Entry<String, String> entry : request.getParamaterArrayMap().entrySet()) {
                    paramsters = paramsters + entry.getKey() + "=" + entry.getValue() + "&";
                }
                if (!TextUtils.isEmpty(paramsters)) {
                    paramsters = paramsters.substring(0, paramsters.length() - 1);
                }
            }

            if (!TextUtils.isEmpty(request.getMethodName())) {
                requesUrl = requesUrl + request.getMethodName();
            }

            requesUrl = requesUrl + "?" + paramsters;

        } else {//post请求
            requesUrl = requesUrl + request.getMethodName();
        }

        BaseConnection connection = new HTTPConnection(requesUrl);
       /* if (HTTP_DOMAIN.contains("https:")) {
            connection = new HTTPSConnection(requesUrl);
        } else {
            connection = new HTTPConnection(requesUrl);
        }*/

        String result = connection.doRequest(request);
        if (ISDEBUG) {
            Log.w(TAG, "=======================================");
            Log.w(TAG, request.getClass().toString());
            Log.w(TAG, result);
            Log.w(TAG, String.valueOf(connection.getResponseCode()));
            Log.w(TAG, connection.getResponseMessage());
            Log.w(TAG, "=======================================");

            Log.w(TAG, "=================Header======================");
            Map<String, List<String>> headers = connection.getURLConnection().getHeaderFields();
            Set<String> keys = headers.keySet();
            for(String key : keys ){
                if (key!=null){
                    Log.w(TAG, key + "=" + connection.getURLConnection().getHeaderField(key));
                }
            }
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            request.getResponseHandler().onResponse(connection.getResponseCode(), result);
        } else {
            if (TextUtils.isEmpty(result)) {
                if (ISDEBUG) {
                    Log.e(TAG, "responseBody is null");
                }
                if (TextUtils.isEmpty(connection.getResponseMessage())) {
                    request.getResponseHandler().onResponse(connection.getResponseCode(), "");
                } else {
                    request.getResponseHandler().onResponse(connection.getResponseCode(), connection.getResponseMessage());
                }
            } else {
                request.getResponseHandler().onResponse(connection.getResponseCode(), result);
            }
        }
    }
}