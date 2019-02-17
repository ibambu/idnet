package com.ibamb.dnet.module.beans;

public class DataModel<T> {
    /**
     * 0：失败
     * 1：成功
     */
    private int code;
    private String description;
    private T data;

    public DataModel() {
    }

    public DataModel(int code, String description, T data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
