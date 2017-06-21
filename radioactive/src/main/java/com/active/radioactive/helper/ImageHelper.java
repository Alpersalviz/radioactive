package com.active.radioactive.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Kaan on 05.09.2013.
 */

@EBean
public class ImageHelper {
    @RootContext
    Context _context;

    private OnLoadListener _onLoadListener;

    public void getImage(String url) {
        try {
            FileInputStream fis = _context.openFileInput(url.replace("/", "+"));
            if (_onLoadListener != null)
                _onLoadListener.onLoad(BitmapFactory.decodeStream(fis));
        } catch (FileNotFoundException e) {
            requestImage(url);
        } catch (Exception e) {
            if (_onLoadListener != null) {
                _onLoadListener.onFailure(e);
            }
        }
    }

    @Background
    protected void requestImage(String url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = client.execute(httpget);

            if (response.getStatusLine().getStatusCode() == 200) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                response.getEntity().writeTo(baos);
                handleResponse(baos.toByteArray(), url);
            }
        } catch (Exception e) {
            if (_onLoadListener != null) {
                _onLoadListener.onFailure(e);
            }
        }
    }

    @UiThread
    protected void handleResponse(byte[] data, String url) {
        try {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

            if (_onLoadListener != null) {
                _onLoadListener.onLoad(bmp);
            }
            setLocalFile(url, data);
        } catch (Exception e) {
            if (_onLoadListener != null) {
                _onLoadListener.onFailure(e);
            }
        }
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this._onLoadListener = onLoadListener;
    }

    public interface OnLoadListener {

        public void onLoad(Bitmap image);

        public void onFailure(Exception e);
    }

    public void setLocalFile(String url, byte[] data) {
        try {
            FileOutputStream fos = _context.openFileOutput(url.replace("/", "+"), Context.MODE_PRIVATE);
            fos.write(data, 0, data.length);
            fos.close();
        } catch (Exception e) {

        }
    }

}
