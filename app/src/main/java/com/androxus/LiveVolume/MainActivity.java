package com.androxus.LiveVolume;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.Objects;

import app.com.sample.R;

public class MainActivity extends AppCompatActivity {

    SettingsContentObserver settingsContentObserver;
    SeekBar seekBar;//seekbar
    AudioManager audio;//audio manager


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsContentObserver = new SettingsContentObserver(this, new Handler());

        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.
                CONTENT_URI, true, settingsContentObserver);

        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


        seekBar=(SeekBar)findViewById(R.id.seekBar);
        seekBar.setProgress(currentVolume);
        seekBar.setMax(currentVolume);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, AudioManager.ADJUST_SAME);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    public class SettingsContentObserver extends ContentObserver {
        int previousVolume;
        Context context;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        SettingsContentObserver(Context c, Handler handler) {
            super(handler);
            context = c;
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            previousVolume =
                    Objects.requireNonNull(audio).getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            int currentVolume =
                    Objects.requireNonNull(audio).getStreamVolume(AudioManager.STREAM_MUSIC);
            int delta = previousVolume - currentVolume;
            if (delta > 0) {
                Toast.makeText(MainActivity.this, "Volume Decreased to "+currentVolume, Toast.LENGTH_SHORT).show();
                seekBar.setProgress(currentVolume);

                previousVolume = currentVolume;
            }
            else if (delta < 0) {
                Toast.makeText(MainActivity.this, "Volume Increased to "+currentVolume, Toast.LENGTH_SHORT).show();
                seekBar.setProgress(currentVolume);
                previousVolume = currentVolume;
            }
        }
    }
    @Override
    protected void onDestroy() {
        getApplicationContext().getContentResolver().unregisterContentObserver(settingsContentObserver);
        super.onDestroy();
    }
}