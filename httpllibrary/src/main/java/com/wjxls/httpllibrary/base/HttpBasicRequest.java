package com.wjxls.httpllibrary.base;


import android.util.ArrayMap;

import java.util.HashMap;


public abstract class HttpBasicRequest {
	public abstract HttpResponseHandler getResponseHandler();
	public abstract String getMethodName();
	public abstract ArrayMap<String,String> getParamaterArrayMap();
}
