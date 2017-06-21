package com.active.radioactive.repository;

import android.content.Context;

import com.active.radioactive.data.model.ResultContainerModel;
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

@EBean

public class ResultAlbumRepository {

    private OnLoadListener _onLoadListener;

    protected Gson _gson = new Gson();

    protected String _url = "https://itunes.apple.com/search?term=%s&limit=1";

    @Background
    public void getAlbumArt(String streamTitle) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(_url, streamTitle));
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                String jsonString = baos.toString();
                handleResponse(_gson.fromJson(jsonString, ResultContainerModel.class));
            }else {
                handleFailure(new Exception(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase()));
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
    protected void handleResponse(ResultContainerModel data) {
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
        public void onLoad(ResultContainerModel data);

        public void onFailure(Exception e);
    }
}
