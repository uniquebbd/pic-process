package com.dengb.facepp;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.dengb.faceplushttp.FaceppParseException;
import com.dengb.faceplushttp.HttpRequests;
import com.dengb.faceplushttp.PostParameters;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * face++人脸识别
 */
public class FaceppDetect {
    /**
     * face++ 识别需要用到的key和secret 需要自己去face++申请
     */
    private final String faceplus_key = "2b7a8830ea9a61a23619604ac8624124";
    private final String faceplus_secret = "XL1F-szYApAWGwp46Q2KzFTsvxiuB1SN ";
    DetectCallback callback = null;

    public void setDetectCallback(DetectCallback detectCallback) {
        callback = detectCallback;
    }

    public void detect(final Bitmap img) {
        new Thread(new Runnable() {
            public void run() {
                HttpRequests httpRequests = new HttpRequests(faceplus_key, faceplus_secret, true, false);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                float scale = Math.min(1, Math.min(600f / img.getWidth(), 600f / img.getHeight()));
                Matrix matrix = new Matrix();
                matrix.postScale(scale, scale);

                Bitmap imgSmall = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);

                imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] array = stream.toByteArray();

                try {
                    //detect
                    JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(array));
                    //finished , then call the callback function
                    if (callback != null) {
                        callback.detectResult(result);
                    }
                } catch (FaceppParseException e) {
                    e.printStackTrace();
                    callback.detectResult(null);
                }
            }
        }).start();
    }
}
