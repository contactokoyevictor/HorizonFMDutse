package ng.horizonfm.horizonfm;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener
{
    public static final String Broadcast_PLAY_NEW_AUDIO = "com.sivoteksolutions.horizonfm.PlayNewAudio";
    static final String url = "http://s7.voscast.com:10380";//"http://protostar.shoutca.st:8710";
    public static MediaPlayer player;
    public static ProgressBar spinner;
    public static Toast customtoast;
    public static MainActivity mInstance;
    public static String message;
    public static Boolean playing_status = false;
    public static Boolean isConnected = false;
    Dialog dialog;
    static Snackbar snackbar;
    static View sbView;
    static TextView textView;
    boolean serviceBound = false;
    FragmentManager fragmentManager;
    private MediaPlayerBackgroundService splayer;
    private HomeFragment fraghome = new HomeFragment();
    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;


    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MediaPlayerBackgroundService.LocalBinder binder = (MediaPlayerBackgroundService.LocalBinder) service;
            splayer = binder.getService();
            serviceBound = true;
            Log.i("msq", "Service Bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragment(new HomeFragment());
                    return true;
                case R.id.navigation_live:
                    if (mInstance.playing_status.equals(false)) {
                        Log.i("msq", "playing status 0:" + mInstance.playing_status);
                        mInstance_playaudio();
                        item.setIcon(R.drawable.ic_pause_play_24dp);
                        setFragment(new LiveFragment());
                        return true;
                    } else if (mInstance.playing_status.equals(true)) {
                        Log.i("msq", "playing status 1:" + mInstance.playing_status);
                        mInstance_stopMedia();
                        item.setIcon(R.drawable.ic_play_24dp);
                        setFragment(new LiveFragment());
                        return true;
                    } else {
                        if (item.getIcon().equals(R.drawable.ic_pause_play_24dp)) {
                            mInstance_playaudio();
                            item.setIcon(R.drawable.ic_play_24dp);
                            setFragment(new LiveFragment());
                            return true;
                        }

                        if (item.getIcon().equals(R.drawable.ic_play_24dp)) {
                            mInstance_stopMedia();
                            item.setIcon(R.drawable.ic_pause_play_24dp);
                            setFragment(new LiveFragment());
                            return true;
                        }
                    }
                case R.id.navigation_blogs:
                    setFragment(new BlogFragment());
                    return true;
                case R.id.navigation_more:
                    setFragment(new MoreFragment());
                    return true;
            }
            return false;
        }
    };

    public static synchronized void mInstance_playaudio() {
        mInstance.playAudio("http://s7.voscast.com:10380");
        mInstance.playing_status = true;

    }

    public static synchronized void mInstance_pauseMedia() {
        mInstance.pauseMedia();
        mInstance.playing_status = false;

    }

    public static synchronized void mInstance_stopMedia() {
        mInstance.stopMedia();
        mInstance.playing_status = false;

    }

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    public static synchronized MediaPlayer getPlayerInstance() {
        return player;
    }

    // Method to manually check connection status
    public static synchronized boolean chechConnection() {
        return ConnectivityReceiver.isConnected();
    }

    // Showing the status in Snackbar
    public static synchronized void showSnack(boolean isConnected) {
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.GREEN;
            textView.setText("Good! Connected to Internet");
            if (getPlayerInstance() != null && getPlayerInstance().isPlaying()) {
                getPlayerInstance().start();
            }

        } else {
            message = "Sorry! No Internet Connection";
            color = Color.RED;
            textView.setText("Sorry! No Internet Connection");
            if (getPlayerInstance() != null && getPlayerInstance().isPlaying()) {
                getPlayerInstance().stop();
            }
        }


        textView.setTextColor(color);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        snackbar.show();
    }

    public static synchronized void showSnack(boolean isConnected, String message) {

        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.GREEN;
            textView.setText("Good! Connected to Internet");
            if (getPlayerInstance() != null && getPlayerInstance().isPlaying()) {
                getPlayerInstance().start();
            }

        } else {
            color = Color.RED;
            textView.setText("Sorry! No Internet Connection");
            if (getPlayerInstance() != null && getPlayerInstance().isPlaying()) {
                getPlayerInstance().stop();
            }
        }
        textView.setTextColor(color);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;

        view.setLayoutParams(params);
        view.setBackgroundColor(Color.TRANSPARENT);
        snackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mInstance = this;
        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        Context context = getApplicationContext();
        LayoutInflater inflater = getLayoutInflater();

        snackbar = Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);
        sbView = snackbar.getView();
        textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.frame_container) != null) {
            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            fraghome.setArguments(getIntent().getExtras());
            // Add the fragment to the 'fragment_container' FrameLayout
            setFragment(fraghome);
        }

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.action_bar, null);

        addActionBar();
        TextView aboutText = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.aboutText);
        aboutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtclose;
                dialog.setContentView(R.layout.aboutus);
                txtclose = (TextView) dialog.findViewById(R.id.txtclose);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        dialog = new Dialog(this);

    }

    // class for being re-used by several instances
    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (getFragmentManager().findFragmentById(R.id.frame_container) == null) {
            fragmentTransaction.replace(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        } else {
            fragmentTransaction.add(R.id.frame_container, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        }
    }

    public void onLiveButtonClick(View view) {
        if (getPlayerInstance().isPlaying()) {

        }
    }

    private void addActionBar() {
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);
        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        Toolbar parent = (Toolbar) mCustomView.getParent();//first get parent toolbar of current action bar
        parent.setContentInsetsAbsolute(0, 0);// set padding programmatically to 0dp
        View v = getSupportActionBar().getCustomView();
        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        v.setLayoutParams(lp);
    }

    private void playAudio(String url) {
        //Check is service is active
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            splayer.stopSelf();
            playing_status = false;
            Intent playerIntent = new Intent(this, MediaPlayerBackgroundService.class);
            playerIntent.putExtra("url", url);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        if (!serviceBound) {
            Intent playerIntent = new Intent(this, MediaPlayerBackgroundService.class);
            playerIntent.putExtra("url", url);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            //Service is active
            //Send media with BroadcastReceiver
            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    private void playMedia() {
        //Check is service is active
        if (serviceBound) {
            splayer.playMedia();

        }
    }

    private void stopMedia() {
        //Check is service is active
        if (serviceBound) {
            splayer.stopMedia();

        }
    }

    private void pauseMedia() {
        //Check is service is active
        if (!serviceBound) {
            splayer.pauseMedia();
        }
    }

    private void resumeMedia() {
        //Check is service is active
        if (!serviceBound) {
            splayer.resumeMedia();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    // Method to manually check connection status
    private void checkConnection() {
        MainActivity.isConnected = ConnectivityReceiver.isConnected();
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //resumeMedia();
        // register connection status listener
        MainActivity.getInstance().setConnectivityListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            showSnack(isConnected, "Good! Connected to Internet");
        } else {
            showSnack(isConnected, "Sorry! No Internet Connection");
        }
        Log.i("msq", "Connection state is : " + isConnected);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            splayer.stopSelf();
            playing_status = false;
        }
    }
}
