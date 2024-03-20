package com.guy.gonenapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RecyclerView panel_LST_channels;
    private Adapter_Channel adapterChannel;
    private ArrayList<Channel> channels = new ArrayList<>();

    private Random random = new Random();

    int SPEED = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        panel_LST_channels = findViewById(R.id.panel_LST_channels);

        for (int i = 0; i < 16; i++) {
            channels.add(new Channel().setTitle("Channel " + (i + 1)));
        }

        adapterChannel = new Adapter_Channel(channels);
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2);
        panel_LST_channels.setHasFixedSize(true);
        panel_LST_channels.setLayoutManager(layoutManager);
        panel_LST_channels.setAdapter(adapterChannel);

        (findViewById(R.id.panel_FAB_action)).setOnClickListener(v -> start());
    }

    private void tick() {
        Intent intent = new Intent(this, ServiceData.class);
        intent.setAction(ServiceData.ServiceData_ACTION_DATA);
        intent.putExtra(ServiceData.BATCH_SIZE, 1000 / SPEED);
        startService(intent);
    }

    private void start() {
        MCT6.get().cycle(new MCT6.CycleTicker() {
            @Override
            public void secondly(int repeatsRemaining) {
                tick();
            }

            @Override
            public void done() {
            }
        }, MCT6.CONTINUOUSLY_REPEATS, SPEED, "cycle");
        Intent intent = new Intent(this, ServiceData.class);
        intent.setAction(ServiceData.ServiceData_ACTION_START);
        startService(intent);
    }

    private void stop() {
        Intent intent = new Intent(this, ServiceData.class);
        intent.setAction(ServiceData.ServiceData_ACTION_STOP);
        startService(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(radio_tal, new IntentFilter(ServiceData.FM_99));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(radio_tal);
    }

    private BroadcastReceiver radio_tal = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }

            runOnUiThread(() -> {
                for (int i = 0; i < channels.size(); i++) {
                    float[] arr = intent.getFloatArrayExtra(ServiceData.DOWNLOAD_PROGRESS + i);
                    for (float v : arr) {
                        channels.get(i).getData().pollFirst();
                        channels.get(i).getData().addLast(v);
                    }
                }
                adapterChannel.notifyDataSetChanged();

//                for (Channel channel : channels) {
//                    for (int i = 0; i < 10; i++) {
//                        float f = random.nextFloat() * 500 - 250;
//                        f = channel.getData().getLast() + f;
//                        f = Math.max(f, 0);
//                        f = Math.min(f, 64000);
//                        channel.getData().pollFirst();
//                        channel.getData().add(f);
//                    }
//                }

            });
        }
    };
}