package com.gvh.gvhmulticasttest;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public MulticastChannelClientFactory _clientFactory = new MulticastChannelClientFactory();
    public static String AndroidID;
    public static final String TAG = "OCG";
    TextView tv = null;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {

        //TODO: Derp... Arrays :D
        String[] permToNeed = new String[1];
        permToNeed[0] = Manifest.permission.ACCESS_COARSE_LOCATION;
        requestPermissions(permToNeed,42);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitControlsAndServices();
    }

    private void InitControlsAndServices()
    {
        tv = findViewById(R.id.tvMcDetail);
        tv.setText("Click the button to start MC retrieve");
        createNotificationChannel();

        AndroidID = Secure.getString(this.getApplicationContext().getContentResolver(),
                Secure.ANDROID_ID);
        ((TextView)findViewById(R.id.tvDeviceId)).setText(AndroidID);

        MainActivity self = this;

        //Attach button events
        findViewById(R.id.fabObserve).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view != null) {
                    Snackbar.make(view, "Starting retrieve mode", Snackbar.LENGTH_LONG).show();
                }
                Log.d(TAG, "Starting receiver from button press");
                runOnUiThread(() -> tv.setText("!! Started retrieve mode"));
                new MulticastTestListener(GetMulticastIp(), GetMulticastPort(), _clientFactory, GetRingtoneUri(), self).Start();
            }
        });

        findViewById(R.id.fabTestMode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view != null) {
                    Snackbar.make(view, "Starting test mode", Snackbar.LENGTH_LONG).show();
                }
                Log.d(TAG, "Starting test mode");
                runOnUiThread(() -> tv.setText("!! Started test mode"));
                new MulticastTestPingTester(GetMulticastIp(), GetMulticastPort(), _clientFactory, self).Start();
            }
        });

        findViewById(R.id.fabStop).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(view != null) {
                    Snackbar.make(view, "Stopping processes", Snackbar.LENGTH_LONG).show();
                }
                Log.d(TAG, "Stopping processes");
                runOnUiThread(() -> tv.setText("!! Stopped"));
                _clientFactory.CloseClients();
            }
        });
    }


    public final String CHANNEL_ID = "OCCChan";
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name ="OCC Mobile Channel";
            String description = "OCC Mobile stuff channel";
            int importance = NotificationManager.IMPORTANCE_MAX;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //Lights music and action!
            AudioAttributes attributes = new AudioAttributes.Builder() //We need to have explicit actions to set a custom sound!
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            String urlToAlarm = "android.resource://" + getPackageName() + "/" + R.raw.sound1short;
            Log.d(TAG, urlToAlarm);
            Uri alarmSound = Uri.parse(urlToAlarm);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(alarmSound, attributes); // This is IMPORTANT

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    protected Uri GetRingtoneUri()
    {
        String urlToAlarm = "android.resource://" + getPackageName() + "/" + R.raw.sound1short;
        Log.d(TAG, urlToAlarm);
        Uri alarmSound = Uri.parse(urlToAlarm);
        return alarmSound;
    }


    private String GetMulticastIp()
    {
        EditText tf = findViewById(R.id.editMulticastIP);
        return tf.getText().toString();
    }

    private Integer GetMulticastPort()
    {
        EditText tf = findViewById(R.id.editMulticastPort);
        return Integer.parseInt(tf.getText().toString());
    }

}