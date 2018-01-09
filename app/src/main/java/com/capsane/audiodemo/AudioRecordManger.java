package com.capsane.audiodemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author capsane
 * Created by capsane on 18-1-8.
 */

public class AudioRecordManger {

    private static final String TAG = "AudioRecordManager";

    private AudioRecord mAudioRecord;

    private DataOutputStream dataOutputStream;

    private Thread recordThread;

    private boolean isStart = false;

    /**
     * Singleton
     */
    private static AudioRecordManger mInstance;

    private int bufferSize;

    private AudioRecordManger() {
        // CHANNEL_IN_MONO
        bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
    }


    public static AudioRecordManger getInstance() {
        if (mInstance == null) {
            synchronized (AudioRecordManger.class) {
                if (mInstance == null) {
                    mInstance = new AudioRecordManger();
                }
            }
        }
        return mInstance;
    }

    /**
     * 销毁线程
     *
     */
    private void destroyThread() {
        try {
            isStart = false;
            if (recordThread != null && Thread.State.RUNNABLE == recordThread.getState()) {
                try {
                    Thread.sleep(500);
                    recordThread.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                    recordThread = null;
                }
            }
            recordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动录音线程
     *
     */
    private void startThread() {
        destroyThread();
        isStart = true;
        if (recordThread == null) {
            recordThread = new Thread(recordRunnable);
            recordThread.start();
        }
    }

    /**
     * 录音线程
     */
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
                int bytesRecord;
                int bufferSize = 320;
                byte[] tempBuffer = new byte[bufferSize];

                if (mAudioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    stopRecord();
                    return;
                }
                mAudioRecord.startRecording();
                while (isStart) {
                    bytesRecord = mAudioRecord.read(tempBuffer, 0, bufferSize);
                    if (bytesRecord == AudioRecord.ERROR_INVALID_OPERATION || bytesRecord == AudioRecord.ERROR_BAD_VALUE) {
                        continue;
                    }
                    if (bytesRecord != 0 && bytesRecord != -1) {
                        dataOutputStream.write(tempBuffer, 0, bytesRecord);
                    } else {
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    /**
     * 保存文件
     */
    private void setPath(String path) throws Exception {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        dataOutputStream = new DataOutputStream(new FileOutputStream(file, true));
        Log.e(TAG, "file is: " + path);
    }

    /**
     * 启动录音
     */
    public void startRecord(String path) {
        if (isStart) {
            Log.e(TAG, "已经在录音了...");
            return;
        }

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize*2);

        try {
            setPath(path);
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        if (!isStart) {
            Log.e(TAG, "还没开始录音...");
            return;
        }
        try {
            destroyThread();
            if (mAudioRecord != null) {
                if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    mAudioRecord.stop();
                }
                if (mAudioRecord != null) {
                    mAudioRecord.release();     // 释放资源，无法继续使用
                    mAudioRecord = null;
                }
            }
            if (dataOutputStream != null) {
                dataOutputStream.flush();
                dataOutputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
