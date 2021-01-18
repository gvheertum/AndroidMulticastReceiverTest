package com.gvh.gvhmulticasttest;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MulticastTestListener extends MulticastTestBase {
    private Uri _alarmSignal;

    public MulticastTestListener(String ip, int port, MulticastChannelClientFactory clientFactory, Uri alarmSignal,  MainActivity parentActivity) {
        super(ip, port, clientFactory, parentActivity);
        _alarmSignal = alarmSignal;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Start()
    {
        MulticastChannelClient client = _clientFactory.GetClient(_ip, _port);
        client.StartReceiver((m) -> { _parentActivity.runOnUiThread(() -> ProcessMulticastMessage(_ip, _port, m)); return "DONE"; } );
    }

    private void ProcessMulticastMessage(String ip, int port, String multiCastMsg)
    {
        UpdateLastBeat();
        String dispMessage = ip + ":" + port + "->" + multiCastMsg;
        Toast.makeText(_parentActivity, dispMessage, Toast.LENGTH_SHORT).show();
        NotifyUser(dispMessage);
        AppendMCMessage(dispMessage);
    }

    int notificationId = 0;
    private void NotifyUser(String message)
    {
        Ringtone r = RingtoneManager.getRingtone(_parentActivity.getApplicationContext(), _alarmSignal);
        r.play();

        //TODO: Somehow the sound of the notification is still the default messaging sound, so we had to improvise to get the sound working

        NotificationCompat.Builder builder = new NotificationCompat.Builder(_parentActivity, _parentActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_foreground)
                .setContentTitle("Multicast received")
                .setContentText(message)
                //.setSound(Uri.parse(urlToAlarm))
                .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(_parentActivity);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
        notificationId++;
    }
}
