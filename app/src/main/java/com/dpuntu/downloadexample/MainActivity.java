package com.dpuntu.downloadexample;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dpuntu.downloader.DownloadManager;
import com.dpuntu.downloader.Downloader;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created on 2017/10/27.
 *
 * @author dpuntu
 */
public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        // 设置可同时下载任务的数量
        DownloadManager.setCorePoolSize(3);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new DownloadAdapter(initDownloaders()));
    }

    private List<Downloader> initDownloaders() {
        List<Downloader> downloaders = new ArrayList<>();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .addNetworkInterceptor(
                        new HttpLoggingInterceptor(new HttpLogger())
                                .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        for (int i = 0; i <= 6; i++) {
            Downloader downloader = new Downloader.Builder()
                    .client(client)
                    .fileName("韩国美女" + i + ".mov")
                    .filePath(Environment.getExternalStorageDirectory().getPath() + "/downloader")
                    .taskId("韩国美女" + i)
                    .url("http://180.97.173.129/video19.ifeng.com/video09/2017/07/26/5140187-102-009-232657" +
                                 ".mp4?vid=147c1d18-31a9-4735-a3a7-2aab666a2c82&uid=1491562698492_xkoyi59885&from=v_Free&pver=vHTML5Player_v2.0" +
                                 ".0&sver=&se=%E5%95%AA%E5%87%A0%E5%95%AA%E5%87%A0&cat=57-59&ptype=57&platform=pc&sourceType=h5&dt=1501082765000&gid=MeJV0V9d_Vax&sign" +
                                 "=3b7baeced2627c2659ed75b6fae1aa5f" +
                                 "&tm=1509110142642")
                    .build();
            downloaders.add(downloader);
        }
        return downloaders;
    }
}
