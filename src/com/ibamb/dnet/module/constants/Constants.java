package com.ibamb.dnet.module.constants;


public class Constants {

    public static final int UDM_CONTROL_LENGTH = 1;
    public static final int UDM_ID_LENGTH = 1;
    public static final int UDM_MAIN_FRAME_LENGTH = 2;
    public static final int UDM_IP_LENGTH = 4;
    public static final int UDM_MAC_LENGTH = 8;

    public static final int UDM_TYPE_LENGTH = 2;
    public static final int UDM_SUB_FRAME_LENGTH = 1;

    //UDP读写操作的端口号
    public static final int UDM_UDP_SERVER_PORT = 5000;
    //UDP广播地址
    public static final String UDM_BROADCAST_IP = "255.255.255.255";
    //登录成功返回值
    public static final int UDM_LOGIN_SUCCESS = 4;
    /**
     * 设备升级相关常量
     */
    public static final int UPGRADE_REMOTE_PORT = 5001;
    public static final int UPGRADE_TIME_OUT = 40000;
    public static final String[] UPGRADE_SUCCESS_INFO = {"successful","ok"};
    public static final String[] UPGRADE_FAIL_INFO = {"fail","error"};
    public static final String UPGRADE_VLID_HOST = "AT! command mode! OK";
    public static final String UPGRADE_INDEX_FILE = "index.txt";
    public static final String UPGRADE_RESTART_CODE = "at!r";

    public static final int UPGRADE_MISS_PATCH_CODE = -1;
    public static final int UPGRADE_SUCCESS_CODE = 1;
    public static final int UPGRADE_MISS_FILE_CODE = 2;
    public static final int UPGRADE_READ_INDEX_FILE_CODE = 3;
    public static final int UPGRADE_FILE_SEND_FAIL_CODE = 4;
    public static final int UPGRADE_FAIL_CODE = 0;

    public static final int UDM_PARAM_TYPE_NUMBER = 1;
    public static final int UDM_PARAM_TYPE_IPADDR = 2;
    public static final int UDM_PARAM_TYPE_TIME = 3;
    public static final int UDM_PARAM_TYPE_CHAR = 4;

}
