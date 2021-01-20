package com.occ.occpingtester.Testers;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.occ.occpingtester.DeviceMetrics.DeviceStatusMetric;
import com.occ.occpingtester.Firebase.FirebaseChannelClient;
import com.occ.occpingtester.Firebase.FirebaseChannelClientFactory;
import com.occ.occpingtester.MainActivity;
import com.occ.occpingtester.Multicast.MulticastChannelClient;
import com.occ.occpingtester.Multicast.MulticastChannelClientFactory;

public class TesterPingPong extends TesterBase {
    private FirebaseChannelClient fireBaseClient;

    public TesterPingPong(String ip, int port, MulticastChannelClientFactory clientFactory, MainActivity parentActivity) {
        super(ip, port, clientFactory, parentActivity);
        fireBaseClient = new FirebaseChannelClientFactory().GetClient(parentActivity);
    }



    //TODO: This might be reusable with code above, the test and client mode has some overlap
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Start()
    {
        MulticastChannelClient client = _clientFactory.GetClient(_ip, _port);
        client.StartReceiver((m) -> { _parentActivity.runOnUiThread(() -> ProcessReceivedInformationMulticast(_ip, _port, m)); return ""; } );
        fireBaseClient.SubscribeToFirebasePingChannel((String message) -> { _parentActivity.runOnUiThread(() -> ProcessReceivedInformationFirebase(message)); return ""; });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ProcessReceivedInformationMulticast(String ip, int port, String multiCastMsg)
    {
        UpdateLastBeat(true);
        if(multiCastMsg.startsWith(MainActivity.PongTAG))
        {
            Log.d(TAG, "This is a pong, so ignore here!");
        }
        else if(multiCastMsg.startsWith(MainActivity.PingTAG))
        {
            ClearMessageOutput();
            AppendMessageOutput("(MULTICAST) Received: " + multiCastMsg);
            String mcString = ComposeReply(multiCastMsg);
            AppendMessageOutput("(MULTICAST) Reply: " + mcString);
            _clientFactory.GetClient(ip, port).BroadcastInformation(mcString);
        }
        else
        {
            AppendMessageOutput("Invalid ping received: " + multiCastMsg);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ProcessReceivedInformationFirebase(String message) {

        if(message.startsWith(MainActivity.PingTAG)) {
            UpdateLastBeat(false);
            ClearMessageOutput();
            String replyMessage = ComposeReply(message);
            AppendMessageOutput("(Firebase) Received: " + message);
            AppendMessageOutput("(Firebase) Reply: " + replyMessage);
            fireBaseClient.RegisterPongOnFirebaseChannel(replyMessage);
        }
        else {
            Log.d(TAG, "Invalid Multicast detail received: " + message);
        }
    }

    private String ComposeReply(String incomingPing)
    {
        String res =  MainActivity.PongTAG + incomingPing.substring(7) + "|" + _parentActivity.AndroidID;
        DeviceStatusMetric metric = DeviceStatusMetric.Companion.build(_parentActivity.getApplicationContext());
        res +=  "|" + metric.getCellularType() +
                "|" + metric.getNetworkOperatorName() +
                "|" + metric.getCellularSignalStrength()  +
                "|" + metric.getWifiSSID() +
                "|" + metric.getWifiSignalStrength() +
                "|" + metric.getBatteryPercentage() +
                "|" + metric.getAudioVolumePercentage();
        return res;
    }
}
