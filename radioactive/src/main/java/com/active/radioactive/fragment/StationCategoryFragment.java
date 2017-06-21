package com.active.radioactive.fragment;

import android.view.View;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.active.radioactive.R;
import com.active.radioactive.adapter.StationCategoryAdapter;
import com.active.radioactive.data.model.CategoryModel;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.Utils;
import com.active.radioactive.repository.StationRepository;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EFragment;
import com.googlecode.androidannotations.annotations.FragmentArg;
import com.googlecode.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_station_category)
public class StationCategoryFragment extends SherlockFragment implements StationRepository.OnLoadListener {

    @Bean
    StationRepository _repo;

    @Bean
    StationCategoryAdapter _adapter;

    @ViewById
    GridView _grid;

    @ViewById
    ProgressBar _pbLoading;

    @ViewById
    ProgressBar _pbBottomLoading;

    @FragmentArg
    CategoryModel _category;

    protected int _offset = 0;

    private boolean _getNeeded = true;


    @AfterViews
    public void init() {
        _repo.setOnLoadListener(this);
        getData();
        _grid.setAdapter(_adapter);
        _grid.setOnScrollListener(new CGridScrollListener());
    }

    public void getData() {
        if (_offset != 0)
            _pbBottomLoading.setVisibility(View.VISIBLE);
        _getNeeded = false;
        _repo.getStations(_category.Id, _offset);


    }

    @Override
    public void onLoad(StationModel[] data) {
        _getNeeded = data.length == 20;
        _adapter.setData(Utils.toList(data));
        _pbLoading.setVisibility(View.GONE);
        _pbBottomLoading.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();

    }

    private class CGridScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if ((totalItemCount > 0 && _getNeeded) && (firstVisibleItem + visibleItemCount >= totalItemCount)) {
                _offset++;
                getData();
            }
        }
    }
}
