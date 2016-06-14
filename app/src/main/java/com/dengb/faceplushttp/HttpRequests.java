package com.dengb.faceplushttp;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class HttpRequests {
    private static final String WEBSITE_CN = "https://apicn.faceplusplus.com/v2/";
    private static final String DWEBSITE_CN = "http://apicn.faceplusplus.com/v2/";
    private static final String WEBSITE_US = "https://apius.faceplusplus.com/v2/";
    private static final String DWEBSITE_US = "http://apius.faceplusplus.com/v2/";
    private static final int BUFFERSIZE = 1048576;
    private static final int TIMEOUT = 30000;
    private static final int SYNC_TIMEOUT = 60000;
    private String webSite;
    private String apiKey;
    private String apiSecret;
    private PostParameters params;
    private int httpTimeOut = 30000;

    public void setHttpTimeOut(int timeOut) {
        this.httpTimeOut = timeOut;
    }

    public int getHttpTimeOut() {
        return this.httpTimeOut;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiSecret() {
        return this.apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setWebSite(boolean isCN, boolean isDebug) {
        if(isCN && isDebug) {
            this.webSite = "http://apicn.faceplusplus.com/v2/";
        } else if(isCN && !isDebug) {
            this.webSite = "https://apicn.faceplusplus.com/v2/";
        } else if(!isCN && isDebug) {
            this.webSite = "http://apius.faceplusplus.com/v2/";
        } else if(!isCN && !isDebug) {
            this.webSite = "https://apius.faceplusplus.com/v2/";
        }

    }

    public String getWebSite() {
        return new String(this.webSite);
    }

    public JSONObject request(String control, String action) throws FaceppParseException {
        return this.request(control, action, this.getParams());
    }

    public JSONObject getSessionSync(String sessionId) throws FaceppParseException {
        return this.getSessionSync(sessionId, 60000L);
    }

    public JSONObject getSessionSync(String sessionId, long timeOut) throws FaceppParseException {
        StringBuilder sb = new StringBuilder();
        long t = (new Date()).getTime() + timeOut;

        while(true) {
            JSONObject rst = this.request("info", "get_session", (new PostParameters()).setSessionId(sessionId));

            try {
                if(rst.getString("status").equals("SUCC")) {
                    sb.append(rst.toString());
                    break;
                }

                if(rst.getString("status").equals("INVALID_SESSION")) {
                    sb.append("INVALID_SESSION");
                    break;
                }
            } catch (JSONException var11) {
                sb.append("Unknow error.");
                break;
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var10) {
                sb.append("Thread.sleep error.");
                break;
            }

            if((new Date()).getTime() >= t) {
                sb.append("Time Out");
                break;
            }
        }

        String rst1 = sb.toString();
        if(rst1.equals("INVALID_SESSION")) {
            throw new FaceppParseException("Invaild session, unknow error.");
        } else if(rst1.equals("Unknow error.")) {
            throw new FaceppParseException("Unknow error.");
        } else if(rst1.equals("Thread.sleep error.")) {
            throw new FaceppParseException("Thread.sleep error.");
        } else if(rst1.equals("Time Out")) {
            throw new FaceppParseException("Get session time out.");
        } else {
            try {
                JSONObject result = new JSONObject(rst1);
                result.put("response_code", 200);
                return result;
            } catch (JSONException var9) {
                return null;
            }
        }
    }

    public JSONObject request(String control, String action, PostParameters params) throws FaceppParseException {
        HttpURLConnection urlConn = null;

        JSONObject var10;
        try {
            URL url = new URL(this.webSite + control + "/" + action);
            urlConn = (HttpURLConnection)url.openConnection();
            urlConn.setRequestMethod("POST");
            urlConn.setConnectTimeout(this.httpTimeOut);
            urlConn.setReadTimeout(this.httpTimeOut);
            urlConn.setDoOutput(true);
            urlConn.setRequestProperty("connection", "keep-alive");
            urlConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());
            MultipartEntity e = params.getMultiPart();
            e.addPart("api_key", new StringBody(this.apiKey));
            e.addPart("api_secret", new StringBody(this.apiSecret));
            e.writeTo(urlConn.getOutputStream());
            String resultString = null;
            if(urlConn.getResponseCode() == 200) {
                resultString = readString(urlConn.getInputStream());
            } else {
                resultString = readString(urlConn.getErrorStream());
            }

            JSONObject result = new JSONObject(resultString);
            if(result.has("error")) {
                if(result.getString("error").equals("API not found")) {
                    throw new FaceppParseException("API not found");
                }

                throw new FaceppParseException("API error.", result.getInt("error_code"), result.getString("error"), urlConn.getResponseCode());
            }

            result.put("response_code", urlConn.getResponseCode());
            urlConn.getInputStream().close();
            var10 = result;
        } catch (Exception var13) {
            throw new FaceppParseException("error :" + var13.toString());
        } finally {
            if(urlConn != null) {
                urlConn.disconnect();
            }

        }

        return var10;
    }

    private static String readString(InputStream is) {
        StringBuffer rst = new StringBuffer();
        byte[] buffer = new byte[1048576];
        boolean len = false;

        int var6;
        try {
            while((var6 = is.read(buffer)) > 0) {
                for(int e = 0; e < var6; ++e) {
                    rst.append((char)buffer[e]);
                }
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return rst.toString();
    }

    public HttpRequests(String apiKey, String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.webSite = "https://apicn.faceplusplus.com/v2/";
    }

    public HttpRequests() {
    }

    public HttpRequests(String apiKey, String apiSecret, boolean isCN, boolean isDebug) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.setWebSite(isCN, isDebug);
    }

    public PostParameters getParams() {
        if(this.params == null) {
            this.params = new PostParameters();
        }

        return this.params;
    }

    public void setParams(PostParameters params) {
        this.params = params;
    }

    public JSONObject offlineDetect(byte[] image, String jsonResult) throws FaceppParseException {
        return this.offlineDetect(image, jsonResult, this.params);
    }

    public JSONObject offlineDetect(byte[] image, String jsonResult, PostParameters params) throws FaceppParseException {
        if(params == null) {
            params = new PostParameters();
        }

        params.setImg(image);
        params.setMode("offline");
        params.addAttribute("offline_result", jsonResult);
        return this.request("detection", "detect", params);
    }

    public JSONObject detectionDetect() throws FaceppParseException {
        return this.request("detection", "detect");
    }

    public JSONObject detectionDetect(PostParameters params) throws FaceppParseException {
        return this.request("detection", "detect", params);
    }

    public JSONObject detectionLandmark() throws FaceppParseException {
        return this.request("detection", "landmark");
    }

    public JSONObject detectionLandmark(PostParameters params) throws FaceppParseException {
        return this.request("detection", "landmark", params);
    }

    public JSONObject trainVerify() throws FaceppParseException {
        return this.request("train", "verify");
    }

    public JSONObject trainVerify(PostParameters params) throws FaceppParseException {
        return this.request("train", "verify", params);
    }

    public JSONObject trainSearch() throws FaceppParseException {
        return this.request("train", "search");
    }

    public JSONObject trainSearch(PostParameters params) throws FaceppParseException {
        return this.request("train", "search", params);
    }

    public JSONObject trainIdentify() throws FaceppParseException {
        return this.request("train", "identify");
    }

    public JSONObject trainIdentify(PostParameters params) throws FaceppParseException {
        return this.request("train", "identify", params);
    }

    public JSONObject recognitionCompare() throws FaceppParseException {
        return this.request("recognition", "compare");
    }

    public JSONObject recognitionCompare(PostParameters params) throws FaceppParseException {
        return this.request("recognition", "compare", params);
    }

    public JSONObject recognitionVerify() throws FaceppParseException {
        return this.request("recognition", "verify");
    }

    public JSONObject recognitionVerify(PostParameters params) throws FaceppParseException {
        return this.request("recognition", "verify", params);
    }

    public JSONObject recognitionSearch() throws FaceppParseException {
        return this.request("recognition", "search");
    }

    public JSONObject recognitionSearch(PostParameters params) throws FaceppParseException {
        return this.request("recognition", "search", params);
    }

    public JSONObject recognitionIdentify() throws FaceppParseException {
        return this.request("recognition", "identify");
    }

    public JSONObject recognitionIdentify(PostParameters params) throws FaceppParseException {
        return this.request("recognition", "identify", params);
    }

    public JSONObject groupingGrouping() throws FaceppParseException {
        return this.request("grouping", "grouping");
    }

    public JSONObject groupingGrouping(PostParameters params) throws FaceppParseException {
        return this.request("grouping", "grouping", params);
    }

    public JSONObject personCreate() throws FaceppParseException {
        return this.request("person", "create");
    }

    public JSONObject personCreate(PostParameters params) throws FaceppParseException {
        return this.request("person", "create", params);
    }

    public JSONObject personDelete() throws FaceppParseException {
        return this.request("person", "delete");
    }

    public JSONObject personDelete(PostParameters params) throws FaceppParseException {
        return this.request("person", "delete", params);
    }

    public JSONObject personAddFace() throws FaceppParseException {
        return this.request("person", "add_face");
    }

    public JSONObject personAddFace(PostParameters params) throws FaceppParseException {
        return this.request("person", "add_face", params);
    }

    public JSONObject personRemoveFace() throws FaceppParseException {
        return this.request("person", "remove_face");
    }

    public JSONObject personRemoveFace(PostParameters params) throws FaceppParseException {
        return this.request("person", "remove_face", params);
    }

    public JSONObject personSetInfo() throws FaceppParseException {
        return this.request("person", "set_info");
    }

    public JSONObject personSetInfo(PostParameters params) throws FaceppParseException {
        return this.request("person", "set_info", params);
    }

    public JSONObject personGetInfo() throws FaceppParseException {
        return this.request("person", "get_info");
    }

    public JSONObject personGetInfo(PostParameters params) throws FaceppParseException {
        return this.request("person", "get_info", params);
    }

    public JSONObject facesetCreate() throws FaceppParseException {
        return this.request("faceset", "create");
    }

    public JSONObject facesetCreate(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "create", params);
    }

    public JSONObject facesetDelete() throws FaceppParseException {
        return this.request("faceset", "delete");
    }

    public JSONObject facesetDelete(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "delete", params);
    }

    public JSONObject facesetAddFace() throws FaceppParseException {
        return this.request("faceset", "add_face");
    }

    public JSONObject facesetAddFace(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "add_face", params);
    }

    public JSONObject facesetRemoveFace() throws FaceppParseException {
        return this.request("faceset", "remove_face");
    }

    public JSONObject facesetRemoveFace(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "remove_face", params);
    }

    public JSONObject facesetSetInfo() throws FaceppParseException {
        return this.request("faceset", "set_info");
    }

    public JSONObject facesetSetInfo(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "set_info", params);
    }

    public JSONObject facesetGetInfo() throws FaceppParseException {
        return this.request("faceset", "get_info");
    }

    public JSONObject facesetGetInfo(PostParameters params) throws FaceppParseException {
        return this.request("faceset", "get_info", params);
    }

    public JSONObject groupCreate() throws FaceppParseException {
        return this.request("group", "create");
    }

    public JSONObject groupCreate(PostParameters params) throws FaceppParseException {
        return this.request("group", "create", params);
    }

    public JSONObject groupDelete() throws FaceppParseException {
        return this.request("group", "delete");
    }

    public JSONObject groupDelete(PostParameters params) throws FaceppParseException {
        return this.request("group", "delete", params);
    }

    public JSONObject groupAddPerson() throws FaceppParseException {
        return this.request("group", "add_person");
    }

    public JSONObject groupAddPerson(PostParameters params) throws FaceppParseException {
        return this.request("group", "add_person", params);
    }

    public JSONObject groupRemovePerson() throws FaceppParseException {
        return this.request("group", "remove_person");
    }

    public JSONObject groupRemovePerson(PostParameters params) throws FaceppParseException {
        return this.request("group", "remove_person", params);
    }

    public JSONObject groupSetInfo() throws FaceppParseException {
        return this.request("group", "set_info");
    }

    public JSONObject groupSetInfo(PostParameters params) throws FaceppParseException {
        return this.request("group", "set_info", params);
    }

    public JSONObject groupGetInfo() throws FaceppParseException {
        return this.request("group", "get_info");
    }

    public JSONObject groupGetInfo(PostParameters params) throws FaceppParseException {
        return this.request("group", "get_info", params);
    }

    public JSONObject infoGetImage() throws FaceppParseException {
        return this.request("info", "get_image");
    }

    public JSONObject infoGetImage(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_image", params);
    }

    public JSONObject infoGetFace() throws FaceppParseException {
        return this.request("info", "get_face");
    }

    public JSONObject infoGetFace(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_face", params);
    }

    public JSONObject infoGetPersonList() throws FaceppParseException {
        return this.request("info", "get_person_list");
    }

    public JSONObject infoGetPersonList(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_person_list", params);
    }

    public JSONObject infoGetFacesetList() throws FaceppParseException {
        return this.request("info", "get_faceset_list");
    }

    public JSONObject infoGetFacesetList(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_faceset_list", params);
    }

    public JSONObject infoGetGroupList() throws FaceppParseException {
        return this.request("info", "get_group_list");
    }

    public JSONObject infoGetGroupList(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_group_list", params);
    }

    public JSONObject infoGetSession() throws FaceppParseException {
        return this.request("info", "get_session");
    }

    public JSONObject infoGetSession(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_session", params);
    }

    /** @deprecated */
    public JSONObject infoGetQuota() throws FaceppParseException {
        return this.request("info", "get_quota");
    }

    /** @deprecated */
    public JSONObject infoGetQuota(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_quota", params);
    }

    public JSONObject infoGetApp() throws FaceppParseException {
        return this.request("info", "get_app");
    }

    public JSONObject infoGetApp(PostParameters params) throws FaceppParseException {
        return this.request("info", "get_app", params);
    }
}
