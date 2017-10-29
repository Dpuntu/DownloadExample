package com.dpuntu.downloadexample;

import com.dpuntu.downloader.Logger;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created on 2017/10/27.
 *
 * @author dpuntu
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger {

    @Override
    public void log(String message) {
        Logger.i(message);
    }
}
