package com.ibamb.dnet.module.beans;

import java.util.List;

public class DeviceBaseInfo {
    private List<ComBaseInfo> comBaseInfoList;

    public List<ComBaseInfo> getComBaseInfoList() {
        return comBaseInfoList;
    }

    public void setComBaseInfoList(List<ComBaseInfo> comBaseInfoList) {
        this.comBaseInfoList = comBaseInfoList;
    }

    public void print() {
        for (ComBaseInfo comBaseInfo : comBaseInfoList) {
            System.out.println(comBaseInfo.toString());
        }
    }

    public static class ComBaseInfo {
        private int comId;
        private boolean isComEabled;
        private boolean isCanDNS;


        public int getComId() {
            return comId;
        }

        public void setComId(int comId) {
            this.comId = comId;
        }

        public boolean isComEabled() {
            return isComEabled;
        }

        public void setComEabled(boolean comEabled) {
            isComEabled = comEabled;
        }

        public boolean isCanDNS() {
            return isCanDNS;
        }

        public void setCanDNS(boolean canDNS) {
            isCanDNS = canDNS;
        }

        @Override
        public String toString() {
            return "ComBaseInfo{" +
                    "comId=" + comId +
                    ", isComEabled=" + isComEabled +
                    ", isCanDNS=" + isCanDNS +
                    '}';
        }
    }
}
