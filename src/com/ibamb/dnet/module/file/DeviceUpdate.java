package com.ibamb.dnet.module.file;

import com.ibamb.dnet.module.beans.RetMessage;
import com.ibamb.dnet.module.constants.Constants;
import com.ibamb.dnet.module.log.UdmLog;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.zip.ZipFile;

public class DeviceUpdate {

    private String updatePackageZip;
    private String deviceIp;

    private Socket socket;//套接字
    private DataInputStream dataReader;//数据读
    private DataOutputStream dataWriter;//数据写


    public DeviceUpdate(String updatePackageZip, String deviceIp) {
        this.updatePackageZip = updatePackageZip;
        this.deviceIp = deviceIp;
    }

    public RetMessage update() {
        /**
         * 探测主机是否可达,3秒后无响应当作不可达。
         */
        boolean isValidHost;
        RetMessage retMessage = new RetMessage(false);
        try {
            InetAddress inetAddress = InetAddress.getByName(deviceIp);
            boolean isReachable = inetAddress.isReachable(3000);
            if (isReachable) {
                socket = new Socket(deviceIp, Constants.UPGRADE_REMOTE_PORT);
                socket.setSoTimeout(Constants.UPGRADE_TIME_OUT);//设置连接超时时间
                dataReader = new DataInputStream(socket.getInputStream());
                dataWriter = new DataOutputStream(socket.getOutputStream());
                isValidHost = isValidHost();//读取返回信息，验证是否合法IP。
                if (isValidHost) {
                    FileRemoteTransfer transfer = new FileRemoteTransfer(dataReader, dataWriter);
                    ZipFile zipPackage = new ZipFile(new File(updatePackageZip));
                    retMessage = transfer.sendZipFile(zipPackage);
                }
            }

        } catch (Exception e) {
            retMessage.setCode(Constants.UPGRADE_FAIL_CODE);
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (dataReader != null) {
                    dataReader.close();
                }
                if (dataWriter != null) {
                    dataWriter.close();
                }
            } catch (IOException e) {

            }

        }
        return retMessage;
    }

    /**
     * 是否合法主机
     *
     * @return
     */
    private boolean isValidHost() {
        boolean isValid = false;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataReader));
            String rsp = reader.readLine();
            if (rsp.contains(Constants.UPGRADE_VLID_HOST)) {
                isValid = true;
            }
            UdmLog.info("check valid host response:" + rsp);
        } catch (SocketTimeoutException ex) {
        } catch (Exception e) {
        }
        return isValid;
    }
}
