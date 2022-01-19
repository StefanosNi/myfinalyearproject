package com.org.trophy.admin.Helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpHelper extends AsyncTask<String, Void, String> {
    String url;
    HttpListener listener;
    HashMap<String, String> params;
    TYPE type;
    int code;
    Object[] objects;
    public enum TYPE {GET, POST}

    public HttpHelper(){

    }
    public HttpHelper(String url, TYPE type, HttpListener listener, HashMap<String, String> params, int code, Object... objects){
        this.url = url;
        this.listener = listener;
        this.params = params;
        this.type = type;
        this.code = code;
        this.objects = objects;
    }
    public void setListener(HttpListener listener){
        this.listener = listener;
    }
    public void setParams(HashMap<String, String> params){
        this.params = params;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void setType(TYPE type){
        this.type = type;
    }
    public void setCode(int code){
        this.code = code;
    }
    @Override
    protected String doInBackground(String... values) {
        if(type == TYPE.GET){
            return processGetRequest(url, params);
        }else if(type == TYPE.POST){
            return processPostRequest(url, params);
        }else {
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if(listener != null){
            listener.onResponse(result, code, objects);
        }
    }

    @Override
    protected void onPreExecute() {

    }

    private String processGetRequest(String url, HashMap<String, String> map){
        try{
            OkHttpClient client = new OkHttpClient();
            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder();
            for(Map.Entry<String, String> item : map.entrySet()) {
                httpBuilder.addQueryParameter(item.getKey(), item.getValue());
            }
            Request request = new Request.Builder().url(httpBuilder.build()).build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String processPostRequest(String url, HashMap<String, String> map){
        try{
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            for(String key : map.keySet()){
                builder.addFormDataPart(key, map.get(key));
            }
            RequestBody requestBody = builder.build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}