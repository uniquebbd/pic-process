package com.dengb.faceplushttp;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


public class FaceppParseException extends Exception {
    private String errorMessage = null;
    private Integer errorCode = null;
    private Integer httpResponseCode = null;
    private static final long serialVersionUID = 3L;

    public FaceppParseException(String message) {
        super(message);
    }

    public FaceppParseException(String message, int errorCode, String errorMessage, int httpResponseCode) {
        super(message + " code=" + errorCode + ", message=" + errorMessage + ", responseCode=" + httpResponseCode);
        this.errorCode = Integer.valueOf(errorCode);
        this.errorMessage = errorMessage;
        this.httpResponseCode = Integer.valueOf(httpResponseCode);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Integer getErrorCode() {
        return this.errorCode;
    }

    public Integer getHttpResponseCode() {
        return this.httpResponseCode;
    }

    public boolean isAPIError() {
        return this.errorCode != null && this.errorMessage != null && this.httpResponseCode != null;
    }
}
