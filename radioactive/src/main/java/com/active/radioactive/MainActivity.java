package com.active.radioactive;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.active.radioactive.activity.SearchResultActivity_;
import com.active.radioactive.adapter.PagerAdapter;
import com.active.radioactive.data.enumeration.PlayerState;
import com.active.radioactive.data.model.CategoryModel;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.KeyboardHelper;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.helper.Utils;
import com.active.radioactive.receiver.NetworkChangeReceiver;
import com.active.radioactive.repository.CategoryRepository;
import com.active.radioactive.service.PlayerService;
import com.active.radioactive.view.SearchView;
import com.active.radioactive.view.SearchView_;
import com.active.radioactive.view.StationPlayerView;
import com.devspark.appmsg.AppMsg;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;
import com.viewpagerindicator.TitlePageIndicator;

@EActivity(R.layout.activity_main)
public class MainActivity extends SherlockFragmentActivity implements
        CategoryRepository.OnLoadListener,
        TextView.OnEditorActionListener,
        MenuItem.OnActionExpandListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @ViewById
    ViewPager _pager;

    @ViewById
    TitlePageIndicator _indicator;

    @ViewById
    StationPlayerView _stationPlayerView;

    @Bean
    CategoryRepository _categoryRepo;

    public boolean _isConnected;

    private SearchView _searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PagerAdapter.build(getSupportFragmentManager(), this);
        _searchView = SearchView_.build(MainActivity.this);
        _searchView.setOnEditorActionListener(this);
        if (!NetworkingHelper.isNetworkAvailable(this)) {
            AppMsg.makeText(this, R.string.networking_disconnect, AppMsg.STYLE_ALERT).show();
        }
    }

    @AfterViews
    public void init() {
        Utils.setLang(getResources().getConfiguration().locale.getLanguage().toLowerCase());
        _categoryRepo.setOnLoadListener(this);
        _pager.setAdapter(PagerAdapter.getInstance());
        _pager.setOffscreenPageLimit(4);
        _indicator.setFooterColor(getResources().getColor(R.color.light_blue));
        _indicator.setBackgroundColor(getResources().getColor(R.color.actionbar));
        _indicator.setViewPager(_pager);
        _indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Underline);
        _categoryRepo.getCategories();

        connectService();

    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add("Search")
                .setIcon(R.drawable.ic_action_search)
                .setActionView(_searchView)
                .setOnActionExpandListener(this)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return true;
    }


    @Override
    public void onLoad(CategoryModel[] data) {
        PagerAdapter.getInstance().setData(Utils.toList(data));
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
    }

    public void callPlayer(StationModel data, boolean forceStart) {
        _stationPlayerView.setVisibility(View.VISIBLE);
        _stationPlayerView.setData(data, forceStart);
        _indicator.setCurrentItem(0);
    }

    protected void connectService() {
        Intent intent = new Intent(this, PlayerService.class);
        startService(intent);
        bindService(intent, serviceConnection, this.BIND_AUTO_CREATE);
    }

    public void disconnectService() {
        if (_isConnected) {
            unbindService(serviceConnection);
            _isConnected = false;
        }
    }

    ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            _isConnected = true;
            PlayerService.LocalBinder localBinder = (PlayerService.LocalBinder) iBinder;
            _stationPlayerView.setService(localBinder.getServerInstance());

            if (!_stationPlayerView.getService().getState().equals(PlayerState.Stopped) && _stationPlayerView.getService().getData() != null) {

                callPlayer(_stationPlayerView.getService().getData(), _stationPlayerView.getService().getState().equals(PlayerState.Playing));
                PagerAdapter.getInstance().setNowPlaying(_stationPlayerView.getService().getData());

            } else {
                _stationPlayerView.setVisibility(View.GONE);
            }
        }


        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            _isConnected = false;
            _stationPlayerView.setService(null);
        }
    };

    @Click(R.id._stationPlayerView)
    public void stationPlayerClick() {
        _indicator.setCurrentItem(0);

    }

    @Override
    protected void onDestroy() {
        try {
            PagerAdapter.getInstance().setNowPlaying(null);

        } catch (Exception e) {

        }
        disconnectService();
        super.onDestroy();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            if (textView.getText() != null && textView.getText().length() >= 3) {
                SearchResultActivity_.intent(MainActivity.this)._key(_searchView.getSearchString()).startForResult(1000);
            } else {
                _searchView.setError("Please write at least 3 characters!");
            }
        }
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        _searchView.post(new Runnable() {
            @Override
            public void run() {
                KeyboardHelper.
                        show(MainActivity.this, _searchView.getEditText());
            }
        });
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        KeyboardHelper.hide(this, _searchView.getEditText());
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1 && data != null) {
            StationModel stationModel = (StationModel) data.getSerializableExtra("data");
            callPlayer(stationModel, true);

        }
    }

    @Override
    protected void onStart() {
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!sharedPreferences.getBoolean(NetworkChangeReceiver.NETWORK_KEY, true)) {
            AppMsg.makeText(this, R.string.networking_disconnect, AppMsg.STYLE_ALERT).show();
        }
    }
}



