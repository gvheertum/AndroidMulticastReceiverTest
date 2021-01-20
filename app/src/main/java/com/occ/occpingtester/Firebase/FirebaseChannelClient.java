package com.occ.occpingtester.Firebase;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.occ.occpingtester.Entities.FirebasePingMessage;
import com.occ.occpingtester.MainActivity;

import java.util.function.Function;

public class FirebaseChannelClient {

    public final String TAG = "FirebaseChannel";
    private final MainActivity mainActivity;
    private final FirebaseDatabase firebaseDatabase;
    private final DatabaseReference dbReferencePing;
    private final DatabaseReference dbReferencePong;
    private ChildEventListener activeEventListener;
    public FirebaseChannelClient(MainActivity parentActivity)
    {
        this.mainActivity = parentActivity;
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbReferencePing =  firebaseDatabase.getReference("ping");
        dbReferencePong =  firebaseDatabase.getReference("pong");
    }

    public void SubscribeToFirebasePingChannel(Function<String,String> callbackFunction)
    {
        activeEventListener = new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                try {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    FirebasePingMessage message = dataSnapshot.getValue(FirebasePingMessage.class);
                    Log.d(TAG, "child[" + dataSnapshot.getKey() + "]:" + message.getPingIdentifier());

                    //Perform callback
                    callbackFunction.apply("MCPING|" + message.getSessionIdentifier() + "|" + message.getPingIdentifier());
                } catch (Exception e) {
                  Log.d(TAG, "Error processing Firebase data: " + e.toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        dbReferencePing.addChildEventListener(activeEventListener);
    }

    public void StopEventHandling()
    {
        if(activeEventListener != null) {
            dbReferencePing.removeEventListener(activeEventListener);
            activeEventListener = null;
        }
    }

    public void RegisterPongOnFirebaseChannel(String responseMessage)
    {
        Log.d(TAG, "Writing " + responseMessage + " to firebase");
        dbReferencePong.child(responseMessage).child("data").setValue(responseMessage);
    }
}
