package com.occ.occpingtester.Firebase;

import com.occ.occpingtester.MainActivity;
import com.occ.occpingtester.Multicast.MulticastChannelClient;

public class FirebaseChannelClientFactory {

    private static FirebaseChannelClient client;
    public FirebaseChannelClient GetClient(MainActivity parentActivity)
    {
        if(client == null) {
            client = new FirebaseChannelClient(parentActivity);
        }
        else {
            //We are going to reuse the item, so terminate all requests
            client.StopEventHandling();
        }

        return client;
    }

    public void CloseClients()
    {
        if (client != null) {
            client.StopEventHandling();
        }
    }
}
