package com.active.radioactive.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioTrack;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.active.radioactive.MainActivity_;
import com.active.radioactive.R;
import com.active.radioactive.data.enumeration.PlayerState;
import com.active.radioactive.data.model.StationModel;
import com.active.radioactive.helper.ImageHelper;
import com.active.radioactive.helper.ImageHelper_;
import com.active.radioactive.helper.NetworkingHelper;
import com.active.radioactive.receiver.NetworkChangeReceiver;
import com.active.radioactive.repository.StationRepository;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

public class PlayerService extends Service implements PlayerCallback, ImageHelper.OnLoadListener {

    private Handler _uiHandler;

    private PlayerState _state = PlayerState.Stopped;

    private MultiPlayer _multiPlayer;

    private OnActionListener _onActionListener;

    private int _index = 0;

    private StationModel _data;

    private boolean _playerStarted;

    private NotificationManager _notificationManager;

    private NotificationCompat.Builder _notificationBuilder;

    private final int NOTIFICATION_ID = 1;

    private StationRepository _stationRepo = new StationRepository();

    private PendingIntent openIntent;

    private PendingIntent startIntent;

    private PendingIntent pauseIntent;

    private PendingIntent stopIntent;

    protected ImageHelper _imageHelper;


    @Override
    public void onCreate() {
        super.onCreate();
        _uiHandler = new Handler();
        _multiPlayer = new MultiPlayer();
        _notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        TelephonyManager _manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (_manager != null) {
            _manager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        _imageHelper = ImageHelper_.getInstance_(getApplicationContext());

        openIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                MainActivity_.class), PendingIntent.FLAG_UPDATE_CURRENT);

        startIntent = PendingIntent.getService(this, 1, new Intent(this,
                PlayerService.class).putExtra("State", 0), PendingIntent.FLAG_UPDATE_CURRENT);

        pauseIntent = PendingIntent.getService(this, 2, new Intent(this,
                PlayerService.class).putExtra(
                "State", 1), PendingIntent.FLAG_UPDATE_CURRENT);

        stopIntent = PendingIntent.getService(this, 3, new Intent(this,
                PlayerService.class).putExtra("State", 2), PendingIntent.FLAG_CANCEL_CURRENT);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            Log.d("ICY", "ADDED");
            java.net.URL.setURLStreamHandlerFactory(new java.net.URLStreamHandlerFactory() {
                public java.net.URLStreamHandler createURLStreamHandler(String protocol) {
                    if ("icy".equals(protocol))
                        return new com.spoledge.aacdecoder.IcyURLStreamHandler();
                    return null;
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (intent != null && intent.getExtras() != null) {

            switch (intent.getIntExtra("State", -1)) {
                case 0:
                    if (_data != null)
                        playStation(_data);
                    break;
                case 1:
                    pause();
                    break;
                case 2:
                    stop();
                    break;
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);

    }

    public void playStation(StationModel data) {
        if (_data != null && _data.Id.equals(data.Id) && getState().equals(PlayerState.Playing)) {
            return;
        }
        _index = 0;
        _data = data;
        start();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(NetworkChangeReceiver.NETWORK_KEY, NetworkingHelper.isNetworkAvailable(this)).commit();
    }

    private void start() {
        if (_onActionListener != null)
            _onActionListener.onIndexChange(_index, _data.Urls.size());
        stop();
        setState(PlayerState.Playing);
        _multiPlayer.setPlayerCallback(this);
        _multiPlayer.setMetadataEnabled(true);
        _multiPlayer.playAsync(_data.Urls.get(_index).Url);

        _notificationBuilder = new NotificationCompat.Builder(this);
        _notificationBuilder
                .setOngoing(true)
                .setContentIntent(openIntent)
                .setContentTitle(_data.Name)
                .setContentText(_data.Description)
                .addAction(R.drawable.icon_pause, getResources().getString(R.string.pause), pauseIntent)
                .addAction(R.drawable.icon_stop, getResources().getString(R.string.stop), stopIntent)
                .setSmallIcon(R.drawable.radioactivedark);

        sendNotification();
        _imageHelper.setOnLoadListener(this);
        _imageHelper.getImage(_data.IconUrl);
    }

    public void pause() {
        setState(PlayerState.Paused);
        if (_multiPlayer != null) {
            _multiPlayer.stop();
            _notificationBuilder = new NotificationCompat.Builder(this);
            _notificationBuilder
                    .setOngoing(true)
                    .setContentIntent(openIntent)
                    .setContentTitle(_data.Name)
                    .setContentText(_data.Description)
                    .addAction(R.drawable.icon_play, getResources().getString(R.string.play), startIntent)
                    .addAction(R.drawable.icon_stop, getResources().getString(R.string.stop), stopIntent)
                    .setSmallIcon(R.drawable.radioactivedark);
            sendNotification();
        }
    }

    public void stop() {
        setState(PlayerState.Stopped);
        if (_multiPlayer != null) {
            _multiPlayer.stop();
            _notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent ıntent) {
        return mBinder;
    }

    public PlayerState getState() {
        return _state;
    }

    public void setState(PlayerState state) {
        _state = state;
    }

    @Override
    public void playerStarted() {
        _uiHandler.post(new Runnable() {
            @Override
            public void run() {
                _playerStarted = true;
                if (_onActionListener != null)
                    _onActionListener.onPlayerStarted(_playerStarted);

            }
        });


    }

    @Override
    public void playerPCMFeedBuffer(final boolean isPlaying, final int i, final int i2) {
        _uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (_onActionListener != null) {
                    _onActionListener.onBufferChange(i, i2);
                    _onActionListener.onPlayerChange(isPlaying);
                }
                if (isPlaying)
                    _playerStarted = false;

            }
        });
    }

    @Override
    public void playerStopped(int i) {

    }

    @Override
    public void playerException(final Throwable throwable) {
        if (getState().equals(PlayerState.Playing)) {
            try {
                stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        _stationRepo.error(_data.Urls.get(_index).Id);
        _uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (_data.Urls.size() > (_index + 1)) {
                    _index++;
                    start();
                } else {
                    if (_onActionListener != null)
                        _onActionListener.onChannelFailure();
                }

            }
        });
    }


