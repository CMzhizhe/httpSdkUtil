package com.wjxls.httpllibrary.base;

import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.wjxls.httpllibrary.request.WJHttpRequestUtil;
import com.wjxls.httpllibrary.request.basic.BasicGetRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wjxls.httpllibrary.HttpConstants.ISDEBUG;

/**
 * HttpURLConnection封装基类，网络请求，设置请求协议头、发送请求
 * <p>
 * Created by hardyshi on 16/11/22.
 */
public abstract class BaseConnection {

    private static final String TAG = "BaseConnection";
    protected static final String HTTP_REQ_PROPERTY_CHARSET = "Accept-Charset";
    protected static final String HTTP_REQ_VALUE_CHARSET = "UTF-8";
    protected static final String HTTP_REQ_PROPERTY_CONTENT_TYPE = "Content-Type";
    protected static final String HTTP_REQ_VALUE_CONTENT_TYPE = "application/json;charset=UTF-8";
    protected static final String HTTP_REQ_PROPERTY_CONTENT_LENGTH = "Content-Length";
    protected static final String HTTP_REQ_METHOD_GET = "GET";
    protected static final String HTTP_REQ_METHOD_POST = "POST";
    protected static final String HTTP_REQ_PLATFORM = "platform";
    protected static final String HTTP_REQ_ANDROID = "android";
    /**
     * 建立连接的超时时间
     */
    protected static final int CONNECT_TIMEOUT = 10 * 1000;
    /**
     * 建立到资源的连接后从 input 流读入时的超时时间
     */
    protected static final int DEFAULT_READ_TIMEOUT = 10 * 1000;

    public BaseConnection() {

    }

    private void setURLConnectionCommonPara() {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return;
        }
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_READ_TIMEOUT);
        connection.setUseCaches(false);
        connection.setRequestProperty(HTTP_REQ_PROPERTY_CHARSET, HTTP_REQ_VALUE_CHARSET);
        connection.setRequestProperty(HTTP_REQ_PROPERTY_CONTENT_TYPE, HTTP_REQ_VALUE_CONTENT_TYPE);
        connection.setRequestProperty(HTTP_REQ_PLATFORM, HTTP_REQ_ANDROID);
    }


    public String doRequest(HttpBasicRequest request) {
        if (null == getURLConnection()) {
            return "";
        }
        setURLConnectionCommonPara();

        if (ISDEBUG) {
            Log.w(TAG, "=================RequestProperties======================");
            Map<String, List<String>> requestProperties = getURLConnection().getRequestProperties();
            Set<String> stringSet = requestProperties.keySet();
            for(String key : stringSet ){
                if (key!=null){
                    Log.w(TAG, key + "=" + getURLConnection().getRequestProperty(key));
                }
            }
        }


        if (request instanceof BasicGetRequest) {
            return doGetRequest();
        } else {
            byte[] data = new byte[]{};
            if (request.getParamaterArrayMap() != null && request.getParamaterArrayMap().size() > 0) {
                ArrayMap<String, String> map1 = request.getParamaterArrayMap();
                ArrayMap<String, String> map2 = null;
                if (WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener() != null && WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters() != null && WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters().size() > 0) {
                    map2 = WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters();
                }
                if (map2 == null) {
                    data = new Gson().toJson(map1).getBytes();
                } else {
                    ArrayMap<String, String> concatMap = new ArrayMap<>();
                    concatMap.putAll(map1);
                    concatMap.putAll(map2);
                    data = new Gson().toJson(concatMap).getBytes();
                }
            } else {
                if (WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener() != null && WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters() != null && WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters().size() > 0) {
                    data = new Gson().toJson(WJHttpRequestUtil.getInstance().getOnWjhttpMustParamatersListener().onWjhttpMustParamaters()).getBytes();
                }
            }
            return doPostRequest(data);
        }
    }

    protected String doGetRequest() {
        String result = "";
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            HttpURLConnection connection = getURLConnection();
            if (null == connection) {
                return "";
            }
            connection.setRequestMethod(HTTP_REQ_METHOD_GET);
            is = connection.getInputStream();
            os = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            result = os.toString(HTTP_REQ_VALUE_CHARSET);
        } catch (javax.net.ssl.SSLHandshakeException ee) {
            Log.e(TAG, "javax.net.ssl.SSLPeerUnverifiedException");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os!=null){
                    os.close();
                }

                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    protected String doPostRequest(byte[] data) {
        InputStream inptStream = null;
        OutputStream outputStream = null;
        ByteArrayOutputStream os = null;
        try {
            HttpURLConnection connection = getURLConnection();
            if (null == connection) {
                return "";
            }
            connection.setRequestMethod(HTTP_REQ_METHOD_POST);
            connection.setRequestProperty(HTTP_REQ_PROPERTY_CONTENT_LENGTH, String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            outputStream = connection.getOutputStream();
            outputStream.write(data);

            int response = connection.getResponseCode();            //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                inptStream = connection.getInputStream();
                os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inptStream.read(buffer)) != -1) {
                    os.write(buffer, 0, len);
                }
                return os.toString(HTTP_REQ_VALUE_CHARSET);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os!=null){
                    os.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

                if (inptStream != null) {
                    inptStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getResponseMessage() {

        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return "";
        } else {
            try {
                return getURLConnection().getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public int getResponseCode() {
        HttpURLConnection connection = getURLConnection();
        if (null == connection) {
            return -1;
        } else {
            try {
                return getURLConnection().getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }


    public abstract HttpURLConnection getURLConnection();

}
