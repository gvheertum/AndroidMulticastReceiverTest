package com.gvh.gvhmulticasttest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;
import android.view.View;

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

    public static final String TAG = "OCG";
    TextView tv = null;
    TextView tvReceivedContent = null;
    @Override
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fabListen = findViewById(R.id.fabObserve);
        FloatingActionButton fabTestMode = findViewById(R.id.fabTestMode);
        tvReceivedContent = findViewById(R.id.tvReceived);
        tv = findViewById(R.id.tvMcDetail);
        tv.setText("Click the button to start MC retrieve");
        createNotificationChannel();

        fabListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartMCChannel(view);
            }
        });

        fabTestMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartTestMode(view);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void StartMCChannel(View view)
    {
        if(view != null) {
            Snackbar.make(view, "Starting MC", Snackbar.LENGTH_LONG).show();
        }
        Log.d(TAG, "Starting receiver from button press");
        StartReceiver();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void StartTestMode(View view)
    {
        if(view != null) {
            Snackbar.make(view, "Starting test mode", Snackbar.LENGTH_LONG).show();
        }
        Log.d(TAG, "Starting receiver from button press");
        StartReceiver();
    }

    private final String CHANNEL_ID = "OCCChan";
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



    private final String MCAddressRf = "236.99.250.121";
    private final String MCAddressGeneral = "236.99.250.120";
    private final int MCPort = 30011;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void StartReceiver()
    {
        StartListeningForMCTraffic(MCAddressRf, MCPort);
        StartListeningForMCTraffic(MCAddressGeneral, MCPort);
        runOnUiThread(() -> tv.setText("!! Started MC retrieve on: " + MCAddressRf + " and " + MCAddressGeneral + "on port:" + MCPort));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void StartListeningForMCTraffic(String ip, int port)
    {
        MulticastChannelClient client = _clientFactory.GetClient(ip, port);
        client.StartReceiver((m) -> { runOnUiThread(() -> ProcessMulticastMessage(ip, port, m)); return "DONE"; } );
    }


    private void ProcessMulticastMessage(String ip, int port, String multiCastMsg)
    {
        String dispMessage = ip + ":" + port + "->" + multiCastMsg;
        Toast.makeText(MainActivity.this, dispMessage, Toast.LENGTH_SHORT).show();
        NotifyUser(dispMessage);
        AppendMCMessage(dispMessage);
    }

    private String messageContent = "";
    private void AppendMCMessage(String message)
    {
        messageContent = message + "\r\n" + messageContent;
        tvReceivedContent.setText(messageContent);
    }

    int notificationId = 0;
    private void NotifyUser(String message)
    {
        //Define sound URI
        //Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String urlToAlarm = "android.resource://" + getPackageName() + "/" + R.raw.sound1short;
        Log.d(TAG, urlToAlarm);
        Uri alarmSound = Uri.parse(urlToAlarm);
        Ringtone r = RingtoneManager.getRingtone(this.getApplicationContext(), alarmSound);
        r.play();

        //TODO: Somehow the sound of the notification is still the default messaging sound, so we had to improvise to get the sound working

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_foreground)
                .setContentTitle("Multicast received")
                .setContentText(message)
                //.setSound(Uri.parse(urlToAlarm))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);



        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
        notificationId++;
    }


}