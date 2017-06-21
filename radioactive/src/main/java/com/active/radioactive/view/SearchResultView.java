package com.active.radioactive.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.active.radioactive.R;
import com.active.radioactive.activity.SearchResultActivity;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.ImageHelper;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EViewGroup;
import com.googlecode.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_search_result)
public class SearchResultView extends RelativeLayout implements ImageHelper.OnLoadListener {

    @ViewById
    ImageView _imgStation;

    @ViewById
    TextView _lblRadioName;

    @ViewById
    TextView _lblDescription;

    @Bean
    ImageHelper _imageHelper;

    private StationModel _data;

    public SearchResultView(Context context) {
        super(context);
    }

    public SearchResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setData(StationModel data) {
        this._data = data;
        _lblDescription.setText(data.Description);
        _lblRadioName.setText(data.Name);
        _imageHelper.setOnLoadListener(this);
        _imageHelper.getImage(data.IconUrl);
    }

    @Override
    public void onLoad(Bitmap image) {
        _imgStation.setImageBitmap(image);

    }

    @Click(R.id._panel)
    public void panelClick() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("data", _data);
        ((SearchResultActivity) getContext()).setResult(1, resultIntent);
        ((SearchResultActivity) getContext()).finish();

    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
    }
}
