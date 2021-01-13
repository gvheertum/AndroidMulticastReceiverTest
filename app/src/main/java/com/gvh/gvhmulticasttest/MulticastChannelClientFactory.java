package com.gvh.gvhmulticasttest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MulticastChannelClientFactory
{
    public ArrayList<MulticastChannelClient> _multicastClients = new ArrayList<MulticastChannelClient>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MulticastChannelClient GetClient(String ip, int port)
    {
        Log.d("MCClientFactory", "Looking for client: " + ip + ":" + port);
        AtomicReference<MulticastChannelClient> matched = new AtomicReference<MulticastChannelClient>();
        _multicastClients.forEach((e) -> { if(e.GetIP() == ip && e.GetPort() == port) { matched.set(e);} });
        if(matched.get() != null)
        {
            Log.d("MCClientFactory", "Found cache hit");
            return matched.get();
        }

        Log.d("MCClientFactory", "Creating new");
        MulticastChannelClient newItem = new MulticastChannelClient(ip, port);
        _multicastClients.add(newItem);
        return newItem;
    }
}
