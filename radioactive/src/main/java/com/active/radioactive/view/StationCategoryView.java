package com.active.radioactive.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.active.radioactive.MainActivity;
import com.active.radioactive.R;
import com.active.radioactive.adapter.PagerAdapter;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.ImageHelper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_station_category)
public class StationCategoryView extends RelativeLayout implements ImageHelper.OnLoadListener {

    @ViewById
    ImageView _imgStation;

    @ViewById
    TextView _lblRadioName;

    @ViewById
    TextView _lblDescription;

    @Bean
    ImageHelper _imageHelper;

    private StationModel _data;

    public StationCategoryView(Context context) {
        super(context);
    }

    public StationCategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StationCategoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(StationModel _data) {
        this._data = _data;
        _imageHelper.setOnLoadListener(this);
        _imageHelper.getImage(_data.IconUrl);
        _lblRadioName.setText(_data.Name);
        _lblDescription.setText(_data.Description);

    }

    @Click(R.id._panel)
    public void imgClick() {
        ((MainActivity) getContext()).callPlayer(_data, true);
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
}
