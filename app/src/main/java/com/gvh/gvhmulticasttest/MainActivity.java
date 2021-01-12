package com.gvh.gvhmulticasttest;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "OCG";
    public boolean startedMCChannel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d(TAG, "Starting receiver from button press");
                StartReceiver();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void StartReceiver()
    {
        if(this.startedMCChannel) { Log.d(TAG, "Already running"); return; }
        new Thread() {
            public void run() {
                MulticastSocket socket = null;
                InetAddress group = null;

                try {
                    socket = new MulticastSocket(30011);
                    group = InetAddress.getByName("236.99.250.121");
                    socket.joinGroup(group);

                    DatagramPacket packet;
                    while (true){
                        byte[] buf = new byte[256];
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);

                        String str = new String(buf, StandardCharsets.UTF_8).substring(0, packet.getLength());
                        // Java byte values are signed. Convert to an int so we don't have to deal with negative values for bytes >= 0x7f (unsigned).
                        Log.d(TAG, str);
                        String multiCastMsg = str;

                        runOnUiThread(() -> Toast.makeText(MainActivity.this, multiCastMsg, Toast.LENGTH_SHORT).show());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.getMessage());
                    startedMCChannel = false;
                } finally {
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
                    startedMCChannel = false;
                    Log.d(TAG, "MC Thread dead yo!");
                }
            }
        }.start();
        this.startedMCChannel = true;
    }

}