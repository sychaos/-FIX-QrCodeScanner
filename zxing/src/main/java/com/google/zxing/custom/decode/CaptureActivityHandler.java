/*
 * Copyright (C) 2008 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.google.zxing.custom.decode;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.R;
import com.google.zxing.Result;
import com.google.zxing.custom.camera.CameraManager;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getName();

    public static final int AUTO_FOCUS = 1001;
    public static final int DECODE_SUCCEEDED = 1002;
    public static final int DECODE_FAILED = 1003;
    public static final int DECODE = 1004;

    public static final int ENCODE_FAILED = 1005;
    public static final int ENCODE_SUCCEEDED = 1006;

    public static final int QUIT = 1007;
    public static final int RESTART_PREVIEW = 1008;


    private final DecodeListener decodeListener;
    private final DecodeThread mDecodeThread;
    private State mState;

    public CaptureActivityHandler(DecodeListener decodeListener) {
        this.decodeListener = decodeListener;
        mDecodeThread = new DecodeThread(decodeListener);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case AUTO_FOCUS:
                // Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
                if (mState == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, AUTO_FOCUS);
                }
                break;
            case DECODE_SUCCEEDED:
                Log.e(TAG, "Got decode succeeded message");
                mState = State.SUCCESS;
                decodeListener.decodeResult((Result) message.obj);
                break;
            case DECODE_FAILED:
                // We're decoding as fast as possible, so when one decode fails, start another.
                mState = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), DECODE);
                break;
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        Message quit = Message.obtain(mDecodeThread.getHandler(), QUIT);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(DECODE_SUCCEEDED);
        removeMessages(DECODE_FAILED);
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            CameraManager.get().startPreview();
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), DECODE);
            CameraManager.get().requestAutoFocus(this, AUTO_FOCUS);
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }
}