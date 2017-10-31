package com.dpuntu.downloadexample;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dpuntu.downloader.DownloadManager;
import com.dpuntu.downloader.Downloader;
import com.dpuntu.downloader.Logger;
import com.dpuntu.downloader.Observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2017/10/30.
 *
 * @author dpuntu
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private List<Downloader> mDownloaders = new ArrayList<>();

    public DownloadAdapter(List<Downloader> mDownloaders) {
        this.mDownloaders = mDownloaders;
        // 将封装好的下载信息加载到下载队列
        Logger.e(mDownloaders.size() + "");
        DownloadManager.addDownloader(mDownloaders);
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ExampleApp.getApp()).inflate(R.layout.act_relist_item, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DownloadViewHolder holder, int position) {
        final Downloader downloader = mDownloaders.get(position);

        holder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.start(downloader.getTaskId());
            }
        });

        holder.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager.pause(downloader.getTaskId());
            }
        });

        DownloadManager.subjectTask(downloader.getTaskId(), new DownloadObserver(holder.showText));
    }

    @Override
    public int getItemCount() {
        return mDownloaders.size();
    }

    class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView showText;
        Button startBtn, stopBtn;

        DownloadViewHolder(View itemView) {
            super(itemView);
            showText = (TextView) itemView.findViewById(R.id.show);
            startBtn = (Button) itemView.findViewById(R.id.start);
            stopBtn = (Button) itemView.findViewById(R.id.stop);
        }
    }

    private class DownloadObserver implements Observer {
        TextView mTextView;

        DownloadObserver(TextView mTextView) {
            this.mTextView = mTextView;
        }

        @Override
        public void onCreate(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onCreate");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(taskId + " onCreate");
                }
            });
        }

        @Override
        public void onReady(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onReady");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(taskId + " onReady");
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
                    mTextView.setText(taskId + " onLoading , speed = " + speed
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
                    mTextView.setText(taskId + " onPause" + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
                }
            });
        }

        @Override
        public void onFinish(final String taskId) {
            Log.e(MainActivity.class.getName(), taskId + " onFinish");
            DownloadManager.removeTaskObserver(taskId, this);
            // 最好不要在这里做这一步操作，因为你可能绑定了其他监听者，有一处移除，其他的均无法监听甚至出现异常
            //  DownloadManager.remove(taskId);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(taskId + " onFinish");
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
                    mTextView.setText(taskId + " onError " + error + " totalSize = " + totalSize + " loadedSize = " + loadedSize);
                }
            });
        }
    }
}
