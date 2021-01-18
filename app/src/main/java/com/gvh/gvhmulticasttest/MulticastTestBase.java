package com.gvh.gvhmulticasttest;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MulticastTestBase {
    protected final MulticastChannelClientFactory _clientFactory;
    protected final int _port;
    protected final String _ip;
    private final TextView _outputChannel;
    protected static final String TAG = "OCG";
    protected final MainActivity _parentActivity;

    public MulticastTestBase(String ip, int port, MulticastChannelClientFactory clientFactory, MainActivity parentActivity)
    {
        _port = port;
        _ip = ip;
        _clientFactory = clientFactory;
        _outputChannel = parentActivity.findViewById(R.id.tvReceived);
        _parentActivity = parentActivity;

        //Kill all running threads:
        _clientFactory.CloseClients();
    }

    private String messageContent = "";
    protected void AppendMCMessage(String message)
    {
        messageContent = message + "\r\n" + messageContent;
        _outputChannel.setText(messageContent);
    }

    protected void ClearMCMessage()
    {
        messageContent = "";
        _outputChannel.setText(messageContent);
    }

    protected void UpdateLastBeat()
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        ((TextView)_parentActivity.findViewById(R.id.tvLastBeat)).setText(strDate);
    }
}
