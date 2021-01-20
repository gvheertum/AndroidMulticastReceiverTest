package com.occ.occpingtester.Testers;

import android.widget.TextView;

import com.occ.occpingtester.MainActivity;
import com.occ.occpingtester.Multicast.MulticastChannelClientFactory;
import com.occ.occpingtester.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TesterBase {
    protected final MulticastChannelClientFactory _clientFactory;
    protected final int _port;
    protected final String _ip;
    private final TextView _outputChannel;
    protected static final String TAG = "OCG";
    protected final MainActivity _parentActivity;

    public TesterBase(String ip, int port, MulticastChannelClientFactory clientFactory, MainActivity parentActivity)
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
    protected void AppendMessageOutput(String message)
    {
        messageContent = message + "\r\n" + messageContent;
        _outputChannel.setText(messageContent);
    }

    protected void ClearMessageOutput()
    {
        messageContent = "";
        _outputChannel.setText(messageContent);
    }

    protected void UpdateLastBeat(boolean primary)
    {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);

        ((TextView)_parentActivity.findViewById(primary ? R.id.tvLastBeat : R.id.tvLastBeatAlternate)).setText(strDate);
    }
}
