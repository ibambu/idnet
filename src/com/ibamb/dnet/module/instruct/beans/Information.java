package com.ibamb.dnet.module.instruct.beans;

import java.util.Arrays;

public class Information {
    private String type;// 2 octets
    private int length;// 1 octet
    private String data;// zero or more octets
    private byte[] byteData;

    public byte[] getByteData() {
        return byteData;
    }

    public void setByteData(byte[] byteData) {
        this.byteData = byteData;
    }

    public Information(String type, String value){
        this.type = type;
        this.data = value;
    }
    public Information(){

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Information{" +
                "type='" + type + '\'' +
                ", length=" + length +
                ", data='" + data + '\'' +
                ", byteData=" + Arrays.toString(byteData) +
                '}';
    }
}
