package com.example.lorenzo.musicplayer;

// Example of MediaPlayer taken from:
// https://www.tutorialspoint.com/android/android_mediaplayer.htm
//
// Changes in GUI for Udacity exercise

/*
    Music license notice:
    "Menuettos 1 & 2 from 41st Symphony – Mozart "
    (freemusicpublicdomain.com)
    Licensed under Creative Commons: By Attribution 3.0
    http://creativecommons.org/licenses/by/3.0/
*/

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static boolean oneTimeOnly = false;
    private int maxVolume;
    private int currentVolume;
    private SeekBar seekbar;
    private Button playButton;
    private Button pauseButton;
    private Button backwardButton;
    private Button forwardButton;
    private Button volumeDownButton;
    private Button volumeUpButton;
    private MediaPlayer mediaPlayer;
    private double startTime = 0;
    private double finalTime = 0;
    private Handler myHandler = new Handler();
    private int forwardTime = 5000; // ms
    private int backwardTime = 5000; // ms
    private TextView startTimeTextView;
    private TextView finalTimeTextView;
    private TextView songName;
    private TextView volumeTexView;
    private TextView maxVolumeTexView;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            startTimeTextView.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        initControls();

    }

    private void findViews() {
        songName = (TextView) findViewById(R.id.somgname);
        startTimeTextView = (TextView) findViewById(R.id.starttime_textview);
        finalTimeTextView = (TextView) findViewById(R.id.finaltime_textview);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        playButton = (Button) findViewById(R.id.play);
        pauseButton = (Button) findViewById(R.id.pause);
        backwardButton = (Button) findViewById(R.id.backward);
        forwardButton = (Button) findViewById(R.id.forward);
//        volumeUpButton = (Button) findViewById(R.id.volumeup);
//        volumeDownButton = (Button) findViewById(R.id.volumedown);
        volumeSeekbar = (SeekBar) findViewById(R.id.volumeSeekBar);
        volumeTexView = (TextView) findViewById(R.id.volume_textview);
        maxVolumeTexView = (TextView) findViewById(R.id.maxvolume_textview);
    }

    private void initControls() {

        mediaPlayer = MediaPlayer.create(this, R.raw.menuettos_1_2_from_41st_symphony);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        songName.setText("Menuettos 1 & 2 from 41st Symphony – Mozart");
        seekbar.setClickable(false);

        playButton.setEnabled(true);
        pauseButton.setEnabled(false);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeTexView.setText(String.format("%s", currentVolume));

        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        maxVolumeTexView.setText(String.format("%s", maxVolume));

        volumeSeekbar.setMax(maxVolume);
        volumeSeekbar.setProgress(currentVolume);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(MainActivity.this, R.string.itsDone, Toast.LENGTH_SHORT).show();
                    }
                });

                finalTime = mediaPlayer.getDuration();
                startTime = mediaPlayer.getCurrentPosition();

                if (!oneTimeOnly) {
                    seekbar.setMax((int) finalTime);
                    oneTimeOnly = true;
                }

                finalTimeTextView.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        finalTime)))
                );

                startTimeTextView.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                        startTime)))
                );

                seekbar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                playButton.setEnabled(false);
                pauseButton.setEnabled(true);
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
                mediaPlayer.pause();
                pauseButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp + forwardTime) <= finalTime) {
                    startTime = startTime + forwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = (int) startTime;

                if ((temp - backwardTime) > 0) {
                    startTime = startTime - backwardTime;
                    mediaPlayer.seekTo((int) startTime);
                    Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);

                currentVolume = progress;
                volumeTexView.setText(String.format("%s", currentVolume));

                maxVolumeTexView.setText(String.format("%s", maxVolume));
            }
        });


    }
}
