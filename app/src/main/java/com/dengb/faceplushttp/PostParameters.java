package com.dengb.faceplushttp;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

public class PostParameters {
    private MultipartEntity multiPart = null;
    private static final int boundaryLength = 32;
    private static final String boundaryAlphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
    private String boundary = this.getBoundary();

    private String getBoundary() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 32; ++i) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".length())));
        }

        return sb.toString();
    }

    public MultipartEntity getMultiPart() {
        return this.multiPart;
    }

    public PostParameters() {
        this.multiPart = new MultipartEntity(HttpMultipartMode.STRICT, this.boundary, Charset.forName("UTF-8"));
    }

    public String boundaryString() {
        return this.boundary;
    }

    public PostParameters setAsync(boolean flag) {
        this.addString("async", "" + flag);
        return this;
    }

    public PostParameters setUrl(String url) {
        this.addString("url", url);
        return this;
    }

    public PostParameters setAttribute(String type) {
        this.addString("attribute", type);
        return this;
    }

    public PostParameters setTag(String tag) {
        this.addString("tag", tag);
        return this;
    }

    public PostParameters setImg(File file) {
        this.multiPart.addPart("img", new FileBody(file));
        return this;
    }

    public PostParameters setImg(byte[] data) {
        this.setImg(data, "NoName");
        return this;
    }

    public PostParameters setImg(byte[] data, String fileName) {
        this.multiPart.addPart("img", new ByteArrayBody(data, fileName));
        return this;
    }

    public PostParameters setFaceId1(String id) {
        this.addString("face_id1", id);
        return this;
    }

    public PostParameters setFaceId2(String id) {
        this.addString("face_id2", id);
        return this;
    }

    public PostParameters setGroupName(String groupName) {
        this.addString("group_name", groupName);
        return this;
    }

    public PostParameters setGroupId(String groupId) {
        this.addString("group_id", groupId);
        return this;
    }

    public PostParameters setKeyFaceId(String id) {
        this.addString("key_face_id", id);
        return this;
    }

    public PostParameters setCount(int count) {
        this.addString("count", (new Integer(count)).toString());
        return this;
    }

    public PostParameters setType(String type) {
        this.addString("type", type);
        return this;
    }

    public PostParameters setFaceId(String faceId) {
        this.addString("face_id", faceId);
        return this;
    }

    public PostParameters setFacesetId(String facesetId) {
        this.addString("faceset_id", facesetId);
        return this;
    }

    public PostParameters setFacesetId(String[] facesetId) {
        this.setFacesetId(this.toStringList(facesetId));
        return this;
    }

    public PostParameters setFacesetId(ArrayList<String> facesetId) {
        this.setFacesetId(this.toStringList(facesetId));
        return this;
    }

    public PostParameters setPersonId(String personId) {
        this.addString("person_id", personId);
        return this;
    }

    public PostParameters setPersonName(String personName) {
        this.addString("person_name", personName);
        return this;
    }

    public PostParameters setName(String name) {
        this.addString("name", name);
        return this;
    }

    public PostParameters setSessionId(String id) {
        this.addString("session_id", id);
        return this;
    }

    public PostParameters setMode(String type) {
        this.addString("mode", type);
        return this;
    }

    public PostParameters setFaceId(String[] faceIds) {
        return this.setFaceId(this.toStringList(faceIds));
    }

    public PostParameters setPersonId(String[] personIds) {
        return this.setPersonId(this.toStringList(personIds));
    }

    public PostParameters setPersonName(String[] personNames) {
        return this.setPersonName(this.toStringList(personNames));
    }

    public PostParameters setGroupId(String[] groupIds) {
        return this.setGroupId(this.toStringList(groupIds));
    }

    public PostParameters setGroupName(String[] groupNames) {
        return this.setGroupName(this.toStringList(groupNames));
    }

    public PostParameters setFaceId(ArrayList<String> faceIds) {
        return this.setFaceId(this.toStringList(faceIds));
    }

    public PostParameters setPersonId(ArrayList<String> personIds) {
        return this.setPersonId(this.toStringList(personIds));
    }

    public PostParameters setPersonName(ArrayList<String> personNames) {
        return this.setPersonName(this.toStringList(personNames));
    }

    public PostParameters setGroupId(ArrayList<String> groupIds) {
        return this.setGroupId(this.toStringList(groupIds));
    }

    public PostParameters setGroupName(ArrayList<String> groupNames) {
        return this.setGroupName(this.toStringList(groupNames));
    }

    public PostParameters setImgId(String imgId) {
        this.addString("img_id", imgId);
        return this;
    }

    public PostParameters setFacesetName(String facesetName) {
        this.addString("faceset_name", facesetName);
        return this;
    }

    public PostParameters setFacesetName(ArrayList<String> facesetNames) {
        return this.setFacesetName(this.toStringList(facesetNames));
    }

    public PostParameters setFacesetName(String[] facesetNames) {
        return this.setFacesetName(this.toStringList(facesetNames));
    }

    public PostParameters addAttribute(String attr, String value) {
        this.addString(attr, value);
        return this;
    }

    private void addString(String id, String str) {
        try {
            this.multiPart.addPart(id, new StringBody(str, Charset.forName("UTF-8")));
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

    }

    private String toStringList(String[] sa) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < sa.length; ++i) {
            if(i != 0) {
                sb.append(',');
            }

            sb.append(sa[i]);
        }

        return sb.toString();
    }

    private String toStringList(ArrayList<String> sa) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < sa.size(); ++i) {
            if(i != 0) {
                sb.append(',');
            }

            sb.append((String)sa.get(i));
        }

        return sb.toString();
    }
}
