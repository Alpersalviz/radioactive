package com.active.radioactive.repository;

import android.content.Context;

import com.active.radioactive.data.model.CategoryModel;
import com.active.radioactive.helper.Utils;
import com.active.radioactive.preference.CategoryPreferences_;
import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.sharedpreferences.Pref;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;

@EBean
public class CategoryRepository {
    @RootContext
    Context _context;

    private OnLoadListener _onLoadListener;

    protected Gson _gson = new Gson();

    protected String _url = "http://api.bitpazarim.co/categories/%s";

    @Pref
    CategoryPreferences_ _categoryPreferences;

    @Background
    public void getCategories() {
        try {
            handleResponse(_gson.fromJson(_categoryPreferences.getCategoriesJson().get(), CategoryModel[].class));
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(String.format(_url, Utils.getLang()));
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                String jsonString = baos.toString();
                _categoryPreferences.edit().getCategoriesJson().put(jsonString).apply();
                handleResponse(_gson.fromJson(baos.toString(), CategoryModel[].class));
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
    protected void handleResponse(CategoryModel[] data) {
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

        public void onLoad(CategoryModel[] data);

        public void onFailure(Exception e);
    }

}
