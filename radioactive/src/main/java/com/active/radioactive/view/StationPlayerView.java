package com.active.radioactive.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.active.radioactive.R;
import com.active.radioactive.adapter.PagerAdapter;
import com.active.radioactive.data.enumeration.PlayerState;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.ImageHelper;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.service.PlayerService;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_station_player)
public class StationPlayerView extends RelativeLayout implements ImageHelper.OnLoadListener, PlayerService.OnActionListener {

    @ViewById
    ImageView _imgStation;

    @ViewById
    TextView _lblStreamTitle;

    @ViewById
    TextView _lblStationName;

    @ViewById
    ImageButton _btnStop;

    @ViewById
    ImageButton _btnPlay;

    @ViewById
    ProgressBar _pbPlaying;

    @Bean
    protected ImageHelper _imageHelper;

    private StationModel _data;

    private PlayerService _playerService;

    public StationPlayerView(Context context) {
        super(context);
    }

    public StationPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @AfterViews
    public void init() {
    }

    @Click(R.id._btnPlay)
    public void startClick() {
        if (_playerService.getState().equals(PlayerState.Playing)) {
            _playerService.pause();
            _btnPlay.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_play));

        } else {
            _playerService.playStation(_data);
            _btnPlay.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_pause));
        }
    }

    @Click(R.id._btnStop)
    public void stopClick() {
        _playerService.stop();
        _btnPlay.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_play));
    }

    public void setData(StationModel data, boolean forceStart) {
        if (_data != null && data.Id.equals(_data.Id) && _playerService.getState().equals(PlayerState.Playing))
            return;
        _data = data;
        _lblStreamTitle.setText(data.Description);
        _lblStationName.setText(data.Name);
        _imageHelper.setOnLoadListener(this);
        _imageHelper.getImage(data.IconUrl);
        if (forceStart) {
            _playerService.playStation(_data);
            _btnPlay.setImageDrawable(getContext().getResources().getDrawable(R.drawable.icon_pause));
        }
        PagerAdapter.getInstance().setNowPlaying(_data);
    }

    @Override
    public void onLoad(Bitmap image) {
        _imgStation.setImageBitmap(image);
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
    }


    @Override
    public void onTitleChanged(String title) {
        _lblStreamTitle.setText(title);
        PagerAdapter.getInstance().setPlayerTitle(title);
    }

    @Override
    public void onBufferChange(int i, int i2) {
        PagerAdapter.getInstance().setPlayerBuffer(i, i2);
    }

    @Override
    public void onPlayerChange(boolean isPlaying) {
        if (isPlaying) {
            _btnPlay.setEnabled(true);
            _pbPlaying.setVisibility(View.GONE);

        } else {
            _btnPlay.setEnabled(false);
            _pbPlaying.setVisibility(View.VISIBLE);
        }
        PagerAdapter.getInstance().setPlayerChange(isPlaying);

    }

    @Override
    public void onPlayerStarted(boolean playerStarted) {
        if (playerStarted)
            _pbPlaying.setVisibility(View.VISIBLE);

    }

    @Override
    public void onIndexChange(int current, int size) {
        PagerAdapter.getInstance().setIndex(current, size);
    }

    @Override
    public void onChannelFailure() {
        PagerAdapter.getInstance().setChannelFailure();
    }

    public void setService(PlayerService serverInstance) {
        _playerService = serverInstance;
        _playerService.setOnActionListener(StationPlayerView.this);
    }

    public PlayerService getService() {
        return _playerService;
    }

}
