package com.active.radioactive.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.active.radioactive.R;
import com.active.radioactive.data.model.CategoryModel;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.fragment.PlayerFragment;
import com.active.radioactive.fragment.PlayerFragment_;
import com.active.radioactive.fragment.StationCategoryFragment_;

import java.util.ArrayList;
import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapterBase {

    private Context context;

    public static PagerAdapter _pagerAdapter = null;

    private StationModel _nowPlaying;

    private List<CategoryModel> _data = new ArrayList<CategoryModel>();

    private PlayerFragment _playerFragment;

    private PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    public static void build(FragmentManager fm, Context context) {
        _pagerAdapter = new PagerAdapter(fm, context);
    }

    public static PagerAdapter getInstance() {
        if (_pagerAdapter != null)
            return _pagerAdapter;
        else
            throw new ExceptionInInitializerError();
    }

    @Override
    public Fragment getItem(int i) {
        if (_nowPlaying == null) {
            return StationCategoryFragment_.builder()._category(_data.get(i)).build();
        } else {
            if (i == 0) {
                _playerFragment = PlayerFragment_.builder()._data(_nowPlaying).build();
                return _playerFragment;
            } else {
                return StationCategoryFragment_.builder()._category(_data.get(i - 1)).build();
            }
        }
    }


    @Override
    public int getCount() {
        return _nowPlaying != null ? _data.size() + 1 : _data.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (_nowPlaying == null)
            return _data.get(position).Name;
        else {
            if (position == 0)

                return context.getResources().getString(R.string.now_playing);
            else
                return _data.get(position - 1).Name;
        }
    }

    public void setData(List<CategoryModel> _data) {
        this._data = _data;
        notifyDataSetChanged();
    }

    public void setNowPlaying(StationModel nowPlaying) {
        _nowPlaying = nowPlaying;
        if (_nowPlaying == null)
            _playerFragment = null;
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setPlayerTitle(String title) {
        if (_data != null && _playerFragment != null) {
            _playerFragment.setTitle(title);
        }
    }

    public void setPlayerBuffer(int i, int i2) {
        if (_data != null && _playerFragment != null) {
            _playerFragment.setBuffer(i, i2);
        }
    }

    public void setIndex(int current, int size) {
        if (_data != null && _playerFragment != null) {
            _playerFragment.setIndex(current, size);
        }
    }


    public void setPlayerChange(boolean isPlaying) {
        if (_data != null && _playerFragment != null) {
            _playerFragment.setPlayer(isPlaying);
        }

    }

    public void setChannelFailure() {
        if (_data != null && _playerFragment != null) {
            _playerFragment.setChannelFailure();
        }

    }
}
