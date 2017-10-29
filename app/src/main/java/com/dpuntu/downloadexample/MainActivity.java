package com.dpuntu.downloadexample;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dpuntu.downloader.DownloadManager;
import com.dpuntu.downloader.Downloader;
import com.dpuntu.downloader.Observer;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created on 2017/10/27.
 *
 * @author dpuntu
 */
public class MainActivity extends AppCompatActivity {
    private TextView showText;
    private Button startBtn, reStartBtn;
    private OkHttpClient client;
    private String taskId = "韩国美女";
    // 模拟断点续传
    private long mLoadedSize = 0, mTotalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showText = (TextView) findViewById(R.id.show);
        startBtn = (Button) findViewById(R.id.start_task);
        reStartBtn = (Button) findViewById(R.id.restart_task);
        // 建立okHttp请求
        client = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor(new HttpLogger())
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        // 多次点击会添加多个监听器，多次返回
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneStart();
            }
        });

        // startBtn 点了几次就会有几个finish，这个就和多界面管理一样
        reStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reStart();
            }
        });
    }

    private Downloader initDownloader() {
        return new Downloader.Builder()
                .client(client)
                .fileName("韩国美女.mov")
                .filePath(Environment.getExternalStorageDirectory().getPath() + "/downloader")
                .loadedSize(mLoadedSize)
                .totalSize(mTotalSize)
                .taskId(taskId)
                .url("http://180.97.173.129/video19.ifeng.com/video09/2017/07/26/5140187-102-009-232657" +
                             ".mp4?vid=147c1d18-31a9-4735-a3a7-2aab666a2c82&uid=1491562698492_xkoyi59885&from=v_Free&pver=vHTML5Player_v2.0" +
                             ".0&sver=&se=%E5%95%AA%E5%87%A0%E5%95%AA%E5%87%A0&cat=57-59&ptype=57&platform=pc&sourceType=h5&dt=1501082765000&gid=MeJV0V9d_Vax&sign=3b7baeced2627c2659ed75b6fae1aa5f" +
                             "&tm=1509110142642")
                .build();
    }

    private boolean isPause = false;

    private void reStart() {
        isPause = false;
        DownloadManager.getInstance().start(taskId);
    }

    private void oneStart() {
        // 将封装好的下载信息加载到下载队列
        DownloadManager.getInstance().addDownloader(initDownloader());
        // 开始下载，指定下载队列中的某个下载任务
        DownloadManager.getInstance().start(taskId);
        // 下载回调，监听下载的状态
        isPause = true;
        DownloadManager.getInstance().subjectTask(taskId, new StartObserver());
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    // 默认的返回是在子线程中
    private class StartObserver implements Observer {

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
            // 下载到2/3的时候，发出暂停指令
            if (loadedSize >= totalSize / 3 * 2 && isPause) {
                DownloadManager.getInstance().pause(taskId);
                return;
            }
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
            mTotalSize = totalSize;
            mLoadedSize = loadedSize;
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
            DownloadManager.getInstance().removeTaskObserver(taskId, this);
            // 最好不要在这里做这一步操作，因为你可能绑定了其他监听者，有一处移除，其他的均无法监听甚至出现异常
            // DownloadManager.getInstance().remove(taskId);
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
