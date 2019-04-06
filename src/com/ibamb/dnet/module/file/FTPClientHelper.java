package com.ibamb.dnet.module.file;

import com.ibamb.dnet.module.log.UdmLog;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;

public class FTPClientHelper {

    private static FTPClient ftpClient = new FTPClient();

    /**
     * 按照默认地址尝试连接
     *
     * @return
     */
    public static int tryDefalutConnect() {
        int replyCode = connect(FtpConstants.DEFUALT_FPT_HOSTS[0], FtpConstants.DEFUALT_FTP_PORT, FtpConstants.DEFUALT_FTP_USER, FtpConstants.DEFUALT_FTP_PWD);
        if (replyCode != 0) {
            replyCode = connect(FtpConstants.DEFUALT_FPT_HOSTS[1], FtpConstants.DEFUALT_FTP_PORT, FtpConstants.DEFUALT_FTP_USER, FtpConstants.DEFUALT_FTP_PWD);
        }
        return replyCode;
    }

    public static int connect(String host, int port, String userName, String password) {
        int replyCode = 0;
        try {
            ftpClient.connect(host, port);
            replyCode = ftpClient.getReplyCode();
            //是否连接成功
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                replyCode = -1;
                throw new ConnectException("The server refused to connect.");
            } else if (!ftpClient.login(userName, password)) {
                replyCode = -2;
                throw new ConnectException("Incorrect username or password.");
            } else {
                replyCode = FtpConstants.CONNECT_SUCCESS;
            }
        } catch (IOException e) {
            replyCode = -3;
        } finally {
            return replyCode;
        }
    }

    /**
     * FTP 下载文件
     *
     * @param remoteFile 远程FTP服务器上的文件名
     * @param localFile  下载到本地存储的文件名
     * @return
     */
    public static int download(String remoteFile, String localFile) {

        int retCode = 0;
        OutputStream output = null;
        try {
            ftpClient.login(FtpConstants.DEFUALT_FTP_USER, FtpConstants.DEFUALT_FTP_PWD);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("UTF-8");
            output = new FileOutputStream(localFile);
            boolean isSuccess = ftpClient.retrieveFile(remoteFile, output);
            if (isSuccess) {
                retCode = -5;
            }

        } catch (NoRouteToHostException e) {
            retCode = -6;
        } catch (ConnectException e) {
            e.printStackTrace();
            retCode = -3;
        } catch (Exception e) {
            retCode = -4;
            UdmLog.error(e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
                UdmLog.error(e);
            }
            return retCode;
        }
    }
}
