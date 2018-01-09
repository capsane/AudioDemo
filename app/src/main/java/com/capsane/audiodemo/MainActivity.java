package com.capsane.audiodemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AudioRecordManger mAudioRecordManger;

    private AudioTrackManager mAudioTrackManager;

/*
    private AudioRecord mAudioRecord;
    private AudioManager mAudioManager;
    private AudioTrack mAudioTrack;
*/



//    AudioSystem

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAudioRecordManger = AudioRecordManger.getInstance();

        mAudioTrackManager = AudioTrackManager.getInstance();

        Button buttonStartRecord = findViewById(R.id.button_start_record);
        Button buttonStopRecord = findViewById(R.id.button_stop_record);
        Button buttonStartPlay = findViewById(R.id.button_start_play);
        Button buttonStopPlay = findViewById(R.id.button_stop_play);
        buttonStartRecord.setOnClickListener(this);
        buttonStopRecord.setOnClickListener(this);
        buttonStartPlay.setOnClickListener(this);
        buttonStopPlay.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_record:
                mAudioRecordManger.startRecord("/sdcard/data/AudioDemo.pcm");
                break;

            case R.id.button_stop_record:
                mAudioRecordManger.stopRecord();
                break;

            case R.id.button_start_play:
                mAudioTrackManager.startPlay();
                break;
            case R.id.button_stop_play:
                mAudioTrackManager.stopPlay();
                break;

            default:
                break;

        }
    }
}
