package com.active.radioactive.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.view.SearchResultView;
import com.active.radioactive.view.SearchResultView_;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.List;

@EBean
public class SearchResultAdapter extends BaseAdapter {
    List<StationModel> data = new ArrayList<StationModel>();

    @RootContext
    Context context;

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        SearchResultView searchResultView = view != null ? (SearchResultView) view : SearchResultView_.build(context);
        searchResultView.setData(data.get(i));
        return searchResultView;
    }

    public void setData(List<StationModel> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
