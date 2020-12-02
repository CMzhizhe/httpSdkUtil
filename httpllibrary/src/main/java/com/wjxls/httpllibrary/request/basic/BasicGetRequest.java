package com.wjxls.httpllibrary.request.basic;


import android.util.ArrayMap;

import com.wjxls.httpllibrary.base.HttpRequest;
import com.wjxls.httpllibrary.base.HttpResponseHandler;

public class BasicGetRequest extends HttpRequest {
    private HttpResponseHandler mResponseHandlerHandler;
	private String method;
	private ArrayMap<String,String> map;
	public BasicGetRequest(String method,ArrayMap<String,String> map, HttpResponseHandler handler) {
	    this.map = map;
	    this.method = method;
        this.mResponseHandlerHandler = handler;
    }

    @Override
    protected void onResponse(int statusCode, String result) {
        this.mResponseHandlerHandler.onResponse(statusCode,result);
    }

    @Override
    public String getMethodName() {
        return method;
    }

    @Override
    public ArrayMap<String, String> getParamaterArrayMap() {
        return map;
    }
}
