

#### 1.引入依赖
```
implementation 'com.github.CMzhizhe:httpSdkUtil:v1.0.1@aar'
```


### 2.使用方式
```
	 WJHttpRequestUtil.getInstance().init(true,"url地址");
     
	   会存在所有接口都需要上传某些参数的方式
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
		
	get请求
	 BasicGetRequest basicGetRequest = new BasicGetRequest("index_app", null, new HttpResponseHandler() {
                @Override
                public void onResponse(int statusCode, String response) {
                  //这里仍然为异步
                }
            });
    WJHttpRequestUtil.getInstance().doGetRequest(basicGetRequest);
	
	post请求
	 ArrayMap<String,String> map = new ArrayMap<>();
            map.put("partition_tag","normal_19");
            BasicPostRequest basicPostRequest = new BasicPostRequest("product/5", map, new HttpResponseHandler() {
                @Override
                public void onResponse(int statusCode, String response) {
                   //这里仍然为异步
                }
            });
    WJHttpRequestUtil.getInstance().dePostReqeust(basicPostRequest);
```








