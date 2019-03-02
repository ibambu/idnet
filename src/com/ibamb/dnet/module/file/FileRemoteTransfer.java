package com.ibamb.dnet.module.file;

import com.ibamb.dnet.module.beans.RetMessage;
import com.ibamb.dnet.module.constants.Constants;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileRemoteTransfer {
    private DataInputStream inReader;//从远程设备读数据
    private DataOutputStream outWriter;//从本地发送数据

    public FileRemoteTransfer(DataInputStream inReader, DataOutputStream outWriter) {
        this.inReader = inReader;
        this.outWriter = outWriter;
    }

    private RetMessage sendFileToDevice(ZipFile zipFile){
        RetMessage retMessage = new RetMessage(false);
        BufferedReader readerBuffer = null;
        if (zipFile == null) {
            retMessage.setCode(Constants.UPGRADE_MISS_PATCH_CODE);
            return retMessage;
        }
        //读取升级索引文件
        ZipEntry upgradeIndex = zipFile.getEntry(Constants.UPGRADE_INDEX_FILE);
        List<String> entryNames = new ArrayList<>();
        try {
            readerBuffer = new BufferedReader(new InputStreamReader(zipFile.getInputStream(upgradeIndex)));
            String entryName = null;
            while ((entryName = readerBuffer.readLine()) != null) {
                entryNames.add(entryName);
            }

        } catch (IOException e) {
            retMessage.setCode(Constants.UPGRADE_READ_INDEX_FILE_CODE);//读索引文件失败
            return retMessage;
        } finally {
            if (readerBuffer != null) {
                try {
                    readerBuffer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //索引文件丢失
        if (entryNames.isEmpty()) {
            retMessage.setCode(Constants.UPGRADE_READ_INDEX_FILE_CODE);
            return retMessage;
        }
        //检查文件是否缺少
        for (String entryName : entryNames) {
            boolean isExists = false;
            Enumeration enumeration = zipFile.entries();
            while (enumeration.hasMoreElements()) {

                ZipEntry entryName1 = (ZipEntry) enumeration.nextElement();
                if (entryName1.getName().endsWith(entryName)) {
                    isExists = true;
                    break;
                }
            }
            if (!isExists) {
                retMessage.setCode(Constants.UPGRADE_MISS_FILE_CODE);//升级包中的数据文件有丢失
                return retMessage;
            }
        }
        //开始传送升级文件
        StringBuilder upgradeRspBuffer = new StringBuilder();
        byte[] bytes = new byte[1024 * 10];
        int sendLength = 0;
        for (String entryName : entryNames) {
            ZipEntry dataEntry = zipFile.getEntry(entryName);
            if (dataEntry == null) {
                retMessage.setCode(Constants.UPGRADE_MISS_FILE_CODE);
                break;
            }
            InputStream in = null;
            try {
                in = zipFile.getInputStream(dataEntry);
                while ((sendLength = in.read(bytes, 0, bytes.length)) != -1) {
                    outWriter.write(bytes, 0, sendLength);
                    outWriter.flush();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                retMessage.setCode(Constants.UPGRADE_FILE_SEND_FAIL_CODE);//传送文件失败
                return retMessage;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            /**
             * 每传送完一个文件后，等待返回结果。
             */
            String[] failmsgs = Constants.UPGRADE_FAIL_INFO;
            String[] successmsg = Constants.UPGRADE_SUCCESS_INFO;
            int readLength = 0;
            try {
                Thread.sleep(200);//等待200ms,直接读取结果.
                if (entryName.startsWith("cfg_")) {
                    String respnse = new String(bytes, 0, readLength = inReader.read(bytes));
                    if (respnse.toUpperCase().contains("ERROR")) {//有ERROR
                        retMessage.setCode(Constants.UPGRADE_FAIL_CODE);
                    } else if (respnse.toUpperCase().contains("OK")) {//只有OK
                        retMessage.setCode(Constants.UPGRADE_SUCCESS_CODE);
                    } else {
                        retMessage.setCode(Constants.UPGRADE_FAIL_CODE);
                    }
                } else {
                    while ((readLength = inReader.read(bytes)) != -1) {
                        String rsp = new String(bytes, 0, readLength);
                        upgradeRspBuffer.append(rsp);
                        boolean canEnd = upgradeRspBuffer.toString().contains(successmsg[0])||upgradeRspBuffer.toString().contains(successmsg[1]);
                        if (canEnd) {
                            break;
                        }
                        for (String endmsg : failmsgs) {
                            if (upgradeRspBuffer.toString().contains(endmsg)) {
                                canEnd = true;
                                break;
                            }
                        }
                        if (canEnd) {
                            break;
                        }
                    }

                    if (upgradeRspBuffer.toString().contains(successmsg[0])||upgradeRspBuffer.toString().contains(successmsg[1])) {
                        retMessage.setCode(Constants.UPGRADE_SUCCESS_CODE);//升级成功
                    } else {
                        for (String failmsg : failmsgs) {
                            if (upgradeRspBuffer.toString().contains(failmsg)) {
                                break;
                            }
                        }
                        retMessage.setCode(Constants.UPGRADE_FAIL_CODE);//升级失败
                    }
                }
            } catch (Exception e) {
                retMessage.setCode(Constants.UPGRADE_FAIL_CODE);//升级失败
            }
        }
        return retMessage;
    }

    /**
     * 发送ZIP数据包
     *
     * @param zipFile
     * @return
     */
    public RetMessage sendZipFile(ZipFile zipFile) {
        RetMessage retMessage = null;
        try{
            retMessage = sendFileToDevice(zipFile);
            if(retMessage.getCode()==Constants.UPGRADE_SUCCESS_CODE){
                //文件发送成功，升级成功
                byte[] bystes = Constants.UPGRADE_RESTART_CODE.getBytes();
                char enter = 0x0d;
                byte[] enterbytes = charToByte(enter);
                byte[] alldata = new byte[bystes.length + enterbytes.length];
                System.arraycopy(bystes, 0, alldata, 0, bystes.length);
                System.arraycopy(enterbytes, 0, alldata, bystes.length, enterbytes.length);
                outWriter.write(alldata);
                outWriter.flush();
            }
        }catch (Exception e){

        }
        return retMessage;
    }
    private byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

}
