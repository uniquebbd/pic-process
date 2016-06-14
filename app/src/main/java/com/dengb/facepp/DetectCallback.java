package com.dengb.facepp;

import org.json.JSONObject;

public interface DetectCallback {
    void detectResult(JSONObject rst);
}