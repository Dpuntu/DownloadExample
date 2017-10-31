package com.dpuntu.downloadexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dpuntu.downloader.DownloadManager;
import com.dpuntu.downloader.Observer;

/**
 * Created on 2017/10/30.
 *
 * @author dpuntu
 */

public class OtherActivity extends AppCompatActivity {
    private String taskId;
    private TextView showText;
    private Button startBtn, stopBtn;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_relist_item);
        showText = (TextView) findViewById(R.id.show);
        startBtn = (Button) findViewById(R.id.start);
        stopBtn = (Button) findViewById(R.id.stop);
        showText.setText(DownloadManager.getDownloader(taskId).getTaskId());
        if (getIntent() != null) {
            taskId = getIntent().getStringExtra("taskId");
        }
        if (taskId != null && !taskId.isEmpty()) {
            DownloadManager.subjectTask(taskId, new DownloadObserver());
        }
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.start(taskId);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.pause(taskId);
            }
        });

    }

    private class DownloadObserver implements Observer {

        @Override
        public void onCreate(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onCreate");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onCreate");
                }
            });
        }

        @Override
        public void onReady(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onReady");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onReady");
                }
            });
        }

        @Override
        public void onLoading(final String taskId, final String speed, final long totalSize, final long loadedSize) {
            Log.d(MainActivity.class.getName(), taskId + " onLoading , speed = " + speed
                    + " , totalSize = " + totalSize + " , loadedSize = " + loadedSize);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onLoading , speed = " + speed
                                             + " , totalSize = " + totalSize
                                             + " , loadedSize = " + loadedSize);
                }
            });
        }

        @Override
        public void onPause(final String taskId, final long totalSize, final long loadedSize) {
            Log.d(MainActivity.class.getName(), taskId + " onPause"
                    + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onPause" + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
                }
            });
        }

        @Override
        public void onFinish(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onFinish");
            DownloadManager.removeTaskObserver(taskId, this);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onFinish");
                }
            });
        }

        @Override
        public void onError(final String taskId, final String error, final long totalSize, final long loadedSize) {
            Log.e(MainActivity.class.getName(), taskId + " onError " + error
                    + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    showText.setText(taskId + " onError " + error + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
                }
            });
        }
    }
}
