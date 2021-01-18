package com.gvh.gvhmulticasttest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class MulticastTestPingTester extends MulticastTestBase {
    public MulticastTestPingTester(String ip, int port, MulticastChannelClientFactory clientFactory, MainActivity parentActivity) {
        super(ip, port, clientFactory, parentActivity);
    }



    //TODO: This might be reusable with code above, the test and client mode has some overlap
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void Start()
    {
        MulticastChannelClient client = _clientFactory.GetClient(_ip, _port);
        client.StartReceiver((m) -> { _parentActivity.runOnUiThread(() -> ProcessReceivedInformation(_ip, _port, m)); return "DONE"; } );
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void ProcessReceivedInformation(String ip, int port, String multiCastMsg)
    {
        UpdateLastBeat();
        if(multiCastMsg.startsWith("MCPONG|"))
        {
            Log.d(TAG, "This is a pong, so ignore here!");
        }
        else if(multiCastMsg.startsWith("MCPING|"))
        {
            ClearMCMessage();
            AppendMCMessage("Received: " + multiCastMsg);
            String mcString = ComposeReply(multiCastMsg);
            AppendMCMessage("Reply: " + mcString);
            _clientFactory.GetClient(ip, port).BroadcastInformation(mcString);
        }
        else
        {
            AppendMCMessage("Invalid ping received: " + multiCastMsg);
        }
    }

    private String ComposeReply(String incomingPing)
    {
        String res =  "MCPONG|" + incomingPing.substring(7) + "|" + _parentActivity.AndroidID;
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