    @Override
    public void playerMetadata(String s, final String s2) {
        if ("StreamTitle".equals(s) && !s2.equals("")) {
            _data.Description = s2;
            _uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (_onActionListener != null)
                        _onActionListener.onTitleChanged(s2);
                    _notificationBuilder.setContentText(s2);
                    sendNotification();
                }
            });
        } else if ("StreamTitle".equals(s) && s2.equals("")) {
            _uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (_onActionListener != null)
                        _onActionListener.onTitleChanged(_data.Description);
                    _notificationBuilder.setContentText(_data.Description);
                    sendNotification();
                }
            });
        }

    }

    @Override
    public void playerAudioTrackCreated(AudioTrack audioTrack) {

    }

    @Override
    public void onLoad(Bitmap image) {
        _notificationBuilder.setLargeIcon(image);
        sendNotification();
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();

    }

    public class LocalBinder extends Binder {
        public PlayerService getServerInstance() {
            return PlayerService.this;
        }
    }

    public void setOnActionListener(OnActionListener onActionListener) {
        this._onActionListener = onActionListener;
    }

    public interface OnActionListener {
        public void onTitleChanged(String title);

        public void onBufferChange(int i, int i2);

        public void onPlayerChange(boolean isPlaying);

        public void onPlayerStarted(boolean playerStarted);

        public void onIndexChange(int current, int size);

        public void onChannelFailure();
    }

    public StationModel getData() {
        return _data;
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (getState().equals(PlayerState.Playing))
                    suspend();
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                if (_data != null && getState().equals(PlayerState.Suspended))
                    playStation(_data);
            } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                if (getState().equals(PlayerState.Playing))
                    suspend();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

    private void suspend() {
        setState(PlayerState.Suspended);
        if (_multiPlayer != null) {
            _multiPlayer.stop();
            _notificationBuilder = new NotificationCompat.Builder(this);
            _notificationBuilder
                    .setOngoing(true)
                    .setContentIntent(openIntent)
                    .setContentTitle(_data.Name)
                    .setContentText(_data.Name)
                    .addAction(R.drawable.icon_play, "", startIntent)
                    .addAction(R.drawable.icon_stop, "", stopIntent)
                    .setSmallIcon(R.drawable.radioactivedark);
            sendNotification();
        }
    }

    private void sendNotification() {
        _notificationManager.notify(NOTIFICATION_ID, _notificationBuilder.build());
    }

    public void setNotificationLargeIcon(Bitmap icon) {
        _notificationBuilder.setLargeIcon(icon);
        sendNotification();
    }
}

