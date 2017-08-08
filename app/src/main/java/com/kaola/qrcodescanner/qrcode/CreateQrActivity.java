package com.kaola.qrcodescanner.qrcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.google.zxing.QRCodeEncoder;
import com.kaola.qrcodescanner.R;

/**
 * Created by cloudist on 2017/8/8.
 */

public class CreateQrActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_qr);

        final ImageView iv_english_logo = (ImageView) findViewById(R.id.iv_english_logo);

        new Runnable() {
            @Override
            public void run() {
                final Bitmap qr = QRCodeEncoder.syncEncodeQRCode("asbsdbsdbsusd", 500, Color.BLACK, Color.WHITE, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_english_logo.setImageBitmap(qr);
                    }
                });
            }
        }.run();

    }
}
