package com.active.radioactive.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.active.radioactive.helper.NetworkingHelper;

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_KEY="isAvailable";

    @Override
    public void onReceive(Context context, Intent ıntent) {
        PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().putBoolean(NETWORK_KEY,NetworkingHelper.isNetworkAvailable(context)).commit();
    }
}
