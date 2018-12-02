package com.ibamb.dnet.module.beans;


import java.util.Arrays;

public class ParameterItem {
    private String paramId;
    private String paramValue;
    private String displayValue;
    private byte[] byteValue;
    private int convertType;

    public int getConvertType() {
        return convertType;
    }

    public void setConvertType(int convertType) {
        this.convertType = convertType;
    }

    public byte[] getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte[] byteValue) {
        this.byteValue = byteValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }



    public ParameterItem(String paramId,String paramValue) {
        this.paramId = paramId;
        this.paramValue = paramValue;
    }



    public String getParamId() {
        return paramId;
    }

    public void setParamId(String paramId) {
        this.paramId = paramId;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "ParameterItem{" +
                "paramId='" + paramId + '\'' +
                ", paramValue='" + paramValue + '\'' +
                ", displayValue='" + displayValue + '\'' +
                ", byteValue=" + Arrays.toString(byteValue) +
                '}';
    }
}
