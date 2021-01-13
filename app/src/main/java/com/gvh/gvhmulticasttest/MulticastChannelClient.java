package com.gvh.gvhmulticasttest;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MulticastChannelClient {
    private static final String TAG = "MCChannelClient";

    private String _ip;
    private int _port;
    private boolean _running;

    public String GetIP() { return _ip; }
    public int GetPort() { return _port; }

    public MulticastChannelClient(String ip, int port)
    {
        _ip = ip;
        _port = port;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void StartReceiver(Function<String, String> messageReceiveHandler)
    {
        if(_running) { Log.d(TAG, "Already running, ignoring request"); return;}
        new Thread() {
            public void run() {
                MulticastSocket socket = null;
                InetAddress group = null;
                //TODO: This code will open 2 event listeners when executed twice, there should be more sane handling of logic/duplication, but for now meh.
                //TODO: Having a double message is not that much of a hassle
                try
                {
                    socket = new MulticastSocket(_port);
                    group = InetAddress.getByName(_ip);
                    socket.joinGroup(group);
                    _running = true;
                    DatagramPacket packet;
                    while (true){
                        byte[] buf = new byte[256];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        Log.d(TAG, "Received message on:" + _ip + ":" + _port);
                        String str = new String(buf, StandardCharsets.UTF_8).substring(0, packet.getLength());
                        messageReceiveHandler.apply(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                    _running = false;
                } finally {
                    _running = false;
                    if (socket != null) {
                        try {
                            if (group != null) {
                                socket.leaveGroup(group);
                            }
                            socket.close();
                        }
                        catch(IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                    Log.d(TAG, "MC Thread dead yo!");
                    messageReceiveHandler.apply("Failed MC retrieve on: " + _ip + ":" + _port + ", Please restart!"); //TODO: handle the rest and handle already running state
                }
            }
        }.start();
    }

    public void StopReceiver()
    {

    }

    public void BroadcastInformation(String message)
    {
        //TODO: Make a bit nicer!, no re-use now..
        new Thread() {
            public void run() {
                Log.d(TAG, "Broadcasting: " + message);

                MulticastSocket socket = null;
                InetAddress group = null;
                try {
                    Log.d(TAG, "Begin send message");
                    socket = new MulticastSocket(_port);
                    group = InetAddress.getByName(_ip);
                    socket.joinGroup(group);
                    DatagramPacket packet;

                    byte[] buf = message.getBytes();
                    packet = new DatagramPacket(buf, buf.length, group, _port); //For sending you need to have a group and port set in the packet
                    Log.d(TAG, "Message length: " + buf.length);
                    socket.send(packet);
                    Log.d(TAG, "Message was sent!");


                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());

                } finally {

                    if (socket != null) {
                        try {
                            if (group != null) {
                                socket.leaveGroup(group);
                            }
                            socket.close();
                        } catch (IOException e) {
                            Log.d(TAG, e.getMessage());
                        }
                    }
                }
            }
        }.start();
    }
}
