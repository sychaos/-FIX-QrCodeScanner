package com.kaola.qrcodescanner.qrcode.decode;

import android.os.Handler;

import com.google.zxing.Result;

/**
 * Created by cloudist on 2017/8/9.
 */

public interface DecodeListener {
    void decodeResult(Result result);

    Handler getCaptureActivityHandler();
}
