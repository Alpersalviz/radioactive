package com.active.radioactive.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.active.radioactive.MainActivity;
import com.active.radioactive.R;
import com.active.radioactive.adapter.PagerAdapter;
import com.active.radioactive.data.model.ResultContainerModel;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.ImageHelper;
import com.active.radioactive.helper.KeyboardHelper;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.helper.Utils;
import com.active.radioactive.repository.ResultAlbumRepository;
import com.active.radioactive.service.PlayerService;
import com.active.radioactive.view.StationPlayerView;
import com.active.radioactive.view.StationPlayerView_;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.EView;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import static com.active.radioactive.R.*;
import static com.active.radioactive.R.drawable.*;

@EFragment(R.layout.fragment_player)
public class PlayerFragment extends SherlockFragment implements ImageHelper.OnLoadListener, ResultAlbumRepository.OnLoadListener {

    @ViewById
    TextView _lblRadioName;

    @ViewById
    TextView _lblStreamTitle;

    @ViewById
    ImageView _imgStation;

    @ViewById
    ProgressBar _progress;

    @ViewById
    TextView _lblChannel;

    @FragmentArg
    StationModel _data;

    @Bean
    ImageHelper _imageHelper;

    @Bean
    ResultAlbumRepository _resultRepo;

    @AfterViews
    public void init() {
        setData(_data);
    }

    public void setData(StationModel data) {
        _data = data;
        _lblRadioName.setText(_data.Name);
        _lblStreamTitle.setText(_data.Description);
        _imageHelper.setOnLoadListener(this);
        _resultRepo.setOnLoadListener(this);
        _imageHelper.getImage(_data.IconUrl);
    }

    @Override
    public void onLoad(Bitmap image) {
        _imgStation.setImageBitmap(image);
    }

    @Override
    public void onLoad(ResultContainerModel data) {
        if (data.resultCount > 0 && data.results.get(0).artworkUrl100 != null)
            _imageHelper.getImage(data.results.get(0).artworkUrl100.replace("100x100", "600x600"));

    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();

    }

    public void setTitle(String title) {
        try {
            _resultRepo.getAlbumArt(URLEncoder.encode(title.replace("\n", "").replace("\r", "").replace("\t", "").trim(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
        }
        _lblStreamTitle.setText(title.trim());
    }

    public void setBuffer(int i, int i2) {
        _progress.setProgress(i * _progress.getMax() / i2);
    }

    public void setIndex(int current, int size) {
       // _lblChannel.setText(getActivity().getResources().getString(R.string.channel_loading) + "(" + (current + 1) + "/" + size + ")");
    }

    public void setPlayer(boolean isPlaying) {
        if (_lblChannel != null)
            if (isPlaying)
                _lblChannel.setVisibility(View.GONE);
    }

    public void setChannelFailure() {
        _lblChannel.setVisibility(View.VISIBLE);
        _lblChannel.setText(getActivity().getResources().getString(R.string.channel_error));
    }
}


