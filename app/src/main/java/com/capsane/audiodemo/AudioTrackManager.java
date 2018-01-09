package com.capsane.audiodemo;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;

/**
 * 播放录音文件
 * Created by capsane on 18-1-8.
 */

public class AudioTrackManager {

    private static AudioTrackManager mInstance;

    private static final String TAG = "AudioTrackManager";

    private AudioTrack mAudioTrack;

    private String path = "/sdcard/data/AudioDemo.pcm";

    private int bufferSize;

    private int offset;

    private PlayThread playThread;

    private boolean isPlaying = false;


    /**
     * singleton
     */
    private AudioTrackManager() {
        // AudioTrack
        bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);
    }

    public static AudioTrackManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioTrackManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioTrackManager();
                }
            }
        }
        return mInstance;
    }


    public void startPlay() {
        if (isPlaying) {
            Log.e(TAG, "已经开始播放了...");
        } else {
            isPlaying = true;
            mAudioTrack.play();
            playThread = new PlayThread();
            playThread.start();
        }
    }

    public void stopPlay() {
        if (!isPlaying) {
            Log.e(TAG, "还没有播放!!!");
        } else {
            isPlaying = false;
            mAudioTrack.stop();
        }
    }


    class PlayThread extends Thread {
        byte[] data1=new byte[bufferSize*2];
        File file=new File(path);
        int off1=0;
        FileInputStream fileInputStream;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while(isPlaying){
                try {
                    fileInputStream=new FileInputStream(file);
                    fileInputStream.skip((long)off1);
                    fileInputStream.read(data1,0,bufferSize*2);
                    off1 +=bufferSize*2;
                } catch (Exception e) {
                    // TODO: handle exception
                }
                mAudioTrack.write(data1, offset, bufferSize * 2);
            }
        }
    }


}
