package com.active.radioactive.repository;

import android.content.Context;
import android.preference.PreferenceManager;

import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.Utils;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;

@EBean
public class StationRepository {

    @RootContext
    protected Context _context;

    private OnLoadListener _onLoadListener;

    protected Gson _gson = new Gson();

    protected String _url = "http://api.bitpazarim.co/stations/%s/%s/%d/20";

    protected String _errorUrl = "http://api.bitpazarim.co/error/%s";

    @Background
    public void getStations(String categoryId, int offset) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(_url, Utils.getLang(), categoryId, offset * 20));
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                String jsonString = baos.toString();
                handleResponse(_gson.fromJson(jsonString, StationModel[].class));
            } else {
                handleFailure(new Exception(response.getStatusLine().getStatusCode() + " - " + response.getStatusLine().getReasonPhrase()));
            }
        } catch (Exception e) {
            handleFailure(e);
        }
    }

    @Background
    public void error(String id) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(_errorUrl, id));
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                //
            } else {
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
