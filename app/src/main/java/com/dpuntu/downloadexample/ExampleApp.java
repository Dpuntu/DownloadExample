package com.dpuntu.downloadexample;

import android.app.Application;

import com.dpuntu.downloader.DownloadManager;

/**
 * Created on 2017/10/29.
 *
 * @author dpuntu
 */

public class ExampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化下载器，一般在 Application 中初始化一次即可
        DownloadManager.getInstance().initDownloader(this);
    }
}
