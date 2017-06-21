package com.active.radioactive.repository;

import android.content.Context;

import com.active.radioactive.data.model.StationModel;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;

@EBean
public class SearchRepository {
    @RootContext
    protected Context _context;

    private OnLoadListener _onLoadListener;

    protected Gson _gson = new Gson();

    protected String _url = "http://api.bitpazarim.co/search/%s";

    @Background
    public void getSearch(String key) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(_url, URLEncoder.encode(key, "UTF-8")));
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                handleResponse(_gson.fromJson(baos.toString(), StationModel[].class));
            } else {
                handleFailure(new Exception(response.getStatusLine().getStatusCode() + " " + response.getStatusLine().getReasonPhrase()));
            }
        } catch (Exception e) {
            handleFailure(e);
        }
    }

    @UiThread
    protected void handleFailure(Exception e) {
        if (_onLoadListener != null) {
            _onLoadListener.onFailure(e);
        }
    }

    @UiThread
    protected void handleResponse(StationModel[] data) {
        try {

            if (_onLoadListener != null) {
                _onLoadListener.onLoad(data);
            }

        } catch (Exception e) {
            if (_onLoadListener != null) {
                _onLoadListener.onFailure(e);
            }
        }
    }


    public void setOnLoadListener(OnLoadListener onloadListener) {
        this._onLoadListener = onloadListener;
    }

    public interface OnLoadListener {

        public void onLoad(StationModel[] data);

        public void onFailure(Exception e);
    }
}
