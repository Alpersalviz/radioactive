package com.active.radioactive.activity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.active.radioactive.R;
import com.active.radioactive.adapter.SearchResultAdapter;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.helper.Utils;
import com.active.radioactive.repository.SearchRepository;
import com.devspark.appmsg.AppMsg;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.Extra;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_search)
public class SearchResultActivity extends SherlockActivity implements SearchRepository.OnLoadListener {

    @ViewById
    ListView _list;

    @ViewById
    TextView _emptyView;

    @Bean
    SearchRepository _repo;

    @Bean
    SearchResultAdapter _adapter;

    @Extra
    String _key;

    private boolean _getNeeded = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        getSupportActionBar().setSubtitle(_key);
        if (!NetworkingHelper.isNetworkAvailable(this)) {
            AppMsg.makeText(this, R.string.networking_disconnect, AppMsg.STYLE_ALERT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @AfterViews
    public void init() {
        _repo.setOnLoadListener(this);
        getData();
        _list.setAdapter(_adapter);
        _list.setEmptyView(_emptyView);
    }

    public void getData() {
        _repo.getSearch(_key);

    }

    @Override
    public void onLoad(StationModel[] data) {
        _getNeeded = data.length == 20;
        _adapter.setData(Utils.toList(data));

    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
    }


}
